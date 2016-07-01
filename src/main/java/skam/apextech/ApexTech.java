package skam.apextech;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import skam.apextech.proxy.CommonProxy;

/**
 * Created by Sword_Korn on 6/27/2016.
 */

@Mod(modid = ApexTech.MODID, name = ApexTech.NAME, version = ApexTech.VERSION)
public class ApexTech {
    public static final String MODID = "apextech";
    public static final String NAME = MODID;
    public static final String VERSION = "0.0.1";

    public static CreativeTabs apexTab = new CreativeTabs("apexTab") {
        @Override
        public Item getTabIconItem() {
            return Items.DIAMOND;
        }
    };

    @SidedProxy(clientSide = "skam.apextech.proxy.ClientProxy", serverSide = "skam.apextech.proxy.ServerProxy")
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent e) {
        this.proxy.preInit(e);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent e) {
        this.proxy.init(e);
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent e) {
        this.proxy.postInit(e);
    }
}
