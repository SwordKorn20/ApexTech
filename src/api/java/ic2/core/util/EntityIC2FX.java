/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.world.World
 */
package ic2.core.util;

import java.util.Random;
import net.minecraft.client.particle.Particle;
import net.minecraft.world.World;

public class EntityIC2FX
extends Particle {
    public EntityIC2FX(World world, double x, double y, double z, int maxAge, double[] velocity, float[] colour) {
        super(world, x, y, z, velocity[0], velocity[1], velocity[2]);
        this.particleRed = colour[0];
        this.particleGreen = colour[1];
        this.particleBlue = colour[2];
        this.particleGravity = 0.0f;
        this.particleAlpha = 0.6f;
        this.particleMaxAge = maxAge;
        this.setSize(0.02f, 0.02f);
        this.particleScale *= this.rand.nextFloat() * 0.6f + 0.5f;
    }

    public void onUpdate() {
        this.prevPosX = this.posX;
        this.prevPosY = this.posY;
        this.prevPosZ = this.posZ;
        this.moveEntity(0.0, 0.019999999552965164, 0.0);
        if (this.particleAge++ >= this.particleMaxAge) {
            this.setExpired();
        }
    }
}

