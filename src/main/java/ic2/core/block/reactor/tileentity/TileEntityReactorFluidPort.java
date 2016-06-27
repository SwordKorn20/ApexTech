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
 *  net.minecraftforge.fluids.FluidTankInfo
 *  net.minecraftforge.fluids.IFluidHandler
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.reactor.tileentity;

import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.FluidReactorLookup;
import ic2.core.block.comp.TileEntityComponent;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.reactor.container.ContainerReactorFluidPort;
import ic2.core.block.reactor.gui.GuiReactorFluidPort;
import ic2.core.block.reactor.tileentity.TileEntityNuclearReactorElectric;
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
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityReactorFluidPort
extends TileEntityInventory
implements IHasGui,
IUpgradableBlock,
IFluidHandler {
    public final InvSlotUpgrade upgradeSlot;
    private final FluidReactorLookup lookup;

    public TileEntityReactorFluidPort() {
        this.upgradeSlot = new InvSlotUpgrade(this, "upgrade", 1);
        this.lookup = this.addComponent(new FluidReactorLookup(this));
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        for (int i = 0; i < this.upgradeSlot.size(); ++i) {
            ItemStack stack = this.upgradeSlot.get(i);
            if (stack == null || !(stack.getItem() instanceof IUpgradeItem) || !((IUpgradeItem)stack.getItem()).onTick(stack, this)) continue;
            super.markDirty();
        }
    }

    public ContainerBase<TileEntityReactorFluidPort> getGuiContainer(EntityPlayer player) {
        return new ContainerReactorFluidPort(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiReactorFluidPort(new ContainerReactorFluidPort(player, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.FluidConsuming, UpgradableProperty.FluidProducing);
    }

    @Override
    public double getEnergy() {
        return 40.0;
    }

    @Override
    public boolean useEnergy(double amount) {
        return true;
    }

    public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        return reactor != null ? reactor.fill(from, resource, doFill) : 0;
    }

    public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        return reactor != null ? reactor.drain(from, resource, doDrain) : null;
    }

    public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        return reactor != null ? reactor.drain(from, maxDrain, doDrain) : null;
    }

    public boolean canFill(EnumFacing from, Fluid fluid) {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        return reactor != null ? reactor.canFill(from, fluid) : false;
    }

    public boolean canDrain(EnumFacing from, Fluid fluid) {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        return reactor != null ? reactor.canDrain(from, fluid) : false;
    }

    public FluidTankInfo[] getTankInfo(EnumFacing from) {
        TileEntityNuclearReactorElectric reactor = this.getReactor();
        return reactor != null ? reactor.getTankInfo(from) : new FluidTankInfo[]{};
    }

    private TileEntityNuclearReactorElectric getReactor() {
        return this.lookup.getReactor();
    }
}

