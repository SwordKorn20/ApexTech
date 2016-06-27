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

public class ItemMachine3
extends ItemBlockIC2 {
    public ItemMachine3(Block block) {
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
                return "ic2.blockSteamGenerator";
            }
            case 1: {
                return "ic2.blockBlastFurnace";
            }
            case 2: {
                return "ic2.blockBlockCutter";
            }
            case 3: {
                return "ic2.blockSolarDestiller";
            }
            case 4: {
                return "ic2.blockFluidDistributor";
            }
            case 5: {
                return "ic2.blockSortingMachine";
            }
            case 6: {
                return "ic2.blockItemBuffer";
            }
            case 7: {
                return "ic2.blockCropHavester";
            }
            case 8: {
                return "ic2.blockLathe";
            }
        }
        return null;
    }

    public void addInformation(ItemStack stack, EntityPlayer player, List info, boolean b) {
        int meta = stack.getItemDamage();
        switch (meta) {
            case 0: {
                info.add(Localization.translate("ic2.item.tooltip.power") + " 1-x HU/t");
                break;
            }
            case 1: {
                info.add(Localization.translate("ic2.item.tooltip.power") + " 1 HU/t");
                break;
            }
            case 2: {
                info.add(Localization.translate("ic2.item.tooltip.power") + " 128 EU/t, 128 EU/t " + Localization.translate("ic2.item.tooltip.max"));
                break;
            }
            case 5: {
                info.add(Localization.translate("ic2.item.tooltip.power") + " 20-x EU/t, 128 EU/t " + Localization.translate("ic2.item.tooltip.max"));
                break;
            }
            case 7: {
                info.add(Localization.translate("ic2.item.tooltip.power") + " 1-201 EU/t, 32 EU/t " + Localization.translate("ic2.item.tooltip.max"));
                break;
            }
            case 8: {
                info.add(Localization.translate("ic2.item.tooltip.power") + " 1000 KU");
            }
        }
    }
}

