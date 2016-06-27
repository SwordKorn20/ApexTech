/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockFire
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.IEntityMultiPart
 *  net.minecraft.entity.boss.EntityDragon
 *  net.minecraft.entity.boss.EntityDragonPart
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.crafting.FurnaceRecipes
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EntityDamageSourceIndirect
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.RayTraceResult$Type
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.Explosion
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.Event
 *  net.minecraftforge.fml.common.eventhandler.EventBus
 *  net.minecraftforge.fml.common.registry.IThrowableEntity
 */
package ic2.core.item.tool;

import ic2.api.event.LaserEvent;
import ic2.core.ExplosionIC2;
import ic2.core.IC2;
import ic2.core.IC2Achievements;
import ic2.core.Platform;
import ic2.core.block.MaterialIC2TNT;
import ic2.core.ref.BlockName;
import ic2.core.ref.IBlockModelProvider;
import ic2.core.util.StackUtil;
import ic2.core.util.Vector3;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFire;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityDragonPart;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.registry.IThrowableEntity;

public class EntityMiningLaser
extends Entity
implements IThrowableEntity {
    public float range = 0.0f;
    public float power = 0.0f;
    public int blockBreaks = 0;
    public boolean explosive = false;
    public static final double laserSpeed = 1.0;
    public EntityLivingBase owner;
    public boolean headingSet = false;
    public boolean smelt = false;
    private int ticksInAir = 0;

    public EntityMiningLaser(World world) {
        super(world);
        this.setSize(0.8f, 0.8f);
    }

    public EntityMiningLaser(World world, Vector3 start, Vector3 dir, EntityLivingBase owner, float range, float power, int blockBreaks, boolean explosive) {
        super(world);
        this.owner = owner;
        this.setSize(0.8f, 0.8f);
        this.setPosition(start.x, start.y, start.z);
        this.setLaserHeading(dir.x, dir.y, dir.z, 1.0);
        this.range = range;
        this.power = power;
        this.blockBreaks = blockBreaks;
        this.explosive = explosive;
    }

    protected void entityInit() {
    }

    public void setLaserHeading(double motionX, double motionY, double motionZ, double speed) {
        double currentSpeed = MathHelper.sqrt_double((double)(motionX * motionX + motionY * motionY + motionZ * motionZ));
        this.motionX = motionX / currentSpeed * speed;
        this.motionY = motionY / currentSpeed * speed;
        this.motionZ = motionZ / currentSpeed * speed;
        this.prevRotationYaw = this.rotationYaw = (float)Math.toDegrees(Math.atan2(motionX, motionZ));
        this.prevRotationPitch = this.rotationPitch = (float)Math.toDegrees(Math.atan2(motionY, MathHelper.sqrt_double((double)(motionX * motionX + motionZ * motionZ))));
        this.headingSet = true;
    }

    public void setVelocity(double motionX, double motionY, double motionZ) {
        this.setLaserHeading(motionX, motionY, motionZ, 1.0);
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    public void onUpdate() {
        super.onUpdate();
        if (IC2.platform.isSimulating() && (this.range < 1.0f || this.power <= 0.0f || this.blockBreaks <= 0)) {
            if (this.explosive) {
                this.explode();
            }
            this.setDead();
            return;
        }
        ++this.ticksInAir;
        oldPosition = new Vec3d(this.posX, this.posY, this.posZ);
        newPosition = new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        result = this.worldObj.rayTraceBlocks(oldPosition, newPosition, false, true, false);
        oldPosition = new Vec3d(this.posX, this.posY, this.posZ);
        newPosition = result != null ? new Vec3d(result.hitVec.xCoord, result.hitVec.yCoord, result.hitVec.zCoord) : new Vec3d(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        entity = null;
        list = this.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)this, this.getEntityBoundingBox().addCoord(this.motionX, this.motionY, this.motionZ).expand(1.0, 1.0, 1.0));
        d = 0.0;
        for (l = 0; l < list.size(); ++l) {
            entity1 = (Entity)list.get(l);
            if (!entity1.canBeCollidedWith() || entity1 == this.owner && this.ticksInAir < 5) continue;
            f4 = 0.3f;
            axisalignedbb1 = entity1.getEntityBoundingBox().expand((double)f4, (double)f4, (double)f4);
            movingobjectposition1 = axisalignedbb1.calculateIntercept(oldPosition, newPosition);
            if (movingobjectposition1 == null || (d1 = oldPosition.distanceTo(movingobjectposition1.hitVec)) >= d && d != 0.0) continue;
            entity = entity1;
            d = d1;
        }
        if (entity != null) {
            result = new RayTraceResult(entity);
        }
        if (result == null || result.typeOfHit == RayTraceResult.Type.MISS || this.worldObj.isRemote) ** GOTO lbl43
        if (this.explosive) {
            this.explode();
            this.setDead();
            return;
        }
        switch (.$SwitchMap$net$minecraft$util$math$RayTraceResult$Type[result.typeOfHit.ordinal()]) {
            case 1: {
                if (!this.hitBlock(result.getBlockPos(), result.sideHit)) {
                    this.power -= 0.5f;
                }
                ** GOTO lbl44
            }
            case 2: {
                this.hitEntity(result.entityHit);
                ** GOTO lbl44
            }
            default: {
                throw new RuntimeException("invalid hit type: " + (Object)result.typeOfHit);
            }
        }
lbl43: // 1 sources:
        this.power -= 0.5f;
lbl44: // 3 sources:
        this.setPosition(this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ);
        this.range = (float)((double)this.range - Math.sqrt(this.motionX * this.motionX + this.motionY * this.motionY + this.motionZ * this.motionZ));
        if (this.isInWater() == false) return;
        this.setDead();
    }

    private void explode() {
        LaserEvent.LaserExplodesEvent event = new LaserEvent.LaserExplodesEvent(this.worldObj, this, this.owner, this.range, this.power, this.blockBreaks, this.explosive, this.smelt, 5.0f, 0.85f, 0.55f);
        MinecraftForge.EVENT_BUS.post((Event)event);
        if (event.isCanceled()) {
            this.setDead();
            return;
        }
        this.copyDataFromEvent(event);
        ExplosionIC2 explosion = new ExplosionIC2(this.worldObj, null, this.posX, this.posY, this.posZ, event.explosionPower, event.explosionDropRate);
        explosion.doExplosion();
    }

    private void hitEntity(Entity entity) {
        LaserEvent.LaserHitsEntityEvent event = new LaserEvent.LaserHitsEntityEvent(this.worldObj, this, this.owner, this.range, this.power, this.blockBreaks, this.explosive, this.smelt, entity);
        MinecraftForge.EVENT_BUS.post((Event)event);
        if (event.isCanceled()) {
            this.setDead();
            return;
        }
        this.copyDataFromEvent(event);
        entity = event.hitEntity;
        int damage = (int)this.power;
        if (damage > 0) {
            entity.setFire(damage * (this.smelt ? 2 : 1));
            if (entity.attackEntityFrom(new EntityDamageSourceIndirect("arrow", (Entity)this, (Entity)this.owner).setProjectile(), (float)damage) && (this.owner instanceof EntityPlayer && entity instanceof EntityDragon && ((EntityDragon)entity).getHealth() <= 0.0f || entity instanceof EntityDragonPart && ((EntityDragonPart)entity).entityDragonObj instanceof EntityDragon && ((EntityLivingBase)((EntityDragonPart)entity).entityDragonObj).getHealth() <= 0.0f)) {
                IC2.achievements.issueAchievement((EntityPlayer)this.owner, "killDragonMiningLaser");
            }
        }
        this.setDead();
    }

    private boolean hitBlock(BlockPos pos, EnumFacing side) {
        LaserEvent.LaserHitsBlockEvent event = new LaserEvent.LaserHitsBlockEvent(this.worldObj, this, this.owner, this.range, this.power, this.blockBreaks, this.explosive, this.smelt, pos, side, 0.9f, true, true);
        MinecraftForge.EVENT_BUS.post((Event)event);
        if (event.isCanceled()) {
            this.setDead();
            return true;
        }
        this.copyDataFromEvent(event);
        IBlockState state = this.worldObj.getBlockState(event.pos);
        Block block = state.getBlock();
        if (block.isAir(state, (IBlockAccess)this.worldObj, event.pos) || block == Blocks.GLASS || block == Blocks.GLASS_PANE || block == BlockName.glass.getInstance()) {
            return false;
        }
        if (this.worldObj.isRemote) {
            return true;
        }
        float hardness = state.getBlockHardness(this.worldObj, event.pos);
        if (hardness < 0.0f) {
            this.setDead();
            return true;
        }
        this.power -= hardness / 1.5f;
        if (this.power < 0.0f) {
            return true;
        }
        if (state.getMaterial() == Material.TNT || state.getMaterial() == MaterialIC2TNT.instance) {
            block.onBlockDestroyedByExplosion(this.worldObj, event.pos, new Explosion(this.worldObj, (Entity)this, (double)event.pos.getX() + 0.5, (double)event.pos.getY() + 0.5, (double)event.pos.getZ() + 0.5, 1.0f, false, true));
        } else if (this.smelt) {
            if (state.getMaterial() == Material.WOOD) {
                event.dropBlock = false;
            } else {
                for (ItemStack isa : block.getDrops((IBlockAccess)this.worldObj, event.pos, state, 0)) {
                    ItemStack is = FurnaceRecipes.instance().getSmeltingResult(isa);
                    if (is == null) continue;
                    if (StackUtil.placeBlock(is, this.worldObj, event.pos)) {
                        event.removeBlock = false;
                        event.dropBlock = false;
                    } else {
                        event.dropBlock = false;
                        StackUtil.dropAsEntity(this.worldObj, event.pos, is);
                    }
                    this.power = 0.0f;
                }
            }
        }
        if (event.removeBlock) {
            if (event.dropBlock) {
                block.dropBlockAsItemWithChance(this.worldObj, event.pos, state, event.dropChance, 0);
            }
            this.worldObj.setBlockToAir(event.pos);
            if (this.worldObj.rand.nextInt(10) == 0 && state.getMaterial().getCanBurn()) {
                this.worldObj.setBlockState(event.pos, Blocks.FIRE.getDefaultState());
            }
        }
        --this.blockBreaks;
        return false;
    }

    public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
    }

    public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
    }

    void copyDataFromEvent(LaserEvent event) {
        this.owner = event.owner;
        this.range = event.range;
        this.power = event.power;
        this.blockBreaks = event.blockBreaks;
        this.explosive = event.explosive;
        this.smelt = event.smelt;
    }

    public Entity getThrower() {
        return this.owner;
    }

    public void setThrower(Entity entity) {
        if (entity instanceof EntityLivingBase) {
            this.owner = (EntityLivingBase)entity;
        }
    }

}

