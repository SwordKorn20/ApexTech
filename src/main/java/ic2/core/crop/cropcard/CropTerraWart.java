/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.init.Blocks
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
import ic2.core.ref.ItemName;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class CropTerraWart
extends IC2CropCard {
    @Override
    public String getName() {
        return "terrawart";
    }

    @Override
    public CropProperties getProperties() {
        return new CropProperties(5, 2, 4, 0, 3, 0);
    }

    @Override
    public String[] getAttributes() {
        return new String[]{"Blue", "Aether", "Consumable", "Snow"};
    }

    @Override
    public int getMaxSize() {
        return 3;
    }

    @Override
    public double dropGainChance() {
        return 0.8;
    }

    @Override
    public ItemStack getGain(ICropTile crop) {
        return ItemName.terra_wart.getItemStack();
    }

    @Override
    public void tick(ICropTile crop) {
        TileEntityCrop tileEntityCrop = (TileEntityCrop)crop;
        if (tileEntityCrop.isBlockBelow(Blocks.SNOW)) {
            if (this.canGrow(tileEntityCrop)) {
                tileEntityCrop.setGrowthPoints(tileEntityCrop.getGrowthPoints() + (int)((double)tileEntityCrop.calcGrowthRate() * 0.5));
            }
        } else if (tileEntityCrop.isBlockBelow(Blocks.SOUL_SAND) && crop.getWorld().rand.nextInt(300) == 0) {
            tileEntityCrop.setCrop(IC2Crops.cropNetherWart);
        }
    }

    @Override
    public int getRootsLength(ICropTile crop) {
        return 5;
    }
}

