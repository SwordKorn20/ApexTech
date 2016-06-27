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
 *  net.minecraft.util.math.BlockPos
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

import ic2.api.energy.tile.IHeatSource;
import ic2.api.recipe.IMachineRecipeManager;
import ic2.api.recipe.RecipeOutput;
import ic2.api.recipe.Recipes;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IC2Achievements;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.TileEntityLiquidTankInventory;
import ic2.core.block.comp.Redstone;
import ic2.core.block.comp.TileEntityComponent;
import ic2.core.block.invslot.InvSlotConsumableLiquidByList;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotProcessableGeneric;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.state.IIdProvider;
import ic2.core.gui.dynamic.DynamicContainer;
import ic2.core.gui.dynamic.DynamicGui;
import ic2.core.gui.dynamic.GuiParser;
import ic2.core.gui.dynamic.IFluidTankProvider;
import ic2.core.gui.dynamic.IGuiValueProvider;
import ic2.core.item.type.IngotResourceType;
import ic2.core.network.GuiSynced;
import ic2.core.recipe.BasicMachineRecipeManager;
import ic2.core.ref.FluidName;
import ic2.core.ref.ItemName;
import ic2.core.ref.TeBlock;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.IUpgradeItem;
import ic2.core.upgrade.UpgradableProperty;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.mutable.MutableObject;

public class TileEntityBlastFurnace
extends TileEntityLiquidTankInventory
implements IUpgradableBlock,
IHasGui,
IFluidTankProvider,
IGuiValueProvider {
    public int heat = 0;
    public static int maxHeat = 50000;
    @GuiSynced
    public float guiHeat;
    protected final Redstone redstone;
    protected int progress = 0;
    protected int progressNeeded = 300;
    @GuiSynced
    protected float guiProgress;
    public final InvSlotProcessableGeneric inputSlot;
    public final InvSlotOutput outputSlot;
    public final InvSlotConsumableLiquidByList tankInputSlot;
    public final InvSlotOutput tankOutputSlot;
    public final InvSlotUpgrade upgradeSlot;

    public TileEntityBlastFurnace() {
        super(8);
        this.inputSlot = new InvSlotProcessableGeneric(this, "input", 1, Recipes.blastfurnace);
        this.outputSlot = new InvSlotOutput(this, "output", 2){

            @Override
            public void onPickupFromSlot(EntityPlayer player, ItemStack stack) {
                if (player != null && ItemName.ingot.getItemStack(IngotResourceType.steel).isItemEqual(stack)) {
                    IC2.achievements.issueAchievement(player, "acquireRefinedIron");
                }
            }
        };
        this.tankInputSlot = new InvSlotConsumableLiquidByList((TileEntityInventory)this, "cellInput", 1, FluidName.air.getInstance());
        this.tankOutputSlot = new InvSlotOutput(this, "cellOutput", 1);
        this.upgradeSlot = new InvSlotUpgrade(this, "upgrade", 2);
        this.redstone = this.addComponent(new Redstone(this));
    }

    public static void init() {
        Recipes.blastfurnace = new BasicMachineRecipeManager();
    }

    @Override
    public void updateEntityServer() {
        super.updateEntityServer();
        boolean needsInvUpdate = false;
        this.heatup();
        RecipeOutput output = this.getOutput();
        if (output != null && this.isHot()) {
            this.setActive(true);
            if (output.metadata.getInteger("fluid") <= this.getFluidTank().getFluidAmount()) {
                ++this.progress;
                this.getFluidTank().drain(output.metadata.getInteger("fluid"), true);
            }
            this.progressNeeded = output.metadata.getInteger("duration");
            if (this.progress >= output.metadata.getInteger("duration")) {
                this.operateOnce(output.items);
                needsInvUpdate = true;
                this.progress = 0;
            }
        } else {
            if (output == null) {
                this.progress = 0;
            }
            this.setActive(false);
        }
        if (this.getFluidTank().getFluidAmount() < this.getFluidTank().getCapacity()) {
            this.gainFluid();
        }
        for (int i = 0; i < this.upgradeSlot.size(); ++i) {
            ItemStack stack = this.upgradeSlot.get(i);
            if (stack == null || !(stack.getItem() instanceof IUpgradeItem) || !((IUpgradeItem)stack.getItem()).onTick(stack, this)) continue;
            needsInvUpdate = true;
        }
        this.guiProgress = (float)this.progress / (float)this.progressNeeded;
        this.guiHeat = (float)this.heat / (float)maxHeat;
        if (needsInvUpdate) {
            super.markDirty();
        }
    }

    public void operateOnce(List<ItemStack> processResult) {
        this.inputSlot.consume();
        this.outputSlot.add(processResult);
    }

    public RecipeOutput getOutput() {
        if (this.inputSlot.isEmpty()) {
            return null;
        }
        RecipeOutput output = this.inputSlot.process();
        if (output == null || output.metadata == null) {
            return null;
        }
        if (this.outputSlot.canAdd(output.items)) {
            return output;
        }
        return null;
    }

    public boolean gainFluid() {
        boolean ret = false;
        MutableObject output = new MutableObject();
        if (this.tankInputSlot.transferToTank((IFluidTank)this.getFluidTank(), output, true) && (output.getValue() == null || this.tankOutputSlot.canAdd((ItemStack)output.getValue()))) {
            ret = this.tankInputSlot.transferToTank((IFluidTank)this.getFluidTank(), output, false);
            if (output.getValue() != null) {
                this.tankOutputSlot.add((ItemStack)output.getValue());
            }
        }
        return ret;
    }

    @Override
    public boolean canFill(EnumFacing from, Fluid fluid) {
        return fluid == FluidName.air.getInstance();
    }

    @Override
    public boolean canDrain(EnumFacing from, Fluid fluid) {
        return false;
    }

    @Override
    public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
        if (resource == null || !resource.isFluidEqual(this.getFluidTank().getFluid())) {
            return null;
        }
        if (!this.canDrain(from, resource.getFluid())) {
            return null;
        }
        return this.getFluidTank().drain(resource.amount, doDrain);
    }

    @Override
    public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
        return this.getFluidTank().drain(maxDrain, doDrain);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.heat = nbt.getInteger("heat");
        this.progress = nbt.getInteger("progress");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("heat", this.heat);
        nbt.setInteger("progress", this.progress);
        return nbt;
    }

    private void heatup() {
        boolean coolingPerTick = true;
        int heatRequested = 0;
        int gainhU = 0;
        if (!(this.inputSlot.isEmpty() && this.progress < 1 || this.heat > maxHeat)) {
            heatRequested = maxHeat - this.heat + 100;
        } else if (this.redstone.hasRedstoneInput() && this.heat <= maxHeat) {
            heatRequested = maxHeat - this.heat + 100;
        }
        if (heatRequested > 0) {
            EnumFacing dir = this.getFacing();
            TileEntity te = this.worldObj.getTileEntity(this.pos.offset(dir));
            if (te instanceof IHeatSource) {
                gainhU = ((IHeatSource)te).requestHeat(dir.getOpposite(), heatRequested);
                this.heat += gainhU;
            }
            if (gainhU == 0) {
                this.heat -= Math.min(this.heat, 1);
            }
        } else {
            this.heat -= Math.min(this.heat, 1);
        }
    }

    public boolean isHot() {
        return this.heat >= maxHeat;
    }

    public ContainerBase<TileEntityBlastFurnace> getGuiContainer(EntityPlayer player) {
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
    public double getGuiValue(String name) {
        if (name.equals("progress")) {
            return this.guiProgress;
        }
        if (name.equals("heat")) {
            return this.guiHeat;
        }
        throw new IllegalArgumentException();
    }

    @Override
    public IFluidTank getFluidTank(String name) {
        if ("fluid".equals(name)) {
            return this.fluidTank;
        }
        throw new IllegalArgumentException();
    }

    @Override
    public double getEnergy() {
        return 0.0;
    }

    @Override
    public boolean useEnergy(double amount) {
        return false;
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.RedstoneSensitive, UpgradableProperty.ItemConsuming, UpgradableProperty.ItemProducing, UpgradableProperty.FluidConsuming);
    }

}

