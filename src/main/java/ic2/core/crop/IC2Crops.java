/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockBush
 *  net.minecraft.block.BlockFlower
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.world.biome.Biome
 *  net.minecraftforge.common.BiomeDictionary
 *  net.minecraftforge.common.BiomeDictionary$Type
 */
package ic2.core.crop;

import ic2.api.crops.BaseSeed;
import ic2.api.crops.CropCard;
import ic2.api.crops.Crops;
import ic2.core.block.state.IIdProvider;
import ic2.core.crop.cropcard.CropBaseMetalCommon;
import ic2.core.crop.cropcard.CropBaseMetalUncommon;
import ic2.core.crop.cropcard.CropBaseMushroom;
import ic2.core.crop.cropcard.CropCarrots;
import ic2.core.crop.cropcard.CropCocoa;
import ic2.core.crop.cropcard.CropCoffee;
import ic2.core.crop.cropcard.CropColorFlower;
import ic2.core.crop.cropcard.CropEating;
import ic2.core.crop.cropcard.CropHops;
import ic2.core.crop.cropcard.CropMelon;
import ic2.core.crop.cropcard.CropNetherWart;
import ic2.core.crop.cropcard.CropPotato;
import ic2.core.crop.cropcard.CropPumpkin;
import ic2.core.crop.cropcard.CropRedWheat;
import ic2.core.crop.cropcard.CropReed;
import ic2.core.crop.cropcard.CropStickreed;
import ic2.core.crop.cropcard.CropTerraWart;
import ic2.core.crop.cropcard.CropVenomilia;
import ic2.core.crop.cropcard.CropWeed;
import ic2.core.crop.cropcard.CropWheat;
import ic2.core.item.type.DustResourceType;
import ic2.core.ref.ItemName;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockFlower;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

public class IC2Crops
extends Crops {
    private final Map<BiomeDictionary.Type, Integer> humidityBiomeTypeBonus = new EnumMap<BiomeDictionary.Type, Integer>(BiomeDictionary.Type.class);
    private final Map<BiomeDictionary.Type, Integer> nutrientBiomeTypeBonus = new EnumMap<BiomeDictionary.Type, Integer>(BiomeDictionary.Type.class);
    private final Map<ItemStack, BaseSeed> baseSeeds = new HashMap<ItemStack, BaseSeed>();
    public static CropCard cropWheat = new CropWheat();
    public static CropCard cropPumpkin = new CropPumpkin();
    public static CropCard cropMelon = new CropMelon();
    public static CropCard cropYellowFlower = new CropColorFlower("dandelion", new String[]{"Yellow", "Flower"}, 11);
    public static CropCard cropRedFlower = new CropColorFlower("rose", new String[]{"Red", "Flower", "Rose"}, 1);
    public static CropCard cropBlackFlower = new CropColorFlower("blackthorn", new String[]{"Black", "Flower", "Rose"}, 0);
    public static CropCard cropPurpleFlower = new CropColorFlower("tulip", new String[]{"Purple", "Flower", "Tulip"}, 5);
    public static CropCard cropBlueFlower = new CropColorFlower("cyazint", new String[]{"Blue", "Flower"}, 6);
    public static CropCard cropVenomilia = new CropVenomilia();
    public static CropCard cropReed = new CropReed();
    public static CropCard cropStickReed = new CropStickreed();
    public static CropCard cropCocoa = new CropCocoa();
    public static CropCard cropRedMushroom = new CropBaseMushroom("redmushroom", new String[]{"Red", "Food", "Mushroom"}, new ItemStack((Block)Blocks.RED_MUSHROOM, 1, 32767));
    public static CropCard cropBrownMushroom = new CropBaseMushroom("brownmushroom", new String[]{"Brown", "Food", "Mushroom"}, new ItemStack((Block)Blocks.BROWN_MUSHROOM, 1, 32767));
    public static CropCard cropNetherWart = new CropNetherWart();
    public static CropCard cropTerraWart = new CropTerraWart();
    public static CropCard cropFerru = new CropBaseMetalCommon("ferru", new String[]{"Gray", "Leaves", "Metal"}, new String[]{"oreIron", "blockIron"}, ItemName.dust.getItemStack(DustResourceType.small_iron));
    public static CropCard cropCyprium = new CropBaseMetalCommon("cyprium", new String[]{"Orange", "Leaves", "Metal"}, new String[]{"oreCopper", "blockCopper"}, ItemName.dust.getItemStack(DustResourceType.small_copper));
    public static CropCard cropStagnium = new CropBaseMetalCommon("stagnium", new String[]{"Shiny", "Leaves", "Metal"}, new String[]{"oreTin", "blockTin"}, ItemName.dust.getItemStack(DustResourceType.small_tin));
    public static CropCard cropPlumbiscus = new CropBaseMetalCommon("plumbiscus", new String[]{"Dense", "Leaves", "Metal"}, new String[]{"oreLead", "blockLead"}, ItemName.dust.getItemStack(DustResourceType.small_lead));
    public static CropCard cropAurelia = new CropBaseMetalUncommon("aurelia", new String[]{"Gold", "Leaves", "Metal"}, new String[]{"oreGold", "blockGold"}, ItemName.dust.getItemStack(DustResourceType.small_gold));
    public static CropCard cropShining = new CropBaseMetalUncommon("shining", new String[]{"Silver", "Leaves", "Metal"}, new String[0], ItemName.dust.getItemStack(DustResourceType.small_silver));
    public static CropCard cropRedwheat = new CropRedWheat();
    public static CropCard cropCoffee = new CropCoffee();
    public static CropCard cropHops = new CropHops();
    public static CropCard cropCarrots = new CropCarrots();
    public static CropCard cropPotato = new CropPotato();
    public static CropCard cropEatingPlant = new CropEating();
    private final Map<String, Map<String, CropCard>> cropMap = new HashMap<String, Map<String, CropCard>>();

    public static void init() {
        Crops.instance = new IC2Crops();
        Crops.weed = new CropWeed();
        Crops.instance.addBiomenutrientsBonus(BiomeDictionary.Type.JUNGLE, 10);
        Crops.instance.addBiomenutrientsBonus(BiomeDictionary.Type.SWAMP, 10);
        Crops.instance.addBiomenutrientsBonus(BiomeDictionary.Type.MUSHROOM, 5);
        Crops.instance.addBiomenutrientsBonus(BiomeDictionary.Type.FOREST, 5);
        Crops.instance.addBiomenutrientsBonus(BiomeDictionary.Type.RIVER, 2);
        Crops.instance.addBiomenutrientsBonus(BiomeDictionary.Type.PLAINS, 0);
        Crops.instance.addBiomenutrientsBonus(BiomeDictionary.Type.SAVANNA, -2);
        Crops.instance.addBiomenutrientsBonus(BiomeDictionary.Type.HILLS, -5);
        Crops.instance.addBiomenutrientsBonus(BiomeDictionary.Type.MOUNTAIN, -5);
        Crops.instance.addBiomenutrientsBonus(BiomeDictionary.Type.WASTELAND, -8);
        Crops.instance.addBiomenutrientsBonus(BiomeDictionary.Type.END, -10);
        Crops.instance.addBiomenutrientsBonus(BiomeDictionary.Type.NETHER, -10);
        Crops.instance.addBiomenutrientsBonus(BiomeDictionary.Type.DEAD, -10);
        IC2Crops.registerCrops();
        IC2Crops.registerBaseSeeds();
    }

    public static void registerCrops() {
        Crops.instance.registerCrop(weed);
        Crops.instance.registerCrop(cropWheat);
        Crops.instance.registerCrop(cropPumpkin);
        Crops.instance.registerCrop(cropMelon);
        Crops.instance.registerCrop(cropYellowFlower);
        Crops.instance.registerCrop(cropRedFlower);
        Crops.instance.registerCrop(cropBlackFlower);
        Crops.instance.registerCrop(cropPurpleFlower);
        Crops.instance.registerCrop(cropBlueFlower);
        Crops.instance.registerCrop(cropVenomilia);
        Crops.instance.registerCrop(cropReed);
        Crops.instance.registerCrop(cropStickReed);
        Crops.instance.registerCrop(cropCocoa);
        Crops.instance.registerCrop(cropFerru);
        Crops.instance.registerCrop(cropAurelia);
        Crops.instance.registerCrop(cropRedwheat);
        Crops.instance.registerCrop(cropNetherWart);
        Crops.instance.registerCrop(cropTerraWart);
        Crops.instance.registerCrop(cropCoffee);
        Crops.instance.registerCrop(cropHops);
        Crops.instance.registerCrop(cropCarrots);
        Crops.instance.registerCrop(cropPotato);
        Crops.instance.registerCrop(cropRedMushroom);
        Crops.instance.registerCrop(cropBrownMushroom);
        Crops.instance.registerCrop(cropEatingPlant);
        Crops.instance.registerCrop(cropCyprium);
        Crops.instance.registerCrop(cropStagnium);
        Crops.instance.registerCrop(cropPlumbiscus);
        Crops.instance.registerCrop(cropShining);
    }

    public static void registerBaseSeeds() {
        Crops.instance.registerBaseSeed(new ItemStack(Items.WHEAT_SEEDS, 1, 32767), cropWheat, 1, 1, 1, 1);
        Crops.instance.registerBaseSeed(new ItemStack(Items.PUMPKIN_SEEDS, 1, 32767), cropPumpkin, 1, 1, 1, 1);
        Crops.instance.registerBaseSeed(new ItemStack(Items.MELON_SEEDS, 1, 32767), cropMelon, 1, 1, 1, 1);
        Crops.instance.registerBaseSeed(new ItemStack(Items.NETHER_WART, 1, 32767), cropNetherWart, 1, 1, 1, 1);
        Crops.instance.registerBaseSeed(new ItemStack(Items.REEDS, 1, 32767), cropReed, 1, 3, 0, 2);
        Crops.instance.registerBaseSeed(new ItemStack(Items.DYE, 1, 3), cropCocoa, 1, 0, 0, 0);
        Crops.instance.registerBaseSeed(new ItemStack((Block)Blocks.RED_FLOWER, 4, 32767), cropRedFlower, 4, 1, 1, 1);
        Crops.instance.registerBaseSeed(new ItemStack((Block)Blocks.YELLOW_FLOWER, 4, 32767), cropYellowFlower, 4, 1, 1, 1);
        Crops.instance.registerBaseSeed(new ItemStack(Items.CARROT, 1, 32767), cropCarrots, 1, 1, 1, 1);
        Crops.instance.registerBaseSeed(new ItemStack(Items.POTATO, 1, 32767), cropPotato, 1, 1, 1, 1);
        Crops.instance.registerBaseSeed(new ItemStack((Block)Blocks.BROWN_MUSHROOM, 4, 32767), cropBrownMushroom, 1, 1, 1, 1);
        Crops.instance.registerBaseSeed(new ItemStack((Block)Blocks.RED_MUSHROOM, 4, 32767), cropRedMushroom, 1, 1, 1, 1);
    }

    @Override
    public void addBiomenutrientsBonus(BiomeDictionary.Type type, int nutrientsBonus) {
        this.nutrientBiomeTypeBonus.put(type, nutrientsBonus);
    }

    @Override
    public void addBiomehumidityBonus(BiomeDictionary.Type type, int humidityBonus) {
        this.humidityBiomeTypeBonus.put(type, humidityBonus);
    }

    @Override
    public int getHumidityBiomeBonus(Biome biome) {
        Integer ret = 0;
        for (BiomeDictionary.Type type : BiomeDictionary.getTypesForBiome((Biome)biome)) {
            Integer val = this.humidityBiomeTypeBonus.get((Object)type);
            if (val == null || val <= ret) continue;
            ret = val;
        }
        return ret;
    }

    @Override
    public int getNutrientBiomeBonus(Biome biome) {
        Integer ret = 0;
        for (BiomeDictionary.Type type : BiomeDictionary.getTypesForBiome((Biome)biome)) {
            Integer val = this.nutrientBiomeTypeBonus.get((Object)type);
            if (val == null || val <= ret) continue;
            ret = val;
        }
        return ret;
    }

    @Override
    public CropCard getCropCard(String owner, String name) {
        Map<String, CropCard> map = this.cropMap.get(owner);
        if (map == null) {
            return null;
        }
        return map.get(name);
    }

    @Override
    public CropCard getCropCard(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            return null;
        }
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt.hasKey("owner") && nbt.hasKey("name")) {
            return this.getCropCard(nbt.getString("owner"), nbt.getString("name"));
        }
        return null;
    }

    @Override
    public Collection<CropCard> getCrops() {
        return new AbstractCollection<CropCard>(){

            @Override
            public Iterator<CropCard> iterator() {
                return new Iterator<CropCard>(){
                    private final Iterator<Map<String, CropCard>> mapIterator;
                    private Iterator<CropCard> iterator;

                    @Override
                    public boolean hasNext() {
                        return this.iterator != null && this.iterator.hasNext();
                    }

                    @Override
                    public CropCard next() {
                        if (this.iterator == null) {
                            throw new NoSuchElementException("no more elements");
                        }
                        CropCard ret = this.iterator.next();
                        if (!this.iterator.hasNext()) {
                            this.iterator = this.getNextIterator();
                        }
                        return ret;
                    }

                    @Override
                    public void remove() {
                        throw new UnsupportedOperationException("This iterator is read-only.");
                    }

                    private Iterator<CropCard> getNextIterator() {
                        Iterator<CropCard> ret = null;
                        while (this.mapIterator.hasNext() && ret == null) {
                            ret = this.mapIterator.next().values().iterator();
                            if (ret.hasNext()) continue;
                            ret = null;
                        }
                        return ret;
                    }
                };
            }

            @Override
            public int size() {
                int ret = 0;
                for (Map map : IC2Crops.this.cropMap.values()) {
                    ret += map.size();
                }
                return ret;
            }

        };
    }

    @Override
    public void registerCrop(CropCard crop) {
        String owner = crop.getOwner();
        String name = crop.getName();
        Map<String, CropCard> map = this.cropMap.get(owner);
        if (map == null) {
            map = new HashMap<String, CropCard>();
            this.cropMap.put(owner, map);
        }
        map.put(name, crop);
    }

    @Override
    public boolean registerBaseSeed(ItemStack stack, CropCard crop, int size, int growth, int gain, int resistance) {
        for (ItemStack key : this.baseSeeds.keySet()) {
            if (key.getItem() != stack.getItem() || key.getItemDamage() != stack.getItemDamage()) continue;
            return false;
        }
        this.baseSeeds.put(stack, new BaseSeed(crop, size, growth, gain, resistance, stack.stackSize));
        return true;
    }

    @Override
    public BaseSeed getBaseSeed(ItemStack stack) {
        if (stack == null) {
            return null;
        }
        for (Map.Entry<ItemStack, BaseSeed> entry : this.baseSeeds.entrySet()) {
            ItemStack key = entry.getKey();
            if (key.getItem() != stack.getItem() || key.getItemDamage() != 32767 && key.getItemDamage() != stack.getItemDamage()) continue;
            return this.baseSeeds.get((Object)key);
        }
        return null;
    }

}

