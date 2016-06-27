/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.IProjectile
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 */
package ic2.core.block;

import ic2.core.PointExplosion;
import ic2.core.util.Util;
import ic2.core.util.Vector3;
import java.util.Random;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IProjectile;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityDynamite
extends Entity
implements IProjectile {
    public boolean sticky = false;
    public static final int netId = 142;
    public BlockPos stickPos;
    public int fuse = 100;
    private boolean inGround = false;
    public EntityLivingBase owner;
    private int ticksInGround;

    public EntityDynamite(World world, double x, double y, double z) {
        super(world);
        this.setSize(0.5f, 0.5f);
        this.setPosition(x, y, z);
    }

    public EntityDynamite(World world) {
        this(world, 0.0, 0.0, 0.0);
    }

    public EntityDynamite(World world, EntityLivingBase owner) {
        super(world);
        this.owner = owner;
        this.setSize(0.5f, 0.5f);
        Vector3 eyePos = Util.getEyePosition((Entity)owner);
        this.setLocationAndAngles(eyePos.x, eyePos.y, eyePos.z, owner.rotationYaw, owner.rotationPitch);
        this.posX -= Math.cos(Math.toRadians(this.rotationYaw)) * 0.16;
        this.posY -= 0.1;
        this.posZ -= Math.sin(Math.toRadians(this.rotationYaw)) * 0.16;
        this.setPosition(this.posX, this.posY, this.posZ);
        this.motionX = (- Math.sin(Math.toRadians(this.rotationYaw))) * Math.cos(Math.toRadians(this.rotationPitch));
        this.motionZ = Math.cos(Math.toRadians(this.rotationYaw)) * Math.cos(Math.toRadians(this.rotationPitch));
        this.motionY = - Math.sin(Math.toRadians(this.rotationPitch));
        this.setThrowableHeading(this.motionX, this.motionY, this.motionZ, 1.0f, 1.0f);
    }

    protected void entityInit() {
    }

    public void setThrowableHeading(double d, double d1, double d2, float f, float f1) {
        float f2 = MathHelper.sqrt_double((double)(d * d + d1 * d1 + d2 * d2));
        d /= (double)f2;
        d1 /= (double)f2;
        d2 /= (double)f2;
        d += this.rand.nextGaussian() * 0.0075 * (double)f1;
        d1 += this.rand.nextGaussian() * 0.0075 * (double)f1;
        d2 += this.rand.nextGaussian() * 0.0075 * (double)f1;
        this.motionX = d *= (double)f;
        this.motionY = d1 *= (double)f;
        this.motionZ = d2 *= (double)f;
        float f3 = MathHelper.sqrt_double((double)(d * d + d2 * d2));
        this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(d, d2) * 180.0 / 3.141592653589793);
        this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(d1, f3) * 180.0 / 3.141592653589793);
        this.ticksInGround = 0;
    }

    public void setVelocity(double d, double d1, double d2) {
        this.motionX = d;
        this.motionY = d1;
        this.motionZ = d2;
        if (this.prevRotationPitch == 0.0f && this.prevRotationYaw == 0.0f) {
            float f = MathHelper.sqrt_double((double)(d * d + d2 * d2));
            this.prevRotationYaw = this.rotationYaw = (float)(Math.atan2(d, d2) * 180.0 / 3.141592653589793);
            this.prevRotationPitch = this.rotationPitch = (float)(Math.atan2(d1, f) * 180.0 / 3.141592653589793);
            this.prevRotationPitch = this.rotationPitch;
            this.prevRotationYaw = this.rotationYaw;
            this.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rotationYaw, this.rotationPitch);
            this.ticksInGround = 0;
        }
    }

    public void onUpdate() {
        Vec3d end;
        Vec3d start;
        RayTraceResult result;
        super.onUpdate();
        if (this.prevRotationPitch == 0.0f && this.prevRotationYaw == 0.0f) {
            float f = MathHelper.sqrt_double((double)(this.motionX * this.motionX + this.motionZ * this.motionZ));
            this.prevRotationYaw = this.rotationYaw = (float)Math.toDegrees(Math.atan2(this.motionX, this.motionZ));
            this.prevRotationPitch = this.rotationPitch = (float)Math.toDegrees(Math.atan2(this.motionY, f));
        }
        if (this.fuse-- <= 0) {
            this.setDead();
            if (!this.worldObj.isRemote) {
                this.explode();
            }
        } else if (this.fuse < 100 && this.fuse % 2 == 0) {
            this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY + 0.5, this.posZ, 0.0, 0.0, 0.0, new int[0]);
        }
        if (this.inGround) {
            ++this.ticksInGround;
            if (this.ticksInGround >= 200) {
                this.setDead();
            }
            if (this.sticky) {
                this.fuse -= 3;
                this.motionX = 0.0;
                this.motionY = 0.0;
                this.motionZ = 0.0;
                if (!this.worldObj.isAirBlock(this.stickPos)) {
                    return;
                }
            }
        }
        if ((result = this.worldObj.rayTraceBlocks(start = this.getPositionVector(), end = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ), false, true, false)) != null) {
            float remainX = (float)(result.hitVec.xCoord - this.posX);
            float remainY = (float)(result.hitVec.yCoord - this.posY);
            float remainZ = (float)(result.hitVec.zCoord - this.posZ);
            float f1 = MathHelper.sqrt_double((double)(remainX * remainX + remainY * remainY + remainZ * remainZ));
            this.stickPos = result.getBlockPos();
            this.posX -= (double)remainX / (double)f1 * 0.05;
            this.posY -= (double)remainY / (double)f1 * 0.05;
            this.posZ -= (double)remainZ / (double)f1 * 0.05;
            this.posX += (double)remainX;
            this.posY += (double)remainY;
            this.posZ += (double)remainZ;
            this.motionX *= (double)(0.75f - this.rand.nextFloat());
            this.motionY *= -0.30000001192092896;
            this.motionZ *= (double)(0.75f - this.rand.nextFloat());
            this.inGround = true;
        } else {
            this.posX += this.motionX;
            this.posY += this.motionY;
            this.posZ += this.motionZ;
            this.inGround = false;
        }
        float f2 = MathHelper.sqrt_double((double)(this.motionX * this.motionX + this.motionZ * this.motionZ));
        this.rotationYaw = (float)Math.toDegrees(Math.atan2(this.motionX, this.motionZ));
        this.rotationPitch = (float)Math.toDegrees(Math.atan2(this.motionY, f2));
        while (this.rotationPitch - this.prevRotationPitch < -180.0f) {
            this.prevRotationPitch -= 360.0f;
        }
        while (this.rotationPitch - this.prevRotationPitch >= 180.0f) {
            this.prevRotationPitch += 360.0f;
        }
        while (this.rotationYaw - this.prevRotationYaw < -180.0f) {
            this.prevRotationYaw -= 360.0f;
        }
        while (this.rotationYaw - this.prevRotationYaw >= 180.0f) {
            this.prevRotationYaw += 360.0f;
        }
        this.rotationPitch = this.prevRotationPitch + (this.rotationPitch - this.prevRotationPitch) * 0.2f;
        this.rotationYaw = this.prevRotationYaw + (this.rotationYaw - this.prevRotationYaw) * 0.2f;
        float f3 = 0.98f;
        float f5 = 0.04f;
        if (this.isInWater()) {
            this.fuse += 2000;
            for (int i1 = 0; i1 < 4; ++i1) {
                float f6 = 0.25f;
                this.worldObj.spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX - this.motionX * (double)f6, this.posY - this.motionY * (double)f6, this.posZ - this.motionZ * (double)f6, this.motionX, this.motionY, this.motionZ, new int[0]);
            }
            f3 = 0.75f;
        }
        this.motionX *= (double)f3;
        this.motionY *= (double)f3;
        this.motionZ *= (double)f3;
        this.motionY -= (double)f5;
        this.setPosition(this.posX, this.posY, this.posZ);
    }

    public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setByte("inGround", this.inGround ? 1 : 0);
    }

    public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
        this.inGround = nbttagcompound.getByte("inGround") == 1;
    }

    public void explode() {
        PointExplosion explosion = new PointExplosion(this.worldObj, this, this.owner, this.posX, this.posY, this.posZ, 1.0f, 1.0f, 20);
        explosion.doExplosionA();
        explosion.doExplosionB(true);
    }
}

