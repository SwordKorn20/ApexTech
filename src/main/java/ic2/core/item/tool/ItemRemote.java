/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package ic2.core.item.tool;

import ic2.core.IC2;
import ic2.core.Platform;
import ic2.core.audio.AudioManager;
import ic2.core.audio.PositionSpec;
import ic2.core.block.BlockDynamite;
import ic2.core.item.ItemIC2;
import ic2.core.ref.BlockName;
import ic2.core.ref.IBlockModelProvider;
import ic2.core.ref.ItemName;
import ic2.core.util.StackUtil;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemRemote
extends ItemIC2 {
    public ItemRemote() {
        super(ItemName.remote);
        this.setMaxStackSize(1);
    }

    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote) {
            return EnumActionResult.SUCCESS;
        }
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (block != BlockName.dynamite.getInstance()) {
            return EnumActionResult.SUCCESS;
        }
        if (!((Boolean)state.getValue(BlockDynamite.linked)).booleanValue()) {
            ItemRemote.addRemote(pos, stack);
            world.setBlockState(pos, state.withProperty(BlockDynamite.linked, (Comparable)Boolean.valueOf(true)));
        } else {
            int index = ItemRemote.hasRemote(pos, stack);
            if (index > -1) {
                world.setBlockState(pos, state.withProperty(BlockDynamite.linked, (Comparable)Boolean.valueOf(false)));
                ItemRemote.removeRemote(index, stack);
            } else {
                IC2.platform.messagePlayer(player, "This dynamite stick is not linked to this remote, cannot unlink.", new Object[0]);
            }
        }
        return EnumActionResult.SUCCESS;
    }

    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if (world.isRemote) {
            return new ActionResult(EnumActionResult.SUCCESS, (Object)stack);
        }
        IC2.audioManager.playOnce((Object)player, PositionSpec.Hand, "Tools/dynamiteomote.ogg", true, IC2.audioManager.getDefaultVolume());
        ItemRemote.launchRemotes(world, stack, player);
        return new ActionResult(EnumActionResult.SUCCESS, (Object)stack);
    }

    public static void addRemote(BlockPos pos, ItemStack freq) {
        NBTTagCompound compound = StackUtil.getOrCreateNbtData(freq);
        if (!compound.hasKey("coords")) {
            compound.setTag("coords", (NBTBase)new NBTTagList());
        }
        NBTTagList coords = compound.getTagList("coords", 10);
        NBTTagCompound coord = new NBTTagCompound();
        coord.setInteger("x", pos.getX());
        coord.setInteger("y", pos.getY());
        coord.setInteger("z", pos.getZ());
        coords.appendTag((NBTBase)coord);
        compound.setTag("coords", (NBTBase)coords);
        freq.setItemDamage(coords.tagCount());
    }

    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        if (stack.getItemDamage() > 0) {
            tooltip.add("Linked to " + stack.getItemDamage() + " dynamite");
        }
    }

    public static void launchRemotes(World world, ItemStack freq, EntityPlayer player) {
        NBTTagCompound compound = StackUtil.getOrCreateNbtData(freq);
        if (!compound.hasKey("coords")) {
            return;
        }
        NBTTagList coords = compound.getTagList("coords", 10);
        int i = 0;
        while (i < coords.tagCount()) {
            NBTTagCompound coord = coords.getCompoundTagAt(i);
            BlockPos pos = new BlockPos(coord.getInteger("x"), coord.getInteger("y"), coord.getInteger("z"));
            IBlockState state = world.getBlockState(pos);
            if (world.isBlockLoaded(pos)) {
                if (state.getBlock() == BlockName.dynamite.getInstance() && ((Boolean)state.getValue(BlockDynamite.linked)).booleanValue()) {
                    state.getBlock().removedByPlayer(state, world, pos, player, false);
                    world.setBlockToAir(pos);
                }
                coords.removeTag(i);
                continue;
            }
            ++i;
        }
        freq.setItemDamage(0);
    }

    public static int hasRemote(BlockPos pos, ItemStack freq) {
        NBTTagCompound compound = StackUtil.getOrCreateNbtData(freq);
        if (!compound.hasKey("coords")) {
            return -1;
        }
        NBTTagList coords = compound.getTagList("coords", 10);
        for (int i = 0; i < coords.tagCount(); ++i) {
            NBTTagCompound coord = coords.getCompoundTagAt(i);
            if (coord.getInteger("x") != pos.getX() || coord.getInteger("y") != pos.getY() || coord.getInteger("z") != pos.getZ()) continue;
            return i;
        }
        return -1;
    }

    public static void removeRemote(int index, ItemStack freq) {
        NBTTagCompound compound = StackUtil.getOrCreateNbtData(freq);
        if (!compound.hasKey("coords")) {
            return;
        }
        NBTTagList coords = compound.getTagList("coords", 10);
        NBTTagList newCoords = new NBTTagList();
        for (int i = 0; i < coords.tagCount(); ++i) {
            if (i == index) continue;
            newCoords.appendTag((NBTBase)coords.getCompoundTagAt(i));
        }
        compound.setTag("coords", (NBTBase)newCoords);
        freq.setItemDamage(newCoords.tagCount());
    }
}

