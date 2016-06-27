/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockDynamicLiquid
 *  net.minecraft.block.BlockFire
 *  net.minecraft.block.BlockStaticLiquid
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.crafting.FurnaceRecipes
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.RayTraceResult$Type
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.registry.IThrowableEntity
 */
package ic2.core.item.tool;

import ic2.core.ExplosionIC2;
import ic2.core.IC2;
import ic2.core.Platform;
import ic2.core.util.Quaternion;
import ic2.core.util.StackUtil;
import ic2.core.util.Util;
import ic2.core.util.Vector3;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockFire;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IThrowableEntity;

public class EntityParticle
extends Entity
implements IThrowableEntity {
    private double coreSize;
    private double influenceSize;
    private int lifeTime;
    private Entity owner;
    private Vector3[] radialTestVectors;

    public EntityParticle(World world) {
        super(world);
        this.noClip = true;
        this.lifeTime = 6000;
    }

    public EntityParticle(World world, EntityLivingBase owner1, float speed, double coreSize1, double influenceSize1) {
        this(world);
        this.coreSize = coreSize1;
        this.influenceSize = influenceSize1;
        this.owner = owner1;
        Vector3 eyePos = Util.getEyePosition(this.owner);
        this.setPosition(eyePos.x, eyePos.y, eyePos.z);
        Vector3 motion = new Vector3(owner1.getLookVec());
        Vector3 ortho = motion.copy().cross(Vector3.UP).scaleTo(influenceSize1);
        double stepAngle = Math.atan(0.5 / influenceSize1) * 2.0;
        int steps = (int)Math.ceil(6.283185307179586 / stepAngle);
        Quaternion q = new Quaternion().setFromAxisAngle(motion, stepAngle);
        this.radialTestVectors = new Vector3[steps];
        this.radialTestVectors[0] = ortho.copy();
        for (int i = 1; i < steps; ++i) {
            q.rotate(ortho);
            this.radialTestVectors[i] = ortho.copy();
        }
        motion.scale(speed);
        this.motionX = motion.x;
        this.motionY = motion.y;
        this.motionZ = motion.z;
    }

    protected void entityInit() {
    }

    protected void readEntityFromNBT(NBTTagCompound nbttagcompound) {
    }

    protected void writeEntityToNBT(NBTTagCompound nbttagcompound) {
    }

    public Entity getThrower() {
        return this.owner;
    }

    public void setThrower(Entity entity) {
        this.owner = entity;
    }

    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.posX += this.motionX;
        this.posY += this.motionY;
        this.posZ += this.motionZ;
        Vector3 start = new Vector3(this.prevPosX, this.prevPosY, this.prevPosZ);
        Vector3 end = new Vector3(this.posX, this.posY, this.posZ);
        RayTraceResult hit = this.worldObj.rayTraceBlocks(start.toVec3(), end.toVec3(), true);
        if (hit != null) {
            end.set(hit.hitVec);
            this.posX = hit.hitVec.xCoord;
            this.posY = hit.hitVec.yCoord;
            this.posZ = hit.hitVec.zCoord;
        }
        List entitiesToCheck = this.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)this, new AxisAlignedBB(this.prevPosX, this.prevPosY, this.prevPosZ, this.posX, this.posY, this.posZ).expand(this.influenceSize, this.influenceSize, this.influenceSize));
        ArrayList<RayTraceResult> entitiesInfluences = new ArrayList<RayTraceResult>();
        double minDistanceSq = start.distanceSquared(end);
        for (Entity entity : entitiesToCheck) {
            RayTraceResult entityInfluence;
            double distanceSq;
            if (entity == this.owner || !entity.canBeCollidedWith() || (entityInfluence = entity.getEntityBoundingBox().expand(this.influenceSize, this.influenceSize, this.influenceSize).calculateIntercept(start.toVec3(), end.toVec3())) == null) continue;
            entitiesInfluences.add(entityInfluence);
            RayTraceResult entityHit = entity.getEntityBoundingBox().expand(this.coreSize, this.coreSize, this.coreSize).calculateIntercept(start.toVec3(), end.toVec3());
            if (entityHit == null || (distanceSq = start.distanceSquared(entityHit.hitVec)) >= minDistanceSq) continue;
            hit = entityHit;
            minDistanceSq = distanceSq;
        }
        double maxInfluenceDistance = Math.sqrt(minDistanceSq) + this.influenceSize;
        for (RayTraceResult entityInfluence : entitiesInfluences) {
            if (start.distance(entityInfluence.hitVec) > maxInfluenceDistance) continue;
            this.onInfluence(entityInfluence);
        }
        if (this.radialTestVectors != null) {
            Vector3 vForward = end.copy().sub(start);
            double len = vForward.length();
            vForward.scale(1.0 / len);
            Vector3 origin = new Vector3(start);
            Vector3 tmp = new Vector3();
            int d = 0;
            while ((double)d < len) {
                for (int i = 0; i < this.radialTestVectors.length; ++i) {
                    origin.copy(tmp).add(this.radialTestVectors[i]);
                    RayTraceResult influence = this.worldObj.rayTraceBlocks(origin.toVec3(), tmp.toVec3(), true);
                    if (influence == null) continue;
                    this.onInfluence(influence);
                }
                origin.add(vForward);
                ++d;
            }
        }
        if (hit != null) {
            this.onImpact(hit);
            this.setDead();
        } else {
            --this.lifeTime;
            if (this.lifeTime <= 0) {
                this.setDead();
            }
        }
    }

    protected void onImpact(RayTraceResult hit) {
        if (!IC2.platform.isSimulating()) {
            return;
        }
        System.out.println("hit " + (Object)hit.typeOfHit + " " + (Object)hit.hitVec + " sim=" + IC2.platform.isSimulating());
        if (hit.typeOfHit != RayTraceResult.Type.BLOCK || IC2.platform.isSimulating()) {
            // empty if block
        }
        ExplosionIC2 explosion = new ExplosionIC2(this.worldObj, this.owner, hit.hitVec.xCoord, hit.hitVec.yCoord, hit.hitVec.zCoord, 18.0f, 0.95f, ExplosionIC2.Type.Heat);
        explosion.doExplosion();
    }

    protected void onInfluence(RayTraceResult hit) {
        if (!IC2.platform.isSimulating()) {
            return;
        }
        System.out.println("influenced " + (Object)hit.typeOfHit + " " + (Object)hit.hitVec + " sim=" + IC2.platform.isSimulating());
        if (hit.typeOfHit == RayTraceResult.Type.BLOCK && IC2.platform.isSimulating()) {
            IBlockState state = this.worldObj.getBlockState(hit.getBlockPos());
            Block block = state.getBlock();
            if (block == Blocks.WATER || block == Blocks.FLOWING_WATER) {
                this.worldObj.setBlockToAir(hit.getBlockPos());
            } else {
                List<ItemStack> drops = StackUtil.getDrops((IBlockAccess)this.worldObj, hit.getBlockPos(), state, null, 0, true);
                if (drops.size() == 1 && drops.get((int)0).stackSize == 1) {
                    ItemStack existing = drops.get(0);
                    ItemStack smelted = FurnaceRecipes.instance().getSmeltingResult(existing);
                    if (smelted != null && smelted.getItem() instanceof ItemBlock) {
                        this.worldObj.setBlockState(hit.getBlockPos(), ((ItemBlock)smelted.getItem()).block.getDefaultState());
                    } else if (block.isFlammable((IBlockAccess)this.worldObj, hit.getBlockPos(), hit.sideHit)) {
                        this.worldObj.setBlockState(hit.getBlockPos().offset(hit.sideHit.getOpposite()), Blocks.FIRE.getDefaultState());
                    }
                }
            }
        }
    }
}

