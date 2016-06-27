package skam.apextech.item;

import net.minecraft.item.Item;
import skam.apextech.ApexTech;

/**
 * Created by Sword_Korn on 6/27/2016.
 */
public class ApexItemBase extends Item {
    public ApexItemBase(String regName) {
        super();
        this.setCreativeTab(ApexTech.apexTab);
    }
}
