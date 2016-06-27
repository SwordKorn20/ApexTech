/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
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

import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.api.recipe.RecipeOutput;
import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.TileEntityLiquidTankInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotConsumableLiquidByTank;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.machine.container.ContainerFluidDistributor;
import ic2.core.block.machine.gui.GuiFluidDistributor;
import ic2.core.util.LiquidUtil;
import java.util.EnumSet;
import java.util.Iterator;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
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

public class TileEntityFluidDistributor
extends TileEntityLiquidTankInventory
implements IHasGui,
INetworkClientTileEntityEventListener {
    public final InvSlotConsumableLiquidByTank inputSlot;
    public final InvSlotOutput OutputSlot;

    public TileEntityFluidDistributor() {
        super(1);
        this.inputSlot = new InvSlotConsumableLiquidByTank(this, "inputSlot", InvSlot.Access.I, 1, InvSlot.InvSide.BOTTOM, InvSlotConsumableLiquid.OpType.Fill, (IFluidTank)this.getFluidTank());
        this.OutputSlot = new InvSlotOutput(this, "OutputSlot", 1);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @Override
    protected void updateEntityServer() {
        EnumFacing[] outputinputSlot;
        int amount;
        super.updateEntityServer();
        if (this.getFluidTank().getFluidAmount() > 0 && (outputinputSlot = this.processInputSlot(true)) != null) {
            this.processInputSlot(false);
            List<ItemStack> processResult = outputinputSlot.items;
            this.OutputSlot.add(processResult);
        }
        if (this.getFluidTank().getFluidAmount() <= 0) return;
        if (this.getActive()) {
            outputinputSlot = EnumFacing.VALUES;
            int processResult = outputinputSlot.length;
            int n = 0;
            while (n < processResult) {
                int amount2;
                TileEntity target;
                EnumFacing side;
                EnumFacing dir = outputinputSlot[n];
                if (dir == this.getFacing() && LiquidUtil.isFluidTile(target = this.worldObj.getTileEntity(this.pos.offset(dir)), side = dir.getOpposite()) && (amount2 = LiquidUtil.fillTile(target, side, this.getFluidTank().getFluid(), false)) > 0) {
                    this.getFluidTank().drain(amount2, true);
                }
                ++n;
            }
            return;
        }
        EnumSet<EnumFacing> acceptingNeighbors = EnumSet.noneOf(EnumFacing.class);
        int acceptedVolume = 0;
        for (EnumFacing dir : EnumFacing.VALUES) {
            int amount3;
            TileEntity target;
            EnumFacing side;
            if (dir == this.getFacing() || !LiquidUtil.isFluidTile(target = this.worldObj.getTileEntity(this.pos.offset(dir)), side = dir.getOpposite()) || (amount3 = LiquidUtil.fillTile(target, side, this.getFluidTank().getFluid(), true)) <= 0) continue;
            acceptingNeighbors.add(dir);
            acceptedVolume += amount3;
        }
        block2 : while (!acceptingNeighbors.isEmpty() && (amount = Math.min(acceptedVolume, this.getFluidTank().getFluidAmount())) > 0) {
            EnumFacing side;
            int cAmount;
            FluidStack fs;
            TileEntity target;
            if ((amount /= acceptingNeighbors.size()) > 0) {
                Iterator it = acceptingNeighbors.iterator();
                while (it.hasNext()) {
                    EnumFacing dir2 = (EnumFacing)it.next();
                    target = this.worldObj.getTileEntity(this.pos.offset(dir2));
                    side = dir2.getOpposite();
                    fs = this.getFluidTank().getFluid().copy();
                    if (fs.amount <= 0) continue block2;
                    fs.amount = Math.min(amount, fs.amount);
                    cAmount = LiquidUtil.fillTile(target, side, fs, false);
                    this.getFluidTank().drain(cAmount, true);
                    acceptedVolume -= cAmount;
                    if (cAmount >= fs.amount) continue;
                    it.remove();
                }
                continue;
            }
            for (EnumFacing dir3 : acceptingNeighbors) {
                target = this.worldObj.getTileEntity(this.pos.offset(dir3));
                side = dir3.getOpposite();
                fs = this.getFluidTank().getFluid().copy();
                fs.amount = Math.min(acceptedVolume, fs.amount);
                if (fs.amount <= 0) return;
                cAmount = LiquidUtil.fillTile(target, side, fs, false);
                this.getFluidTank().drain(cAmount, true);
                acceptedVolume -= cAmount;
            }
            return;
        }
    }

    private RecipeOutput processInputSlot(boolean simulate) {
        if (!this.inputSlot.isEmpty()) {
            MutableObject output = new MutableObject();
            if (this.inputSlot.transferFromTank((IFluidTank)this.getFluidTank(), output, simulate) && (output.getValue() == null || this.OutputSlot.canAdd((ItemStack)output.getValue()))) {
                if (output.getValue() == null) {
                    return new RecipeOutput(null, new ItemStack[0]);
                }
                return new RecipeOutput(null, (ItemStack)output.getValue());
            }
        }
        return null;
    }

    @Override
    public void onNetworkEvent(EntityPlayer player, int event) {
        this.setActive(!this.getActive());
    }

    public ContainerBase<TileEntityFluidDistributor> getGuiContainer(EntityPlayer player) {
        return new ContainerFluidDistributor(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiFluidDistributor(new ContainerFluidDistributor(player, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }

    @Override
    public boolean canFill(EnumFacing from, Fluid fluid) {
        if (this.getActive() ? from == this.getFacing() : from != this.getFacing()) {
            return false;
        }
        if (this.getFluidTank().getFluidAmount() == 0) {
            return true;
        }
        if (this.getFluidTank().getFluid().getFluid().equals((Object)fluid)) {
            return true;
        }
        return false;
    }

    @Override
    public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
        if (!this.canFill(from, resource.getFluid())) {
            return 0;
        }
        return this.getFluidTank().fill(resource, doFill);
    }

    @Override
    public boolean canDrain(EnumFacing from, Fluid fluid) {
        return false;
    }

    @Override
    public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
        return null;
    }

    @Override
    public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
        return null;
    }
}

