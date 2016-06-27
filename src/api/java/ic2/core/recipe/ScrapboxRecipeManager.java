/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockGrass
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.item.ItemStack
 */
package ic2.core.recipe;

import ic2.api.recipe.IScrapboxManager;
import ic2.api.recipe.Recipes;
import ic2.core.IC2;
import ic2.core.block.state.IIdProvider;
import ic2.core.block.type.ResourceBlock;
import ic2.core.item.type.CraftingItemType;
import ic2.core.item.type.DustResourceType;
import ic2.core.ref.BlockName;
import ic2.core.ref.ItemName;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public final class ScrapboxRecipeManager
implements IScrapboxManager {
    private final List<Drop> drops = new ArrayList<Drop>();

    public static void setup() {
        if (Recipes.scrapboxDrops != null) {
            throw new IllegalStateException("already initialized");
        }
        Recipes.scrapboxDrops = new ScrapboxRecipeManager();
    }

    public static void load() {
        ((ScrapboxRecipeManager)Recipes.scrapboxDrops).addBuiltinDrops();
    }

    private ScrapboxRecipeManager() {
    }

    @Override
    public void addDrop(ItemStack drop, float rawChance) {
        this.drops.add(new Drop(drop, rawChance));
    }

    @Override
    public ItemStack getDrop(ItemStack input, boolean adjustInput) {
        if (this.drops.isEmpty()) {
            return null;
        }
        if (adjustInput) {
            --input.stackSize;
        }
        float chance = IC2.random.nextFloat() * Drop.topChance;
        int low = 0;
        int high = this.drops.size() - 1;
        while (low < high) {
            int mid = (high + low) / 2;
            if (chance < this.drops.get((int)mid).upperChanceBound) {
                high = mid;
                continue;
            }
            low = mid + 1;
        }
        return this.drops.get((int)low).item.copy();
    }

    @Override
    public Map<ItemStack, Float> getDrops() {
        HashMap<ItemStack, Float> ret = new HashMap<ItemStack, Float>(this.drops.size());
        for (Drop drop : this.drops) {
            ret.put(drop.item, Float.valueOf(drop.originalChance.floatValue() / Drop.topChance));
        }
        return ret;
    }

    private void addBuiltinDrops() {
        if (IC2.suddenlyHoes) {
            this.addDrop(Items.WOODEN_HOE, 9001.0f);
        } else {
            this.addDrop(Items.WOODEN_HOE, 5.01f);
        }
        this.addDrop(Blocks.DIRT, 5.0f);
        this.addDrop(Items.STICK, 4.0f);
        this.addDrop((Block)Blocks.GRASS, 3.0f);
        this.addDrop(Blocks.GRAVEL, 3.0f);
        this.addDrop(Blocks.NETHERRACK, 2.0f);
        this.addDrop(Items.ROTTEN_FLESH, 2.0f);
        this.addDrop(Items.APPLE, 1.5f);
        this.addDrop(Items.BREAD, 1.5f);
        this.addDrop(ItemName.filled_tin_can.getItemStack(), 1.5f);
        this.addDrop(Items.WOODEN_SWORD, 1.0f);
        this.addDrop(Items.WOODEN_SHOVEL, 1.0f);
        this.addDrop(Items.WOODEN_PICKAXE, 1.0f);
        this.addDrop(Blocks.SOUL_SAND, 1.0f);
        this.addDrop(Items.SIGN, 1.0f);
        this.addDrop(Items.LEATHER, 1.0f);
        this.addDrop(Items.FEATHER, 1.0f);
        this.addDrop(Items.BONE, 1.0f);
        this.addDrop(Items.COOKED_PORKCHOP, 0.9f);
        this.addDrop(Items.COOKED_BEEF, 0.9f);
        this.addDrop(Blocks.PUMPKIN, 0.9f);
        this.addDrop(Items.COOKED_CHICKEN, 0.9f);
        this.addDrop(Items.MINECART, 0.01f);
        this.addDrop(Items.REDSTONE, 0.9f);
        this.addDrop(ItemName.crafting.getItemStack(CraftingItemType.rubber), 0.8f);
        this.addDrop(Items.GLOWSTONE_DUST, 0.8f);
        this.addDrop(ItemName.dust.getItemStack(DustResourceType.coal), 0.8f);
        this.addDrop(ItemName.dust.getItemStack(DustResourceType.copper), 0.8f);
        this.addDrop(ItemName.dust.getItemStack(DustResourceType.tin), 0.8f);
        this.addDrop(ItemName.single_use_battery.getItemStack(), 0.7f);
        this.addDrop(ItemName.dust.getItemStack(DustResourceType.iron), 0.7f);
        this.addDrop(ItemName.dust.getItemStack(DustResourceType.gold), 0.7f);
        this.addDrop(Items.SLIME_BALL, 0.6f);
        this.addDrop(Blocks.IRON_ORE, 0.5f);
        this.addDrop((Item)Items.GOLDEN_HELMET, 0.01f);
        this.addDrop(Blocks.GOLD_ORE, 0.5f);
        this.addDrop(Items.CAKE, 0.5f);
        this.addDrop(Items.DIAMOND, 0.1f);
        this.addDrop(Items.EMERALD, 0.05f);
        this.addDrop(Items.ENDER_PEARL, 0.08f);
        this.addDrop(Items.BLAZE_ROD, 0.04f);
        this.addDrop(Items.EGG, 0.8f);
        this.addDrop(BlockName.resource.getItemStack(ResourceBlock.copper_ore), 0.7f);
        this.addDrop(BlockName.resource.getItemStack(ResourceBlock.tin_ore), 0.7f);
    }

    private void addDrop(Block block, float rawChance) {
        this.addDrop(new ItemStack(block), rawChance);
    }

    private void addDrop(Item item, float rawChance) {
        this.addDrop(new ItemStack(item), rawChance);
    }

    private static class Drop {
        ItemStack item;
        Float originalChance;
        float upperChanceBound;
        static float topChance;

        Drop(ItemStack item1, float chance) {
            this.item = item1;
            this.originalChance = Float.valueOf(chance);
            this.upperChanceBound = topChance += chance;
        }
    }

}

