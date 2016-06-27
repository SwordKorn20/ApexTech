/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 */
package ic2.core.block.generator.container;

import ic2.core.block.generator.container.ContainerBaseGenerator;
import ic2.core.block.generator.tileentity.TileEntitySolarGenerator;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;

public class ContainerSolarGenerator
extends ContainerBaseGenerator<TileEntitySolarGenerator> {
    public ContainerSolarGenerator(EntityPlayer player, TileEntitySolarGenerator tileEntity1) {
        super(player, tileEntity1, 166, 80, 26);
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("skyLight");
        return ret;
    }
}

