/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.World
 */
package ic2.core.block.wiring;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.IEnergyNet;
import ic2.api.energy.NodeStats;
import ic2.api.energy.tile.IEnergyTile;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.comp.RedstoneEmitter;
import ic2.core.block.comp.TileEntityComponent;
import ic2.core.block.wiring.CableType;
import ic2.core.block.wiring.TileEntityCable;
import ic2.core.util.Util;
import net.minecraft.world.World;

public class TileEntityCableDetector
extends TileEntityCable {
    private static final int tickRate = 32;
    private final RedstoneEmitter rsEmitter;
    private int ticker = 0;

    public TileEntityCableDetector() {
        super(CableType.detector, 0);
        this.rsEmitter = this.addComponent(new RedstoneEmitter(this));
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        if (++this.ticker % 32 == 0) {
            double energy = EnergyNet.instance.getNodeStats(this).getEnergyIn();
            if (energy > 0.0) {
                this.setActive(true);
                this.rsEmitter.setLevel(15);
            } else {
                this.setActive(false);
                this.rsEmitter.setLevel(0);
            }
        }
    }

    @Override
    protected int getComparatorInputOverride() {
        if (!this.worldObj.isRemote) {
            return (int)Util.map(EnergyNet.instance.getNodeStats(this).getEnergyIn() / (this.getConductorBreakdownEnergy() - 1.0), 1.0, 15.0);
        }
        return 0;
    }
}

