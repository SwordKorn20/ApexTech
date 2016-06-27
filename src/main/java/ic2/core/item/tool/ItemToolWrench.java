/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.entity.player.PlayerCapabilities
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.server.management.PlayerInteractionManager
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumFacing$Axis
 *  net.minecraft.util.EnumFacing$AxisDirection
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldSettings
 *  net.minecraft.world.WorldSettings$GameType
 *  net.minecraftforge.common.ForgeHooks
 */
package ic2.core.item.tool;

import com.mojang.authlib.GameProfile;
import ic2.api.item.IBoxable;
import ic2.api.tile.IWrenchable;
import ic2.core.IC2;
import ic2.core.audio.AudioManager;
import ic2.core.audio.PositionSpec;
import ic2.core.init.MainConfig;
import ic2.core.item.ItemIC2;
import ic2.core.ref.ItemName;
import ic2.core.util.ConfigUtil;
import ic2.core.util.Keyboard;
import ic2.core.util.Log;
import ic2.core.util.LogCategory;
import ic2.core.util.StackUtil;
import ic2.core.util.Util;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.management.PlayerInteractionManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldSettings;
import net.minecraftforge.common.ForgeHooks;

public class ItemToolWrench
extends ItemIC2
implements IBoxable {
    public ItemToolWrench() {
        this(ItemName.wrench);
    }

    protected ItemToolWrench(ItemName name) {
        super(name);
        this.setMaxDamage(120);
        this.setMaxStackSize(1);
    }

    public boolean canTakeDamage(ItemStack stack, int amount) {
        return true;
    }

    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (!this.canTakeDamage(stack, 1)) {
            return EnumActionResult.FAIL;
        }
        WrenchResult result = ItemToolWrench.wrenchBlock(world, pos, side, player, this.canTakeDamage(stack, 10));
        if (result != WrenchResult.Nothing) {
            if (!world.isRemote) {
                this.damage(stack, result == WrenchResult.Rotated ? 1 : 10, player);
            } else {
                IC2.audioManager.playOnce((Object)player, PositionSpec.Hand, "Tools/wrench.ogg", true, IC2.audioManager.getDefaultVolume());
            }
            return world.isRemote ? EnumActionResult.PASS : EnumActionResult.SUCCESS;
        }
        return EnumActionResult.FAIL;
    }

    public static WrenchResult wrenchBlock(World world, BlockPos pos, EnumFacing side, EntityPlayer player, boolean remove) {
        IBlockState state = Util.getBlockState((IBlockAccess)world, pos);
        Block block = state.getBlock();
        if (block.isAir(state, (IBlockAccess)world, pos)) {
            return WrenchResult.Nothing;
        }
        if (block instanceof IWrenchable) {
            EnumFacing currentFacing;
            IWrenchable wrenchable = (IWrenchable)block;
            EnumFacing newFacing = currentFacing = wrenchable.getFacing(world, pos);
            if (IC2.keyboard.isAltKeyDown(player)) {
                EnumFacing.Axis axis = side.getAxis();
                if (side.getAxisDirection() == EnumFacing.AxisDirection.POSITIVE && !player.isSneaking() || side.getAxisDirection() == EnumFacing.AxisDirection.NEGATIVE && player.isSneaking()) {
                    newFacing = newFacing.rotateAround(axis);
                } else {
                    for (int i = 0; i < 3; ++i) {
                        newFacing = newFacing.rotateAround(axis);
                    }
                }
            } else {
                newFacing = player.isSneaking() ? side.getOpposite() : side;
            }
            if (newFacing != currentFacing && wrenchable.setFacing(world, pos, newFacing, player)) {
                return WrenchResult.Rotated;
            }
            if (remove && wrenchable.wrenchCanRemove(world, pos, player)) {
                if (!world.isRemote) {
                    int experience;
                    TileEntity te = world.getTileEntity(pos);
                    if (ConfigUtil.getBool(MainConfig.get(), "protection/wrenchLogging")) {
                        String playerName = player.getGameProfile().getName() + "/" + player.getGameProfile().getId();
                        String teName = te != null ? te.getClass().getSimpleName().replace("TileEntity", "") : "no te";
                        IC2.log.info(LogCategory.PlayerActivity, "Player %s used a wrench to remove the %s (%s) at %s.", new Object[]{playerName, state, teName, Util.formatPosition((IBlockAccess)world, pos)});
                    }
                    if (player instanceof EntityPlayerMP) {
                        experience = ForgeHooks.onBlockBreakEvent((World)world, (WorldSettings.GameType)((EntityPlayerMP)player).interactionManager.getGameType(), (EntityPlayerMP)((EntityPlayerMP)player), (BlockPos)pos);
                        if (experience < 0) {
                            return WrenchResult.Nothing;
                        }
                    } else {
                        experience = 0;
                    }
                    block.onBlockHarvested(world, pos, state, player);
                    if (!block.removedByPlayer(state, world, pos, player, true)) {
                        return WrenchResult.Nothing;
                    }
                    block.onBlockDestroyedByPlayer(world, pos, state);
                    List<ItemStack> drops = wrenchable.getWrenchDrops(world, pos, state, te, player, 0);
                    for (ItemStack stack : drops) {
                        StackUtil.dropAsEntity(world, pos, stack);
                    }
                    if (!player.capabilities.isCreativeMode && experience > 0) {
                        block.dropXpOnBlockBreak(world, pos, experience);
                    }
                }
                return WrenchResult.Removed;
            }
        } else if (block.rotateBlock(world, pos, side)) {
            return WrenchResult.Rotated;
        }
        return WrenchResult.Nothing;
    }

    public void damage(ItemStack is, int damage, EntityPlayer player) {
        is.damageItem(damage, (EntityLivingBase)player);
    }

    @Override
    public boolean canBeStoredInToolbox(ItemStack itemstack) {
        return true;
    }

    private static enum WrenchResult {
        Rotated,
        Removed,
        Nothing;
        

        private WrenchResult() {
        }
    }

}

