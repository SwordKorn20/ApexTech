/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.entity.player.PlayerCapabilities
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package ic2.core.item;

import ic2.api.crops.CropCard;
import ic2.api.crops.Crops;
import ic2.api.crops.ICropSeed;
import ic2.core.crop.TileEntityCrop;
import ic2.core.init.Localization;
import ic2.core.item.ItemIC2;
import ic2.core.ref.ItemName;
import ic2.core.util.Util;
import java.util.Collection;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemCropSeed
extends ItemIC2
implements ICropSeed {
    public ItemCropSeed() {
        super(ItemName.crop_seed_bag);
        this.setMaxStackSize(1);
        if (!Util.inDev()) {
            this.setCreativeTab(null);
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        if (itemstack == null) {
            return "ic2.crop.unknown";
        }
        CropCard cropCard = Crops.instance.getCropCard(itemstack);
        int level = this.getScannedFromStack(itemstack);
        if (level == 0) {
            return "ic2.crop.unknown";
        }
        if (level < 0 || cropCard == null) {
            return "ic2.crop.invalid";
        }
        return cropCard.getDisplayName();
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        return Localization.translate("ic2.crop.seeds", super.getItemStackDisplayName(stack));
    }

    public boolean isDamageable() {
        return true;
    }

    public boolean isRepairable() {
        return false;
    }

    public void addInformation(ItemStack stack, EntityPlayer player, List<String> info, boolean debugTooltips) {
        if (this.getScannedFromStack(stack) >= 4) {
            info.add("\u00a72Gr\u00a77 " + this.getGrowthFromStack(stack));
            info.add("\u00a76Ga\u00a77 " + this.getGainFromStack(stack));
            info.add("\u00a73Re\u00a77 " + this.getResistanceFromStack(stack));
        }
    }

    public EnumActionResult onItemUse(ItemStack itemstack, EntityPlayer entityplayer, World world, BlockPos pos, EnumHand hand, EnumFacing side, float a, float b, float c) {
        TileEntityCrop crop;
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityCrop && (crop = (TileEntityCrop)te).tryPlantIn(Crops.instance.getCropCard(itemstack), 1, this.getGrowthFromStack(itemstack), this.getGainFromStack(itemstack), this.getResistanceFromStack(itemstack), this.getScannedFromStack(itemstack))) {
            if (!entityplayer.capabilities.isCreativeMode) {
                entityplayer.inventory.mainInventory[entityplayer.inventory.currentItem] = null;
            }
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

    public void getSubItems(Item item, CreativeTabs tabs, List items) {
        for (CropCard crop : Crops.instance.getCrops()) {
            items.add(ItemCropSeed.generateItemStackFromValues(crop, 1, 1, 1, 4));
        }
    }

    public static ItemStack generateItemStackFromValues(CropCard crop, int statGrowth, int statGain, int statResistance, int scan) {
        ItemStack stack = ItemName.crop_seed_bag.getItemStack();
        NBTTagCompound tag = new NBTTagCompound();
        tag.setString("owner", crop.getOwner());
        tag.setString("name", crop.getName());
        tag.setByte("growth", (byte)statGrowth);
        tag.setByte("gain", (byte)statGain);
        tag.setByte("resistance", (byte)statResistance);
        tag.setByte("scan", (byte)scan);
        stack.setTagCompound(tag);
        return stack;
    }

    @Override
    public CropCard getCropFromStack(ItemStack is) {
        if (is.getTagCompound() == null) {
            return null;
        }
        return Crops.instance.getCropCard(is.getTagCompound().getString("owner"), is.getTagCompound().getString("name"));
    }

    @Override
    public void setCropFromStack(ItemStack is, CropCard crop) {
        if (is.getTagCompound() == null) {
            return;
        }
        is.getTagCompound().setString("owner", crop.getOwner());
        is.getTagCompound().setString("name", crop.getName());
    }

    @Override
    public int getGrowthFromStack(ItemStack is) {
        if (is.getTagCompound() == null) {
            return -1;
        }
        return is.getTagCompound().getByte("growth");
    }

    @Override
    public void setGrowthFromStack(ItemStack is, int value) {
        if (is.getTagCompound() == null) {
            return;
        }
        is.getTagCompound().setByte("growth", (byte)value);
    }

    @Override
    public int getGainFromStack(ItemStack is) {
        if (is.getTagCompound() == null) {
            return -1;
        }
        return is.getTagCompound().getByte("gain");
    }

    @Override
    public void setGainFromStack(ItemStack is, int value) {
        if (is.getTagCompound() == null) {
            return;
        }
        is.getTagCompound().setByte("gain", (byte)value);
    }

    @Override
    public int getResistanceFromStack(ItemStack is) {
        if (is.getTagCompound() == null) {
            return -1;
        }
        return is.getTagCompound().getByte("resistance");
    }

    @Override
    public void setResistanceFromStack(ItemStack is, int value) {
        if (is.getTagCompound() == null) {
            return;
        }
        is.getTagCompound().setByte("resistance", (byte)value);
    }

    @Override
    public int getScannedFromStack(ItemStack is) {
        if (is.getTagCompound() == null) {
            return -1;
        }
        return is.getTagCompound().getByte("scan");
    }

    @Override
    public void setScannedFromStack(ItemStack is, int value) {
        if (is.getTagCompound() == null) {
            return;
        }
        is.getTagCompound().setByte("scan", (byte)value);
    }

    @Override
    public void incrementScannedFromStack(ItemStack is) {
        if (is.getTagCompound() == null) {
            return;
        }
        is.getTagCompound().setByte("scan", (byte)(this.getScannedFromStack(is) + 1));
    }
}

