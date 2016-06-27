/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package ic2.core.energy;

import ic2.api.energy.IEnergyNet;
import ic2.api.energy.NodeStats;
import ic2.api.energy.tile.IEnergyTile;
import ic2.core.IC2;
import ic2.core.Platform;
import ic2.core.WorldData;
import ic2.core.energy.EnergyNetLocal;
import ic2.core.energy.EventHandler;
import ic2.core.energy.Tile;
import ic2.core.util.Util;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EnergyNetGlobal
implements IEnergyNet {
    public static final boolean replaceConflicting = System.getProperty("ic2.energynet.replaceconflicting") != null;
    public static final boolean debugTileManagement = System.getProperty("ic2.energynet.debugtilemanagement") != null;
    public static final boolean debugGrid = System.getProperty("ic2.energynet.debuggrid") != null;
    public static final boolean debugGridVerbose = debugGrid && System.getProperty("ic2.energynet.debuggrid").equals("verbose");
    public static final boolean checkApi = System.getProperty("ic2.energynet.checkapi") != null;
    public static final boolean logAll = System.getProperty("ic2.energynet.logall") != null;

    public static EnergyNetGlobal initialize() {
        return new EnergyNetGlobal();
    }

    private EnergyNetGlobal() {
        new EventHandler();
    }

    @Override
    public IEnergyTile getTile(World world, BlockPos pos) {
        Tile tile = WorldData.get((World)world).energyNet.getTile(pos);
        return tile != null ? tile.mainTile : null;
    }

    @Override
    public IEnergyTile getSubTile(World world, BlockPos pos) {
        Tile tile = WorldData.get((World)world).energyNet.getTile(pos);
        return tile != null ? tile.getSubTileAt(pos) : null;
    }

    @Override
    public World getWorld(IEnergyTile tile) {
        if (tile instanceof TileEntity) {
            return ((TileEntity)tile).getWorld();
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public BlockPos getPos(IEnergyTile tile) {
        if (tile instanceof TileEntity) {
            return ((TileEntity)tile).getPos();
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public NodeStats getNodeStats(IEnergyTile tile) {
        return WorldData.get((World)this.getWorld((IEnergyTile)tile)).energyNet.getNodeStats(tile);
    }

    @Override
    public double getPowerFromTier(int tier) {
        if (tier < 14) {
            return 8 << tier * 2;
        }
        return 8.0 * Math.pow(4.0, tier);
    }

    @Override
    public int getTierFromPower(double power) {
        if (power <= 0.0) {
            return 0;
        }
        return (int)Math.ceil(Math.log(power / 8.0) / Math.log(4.0));
    }

    public static void onTickEnd(World world) {
        if (!IC2.platform.isSimulating()) {
            return;
        }
        WorldData.get((World)world).energyNet.onTickEnd();
    }

    protected static boolean verifyGrid() {
        return Util.hasAssertions();
    }
}

