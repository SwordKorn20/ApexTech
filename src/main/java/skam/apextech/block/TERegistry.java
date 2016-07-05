package skam.apextech.block;

import cofh.api.energy.EnergyStorage;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;
import skam.apextech.block.storage.ApexTE;

/**
 * Created by Sword_Korn on 6/27/2016.
 */
public class TERegistry {
    //TileEntities

    public void loadTERegistry() {

    }

    /**
     * This is as fancy as I could get the registry process to be.
     *
     * @param regName - ID of the TileEntity, or the String name
     * @param te - The class the TileEntity inherits from
     * @return
     */
    private static TileEntity regTE(String regName, Class te, EnergyStorage storage) {
        TileEntity tile = new ApexTE(storage);
        GameRegistry.registerTileEntity(te, regName);
        return tile;
    }
}
