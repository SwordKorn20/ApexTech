/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.block.generator.container;

import ic2.core.ContainerFullInv;
import ic2.core.block.generator.tileentity.TileEntityStirlingGenerator;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerStirlingGenerator
extends ContainerFullInv<TileEntityStirlingGenerator> {
    public ContainerStirlingGenerator(EntityPlayer player, TileEntityStirlingGenerator tileEntity1) {
        super(player, tileEntity1, 166);
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("receivedheat");
        ret.add("production");
        return ret;
    }
}

