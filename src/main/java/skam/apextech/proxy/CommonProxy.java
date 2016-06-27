package skam.apextech.proxy;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import skam.apextech.block.BlockRegistry;

/**
 * Created by Sword_Korn on 6/27/2016.
 */
public class CommonProxy {
    public void preInit(FMLPreInitializationEvent e) {
        BlockRegistry.loadBlockRegistry();
    }

    public void init(FMLInitializationEvent e) {

    }

    public void postInit(FMLPostInitializationEvent e) {

    }
}
