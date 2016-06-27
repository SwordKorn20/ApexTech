/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.inventory.Container
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.item.tool;

import ic2.api.crops.CropCard;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.Platform;
import ic2.core.crop.TileEntityCrop;
import ic2.core.item.BaseElectricItem;
import ic2.core.item.IHandHeldInventory;
import ic2.core.item.tool.ContainerCropnalyzer;
import ic2.core.item.tool.HandHeldCropnalyzer;
import ic2.core.ref.ItemName;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCropnalyzer
extends BaseElectricItem
implements IHandHeldInventory {
    public ItemCropnalyzer() {
        super(ItemName.cropnalyzer, 100000.0, 128.0, 2);
    }

    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if (IC2.platform.isSimulating()) {
            IC2.platform.launchGui(player, this.getInventory(player, stack));
        }
        return new ActionResult(EnumActionResult.SUCCESS, (Object)stack);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }

    @Override
    public IHasGui getInventory(EntityPlayer player, ItemStack stack) {
        return new HandHeldCropnalyzer(player, stack);
    }

    public boolean onDroppedByPlayer(ItemStack stack, EntityPlayer player) {
        HandHeldCropnalyzer cropnalyzer;
        if (player instanceof EntityPlayerMP && stack != null && player.openContainer instanceof ContainerCropnalyzer && (cropnalyzer = (HandHeldCropnalyzer)((ContainerCropnalyzer)player.openContainer).base).isThisContainer(stack)) {
            ((EntityPlayerMP)player).closeScreen();
        }
        return true;
    }

    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        TileEntityCrop crop;
        if (world.isRemote || player.isSneaking()) {
            return EnumActionResult.PASS;
        }
        TileEntity te = world.getTileEntity(pos);
        if (te != null && te instanceof TileEntityCrop && (crop = (TileEntityCrop)te).getCrop() == null) {
            return EnumActionResult.PASS;
        }
        return EnumActionResult.PASS;
    }
}

