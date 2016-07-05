package skam.apextech.block;

import cofh.api.energy.EnergyStorage;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;
import skam.apextech.block.storage.ApexTE;
import skam.apextech.block.storage.TileMSUCore;

/**
 * Created by Sword_Korn on 6/27/2016.
 */
public class TERegistry {
    //TileEntities

    public void loadTERegistry() {
    	
    	regTE("ApexTE", ApexTE.class);
    	regTE("MSUCore", TileMSUCore.class);
    	
    }

    /**
     * This is as fancy as I could get the registry process to be.
     *
     * @param regName - ID of the TileEntity, or the String name
     * @param te - The class the TileEntity inherits from
     * @return
     */
    private static TileEntity regTE(String regName, Class te) {
        TileEntity tile = new ApexTE();
        GameRegistry.registerTileEntity(te, regName);
        return tile;
    }
}
