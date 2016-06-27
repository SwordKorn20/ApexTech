/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package ic2.core.item.tool;

import ic2.api.crops.CropCard;
import ic2.core.IC2;
import ic2.core.Platform;
import ic2.core.block.state.IIdProvider;
import ic2.core.crop.IC2Crops;
import ic2.core.crop.TileEntityCrop;
import ic2.core.item.ItemIC2;
import ic2.core.item.type.CropResItemType;
import ic2.core.ref.ItemName;
import ic2.core.util.StackUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemWeedingTrowel
extends ItemIC2 {
    public ItemWeedingTrowel() {
        super(ItemName.weeding_trowel);
        this.setMaxStackSize(1);
    }

    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        TileEntityCrop tileEntityCrop;
        if (!IC2.platform.isSimulating()) {
            return EnumActionResult.PASS;
        }
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity instanceof TileEntityCrop && (tileEntityCrop = (TileEntityCrop)tileEntity).getCrop() == IC2Crops.weed) {
            StackUtil.dropAsEntity(world, pos, StackUtil.copyWithSize(ItemName.crop_res.getItemStack(CropResItemType.weed), tileEntityCrop.getCurrentSize()));
            tileEntityCrop.reset();
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }
}

