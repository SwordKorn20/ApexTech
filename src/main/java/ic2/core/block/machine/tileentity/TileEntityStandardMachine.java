/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.machine.tileentity;

import ic2.api.network.INetworkTileEntityEventListener;
import ic2.api.recipe.RecipeOutput;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.Platform;
import ic2.core.audio.AudioManager;
import ic2.core.audio.AudioSource;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.Energy;
import ic2.core.block.invslot.InvSlotDischarge;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotProcessable;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import ic2.core.gui.dynamic.DynamicContainer;
import ic2.core.gui.dynamic.DynamicGui;
import ic2.core.gui.dynamic.GuiParser;
import ic2.core.gui.dynamic.IGuiValueProvider;
import ic2.core.network.GuiSynced;
import ic2.core.network.NetworkManager;
import ic2.core.ref.TeBlock;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.IUpgradeItem;
import ic2.core.util.SideGateway;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileEntityStandardMachine
extends TileEntityElectricMachine
implements IHasGui,
IGuiValueProvider,
INetworkTileEntityEventListener,
IUpgradableBlock {
    protected short progress = 0;
    public final int defaultEnergyConsume;
    public final int defaultOperationLength;
    public final int defaultTier;
    public final int defaultEnergyStorage;
    public int energyConsume;
    public int operationLength;
    public int operationsPerTick;
    @GuiSynced
    protected float guiProgress;
    public AudioSource audioSource;
    private static final int EventStart = 0;
    private static final int EventInterrupt = 1;
    private static final int EventStop = 2;
    public InvSlotProcessable inputSlot;
    public final InvSlotOutput outputSlot;
    public final InvSlotUpgrade upgradeSlot;

    public TileEntityStandardMachine(int energyPerTick, int length, int outputSlots) {
        this(energyPerTick, length, outputSlots, 1);
    }

    public TileEntityStandardMachine(int energyPerTick, int length, int outputSlots, int aDefaultTier) {
        super(energyPerTick * length, aDefaultTier);
        this.defaultEnergyConsume = this.energyConsume = energyPerTick;
        this.defaultOperationLength = this.operationLength = length;
        this.defaultTier = aDefaultTier;
        this.defaultEnergyStorage = energyPerTick * length;
        this.outputSlot = new InvSlotOutput(this, "output", outputSlots);
        this.upgradeSlot = new InvSlotUpgrade(this, "upgrade", 4);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.progress = nbttagcompound.getShort("progress");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setShort("progress", this.progress);
        return nbt;
    }

    public float getProgress() {
        return this.guiProgress;
    }

    @Override
    protected void onLoaded() {
        super.onLoaded();
        if (IC2.platform.isSimulating()) {
            this.setOverclockRates();
        }
    }

    @Override
    protected void onUnloaded() {
        super.onUnloaded();
        if (IC2.platform.isRendering() && this.audioSource != null) {
            IC2.audioManager.removeSources(this);
            this.audioSource = null;
        }
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (IC2.platform.isSimulating()) {
            this.setOverclockRates();
        }
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        boolean needsInvUpdate = false;
        RecipeOutput output = this.getOutput();
        if (output != null && this.energy.useEnergy(this.energyConsume)) {
            this.setActive(true);
            if (this.progress == 0) {
                IC2.network.get(true).initiateTileEntityEvent(this, 0, true);
            }
            this.progress = (short)(this.progress + 1);
            if (this.progress >= this.operationLength) {
                this.operate(output);
                needsInvUpdate = true;
                this.progress = 0;
                IC2.network.get(true).initiateTileEntityEvent(this, 2, true);
            }
        } else {
            if (this.progress != 0 && this.getActive()) {
                IC2.network.get(true).initiateTileEntityEvent(this, 1, true);
            }
            if (output == null) {
                this.progress = 0;
            }
            this.setActive(false);
        }
        for (int i = 0; i < this.upgradeSlot.size(); ++i) {
            ItemStack stack = this.upgradeSlot.get(i);
            if (stack == null || !(stack.getItem() instanceof IUpgradeItem) || !((IUpgradeItem)stack.getItem()).onTick(stack, this)) continue;
            needsInvUpdate = true;
        }
        this.guiProgress = (float)this.progress / (float)this.operationLength;
        if (needsInvUpdate) {
            super.markDirty();
        }
    }

    @Override
    protected int getComparatorInputOverride() {
        return this.progress * 15 / this.operationLength;
    }

    public void setOverclockRates() {
        this.upgradeSlot.onChanged();
        double previousProgress = (double)this.progress / (double)this.operationLength;
        double stackOpLen = ((double)this.defaultOperationLength + (double)this.upgradeSlot.extraProcessTime) * 64.0 * this.upgradeSlot.processTimeMultiplier;
        this.operationsPerTick = (int)Math.min(Math.ceil(64.0 / stackOpLen), 2.147483647E9);
        this.operationLength = (int)Math.round(stackOpLen * (double)this.operationsPerTick / 64.0);
        this.energyConsume = TileEntityStandardMachine.applyModifier(this.defaultEnergyConsume, this.upgradeSlot.extraEnergyDemand, this.upgradeSlot.energyDemandMultiplier);
        this.energy.setSinkTier(TileEntityStandardMachine.applyModifier(this.defaultTier, this.upgradeSlot.extraTier, 1.0));
        this.energy.setCapacity(TileEntityStandardMachine.applyModifier(this.defaultEnergyStorage, this.upgradeSlot.extraEnergyStorage + this.operationLength * this.energyConsume, this.upgradeSlot.energyStorageMultiplier));
        this.dischargeSlot.setTier(this.energy.getSinkTier());
        if (this.operationLength < 1) {
            this.operationLength = 1;
        }
        this.progress = (short)Math.floor(previousProgress * (double)this.operationLength + 0.1);
    }

    public void operate(RecipeOutput output) {
        for (int i = 0; i < this.operationsPerTick; ++i) {
            List<ItemStack> processResult = output.items;
            for (int j = 0; j < this.upgradeSlot.size(); ++j) {
                ItemStack stack = this.upgradeSlot.get(j);
                if (stack == null || !(stack.getItem() instanceof IUpgradeItem)) continue;
                ((IUpgradeItem)stack.getItem()).onProcessEnd(stack, this, processResult);
            }
            this.operateOnce(output, processResult);
            output = this.getOutput();
            if (output == null) break;
        }
    }

    public void operateOnce(RecipeOutput output, List<ItemStack> processResult) {
        this.inputSlot.consume();
        this.outputSlot.add(processResult);
    }

    public RecipeOutput getOutput() {
        if (this.inputSlot.isEmpty()) {
            return null;
        }
        RecipeOutput output = this.inputSlot.process();
        if (output == null) {
            return null;
        }
        if (this.outputSlot.canAdd(output.items)) {
            return output;
        }
        return null;
    }

    public ContainerBase<? extends TileEntityStandardMachine> getGuiContainer(EntityPlayer player) {
        return DynamicContainer.create(this, player, GuiParser.parse(this.teBlock));
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return DynamicGui.create(this, player, GuiParser.parse(this.teBlock));
    }

    public String getStartSoundFile() {
        return null;
    }

    public String getInterruptSoundFile() {
        return null;
    }

    @Override
    public void onNetworkEvent(int event) {
        if (this.audioSource == null && this.getStartSoundFile() != null) {
            this.audioSource = IC2.audioManager.createSource(this, this.getStartSoundFile());
        }
        switch (event) {
            case 0: {
                if (this.audioSource == null) break;
                this.audioSource.play();
                break;
            }
            case 1: {
                if (this.audioSource == null) break;
                this.audioSource.stop();
                if (this.getInterruptSoundFile() == null) break;
                IC2.audioManager.playOnce(this, this.getInterruptSoundFile());
                break;
            }
            case 2: {
                if (this.audioSource == null) break;
                this.audioSource.stop();
            }
        }
    }

    public static int applyModifier(int base, int extra, double multiplier) {
        double ret = Math.round(((double)base + (double)extra) * multiplier);
        return ret > 2.147483647E9 ? Integer.MAX_VALUE : (int)ret;
    }

    @Override
    public double getEnergy() {
        return this.energy.getEnergy();
    }

    @Override
    public boolean useEnergy(double amount) {
        return this.energy.useEnergy(amount);
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }

    @Override
    public double getGuiValue(String name) {
        if (name.equals("progress")) {
            return this.guiProgress;
        }
        throw new IllegalArgumentException(this.getClass().getSimpleName() + " Cannot get value for " + name);
    }
}

