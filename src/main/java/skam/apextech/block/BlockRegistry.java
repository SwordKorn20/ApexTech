package skam.apextech.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created by Sword_Korn on 6/27/2016.
 */
public class BlockRegistry {
    //Blocks

    /**
     * Any basic block with no extended functionality can be regsitered using the registerBlock method.
     *
     * @args String of the registry name
     */
    public static void loadBlockRegistry() {

    }

    /**
     * Calls upon the internal method to dynamically register basic blocks.
     * **DO NOT USE FOR ADVANCED BLOCKS WITH ADDITIONAL BEHAVIOURS!!!**
     *
     * @param regName
     * @return
     */
    private static Block registerBlock(String regName) {
        final Block ATBlock = new ApexBlockBase(regName);
        final ItemBlock itemBlock = new ItemBlock(ATBlock);

        return registerBlock(regName, itemBlock, ATBlock);
    }

    /**
     * **DO NOT USE!!!**
     *
     * Behaves as a registry "factory", effectively applying all the necessary methods to register any basic block to
     * Minecraft.
     *
     * Call the shortened version above. Only edit if ABSOLUTELY NECESSARY!!!
     *
     * @param regName
     * @param itemBlock
     * @param block
     * @return
     */
    private static Block registerBlock(String regName, ItemBlock itemBlock, Block block) {
        block.setRegistryName(regName);
        block.setUnlocalizedName(regName);

        GameRegistry.register(block);

        itemBlock.setRegistryName(regName);
        itemBlock.setUnlocalizedName(regName);
        GameRegistry.register(itemBlock);

        return block;
    }
}
