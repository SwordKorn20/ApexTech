/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  mezz.jei.api.IGuiHelper
 *  mezz.jei.api.IJeiHelpers
 *  mezz.jei.api.IJeiRuntime
 *  mezz.jei.api.IModPlugin
 *  mezz.jei.api.IModRegistry
 *  mezz.jei.api.JEIPlugin
 *  mezz.jei.api.recipe.IRecipeCategory
 *  mezz.jei.api.recipe.IRecipeHandler
 *  mezz.jei.api.recipe.IRecipeWrapper
 *  net.minecraft.item.ItemStack
 */
package ic2.jeiIntegration;

import ic2.api.recipe.IMachineRecipeManager;
import ic2.api.recipe.Recipes;
import ic2.core.block.state.IIdProvider;
import ic2.core.ref.BlockName;
import ic2.core.ref.TeBlock;
import ic2.jeiIntegration.recipe.crafting.AdvRecipeHandler;
import ic2.jeiIntegration.recipe.crafting.AdvShapelessRecipeHandler;
import ic2.jeiIntegration.recipe.machine.DynamicCategory;
import ic2.jeiIntegration.recipe.machine.IORecipeCategory;
import ic2.jeiIntegration.recipe.machine.IORecipeHandler;
import ic2.jeiIntegration.recipe.machine.IRecipeWrapperGenerator;
import ic2.jeiIntegration.recipe.misc.ScrapboxRecipeCategory;
import ic2.jeiIntegration.recipe.misc.ScrapboxRecipeHandler;
import ic2.jeiIntegration.recipe.misc.ScrapboxRecipeWrapper;
import java.util.List;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.IJeiHelpers;
import mezz.jei.api.IJeiRuntime;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.IModRegistry;
import mezz.jei.api.JEIPlugin;
import mezz.jei.api.recipe.IRecipeCategory;
import mezz.jei.api.recipe.IRecipeHandler;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.item.ItemStack;

@JEIPlugin
public class SubModule
implements IModPlugin {
    public void register(IModRegistry registry) {
        registry.addRecipeHandlers(new IRecipeHandler[]{new AdvRecipeHandler()});
        registry.addRecipeHandlers(new IRecipeHandler[]{new AdvShapelessRecipeHandler()});
        IGuiHelper guiHelper = registry.getJeiHelpers().getGuiHelper();
        registry.addRecipeCategories(new IRecipeCategory[]{new ScrapboxRecipeCategory(guiHelper)});
        registry.addRecipeHandlers(new IRecipeHandler[]{new ScrapboxRecipeHandler()});
        registry.addRecipes(ScrapboxRecipeWrapper.createRecipes());
        registry.addRecipeHandlers(new IRecipeHandler[]{new IORecipeHandler()});
        this.addMachineRecipes(registry, new DynamicCategory<IMachineRecipeManager>(TeBlock.macerator, Recipes.macerator, guiHelper), IRecipeWrapperGenerator.basicMachine);
        this.addMachineRecipes(registry, new DynamicCategory<IMachineRecipeManager>(TeBlock.extractor, Recipes.extractor, guiHelper), IRecipeWrapperGenerator.basicMachine);
        this.addMachineRecipes(registry, new DynamicCategory<IMachineRecipeManager>(TeBlock.compressor, Recipes.compressor, guiHelper), IRecipeWrapperGenerator.basicMachine);
        this.addMachineRecipes(registry, new DynamicCategory<IMachineRecipeManager>(TeBlock.centrifuge, Recipes.centrifuge, guiHelper), IRecipeWrapperGenerator.basicMachine);
        this.addMachineRecipes(registry, new DynamicCategory<IMachineRecipeManager>(TeBlock.blast_furnace, Recipes.blastfurnace, guiHelper), IRecipeWrapperGenerator.basicMachine);
        this.addMachineRecipes(registry, new DynamicCategory<IMachineRecipeManager>(TeBlock.ore_washing_plant, Recipes.oreWashing, guiHelper), IRecipeWrapperGenerator.basicMachine);
        this.addMachineRecipes(registry, new DynamicCategory<IMachineRecipeManager>(TeBlock.recycler, Recipes.recycler, guiHelper), IRecipeWrapperGenerator.recycler);
    }

    private <T> void addMachineRecipes(IModRegistry registry, IORecipeCategory<T> category, IRecipeWrapperGenerator<T> wrappergen) {
        registry.addRecipeCategories(new IRecipeCategory[]{category});
        registry.addRecipes(wrappergen.getRecipeList(category));
        registry.addRecipeCategoryCraftingItem(BlockName.te.getItemStack(category.getBlock()), new String[]{category.getUid()});
    }

    public void onRuntimeAvailable(IJeiRuntime jeiRuntime) {
    }
}

