/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyEnum
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package ic2.core.item.tool;

import ic2.api.item.IBoxable;
import ic2.core.IC2;
import ic2.core.IC2Achievements;
import ic2.core.audio.AudioManager;
import ic2.core.audio.PositionSpec;
import ic2.core.block.BlockRubWood;
import ic2.core.block.state.IIdProvider;
import ic2.core.item.ItemIC2;
import ic2.core.item.type.MiscResourceType;
import ic2.core.ref.BlockName;
import ic2.core.ref.IBlockModelProvider;
import ic2.core.ref.ItemName;
import ic2.core.util.StackUtil;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemTreetap
extends ItemIC2
implements IBoxable {
    public ItemTreetap() {
        super(ItemName.treetap);
        this.setMaxStackSize(1);
        this.setMaxDamage(16);
    }

    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float xOffset, float yOffset, float zOffset) {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (block == BlockName.rubber_wood.getInstance()) {
            ItemTreetap.attemptExtract(player, world, pos, side, state, null);
            if (!world.isRemote) {
                stack.damageItem(1, (EntityLivingBase)player);
            }
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

    public static boolean attemptExtract(EntityPlayer player, World world, BlockPos pos, EnumFacing side, IBlockState state, List<ItemStack> stacks) {
        assert (state.getBlock() == BlockName.rubber_wood.getInstance());
        BlockRubWood.RubberWoodState rwState = (BlockRubWood.RubberWoodState)((Object)state.getValue(BlockRubWood.stateProperty));
        if (rwState.isPlain() || rwState.facing != side) {
            return false;
        }
        if (rwState.wet) {
            if (!world.isRemote) {
                world.setBlockState(pos, state.withProperty(BlockRubWood.stateProperty, (Comparable)((Object)rwState.getDry())));
                if (stacks != null) {
                    stacks.add(StackUtil.copyWithSize(ItemName.misc_resource.getItemStack(MiscResourceType.resin), world.rand.nextInt(3) + 1));
                } else {
                    ItemTreetap.ejectResin(world, pos, side, world.rand.nextInt(3) + 1);
                }
                if (player != null) {
                    IC2.achievements.issueAchievement(player, "acquireResin");
                }
            }
            if (world.isRemote && player != null) {
                IC2.audioManager.playOnce((Object)player, PositionSpec.Hand, "Tools/Treetap.ogg", true, IC2.audioManager.getDefaultVolume());
            }
            return true;
        }
        if (!world.isRemote && world.rand.nextInt(5) == 0) {
            world.setBlockState(pos, state.withProperty(BlockRubWood.stateProperty, (Comparable)((Object)BlockRubWood.RubberWoodState.plain_y)));
        }
        if (world.rand.nextInt(5) == 0) {
            if (!world.isRemote) {
                ItemTreetap.ejectResin(world, pos, side, 1);
                if (stacks != null) {
                    stacks.add(ItemName.misc_resource.getItemStack(MiscResourceType.resin));
                } else {
                    ItemTreetap.ejectResin(world, pos, side, 1);
                }
            }
            if (world.isRemote && player != null) {
                IC2.audioManager.playOnce((Object)player, PositionSpec.Hand, "Tools/Treetap.ogg", true, IC2.audioManager.getDefaultVolume());
            }
            return true;
        }
        return false;
    }

    private static void ejectResin(World world, BlockPos pos, EnumFacing side, int quantity) {
        double ejectBias = 0.3;
        double ejectX = (double)pos.getX() + 0.5 + (double)side.getFrontOffsetX() * 0.3;
        double ejectY = (double)pos.getY() + 0.5 + (double)side.getFrontOffsetY() * 0.3;
        double ejectZ = (double)pos.getZ() + 0.5 + (double)side.getFrontOffsetZ() * 0.3;
        for (int i = 0; i < quantity; ++i) {
            EntityItem entityitem = new EntityItem(world, ejectX, ejectY, ejectZ, ItemName.misc_resource.getItemStack(MiscResourceType.resin));
            entityitem.setDefaultPickupDelay();
            world.spawnEntityInWorld((Entity)entityitem);
        }
    }

    @Override
    public boolean canBeStoredInToolbox(ItemStack itemstack) {
        return true;
    }
}

