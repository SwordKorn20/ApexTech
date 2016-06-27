/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 */
package ic2.core.block.machine.tileentity;

import ic2.core.IC2;
import ic2.core.IC2DamageSource;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.comp.Energy;
import ic2.core.block.comp.Redstone;
import ic2.core.block.comp.TileEntityComponent;
import ic2.core.item.armor.ItemArmorHazmat;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class TileEntityTesla
extends TileEntityBlock {
    protected final Redstone redstone;
    protected final Energy energy;
    private int ticker = IC2.random.nextInt(32);

    public TileEntityTesla() {
        this.redstone = this.addComponent(new Redstone(this));
        this.energy = this.addComponent(Energy.asBasicSink(this, 10000.0, 2));
    }

    @Override
    protected void updateEntityServer() {
        int damage;
        super.updateEntityServer();
        if (!this.redstone.hasRedstoneInput()) {
            return;
        }
        if (this.energy.useEnergy(1.0) && ++this.ticker % 32 == 0 && (damage = (int)this.energy.getEnergy() / 400) > 0 && this.shock(damage)) {
            this.energy.useEnergy(damage * 400);
        }
    }

    protected boolean shock(int damage) {
        int r = 4;
        List entities = this.worldObj.getEntitiesWithinAABB((Class)EntityLivingBase.class, new AxisAlignedBB((double)(this.pos.getX() - 4), (double)(this.pos.getY() - 4), (double)(this.pos.getZ() - 4), (double)(this.pos.getX() + 4 + 1), (double)(this.pos.getY() + 4 + 1), (double)(this.pos.getZ() + 4 + 1)));
        for (EntityLivingBase entity : entities) {
            if (ItemArmorHazmat.hasCompleteHazmat(entity) || !entity.attackEntityFrom((DamageSource)IC2DamageSource.electricity, (float)damage)) continue;
            if (this.worldObj instanceof WorldServer) {
                WorldServer world = (WorldServer)this.worldObj;
                Random rnd = world.rand;
                for (int i = 0; i < damage; ++i) {
                    world.spawnParticle(EnumParticleTypes.REDSTONE, true, entity.posX + (double)rnd.nextFloat() - 0.5, entity.posY + (double)(rnd.nextFloat() * 2.0f) - 1.0, entity.posZ + (double)rnd.nextFloat() - 0.5, 0, 0.1, 0.1, 1.0, 1.0, new int[0]);
                }
            }
            return true;
        }
        return false;
    }
}

