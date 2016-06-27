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
import ic2.core.init.MainConfig;
import ic2.core.item.block.ItemBlockIC2;
import ic2.core.util.ConfigUtil;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemGenerator
extends ItemBlockIC2 {
    public ItemGenerator(Block block) {
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
                return "ic2.blockGenerator";
            }
            case 1: {
                return "ic2.blockGeoGenerator";
            }
            case 2: {
                return "ic2.blockWaterGenerator";
            }
            case 3: {
                return "ic2.blockSolarGenerator";
            }
            case 4: {
                return "ic2.blockWindGenerator";
            }
            case 5: {
                return "ic2.blockNuclearReactor";
            }
            case 6: {
                return "ic2.blockRTGenerator";
            }
            case 7: {
                return "ic2.blockSemifluidGenerator";
            }
            case 8: {
                return "ic2.blockStirlingGenerator";
            }
            case 9: {
                return "ic2.blockKineticGenerator";
            }
        }
        return null;
    }

    public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean b) {
        int meta = stack.getItemDamage();
        switch (meta) {
            case 0: {
                info.add(Localization.translate("ic2.item.tooltip.PowerOutput") + " " + Math.round(10.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/generator")) + " EU/t " + Localization.translate("ic2.item.tooltip.max"));
                break;
            }
            case 1: {
                info.add(Localization.translate("ic2.item.tooltip.PowerOutput") + " " + Math.round(20.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/geothermal")) + " EU/t " + Localization.translate("ic2.item.tooltip.max"));
                break;
            }
            case 2: {
                info.add(Localization.translate("ic2.item.tooltip.PowerOutput") + " 0-2 EU/t " + Localization.translate("ic2.item.tooltip.max"));
                break;
            }
            case 3: {
                info.add(Localization.translate("ic2.item.tooltip.PowerOutput") + " 1 EU/t " + Localization.translate("ic2.item.tooltip.max"));
                break;
            }
            case 4: {
                info.add(Localization.translate("ic2.item.tooltip.PowerOutput") + " 0-5 EU/t " + Localization.translate("ic2.item.tooltip.max"));
                break;
            }
            case 5: {
                info.add(Localization.translate("ic2.item.tooltip.PowerOutput") + " 1-8196 EU/t " + Localization.translate("ic2.item.tooltip.max"));
                break;
            }
            case 6: {
                info.add(Localization.translate("ic2.item.tooltip.PowerOutput") + " 1-32 EU/t " + Localization.translate("ic2.item.tooltip.max"));
                break;
            }
            case 7: {
                info.add(Localization.translate("ic2.item.tooltip.PowerOutput") + " 8-32 EU/t " + Localization.translate("ic2.item.tooltip.max"));
                break;
            }
            case 8: {
                info.add(Localization.translate("ic2.item.tooltip.PowerOutput") + " 1-50 EU/t " + Localization.translate("ic2.item.tooltip.max"));
                break;
            }
            case 9: {
                info.add(Localization.translate("ic2.item.tooltip.PowerOutput") + " 1-512 EU/t " + Localization.translate("ic2.item.tooltip.max"));
            }
        }
    }
}

