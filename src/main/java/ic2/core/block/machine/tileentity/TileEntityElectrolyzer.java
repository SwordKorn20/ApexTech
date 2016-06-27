/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidRegistry
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.FluidTank
 *  net.minecraftforge.fluids.FluidTankInfo
 *  net.minecraftforge.fluids.IFluidHandler
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.machine.tileentity;

import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.Energy;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.container.ContainerElectrolyzer;
import ic2.core.block.machine.gui.GuiElectrolyzer;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import ic2.core.block.machine.tileentity.TileEntityTank;
import ic2.core.ref.FluidName;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.IUpgradeItem;
import ic2.core.upgrade.UpgradableProperty;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityElectrolyzer
extends TileEntityElectricMachine
implements IFluidHandler,
IUpgradableBlock,
IHasGui {
    protected int progress = 0;
    protected int progressCost = 32;
    protected int progressNeeded = 200;
    protected FluidTank input = new FluidTank(8000);
    public final InvSlotUpgrade upgradeSlot;

    public TileEntityElectrolyzer() {
        super(32000, 2);
        this.upgradeSlot = new InvSlotUpgrade(this, "upgradeSlot", 4);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.progress = nbt.getInteger("progress");
        this.input.readFromNBT(nbt.getCompoundTag("input"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("progress", this.progress);
        NBTTagCompound inputTag = new NBTTagCompound();
        this.input.writeToNBT(inputTag);
        nbt.setTag("input", (NBTBase)inputTag);
        return nbt;
    }

    @Override
    public void updateEntityServer() {
        super.updateEntityServer();
        boolean needsInvUpdate = false;
        if (this.canOperate()) {
            this.setActive(true);
            this.energy.useEnergy(this.progressCost);
            ++this.progress;
            if (this.progress >= this.progressNeeded) {
                this.operate();
                this.progress = 0;
                needsInvUpdate = true;
            }
        } else {
            this.setActive(false);
            this.progress = 0;
        }
        for (int i = 0; i < this.upgradeSlot.size(); ++i) {
            ItemStack stack = this.upgradeSlot.get(i);
            if (stack == null || !(stack.getItem() instanceof IUpgradeItem) || !((IUpgradeItem)stack.getItem()).onTick(stack, this)) continue;
            needsInvUpdate = true;
        }
        if (needsInvUpdate) {
            super.markDirty();
        }
    }

    private boolean canOperate() {
        if (this.energy.getEnergy() < (double)this.progressCost) {
            return false;
        }
        if (this.input.getFluid() == null || this.input.getFluidAmount() < 40) {
            return false;
        }
        if (this.input.getFluid().getFluid() == FluidRegistry.WATER && this.areTanksAvailable()) {
            return this.canFillTank(EnumFacing.DOWN, new FluidStack(FluidName.hydrogen.getInstance(), 26)) && this.canFillTank(EnumFacing.UP, new FluidStack(FluidName.oxygen.getInstance(), 13));
        }
        return false;
    }

    private void operate() {
        this.input.drain(40, true);
        this.fillTank(EnumFacing.DOWN, new FluidStack(FluidName.hydrogen.getInstance(), 26));
        this.fillTank(EnumFacing.UP, new FluidStack(FluidName.oxygen.getInstance(), 13));
    }

    private boolean canFillTank(EnumFacing facing, FluidStack fluid) {
        TileEntity te = this.worldObj.getTileEntity(this.pos.offset(facing));
        if (te == null) {
            return false;
        }
        if (te instanceof TileEntityTank) {
            return ((TileEntityTank)te).fill(facing, fluid, false) == fluid.amount;
        }
        return false;
    }

    private void fillTank(EnumFacing facing, FluidStack fluid) {
        TileEntity te = this.worldObj.getTileEntity(this.pos.offset(facing));
        if (te != null && te instanceof TileEntityTank) {
            ((TileEntityTank)te).fill(facing, fluid, true);
        }
    }

    public boolean areTanksAvailable() {
        Set<EnumFacing> possibleFacings = Collections.unmodifiableSet(EnumSet.of(EnumFacing.DOWN, EnumFacing.UP));
        for (EnumFacing facing : possibleFacings) {
            TileEntity te = this.worldObj.getTileEntity(this.pos.offset(facing));
            if (te != null && te instanceof TileEntityTank) continue;
            return false;
        }
        return true;
    }

    public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
        if (this.canFill(from, resource.getFluid())) {
            return this.input.fill(resource, doFill);
        }
        return 0;
    }

    public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
        return null;
    }

    public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
        return null;
    }

    public FluidTankInfo[] getTankInfo(EnumFacing from) {
        return new FluidTankInfo[]{this.input.getInfo()};
    }

    public boolean canFill(EnumFacing from, Fluid fluid) {
        return fluid == FluidRegistry.WATER || fluid == FluidName.heavy_water.getInstance();
    }

    public boolean canDrain(EnumFacing from, Fluid fluid) {
        return false;
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.ItemProducing);
    }

    @Override
    public double getEnergy() {
        return this.energy.getEnergy();
    }

    @Override
    public boolean useEnergy(double amount) {
        return this.energy.useEnergy(amount);
    }

    public ContainerBase<TileEntityElectrolyzer> getGuiContainer(EntityPlayer player) {
        return new ContainerElectrolyzer(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiElectrolyzer(new ContainerElectrolyzer(player, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }

    public FluidTank getInput() {
        return this.input;
    }
}

