/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package ic2.core.block.machine.tileentity;

import ic2.core.block.EntityIC2Explosive;
import ic2.core.block.EntityItnt;
import ic2.core.block.machine.tileentity.Explosive;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ITnt
extends Explosive {
    @Override
    protected boolean explodeOnRemoval() {
        return true;
    }

    @Override
    protected EntityIC2Explosive getEntity(EntityLivingBase igniter) {
        return new EntityItnt(this.worldObj, (double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5);
    }
}

