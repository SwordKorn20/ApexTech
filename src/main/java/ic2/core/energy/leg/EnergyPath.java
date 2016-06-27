/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.EnumFacing
 */
package ic2.core.energy.leg;

import ic2.core.energy.leg.Tile;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.util.EnumFacing;

class EnergyPath {
    Tile target = null;
    EnumFacing targetDirection;
    Set<Tile> conductors = new HashSet<Tile>();
    int minX = Integer.MAX_VALUE;
    int minY = Integer.MAX_VALUE;
    int minZ = Integer.MAX_VALUE;
    int maxX = Integer.MIN_VALUE;
    int maxY = Integer.MIN_VALUE;
    int maxZ = Integer.MIN_VALUE;
    double loss = 0.0;
    int minInsulationEnergyAbsorption = Integer.MAX_VALUE;
    int minInsulationBreakdownEnergy = Integer.MAX_VALUE;
    int minConductorBreakdownEnergy = Integer.MAX_VALUE;
    long energyConducted = 0;
    int maxPacketConducted = 0;

    EnergyPath() {
    }
}

