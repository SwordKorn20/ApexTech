/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockLeaves
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyEnum
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemLeaves
 *  net.minecraft.item.ItemStack
 */
package ic2.core.item.block;

import ic2.core.block.Ic2Leaves;
import ic2.core.init.Localization;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLeaves;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemLeaves;
import net.minecraft.item.ItemStack;

public class ItemIc2Leaves
extends ItemLeaves {
    public ItemIc2Leaves(Block block) {
        super((BlockLeaves)block);
        this.setHasSubtypes(false);
    }

    public String getUnlocalizedName() {
        return "ic2." + super.getUnlocalizedName().substring(5);
    }

    public String getUnlocalizedName(ItemStack stack) {
        return this.getUnlocalizedName() + "." + ((Ic2Leaves.LeavesType)((Object)this.block.getStateFromMeta(stack.getMetadata()).getValue(Ic2Leaves.typeProperty))).getName();
    }

    public String getItemStackDisplayName(ItemStack stack) {
        return Localization.translate(this.getUnlocalizedName(stack));
    }
}

