/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package ic2.core.crop.cropcard;

import ic2.api.crops.CropProperties;
import ic2.api.crops.ICropTile;
import ic2.core.IC2;
import ic2.core.crop.IC2CropCard;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class CropMelon
extends IC2CropCard {
    @Override
    public String getName() {
        return "melon";
    }

    @Override
    public String getDiscoveredBy() {
        return "Chao";
    }

    @Override
    public CropProperties getProperties() {
        return new CropProperties(2, 0, 4, 0, 2, 0);
    }

    @Override
    public String[] getAttributes() {
        return new String[]{"Green", "Food", "Stem"};
    }

    @Override
    public int getMaxSize() {
        return 4;
    }

    @Override
    public boolean canGrow(ICropTile crop) {
        return crop.getCurrentSize() <= 3;
    }

    @Override
    public int getWeightInfluences(ICropTile crop, int humidity, int nutrients, int air) {
        return (int)((double)humidity * 1.1 + (double)nutrients * 0.9 + (double)air);
    }

    @Override
    public ItemStack getGain(ICropTile crop) {
        if (IC2.random.nextInt(3) == 0) {
            return new ItemStack(Blocks.MELON_BLOCK);
        }
        return new ItemStack(Items.MELON, IC2.random.nextInt(4) + 2);
    }

    @Override
    public ItemStack getSeeds(ICropTile crop) {
        if (crop.getStatGain() <= 1 && crop.getStatGrowth() <= 1 && crop.getStatResistance() <= 1) {
            return new ItemStack(Items.MELON_SEEDS, IC2.random.nextInt(2) + 1);
        }
        return super.getSeeds(crop);
    }

    @Override
    public int getGrowthDuration(ICropTile crop) {
        if (crop.getCurrentSize() == 3) {
            return 700;
        }
        return 250;
    }

    @Override
    public int getSizeAfterHarvest(ICropTile crop) {
        return 3;
    }
}

