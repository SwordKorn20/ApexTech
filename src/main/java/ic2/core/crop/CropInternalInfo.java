/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockFarmland
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyInteger
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.init.Blocks
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraft.world.biome.Biome
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidRegistry
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.FluidTank
 */
package ic2.core.crop;

import ic2.api.crops.Crops;
import ic2.core.IC2;
import ic2.core.crop.TileEntityCrop;
import ic2.core.ref.FluidName;
import ic2.core.util.Util;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class CropInternalInfo {
    private byte statGrowth;
    private byte statGain;
    private byte statResistance;
    private short storageNutrient;
    private short storageWater;
    private short storageWeedEX;
    private byte terrainAirQuality;
    private byte terrainHumidity;
    private byte terrainNutrients;
    private byte currentSize;
    private short growthPoints;
    private byte scanLevel;
    private boolean crossingBase;

    public CropInternalInfo() {
        this.resetTile();
    }

    public void readFromNbt(NBTTagCompound nbtTagCompound) {
        if (nbtTagCompound.hasKey("internalInfo")) {
            NBTTagCompound nbtTagInfo = nbtTagCompound.getCompoundTag("internalInfo");
            this.setStatGrowth(nbtTagInfo.getByte("statGrowth"));
            this.setStatGain(nbtTagInfo.getByte("statGain"));
            this.setStatResistance(nbtTagInfo.getByte("statResistance"));
            this.setStorageNutrient(nbtTagInfo.getShort("storageNutrient"));
            this.setStorageWater(nbtTagInfo.getShort("storageWater"));
            this.setStorageWeedEX(nbtTagInfo.getShort("storageWeedEX"));
            this.setTerrainHumidity(nbtTagInfo.getByte("terrainHumidity"));
            this.setTerrainNutrients(nbtTagInfo.getByte("terrainNutrients"));
            this.setTerrainAirQuality(nbtTagInfo.getByte("terrainAirQuality"));
            this.setCurrentSize(nbtTagInfo.getByte("currentSize"));
            this.setGrowthPoints(nbtTagInfo.getShort("growthPoints"));
            this.setScanLevel(nbtTagInfo.getByte("scanLevel"));
            this.setCrossingBase(nbtTagInfo.getBoolean("crossingBase"));
        } else {
            this.resetTile();
        }
    }

    public void writeToNbt(NBTTagCompound nbtTagCompound) {
        NBTTagCompound nbtTagInfo = new NBTTagCompound();
        nbtTagInfo.setByte("statGrowth", this.statGrowth);
        nbtTagInfo.setByte("statGain", this.statGain);
        nbtTagInfo.setByte("statResistance", this.statResistance);
        nbtTagInfo.setShort("storageNutrient", this.storageNutrient);
        nbtTagInfo.setShort("storageWater", this.storageWater);
        nbtTagInfo.setShort("storageWeedEX", this.storageWeedEX);
        nbtTagInfo.setByte("terrainHumidity", this.terrainHumidity);
        nbtTagInfo.setByte("terrainNutrients", this.terrainNutrients);
        nbtTagInfo.setByte("terrainAirQuality", this.terrainAirQuality);
        nbtTagInfo.setByte("currentSize", this.currentSize);
        nbtTagInfo.setShort("growthPoints", this.growthPoints);
        nbtTagInfo.setByte("scanLevel", this.scanLevel);
        nbtTagInfo.setBoolean("crossingBase", this.crossingBase);
        nbtTagCompound.setTag("internalInfo", (NBTBase)nbtTagInfo);
    }

    public void resetCrop() {
        this.setStatGrowth(0);
        this.setStatGain(0);
        this.setStatResistance(0);
        this.terrainAirQuality = -1;
        this.terrainHumidity = -1;
        this.terrainNutrients = -1;
        this.setCurrentSize(0);
        this.setGrowthPoints(0);
        this.setScanLevel(0);
        this.setCrossingBase(false);
    }

    public void resetTile() {
        this.setStatGrowth(0);
        this.setStatGain(0);
        this.setStatResistance(0);
        this.setStorageNutrient(0);
        this.setStorageWater(0);
        this.setStorageWeedEX(0);
        this.terrainAirQuality = -1;
        this.terrainHumidity = -1;
        this.terrainNutrients = -1;
        this.setCurrentSize(0);
        this.setGrowthPoints(0);
        this.setScanLevel(0);
        this.setCrossingBase(false);
    }

    public void mixStats(List<TileEntityCrop> cropTes) {
        this.statGrowth = 0;
        this.statResistance = 0;
        this.statGain = 0;
        for (TileEntityCrop te : cropTes) {
            this.statGrowth = (byte)(this.statGrowth + te.getStatGrowth());
            this.statResistance = (byte)(this.statResistance + te.getStatResistance());
            this.statGain = (byte)(this.statGain + te.getStatGain());
        }
        int count = cropTes.size();
        this.statGrowth = (byte)(this.statGrowth / count);
        this.statResistance = (byte)(this.statResistance / count);
        this.statGain = (byte)(this.statGain / count);
        this.statGrowth = (byte)(this.statGrowth + (IC2.random.nextInt(1 + 2 * count) - count));
        this.statGain = (byte)(this.statGain + (IC2.random.nextInt(1 + 2 * count) - count));
        this.statResistance = (byte)(this.statResistance + (IC2.random.nextInt(1 + 2 * count) - count));
        this.setStatGrowth(Util.limit(this.statGrowth, 0, 31));
        this.setStatGain(Util.limit(this.statGain, 0, 31));
        this.setStatResistance(Util.limit(this.statResistance, 0, 31));
    }

    public int getStatGrowth() {
        return this.statGrowth;
    }

    public void setStatGrowth(int statGrowth) {
        this.statGrowth = (byte)statGrowth;
    }

    public void increaseStatGrowth() {
        this.statGrowth = (byte)(this.statGrowth + 1);
    }

    public void increaseStatGrowth(int value) {
        this.statGrowth = (byte)(this.statGrowth + value);
    }

    public void decreaseStatGrowth() {
        this.statGrowth = (byte)(this.statGrowth - 1);
    }

    public void decreaseStatGrowth(int value) {
        this.statGrowth = (byte)(this.statGrowth - value);
    }

    public int getStatGain() {
        return this.statGain;
    }

    public void setStatGain(int statGain) {
        this.statGain = (byte)statGain;
    }

    public void increaseStatGain() {
        this.statGain = (byte)(this.statGain + 1);
    }

    public void increaseStatGain(int value) {
        this.statGain = (byte)(this.statGain + value);
    }

    public void decreaseStatGain() {
        this.statGain = (byte)(this.statGain - 1);
    }

    public void decreaseStatGain(int value) {
        this.statGain = (byte)(this.statGain - value);
    }

    public int getStatResistance() {
        return this.statResistance;
    }

    public void setStatResistance(int statResistance) {
        this.statResistance = (byte)statResistance;
    }

    public void increaseStatResistance() {
        this.statResistance = (byte)(this.statResistance + 1);
    }

    public void increaseStatResistance(int value) {
        this.statResistance = (byte)(this.statResistance + value);
    }

    public void decreaseStatResistance() {
        this.statResistance = (byte)(this.statResistance - 1);
    }

    public void decreaseStatResistance(int value) {
        this.statResistance = (byte)(this.statResistance - value);
    }

    public int getStorageNutrient() {
        return this.storageNutrient;
    }

    public void setStorageNutrient(int storageNutrient) {
        this.storageNutrient = (short)storageNutrient;
    }

    public void increaseStorageNutrient() {
        this.storageNutrient = (short)(this.storageNutrient + 1);
    }

    public void increaseStorageNutrient(int value) {
        this.storageNutrient = (short)(this.storageNutrient + value);
    }

    public void decreaseStorageNutrient() {
        this.storageNutrient = (short)(this.storageNutrient - 1);
    }

    public void decreaseStorageNutrient(int value) {
        this.storageNutrient = (short)(this.storageNutrient - value);
    }

    public boolean applyFertilizer(boolean manual) {
        if (this.storageNutrient >= 100) {
            return false;
        }
        this.storageNutrient = (short)(this.storageNutrient + (manual ? 100 : 90));
        return true;
    }

    public int getStorageWater() {
        return this.storageWater;
    }

    public void setStorageWater(int storageWater) {
        this.storageWater = (short)storageWater;
    }

    public void increaseStorageWater() {
        this.storageWater = (short)(this.storageWater + 1);
    }

    public void increaseStorageWater(int value) {
        this.storageWater = (short)(this.storageWater + value);
    }

    public void decreaseStorageWater() {
        this.storageWater = (short)(this.storageWater - 1);
    }

    public void decreaseStorageWater(int value) {
        this.storageWater = (short)(this.storageWater - value);
    }

    public boolean applyWater(FluidTank fluidTank) {
        if (this.storageWater >= 200) {
            return false;
        }
        int apply = 200 - this.storageWater;
        if (fluidTank.getFluid().getFluid() != FluidRegistry.WATER) {
            return false;
        }
        FluidStack drain = fluidTank.drain(apply, true);
        if (drain != null) {
            this.increaseStorageWater(drain.amount);
            return true;
        }
        return false;
    }

    public int getStorageWeedEX() {
        return this.storageWeedEX;
    }

    public void setStorageWeedEX(int storageWeedEX) {
        this.storageWeedEX = (short)storageWeedEX;
    }

    public void increaseStorageWeedEX() {
        this.storageWeedEX = (short)(this.storageWeedEX + 1);
    }

    public void increaseStorageWeedEX(int value) {
        this.storageWeedEX = (short)(this.storageWeedEX + value);
    }

    public void decreaseStorageWeedEX() {
        this.storageWeedEX = (short)(this.storageWeedEX - 1);
    }

    public void decreaseStorageWeedEX(int value) {
        this.storageWeedEX = (short)(this.storageWeedEX - value);
    }

    public boolean applyWeedEX(FluidTank fluidTank) {
        if (this.storageWeedEX >= 150) {
            return false;
        }
        int apply = 150 - this.storageWeedEX;
        if (fluidTank.getFluid().getFluid() != FluidName.weed_ex.getInstance()) {
            return false;
        }
        FluidStack drain = fluidTank.drain(apply, true);
        if (drain != null) {
            this.increaseStorageWeedEX(drain.amount);
            return true;
        }
        return false;
    }

    public int getTerrainHumidity() {
        return this.terrainHumidity;
    }

    public void setTerrainHumidity(int value) {
        this.terrainHumidity = (byte)value;
    }

    public void updateTerrainHumidity(World world, BlockPos pos) {
        int value = Crops.instance.getHumidityBiomeBonus(world.getBiomeGenForCoords(pos));
        if ((Integer)world.getBlockState(pos.down()).getValue((IProperty)BlockFarmland.MOISTURE) >= 7) {
            value += 2;
        }
        if (this.getStorageWater() >= 5) {
            value += 2;
        }
        this.setTerrainHumidity(value += (this.getStorageWater() + 24) / 25);
    }

    public int getTerrainNutrients() {
        return this.terrainNutrients;
    }

    public void setTerrainNutrients(int value) {
        this.terrainNutrients = (byte)value;
    }

    public void updateTerrainNutrients(World world, BlockPos pos) {
        int value = Crops.instance.getNutrientBiomeBonus(world.getBiomeGenForCoords(pos));
        for (int i = 1; i < 5 && world.getBlockState(pos.down(i)).getBlock() == Blocks.DIRT; ++i) {
            ++value;
        }
        this.setTerrainNutrients(value += (this.storageNutrient + 19) / 20);
    }

    public int getTerrainAirQuality() {
        return this.terrainAirQuality;
    }

    public void setTerrainAirQuality(int value) {
        this.terrainAirQuality = (byte)value;
    }

    public void updateTerrainAirQuality(World world, BlockPos pos) {
        int value = 0;
        int height = (pos.getY() - 64) / 15;
        if (height > 4) {
            height = 4;
        }
        if (height < 0) {
            height = 0;
        }
        value += height;
        int fresh = 9;
        for (int x = pos.getX() - 1; x < pos.getX() + 1 && fresh > 0; ++x) {
            for (int z = pos.getZ() - 1; z < pos.getZ() + 1 && fresh > 0; ++z) {
                if (!world.isBlockNormalCube(new BlockPos(x, pos.getY(), z), false) && !(world.getTileEntity(new BlockPos(x, pos.getY(), z)) instanceof TileEntityCrop)) continue;
                --fresh;
            }
        }
        value += fresh / 2;
        if (world.canSeeSky(pos.up())) {
            value += 2;
        }
        this.setTerrainAirQuality(value);
    }

    public int getCurrentSize() {
        return this.currentSize;
    }

    public void setCurrentSize(int currentSize) {
        this.currentSize = (byte)currentSize;
    }

    public void increaseCurrentSize() {
        this.currentSize = (byte)(this.currentSize + 1);
    }

    public void increaseCurrentSize(int value) {
        this.currentSize = (byte)(this.currentSize + value);
    }

    public void decreaseCurrentSize() {
        this.currentSize = (byte)(this.currentSize - 1);
    }

    public void decreaseCurrentSize(int value) {
        this.currentSize = (byte)(this.currentSize - value);
    }

    public int getGrowthPoints() {
        return this.growthPoints;
    }

    public void setGrowthPoints(int growthPoints) {
        this.growthPoints = (short)growthPoints;
    }

    public void increaseGrowthPoints() {
        this.growthPoints = (short)(this.growthPoints + 1);
    }

    public void increaseGrowthPoints(int value) {
        this.growthPoints = (short)(this.growthPoints + value);
    }

    public void decreaseGrowthPoints() {
        this.growthPoints = (short)(this.growthPoints - 1);
    }

    public void decreaseGrowthPoints(int value) {
        this.growthPoints = (short)(this.growthPoints - value);
    }

    public int getScanLevel() {
        return this.scanLevel;
    }

    public void setScanLevel(int scanLevel) {
        this.scanLevel = (byte)scanLevel;
    }

    public boolean isCrossingBase() {
        return this.crossingBase;
    }

    public void setCrossingBase(boolean crossingBase) {
        this.crossingBase = crossingBase;
    }
}

