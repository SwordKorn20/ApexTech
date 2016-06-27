/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSetMultimap
 *  net.minecraft.client.Minecraft
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.tileentity.TileEntityEnderChest
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.EventBus
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$ClientTickEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$Phase
 *  net.minecraftforge.fml.common.gameevent.TickEvent$ServerTickEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$WorldTickEvent
 */
package ic2.core;

import com.google.common.collect.ImmutableSetMultimap;
import ic2.core.IC2;
import ic2.core.IWorldTickCallback;
import ic2.core.Ic2WorldDecorator;
import ic2.core.Platform;
import ic2.core.WindSim;
import ic2.core.WorldData;
import ic2.core.audio.AudioManager;
import ic2.core.energy.EnergyNetGateway;
import ic2.core.init.MainConfig;
import ic2.core.network.NetworkManager;
import ic2.core.util.ConfigUtil;
import ic2.core.util.Keyboard;
import ic2.core.util.Log;
import ic2.core.util.LogCategory;
import ic2.core.util.ReflectionUtil;
import ic2.core.util.SideGateway;
import ic2.core.util.Util;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class TickHandler {
    private static final boolean debugupdate = System.getProperty("ic2.debugupdate") != null;
    private static final Map<IWorldTickCallback, Throwable> debugTraces = debugupdate ? new WeakHashMap() : null;
    private static Throwable lastDebugTrace;
    private static final Field updateEntityTick;

    public TickHandler() {
        MinecraftForge.EVENT_BUS.register((Object)this);
    }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        World world;
        block10 : {
            world = event.world;
            if (!world.isRemote) {
                try {
                    if (world instanceof WorldServer && world.playerEntities.isEmpty() && world.getPersistentChunks().isEmpty() && updateEntityTick.getInt((Object)world) >= 1200) {
                        return;
                    }
                    break block10;
                }
                catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            if (Minecraft.getMinecraft().isGamePaused()) {
                return;
            }
        }
        WorldData worldData = WorldData.get(world);
        if (event.phase == TickEvent.Phase.START) {
            IC2.platform.profilerStartSection("updates");
            TickHandler.processUpdates(world, worldData);
            if (!world.isRemote) {
                IC2.platform.profilerEndStartSection("retrogen");
                Ic2WorldDecorator.onTick(world, worldData);
                IC2.platform.profilerEndStartSection("Wind");
                worldData.windSim.updateWind();
                if (ConfigUtil.getBool(MainConfig.get(), "balance/disableEnderChest")) {
                    IC2.platform.profilerEndStartSection("EnderChestCheck");
                    for (int i = 0; i < world.tickableTileEntities.size(); ++i) {
                        TileEntity te = (TileEntity)world.tickableTileEntities.get(i);
                        if (!(te instanceof TileEntityEnderChest) || te.isInvalid() || world.isAirBlock(te.getPos())) continue;
                        world.setBlockToAir(te.getPos());
                        IC2.log.info(LogCategory.General, "Removed vanilla ender chest at %s.", Util.formatPosition(te));
                    }
                }
            }
            IC2.platform.profilerEndSection();
        } else {
            IC2.platform.profilerStartSection("EnergyNet");
            EnergyNetGateway.onTickEnd(world);
            IC2.platform.profilerEndStartSection("Networking");
            IC2.network.get(!world.isRemote).onTickEnd(worldData);
            IC2.platform.profilerEndSection();
        }
    }

    @SubscribeEvent
    public void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            ++ic2.core.item.tool.ItemNanoSaber.ticker;
        }
    }

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            IC2.platform.profilerStartSection("Keyboard");
            IC2.keyboard.sendKeyUpdate();
            IC2.platform.profilerEndStartSection("AudioManager");
            IC2.audioManager.onTick();
            IC2.platform.profilerEndStartSection("updates");
            World world = IC2.platform.getPlayerWorld();
            if (world != null) {
                TickHandler.processUpdates(world, WorldData.get(world));
            }
            IC2.platform.profilerEndSection();
        }
    }

    public void requestSingleWorldTick(World world, IWorldTickCallback callback) {
        WorldData.get((World)world).singleUpdates.add(callback);
        if (debugupdate) {
            debugTraces.put(callback, new Throwable());
        }
    }

    public void requestContinuousWorldTick(World world, IWorldTickCallback update) {
        WorldData worldData = WorldData.get(world);
        if (!worldData.continuousUpdatesInUse) {
            worldData.continuousUpdates.add(update);
        } else {
            worldData.continuousUpdatesToRemove.remove(update);
            worldData.continuousUpdatesToAdd.add(update);
        }
        if (debugupdate) {
            debugTraces.put(update, new Throwable());
        }
    }

    public void removeContinuousWorldTick(World world, IWorldTickCallback update) {
        WorldData worldData = WorldData.get(world);
        if (!worldData.continuousUpdatesInUse) {
            worldData.continuousUpdates.remove(update);
        } else {
            worldData.continuousUpdatesToAdd.remove(update);
            worldData.continuousUpdatesToRemove.add(update);
        }
    }

    public static Throwable getLastDebugTrace() {
        return lastDebugTrace;
    }

    private static void processUpdates(World world, WorldData worldData) {
        IWorldTickCallback callback;
        IC2.platform.profilerStartSection("single-update");
        while ((callback = worldData.singleUpdates.poll()) != null) {
            if (debugupdate) {
                lastDebugTrace = debugTraces.remove(callback);
            }
            callback.onTick(world);
        }
        IC2.platform.profilerEndStartSection("cont-update");
        worldData.continuousUpdatesInUse = true;
        for (IWorldTickCallback update : worldData.continuousUpdates) {
            if (debugupdate) {
                lastDebugTrace = debugTraces.remove(update);
            }
            update.onTick(world);
        }
        worldData.continuousUpdatesInUse = false;
        if (debugupdate) {
            lastDebugTrace = null;
        }
        worldData.continuousUpdates.addAll(worldData.continuousUpdatesToAdd);
        worldData.continuousUpdatesToAdd.clear();
        worldData.continuousUpdates.removeAll(worldData.continuousUpdatesToRemove);
        worldData.continuousUpdatesToRemove.clear();
        IC2.platform.profilerEndSection();
    }

    static {
        updateEntityTick = ReflectionUtil.getField(WorldServer.class, "field_80004_Q", "updateEntityTick");
    }
}

