/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockDynamicLiquid
 *  net.minecraft.block.BlockLiquid
 *  net.minecraft.block.BlockStaticLiquid
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyInteger
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldProvider
 *  net.minecraftforge.common.capabilities.Capability
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidContainerRegistry
 *  net.minecraftforge.fluids.FluidRegistry
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.IFluidBlock
 *  net.minecraftforge.fluids.IFluidContainerItem
 *  net.minecraftforge.fluids.IFluidHandler
 *  net.minecraftforge.fluids.capability.CapabilityFluidHandler
 *  net.minecraftforge.fluids.capability.IFluidHandler
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.util;

import com.google.common.collect.ImmutableMap;
import ic2.core.util.StackUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.apache.commons.lang3.mutable.MutableObject;

public class LiquidUtil {
    public static List<Fluid> getAllFluids() {
        HashSet fluids = new HashSet(FluidRegistry.getRegisteredFluids().values());
        fluids.remove(null);
        ArrayList<Fluid> ret = new ArrayList<Fluid>(fluids);
        Collections.sort(ret, new Comparator<Fluid>(){

            @Override
            public int compare(Fluid a, Fluid b) {
                String nameA = a.getName();
                String nameB = b.getName();
                if (nameA == null) {
                    if (nameB == null) {
                        return 0;
                    }
                    return 1;
                }
                if (nameB == null) {
                    return -1;
                }
                return nameA.toLowerCase(Locale.ENGLISH).compareTo(nameB.toLowerCase(Locale.ENGLISH));
            }
        });
        return ret;
    }

    public static LiquidData getLiquid(World world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        Fluid liquid = null;
        boolean isSource = false;
        if (block instanceof IFluidBlock) {
            IFluidBlock fblock = (IFluidBlock)block;
            liquid = fblock.getFluid();
            isSource = fblock.canDrain(world, pos);
        } else if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
            liquid = FluidRegistry.WATER;
            isSource = (Integer)state.getValue((IProperty)BlockLiquid.LEVEL) == 0;
        } else if (block == Blocks.LAVA || block == Blocks.FLOWING_LAVA) {
            liquid = FluidRegistry.LAVA;
            boolean bl = isSource = (Integer)state.getValue((IProperty)BlockLiquid.LEVEL) == 0;
        }
        if (liquid != null) {
            return new LiquidData(liquid, isSource);
        }
        return null;
    }

    public static FluidStack drainContainer(ItemStack stack, Fluid fluid, int maxAmount, MutableObject<ItemStack> output, FluidContainerOutputMode outputMode, boolean simulate) {
        if (output == null && outputMode != FluidContainerOutputMode.InPlace) {
            throw new IllegalArgumentException("no output specified for output mode " + (Object)((Object)outputMode));
        }
        if (output != null && outputMode == FluidContainerOutputMode.InPlace) {
            throw new IllegalArgumentException("output specified for output mode " + (Object)((Object)outputMode));
        }
        if (output != null) {
            output.setValue((Object)null);
        }
        if (stack == null || stack.stackSize < 1) {
            return null;
        }
        if (FluidContainerRegistry.isFilledContainer((ItemStack)stack)) {
            FluidStack fs = FluidContainerRegistry.getFluidForFilledItem((ItemStack)stack);
            if (fs == null || fluid != null && fluid != fs.getFluid()) {
                return null;
            }
            if (maxAmount <= 0) {
                return new FluidStack(fs.getFluid(), 0);
            }
            if (fs.amount <= 0 || fs.amount > maxAmount) {
                return null;
            }
            if (!stack.getItem().hasContainerItem(stack)) {
                if (!simulate) {
                    --stack.stackSize;
                }
            } else if (outputMode.outputEmptyFull || outputMode == FluidContainerOutputMode.InPlacePreferred && stack.stackSize > 1) {
                ItemStack container = stack.getItem().getContainerItem(stack);
                if (container != null) {
                    output.setValue((Object)container.copy());
                }
                if (!simulate) {
                    --stack.stackSize;
                }
            } else {
                if (stack.stackSize > 1) {
                    return null;
                }
                if (!simulate) {
                    StackUtil.copyStack(stack, stack.getItem().getContainerItem(stack));
                }
            }
            return fs;
        }
        if (stack.getItem() instanceof IFluidContainerItem) {
            IFluidContainerItem container = (IFluidContainerItem)stack.getItem();
            FluidStack fs = container.getFluid(stack);
            if (fs == null) {
                return null;
            }
            if (fluid != null && fs.getFluid() != fluid) {
                return null;
            }
            if (maxAmount <= 0) {
                return new FluidStack(fs.getFluid(), 0);
            }
            ItemStack singleStack = StackUtil.copyWithSize(stack, 1);
            fs = container.drain(singleStack, maxAmount, true);
            if (fs == null || fs.amount <= 0) {
                return null;
            }
            if (singleStack.stackSize <= 0) {
                if (!simulate) {
                    --stack.stackSize;
                }
            } else {
                boolean isEmpty;
                boolean bl = isEmpty = container.getFluid(singleStack) == null;
                if (isEmpty && outputMode.outputEmptyFull || outputMode == FluidContainerOutputMode.AnyToOutput || outputMode == FluidContainerOutputMode.InPlacePreferred && stack.stackSize > 1) {
                    output.setValue((Object)singleStack);
                    if (!simulate) {
                        --stack.stackSize;
                    }
                } else {
                    if (stack.stackSize > 1) {
                        return null;
                    }
                    if (!simulate) {
                        StackUtil.copyStack(singleStack, stack);
                    }
                }
            }
            return fs;
        }
        if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
            ItemStack singleStack = StackUtil.copyWithSize(stack, 1);
            IFluidHandler handler = (IFluidHandler)singleStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
            if (handler == null) {
                return null;
            }
            FluidStack fs = fluid == null ? handler.drain(maxAmount, true) : handler.drain(new FluidStack(fluid, maxAmount), true);
            if (fs == null || fs.amount <= 0) {
                return null;
            }
            if (singleStack.stackSize <= 0) {
                if (!simulate) {
                    --stack.stackSize;
                }
            } else {
                boolean isEmpty;
                FluidStack leftOver = handler.drain(Integer.MAX_VALUE, false);
                boolean bl = isEmpty = leftOver == null || leftOver.amount <= 0;
                if (isEmpty && outputMode.outputEmptyFull || outputMode == FluidContainerOutputMode.AnyToOutput || outputMode == FluidContainerOutputMode.InPlacePreferred && stack.stackSize > 1) {
                    output.setValue((Object)singleStack);
                    if (!simulate) {
                        --stack.stackSize;
                    }
                } else {
                    if (stack.stackSize > 1) {
                        return null;
                    }
                    if (!simulate) {
                        StackUtil.copyStack(singleStack, stack);
                    }
                }
            }
            return fs;
        }
        return null;
    }

    public static int fillContainer(ItemStack stack, FluidStack fs, MutableObject<ItemStack> output, FluidContainerOutputMode outputMode, boolean simulate) {
        int ret;
        if (output == null && outputMode != FluidContainerOutputMode.InPlace) {
            throw new IllegalArgumentException("no output specified for output mode " + (Object)((Object)outputMode));
        }
        if (output != null && outputMode == FluidContainerOutputMode.InPlace) {
            throw new IllegalArgumentException("output specified for output mode " + (Object)((Object)outputMode));
        }
        if (output != null) {
            output.setValue((Object)null);
        }
        if (stack == null || stack.stackSize < 1) {
            return 0;
        }
        if (fs == null || fs.amount <= 0) {
            return 0;
        }
        if (FluidContainerRegistry.isEmptyContainer((ItemStack)stack)) {
            ItemStack filled = FluidContainerRegistry.fillFluidContainer((FluidStack)fs, (ItemStack)stack);
            if (filled == null) {
                return 0;
            }
            if (outputMode.outputEmptyFull || outputMode == FluidContainerOutputMode.InPlacePreferred && stack.stackSize > 1) {
                output.setValue((Object)filled);
                if (!simulate) {
                    --stack.stackSize;
                }
            } else {
                if (stack.stackSize > 1) {
                    return 0;
                }
                if (!simulate) {
                    StackUtil.copyStack(filled, stack);
                }
            }
            ret = FluidContainerRegistry.getFluidForFilledItem((ItemStack)filled).amount;
        } else if (stack.getItem() instanceof IFluidContainerItem) {
            boolean isFull;
            ItemStack singleStack;
            IFluidContainerItem container = (IFluidContainerItem)stack.getItem();
            int amount = container.fill(singleStack = StackUtil.copyWithSize(stack, 1), fs.copy(), true);
            if (amount <= 0) {
                return 0;
            }
            assert (singleStack.stackSize == 1);
            boolean bl = isFull = container.getFluid((ItemStack)singleStack).amount == container.getCapacity(singleStack);
            if (isFull && outputMode.outputEmptyFull || outputMode == FluidContainerOutputMode.AnyToOutput || outputMode == FluidContainerOutputMode.InPlacePreferred && stack.stackSize > 1) {
                output.setValue((Object)singleStack);
                if (!simulate) {
                    --stack.stackSize;
                }
            } else {
                if (stack.stackSize > 1) {
                    return 0;
                }
                if (!simulate) {
                    StackUtil.copyStack(singleStack, stack);
                }
            }
            ret = amount;
        } else if (stack.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)) {
            boolean isFull;
            ItemStack singleStack = StackUtil.copyWithSize(stack, 1);
            IFluidHandler handler = (IFluidHandler)singleStack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
            if (handler == null) {
                return 0;
            }
            int amount = handler.fill(fs.copy(), true);
            if (amount <= 0) {
                return 0;
            }
            assert (singleStack.stackSize == 1);
            FluidStack fillTestFs = fs.copy();
            fillTestFs.amount = Integer.MAX_VALUE;
            boolean bl = isFull = handler.fill(fillTestFs, false) <= 0;
            if (isFull && outputMode.outputEmptyFull || outputMode == FluidContainerOutputMode.AnyToOutput || outputMode == FluidContainerOutputMode.InPlacePreferred && stack.stackSize > 1) {
                output.setValue((Object)singleStack);
                if (!simulate) {
                    --stack.stackSize;
                }
            } else {
                if (stack.stackSize > 1) {
                    return 0;
                }
                if (!simulate) {
                    StackUtil.copyStack(singleStack, stack);
                }
            }
            ret = amount;
        } else {
            ret = 0;
        }
        return ret;
    }

    public static boolean isFluidTile(TileEntity te, EnumFacing side) {
        return te instanceof net.minecraftforge.fluids.IFluidHandler || te != null && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
    }

    public static FluidStack drainTile(TileEntity te, EnumFacing side, int maxAmount, boolean simulate) {
        return LiquidUtil.drainTile(te, side, null, maxAmount, simulate);
    }

    public static FluidStack drainTile(TileEntity te, EnumFacing side, Fluid fluid, int maxAmount, boolean simulate) {
        if (te instanceof net.minecraftforge.fluids.IFluidHandler) {
            if (fluid == null) {
                return ((net.minecraftforge.fluids.IFluidHandler)te).drain(side, maxAmount, !simulate);
            }
            return ((net.minecraftforge.fluids.IFluidHandler)te).drain(side, new FluidStack(fluid, maxAmount), !simulate);
        }
        if (te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side)) {
            IFluidHandler handler = (IFluidHandler)te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
            if (handler == null) {
                return null;
            }
            if (fluid == null) {
                return handler.drain(maxAmount, !simulate);
            }
            return handler.drain(new FluidStack(fluid, maxAmount), !simulate);
        }
        return null;
    }

    public static int fillTile(TileEntity te, EnumFacing side, FluidStack fs, boolean simulate) {
        if (te instanceof net.minecraftforge.fluids.IFluidHandler) {
            return ((net.minecraftforge.fluids.IFluidHandler)te).fill(side, fs, !simulate);
        }
        if (te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side)) {
            IFluidHandler handler = (IFluidHandler)te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
            if (handler == null) {
                return 0;
            }
            return handler.fill(fs, !simulate);
        }
        return 0;
    }

    public static List<AdjacentFluidHandler> getAdjacentHandlers(TileEntity source) {
        ArrayList<AdjacentFluidHandler> ret = new ArrayList<AdjacentFluidHandler>();
        for (EnumFacing dir : EnumFacing.VALUES) {
            TileEntity te = source.getWorld().getTileEntity(source.getPos().offset(dir));
            if (!LiquidUtil.isFluidTile(te, dir.getOpposite())) continue;
            ret.add(new AdjacentFluidHandler(te, dir));
        }
        return ret;
    }

    public static AdjacentFluidHandler getAdjacentHandler(TileEntity source, EnumFacing dir) {
        TileEntity te = source.getWorld().getTileEntity(source.getPos().offset(dir));
        if (!LiquidUtil.isFluidTile(te, dir.getOpposite())) {
            return null;
        }
        return new AdjacentFluidHandler(te, dir);
    }

    public static int distribute(TileEntity source, FluidStack stack, boolean simulate) {
        int transferred = 0;
        for (AdjacentFluidHandler handler : LiquidUtil.getAdjacentHandlers(source)) {
            int amount = LiquidUtil.fillTile(handler.handler, handler.dir.getOpposite(), stack, simulate);
            transferred += amount;
            stack.amount -= amount;
            if (stack.amount > 0) continue;
            break;
        }
        stack.amount += transferred;
        return transferred;
    }

    public static int distributeAll(TileEntity source, int amount) {
        if (!(source instanceof TileEntity)) {
            throw new IllegalArgumentException("source has to be a tile entity");
        }
        TileEntity srcTe = source;
        int transferred = 0;
        for (EnumFacing dir : EnumFacing.VALUES) {
            FluidStack stack;
            TileEntity te = srcTe.getWorld().getTileEntity(srcTe.getPos().offset(dir));
            if (!LiquidUtil.isFluidTile(te, dir.getOpposite()) || (stack = LiquidUtil.transfer(source, dir, te, amount)) == null) continue;
            transferred += stack.amount;
            if ((amount -= stack.amount) <= 0) break;
        }
        return transferred;
    }

    public static FluidStack transfer(TileEntity source, EnumFacing dir, TileEntity target, int amount) {
        int cAmount;
        FluidStack ret;
        do {
            if ((ret = LiquidUtil.drainTile(source, dir, amount, true)) == null || ret.amount <= 0) {
                return null;
            }
            if (ret.amount > amount) {
                throw new IllegalStateException("The fluid handler " + (Object)source + " drained more than the requested amount.");
            }
            cAmount = LiquidUtil.fillTile(target, LiquidUtil.getOppositeDir(dir), ret, true);
            if (cAmount <= amount) continue;
            throw new IllegalStateException("The fluid handler " + (Object)target + " filled more than the requested amount.");
        } while ((amount = cAmount) != ret.amount && amount > 0);
        if (amount <= 0) {
            return null;
        }
        ret = LiquidUtil.drainTile(source, dir, amount, false);
        if (ret.amount != amount) {
            throw new IllegalStateException("The fluid handler " + (Object)source + " drained inconsistently. Expected " + amount + ", got " + ret.amount + ".");
        }
        amount = LiquidUtil.fillTile(target, LiquidUtil.getOppositeDir(dir), ret, false);
        if (amount != ret.amount) {
            throw new IllegalStateException("The fluid handler " + (Object)target + " filled inconsistently. Expected " + ret.amount + ", got " + amount + ".");
        }
        return ret;
    }

    private static EnumFacing getOppositeDir(EnumFacing dir) {
        if (dir == null) {
            return null;
        }
        return dir.getOpposite();
    }

    public static boolean check(FluidStack fs) {
        return fs.getFluid() != null;
    }

    public static FluidStack drainBlock(World world, BlockPos pos, boolean simulate) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof IFluidBlock) {
            IFluidBlock liquid = (IFluidBlock)block;
            if (liquid.canDrain(world, pos)) {
                return liquid.drain(world, pos, !simulate);
            }
        } else if (block instanceof BlockLiquid && (Integer)state.getValue((IProperty)BlockLiquid.LEVEL) == 0) {
            FluidStack fluid = null;
            if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
                fluid = new FluidStack(FluidRegistry.WATER, 1000);
            } else if (block == Blocks.LAVA || block == Blocks.FLOWING_LAVA) {
                fluid = new FluidStack(FluidRegistry.LAVA, 1000);
            }
            if (fluid != null && !simulate) {
                world.setBlockToAir(pos);
            }
            return fluid;
        }
        return null;
    }

    public static boolean drainBlockToContainer(World world, BlockPos pos, ItemStack stack, MutableObject<ItemStack> output, FluidContainerOutputMode outputMode, boolean simulate) {
        FluidStack fs = LiquidUtil.drainBlock(world, pos, true);
        if (fs == null || fs.amount <= 0) {
            return false;
        }
        int amount = LiquidUtil.fillContainer(stack, fs, output, outputMode, true);
        if (amount != fs.amount) {
            return false;
        }
        if (!simulate) {
            fs = LiquidUtil.drainBlock(world, pos, false);
            if (fs == null || fs.amount <= 0) {
                return false;
            }
            LiquidUtil.fillContainer(stack, fs, output, outputMode, false);
        }
        return true;
    }

    public static boolean drainBlockToContainer(World world, BlockPos pos, ItemStack stack, EntityPlayer player) {
        MutableObject output = new MutableObject();
        if (!LiquidUtil.drainBlockToContainer(world, pos, stack, output, FluidContainerOutputMode.InPlacePreferred, true)) {
            return false;
        }
        if (!LiquidUtil.storeOutputContainer(output, player)) {
            return false;
        }
        LiquidUtil.drainBlockToContainer(world, pos, stack, output, FluidContainerOutputMode.InPlacePreferred, false);
        return true;
    }

    public static boolean fillBlock(FluidStack fs, World world, BlockPos pos, boolean simulate) {
        Block fluidBlock;
        if (fs == null || fs.amount < 1000) {
            return false;
        }
        Fluid fluid = fs.getFluid();
        if (fluid == null || !fluid.canBePlacedInWorld() || fluid.getBlock() == null) {
            return false;
        }
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (!block.isAir(state, (IBlockAccess)world, pos) && state.getMaterial().isSolid()) {
            return false;
        }
        if (block == fluid.getBlock() && LiquidUtil.isFullFluidBlock(world, pos, block, state)) {
            return false;
        }
        if (simulate) {
            return true;
        }
        if (world.provider.doesWaterVaporize() && (fluidBlock = fluid.getBlock()) != null && fluidBlock.getDefaultState().getMaterial() == Material.WATER) {
            world.playSound(null, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5f, 2.6f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8f);
            for (int i = 0; i < 8; ++i) {
                world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, (double)pos.getX() + Math.random(), (double)pos.getY() + Math.random(), (double)pos.getZ() + Math.random(), 0.0, 0.0, 0.0, new int[0]);
            }
        } else {
            if (!(world.isRemote || state.getMaterial().isSolid() || state.getMaterial().isLiquid())) {
                world.destroyBlock(pos, true);
            }
            block = fluid == FluidRegistry.WATER ? Blocks.FLOWING_WATER : (fluid == FluidRegistry.LAVA ? Blocks.FLOWING_LAVA : fluid.getBlock());
            if (!world.setBlockState(pos, block.getDefaultState())) {
                return false;
            }
        }
        fs.amount -= 1000;
        return true;
    }

    private static boolean isFullFluidBlock(World world, BlockPos pos, Block block, IBlockState state) {
        if (block instanceof IFluidBlock) {
            IFluidBlock fBlock = (IFluidBlock)block;
            FluidStack drained = fBlock.drain(world, pos, false);
            return drained != null && drained.amount >= 1000;
        }
        if (state.getProperties().containsKey((Object)BlockLiquid.LEVEL)) {
            return (Integer)state.getValue((IProperty)BlockLiquid.LEVEL) == 0;
        }
        return false;
    }

    public static boolean fillBlockFromContainer(World world, BlockPos pos, ItemStack stack, MutableObject<ItemStack> output, FluidContainerOutputMode outputMode, boolean simulate) {
        FluidStack fs = LiquidUtil.drainContainer(stack, null, 1000, output, outputMode, true);
        if (fs == null || fs.amount < 1000) {
            return false;
        }
        if (!LiquidUtil.fillBlock(fs, world, pos, simulate)) {
            return false;
        }
        if (!simulate) {
            LiquidUtil.drainContainer(stack, null, 1000, output, outputMode, false);
        }
        return true;
    }

    public static boolean fillBlockFromContainer(World world, BlockPos pos, ItemStack stack, EntityPlayer player) {
        MutableObject output = new MutableObject();
        if (!LiquidUtil.fillBlockFromContainer(world, pos, stack, output, FluidContainerOutputMode.InPlacePreferred, true)) {
            return false;
        }
        if (!LiquidUtil.storeOutputContainer(output, player)) {
            return false;
        }
        LiquidUtil.fillBlockFromContainer(world, pos, stack, output, FluidContainerOutputMode.InPlacePreferred, false);
        return true;
    }

    public static boolean storeOutputContainer(MutableObject<ItemStack> output, EntityPlayer player) {
        if (output.getValue() == null) {
            return true;
        }
        return StackUtil.storeInventoryItem((ItemStack)output.getValue(), player, false);
    }

    public static String toStringSafe(FluidStack fluidStack) {
        if (fluidStack.getFluid() == null) {
            return "" + fluidStack.amount + "(mb)x(null)@(unknown)";
        }
        return fluidStack.toString();
    }

    public static class AdjacentFluidHandler {
        public final TileEntity handler;
        public final EnumFacing dir;

        private AdjacentFluidHandler(TileEntity handler, EnumFacing dir) {
            this.handler = handler;
            this.dir = dir;
        }
    }

    public static enum FluidContainerOutputMode {
        EmptyFullToOutput(true),
        AnyToOutput(true),
        InPlacePreferred(false),
        InPlace(false);
        
        private final boolean outputEmptyFull;

        private FluidContainerOutputMode(boolean outputEmptyFull) {
            this.outputEmptyFull = outputEmptyFull;
        }
    }

    public static class LiquidData {
        public final Fluid liquid;
        public final boolean isSource;

        LiquidData(Fluid liquid1, boolean isSource1) {
            this.liquid = liquid1;
            this.isSource = isSource1;
        }
    }

}

