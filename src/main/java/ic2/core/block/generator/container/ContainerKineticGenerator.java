/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.block.generator.container;

import ic2.core.ContainerFullInv;
import ic2.core.block.generator.tileentity.TileEntityKineticGenerator;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerKineticGenerator
extends ContainerFullInv<TileEntityKineticGenerator> {
    public ContainerKineticGenerator(EntityPlayer player, TileEntityKineticGenerator tileEntity1) {
        super(player, tileEntity1, 166);
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("production");
        return ret;
    }
}

