/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.oredict.OreDictionary
 */
package ic2.core.recipe;

import ic2.core.block.state.IIdProvider;
import ic2.core.block.type.ResourceBlock;
import ic2.core.item.type.CraftingItemType;
import ic2.core.item.type.DustResourceType;
import ic2.core.item.type.IngotResourceType;
import ic2.core.item.type.MiscResourceType;
import ic2.core.item.type.OreResourceType;
import ic2.core.item.type.PlateResourceType;
import ic2.core.ref.BlockName;
import ic2.core.ref.ItemName;
import ic2.core.util.StackUtil;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class OreDictionaryEntries {
    public static void load() {
        OreDictionaryEntries.add("oreCopper", BlockName.resource.getItemStack(ResourceBlock.copper_ore));
        OreDictionaryEntries.add("oreLead", BlockName.resource.getItemStack(ResourceBlock.lead_ore));
        OreDictionaryEntries.add("oreTin", BlockName.resource.getItemStack(ResourceBlock.tin_ore));
        OreDictionaryEntries.add("oreUranium", BlockName.resource.getItemStack(ResourceBlock.uranium_ore));
        OreDictionaryEntries.add("dustStone", ItemName.dust.getItemStack(DustResourceType.stone));
        OreDictionaryEntries.add("dustBronze", ItemName.dust.getItemStack(DustResourceType.bronze));
        OreDictionaryEntries.add("dustClay", ItemName.dust.getItemStack(DustResourceType.clay));
        OreDictionaryEntries.add("dustCoal", ItemName.dust.getItemStack(DustResourceType.coal));
        OreDictionaryEntries.add("dustCopper", ItemName.dust.getItemStack(DustResourceType.copper));
        OreDictionaryEntries.add("dustGold", ItemName.dust.getItemStack(DustResourceType.gold));
        OreDictionaryEntries.add("dustIron", ItemName.dust.getItemStack(DustResourceType.iron));
        OreDictionaryEntries.add("dustSilver", ItemName.dust.getItemStack(DustResourceType.silver));
        OreDictionaryEntries.add("dustTin", ItemName.dust.getItemStack(DustResourceType.tin));
        OreDictionaryEntries.add("dustLead", ItemName.dust.getItemStack(DustResourceType.lead));
        OreDictionaryEntries.add("dustObsidian", ItemName.dust.getItemStack(DustResourceType.obsidian));
        OreDictionaryEntries.add("dustLapis", ItemName.dust.getItemStack(DustResourceType.lapis));
        OreDictionaryEntries.add("dustSulfur", ItemName.dust.getItemStack(DustResourceType.sulfur));
        OreDictionaryEntries.add("dustLithium", ItemName.dust.getItemStack(DustResourceType.lithium));
        OreDictionaryEntries.add("dustDiamond", ItemName.dust.getItemStack(DustResourceType.diamond));
        OreDictionaryEntries.add("dustSiliconDioxide", ItemName.dust.getItemStack(DustResourceType.silicon_dioxide));
        OreDictionaryEntries.add("dustHydratedCoal", ItemName.dust.getItemStack(DustResourceType.coal_fuel));
        OreDictionaryEntries.add("dustAshes", ItemName.misc_resource.getItemStack(MiscResourceType.ashes));
        OreDictionaryEntries.add("dustTinyCopper", ItemName.dust.getItemStack(DustResourceType.small_copper));
        OreDictionaryEntries.add("dustTinyGold", ItemName.dust.getItemStack(DustResourceType.small_gold));
        OreDictionaryEntries.add("dustTinyIron", ItemName.dust.getItemStack(DustResourceType.small_iron));
        OreDictionaryEntries.add("dustTinySilver", ItemName.dust.getItemStack(DustResourceType.small_silver));
        OreDictionaryEntries.add("dustTinyTin", ItemName.dust.getItemStack(DustResourceType.small_tin));
        OreDictionaryEntries.add("dustTinyLead", ItemName.dust.getItemStack(DustResourceType.small_lead));
        OreDictionaryEntries.add("dustTinySulfur", ItemName.dust.getItemStack(DustResourceType.small_sulfur));
        OreDictionaryEntries.add("dustTinyLithium", ItemName.dust.getItemStack(DustResourceType.small_lithium));
        OreDictionaryEntries.add("dustTinyBronze", ItemName.dust.getItemStack(DustResourceType.small_bronze));
        OreDictionaryEntries.add("dustTinyLapis", ItemName.dust.getItemStack(DustResourceType.small_lapis));
        OreDictionaryEntries.add("dustTinyObsidian", ItemName.dust.getItemStack(DustResourceType.small_obsidian));
        OreDictionaryEntries.add("itemRubber", ItemName.crafting.getItemStack(CraftingItemType.rubber));
        OreDictionaryEntries.add("ingotBronze", ItemName.ingot.getItemStack(IngotResourceType.bronze));
        OreDictionaryEntries.add("ingotCopper", ItemName.ingot.getItemStack(IngotResourceType.copper));
        OreDictionaryEntries.add("ingotSteel", ItemName.ingot.getItemStack(IngotResourceType.steel));
        OreDictionaryEntries.add("ingotLead", ItemName.ingot.getItemStack(IngotResourceType.lead));
        OreDictionaryEntries.add("ingotTin", ItemName.ingot.getItemStack(IngotResourceType.tin));
        OreDictionaryEntries.add("ingotSilver", ItemName.ingot.getItemStack(IngotResourceType.silver));
        OreDictionaryEntries.add("plateIron", ItemName.plate.getItemStack(PlateResourceType.iron));
        OreDictionaryEntries.add("plateGold", ItemName.plate.getItemStack(PlateResourceType.gold));
        OreDictionaryEntries.add("plateCopper", ItemName.plate.getItemStack(PlateResourceType.copper));
        OreDictionaryEntries.add("plateTin", ItemName.plate.getItemStack(PlateResourceType.tin));
        OreDictionaryEntries.add("plateLead", ItemName.plate.getItemStack(PlateResourceType.lead));
        OreDictionaryEntries.add("plateLapis", ItemName.plate.getItemStack(PlateResourceType.lapis));
        OreDictionaryEntries.add("plateObsidian", ItemName.plate.getItemStack(PlateResourceType.obsidian));
        OreDictionaryEntries.add("plateBronze", ItemName.plate.getItemStack(PlateResourceType.bronze));
        OreDictionaryEntries.add("plateSteel", ItemName.plate.getItemStack(PlateResourceType.steel));
        OreDictionaryEntries.add("plateDenseSteel", ItemName.plate.getItemStack(PlateResourceType.dense_steel));
        OreDictionaryEntries.add("plateDenseIron", ItemName.plate.getItemStack(PlateResourceType.dense_iron));
        OreDictionaryEntries.add("plateDenseGold", ItemName.plate.getItemStack(PlateResourceType.dense_gold));
        OreDictionaryEntries.add("plateDenseCopper", ItemName.plate.getItemStack(PlateResourceType.dense_copper));
        OreDictionaryEntries.add("plateDenseTin", ItemName.plate.getItemStack(PlateResourceType.dense_tin));
        OreDictionaryEntries.add("plateDenseLead", ItemName.plate.getItemStack(PlateResourceType.dense_lead));
        OreDictionaryEntries.add("plateDenseLapis", ItemName.plate.getItemStack(PlateResourceType.dense_lapis));
        OreDictionaryEntries.add("plateDenseObsidian", ItemName.plate.getItemStack(PlateResourceType.dense_obsidian));
        OreDictionaryEntries.add("plateDenseBronze", ItemName.plate.getItemStack(PlateResourceType.dense_bronze));
        OreDictionaryEntries.add("crushedIron", ItemName.crushed.getItemStack(OreResourceType.iron));
        OreDictionaryEntries.add("crushedGold", ItemName.crushed.getItemStack(OreResourceType.gold));
        OreDictionaryEntries.add("crushedSilver", ItemName.crushed.getItemStack(OreResourceType.silver));
        OreDictionaryEntries.add("crushedLead", ItemName.crushed.getItemStack(OreResourceType.lead));
        OreDictionaryEntries.add("crushedCopper", ItemName.crushed.getItemStack(OreResourceType.copper));
        OreDictionaryEntries.add("crushedTin", ItemName.crushed.getItemStack(OreResourceType.tin));
        OreDictionaryEntries.add("crushedUranium", ItemName.crushed.getItemStack(OreResourceType.uranium));
        OreDictionaryEntries.add("crushedPurifiedIron", ItemName.purified.getItemStack(OreResourceType.iron));
        OreDictionaryEntries.add("crushedPurifiedGold", ItemName.purified.getItemStack(OreResourceType.gold));
        OreDictionaryEntries.add("crushedPurifiedSilver", ItemName.purified.getItemStack(OreResourceType.silver));
        OreDictionaryEntries.add("crushedPurifiedLead", ItemName.purified.getItemStack(OreResourceType.lead));
        OreDictionaryEntries.add("crushedPurifiedCopper", ItemName.purified.getItemStack(OreResourceType.copper));
        OreDictionaryEntries.add("crushedPurifiedTin", ItemName.purified.getItemStack(OreResourceType.tin));
        OreDictionaryEntries.add("crushedPurifiedUranium", ItemName.purified.getItemStack(OreResourceType.uranium));
        OreDictionaryEntries.add("blockBronze", BlockName.resource.getItemStack(ResourceBlock.bronze_block));
        OreDictionaryEntries.add("blockCopper", BlockName.resource.getItemStack(ResourceBlock.copper_block));
        OreDictionaryEntries.add("blockTin", BlockName.resource.getItemStack(ResourceBlock.tin_block));
        OreDictionaryEntries.add("blockUranium", BlockName.resource.getItemStack(ResourceBlock.uranium_block));
        OreDictionaryEntries.add("blockLead", BlockName.resource.getItemStack(ResourceBlock.lead_block));
        OreDictionaryEntries.add("blockSteel", BlockName.resource.getItemStack(ResourceBlock.steel_block));
        OreDictionaryEntries.add("circuitBasic", ItemName.crafting.getItemStack(CraftingItemType.circuit));
        OreDictionaryEntries.add("circuitAdvanced", ItemName.crafting.getItemStack(CraftingItemType.advanced_circuit));
        OreDictionaryEntries.add("gemDiamond", ItemName.crafting.getItemStack(CraftingItemType.industrial_diamond));
        OreDictionaryEntries.add("craftingToolForgeHammer", StackUtil.copyWithWildCard(ItemName.forge_hammer.getItemStack()));
        OreDictionaryEntries.add("craftingToolWireCutter", StackUtil.copyWithWildCard(ItemName.cutter.getItemStack()));
    }

    private static void add(String name, ItemStack stack) {
        if (name == null) {
            throw new NullPointerException("null name for stack " + StackUtil.toStringSafe(stack));
        }
        if (stack == null || stack.getItem() == null) {
            throw new IllegalArgumentException("invalid stack for " + name + ": " + StackUtil.toStringSafe(stack));
        }
        OreDictionary.registerOre((String)name, (ItemStack)stack);
    }
}

