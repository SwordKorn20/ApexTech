/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 */
package ic2.core.energy.leg;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.IEnergyNet;
import ic2.api.energy.tile.IEnergyTile;
import ic2.core.energy.leg.EnergyNetLocalLeg;
import java.util.Map;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

class Tile {
    final IEnergyTile entity;
    final IEnergyTile subTile;
    final Tile[] neighbors = new Tile[6];

    Tile(EnergyNetLocalLeg enet, IEnergyTile te, IEnergyTile subTile) {
        this.entity = te;
        this.subTile = subTile;
        BlockPos pos = EnergyNet.instance.getPos(subTile);
        for (EnumFacing dir : EnumFacing.VALUES) {
            BlockPos coords = pos.offset(dir);
            int index = dir.ordinal();
            this.neighbors[index] = enet.registeredTiles.get((Object)coords);
            if (this.neighbors[index] == null) continue;
            this.neighbors[index].neighbors[dir.getOpposite().ordinal()] = this;
        }
    }

    void destroy() {
        for (EnumFacing dir : EnumFacing.VALUES) {
            Tile neighbor = this.neighbors[dir.ordinal()];
            if (neighbor == null) continue;
            neighbor.neighbors[dir.getOpposite().ordinal()] = null;
        }
    }

    public String toString() {
        return super.toString() + "{" + this.entity + "," + this.subTile + "}";
    }

    int getAmountNeighbors() {
        int n = 0;
        for (Tile t : this.neighbors) {
            if (t == null) continue;
            ++n;
        }
        return n;
    }
}

