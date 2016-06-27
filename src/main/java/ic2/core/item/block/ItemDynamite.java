/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockDispenser
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.PlayerCapabilities
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.registry.RegistryDefaulted
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package ic2.core.item.block;

import ic2.api.item.IBoxable;
import ic2.core.IC2;
import ic2.core.Platform;
import ic2.core.block.BehaviorDynamiteDispense;
import ic2.core.block.EntityDynamite;
import ic2.core.block.EntityStickyDynamite;
import ic2.core.item.ItemIC2;
import ic2.core.ref.BlockName;
import ic2.core.ref.IBlockModelProvider;
import ic2.core.ref.ItemName;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.RegistryDefaulted;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class ItemDynamite
extends ItemIC2
implements IBoxable {
    public boolean sticky;

    public ItemDynamite(ItemName name) {
        super(name);
        this.sticky = name == ItemName.dynamite_sticky;
        this.setMaxStackSize(16);
        BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject((Object)this, (Object)new BehaviorDynamiteDispense(this.sticky));
    }

    public int getMetadata(int i) {
        return i;
    }

    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer entityplayer, World world, BlockPos pos, EnumHand hand, EnumFacing side, float a, float b, float c) {
        if (this.sticky) {
            return EnumActionResult.PASS;
        }
        pos = pos.offset(side);
        IBlockState state = world.getBlockState(pos);
        Object dynamite = BlockName.dynamite.getInstance();
        if (state.getBlock().isAir(state, (IBlockAccess)world, pos) && dynamite.canReplace(world, pos, side, stack) && dynamite.canPlaceBlockAt(world, pos)) {
            world.setBlockState(pos, dynamite.onBlockPlaced(world, pos, side, a, b, c, 0, (EntityLivingBase)entityplayer), 3);
            --stack.stackSize;
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.FAIL;
    }

    public ActionResult<ItemStack> onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer, EnumHand hand) {
        if (!entityplayer.capabilities.isCreativeMode) {
            --itemstack.stackSize;
        }
        world.playSound(entityplayer, entityplayer.getPosition(), SoundEvents.ENTITY_ARROW_SHOOT, SoundCategory.PLAYERS, 0.5f, 0.4f / (itemRand.nextFloat() * 0.4f + 0.8f));
        if (IC2.platform.isSimulating()) {
            if (this.sticky) {
                world.spawnEntityInWorld((Entity)new EntityStickyDynamite(world, (EntityLivingBase)entityplayer));
            } else {
                world.spawnEntityInWorld((Entity)new EntityDynamite(world, (EntityLivingBase)entityplayer));
            }
        }
        return new ActionResult(EnumActionResult.SUCCESS, (Object)itemstack);
    }

    @Override
    public boolean canBeStoredInToolbox(ItemStack itemstack) {
        return true;
    }
}

