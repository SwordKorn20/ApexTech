/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 */
package ic2.core.item.block;

import ic2.core.block.BlockBase;
import ic2.core.init.Localization;
import ic2.core.ref.BlockName;
import ic2.core.ref.IBlockModelProvider;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockIC2
extends ItemBlock {
    public ItemBlockIC2(Block block) {
        super(block);
    }

    public String getUnlocalizedName(ItemStack stack) {
        return this.getUnlocalizedName();
    }

    public String getItemStackDisplayName(ItemStack stack) {
        return Localization.translate(this.getUnlocalizedName(stack));
    }

    public boolean canHarvestBlock(IBlockState block, ItemStack stack) {
        return block.getBlock() == BlockName.scaffold.getInstance();
    }

    public EnumRarity getRarity(ItemStack stack) {
        if (this.block instanceof BlockBase) {
            return ((BlockBase)this.block).getRarity(stack);
        }
        return super.getRarity(stack);
    }
}

