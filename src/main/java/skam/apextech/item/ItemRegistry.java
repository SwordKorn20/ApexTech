package skam.apextech.item;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;
import skam.apextech.ApexTech;

/**
 * Created by Sword_Korn on 6/27/2016.
 */
public class ItemRegistry {
    //Items

    /**
     * Place all items in here for Registry
     *
     * @args String of the registry name
     */
    public static void loadItemRegistry() {

    }

    /**
     * Call this method to dynamically register the item
     *
     * **NOTE**
     *
     * This will only register a BASIC item!
     *
     * DO NOT USE FOR ADVANCED BEHAVIOURS!!!
     *
     * @param regName
     * @return
     */
    public static Item regItem(String regName) {
        ApexItemBase item = new ApexItemBase(regName);

        return regItem(item, regName);
    }

    /**
     * **DO NOT USE!!!**
     *
     * Defines a shortcut method to dynamically register Items
     *
     * Only edit if ABSOLUTELY NECESSARY!!!
     *
     * @param item
     * @param regName
     * @return
     */
    public static Item regItem(Item item, String regName) {
        item.setRegistryName(regName);
        item.setCreativeTab(ApexTech.apexTab);

        return GameRegistry.register(item);
    }
}
