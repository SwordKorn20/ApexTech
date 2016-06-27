/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package ic2.core.block.beam;

import ic2.core.block.beam.EntityParticle;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class TileEmitter
extends TileEntityElectricMachine {
    private int progress;

    public TileEmitter() {
        super(5000, 1);
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        if (this.progress < 100) {
            ++this.progress;
        }
        if (this.progress == 100 && this.worldObj.isBlockPowered(this.pos)) {
            this.progress = 0;
            this.worldObj.spawnEntityInWorld((Entity)new EntityParticle(this));
        }
    }
}

