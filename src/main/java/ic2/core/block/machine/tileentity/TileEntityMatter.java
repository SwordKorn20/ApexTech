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
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.world.World
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.FluidTank
 *  net.minecraftforge.fluids.IFluidTank
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.block.machine.tileentity;

import ic2.api.energy.tile.IExplosionPowerOverride;
import ic2.api.recipe.IMachineRecipeManager;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.RecipeInputOreDict;
import ic2.api.recipe.RecipeOutput;
import ic2.api.recipe.Recipes;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.Platform;
import ic2.core.audio.AudioManager;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.TileEntityLiquidTankElectricMachine;
import ic2.core.block.comp.Energy;
import ic2.core.block.comp.Redstone;
import ic2.core.block.comp.TileEntityComponent;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotConsumableLiquidByList;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotProcessableGeneric;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.container.ContainerMatter;
import ic2.core.block.machine.gui.GuiMatter;
import ic2.core.block.state.IIdProvider;
import ic2.core.init.MainConfig;
import ic2.core.item.type.CraftingItemType;
import ic2.core.network.NetworkManager;
import ic2.core.recipe.BasicMachineRecipeManager;
import ic2.core.ref.FluidName;
import ic2.core.ref.ItemName;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.IUpgradeItem;
import ic2.core.upgrade.UpgradableProperty;
import ic2.core.util.ConfigUtil;
import ic2.core.util.SideGateway;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.mutable.MutableObject;

public class TileEntityMatter
extends TileEntityLiquidTankElectricMachine
implements IHasGui,
IUpgradableBlock,
IExplosionPowerOverride {
    public final int defaultTier;
    public int soundTicker = IC2.random.nextInt(32);
    public int scrap = 0;
    private double lastEnergy;
    private final int StateIdle = 0;
    private final int StateRunning = 1;
    private final int StateRunningScrap = 2;
    private int state = 0;
    private int prevState = 0;
    public boolean redstonePowered = false;
    private AudioSource audioSource;
    private AudioSource audioSourceScrap;
    public final InvSlotUpgrade upgradeSlot;
    public final InvSlotProcessableGeneric amplifierSlot;
    public final InvSlotOutput outputSlot;
    public final InvSlotConsumableLiquid containerslot;
    protected final Redstone redstone;

    public TileEntityMatter() {
        super(Math.round(1000000.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/uuEnergyFactor")), 3, 8);
        this.amplifierSlot = new InvSlotProcessableGeneric(this, "scrap", 1, Recipes.matterAmplifier);
        this.outputSlot = new InvSlotOutput(this, "output", 1);
        this.containerslot = new InvSlotConsumableLiquidByList((TileEntityInventory)this, "container", InvSlot.Access.I, 1, InvSlot.InvSide.TOP, InvSlotConsumableLiquid.OpType.Fill, FluidName.uu_matter.getInstance());
        this.upgradeSlot = new InvSlotUpgrade(this, "upgrade", 4);
        this.defaultTier = 3;
        this.redstone = this.addComponent(new Redstone(this));
        this.redstone.subscribe(new Redstone.IRedstoneChangeHandler(){

            @Override
            public void onRedstoneChange(int newLevel) {
                TileEntityMatter.this.energy.setEnabled(newLevel == 0);
            }
        });
    }

    public static void init() {
        Recipes.matterAmplifier = new BasicMachineRecipeManager();
        TileEntityMatter.addAmplifier(ItemName.crafting.getItemStack(CraftingItemType.scrap), 1, 5000);
        TileEntityMatter.addAmplifier(ItemName.crafting.getItemStack(CraftingItemType.scrap_box), 1, 45000);
    }

    public static void addAmplifier(ItemStack input, int amount, int amplification) {
        TileEntityMatter.addAmplifier(new RecipeInputItemStack(input, amount), amplification);
    }

    public static void addAmplifier(String input, int amount, int amplification) {
        TileEntityMatter.addAmplifier(new RecipeInputOreDict(input, amount), amplification);
    }

    public static void addAmplifier(IRecipeInput input, int amplification) {
        NBTTagCompound metadata = new NBTTagCompound();
        metadata.setInteger("amplification", amplification);
        Recipes.matterAmplifier.addRecipe(input, metadata, false, new ItemStack[0]);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.scrap = nbt.getInteger("scrap");
        this.lastEnergy = nbt.getDouble("lastEnergy");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("scrap", this.scrap);
        nbt.setDouble("lastEnergy", this.lastEnergy);
        return nbt;
    }

    @Override
    protected void onLoaded() {
        super.onLoaded();
        if (!this.worldObj.isRemote) {
            this.setUpgradestat();
        }
    }

    @Override
    protected void onUnloaded() {
        if (IC2.platform.isRendering() && this.audioSource != null) {
            IC2.audioManager.removeSources(this);
            this.audioSource = null;
            this.audioSourceScrap = null;
        }
        super.onUnloaded();
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        this.redstonePowered = false;
        boolean needsInvUpdate = false;
        for (int i = 0; i < this.upgradeSlot.size(); ++i) {
            ItemStack stack = this.upgradeSlot.get(i);
            if (stack == null || !(stack.getItem() instanceof IUpgradeItem) || !((IUpgradeItem)stack.getItem()).onTick(stack, this)) continue;
            needsInvUpdate = true;
        }
        if (this.redstone.hasRedstoneInput() || this.energy.getEnergy() <= 0.0) {
            this.setState(0);
            this.setActive(false);
        } else {
            MutableObject output;
            RecipeOutput amplifier;
            if (this.scrap > 0) {
                double bonus = Math.min((double)this.scrap, this.energy.getEnergy() - this.lastEnergy);
                if (bonus > 0.0) {
                    this.energy.forceAddEnergy(5.0 * bonus);
                    this.scrap = (int)((double)this.scrap - bonus);
                }
                this.setState(2);
            } else {
                this.setState(1);
            }
            this.setActive(true);
            if (this.scrap < 10000 && (amplifier = this.amplifierSlot.process()) != null) {
                this.amplifierSlot.consume();
                this.scrap += amplifier.metadata.getInteger("amplification");
            }
            if (this.energy.getEnergy() >= this.energy.getCapacity()) {
                needsInvUpdate = this.attemptGeneration();
            }
            if (this.containerslot.transferFromTank((IFluidTank)this.fluidTank, output = new MutableObject(), true) && (output.getValue() == null || this.outputSlot.canAdd((ItemStack)output.getValue()))) {
                this.containerslot.transferFromTank((IFluidTank)this.fluidTank, output, false);
                if (output.getValue() != null) {
                    this.outputSlot.add((ItemStack)output.getValue());
                }
            }
            this.lastEnergy = this.energy.getEnergy();
            if (needsInvUpdate) {
                this.markDirty();
            }
        }
    }

    @Override
    protected int getComparatorInputOverride() {
        return this.energy.getComparatorValue();
    }

    public boolean amplificationIsAvailable() {
        if (this.scrap > 0) {
            return true;
        }
        RecipeOutput amplifier = this.amplifierSlot.process();
        return amplifier != null && amplifier.metadata.getInteger("amplification") > 0;
    }

    public boolean attemptGeneration() {
        if (this.fluidTank.getFluidAmount() + 1 > this.fluidTank.getCapacity()) {
            return false;
        }
        this.fill(null, new FluidStack(FluidName.uu_matter.getInstance(), 1), true);
        this.energy.useEnergy(this.energy.getCapacity());
        return true;
    }

    public String getProgressAsString() {
        int p = (int)Math.min(100.0 * this.energy.getFillRatio(), 100.0);
        return "" + p + "%";
    }

    public ContainerBase<TileEntityMatter> getGuiContainer(EntityPlayer player) {
        return new ContainerMatter(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiMatter(new ContainerMatter(player, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }

    private void setState(int aState) {
        this.state = aState;
        if (this.prevState != this.state) {
            IC2.network.get(true).updateTileEntityField(this, "state");
        }
        this.prevState = this.state;
    }

    @Override
    public List<String> getNetworkedFields() {
        ArrayList<String> ret = new ArrayList<String>();
        ret.add("state");
        ret.addAll(super.getNetworkedFields());
        return ret;
    }

    @Override
    public void onNetworkUpdate(String field) {
        if (field.equals("state") && this.prevState != this.state) {
            switch (this.state) {
                case 0: {
                    if (this.audioSource != null) {
                        this.audioSource.stop();
                    }
                    if (this.audioSourceScrap == null) break;
                    this.audioSourceScrap.stop();
                    break;
                }
                case 1: {
                    if (this.audioSource == null) {
                        this.audioSource = IC2.audioManager.createSource(this, PositionSpec.Center, "Generators/MassFabricator/MassFabLoop.ogg", true, false, IC2.audioManager.getDefaultVolume());
                    }
                    if (this.audioSource != null) {
                        this.audioSource.play();
                    }
                    if (this.audioSourceScrap == null) break;
                    this.audioSourceScrap.stop();
                    break;
                }
                case 2: {
                    if (this.audioSource == null) {
                        this.audioSource = IC2.audioManager.createSource(this, PositionSpec.Center, "Generators/MassFabricator/MassFabLoop.ogg", true, false, IC2.audioManager.getDefaultVolume());
                    }
                    if (this.audioSourceScrap == null) {
                        this.audioSourceScrap = IC2.audioManager.createSource(this, PositionSpec.Center, "Generators/MassFabricator/MassFabScrapSolo.ogg", true, false, IC2.audioManager.getDefaultVolume());
                    }
                    if (this.audioSource != null) {
                        this.audioSource.play();
                    }
                    if (this.audioSourceScrap == null) break;
                    this.audioSourceScrap.play();
                }
            }
            this.prevState = this.state;
        }
        super.onNetworkUpdate(field);
    }

    @Override
    public boolean canFill(EnumFacing from, Fluid fluid) {
        return fluid == FluidName.uu_matter.getInstance();
    }

    @Override
    public boolean canDrain(EnumFacing from, Fluid fluid) {
        return true;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (IC2.platform.isSimulating()) {
            this.setUpgradestat();
        }
    }

    public void setUpgradestat() {
        this.upgradeSlot.onChanged();
        this.energy.setSinkTier(TileEntityMatter.applyModifier(this.defaultTier, this.upgradeSlot.extraTier, 1.0));
    }

    private static int applyModifier(int base, int extra, double multiplier) {
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
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.RedstoneSensitive, UpgradableProperty.Transformer, UpgradableProperty.ItemConsuming, UpgradableProperty.ItemProducing, UpgradableProperty.FluidProducing);
    }

    @Override
    public boolean shouldExplode() {
        return true;
    }

    @Override
    public float getExplosionPower(int tier, float defaultPower) {
        return 15.0f;
    }

}

