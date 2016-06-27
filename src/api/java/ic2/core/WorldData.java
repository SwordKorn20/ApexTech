/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldProvider
 *  net.minecraft.world.chunk.Chunk
 *  net.minecraftforge.fml.common.FMLCommonHandler
 *  net.minecraftforge.fml.relauncher.Side
 */
package ic2.core;

import ic2.core.IWorldTickCallback;
import ic2.core.Ic2Player;
import ic2.core.WindSim;
import ic2.core.energy.EnergyNetGateway;
import ic2.core.energy.EnergyNetLocal;
import ic2.core.energy.leg.EnergyNetLocalLeg;
import ic2.core.network.TeUpdateDataServer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class WorldData {
    private static ConcurrentMap<Integer, WorldData> mappingClient = FMLCommonHandler.instance().getSide().isClient() ? new ConcurrentHashMap() : null;
    private static ConcurrentMap<Integer, WorldData> mappingServer = new ConcurrentHashMap<Integer, WorldData>();
    final Queue<IWorldTickCallback> singleUpdates = new ConcurrentLinkedQueue<IWorldTickCallback>();
    final Set<IWorldTickCallback> continuousUpdates = new HashSet<IWorldTickCallback>();
    boolean continuousUpdatesInUse = false;
    final List<IWorldTickCallback> continuousUpdatesToAdd = new ArrayList<IWorldTickCallback>();
    final List<IWorldTickCallback> continuousUpdatesToRemove = new ArrayList<IWorldTickCallback>();
    public Ic2Player fakePlayer;
    public EnergyNetLocal energyNet;
    public EnergyNetLocalLeg energyNetLeg;
    public final Map<TileEntity, TeUpdateDataServer> tesToUpdate = new IdentityHashMap<TileEntity, TeUpdateDataServer>();
    public WindSim windSim;
    public final Map<Chunk, NBTTagCompound> worldGenData = new IdentityHashMap<Chunk, NBTTagCompound>();
    public final Set<Chunk> chunksToDecorate = Collections.newSetFromMap(new IdentityHashMap());
    private boolean initialized;

    public static WorldData get(World world) {
        return WorldData.get(world, true);
    }

    public static WorldData get(World world, boolean load) {
        if (world == null) {
            throw new IllegalArgumentException("world is null");
        }
        WorldData ret = WorldData.get(world.provider.getDimension(), !world.isRemote, load);
        ret.initialize(world);
        return ret;
    }

    public static WorldData get(int dimensionId, boolean simulating) {
        return WorldData.get(dimensionId, simulating, true);
    }

    public static WorldData get(int dimensionId, boolean simulating, boolean load) {
        ConcurrentMap<Integer, WorldData> mapping = simulating ? mappingServer : mappingClient;
        WorldData ret = mapping.get(dimensionId);
        if (ret == null) {
            if (!load) {
                return null;
            }
            ret = new WorldData();
            WorldData prev = mapping.putIfAbsent(dimensionId, ret);
            if (prev != null) {
                ret = prev;
            }
        }
        return ret;
    }

    public static void onWorldUnload(World world) {
        ConcurrentMap<Integer, WorldData> mapping = world.isRemote ? mappingClient : mappingServer;
        mapping.remove(world.provider.getDimension());
    }

    private WorldData() {
    }

    private void initialize(World world) {
        if (!this.initialized) {
            this.initialized = true;
            this.fakePlayer = new Ic2Player(world);
            if (!world.isRemote) {
                EnergyNetGateway.initWorldData(this, world);
                this.windSim = new WindSim(world);
            }
        }
    }
}

