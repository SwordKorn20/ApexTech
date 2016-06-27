/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package ic2.core.block.beam;

import ic2.core.block.beam.TileEmitter;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityParticle
extends Entity {
    private static final double initialVelocity = 0.5;
    private static final double slowdown = 0.99;

    public EntityParticle(World world) {
        super(world);
        this.noClip = true;
    }

    public EntityParticle(TileEmitter emitter) {
        this(emitter.getWorld());
        EnumFacing dir = emitter.getFacing();
        double x = (double)emitter.getPos().getX() + 0.5 + (double)dir.getFrontOffsetX() * 0.5;
        double y = (double)emitter.getPos().getY() + 0.5 + (double)dir.getFrontOffsetY() * 0.5;
        double z = (double)emitter.getPos().getZ() + 0.5 + (double)dir.getFrontOffsetZ() * 0.5;
        this.setPosition(x, y, z);
        this.motionX = (double)dir.getFrontOffsetX() * 0.5;
        this.motionY = (double)dir.getFrontOffsetY() * 0.5;
        this.motionZ = (double)dir.getFrontOffsetZ() * 0.5;
        this.setSize(0.2f, 0.2f);
    }

    protected void entityInit() {
    }

    protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
    }

    protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
    }

    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.99;
        this.motionY *= 0.99;
        this.motionZ *= 0.99;
        if (this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ < 1.0E-4) {
            this.setDead();
        }
    }
}

