/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.crafting.FurnaceRecipes
 */
package ic2.core.recipe;

import ic2.core.block.state.IIdProvider;
import ic2.core.block.type.ResourceBlock;
import ic2.core.item.type.CraftingItemType;
import ic2.core.item.type.DustResourceType;
import ic2.core.item.type.IngotResourceType;
import ic2.core.item.type.MiscResourceType;
import ic2.core.item.type.OreResourceType;
import ic2.core.ref.BlockName;
import ic2.core.ref.ItemName;
import ic2.core.util.StackUtil;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;

public class SmeltingRecipes {
    public static void load() {
        SmeltingRecipes.add(BlockName.resource.getItemStack(ResourceBlock.copper_ore), ItemName.ingot.getItemStack(IngotResourceType.copper), 0.5f);
        SmeltingRecipes.add(BlockName.resource.getItemStack(ResourceBlock.lead_ore), ItemName.ingot.getItemStack(IngotResourceType.lead), 0.5f);
        SmeltingRecipes.add(BlockName.resource.getItemStack(ResourceBlock.tin_ore), ItemName.ingot.getItemStack(IngotResourceType.tin), 0.5f);
        SmeltingRecipes.add(ItemName.crushed.getItemStack(OreResourceType.iron), new ItemStack(Items.IRON_INGOT));
        SmeltingRecipes.add(ItemName.crushed.getItemStack(OreResourceType.gold), new ItemStack(Items.GOLD_INGOT));
        SmeltingRecipes.add(ItemName.crushed.getItemStack(OreResourceType.copper), ItemName.ingot.getItemStack(IngotResourceType.copper));
        SmeltingRecipes.add(ItemName.crushed.getItemStack(OreResourceType.lead), ItemName.ingot.getItemStack(IngotResourceType.lead));
        SmeltingRecipes.add(ItemName.crushed.getItemStack(OreResourceType.silver), ItemName.ingot.getItemStack(IngotResourceType.silver));
        SmeltingRecipes.add(ItemName.crushed.getItemStack(OreResourceType.tin), ItemName.ingot.getItemStack(IngotResourceType.tin));
        SmeltingRecipes.add(ItemName.purified.getItemStack(OreResourceType.iron), new ItemStack(Items.IRON_INGOT));
        SmeltingRecipes.add(ItemName.purified.getItemStack(OreResourceType.gold), new ItemStack(Items.GOLD_INGOT));
        SmeltingRecipes.add(ItemName.purified.getItemStack(OreResourceType.copper), ItemName.ingot.getItemStack(IngotResourceType.copper));
        SmeltingRecipes.add(ItemName.purified.getItemStack(OreResourceType.lead), ItemName.ingot.getItemStack(IngotResourceType.lead));
        SmeltingRecipes.add(ItemName.purified.getItemStack(OreResourceType.silver), ItemName.ingot.getItemStack(IngotResourceType.silver));
        SmeltingRecipes.add(ItemName.purified.getItemStack(OreResourceType.tin), ItemName.ingot.getItemStack(IngotResourceType.tin));
        SmeltingRecipes.add(ItemName.dust.getItemStack(DustResourceType.iron), new ItemStack(Items.IRON_INGOT));
        SmeltingRecipes.add(ItemName.dust.getItemStack(DustResourceType.gold), new ItemStack(Items.GOLD_INGOT));
        SmeltingRecipes.add(ItemName.dust.getItemStack(DustResourceType.copper), ItemName.ingot.getItemStack(IngotResourceType.copper));
        SmeltingRecipes.add(ItemName.dust.getItemStack(DustResourceType.lead), ItemName.ingot.getItemStack(IngotResourceType.lead));
        SmeltingRecipes.add(ItemName.dust.getItemStack(DustResourceType.silver), ItemName.ingot.getItemStack(IngotResourceType.silver));
        SmeltingRecipes.add(ItemName.dust.getItemStack(DustResourceType.tin), ItemName.ingot.getItemStack(IngotResourceType.tin));
        SmeltingRecipes.add(ItemName.dust.getItemStack(DustResourceType.bronze), ItemName.ingot.getItemStack(IngotResourceType.bronze));
        SmeltingRecipes.add(StackUtil.copyWithWildCard(BlockName.rubber_wood.getItemStack()), new ItemStack(Blocks.LOG, 1, 3), 0.1f);
        SmeltingRecipes.add(ItemName.misc_resource.getItemStack(MiscResourceType.resin), ItemName.crafting.getItemStack(CraftingItemType.rubber), 0.3f);
        SmeltingRecipes.add(ItemName.dust.getItemStack(DustResourceType.coal_fuel), ItemName.dust.getItemStack(DustResourceType.coal));
        SmeltingRecipes.add(ItemName.crafting.getItemStack(CraftingItemType.raw_crystal_memory), ItemName.crystal_memory.getItemStack());
    }

    private static void add(ItemStack input, ItemStack output) {
        SmeltingRecipes.add(input, output, 0.0f);
    }

    private static void add(ItemStack input, ItemStack output, float xp) {
        if (input == null) {
            throw new NullPointerException();
        }
        if (output == null) {
            throw new NullPointerException();
        }
        if (xp < 0.0f) {
            throw new IllegalArgumentException("negative xp");
        }
        FurnaceRecipes.instance().addSmeltingRecipe(input, output, xp);
    }
}

