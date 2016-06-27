/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.world.World
 */
package ic2.core.item;

import ic2.core.block.state.IIdProvider;
import ic2.core.item.EntityIC2Boat;
import ic2.core.item.ItemIC2Boat;
import ic2.core.ref.ItemName;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.World;

public class EntityBoatRubber
extends EntityIC2Boat {
    public EntityBoatRubber(World world) {
        super(world);
    }

    @Override
    protected ItemStack getItem() {
        return ItemName.boat.getItemStack(ItemIC2Boat.BoatType.rubber);
    }

    @Override
    protected void breakBoat(double motion) {
        this.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 16.0f, 8.0f);
        this.entityDropItem(motion > 0.26 ? ItemName.boat.getItemStack(ItemIC2Boat.BoatType.broken_rubber) : this.getItem(), 0.0f);
    }

    @Override
    public String getTexture() {
        return "textures/models/boatRubber.png";
    }
}

