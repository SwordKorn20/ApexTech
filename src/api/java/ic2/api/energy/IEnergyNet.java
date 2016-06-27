/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package ic2.api.energy;

import ic2.api.energy.NodeStats;
import ic2.api.energy.tile.IEnergyTile;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public interface IEnergyNet {
    public IEnergyTile getTile(World var1, BlockPos var2);

    public IEnergyTile getSubTile(World var1, BlockPos var2);

    public World getWorld(IEnergyTile var1);

    public BlockPos getPos(IEnergyTile var1);

    public NodeStats getNodeStats(IEnergyTile var1);

    public double getPowerFromTier(int var1);

    public int getTierFromPower(double var1);
}

