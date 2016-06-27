/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 */
package ic2.core.item.reactor;

import ic2.api.reactor.IReactor;
import ic2.core.init.Localization;
import ic2.core.item.reactor.AbstractDamageableReactorComponent;
import ic2.core.ref.ItemName;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ItemReactorHeatStorage
extends AbstractDamageableReactorComponent {
    public ItemReactorHeatStorage(ItemName name, int heatStorage) {
        super(name, heatStorage);
    }

    @Override
    public boolean canStoreHeat(ItemStack stack, IReactor reactor, int x, int y) {
        return true;
    }

    @Override
    public int getMaxHeat(ItemStack stack, IReactor reactor, int x, int y) {
        return this.getMaxCustomDamage(stack);
    }

    @Override
    public int getCurrentHeat(ItemStack stack, IReactor reactor, int x, int y) {
        return this.getCustomDamage(stack);
    }

    @Override
    public int alterHeat(ItemStack stack, IReactor reactor, int x, int y, int heat) {
        int myHeat = this.getCurrentHeat(stack, reactor, x, y);
        int max = this.getMaxHeat(stack, reactor, x, y);
        if ((myHeat += heat) > max) {
            reactor.setItemAt(x, y, null);
            heat = max - myHeat + 1;
        } else {
            if (myHeat < 0) {
                heat = myHeat;
                myHeat = 0;
            } else {
                heat = 0;
            }
            this.setCustomDamage(stack, myHeat);
        }
        return heat;
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        if (this.getCustomDamage(stack) > 0) {
            tooltip.add(Localization.translate("ic2.reactoritem.heatwarning.line1"));
            tooltip.add(Localization.translate("ic2.reactoritem.heatwarning.line2"));
        }
    }
}

