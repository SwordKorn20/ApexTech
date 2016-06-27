/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumFacing
 *  net.minecraftforge.common.capabilities.Capability
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidContainerRegistry
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.IFluidContainerItem
 *  net.minecraftforge.fluids.IFluidTank
 *  net.minecraftforge.fluids.capability.CapabilityFluidHandler
 *  net.minecraftforge.fluids.capability.IFluidHandler
 *  net.minecraftforge.fluids.capability.IFluidTankProperties
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.block.invslot;

import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumable;
import ic2.core.util.LiquidUtil;
import ic2.core.util.StackUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;
import org.apache.commons.lang3.mutable.MutableObject;

public class InvSlotConsumableLiquid
extends InvSlotConsumable {
    private OpType opType;

    public InvSlotConsumableLiquid(TileEntityInventory base1, String name1, int count) {
        this(base1, name1, InvSlot.Access.I, count, InvSlot.InvSide.TOP, OpType.Drain);
    }

    public InvSlotConsumableLiquid(TileEntityInventory base1, String name1, InvSlot.Access access1, int count, InvSlot.InvSide preferredSide1, OpType opType1) {
        super(base1, name1, access1, count, preferredSide1);
        this.opType = opType1;
    }

    @Override
    public boolean accepts(ItemStack stack) {
        Item item = stack.getItem();
        if (item == null) {
            return false;
        }
        if (this.opType == OpType.Drain || this.opType == OpType.Both) {
            ItemStack singleStack;
            IFluidHandler handler;
            Object containerFluid = null;
            if (FluidContainerRegistry.isFilledContainer((ItemStack)stack)) {
                containerFluid = FluidContainerRegistry.getFluidForFilledItem((ItemStack)stack);
            } else if (item instanceof IFluidContainerItem) {
                containerFluid = ((IFluidContainerItem)item).getFluid(stack);
            } else if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null) && (handler = (IFluidHandler)(singleStack = StackUtil.copyWithSize(stack, 1)).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) != null) {
                containerFluid = handler.drain(Integer.MAX_VALUE, false);
            }
            if (containerFluid != null && containerFluid.amount > 0 && this.acceptsLiquid(containerFluid.getFluid())) {
                return true;
            }
        }
        if (this.opType == OpType.Fill || this.opType == OpType.Both) {
            IFluidHandler handler;
            ItemStack singleStack;
            if (FluidContainerRegistry.isEmptyContainer((ItemStack)stack)) {
                if (this.getPossibleFluids() == null) {
                    return true;
                }
                for (Fluid fluid : this.getPossibleFluids()) {
                    if (FluidContainerRegistry.fillFluidContainer((FluidStack)new FluidStack(fluid, Integer.MAX_VALUE), (ItemStack)stack) == null) continue;
                    return true;
                }
            } else if (item instanceof IFluidContainerItem) {
                IFluidContainerItem containerItem = (IFluidContainerItem)item;
                FluidStack prevFluid = containerItem.getFluid(stack);
                if (prevFluid == null || containerItem.getCapacity(stack) > prevFluid.amount) {
                    if (this.getPossibleFluids() == null) {
                        return true;
                    }
                    ItemStack singleStack2 = StackUtil.copyWithSize(stack, 1);
                    for (Fluid fluid : this.getPossibleFluids()) {
                        if (containerItem.fill(singleStack2, new FluidStack(fluid, Integer.MAX_VALUE), false) <= 0) continue;
                        return true;
                    }
                }
            } else if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null) && (handler = (IFluidHandler)(singleStack = StackUtil.copyWithSize(stack, 1)).getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) != null) {
                FluidStack fs = handler.drain(Integer.MAX_VALUE, false);
                if (fs == null) {
                    for (IFluidTankProperties properties : handler.getTankProperties()) {
                        if (!properties.canFill()) continue;
                        return true;
                    }
                } else {
                    fs = fs.copy();
                    fs.amount = Integer.MAX_VALUE;
                    if (handler.fill(fs, false) > 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public FluidStack drain(Fluid fluid, int maxAmount, MutableObject<ItemStack> output, boolean simulate) {
        output.setValue((Object)null);
        if (fluid != null && !this.acceptsLiquid(fluid)) {
            return null;
        }
        if (this.opType != OpType.Drain && this.opType != OpType.Both) {
            return null;
        }
        ItemStack stack = this.get();
        if (stack == null) {
            return null;
        }
        FluidStack ret = LiquidUtil.drainContainer(stack, fluid, maxAmount, output, LiquidUtil.FluidContainerOutputMode.EmptyFullToOutput, fluid == null || simulate);
        if (ret == null) {
            return null;
        }
        if (fluid == null) {
            if (!this.acceptsLiquid(ret.getFluid())) {
                output.setValue((Object)null);
                return null;
            }
            if (!simulate) {
                ret = LiquidUtil.drainContainer(stack, fluid, maxAmount, output, LiquidUtil.FluidContainerOutputMode.EmptyFullToOutput, false);
            }
        }
        if (stack.stackSize <= 0) {
            this.put(null);
        }
        return ret;
    }

    public int fill(FluidStack fs, MutableObject<ItemStack> output, boolean simulate) {
        output.setValue((Object)null);
        if (fs == null || fs.amount <= 0) {
            return 0;
        }
        if (this.opType != OpType.Fill && this.opType != OpType.Both) {
            return 0;
        }
        ItemStack stack = this.get();
        if (stack == null) {
            return 0;
        }
        int ret = LiquidUtil.fillContainer(stack, fs, output, LiquidUtil.FluidContainerOutputMode.EmptyFullToOutput, simulate);
        if (stack.stackSize <= 0) {
            this.put(null);
        }
        return ret;
    }

    public boolean transferToTank(IFluidTank tank, MutableObject<ItemStack> output, boolean simulate) {
        FluidStack fluid;
        int space = tank.getCapacity();
        Fluid fluidRequired = null;
        FluidStack tankFluid = tank.getFluid();
        if (tankFluid != null) {
            space -= tankFluid.amount;
            fluidRequired = tankFluid.getFluid();
        }
        if ((fluid = this.drain(fluidRequired, space, output, true)) == null) {
            return false;
        }
        int amount = tank.fill(fluid, !simulate);
        if (amount <= 0) {
            return false;
        }
        if (!simulate) {
            this.drain(fluidRequired, amount, output, false);
        }
        return true;
    }

    public boolean transferFromTank(IFluidTank tank, MutableObject<ItemStack> output, boolean simulate) {
        FluidStack tankFluid = tank.drain(tank.getFluidAmount(), false);
        if (tankFluid == null || tankFluid.amount <= 0) {
            return false;
        }
        int amount = this.fill(tankFluid, output, simulate);
        if (amount <= 0) {
            return false;
        }
        if (!simulate) {
            tank.drain(amount, true);
        }
        return true;
    }

    public void setOpType(OpType opType1) {
        this.opType = opType1;
    }

    protected boolean acceptsLiquid(Fluid fluid) {
        return true;
    }

    protected Iterable<Fluid> getPossibleFluids() {
        return null;
    }

    public static enum OpType {
        Drain,
        Fill,
        Both,
        None;
        

        private OpType() {
        }
    }

}

