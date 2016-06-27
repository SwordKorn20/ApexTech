/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.block.machine.container;

import ic2.core.block.machine.container.ContainerElectricMachine;
import ic2.core.block.machine.tileentity.TileEntityChunkloader;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerChunkLoader
extends ContainerElectricMachine<TileEntityChunkloader> {
    public ContainerChunkLoader(EntityPlayer player, TileEntityChunkloader base1) {
        super(player, base1, 250, 8, 143);
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("loadedChunks");
        return ret;
    }
}

