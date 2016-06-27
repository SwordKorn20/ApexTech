/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.init.Blocks
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.World
 */
package ic2.core.block;

import ic2.core.ExplosionIC2;
import ic2.core.IC2;
import ic2.core.Platform;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityIC2Explosive
extends Entity {
    public DamageSource damageSource;
    public EntityLivingBase igniter;
    public int fuse = 80;
    public float explosivePower = 4.0f;
    public int radiationRange = 0;
    public float dropRate = 0.3f;
    public float damageVsEntitys = 1.0f;
    public IBlockState renderBlockState = Blocks.DIRT.getDefaultState();

    public EntityIC2Explosive(World world) {
        super(world);
        this.preventEntitySpawning = true;
        this.setSize(0.98f, 0.98f);
    }

    public EntityIC2Explosive(World world, double x, double y, double z, int fuse, float power, float dropRate, float damage, IBlockState renderBlockState, int radiationRange) {
        this(world);
        this.setPosition(x, y, z);
        float f = (float)(Math.random() * 3.1415927410125732 * 2.0);
        this.motionX = (- MathHelper.sin((float)(f * 3.141593f / 180.0f))) * 0.02f;
        this.motionY = 0.20000000298023224;
        this.motionZ = (- MathHelper.cos((float)(f * 3.141593f / 180.0f))) * 0.02f;
        this.prevPosX = x;
        this.prevPosY = y;
        this.prevPosZ = z;
        this.fuse = fuse;
        this.explosivePower = power;
        this.radiationRange = radiationRange;
        this.dropRate = dropRate;
        this.damageVsEntitys = damage;
        this.renderBlockState = renderBlockState;
    }

    protected void entityInit() {
    }

    protected boolean canTriggerWalking() {
        return false;
    }

    public boolean canBeCollidedWith() {
        return !this.isDead;
    }

    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.motionY -= 0.04;
        this.moveEntity(this.motionX, this.motionY, this.motionZ);
        this.motionX *= 0.98;
        this.motionY *= 0.98;
        this.motionZ *= 0.98;
        if (this.onGround) {
            this.motionX *= 0.7;
            this.motionZ *= 0.7;
            this.motionY *= -0.5;
        }
        if (this.fuse-- <= 0) {
            this.setDead();
            if (IC2.platform.isSimulating()) {
                this.explode();
            }
        } else {
            this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY + 0.5, this.posZ, 0.0, 0.0, 0.0, new int[0]);
        }
    }

    private void explode() {
        ExplosionIC2 explosion = new ExplosionIC2(this.worldObj, this, this.posX, this.posY, this.posZ, this.explosivePower, this.dropRate, this.radiationRange > 0 ? ExplosionIC2.Type.Nuclear : ExplosionIC2.Type.Normal, this.igniter, this.radiationRange);
        explosion.doExplosion();
    }

    protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setByte("Fuse", (byte)this.fuse);
    }

    protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
        this.fuse = nbttagcompound.getByte("Fuse");
    }

    public EntityIC2Explosive setIgniter(EntityLivingBase igniter1) {
        this.igniter = igniter1;
        return this;
    }
}

