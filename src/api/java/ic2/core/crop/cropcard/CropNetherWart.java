/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 */
package ic2.core.crop.cropcard;

import ic2.api.crops.CropCard;
import ic2.api.crops.CropProperties;
import ic2.api.crops.ICropTile;
import ic2.core.crop.IC2CropCard;
import ic2.core.crop.IC2Crops;
import ic2.core.crop.TileEntityCrop;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class CropNetherWart
extends IC2CropCard {
    @Override
    public String getName() {
        return "netherwart";
    }

    @Override
    public String getDiscoveredBy() {
        return "Notch";
    }

    @Override
    public CropProperties getProperties() {
        return new CropProperties(5, 4, 2, 0, 2, 1);
    }

    @Override
    public String[] getAttributes() {
        return new String[]{"Red", "Nether", "Ingredient", "Soulsand"};
    }

    @Override
    public int getMaxSize() {
        return 3;
    }

    @Override
    public double dropGainChance() {
        return 2.0;
    }

    @Override
    public ItemStack getGain(ICropTile crop) {
        return new ItemStack(Items.NETHER_WART, 1);
    }

    @Override
    public void tick(ICropTile crop) {
        TileEntityCrop tileEntityCrop = (TileEntityCrop)crop;
        if (tileEntityCrop.isBlockBelow(Blocks.SOUL_SAND)) {
            if (this.canGrow(tileEntityCrop)) {
                tileEntityCrop.setGrowthPoints(tileEntityCrop.getGrowthPoints() + (int)((double)tileEntityCrop.calcGrowthRate() * 0.5));
            }
        } else if (tileEntityCrop.isBlockBelow(Blocks.SNOW) && crop.getWorld().rand.nextInt(300) == 0) {
            tileEntityCrop.setCrop(IC2Crops.cropTerraWart);
        }
    }

    @Override
    public int getRootsLength(ICropTile crop) {
        return 5;
    }
}

