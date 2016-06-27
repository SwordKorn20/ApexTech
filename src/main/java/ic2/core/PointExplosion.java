/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.Explosion
 *  net.minecraft.world.World
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.Event
 *  net.minecraftforge.fml.common.eventhandler.EventBus
 */
package ic2.core;

import ic2.api.event.ExplosionEvent;
import ic2.core.util.Util;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;

public class PointExplosion
extends Explosion {
    private final World world;
    private final Entity entity;
    private final float dropRate;
    private final int entityDamage;
    private float explosionSize;

    public PointExplosion(World world1, Entity entity, EntityLivingBase exploder, double x, double y, double z, float power, float dropRate1, int entityDamage1) {
        super(world1, (Entity)exploder, x, y, z, power, true, true);
        this.world = world1;
        this.entity = entity;
        this.dropRate = dropRate1;
        this.entityDamage = entityDamage1;
        this.explosionSize = power;
    }

    public void doExplosionA() {
        double explosionX = this.getPosition().xCoord;
        double explosionY = this.getPosition().yCoord;
        double explosionZ = this.getPosition().zCoord;
        ExplosionEvent event = new ExplosionEvent(this.world, this.entity, this.getPosition(), this.explosionSize, this.getExplosivePlacedBy(), 0, 1.0);
        if (MinecraftForge.EVENT_BUS.post((Event)event)) {
            return;
        }
        for (int x = Util.roundToNegInf((double)explosionX) - 1; x <= Util.roundToNegInf(explosionX) + 1; ++x) {
            for (int y = Util.roundToNegInf((double)explosionY) - 1; y <= Util.roundToNegInf(explosionY) + 1; ++y) {
                for (int z = Util.roundToNegInf((double)explosionZ) - 1; z <= Util.roundToNegInf(explosionZ) + 1; ++z) {
                    BlockPos pos = new BlockPos(x, y, z);
                    IBlockState block = this.world.getBlockState(pos);
                    if (block.getBlock().getExplosionResistance(this.world, pos, (Entity)this.getExplosivePlacedBy(), (Explosion)this) >= this.explosionSize * 10.0f) continue;
                    this.getAffectedBlockPositions().add(pos);
                }
            }
        }
        List entitiesInRange = this.world.getEntitiesWithinAABBExcludingEntity((Entity)this.getExplosivePlacedBy(), new AxisAlignedBB(explosionX - 2.0, explosionY - 2.0, explosionZ - 2.0, explosionX + 2.0, explosionY + 2.0, explosionZ + 2.0));
        for (Entity entity : entitiesInRange) {
            entity.attackEntityFrom(DamageSource.causeExplosionDamage((Explosion)this), (float)this.entityDamage);
        }
        this.explosionSize = 1.0f / this.dropRate;
    }
}

