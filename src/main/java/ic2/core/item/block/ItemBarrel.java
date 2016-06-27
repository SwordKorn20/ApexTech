/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package ic2.core.item.block;

import ic2.core.block.BlockScaffold;
import ic2.core.block.TileEntityBarrel;
import ic2.core.block.state.IIdProvider;
import ic2.core.init.Localization;
import ic2.core.item.ItemBooze;
import ic2.core.item.ItemIC2;
import ic2.core.item.block.ItemBlockTileEntity;
import ic2.core.ref.BlockName;
import ic2.core.ref.ItemName;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemBarrel
extends ItemIC2 {
    public ItemBarrel() {
        super(ItemName.barrel);
        this.setMaxStackSize(1);
    }

    @Override
    public String getItemStackDisplayName(ItemStack itemstack) {
        int v = ItemBooze.getAmountOfValue(itemstack.getItemDamage());
        if (v > 0) {
            return "" + v + Localization.translate("ic2.item.LBoozeBarrel");
        }
        return Localization.translate("ic2.item.EmptyBoozeBarrel");
    }

    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float a, float b, float c) {
        if (world.getBlockState(pos) == BlockName.scaffold.getBlockState(BlockScaffold.ScaffoldType.wood) && ItemBlockTileEntity.placeTeBlock(stack, (EntityLivingBase)player, world, pos, side, new TileEntityBarrel(stack.getItemDamage()))) {
            --stack.stackSize;
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }
}

