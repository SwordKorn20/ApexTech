package skam.apextech.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import skam.apextech.ApexTech;

/**
 * Created by Sword_Korn on 6/27/2016.
 */
public class ApexBlockBase extends Block {
    public ApexBlockBase(String regName) {
        super(Material.ROCK);
        this.setCreativeTab(ApexTech.apexTab);
    }
}
