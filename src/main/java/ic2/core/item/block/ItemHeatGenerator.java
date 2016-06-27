/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package ic2.core.item.block;

import ic2.core.init.Localization;
import ic2.core.item.block.ItemBlockIC2;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemHeatGenerator
extends ItemBlockIC2 {
    public ItemHeatGenerator(Block block) {
        super(block);
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    public int getMetadata(int i) {
        return i;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemstack) {
        int meta = itemstack.getItemDamage();
        switch (meta) {
            case 0: {
                return "ic2.blockSolidHeatGenerator";
            }
            case 1: {
                return "ic2.blockFluidHeatGenerator";
            }
            case 2: {
                return "ic2.blockRTHeatGenerator";
            }
            case 3: {
                return "ic2.blockElectricHeatGenerator";
            }
        }
        return null;
    }

    public void addInformation(ItemStack stack, EntityPlayer player, List<String> info, boolean b) {
        int meta = stack.getItemDamage();
        switch (meta) {
            case 0: {
                info.add(Localization.translate("ic2.item.tooltip.PowerOutput") + " 20 HU/t " + Localization.translate("ic2.item.tooltip.max"));
                break;
            }
            case 1: {
                info.add(Localization.translate("ic2.item.tooltip.PowerOutput") + " 8-32 HU/t " + Localization.translate("ic2.item.tooltip.max"));
                break;
            }
            case 2: {
                info.add(Localization.translate("ic2.item.tooltip.PowerOutput") + " 1-32 HU/t " + Localization.translate("ic2.item.tooltip.max"));
                break;
            }
            case 3: {
                info.add(Localization.translate("ic2.item.tooltip.PowerOutput") + " 0-100 HU/t " + Localization.translate("ic2.item.tooltip.max"));
                info.add(Localization.translate("ic2.item.tooltip.power") + " 0-100 EU/t " + Localization.translate("ic2.item.tooltip.max"));
            }
        }
    }
}

