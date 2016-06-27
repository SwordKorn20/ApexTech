/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.PlayerCapabilities
 *  net.minecraft.init.Items
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.Explosion
 *  net.minecraft.world.World
 */
package ic2.core.block.machine.tileentity;

import ic2.core.block.EntityIC2Explosive;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.Redstone;
import ic2.core.block.comp.TileEntityComponent;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

public abstract class Explosive
extends TileEntityInventory
implements Redstone.IRedstoneChangeHandler {
    protected final Redstone redstone;
    private boolean exploded;

    protected Explosive() {
        this.redstone = this.addComponent(new Redstone(this));
        this.redstone.subscribe(this);
    }

    @Override
    public void onRedstoneChange(int newLevel) {
        if (newLevel > 0) {
            this.explode(null, false);
        }
    }

    @Override
    protected boolean onActivated(EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        Item item;
        if (heldItem != null && ((item = heldItem.getItem()) == Items.FLINT_AND_STEEL || item == Items.FIRE_CHARGE) && this.explode((EntityLivingBase)player, false)) {
            if (item == Items.FLINT_AND_STEEL) {
                heldItem.damageItem(1, (EntityLivingBase)player);
            } else if (!player.capabilities.isCreativeMode) {
                --heldItem.stackSize;
            }
            return true;
        }
        return super.onActivated(player, hand, heldItem, side, hitX, hitY, hitZ);
    }

    @Override
    protected void onExploded(Explosion explosion) {
        super.onExploded(explosion);
        this.explode(explosion.getExplosivePlacedBy(), true);
    }

    @Override
    protected boolean onRemovedByPlayer(EntityPlayer player, boolean willHarvest) {
        if (this.explodeOnRemoval()) {
            this.explode((EntityLivingBase)player, false);
            return true;
        }
        return super.onRemovedByPlayer(player, willHarvest);
    }

    @Override
    protected ItemStack adjustDrop(ItemStack drop, boolean wrench) {
        if (this.exploded) {
            return null;
        }
        return super.adjustDrop(drop, wrench);
    }

    protected boolean explode(EntityLivingBase igniter, boolean shortFuse) {
        EntityIC2Explosive entity = this.getEntity(igniter);
        if (entity == null) {
            return false;
        }
        if (this.worldObj.isRemote) {
            return true;
        }
        entity.setIgniter(igniter);
        this.onIgnite(igniter);
        this.worldObj.setBlockToAir(this.pos);
        if (shortFuse) {
            entity.fuse = this.worldObj.rand.nextInt(Math.max(1, entity.fuse / 4)) + entity.fuse / 8;
        }
        this.worldObj.spawnEntityInWorld((Entity)entity);
        this.worldObj.playSound((EntityPlayer)null, entity.posX, entity.posY, entity.posZ, SoundEvents.ENTITY_TNT_PRIMED, SoundCategory.BLOCKS, 1.0f, 1.0f);
        this.exploded = true;
        return true;
    }

    protected boolean explodeOnRemoval() {
        return false;
    }

    protected abstract EntityIC2Explosive getEntity(EntityLivingBase var1);

    protected void onIgnite(EntityLivingBase igniter) {
    }
}

