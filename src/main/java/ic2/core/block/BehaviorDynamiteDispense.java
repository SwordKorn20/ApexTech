/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.dispenser.BehaviorProjectileDispense
 *  net.minecraft.dispenser.IPosition
 *  net.minecraft.entity.IProjectile
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 */
package ic2.core.block;

import ic2.core.block.EntityDynamite;
import ic2.core.block.EntityStickyDynamite;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class BehaviorDynamiteDispense
extends BehaviorProjectileDispense {
    private final boolean sticky;

    public BehaviorDynamiteDispense(boolean sticky) {
        this.sticky = sticky;
    }

    protected IProjectile getProjectileEntity(World world, IPosition pos, ItemStack stack) {
        if (this.sticky) {
            return new EntityStickyDynamite(world, pos.getX(), pos.getY(), pos.getZ());
        }
        return new EntityDynamite(world, pos.getX(), pos.getY(), pos.getZ());
    }
}

