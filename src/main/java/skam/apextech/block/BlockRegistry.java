package skam.apextech.block;

import net.minecraft.block.Block;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.fml.common.registry.GameRegistry;
import skam.apextech.block.storage.MSUCore;
import skam.apextech.block.storage.MSUCoreActive;

/**
 * Created by Sword_Korn on 6/27/2016.
 */
public class BlockRegistry {
    //Blocks
	
	public static Block MSUCore = new MSUCore("MSUCore", 2.5F, false);
	public static Block MSUCoreActive = new MSUCoreActive("MSUCoreActive", 2.5F, false);
	
    /**
     * Any basic block with no extended functionality can be registered using the registerBlock method.
     *
     * @args String of the registry name
     * @args Float for hardness (if TEBlock)
     * @args Boolean for render (if TEBlock)
     */
    public static void loadBlockRegistry() {
    	
    	
    	GameRegistry.register(MSUCore.setRegistryName("MSUCore"));
		GameRegistry.register(new BaseItemBlock(MSUCore).setRegistryName(MSUCore.getRegistryName()));
		
		GameRegistry.register(MSUCoreActive.setRegistryName("MSUCoreActive"));
		GameRegistry.register(new BaseItemBlock(MSUCoreActive).setRegistryName(MSUCoreActive.getRegistryName()));

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
     * Used to register a block with a bound TileEntity
     *
     * @param regName - Block name
     * @param hardness - As it says
     * @param isBlockModel - Boolean check. If true, block will render invisible. If false, will render as full block
     * @return
     */
    
    /*
    private static Block registerBlock(String regName, float hardness, boolean isBlockModel) {
        final Block TEBlock = new ApexTEBlock(regName, hardness, isBlockModel);
        final ItemBlock itemBlock = new ItemBlock(TEBlock);

        return registerBlock(regName, itemBlock, TEBlock);
    }
   */
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
