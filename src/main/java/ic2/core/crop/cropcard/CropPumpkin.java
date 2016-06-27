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

public class CropPumpkin
extends IC2CropCard {
    @Override
    public String getName() {
        return "pumpkin";
    }

    @Override
    public String getDiscoveredBy() {
        return "Notch";
    }

    @Override
    public CropProperties getProperties() {
        return new CropProperties(1, 0, 1, 0, 3, 1);
    }

    @Override
    public String[] getAttributes() {
        return new String[]{"Orange", "Decoration", "Stem"};
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
    public boolean canBeHarvested(ICropTile crop) {
        return crop.getCurrentSize() == 4;
    }

    @Override
    public int getOptimalHarvestSize(ICropTile crop) {
        return 4;
    }

    @Override
    public ItemStack getGain(ICropTile crop) {
        return new ItemStack(Blocks.PUMPKIN);
    }

    @Override
    public ItemStack getSeeds(ICropTile crop) {
        if (crop.getStatGain() <= 1 && crop.getStatGrowth() <= 1 && crop.getStatResistance() <= 1) {
            return new ItemStack(Items.PUMPKIN_SEEDS, IC2.random.nextInt(3) + 1);
        }
        return super.getSeeds(crop);
    }

    @Override
    public int getGrowthDuration(ICropTile crop) {
        if (crop.getCurrentSize() == 3) {
            return 600;
        }
        return 200;
    }

    @Override
    public int getSizeAfterHarvest(ICropTile crop) {
        return 3;
    }
}

