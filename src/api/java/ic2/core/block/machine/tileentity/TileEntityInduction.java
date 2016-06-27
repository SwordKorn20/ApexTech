/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.machine.tileentity;

import ic2.api.recipe.RecipeOutput;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.Energy;
import ic2.core.block.comp.Redstone;
import ic2.core.block.comp.TileEntityComponent;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotProcessable;
import ic2.core.block.invslot.InvSlotProcessableSmelting;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import ic2.core.gui.dynamic.DynamicContainer;
import ic2.core.gui.dynamic.DynamicGui;
import ic2.core.gui.dynamic.GuiParser;
import ic2.core.gui.dynamic.IGuiValueProvider;
import ic2.core.network.GuiSynced;
import ic2.core.ref.TeBlock;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.IUpgradeItem;
import ic2.core.upgrade.UpgradableProperty;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityInduction
extends TileEntityElectricMachine
implements IHasGui,
IUpgradableBlock,
IGuiValueProvider {
    private static final short maxHeat = 10000;
    public final InvSlotProcessable inputSlotA;
    public final InvSlotProcessable inputSlotB;
    public final InvSlotUpgrade upgradeSlot;
    public final InvSlotOutput outputSlotA;
    public final InvSlotOutput outputSlotB;
    protected final Redstone redstone;
    public int soundTicker = IC2.random.nextInt(64);
    @GuiSynced
    public short heat = 0;
    @GuiSynced
    public short progress = 0;

    public TileEntityInduction() {
        super(10000, 2);
        this.inputSlotA = new InvSlotProcessableSmelting(this, "inputA", 1);
        this.inputSlotB = new InvSlotProcessableSmelting(this, "inputB", 1);
        this.outputSlotA = new InvSlotOutput(this, "outputA", 1);
        this.outputSlotB = new InvSlotOutput(this, "outputB", 1);
        this.upgradeSlot = new InvSlotUpgrade(this, "upgrade", 2);
        this.redstone = this.addComponent(new Redstone(this));
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.heat = nbt.getShort("heat");
        this.progress = nbt.getShort("progress");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setShort("heat", this.heat);
        nbt.setShort("progress", this.progress);
        return nbt;
    }

    @Override
    protected void updateEntityServer() {
        boolean canOperate;
        super.updateEntityServer();
        boolean needsInvUpdate = false;
        boolean newActive = this.getActive();
        if (this.heat == 0) {
            newActive = false;
        }
        if (this.progress >= 4000) {
            this.operate();
            needsInvUpdate = true;
            this.progress = 0;
            newActive = false;
        }
        if (((canOperate = this.canOperate()) || this.redstone.hasRedstoneInput()) && this.energy.useEnergy(1.0)) {
            if (this.heat < 10000) {
                this.heat = (short)(this.heat + 1);
            }
            newActive = true;
        } else {
            this.heat = (short)(this.heat - Math.min(this.heat, 4));
        }
        if (!newActive || this.progress == 0) {
            if (canOperate) {
                if (this.energy.getEnergy() >= 15.0) {
                    newActive = true;
                }
            } else {
                this.progress = 0;
            }
        } else if (!canOperate || this.energy.getEnergy() < 15.0) {
            if (!canOperate) {
                this.progress = 0;
            }
            newActive = false;
        }
        if (newActive && canOperate) {
            this.progress = (short)(this.progress + this.heat / 30);
            this.energy.useEnergy(15.0);
        }
        if (needsInvUpdate) {
            this.markDirty();
        }
        if (newActive != this.getActive()) {
            this.setActive(newActive);
        }
        for (int i = 0; i < this.upgradeSlot.size(); ++i) {
            ItemStack stack = this.upgradeSlot.get(i);
            if (stack == null || !(stack.getItem() instanceof IUpgradeItem) || !((IUpgradeItem)stack.getItem()).onTick(stack, this)) continue;
            super.markDirty();
        }
    }

    @Override
    protected int getComparatorInputOverride() {
        return this.heat * 15 / 10000;
    }

    public String getHeat() {
        return "" + this.heat * 100 / 10000 + "%";
    }

    public int gaugeProgressScaled(int i) {
        return i * this.progress / 4000;
    }

    public void operate() {
        this.operate(this.inputSlotA, this.outputSlotA);
        this.operate(this.inputSlotB, this.outputSlotB);
    }

    public void operate(InvSlotProcessable inputSlot, InvSlotOutput outputSlot) {
        if (!this.canOperate(inputSlot, outputSlot)) {
            return;
        }
        outputSlot.add(inputSlot.process().items);
        inputSlot.consume();
    }

    public boolean canOperate() {
        return this.canOperate(this.inputSlotA, this.outputSlotA) || this.canOperate(this.inputSlotB, this.outputSlotB);
    }

    public boolean canOperate(InvSlotProcessable inputSlot, InvSlotOutput outputSlot) {
        if (inputSlot.isEmpty()) {
            return false;
        }
        RecipeOutput output = inputSlot.process();
        if (output == null) {
            return false;
        }
        return outputSlot.canAdd(output.items);
    }

    public ContainerBase<TileEntityInduction> getGuiContainer(EntityPlayer player) {
        return DynamicContainer.create(this, player, GuiParser.parse(this.teBlock));
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return DynamicGui.create(this, player, GuiParser.parse(this.teBlock));
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
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
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.RedstoneSensitive, UpgradableProperty.ItemConsuming, UpgradableProperty.ItemProducing);
    }

    @Override
    public double getGuiValue(String name) {
        if ("progress".equals(name)) {
            return (double)this.gaugeProgressScaled(1000) / 1000.0;
        }
        throw new IllegalArgumentException();
    }
}

