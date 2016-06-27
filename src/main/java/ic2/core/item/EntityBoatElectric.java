/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 */
package ic2.core.item;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItemManager;
import ic2.core.IC2;
import ic2.core.block.state.IIdProvider;
import ic2.core.item.EntityIC2Boat;
import ic2.core.item.ItemIC2Boat;
import ic2.core.ref.ItemName;
import ic2.core.util.Keyboard;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class EntityBoatElectric
extends EntityIC2Boat {
    private static final double euConsume = 4.0;
    private boolean accelerated = false;

    public EntityBoatElectric(World world) {
        super(world);
    }

    @Override
    protected ItemStack getItem() {
        return ItemName.boat.getItemStack(ItemIC2Boat.BoatType.electric);
    }

    @Override
    protected double getAccelerationFactor() {
        return this.accelerated ? 1.5 : 0.25;
    }

    @Override
    protected double getTopSpeed() {
        return 0.7;
    }

    @Override
    protected boolean isOnWater(IBlockState block) {
        return block.getMaterial() == Material.WATER || block.getMaterial() == Material.LAVA;
    }

    @Override
    public String getTexture() {
        return "textures/models/boatElectric.png";
    }

    @Override
    public void onUpdate() {
        this.isImmuneToFire = true;
        this.extinguish();
        if (this.getRidingEntity() != null) {
            this.getRidingEntity().extinguish();
        }
        this.accelerated = false;
        block0 : for (Entity riddenByEntity : this.getPassengers()) {
            if (!(riddenByEntity instanceof EntityPlayer) || !IC2.keyboard.isForwardKeyDown((EntityPlayer)riddenByEntity)) continue;
            EntityPlayer player = (EntityPlayer)riddenByEntity;
            for (int i = 0; i < 4; ++i) {
                ItemStack stack = player.inventory.armorInventory[i];
                if (stack == null || ElectricItem.manager.discharge(stack, 4.0, Integer.MAX_VALUE, true, true, true) != 4.0) continue;
                ElectricItem.manager.discharge(stack, 4.0, Integer.MAX_VALUE, true, true, false);
                this.accelerated = true;
                continue block0;
            }
        }
        super.onUpdate();
    }
}

