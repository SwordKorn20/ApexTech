package skam.apextech.proxy;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import skam.apextech.block.TileEntityRegistry;

/**
 * Created by Sword_Korn on 6/27/2016.
 */
public class CommonProxy {
    public void preInit(FMLPreInitializationEvent e) {

    }

    public void init(FMLInitializationEvent e) {
        TileEntityRegistry.loadTileEntityRegistry();
    }

    public void postInit(FMLPostInitializationEvent e) {

    }
}
