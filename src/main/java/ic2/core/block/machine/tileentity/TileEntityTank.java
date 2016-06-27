/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumFacing
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.FluidTank
 *  net.minecraftforge.fluids.FluidTankInfo
 *  net.minecraftforge.fluids.IFluidTank
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.machine.tileentity;

import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.TileEntityLiquidTankInventory;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.gui.dynamic.DynamicContainer;
import ic2.core.gui.dynamic.DynamicGui;
import ic2.core.gui.dynamic.GuiParser;
import ic2.core.gui.dynamic.IFluidTankProvider;
import ic2.core.ref.TeBlock;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.IUpgradeItem;
import ic2.core.upgrade.UpgradableProperty;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityTank
extends TileEntityLiquidTankInventory
implements IUpgradableBlock,
IHasGui,
IFluidTankProvider {
    public final InvSlotUpgrade upgradeSlot;

    public TileEntityTank() {
        super(8);
        this.upgradeSlot = new InvSlotUpgrade(this, "upgrade", 4);
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        boolean needsInvUpdate = false;
        for (int i = 0; i < this.upgradeSlot.size(); ++i) {
            ItemStack stack = this.upgradeSlot.get(i);
            if (stack == null || !(stack.getItem() instanceof IUpgradeItem) || !((IUpgradeItem)stack.getItem()).onTick(stack, this)) continue;
            needsInvUpdate = true;
        }
        if (needsInvUpdate) {
            super.markDirty();
        }
    }

    @Override
    public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
        if (this.canFill(from, resource.getFluid())) {
            return this.getFluidTank().fill(resource, doFill);
        }
        return 0;
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
    public FluidTankInfo[] getTankInfo(EnumFacing from) {
        return new FluidTankInfo[]{this.getFluidTank().getInfo()};
    }

    @Override
    public boolean canFill(EnumFacing from, Fluid fluid) {
        return true;
    }

    @Override
    public boolean canDrain(EnumFacing from, Fluid fluid) {
        FluidStack fluidStack = this.getFluidTank().getFluid();
        return fluidStack != null && fluidStack.isFluidEqual(new FluidStack(fluid, 1));
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
        return EnumSet.of(UpgradableProperty.FluidConsuming, UpgradableProperty.FluidProducing);
    }

    public ContainerBase<TileEntityTank> getGuiContainer(EntityPlayer player) {
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
    public IFluidTank getFluidTank(String name) {
        if ("fluid".equals(name)) {
            return this.fluidTank;
        }
        throw new IllegalArgumentException();
    }
}

