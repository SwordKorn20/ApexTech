/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.World
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.EventBus
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package ic2.core.energy;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyTile;
import ic2.core.IC2;
import ic2.core.WorldData;
import ic2.core.energy.EnergyNetLocal;
import ic2.core.util.Log;
import ic2.core.util.LogCategory;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EventHandler {
    public EventHandler() {
        MinecraftForge.EVENT_BUS.register((Object)this);
    }

    @SubscribeEvent
    public void onEnergyTileLoad(EnergyTileLoadEvent event) {
        if (event.getWorld().isRemote) {
            IC2.log.warn(LogCategory.EnergyNet, "EnergyTileLoadEvent: posted for %s client-side, aborting", event.tile);
            return;
        }
        WorldData.get((World)event.getWorld()).energyNet.addTile(event.tile);
    }

    @SubscribeEvent
    public void onEnergyTileUnload(EnergyTileUnloadEvent event) {
        if (event.getWorld().isRemote) {
            IC2.log.warn(LogCategory.EnergyNet, "EnergyTileUnloadEvent: posted for %s client-side, aborting", event.tile);
            return;
        }
        WorldData.get((World)event.getWorld()).energyNet.removeTile(event.tile);
    }
}

