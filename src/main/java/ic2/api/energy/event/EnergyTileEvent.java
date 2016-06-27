/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.World
 *  net.minecraftforge.event.world.WorldEvent
 */
package ic2.api.energy.event;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.IEnergyNet;
import ic2.api.energy.tile.IEnergyTile;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;

public class EnergyTileEvent
extends WorldEvent {
    public final IEnergyTile tile;

    public EnergyTileEvent(IEnergyTile tile) {
        super(EnergyNet.instance.getWorld(tile));
        if (this.getWorld() == null) {
            throw new NullPointerException("world is null");
        }
        this.tile = tile;
    }
}

