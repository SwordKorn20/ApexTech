/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.Event
 *  net.minecraftforge.fml.common.eventhandler.EventBus
 */
package ic2.core.block.wiring;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyTile;
import ic2.core.block.wiring.CableType;
import ic2.core.block.wiring.TileEntityCable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;

public class TileEntityCableSplitter
extends TileEntityCable {
    public static final int tickRate = 20;
    public int ticksUntilNextUpdate = 0;

    public TileEntityCableSplitter() {
        super(CableType.splitter, 0);
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        if (this.ticksUntilNextUpdate == 0) {
            this.ticksUntilNextUpdate = 20;
            if (this.worldObj.isBlockPowered(this.pos) == this.addedToEnergyNet) {
                if (this.addedToEnergyNet) {
                    MinecraftForge.EVENT_BUS.post((Event)new EnergyTileUnloadEvent(this));
                    this.addedToEnergyNet = false;
                } else {
                    MinecraftForge.EVENT_BUS.post((Event)new EnergyTileLoadEvent(this));
                    this.addedToEnergyNet = true;
                }
            }
            this.setActive(this.addedToEnergyNet);
        }
        --this.ticksUntilNextUpdate;
    }
}

