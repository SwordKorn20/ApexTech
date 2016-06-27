/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockGrass
 *  net.minecraft.block.BlockTallGrass
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.entity.player.PlayerCapabilities
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.ITickable
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.EnumSkyBlock
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.common.property.IUnlistedProperty
 *  net.minecraftforge.fluids.FluidTank
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  net.minecraftforge.oredict.OreDictionary
 */
package ic2.core.crop;

import ic2.api.crops.BaseSeed;
import ic2.api.crops.CropCard;
import ic2.api.crops.CropProperties;
import ic2.api.crops.Crops;
import ic2.api.crops.ICropTile;
import ic2.api.network.INetworkDataProvider;
import ic2.api.network.INetworkUpdateListener;
import ic2.core.IC2;
import ic2.core.Ic2Items;
import ic2.core.Ic2Player;
import ic2.core.Platform;
import ic2.core.block.state.IIdProvider;
import ic2.core.block.state.UnlistedProperty;
import ic2.core.crop.CropInternalInfo;
import ic2.core.crop.IC2Crops;
import ic2.core.init.Localization;
import ic2.core.item.ItemCropSeed;
import ic2.core.item.type.CropResItemType;
import ic2.core.network.NetworkManager;
import ic2.core.ref.IItemModelProvider;
import ic2.core.ref.ItemName;
import ic2.core.util.SideGateway;
import ic2.core.util.StackUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockTallGrass;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.IUnlistedProperty;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class TileEntityCrop
extends TileEntity
implements INetworkDataProvider,
INetworkUpdateListener,
ICropTile,
ITickable {
    public static final IUnlistedProperty<ModelResourceLocation> modelProperty = new UnlistedProperty<ModelResourceLocation>("model", ModelResourceLocation.class);
    private static final boolean debug = false;
    private CropCard crop = null;
    private final CropInternalInfo internalInfo = new CropInternalInfo();
    public NBTTagCompound customData = new NBTTagCompound();
    public char ticker = (char)IC2.random.nextInt(tickRate);
    public boolean dirty = true;
    public static int tickRate = 256;
    @SideOnly(value=Side.CLIENT)
    private volatile ModelResourceLocation model;

    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (nbt.hasKey("cropOwner") && nbt.hasKey("cropName")) {
            this.crop = Crops.instance.getCropCard(nbt.getString("cropOwner"), nbt.getString("cropName"));
        }
        if (nbt.hasKey("internalInfo")) {
            this.internalInfo.readFromNbt(nbt.getCompoundTag("internalInfo"));
        }
        if (nbt.hasKey("customData")) {
            this.customData = nbt.getCompoundTag("customData");
        }
    }

    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        if (this.crop != null) {
            nbt.setString("cropOwner", this.crop.getOwner());
            nbt.setString("cropName", this.crop.getName());
        }
        NBTTagCompound internalInfoTag = new NBTTagCompound();
        this.internalInfo.writeToNbt(internalInfoTag);
        nbt.setTag("internalInfo", (NBTBase)internalInfoTag);
        nbt.setTag("customData", (NBTBase)this.customData);
        return nbt;
    }

    public void update() {
        this.ticker = (char)(this.ticker + '\u0001');
        if (this.ticker % tickRate == 0) {
            this.tick();
        }
        if (this.dirty) {
            this.dirty = false;
            IBlockState state = this.worldObj.getBlockState(this.pos);
            this.worldObj.notifyBlockUpdate(this.pos, state, state, 3);
            this.worldObj.checkLightFor(EnumSkyBlock.BLOCK, this.pos);
            if (!this.worldObj.isRemote) {
                for (String field : this.getNetworkedFields()) {
                    IC2.network.get(true).updateTileEntityField(this, field);
                }
            }
        }
    }

    @Override
    public List<String> getNetworkedFields() {
        ArrayList<String> ret = new ArrayList<String>(4);
        ret.add("crop");
        ret.add("internalInfo");
        ret.add("customData");
        return ret;
    }

    public void tick() {
        if (this.worldObj.isRemote) {
            return;
        }
        if (this.ticker % (tickRate << 2) == 0) {
            this.internalInfo.updateTerrainHumidity(this.worldObj, this.pos);
        }
        if ((this.ticker + tickRate) % (tickRate << 2) == 0) {
            this.internalInfo.updateTerrainNutrients(this.worldObj, this.pos);
        }
        if ((this.ticker + tickRate * 2) % (tickRate << 2) == 0) {
            this.internalInfo.updateTerrainAirQuality(this.worldObj, this.pos);
        }
        if (this.crop == null) {
            if (!this.isCrossingBase() || !this.attemptCrossing()) {
                if (IC2.random.nextInt(100) == 0 && !this.hasEx()) {
                    this.reset();
                    this.crop = IC2Crops.weed;
                    this.setCurrentSize(1);
                } else {
                    if (this.getStorageWeedEX() > 0 && IC2.random.nextInt(10) == 0) {
                        this.internalInfo.decreaseStorageWeedEX();
                    }
                    return;
                }
            }
            assert (this.crop != null);
        }
        this.crop.tick(this);
        if (this.crop.canGrow(this)) {
            this.internalInfo.increaseGrowthPoints(this.calcGrowthRate());
            if (this.crop == null) {
                return;
            }
            if (this.internalInfo.getGrowthPoints() >= this.crop.getGrowthDuration(this)) {
                this.internalInfo.setGrowthPoints(0);
                this.internalInfo.increaseCurrentSize();
                this.dirty = true;
            }
        }
        if (this.internalInfo.getStorageNutrient() > 0) {
            this.internalInfo.decreaseStorageNutrient();
        }
        if (this.internalInfo.getStorageWater() > 0) {
            this.internalInfo.decreaseStorageWater();
        }
        if (this.crop.isWeed(this) && IC2.random.nextInt(50) - this.internalInfo.getStatGrowth() <= 2) {
            this.generateWeed();
        }
    }

    public void generateWeed() {
        BlockPos soilPos;
        Block block;
        BlockPos dstPos = this.pos.offset(EnumFacing.HORIZONTALS[IC2.random.nextInt(4)]);
        TileEntity dstRaw = this.worldObj.getTileEntity(dstPos);
        if (dstRaw instanceof TileEntityCrop) {
            TileEntityCrop teCrop = (TileEntityCrop)dstRaw;
            CropCard neighborCrop = teCrop.getCrop();
            if (neighborCrop == null || !neighborCrop.isWeed(teCrop) && IC2.random.nextInt(32) >= teCrop.getStatResistance() && !teCrop.hasEx()) {
                int newGrowth = Math.max(this.getStatGrowth(), teCrop.getStatGrowth());
                if (newGrowth < 31 && IC2.random.nextBoolean()) {
                    ++newGrowth;
                }
                teCrop.reset();
                teCrop.crop = IC2Crops.weed;
                teCrop.setCurrentSize(1);
                teCrop.setStatGrowth(newGrowth);
            }
        } else if (this.worldObj.isAirBlock(dstPos) && ((block = this.worldObj.getBlockState(soilPos = dstPos.down()).getBlock()) == Blocks.DIRT || block == Blocks.GRASS || block == Blocks.FARMLAND)) {
            this.worldObj.setBlockState(soilPos, Blocks.GRASS.getDefaultState(), 7);
            this.worldObj.setBlockState(dstPos, Blocks.TALLGRASS.getDefaultState(), 7);
        }
    }

    public boolean hasEx() {
        if (this.getStorageWeedEX() > 0) {
            this.internalInfo.decreaseStorageWeedEX(5);
            return true;
        }
        return false;
    }

    public boolean attemptCrossing() {
        if (IC2.random.nextInt(3) != 0) {
            return false;
        }
        ArrayList<TileEntityCrop> cropTes = new ArrayList<TileEntityCrop>(4);
        for (EnumFacing dir : EnumFacing.HORIZONTALS) {
            this.askCropJoinCross(this.pos.offset(dir), cropTes);
        }
        if (cropTes.size() < 2) {
            return false;
        }
        CropCard[] crops = Crops.instance.getCrops().toArray(new CropCard[0]);
        if (crops.length == 0) {
            return false;
        }
        int[] ratios = new int[crops.length];
        int total = 0;
        for (int i = 0; i < ratios.length; ++i) {
            CropCard crop = crops[i];
            if (crop.canGrow(this)) {
                for (TileEntityCrop te : cropTes) {
                    total += this.calculateRatioFor(crop, te.getCrop());
                }
            }
            ratios[i] = total;
        }
        int search = IC2.random.nextInt(total);
        int min = 0;
        int max = ratios.length - 1;
        while (min < max) {
            int cur = (min + max) / 2;
            int value = ratios[cur];
            if (search < value) {
                max = cur;
                continue;
            }
            min = cur + 1;
        }
        assert (min == max);
        assert (min >= 0 && min < ratios.length);
        assert (ratios[min] > search);
        assert (min == 0 || ratios[min - 1] <= search);
        this.internalInfo.setCrossingBase(false);
        this.crop = crops[min];
        this.dirty = true;
        this.internalInfo.setCurrentSize(1);
        this.internalInfo.mixStats(cropTes);
        return true;
    }

    public int calculateRatioFor(CropCard newCrop, CropCard oldCrop) {
        if (newCrop == oldCrop) {
            return 500;
        }
        int value = 0;
        int[] newCropProperties = newCrop.getProperties().getAllProperties();
        int[] oldCropProperties = oldCrop.getProperties().getAllProperties();
        for (int i = 0; i < 5; ++i) {
            int delta = Math.abs(newCropProperties[i] - oldCropProperties[i]);
            value += - delta + 2;
        }
        for (String attributeNew : newCrop.getAttributes()) {
            for (String attributeOld : oldCrop.getAttributes()) {
                if (!attributeNew.equalsIgnoreCase(attributeOld)) continue;
                value += 5;
            }
        }
        int diff = newCrop.getProperties().getTier() - oldCrop.getProperties().getTier();
        if (diff > 1) {
            value -= 2 * diff;
        }
        if (diff < -3) {
            value -= - diff;
        }
        return Math.max(value, 0);
    }

    public void askCropJoinCross(BlockPos nPos, List<TileEntityCrop> crops) {
        TileEntity teRaw = this.worldObj.getTileEntity(nPos);
        if (!(teRaw instanceof TileEntityCrop)) {
            return;
        }
        TileEntityCrop sideCrop = (TileEntityCrop)teRaw;
        CropCard neighborCrop = sideCrop.getCrop();
        if (neighborCrop == null) {
            return;
        }
        if (!neighborCrop.canGrow(this) || !neighborCrop.canCross(sideCrop)) {
            return;
        }
        int base = 4;
        if (sideCrop.getStatGrowth() >= 16) {
            ++base;
        }
        if (sideCrop.getStatGain() >= 30) {
            ++base;
        }
        if (sideCrop.getStatResistance() >= 28) {
            base += 27 - sideCrop.getStatResistance();
        }
        if (base >= IC2.random.nextInt(20)) {
            crops.add(sideCrop);
        }
    }

    public boolean leftClick(EntityPlayer player) {
        if (this.crop == null) {
            if (this.internalInfo.isCrossingBase()) {
                this.internalInfo.setCrossingBase(false);
                this.dirty = true;
                if (IC2.platform.isSimulating()) {
                    StackUtil.dropAsEntity(this.worldObj, this.pos, new ItemStack(Ic2Items.crop.getItem()));
                }
                return true;
            }
            return false;
        }
        return this.crop.onLeftClick(this, player);
    }

    @Override
    public boolean pick() {
        int i;
        if (this.crop == null) {
            return false;
        }
        boolean bonus = this.crop.canBeHarvested(this);
        float firstchance = this.crop.dropSeedChance(this);
        for (int i2 = 0; i2 < this.internalInfo.getStatResistance(); ++i2) {
            firstchance *= 1.1f;
        }
        int dropCount = 0;
        if (bonus) {
            if (this.worldObj.rand.nextFloat() <= (firstchance + 1.0f) * 0.8f) {
                ++dropCount;
            }
            float chance = this.crop.dropSeedChance(this) + (float)this.getStatGrowth() / 100.0f;
            for (i = 23; i < this.internalInfo.getStatGain(); ++i) {
                chance *= 0.95f;
            }
            if (this.worldObj.rand.nextFloat() <= chance) {
                ++dropCount;
            }
        } else if (this.worldObj.rand.nextFloat() <= firstchance * 1.5f) {
            ++dropCount;
        }
        ItemStack[] drops = new ItemStack[dropCount];
        for (i = 0; i < dropCount; ++i) {
            drops[i] = this.crop.getSeeds(this);
        }
        this.reset();
        if (!this.worldObj.isRemote && drops.length > 0) {
            for (ItemStack drop : drops) {
                if (drop.getItem() != ItemName.crop_seed_bag.getInstance()) {
                    drop.setTagCompound(null);
                }
                StackUtil.dropAsEntity(this.worldObj, this.pos, drop);
            }
        }
        return true;
    }

    public boolean rightClick(EntityPlayer player, ItemStack heldItem) {
        boolean creative = player.capabilities.isCreativeMode;
        if (heldItem != null) {
            if (this.crop == null) {
                if (heldItem.getItem() == ItemName.crop_seed_bag.getInstance() && !this.internalInfo.isCrossingBase()) {
                    if (!creative) {
                        --heldItem.stackSize;
                    }
                    this.internalInfo.setCrossingBase(true);
                    this.dirty = true;
                    return true;
                }
                if (this.applyBaseSeed(player)) {
                    return true;
                }
            }
            if (heldItem.getItem() == Items.WATER_BUCKET || heldItem.getItem() == Ic2Items.waterCell.getItem()) {
                if (this.internalInfo.getStorageWater() < 10) {
                    this.internalInfo.setStorageWater(10);
                    return true;
                }
                return heldItem.getItem() == Items.WATER_BUCKET;
            }
            if (heldItem.getItem() == Items.WHEAT_SEEDS) {
                if (this.internalInfo.getStorageNutrient() <= 50) {
                    this.internalInfo.increaseStorageNutrient(25);
                    --heldItem.stackSize;
                    return true;
                }
                return false;
            }
            if (heldItem.getItem() == Items.DYE && heldItem.getItemDamage() == 15 || StackUtil.checkItemEquality(heldItem, ItemName.crop_res.getItemStack(CropResItemType.fertilizer))) {
                if (this.applyFertilizer(true)) {
                    if (creative) {
                        return true;
                    }
                    --heldItem.stackSize;
                    return true;
                }
                return false;
            }
            if (heldItem.getItem() == Ic2Items.weedEx.getItem() && this.applyWeedEx(true)) {
                heldItem.damageItem(1, (EntityLivingBase)player);
                return true;
            }
        }
        if (this.crop == null) {
            return false;
        }
        return this.crop.onRightClick(this, player);
    }

    public boolean applyBaseSeed(EntityPlayer player) {
        BaseSeed seed;
        ItemStack current = player.getHeldItemMainhand();
        if (current == null) {
            current = player.getHeldItemOffhand();
        }
        if ((seed = Crops.instance.getBaseSeed(current)) != null) {
            if (current.stackSize < seed.stackSize) {
                return false;
            }
            if (this.tryPlantIn(seed.crop, seed.size, seed.statGrowth, seed.statGain, seed.statResistance, 1)) {
                if (player.capabilities.isCreativeMode) {
                    return true;
                }
                if (current.getItem().hasContainerItem(current)) {
                    if (current.stackSize > 1) {
                        return false;
                    }
                    player.inventory.mainInventory[player.inventory.currentItem] = current.getItem().getContainerItem(current);
                } else {
                    current.stackSize -= seed.stackSize;
                    if (current.stackSize <= 0) {
                        player.inventory.mainInventory[player.inventory.currentItem] = null;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean tryPlantIn(CropCard crop, int size, int statGr, int statGa, int statRe, int scan) {
        if (crop == null || crop == IC2Crops.weed || this.internalInfo.isCrossingBase()) {
            return false;
        }
        if (!crop.canGrow(this)) {
            return false;
        }
        this.reset();
        this.crop = crop;
        this.internalInfo.setCurrentSize(size);
        this.internalInfo.setStatGrowth(statGr);
        this.internalInfo.setStatGain(statGa);
        this.internalInfo.setStatResistance(statRe);
        this.internalInfo.setScanLevel(scan);
        return true;
    }

    public boolean applyFertilizer(boolean manual) {
        return this.internalInfo.applyFertilizer(manual);
    }

    public boolean applyWater(FluidTank fluidTank) {
        return this.internalInfo.applyWater(fluidTank);
    }

    public boolean applyWeedEX(FluidTank fluidTank) {
        return this.internalInfo.applyWeedEX(fluidTank);
    }

    public boolean applyWeedEx(boolean manual) {
        boolean triggerDecline;
        if (this.internalInfo.getStorageWeedEX() >= 100 && manual || this.internalInfo.getStorageWeedEX() >= 150) {
            return false;
        }
        this.internalInfo.increaseStorageWeedEX(50);
        if (manual) {
            triggerDecline = this.worldObj.rand.nextInt(5) == 0;
        } else {
            boolean bl = triggerDecline = this.worldObj.rand.nextInt(3) == 0;
        }
        if (this.crop != null && this.crop.isWeed(this) && this.internalInfo.getStorageWeedEX() >= 75 && triggerDecline) {
            switch (this.worldObj.rand.nextInt(5)) {
                case 0: {
                    if (this.internalInfo.getStatGrowth() > 0) {
                        this.internalInfo.decreaseStatGrowth();
                    }
                }
                case 1: {
                    if (this.internalInfo.getStatGain() <= 0) break;
                    this.internalInfo.decreaseStatGain();
                }
            }
            if (this.internalInfo.getStatResistance() > 0) {
                this.internalInfo.decreaseStatResistance();
            }
        }
        return true;
    }

    @Override
    public List<ItemStack> performHarvest() {
        if (this.crop == null) {
            return null;
        }
        if (!this.crop.canBeHarvested(this)) {
            return null;
        }
        double chance = this.crop.dropGainChance();
        int dropCount = (int)Math.max(0, Math.round(IC2.random.nextGaussian() * chance * 0.6827 + (chance *= Math.pow(1.03, this.getStatGain()))));
        ItemStack[] ret = new ItemStack[dropCount];
        for (int i = 0; i < dropCount; ++i) {
            ret[i] = this.crop.getGain(this);
            if (ret[i] == null || IC2.random.nextInt(100) > this.getStatGain()) continue;
            ++ret[i].stackSize;
        }
        this.setCurrentSize(this.crop.getSizeAfterHarvest(this));
        this.dirty = true;
        return Arrays.asList(ret);
    }

    @Override
    public boolean performManualHarvest() {
        List<ItemStack> drops = this.performHarvest();
        if (drops == null) {
            return false;
        }
        if (!this.worldObj.isRemote && !drops.isEmpty()) {
            for (ItemStack drop : drops) {
                StackUtil.dropAsEntity(this.worldObj, this.pos, drop);
            }
        }
        return true;
    }

    public void onNeighbourChange() {
        if (this.crop == null) {
            return;
        }
        this.crop.onNeighbourChange(this);
    }

    public boolean isRedstoneSignalEmitter() {
        return this.crop.isRedstoneSignalEmitter(this);
    }

    public int emitRedstone() {
        if (this.crop == null) {
            return 0;
        }
        if (!this.isRedstoneSignalEmitter()) {
            return 0;
        }
        return this.crop.getEmittedRedstoneSignal(this);
    }

    public void onBlockDestroyed() {
        if (this.crop == null) {
            return;
        }
        this.crop.onBlockDestroyed(this);
    }

    public int getEmittedLight() {
        if (this.crop == null) {
            return 0;
        }
        return this.crop.getEmittedLight(this);
    }

    public int calcGrowthRate() {
        int have;
        if (this.crop == null) {
            return 0;
        }
        int base = 3 + IC2.random.nextInt(7) + this.getStatGrowth();
        int need = (this.crop.getProperties().getTier() - 1) * 4 + this.getStatGrowth() + this.getStatGain() + this.getStatResistance();
        if (need < 0) {
            need = 0;
        }
        if ((have = this.crop.getWeightInfluences(this, this.getHumidity(), this.getNutrients(), this.getAirQuality()) * 5) >= need) {
            base = base * (100 + (have - need)) / 100;
        } else {
            int neg = (need - have) * 4;
            if (neg > 100 && IC2.random.nextInt(32) > this.getStatResistance()) {
                this.reset();
                base = 0;
            } else if ((base = base * (100 - neg) / 100) < 0) {
                base = 0;
            }
        }
        return base;
    }

    public void calculateTrampling() {
        if (this.worldObj.isRemote) {
            return;
        }
        if (IC2.random.nextInt(100) == 0 && IC2.random.nextInt(40) > this.getScanLevel()) {
            this.reset();
            this.worldObj.setBlockState(this.pos.down(), Blocks.DIRT.getDefaultState(), 7);
        }
    }

    public void onEntityCollision(Entity entity) {
        if (this.crop == null) {
            return;
        }
        if (this.crop.onEntityCollision(this, entity)) {
            this.calculateTrampling();
        }
    }

    @Override
    public void reset() {
        this.crop = null;
        this.customData = new NBTTagCompound();
        this.dirty = true;
        this.internalInfo.resetCrop();
    }

    @Override
    public void updateState() {
        this.dirty = true;
    }

    public String getScanned() {
        if (this.crop == null) {
            return null;
        }
        int scanLevel = this.getScanLevel();
        if (scanLevel <= 0) {
            return null;
        }
        String name = Localization.translate(this.crop.getDisplayName());
        if (scanLevel >= 4) {
            return "Hello";
        }
        return "Hello";
    }

    @Override
    public boolean isBlockBelow(Block reqBlock) {
        if (this.crop == null) {
            return false;
        }
        for (int i = 1; i < this.crop.getRootsLength(this); ++i) {
            BlockPos blockPos = this.pos.down(i);
            IBlockState state = this.worldObj.getBlockState(blockPos);
            Block block = state.getBlock();
            if (block.isAir(state, (IBlockAccess)this.worldObj, blockPos)) {
                return false;
            }
            if (block != reqBlock) continue;
            return true;
        }
        return false;
    }

    @Override
    public boolean isBlockBelow(String oreDictionaryEntry) {
        if (this.crop == null) {
            return false;
        }
        for (int i = 1; i < this.crop.getRootsLength(this); ++i) {
            BlockPos blockPos = this.pos.down(i);
            IBlockState state = this.worldObj.getBlockState(blockPos);
            Block block = state.getBlock();
            if (block.isAir(state, (IBlockAccess)this.worldObj, blockPos)) {
                return false;
            }
            ItemStack stackBelow = StackUtil.getPickStack(this.worldObj, blockPos, state, Ic2Player.get(this.worldObj));
            for (ItemStack stack : OreDictionary.getOres((String)oreDictionaryEntry)) {
                if (!StackUtil.checkItemEquality(stackBelow, stack)) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public ItemStack generateSeeds(CropCard crop, int growth, int gain, int resistance, int scan) {
        return ItemCropSeed.generateItemStackFromValues(crop, growth, gain, resistance, scan);
    }

    @Override
    public void onNetworkUpdate(String field) {
        this.dirty = true;
        if (this.crop == null) {
            new ModelResourceLocation("ic2:ic2crop");
        }
        ResourceLocation rl = this.crop.getModelLocation().get(this.getCurrentSize() - 1);
        this.model = new ModelResourceLocation(rl.getResourceDomain() + ":" + rl.getResourcePath());
    }

    @Override
    public CropCard getCrop() {
        return this.crop;
    }

    @Override
    public NBTTagCompound getCustomData() {
        return this.customData;
    }

    @Override
    public int getLightLevel() {
        return this.worldObj.getLightFromNeighbors(this.pos);
    }

    @Override
    public void setCrop(CropCard cropCard) {
        this.crop = cropCard;
        this.dirty = true;
    }

    @Override
    public int getCurrentSize() {
        return this.internalInfo.getCurrentSize();
    }

    @Override
    public void setCurrentSize(int currentSize) {
        this.internalInfo.setCurrentSize(currentSize);
    }

    @Override
    public int getStatGrowth() {
        return this.internalInfo.getStatGrowth();
    }

    @Override
    public void setStatGrowth(int statGrowth) {
        this.internalInfo.setStatGrowth(statGrowth);
    }

    @Override
    public int getStatGain() {
        return this.internalInfo.getStatGain();
    }

    @Override
    public void setStatGain(int statGain) {
        this.internalInfo.setStatGain(statGain);
    }

    @Override
    public int getStatResistance() {
        return this.internalInfo.getStatResistance();
    }

    @Override
    public void setStatResistance(int statResistance) {
        this.internalInfo.setStatResistance(statResistance);
    }

    @Override
    public int getStorageNutrient() {
        return this.internalInfo.getStorageNutrient();
    }

    @Override
    public void setStorageNutrient(int storageNutrient) {
        this.internalInfo.setStorageNutrient(storageNutrient);
    }

    @Override
    public int getStorageWater() {
        return this.internalInfo.getStorageWater();
    }

    @Override
    public void setStorageWater(int storageWater) {
        this.internalInfo.setStorageWater(storageWater);
    }

    @Override
    public int getStorageWeedEX() {
        return this.internalInfo.getStorageWeedEX();
    }

    @Override
    public void setStorageWeedEX(int storageWeedEX) {
        this.internalInfo.setStorageWeedEX(storageWeedEX);
    }

    @Override
    public int getHumidity() {
        return this.internalInfo.getTerrainHumidity();
    }

    @Override
    public int getNutrients() {
        return this.internalInfo.getTerrainNutrients();
    }

    @Override
    public int getAirQuality() {
        return this.internalInfo.getTerrainAirQuality();
    }

    @Override
    public int getScanLevel() {
        return this.internalInfo.getScanLevel();
    }

    @Override
    public void setScanLevel(int scanLevel) {
        this.internalInfo.setScanLevel(scanLevel);
    }

    @Override
    public int getGrowthPoints() {
        return this.internalInfo.getGrowthPoints();
    }

    @Override
    public void setGrowthPoints(int growthPoints) {
        this.internalInfo.setGrowthPoints(growthPoints);
    }

    @Override
    public boolean isCrossingBase() {
        return this.internalInfo.isCrossingBase();
    }

    @Override
    public void setCrossingBase(boolean crossingBase) {
        this.internalInfo.setCrossingBase(crossingBase);
    }

    @Override
    public World getWorld() {
        return this.worldObj;
    }

    @Override
    public BlockPos getLocation() {
        return this.pos;
    }
}

