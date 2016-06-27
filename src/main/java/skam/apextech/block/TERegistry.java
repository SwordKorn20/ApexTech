package skam.apextech.block;

import cofh.api.energy.EnergyStorage;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.fml.common.registry.GameRegistry;
import skam.apextech.block.storage.TileMSUCore;

/**
 * Created by Sword_Korn on 6/27/2016.
 */
public class TERegistry {
	
    public void loadTERegistry() {
    	
    	GameRegistry.registerTileEntity(TileMSUCore.class, "TileMSUCore");
    	
    }

}
