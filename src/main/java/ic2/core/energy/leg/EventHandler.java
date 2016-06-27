/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.World
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.EventBus
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent
 *  net.minecraftforge.fml.common.gameevent.TickEvent$Phase
 *  net.minecraftforge.fml.common.gameevent.TickEvent$WorldTickEvent
 */
package ic2.core.energy.leg;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyTile;
import ic2.core.IC2;
import ic2.core.Platform;
import ic2.core.energy.leg.EnergyNetLocalLeg;
import ic2.core.util.Log;
import ic2.core.util.LogCategory;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class EventHandler {
    public EventHandler() {
        MinecraftForge.EVENT_BUS.register((Object)this);
    }

    @SubscribeEvent
    public void onEnergyTileLoad(EnergyTileLoadEvent event) {
        if (!IC2.platform.isSimulating()) {
            IC2.log.warn(LogCategory.EnergyNet, "EnergyNet.addTileEntity: called for " + event.tile + " client-side, aborting");
            return;
        }
        EnergyNetLocalLeg enet = EnergyNetLocalLeg.getForWorld(event.getWorld());
        enet.addTileEntity(event.tile);
    }

    @SubscribeEvent
    public void onEnergyTileUnload(EnergyTileUnloadEvent event) {
        if (!IC2.platform.isSimulating()) {
            IC2.log.warn(LogCategory.EnergyNet, "EnergyNet.removeTileEntity: called for " + event.tile + " client-side, aborting");
            return;
        }
        EnergyNetLocalLeg enet = EnergyNetLocalLeg.getForWorld(event.getWorld());
        enet.removeTileEntity(event.tile);
    }

    @SubscribeEvent
    public void preTick(TickEvent.WorldTickEvent event) {
        EnergyNetLocalLeg enet = EnergyNetLocalLeg.getForWorld(event.world);
        if (event.phase == TickEvent.Phase.START) {
            enet.tickStart();
        } else {
            enet.tickEnd();
        }
    }
}

