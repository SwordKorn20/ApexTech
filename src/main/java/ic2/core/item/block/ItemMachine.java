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

public class ItemMachine
extends ItemBlockIC2 {
    public ItemMachine(Block block) {
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
                return "ic2.blockMachine";
            }
            case 1: {
                return "ic2.blockIronFurnace";
            }
            case 2: {
                return "ic2.blockElecFurnace";
            }
            case 3: {
                return "ic2.blockMacerator";
            }
            case 4: {
                return "ic2.blockExtractor";
            }
            case 5: {
                return "ic2.blockCompressor";
            }
            case 6: {
                return "ic2.blockCanner";
            }
            case 7: {
                return "ic2.blockMiner";
            }
            case 8: {
                return "ic2.blockPump";
            }
            case 9: {
                return "ic2.blockMagnetizer";
            }
            case 10: {
                return "ic2.blockElectrolyzer";
            }
            case 11: {
                return "ic2.blockRecycler";
            }
            case 12: {
                return "ic2.blockAdvMachine";
            }
            case 13: {
                return "ic2.blockInduction";
            }
            case 14: {
                return "ic2.blockMatter";
            }
            case 15: {
                return "ic2.blockTerra";
            }
        }
        return null;
    }

    public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean b) {
        int meta = stack.getItemDamage();
        switch (meta) {
            case 2: {
                info.add(Localization.translate("ic2.item.tooltip.power") + " 3 EU/t, 32 EU/t " + Localization.translate("ic2.item.tooltip.max"));
                break;
            }
            case 3: {
                info.add(Localization.translate("ic2.item.tooltip.power") + " 2 EU/t, 32 EU/t " + Localization.translate("ic2.item.tooltip.max"));
                break;
            }
            case 4: {
                info.add(Localization.translate("ic2.item.tooltip.power") + " 2 EU/t, 32 EU/t " + Localization.translate("ic2.item.tooltip.max"));
                break;
            }
            case 5: {
                info.add(Localization.translate("ic2.item.tooltip.power") + " 2 EU/t, 32 EU/t " + Localization.translate("ic2.item.tooltip.max"));
                break;
            }
            case 6: {
                info.add(Localization.translate("ic2.item.tooltip.power") + " 4 EU/t, 32 EU/t " + Localization.translate("ic2.item.tooltip.max"));
                break;
            }
            case 8: {
                info.add(Localization.translate("ic2.item.tooltip.power") + " 4 EU/t, 32 EU/t " + Localization.translate("ic2.item.tooltip.max"));
                break;
            }
            case 9: {
                info.add(Localization.translate("ic2.item.tooltip.power") + " 32 EU/t " + Localization.translate("ic2.item.tooltip.max"));
                break;
            }
            case 11: {
                info.add(Localization.translate("ic2.item.tooltip.power") + " 1 EU/t, 32 EU/t " + Localization.translate("ic2.item.tooltip.max"));
                break;
            }
            case 13: {
                info.add(Localization.translate("ic2.item.tooltip.power") + " 1 EU/t, 128 EU/t " + Localization.translate("ic2.item.tooltip.max"));
                break;
            }
            case 14: {
                info.add(Localization.translate("ic2.item.tooltip.power") + " 512 EU/t " + Localization.translate("ic2.item.tooltip.max"));
                break;
            }
            case 15: {
                info.add(Localization.translate("ic2.item.tooltip.power") + " 512 EU/t " + Localization.translate("ic2.item.tooltip.max"));
            }
        }
    }
}

