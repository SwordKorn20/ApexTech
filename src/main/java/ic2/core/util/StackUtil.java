/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockChest
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.entity.player.PlayerCapabilities
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.ISidedInventory
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemBlockSpecial
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.tileentity.TileEntityChest
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.RayTraceResult$Type
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.ILockableContainer
 *  net.minecraft.world.World
 *  net.minecraftforge.common.capabilities.Capability
 *  net.minecraftforge.fml.common.FMLCommonHandler
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.items.CapabilityItemHandler
 *  net.minecraftforge.items.IItemHandler
 */
package ic2.core.util;

import com.google.common.base.Predicate;
import com.mojang.authlib.GameProfile;
import ic2.core.IC2;
import ic2.core.Ic2Player;
import ic2.core.block.personal.IPersonalBlock;
import ic2.core.util.Log;
import ic2.core.util.LogCategory;
import ic2.core.util.Util;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBlockSpecial;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.ILockableContainer;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public final class StackUtil {
    private static final int[] emptySlotArray = new int[0];
    static final Set<String> ignoredNbtKeys = new HashSet<String>(Arrays.asList("damage", "charge", "energy", "advDmg"));

    public static boolean isInventoryTile(TileEntity te, EnumFacing side) {
        return te instanceof IInventory || te != null && te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
    }

    public static AdjacentInv getAdjacentInventory(TileEntity source, EnumFacing dir) {
        TileEntity target = source.getWorld().getTileEntity(source.getPos().offset(dir));
        if (!StackUtil.isInventoryTile(target, dir)) {
            return null;
        }
        if (target instanceof IPersonalBlock) {
            if (!(source instanceof IPersonalBlock)) {
                return null;
            }
            if (!((IPersonalBlock)target).permitsAccess(((IPersonalBlock)source).getOwner())) {
                return null;
            }
        }
        if (target instanceof TileEntityChest && Blocks.CHEST.getLockableContainer(target.getWorld(), target.getPos()) == null) {
            return null;
        }
        return new AdjacentInv(target, dir);
    }

    public static List<AdjacentInv> getAdjacentInventories(TileEntity source) {
        ArrayList<AdjacentInv> inventories = new ArrayList<AdjacentInv>();
        for (EnumFacing dir : EnumFacing.VALUES) {
            AdjacentInv inventory = StackUtil.getAdjacentInventory(source, dir);
            if (inventory == null) continue;
            inventories.add(inventory);
        }
        Collections.sort(inventories, new Comparator<AdjacentInv>(){

            @Override
            public int compare(AdjacentInv a, AdjacentInv b) {
                if (a.te instanceof IPersonalBlock || !(b.te instanceof IPersonalBlock)) {
                    return -1;
                }
                if (b.te instanceof IPersonalBlock || !(a.te instanceof IPersonalBlock)) {
                    return 1;
                }
                return StackUtil.getInventorySize(b.te, b.dir.getOpposite()) - StackUtil.getInventorySize(a.te, a.dir.getOpposite());
            }
        });
        return inventories;
    }

    public static int getInventorySize(TileEntity te, EnumFacing side) {
        if (te instanceof IInventory) {
            return ((IInventory)te).getSizeInventory();
        }
        if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)) {
            IItemHandler handler = (IItemHandler)te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
            if (handler == null) {
                return 0;
            }
            return handler.getSlots();
        }
        return 0;
    }

    public static int distribute(TileEntity source, ItemStack stack, boolean simulate) {
        int transferred = 0;
        for (AdjacentInv inventory : StackUtil.getAdjacentInventories(source)) {
            int amount = StackUtil.putInInventory(source, inventory, stack, simulate);
            transferred += amount;
            stack.stackSize -= amount;
            if (stack.stackSize != 0) continue;
            break;
        }
        stack.stackSize += transferred;
        return transferred;
    }

    public static ItemStack fetch(TileEntity source, ItemStack stack, boolean simulate) {
        ItemStack ret = null;
        int oldStackSize = stack.stackSize;
        for (AdjacentInv inventory : StackUtil.getAdjacentInventories(source)) {
            ItemStack transferred = StackUtil.getFromInventory(source, inventory, stack, true, simulate);
            if (transferred == null) continue;
            if (ret == null) {
                ret = transferred;
            } else {
                ret.stackSize += transferred.stackSize;
                stack.stackSize -= transferred.stackSize;
            }
            if (stack.stackSize > 0) continue;
            break;
        }
        stack.stackSize = oldStackSize;
        return ret;
    }

    public static int transfer(TileEntity src, TileEntity dst, EnumFacing dir, int amount) {
        if (amount <= 0) {
            return 0;
        }
        EnumFacing reverseDir = dir.getOpposite();
        int[] srcSlots = StackUtil.getInventorySlots(src, dir, false, true);
        if (srcSlots.length == 0) {
            return 0;
        }
        int[] dstSlots = StackUtil.getInventorySlots(dst, reverseDir, true, false);
        if (dstSlots.length == 0) {
            return 0;
        }
        if (src instanceof IInventory) {
            if (dst instanceof IInventory) {
                return StackUtil.transfer((IInventory)src, srcSlots, (IInventory)dst, dstSlots, dir, reverseDir, amount);
            }
            if (dst.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir.getOpposite())) {
                IItemHandler dstHandler = (IItemHandler)dst.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir.getOpposite());
                if (dstHandler == null) {
                    return 0;
                }
                return StackUtil.transfer((IInventory)src, srcSlots, dstHandler, dstSlots, reverseDir, amount);
            }
            return 0;
        }
        if (src.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir)) {
            IItemHandler srcHandler = (IItemHandler)src.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir);
            if (srcHandler == null) {
                return 0;
            }
            if (dst instanceof IInventory) {
                return StackUtil.transfer(srcHandler, srcSlots, (IInventory)dst, dstSlots, reverseDir, amount);
            }
            if (dst.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir.getOpposite())) {
                IItemHandler dstHandler = (IItemHandler)dst.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, dir.getOpposite());
                if (dstHandler == null) {
                    return 0;
                }
                return StackUtil.transfer(srcHandler, srcSlots, dstHandler, dstSlots, amount);
            }
            return 0;
        }
        return 0;
    }

    private static int transfer(IInventory src, int[] srcSlots, IInventory dst, int[] dstSlots, EnumFacing dir, EnumFacing reverseDir, int amount) {
        ISidedInventory dstSided = dst instanceof ISidedInventory ? (ISidedInventory)dst : null;
        int total = amount;
        for (int srcSlot : srcSlots) {
            ItemStack srcStack = src.getStackInSlot(srcSlot);
            if (srcStack == null) continue;
            int origSrcAmount = srcStack.stackSize;
            StackUtil.insert(srcStack, dst, dstSided, reverseDir, dstSlots);
            if (srcStack.stackSize >= origSrcAmount) continue;
            amount -= origSrcAmount - srcStack.stackSize;
            if (srcStack.stackSize <= 0) {
                src.setInventorySlotContents(srcSlot, null);
            }
            if (amount <= 0) break;
        }
        amount = total - amount;
        assert (amount >= 0);
        if (amount > 0) {
            src.markDirty();
            dst.markDirty();
        }
        return amount;
    }

    private static int transfer(IItemHandler src, int[] srcSlots, IInventory dst, int[] dstSlots, EnumFacing reverseDir, int amount) {
        ISidedInventory dstSided = dst instanceof ISidedInventory ? (ISidedInventory)dst : null;
        int total = amount;
        for (int srcSlot : srcSlots) {
            ItemStack srcStack = src.extractItem(srcSlot, amount, true);
            if (srcStack == null) continue;
            int origSrcAmount = srcStack.stackSize;
            StackUtil.insert(srcStack, dst, dstSided, reverseDir, dstSlots);
            if (srcStack.stackSize >= origSrcAmount) continue;
            src.extractItem(srcSlot, origSrcAmount - srcStack.stackSize, false);
            if ((amount -= origSrcAmount - srcStack.stackSize) <= 0) break;
        }
        amount = total - amount;
        assert (amount >= 0);
        if (amount > 0) {
            dst.markDirty();
        }
        return amount;
    }

    private static void insert(ItemStack stack, IInventory dst, ISidedInventory dstSided, EnumFacing side, int[] dstSlots) {
        for (int pass = 0; pass < 2; ++pass) {
            for (int i = 0; i < dstSlots.length; ++i) {
                int amount;
                int dstSlot = dstSlots[i];
                if (dstSlot < 0) continue;
                ItemStack dstStack = dst.getStackInSlot(dstSlot);
                if (pass == 0 && (dstStack == null || !StackUtil.checkItemEqualityStrict(stack, dstStack)) || pass == 1 && dstStack != null || !dst.isItemValidForSlot(dstSlot, stack) || dstSided != null && !dstSided.canInsertItem(dstSlot, stack, side)) continue;
                if (dstStack == null) {
                    amount = Math.min(stack.stackSize, dst.getInventoryStackLimit());
                    dst.setInventorySlotContents(dstSlot, StackUtil.copyWithSize(stack, amount));
                } else {
                    amount = Math.min(stack.stackSize, Math.min(dstStack.getMaxStackSize(), dst.getInventoryStackLimit()) - dstStack.stackSize);
                    if (amount <= 0) {
                        dstSlots[i] = -1;
                        continue;
                    }
                    dstStack.stackSize += amount;
                }
                assert (amount > 0);
                stack.stackSize -= amount;
                if (stack.stackSize > 0) continue;
                return;
            }
        }
    }

    private static int transfer(IItemHandler src, int[] srcSlots, IItemHandler dst, int[] dstSlots, int amount) {
        int total = amount;
        for (int srcSlot : srcSlots) {
            ItemStack srcStack = src.extractItem(srcSlot, amount, true);
            if (srcStack == null) continue;
            int origSrcAmount = srcStack.stackSize;
            StackUtil.insert(srcStack, dst, dstSlots);
            if (srcStack.stackSize >= origSrcAmount) continue;
            src.extractItem(srcSlot, origSrcAmount - srcStack.stackSize, false);
            if ((amount -= origSrcAmount - srcStack.stackSize) <= 0) break;
        }
        amount = total - amount;
        assert (amount >= 0);
        return amount;
    }

    private static int transfer(IInventory src, int[] srcSlots, IItemHandler dst, int[] dstSlots, EnumFacing dir, int amount) {
        int total = amount;
        for (int srcSlot : srcSlots) {
            ItemStack srcStack = src.getStackInSlot(srcSlot);
            if (srcStack == null) continue;
            int origSize = srcStack.stackSize;
            if (srcStack.stackSize > amount) {
                srcStack.stackSize = amount;
            }
            int transferSize = srcStack.stackSize;
            StackUtil.insert(srcStack, dst, dstSlots);
            amount -= transferSize - srcStack.stackSize;
            srcStack.stackSize = origSize - (transferSize - srcStack.stackSize);
            if (srcStack.stackSize > 0) continue;
            src.setInventorySlotContents(srcSlot, null);
        }
        amount = total - amount;
        assert (amount >= 0);
        if (amount > 0) {
            src.markDirty();
        }
        return amount;
    }

    private static void insert(ItemStack stack, IItemHandler dst, int[] dstSlots) {
        for (int pass = 0; pass < 2; ++pass) {
            for (int i = 0; i < dstSlots.length; ++i) {
                int dstSlot = dstSlots[i];
                if (dstSlot < 0) continue;
                ItemStack dstStack = dst.getStackInSlot(dstSlot);
                if (pass == 0 && (dstStack == null || dstStack.stackSize <= 0 || !StackUtil.checkItemEqualityStrict(stack, dstStack)) || pass == 1 && dstStack != null) continue;
                ItemStack remaining = dst.insertItem(dstSlot, stack, false);
                if (remaining == null || remaining.stackSize <= 0) {
                    stack.stackSize = 0;
                    return;
                }
                if (remaining.stackSize >= stack.stackSize) continue;
                stack.stackSize = remaining.stackSize;
            }
        }
    }

    public static void distributeDrops(TileEntity source, List<ItemStack> stacks) {
        Iterator<ItemStack> it = stacks.iterator();
        while (it.hasNext()) {
            ItemStack stack = it.next();
            int amount = StackUtil.distribute(source, stack, false);
            if (amount == stack.stackSize) {
                it.remove();
                continue;
            }
            stack.stackSize -= amount;
        }
        for (ItemStack stack : stacks) {
            StackUtil.dropAsEntity(source.getWorld(), source.getPos(), stack);
        }
        stacks.clear();
    }

    private static ItemStack getFromInventory(TileEntity source, AdjacentInv inventory, ItemStack stack, boolean ignoreMaxStackSize, boolean simulate) {
        if (inventory instanceof PersonalAdjacentInv) {
            if (source instanceof IPersonalBlock && ((PersonalAdjacentInv)inventory).owner.permitsAccess(((IPersonalBlock)source).getOwner())) {
                return StackUtil.getFromInventory(inventory.te, inventory.dir.getOpposite(), stack, stack.stackSize, ignoreMaxStackSize, simulate, true);
            }
            return null;
        }
        return StackUtil.getFromInventory(inventory.te, inventory.dir.getOpposite(), stack, stack.stackSize, ignoreMaxStackSize, simulate, false);
    }

    public static ItemStack getFromInventory(TileEntity te, EnumFacing side, ItemStack stackDestination, int max, boolean ignoreMaxStackSize, boolean simulate) {
        return StackUtil.getFromInventory(te, side, stackDestination, max, ignoreMaxStackSize, simulate, false);
    }

    private static ItemStack getFromInventory(TileEntity te, EnumFacing side, ItemStack stackDestination, int max, boolean ignoreMaxStackSize, boolean simulate, boolean personal) {
        int[] slots;
        if (stackDestination != null && !ignoreMaxStackSize) {
            max = Math.min(max, stackDestination.getMaxStackSize() - stackDestination.stackSize);
        }
        if ((slots = StackUtil.getInventorySlots(te, side, false, true, personal)).length == 0) {
            return null;
        }
        ItemStack ret = null;
        if (te instanceof IInventory) {
            IInventory inv = (IInventory)te;
            for (int slot : slots) {
                if (max <= 0) break;
                ItemStack stack = inv.getStackInSlot(slot);
                assert (stack != null);
                if (stackDestination != null && !StackUtil.checkItemEqualityStrict(stack, stackDestination)) continue;
                if (ret == null) {
                    ret = StackUtil.copyWithSize(stack, 0);
                    if (stackDestination == null) {
                        if (!ignoreMaxStackSize) {
                            max = Math.min(max, ret.getMaxStackSize());
                        }
                        stackDestination = ret;
                    }
                }
                int transfer = Math.min(max, stack.stackSize);
                if (!simulate) {
                    stack.stackSize -= transfer;
                    if (stack.stackSize == 0) {
                        inv.setInventorySlotContents(slot, null);
                    }
                }
                max -= transfer;
                ret.stackSize += transfer;
            }
            if (!simulate && ret != null) {
                inv.markDirty();
            }
        } else if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)) {
            IItemHandler handler = (IItemHandler)te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
            if (handler == null) {
                return null;
            }
            for (int slot : slots) {
                ItemStack stack;
                if (max <= 0) break;
                if (stackDestination != null && ((stack = handler.getStackInSlot(slot)) == null || !StackUtil.checkItemEqualityStrict(stack, stackDestination)) || (stack = handler.extractItem(slot, max, simulate)) == null || stack.stackSize <= 0) continue;
                if (ret == null) {
                    ret = StackUtil.copyWithSize(stack, 0);
                    if (stackDestination == null) {
                        if (!ignoreMaxStackSize) {
                            max = Math.min(max, ret.getMaxStackSize());
                        }
                        stackDestination = ret;
                    }
                } else assert (StackUtil.checkItemEqualityStrict(stack, ret));
                max -= stack.stackSize;
                ret.stackSize += stack.stackSize;
            }
        }
        return ret;
    }

    private static int putInInventory(TileEntity source, AdjacentInv inventory, ItemStack stackSource, boolean simulate) {
        if (inventory instanceof PersonalAdjacentInv) {
            if (source instanceof IPersonalBlock && ((PersonalAdjacentInv)inventory).owner.permitsAccess(((IPersonalBlock)source).getOwner())) {
                return StackUtil.putInInventory(inventory.te, inventory.dir.getOpposite(), stackSource, simulate, true);
            }
            return 0;
        }
        return StackUtil.putInInventory(inventory.te, inventory.dir.getOpposite(), stackSource, simulate, false);
    }

    public static int putInInventory(TileEntity te, EnumFacing side, ItemStack stackSource, boolean simulate) {
        return StackUtil.putInInventory(te, side, stackSource, simulate, false);
    }

    private static int putInInventory(TileEntity te, EnumFacing side, ItemStack stackSource, boolean simulate, boolean personal) {
        if (stackSource == null) {
            return 0;
        }
        int[] slots = StackUtil.getInventorySlots(te, side, true, false, personal);
        if (slots.length == 0) {
            return 0;
        }
        if (te instanceof IInventory) {
            ItemStack stack;
            int transfer;
            IInventory inv = (IInventory)te;
            int toTransfer = stackSource.stackSize;
            for (int slot2 : slots) {
                if (toTransfer <= 0) break;
                if (!inv.isItemValidForSlot(slot2, stackSource) && !personal || inv instanceof ISidedInventory && !((ISidedInventory)inv).canInsertItem(slot2, stackSource, side) && !personal || (stack = inv.getStackInSlot(slot2)) == null || !StackUtil.checkItemEqualityStrict(stack, stackSource)) continue;
                transfer = Math.min(toTransfer, Math.min(inv.getInventoryStackLimit(), stack.getMaxStackSize()) - stack.stackSize);
                if (!simulate) {
                    stack.stackSize += transfer;
                }
                toTransfer -= transfer;
            }
            for (int slot2 : slots) {
                if (toTransfer <= 0) break;
                if (!inv.isItemValidForSlot(slot2, stackSource) && !personal || inv instanceof ISidedInventory && !((ISidedInventory)inv).canInsertItem(slot2, stackSource, side) && !personal || (stack = inv.getStackInSlot(slot2)) != null) continue;
                transfer = Math.min(toTransfer, Math.min(inv.getInventoryStackLimit(), stackSource.getMaxStackSize()));
                if (!simulate) {
                    ItemStack dest = StackUtil.copyWithSize(stackSource, transfer);
                    inv.setInventorySlotContents(slot2, dest);
                }
                toTransfer -= transfer;
            }
            if (!simulate && toTransfer != stackSource.stackSize) {
                inv.markDirty();
            }
            return stackSource.stackSize - toTransfer;
        }
        if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)) {
            ItemStack remaining;
            ItemStack stack;
            IItemHandler handler = (IItemHandler)te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
            if (handler == null) {
                return 0;
            }
            ItemStack src = stackSource.copy();
            for (int slot3 : slots) {
                if (src.stackSize <= 0) break;
                stack = handler.getStackInSlot(slot3);
                if (stack == null || stack.stackSize <= 0) continue;
                remaining = handler.insertItem(slot3, src, simulate);
                if (remaining == null) {
                    src.stackSize = 0;
                    continue;
                }
                if (remaining.stackSize >= src.stackSize) continue;
                src.stackSize = remaining.stackSize;
            }
            for (int slot3 : slots) {
                if (src.stackSize <= 0) break;
                stack = handler.getStackInSlot(slot3);
                if (stack != null && stack.stackSize != 0) continue;
                remaining = handler.insertItem(slot3, src, simulate);
                if (remaining == null) {
                    src.stackSize = 0;
                    continue;
                }
                if (remaining.stackSize >= src.stackSize) continue;
                src.stackSize = remaining.stackSize;
            }
            return stackSource.stackSize - src.stackSize;
        }
        return 0;
    }

    public static int[] getInventorySlots(TileEntity te, EnumFacing side, boolean checkInsert, boolean checkExtract) {
        return StackUtil.getInventorySlots(te, side, checkInsert, checkExtract, false);
    }

    private static int[] getInventorySlots(TileEntity te, EnumFacing side, boolean checkInsert, boolean checkExtract, boolean personal) {
        if (te instanceof IInventory) {
            ISidedInventory sidedInv;
            int[] ret;
            IInventory inv = (IInventory)te;
            if (inv.getInventoryStackLimit() <= 0) {
                return emptySlotArray;
            }
            if (inv instanceof ISidedInventory) {
                sidedInv = (ISidedInventory)inv;
                ret = sidedInv.getSlotsForFace(side);
                if (ret.length == 0) {
                    return emptySlotArray;
                }
                ret = Arrays.copyOf(ret, ret.length);
            } else {
                int size = inv.getSizeInventory();
                if (size <= 0) {
                    return emptySlotArray;
                }
                sidedInv = null;
                ret = new int[size];
                int i = 0;
                while (i < ret.length) {
                    ret[i] = i++;
                }
            }
            if (checkInsert || checkExtract) {
                int writeIdx = 0;
                for (int readIdx = 0; readIdx < ret.length; ++readIdx) {
                    int slot = ret[readIdx];
                    ItemStack stack = inv.getStackInSlot(slot);
                    if (checkExtract && (stack == null || stack.stackSize <= 0 || sidedInv != null && !personal && !sidedInv.canExtractItem(slot, stack, side)) || checkInsert && stack != null && (stack.stackSize >= stack.getMaxStackSize() || stack.stackSize >= inv.getInventoryStackLimit() || sidedInv != null && !personal && !sidedInv.canInsertItem(slot, stack, side))) continue;
                    ret[writeIdx] = slot;
                    ++writeIdx;
                }
                if (writeIdx != ret.length) {
                    ret = Arrays.copyOf(ret, writeIdx);
                }
            }
            return ret;
        }
        if (te.hasCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)) {
            IItemHandler handler = (IItemHandler)te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
            if (handler == null) {
                return emptySlotArray;
            }
            int size = handler.getSlots();
            if (size <= 0) {
                return emptySlotArray;
            }
            int[] ret = new int[size];
            int i = 0;
            while (i < ret.length) {
                ret[i] = i++;
            }
            if (checkInsert || checkExtract) {
                int writeIdx = 0;
                for (int readIdx = 0; readIdx < ret.length; ++readIdx) {
                    int slot = ret[readIdx];
                    ItemStack stack = handler.getStackInSlot(slot);
                    if (checkExtract && (stack == null || stack.stackSize <= 0 || handler.extractItem(slot, Integer.MAX_VALUE, true) == null) || checkInsert && !StackUtil.checkInsert(handler, slot, stack)) continue;
                    ret[writeIdx] = slot;
                    ++writeIdx;
                }
                if (writeIdx != ret.length) {
                    ret = Arrays.copyOf(ret, writeIdx);
                }
            }
            return ret;
        }
        return emptySlotArray;
    }

    private static boolean checkInsert(IItemHandler handler, int slot, ItemStack stack) {
        if (stack == null || stack.stackSize >= stack.getMaxStackSize()) {
            return true;
        }
        ItemStack result = handler.insertItem(slot, StackUtil.copyWithSize(stack, Integer.MAX_VALUE), true);
        return result == null || result.stackSize < Integer.MAX_VALUE;
    }

    public static boolean consumeFromPlayerInventory(EntityPlayer player, Predicate<ItemStack> request, int amount, boolean simulate) {
        ItemStack[] contents = player.inventory.mainInventory;
        for (int pass = 0; pass < 2; ++pass) {
            int amountNeeded = amount;
            for (int i = 0; i < contents.length; ++i) {
                ItemStack stack = contents[i];
                if (!request.apply((Object)stack)) continue;
                if (player.capabilities.isCreativeMode) {
                    return true;
                }
                int cAmount = Math.min(stack.stackSize, amountNeeded);
                amountNeeded -= cAmount;
                if (pass == 1) {
                    if (stack.stackSize == cAmount) {
                        contents[i] = null;
                    } else {
                        contents[i].stackSize -= cAmount;
                    }
                }
                if (amountNeeded <= 0) break;
            }
            if (amountNeeded > 0) {
                if (pass == 1) {
                    IC2.log.warn(LogCategory.General, "Inconsistent inventory transaction for player %s, request %s: %d missing", new Object[]{player, request, amountNeeded});
                }
                return false;
            }
            if (!simulate) continue;
            return true;
        }
        return true;
    }

    public static Predicate<ItemStack> sameStack(final ItemStack stack) {
        if (stack == null) {
            throw new NullPointerException("null stack");
        }
        return new Predicate<ItemStack>(){

            public boolean apply(ItemStack input) {
                return StackUtil.checkItemEquality(input, stack);
            }

            public String toString() {
                return "stack==" + (Object)stack;
            }
        };
    }

    public static Predicate<ItemStack> sameItem(final Item item) {
        if (item == null) {
            throw new NullPointerException("null item");
        }
        return new Predicate<ItemStack>(){

            public boolean apply(ItemStack input) {
                return input.getItem() == item;
            }

            public String toString() {
                return "item==" + (Object)item;
            }
        };
    }

    public static Predicate<ItemStack> sameItem(Block block) {
        if (block == null) {
            throw new NullPointerException("null block");
        }
        Item item = Item.getItemFromBlock((Block)block);
        if (item == null) {
            throw new IllegalArgumentException("block " + (Object)block + " doesn't have an associated item");
        }
        return StackUtil.sameItem(item);
    }

    public static ItemStack consumeFromPlayerHand(EntityPlayer player, Predicate<ItemStack> request, int amount) {
        ItemStack stack = player.getHeldItemMainhand();
        if (stack == null || !StackUtil.check(stack)) {
            return null;
        }
        if (!request.apply((Object)stack)) {
            return null;
        }
        if (player.capabilities.isCreativeMode) {
            return stack;
        }
        if (stack.stackSize < amount) {
            return null;
        }
        stack.stackSize -= amount;
        if (stack.stackSize == 0) {
            player.inventory.mainInventory[player.inventory.currentItem] = null;
        }
        return stack;
    }

    public static void dropAsEntity(World world, BlockPos pos, ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            return;
        }
        double f = 0.7;
        double dx = (double)world.rand.nextFloat() * f + (1.0 - f) * 0.5;
        double dy = (double)world.rand.nextFloat() * f + (1.0 - f) * 0.5;
        double dz = (double)world.rand.nextFloat() * f + (1.0 - f) * 0.5;
        EntityItem entityItem = new EntityItem(world, (double)pos.getX() + dx, (double)pos.getY() + dy, (double)pos.getZ() + dz, stack.copy());
        entityItem.setDefaultPickupDelay();
        world.spawnEntityInWorld((Entity)entityItem);
    }

    public static ItemStack copyWithSize(ItemStack stack, int newSize) {
        ItemStack ret = stack.copy();
        ret.stackSize = newSize;
        return ret;
    }

    public static ItemStack copyWithWildCard(ItemStack stack) {
        ItemStack ret = stack.copy();
        StackUtil.setRawMeta(ret, 32767);
        return ret;
    }

    public static NBTTagCompound getOrCreateNbtData(ItemStack stack) {
        NBTTagCompound ret = stack.getTagCompound();
        if (ret == null) {
            ret = new NBTTagCompound();
            stack.setTagCompound(ret);
        }
        return ret;
    }

    public static boolean checkItemEquality(ItemStack a, ItemStack b) {
        return a == b || a != null && b != null && a.getItem() == b.getItem() && (!a.getHasSubtypes() || a.getMetadata() == b.getMetadata()) && StackUtil.checkNbtEquality(a, b);
    }

    public static boolean checkItemEquality(ItemStack a, Item b) {
        return a == null && b == null || a != null && b != null && a.getItem() == b;
    }

    public static boolean checkItemEqualityStrict(ItemStack a, ItemStack b) {
        return a == b || a != null && b != null && a.isItemEqual(b) && StackUtil.checkNbtEqualityStrict(a, b);
    }

    private static boolean checkNbtEquality(ItemStack a, ItemStack b) {
        NBTTagCompound nbtB;
        NBTTagCompound nbtA = a.getTagCompound();
        if (nbtA == (nbtB = b.getTagCompound())) {
            return true;
        }
        Set keysA = nbtA != null ? nbtA.getKeySet() : Collections.emptySet();
        Set keysB = nbtB != null ? nbtB.getKeySet() : Collections.emptySet();
        HashSet<String> toCheck = new HashSet<String>(Math.max(keysA.size(), keysB.size()));
        for (String key22 : keysA) {
            if (ignoredNbtKeys.contains(key22)) continue;
            if (!keysB.contains(key22)) {
                return false;
            }
            toCheck.add(key22);
        }
        for (String key22 : keysB) {
            if (ignoredNbtKeys.contains(key22)) continue;
            if (!keysA.contains(key22)) {
                return false;
            }
            toCheck.add(key22);
        }
        for (String key22 : toCheck) {
            if (nbtA.getTag(key22).equals((Object)nbtB.getTag(key22))) continue;
            return false;
        }
        return true;
    }

    private static boolean checkNbtEqualityStrict(ItemStack a, ItemStack b) {
        NBTTagCompound nbtB;
        NBTTagCompound nbtA = a.getTagCompound();
        if (nbtA == (nbtB = b.getTagCompound())) {
            return true;
        }
        return nbtA != null && nbtB != null && nbtA.equals((Object)nbtB);
    }

    @Deprecated
    public static boolean areStacksEqual(ItemStack a, ItemStack b) {
        return a == null && b == null || a != null && b != null && !a.isItemEqual(b);
    }

    @Deprecated
    public static boolean isStackEqual(ItemStack stack1, ItemStack stack2) {
        return stack1 == null && stack2 == null || stack1 != null && stack2 != null && stack1.getItem() == stack2.getItem() && (!stack1.getHasSubtypes() && !stack1.isItemStackDamageable() || stack1.getItemDamage() == stack2.getItemDamage());
    }

    @Deprecated
    public static boolean isStackEqualStrict(ItemStack stack1, ItemStack stack2) {
        return StackUtil.isStackEqual(stack1, stack2) && ItemStack.areItemStackTagsEqual((ItemStack)stack1, (ItemStack)stack2);
    }

    public static boolean isTagEqual(ItemStack a, ItemStack b) {
        boolean bEmpty;
        boolean aEmpty = !a.hasTagCompound() || a.getTagCompound().hasNoTags();
        boolean bl = bEmpty = !b.hasTagCompound() || b.getTagCompound().hasNoTags();
        if (aEmpty != bEmpty) {
            return false;
        }
        if (aEmpty) {
            return true;
        }
        return a.getTagCompound().equals((Object)b.getTagCompound());
    }

    public static ItemStack getPickStack(World world, BlockPos pos, IBlockState state, EntityPlayer player) {
        Block block = state.getBlock();
        RayTraceResult target = new RayTraceResult(RayTraceResult.Type.BLOCK, new Vec3d((Vec3i)pos), EnumFacing.DOWN, pos);
        ItemStack ret = FMLCommonHandler.instance().getSide().isClient() ? block.getPickBlock(state, target, world, pos, player) : new ItemStack(block, 1, block.getMetaFromState(state));
        if (ret == null || !StackUtil.check(ret)) {
            return null;
        }
        return ret;
    }

    public static List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        return state.getBlock().getDrops(world, pos, state, fortune);
    }

    public static List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, EntityPlayer player, int fortune, boolean silkTouch) {
        ItemStack drop;
        Block block = state.getBlock();
        if (block.isAir(state, world, pos)) {
            return Collections.emptyList();
        }
        World rawWorld = null;
        if (silkTouch) {
            rawWorld = Util.getWorld(world);
            if (rawWorld == null) {
                throw new IllegalArgumentException("invalid world for silk touch: " + (Object)world);
            }
            if (player == null) {
                player = Ic2Player.get(rawWorld);
            }
        }
        if (silkTouch && block.canSilkHarvest(rawWorld, pos, state, player) && (drop = StackUtil.getPickStack(rawWorld, pos, state, player)) != null) {
            return new ArrayList<ItemStack>(Arrays.asList(new ItemStack[]{drop}));
        }
        return StackUtil.getDrops(world, pos, state, fortune);
    }

    public static boolean placeBlock(ItemStack stack, World world, BlockPos pos) {
        Item item = stack.getItem();
        if (item == null) {
            return false;
        }
        if (item instanceof ItemBlock || item instanceof ItemBlockSpecial) {
            int oldSize = stack.stackSize;
            EnumActionResult result = item.onItemUse(stack, (EntityPlayer)Ic2Player.get(world), world, pos, EnumHand.MAIN_HAND, EnumFacing.DOWN, 0.0f, 0.0f, 0.0f);
            stack.stackSize = oldSize;
            return result == EnumActionResult.SUCCESS;
        }
        return false;
    }

    @Deprecated
    public static Block getBlock(ItemStack stack) {
        Item item = stack.getItem();
        if (item instanceof ItemBlock) {
            return ((ItemBlock)item).block;
        }
        return null;
    }

    @Deprecated
    public static IBlockState getBlockState(ItemStack stack) {
        return StackUtil.getBlock(stack).getStateFromMeta(stack.getItemDamage());
    }

    @Deprecated
    public static boolean equals(Block block, ItemStack stack) {
        return block == StackUtil.getBlock(stack);
    }

    public static boolean damageItemStack(ItemStack stack, int amount) {
        if (stack.attemptDamageItem(amount, IC2.random)) {
            --stack.stackSize;
            stack.setItemDamage(0);
            return stack.stackSize <= 0;
        }
        return false;
    }

    public static boolean check2(Iterable<List<ItemStack>> list) {
        for (List<ItemStack> list2 : list) {
            if (StackUtil.check(list2)) continue;
            return false;
        }
        return true;
    }

    public static boolean check(ItemStack[] array) {
        return StackUtil.check(Arrays.asList(array));
    }

    public static boolean check(Iterable<ItemStack> list) {
        for (ItemStack stack : list) {
            if (StackUtil.check(stack)) continue;
            return false;
        }
        return true;
    }

    public static boolean check(ItemStack stack) {
        return stack.getItem() != null;
    }

    public static String toStringSafe2(Iterable<List<ItemStack>> list) {
        String ret = "[";
        for (List<ItemStack> list2 : list) {
            if (ret.length() > 1) {
                ret = ret + ", ";
            }
            ret = ret + StackUtil.toStringSafe(list2);
        }
        ret = ret + "]";
        return ret;
    }

    public static String toStringSafe(ItemStack[] array) {
        return StackUtil.toStringSafe(Arrays.asList(array));
    }

    public static String toStringSafe(Iterable<ItemStack> list) {
        String ret = "[";
        for (ItemStack stack : list) {
            if (ret.length() > 1) {
                ret = ret + ", ";
            }
            ret = ret + StackUtil.toStringSafe(stack);
        }
        ret = ret + "]";
        return ret;
    }

    public static String toStringSafe(ItemStack stack) {
        if (stack.getItem() == null) {
            return "" + stack.stackSize + "x(null)@(unknown)";
        }
        return stack.toString();
    }

    @Deprecated
    public static void consumeInventoryItem(EntityPlayer player, ItemStack stack) {
        for (int i = 0; i < player.inventory.mainInventory.length; ++i) {
            if (player.inventory.mainInventory[i] == null || !player.inventory.mainInventory[i].isItemEqual(stack)) continue;
            player.inventory.decrStackSize(i, 1);
            return;
        }
    }

    public static boolean storeInventoryItem(ItemStack stack, EntityPlayer player, boolean simulate) {
        if (simulate) {
            for (int i = 0; i < player.inventory.mainInventory.length; ++i) {
                ItemStack invStack = player.inventory.mainInventory[i];
                if (invStack != null && (!StackUtil.checkItemEqualityStrict(stack, invStack) || invStack.stackSize + stack.stackSize > Math.min(player.inventory.getInventoryStackLimit(), invStack.getMaxStackSize()))) continue;
                return true;
            }
        } else if (player.inventory.addItemStackToInventory(stack)) {
            return true;
        }
        return false;
    }

    public static int getRawMeta(ItemStack stack) {
        return Items.DYE.getDamage(stack);
    }

    public static void setRawMeta(ItemStack stack, int meta) {
        if (meta < 0) {
            throw new IllegalArgumentException("negative meta");
        }
        Items.DYE.setDamage(stack, meta);
    }

    public static void copyStack(ItemStack src, ItemStack dst) {
        dst.setItem(src.getItem());
        StackUtil.setRawMeta(dst, StackUtil.getRawMeta(src));
        dst.setTagCompound(src.getTagCompound());
    }

    public static class PersonalAdjacentInv
    extends AdjacentInv {
        public final IPersonalBlock owner;

        private PersonalAdjacentInv(TileEntity te, EnumFacing dir, IPersonalBlock owner) {
            super(te, dir);
            this.owner = owner;
        }
    }

    public static class AdjacentInv {
        public final TileEntity te;
        public final EnumFacing dir;

        private AdjacentInv(TileEntity te, EnumFacing dir) {
            this.te = te;
            this.dir = dir;
        }
    }

}

