/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemStack
 */
package ic2.core.block.reactor.tileentity;

import ic2.core.block.reactor.tileentity.TileEntityAbstractRCI;
import ic2.core.ref.ItemName;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;

public class TileEntityRCI_LZH
extends TileEntityAbstractRCI {
    public TileEntityRCI_LZH() {
        super(ItemName.lzh_condensator.getItemStack(), new ItemStack(Blocks.LAPIS_BLOCK));
    }
}

