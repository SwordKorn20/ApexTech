/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.biome.Biome
 *  net.minecraftforge.common.BiomeDictionary
 *  net.minecraftforge.common.BiomeDictionary$Type
 */
package ic2.api.crops;

import ic2.api.crops.BaseSeed;
import ic2.api.crops.CropCard;
import java.util.Collection;
import net.minecraft.item.ItemStack;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

public abstract class Crops {
    public static Crops instance;
    public static CropCard weed;

    public abstract void addBiomenutrientsBonus(BiomeDictionary.Type var1, int var2);

    public abstract void addBiomehumidityBonus(BiomeDictionary.Type var1, int var2);

    public abstract int getHumidityBiomeBonus(Biome var1);

    public abstract int getNutrientBiomeBonus(Biome var1);

    public abstract CropCard getCropCard(String var1, String var2);

    public abstract CropCard getCropCard(ItemStack var1);

    public abstract Collection<CropCard> getCrops();

    public abstract void registerCrop(CropCard var1);

    public abstract boolean registerBaseSeed(ItemStack var1, CropCard var2, int var3, int var4, int var5, int var6);

    public abstract BaseSeed getBaseSeed(ItemStack var1);
}

