/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package ic2.core.item.block;

import ic2.core.block.BlockMultiID;
import ic2.core.block.state.EnumProperty;
import ic2.core.block.state.IIdProvider;
import ic2.core.item.block.ItemBlockIC2;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemBlockMulti
extends ItemBlockIC2 {
    public ItemBlockMulti(Block block) {
        super(block);
        this.setHasSubtypes(true);
    }

    public int getMetadata(int damage) {
        return damage;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        String name = ((IIdProvider)((Object)((Enum)((Object)this.block.getStateFromMeta(stack.getMetadata()).getValue(((BlockMultiID)this.block).getTypeProperty()))))).getName();
        return super.getUnlocalizedName(stack) + "." + name;
    }
}

