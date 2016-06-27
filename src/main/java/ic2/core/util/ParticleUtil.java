/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.multiplayer.WorldClient
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.ParticleBlockDust
 *  net.minecraft.client.particle.ParticleDigging
 *  net.minecraft.client.particle.ParticleManager
 *  net.minecraft.client.settings.GameSettings
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package ic2.core.util;

import ic2.core.block.TileEntityBlock;
import ic2.core.block.state.IIdProvider;
import ic2.core.ref.BlockName;
import ic2.core.ref.TeBlock;
import java.lang.reflect.Constructor;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleBlockDust;
import net.minecraft.client.particle.ParticleDigging;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ParticleUtil {
    private static final Constructor<ParticleBlockDust> particleBlockDust_ctor = ParticleUtil.getParticleBlockDustCtor();
    private static final Constructor<ParticleDigging> particleDigging_ctor = ParticleUtil.getParticleDiggingCtor();

    public static void spawnBlockLandParticles(World world, double x, double y, double z, int count, TeBlock teBlock) {
        Minecraft mc = Minecraft.getMinecraft();
        Random rnd = world.rand;
        if (mc.theWorld != world || mc.thePlayer == null) {
            return;
        }
        if (mc.gameSettings.particleSetting > 1 || mc.gameSettings.particleSetting == 1 && rnd.nextInt(3) == 0) {
            return;
        }
        if (mc.thePlayer.getDistanceSq(x, y, z) > 1024.0) {
            return;
        }
        double speed = 0.15;
        for (int i = 0; i < count; ++i) {
            ParticleDigging particle;
            double mx = rnd.nextGaussian() * 0.15;
            double my = rnd.nextGaussian() * 0.15;
            double mz = rnd.nextGaussian() * 0.15;
            try {
                particle = (ParticleDigging)particleBlockDust_ctor.newInstance(new Object[]{world, x, y, z, mx, my, mz, BlockName.te.getBlockState(teBlock)});
            }
            catch (Exception e) {
                throw new RuntimeException(e);
            }
            particle.init();
            mc.effectRenderer.addEffect((Particle)particle);
        }
    }

    public static void spawnBlockHitParticles(TileEntityBlock te, EnumFacing side) {
        ParticleDigging particle;
        World world = te.getWorld();
        BlockPos pos = te.getPos();
        double offset = 0.1;
        AxisAlignedBB aabb = te.getVisualBoundingBox();
        double x = (double)pos.getX() + world.rand.nextDouble() * (aabb.maxX - aabb.minX - offset * 2.0) + offset + aabb.minX;
        double y = (double)pos.getY() + world.rand.nextDouble() * (aabb.maxY - aabb.minY - offset * 2.0) + offset + aabb.minY;
        double z = (double)pos.getZ() + world.rand.nextDouble() * (aabb.maxZ - aabb.minZ - offset * 2.0) + offset + aabb.minZ;
        switch (side) {
            case DOWN: {
                y = (double)pos.getY() + aabb.minY - offset;
                break;
            }
            case UP: {
                y = (double)pos.getY() + aabb.maxY + offset;
                break;
            }
            case NORTH: {
                z = (double)pos.getZ() + aabb.minZ - offset;
                break;
            }
            case SOUTH: {
                z = (double)pos.getZ() + aabb.maxZ + offset;
                break;
            }
            case WEST: {
                x = (double)pos.getX() + aabb.minX - offset;
                break;
            }
            case EAST: {
                x = (double)pos.getX() + aabb.maxX + offset;
                break;
            }
            default: {
                throw new IllegalStateException("invalid facing: " + (Object)side);
            }
        }
        try {
            particle = particleDigging_ctor.newInstance(new Object[]{world, x, y, z, 0, 0, 0, te.getBlockState()});
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        particle.setBlockPos(pos);
        particle.multiplyVelocity(0.2f);
        particle.multipleParticleScaleBy(0.6f);
        Minecraft.getMinecraft().effectRenderer.addEffect((Particle)particle);
    }

    private static Constructor<ParticleBlockDust> getParticleBlockDustCtor() {
        try {
            Constructor<ParticleBlockDust> ret = ParticleBlockDust.class.getDeclaredConstructor(World.class, Double.TYPE, Double.TYPE, Double.TYPE, Double.TYPE, Double.TYPE, Double.TYPE, IBlockState.class);
            ret.setAccessible(true);
            return ret;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Constructor<ParticleDigging> getParticleDiggingCtor() {
        try {
            Constructor<ParticleDigging> ret = ParticleDigging.class.getDeclaredConstructor(World.class, Double.TYPE, Double.TYPE, Double.TYPE, Double.TYPE, Double.TYPE, Double.TYPE, IBlockState.class);
            ret.setAccessible(true);
            return ret;
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

