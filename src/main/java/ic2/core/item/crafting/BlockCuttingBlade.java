/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 */
package ic2.core.item.crafting;

import ic2.api.item.IBlockCuttingBlade;
import ic2.core.init.Localization;
import ic2.core.item.ItemMulti;
import ic2.core.item.type.BlockCuttingBladeType;
import ic2.core.ref.ItemName;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class BlockCuttingBlade
extends ItemMulti<BlockCuttingBladeType>
implements IBlockCuttingBlade {
    public BlockCuttingBlade() {
        super(ItemName.block_cutting_blade, BlockCuttingBladeType.class);
    }

    @Override
    public int getHardness(ItemStack stack) {
        BlockCuttingBladeType blade = (BlockCuttingBladeType)this.getType(stack);
        if (blade == null) {
            return 0;
        }
        switch (blade) {
            case iron: {
                return 3;
            }
            case steel: {
                return 6;
            }
            case diamond: {
                return 9;
            }
        }
        return 0;
    }

    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        BlockCuttingBladeType blade = (BlockCuttingBladeType)this.getType(stack);
        if (blade == null) {
            return;
        }
        switch (blade) {
            case iron: {
                tooltip.add(Localization.translate("ic2.IronBlockCuttingBlade.info"));
                break;
            }
            case steel: {
                tooltip.add(Localization.translate("ic2.AdvIronBlockCuttingBlade.info"));
                break;
            }
            case diamond: {
                tooltip.add(Localization.translate("ic2.DiamondBlockCuttingBlade.info"));
            }
        }
        tooltip.add(Localization.translate("ic2.CuttingBlade.hardness", this.getHardness(stack)));
    }

}

