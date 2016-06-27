/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package ic2.core.energy.leg;

import ic2.api.energy.IEnergyNet;
import ic2.api.energy.NodeStats;
import ic2.api.energy.tile.IEnergyTile;
import ic2.core.energy.leg.EnergyNetLocalLeg;
import ic2.core.energy.leg.EnergyPath;
import ic2.core.energy.leg.EventHandler;
import ic2.core.energy.leg.Tile;
import ic2.core.util.Tuple;
import java.util.Map;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnergyNetGlobalLeg
implements IEnergyNet {
    public static EnergyNetGlobalLeg initialize() {
        new EventHandler();
        return new EnergyNetGlobalLeg();
    }

    private EnergyNetGlobalLeg() {
    }

    @Override
    public IEnergyTile getTile(World world, BlockPos pos) {
        Tile tile = EnergyNetLocalLeg.getForWorld((World)world).registeredTiles.get((Object)pos);
        if (tile != null) {
            return tile.entity;
        }
        return null;
    }

    @Override
    public IEnergyTile getSubTile(World world, BlockPos pos) {
        Tile tile = EnergyNetLocalLeg.getForWorld((World)world).registeredTiles.get((Object)pos);
        if (tile != null) {
            return tile.subTile;
        }
        return null;
    }

    @Override
    public World getWorld(IEnergyTile tile) {
        return ((TileEntity)tile).getWorld();
    }

    @Override
    public BlockPos getPos(IEnergyTile tile) {
        return ((TileEntity)tile).getPos();
    }

    @Override
    public NodeStats getNodeStats(IEnergyTile energyTile) {
        EnergyNetLocalLeg enet = EnergyNetLocalLeg.getForWorld(this.getWorld(energyTile));
        if (enet == null) {
            return new NodeStats(0.0, 0.0, 0.0);
        }
        EnergyNetLocalLeg energyNetLocalLeg = enet;
        synchronized (energyNetLocalLeg) {
            Tile tile = enet.registeredTiles.get((Object)this.getPos(energyTile));
            assert (tile.subTile == energyTile);
            Tuple.T2<Iterable<EnergyPath>, Iterable<EnergyPath>> paths = enet.getEnergyPathsContaining(tile);
            long in = 0;
            int max = 0;
            for (EnergyPath path : (Iterable)paths.a) {
                in += path.energyConducted;
                max = Math.max(path.maxPacketConducted, max);
            }
            long out = 0;
            for (EnergyPath path2 : (Iterable)paths.b) {
                out += path2.energyConducted;
                max = Math.max(path2.maxPacketConducted, max);
            }
            return new NodeStats(in, out, this.getTierFromPower(max));
        }
    }

    @Override
    public double getPowerFromTier(int tier) {
        if (tier == Integer.MAX_VALUE) {
            return tier;
        }
        return 8 << tier * 2;
    }

    @Override
    public int getTierFromPower(double power) {
        if (power <= 0.0) {
            return 0;
        }
        return (int)Math.ceil(Math.log(power / 8.0) / Math.log(4.0));
    }
}

