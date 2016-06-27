/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  javax.annotation.Nullable
 *  net.minecraft.block.BlockLiquid
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyInteger
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.item.EntityBoat
 *  net.minecraft.entity.item.EntityBoat$Status
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.passive.EntityWaterMob
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketSteerBoat
 *  net.minecraft.util.EntitySelectors
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$PooledMutableBlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package ic2.core.item;

import com.google.common.base.Predicate;
import ic2.core.util.ReflectionUtil;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityWaterMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketSteerBoat;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class EntityIC2Boat
extends EntityBoat {
    private static Method method_tickLerp;
    private static Field field_paddlePositions;
    private static Field field_previousStatus;
    private static Field field_status;
    private static Field field_outOfControlTicks;
    private static Field field_momentum;
    private static Field field_lastYd;
    private static Field field_waterLevel;
    private static Field field_boatGlide;
    private static Field field_deltaRotation;
    private static Field field_rightInputDown;
    private static Field field_leftInputDown;
    private static Field field_forwardInputDown;
    private static Field field_backInputDown;

    public static void init() {
        method_tickLerp = EntityIC2Boat.getMethod("tickLerp", "func_184447_s", new Class[0]);
        field_paddlePositions = EntityIC2Boat.getField("paddlePositions", "field_184470_f");
        field_previousStatus = EntityIC2Boat.getField("previousStatus", "field_184471_aG");
        field_status = EntityIC2Boat.getField("status", "field_184469_aF");
        field_outOfControlTicks = EntityIC2Boat.getField("outOfControlTicks", "field_184474_h");
        field_momentum = EntityIC2Boat.getField("momentum", "field_184472_g");
        field_lastYd = EntityIC2Boat.getField("lastYd", "field_184473_aH");
        field_waterLevel = EntityIC2Boat.getField("waterLevel", "field_184465_aD");
        field_boatGlide = EntityIC2Boat.getField("boatGlide", "field_184467_aE");
        field_deltaRotation = EntityIC2Boat.getField("deltaRotation", "field_184475_as");
        field_rightInputDown = EntityIC2Boat.getField("rightInputDown", "field_184459_aA");
        field_leftInputDown = EntityIC2Boat.getField("leftInputDown", "field_184480_az");
        field_forwardInputDown = EntityIC2Boat.getField("forwardInputDown", "field_184461_aB");
        field_backInputDown = EntityIC2Boat.getField("backInputDown", "field_184463_aC");
    }

    private static Field getField(String deobfName, String srgName) {
        return ReflectionUtil.getField(EntityBoat.class, srgName, deobfName);
    }

    private static /* varargs */ Method getMethod(String deobfName, String srgName, Class<?> ... parameterTypes) {
        return ReflectionUtil.getMethod(EntityBoat.class, new String[]{deobfName, srgName}, parameterTypes);
    }

    public EntityIC2Boat(World par1World) {
        super(par1World);
    }

    public void onUpdate() {
        try {
            field_previousStatus.set((Object)this, field_status.get((Object)this));
            EntityBoat.Status status = this.getBoatStatus();
            field_status.set((Object)this, (Object)status);
            if (status != EntityBoat.Status.UNDER_WATER && status != EntityBoat.Status.UNDER_FLOWING_WATER) {
                field_outOfControlTicks.setFloat((Object)this, 0.0f);
            } else {
                field_outOfControlTicks.setFloat((Object)this, field_outOfControlTicks.getFloat((Object)this) + 1.0f);
            }
            if (!this.worldObj.isRemote && field_outOfControlTicks.getFloat((Object)this) >= 60.0f) {
                this.removePassengers();
            }
            if (this.getTimeSinceHit() > 0) {
                this.setTimeSinceHit(this.getTimeSinceHit() - 1);
            }
            if (this.getDamageTaken() > 0.0f) {
                this.setDamageTaken(this.getDamageTaken() - 1.0f);
            }
            this.prevPosX = this.posX;
            this.prevPosY = this.posY;
            this.prevPosZ = this.posZ;
            super.onEntityUpdate();
            method_tickLerp.invoke((Object)this, new Object[0]);
            if (this.canPassengerSteer()) {
                if (this.getPassengers().size() == 0 || !(this.getPassengers().get(0) instanceof EntityPlayer)) {
                    this.setPaddleState(false, false);
                }
                this.updateMotion();
                if (this.worldObj.isRemote) {
                    this.controlBoat();
                    this.worldObj.sendPacketToServer((Packet)new CPacketSteerBoat(this.getPaddleState(0), this.getPaddleState(1)));
                }
                this.moveEntity(this.motionX, this.motionY, this.motionZ);
            } else {
                this.motionX = 0.0;
                this.motionY = 0.0;
                this.motionZ = 0.0;
            }
            for (int i = 0; i <= 1; ++i) {
                if (this.getPaddleState(i)) {
                    Array.setFloat(field_paddlePositions.get((Object)this), i, (float)((double)Array.getFloat(field_paddlePositions.get((Object)this), i) + 0.01));
                    continue;
                }
                Array.setFloat(field_paddlePositions.get((Object)this), i, 0.0f);
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Error reflecting boat in update", e);
        }
        this.doBlockCollisions();
        List list = this.worldObj.getEntitiesInAABBexcluding((Entity)this, this.getEntityBoundingBox().expand(0.2, -0.01, 0.2), EntitySelectors.getTeamCollisionPredicate((Entity)this));
        if (!list.isEmpty()) {
            boolean flag = !this.worldObj.isRemote && !(this.getControllingPassenger() instanceof EntityPlayer);
            for (Entity entity : list) {
                if (entity.isPassenger((Entity)this)) continue;
                if (flag && this.getPassengers().size() < 2 && !entity.isRiding() && entity.width < this.width && entity instanceof EntityLivingBase && !(entity instanceof EntityWaterMob) && !(entity instanceof EntityPlayer)) {
                    entity.startRiding((Entity)this);
                    continue;
                }
                this.applyEntityCollision(entity);
            }
        }
    }

    private void updateMotion() {
        double generalMotionChangingThing = -0.04;
        double heightChange = 0.0;
        float momentum = 0.05f;
        try {
            EntityBoat.Status status = (EntityBoat.Status)field_status.get((Object)this);
            if (field_previousStatus.get((Object)this) == EntityBoat.Status.IN_AIR && status != EntityBoat.Status.IN_AIR && status != EntityBoat.Status.ON_LAND) {
                field_waterLevel.setDouble((Object)this, this.getEntityBoundingBox().minY + (double)this.height);
                this.setPosition(this.posX, (double)(this.getWaterLevelAbove() - this.height) + 0.101, this.posZ);
                this.motionY = 0.0;
                field_lastYd.setDouble((Object)this, 0.0);
                field_status.set((Object)this, (Object)EntityBoat.Status.IN_WATER);
            } else {
                switch (status) {
                    case IN_AIR: {
                        momentum = 0.9f;
                        break;
                    }
                    case IN_WATER: {
                        heightChange = (field_waterLevel.getDouble((Object)this) - this.getEntityBoundingBox().minY) / (double)this.height;
                        momentum = 0.9f;
                        break;
                    }
                    case ON_LAND: {
                        momentum = field_boatGlide.getFloat((Object)this);
                        if (!(this.getControllingPassenger() instanceof EntityPlayer)) break;
                        field_boatGlide.setFloat((Object)this, field_boatGlide.getFloat((Object)this) / 2.0f);
                        break;
                    }
                    case UNDER_FLOWING_WATER: {
                        generalMotionChangingThing = -7.0E-4;
                        momentum = 0.9f;
                        break;
                    }
                    case UNDER_WATER: {
                        heightChange = 0.01;
                        momentum = 0.45f;
                    }
                }
                this.motionX *= (double)momentum;
                this.motionZ *= (double)momentum;
                field_deltaRotation.setFloat((Object)this, field_deltaRotation.getFloat((Object)this) * momentum);
                this.motionY += generalMotionChangingThing;
                if (heightChange > 0.0) {
                    this.motionY += heightChange * 0.061538461538461535;
                    this.motionY *= 0.75;
                }
            }
            field_momentum.setFloat((Object)this, momentum);
        }
        catch (Exception e) {
            throw new RuntimeException("Error reflecting boat in updateMotion", e);
        }
    }

    public float getWaterLevelAbove() {
        AxisAlignedBB boundingBox = this.getEntityBoundingBox();
        int minX = MathHelper.floor_double((double)boundingBox.minX);
        int maxX = MathHelper.ceiling_double_int((double)boundingBox.maxX);
        int minZ = MathHelper.floor_double((double)boundingBox.minZ);
        int maxZ = MathHelper.ceiling_double_int((double)boundingBox.maxZ);
        BlockPos.PooledMutableBlockPos blockPosPool = BlockPos.PooledMutableBlockPos.retain();
        try {
            int maxY = MathHelper.ceiling_double_int((double)(boundingBox.maxY - field_lastYd.getDouble((Object)this)));
            block6 : for (int y2 = MathHelper.floor_double((double)boundingBox.maxY); y2 < maxY; ++y2) {
                float waterHeight = 0.0f;
                int x = minX;
                do {
                    if (x >= maxX) {
                        if (waterHeight >= 1.0f) continue block6;
                        float f = (float)blockPosPool.getY() + waterHeight;
                        return f;
                    }
                    for (int z = minZ; z < maxZ; ++z) {
                        blockPosPool.set(x, y2, z);
                        IBlockState block = this.worldObj.getBlockState((BlockPos)blockPosPool);
                        if (this.isOnWater(block)) {
                            waterHeight = Math.max(waterHeight, EntityIC2Boat.getBlockLiquidHeight(block, (IBlockAccess)this.worldObj, (BlockPos)blockPosPool));
                        }
                        if (waterHeight >= 1.0f) continue block6;
                    }
                    ++x;
                } while (true);
            }
            float y2 = maxY + 1;
            return y2;
        }
        catch (Exception e) {
            throw new RuntimeException("Error reflecting boat in getWaterLevelAbove", e);
        }
        finally {
            blockPosPool.release();
        }
    }

    private EntityBoat.Status getBoatStatus() {
        EntityBoat.Status isUnderWater = this.getUnderwaterStatus();
        try {
            if (isUnderWater != null) {
                field_waterLevel.setDouble((Object)this, this.getEntityBoundingBox().maxY);
                return isUnderWater;
            }
            if (this.checkInWater()) {
                return EntityBoat.Status.IN_WATER;
            }
            float glideSpeed = this.getBoatGlide();
            if (glideSpeed > 0.0f) {
                field_boatGlide.setFloat((Object)this, glideSpeed);
                return EntityBoat.Status.ON_LAND;
            }
            return EntityBoat.Status.IN_AIR;
        }
        catch (Exception e) {
            throw new RuntimeException("Error reflecting boat in getBoatStatus", e);
        }
    }

    private boolean checkInWater() {
        boolean isInWater;
        AxisAlignedBB boundingBox = this.getEntityBoundingBox();
        isInWater = false;
        BlockPos.PooledMutableBlockPos blockPosPool = BlockPos.PooledMutableBlockPos.retain();
        try {
            field_waterLevel.setDouble((Object)this, Double.MIN_VALUE);
            for (int x = MathHelper.floor_double((double)boundingBox.minX); x < MathHelper.ceiling_double_int((double)boundingBox.maxX); ++x) {
                for (int y = MathHelper.floor_double((double)boundingBox.minY); y < MathHelper.ceiling_double_int((double)(boundingBox.minY + 0.001)); ++y) {
                    for (int z = MathHelper.floor_double((double)boundingBox.minZ); z < MathHelper.ceiling_double_int((double)boundingBox.maxZ); ++z) {
                        blockPosPool.set(x, y, z);
                        IBlockState block = this.worldObj.getBlockState((BlockPos)blockPosPool);
                        if (!this.isOnWater(block)) continue;
                        float waterHeight = EntityIC2Boat.getLiquidHeight(block, (IBlockAccess)this.worldObj, (BlockPos)blockPosPool);
                        field_waterLevel.setDouble((Object)this, Math.max((double)waterHeight, field_waterLevel.getDouble((Object)this)));
                        isInWater |= boundingBox.minY < (double)waterHeight;
                    }
                }
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Error reflecting boat in checkInWater", e);
        }
        finally {
            blockPosPool.release();
        }
        return isInWater;
    }

    @Nullable
    private EntityBoat.Status getUnderwaterStatus() {
        boolean isUnderWater;
        block8 : {
            AxisAlignedBB boundingBox = this.getEntityBoundingBox();
            double boatTop = boundingBox.maxY + 0.001;
            isUnderWater = false;
            BlockPos.PooledMutableBlockPos blockPosPool = BlockPos.PooledMutableBlockPos.retain();
            try {
                for (int x = MathHelper.floor_double((double)boundingBox.minX); x < MathHelper.ceiling_double_int((double)boundingBox.maxX); ++x) {
                    for (int y = MathHelper.floor_double((double)boundingBox.maxY); y < MathHelper.ceiling_double_int((double)boatTop); ++y) {
                        for (int z = MathHelper.floor_double((double)boundingBox.minZ); z < MathHelper.ceiling_double_int((double)boundingBox.maxZ); ++z) {
                            blockPosPool.set(x, y, z);
                            IBlockState block = this.worldObj.getBlockState((BlockPos)blockPosPool);
                            if (!this.isOnWater(block) || boatTop >= (double)EntityIC2Boat.getLiquidHeight(block, (IBlockAccess)this.worldObj, (BlockPos)blockPosPool)) continue;
                            if ((Integer)block.getValue((IProperty)BlockLiquid.LEVEL) != 0) {
                                EntityBoat.Status status = EntityBoat.Status.UNDER_FLOWING_WATER;
                                return status;
                            }
                            isUnderWater = true;
                            break block8;
                        }
                    }
                }
            }
            finally {
                blockPosPool.release();
            }
        }
        return isUnderWater ? EntityBoat.Status.UNDER_WATER : null;
    }

    public static float getBlockLiquidHeight(IBlockState block, IBlockAccess world, BlockPos pos) {
        int liquidHeight = (Integer)block.getValue((IProperty)BlockLiquid.LEVEL);
        return (liquidHeight & 7) == 0 && world.getBlockState(pos.up()).getMaterial() == block.getMaterial() ? 1.0f : 1.0f - BlockLiquid.getLiquidHeightPercent((int)liquidHeight);
    }

    public static float getLiquidHeight(IBlockState block, IBlockAccess world, BlockPos pos) {
        return (float)pos.getY() + EntityIC2Boat.getBlockLiquidHeight(block, world, pos);
    }

    private void controlBoat() {
        if (this.isBeingRidden()) {
            float speed = 0.0f;
            try {
                boolean left = field_leftInputDown.getBoolean((Object)this);
                boolean right = field_rightInputDown.getBoolean((Object)this);
                boolean forward = field_forwardInputDown.getBoolean((Object)this);
                boolean backward = field_backInputDown.getBoolean((Object)this);
                if (left) {
                    field_deltaRotation.setFloat((Object)this, field_deltaRotation.getFloat((Object)this) - 1.0f);
                }
                if (right) {
                    field_deltaRotation.setFloat((Object)this, field_deltaRotation.getFloat((Object)this) + 1.0f);
                }
                if (right != left && !forward && !backward) {
                    speed += 0.005f;
                }
                this.rotationYaw += field_deltaRotation.getFloat((Object)this);
                if (forward) {
                    speed += 0.04f;
                }
                if (backward) {
                    speed -= 0.005f;
                }
                this.motionX += (double)(MathHelper.sin((float)((- this.rotationYaw) * 3.1415927f / 180.0f)) * speed) * this.getAccelerationFactor();
                this.motionZ += (double)(MathHelper.cos((float)(this.rotationYaw * 3.1415927f / 180.0f)) * speed) * this.getAccelerationFactor();
                this.setPaddleState(right || forward, left || forward);
            }
            catch (Exception e) {
                throw new RuntimeException("Error reflecting boat in controlBoat", e);
            }
        }
    }

    public EntityItem dropItemWithOffset(Item item, int meta, float yOffset) {
        if (item == Items.BOAT) {
            return this.entityDropItem(this.getItem(), yOffset);
        }
        return super.dropItemWithOffset(item, meta, yOffset);
    }

    public abstract String getTexture();

    public ItemStack getPickedResult(RayTraceResult target) {
        return this.getItem();
    }

    protected ItemStack getItem() {
        return new ItemStack(Items.BOAT);
    }

    @Deprecated
    protected void breakBoat(double motion) {
        this.entityDropItem(this.getItem(), 0.0f);
    }

    protected double getAccelerationFactor() {
        return 1.0;
    }

    protected double getTopSpeed() {
        return 0.35;
    }

    protected boolean isOnWater(IBlockState block) {
        return block.getMaterial() == Material.WATER;
    }

}

