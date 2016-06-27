/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package ic2.core.item.reactor;

import ic2.api.reactor.IReactor;
import ic2.core.Ic2Items;
import ic2.core.item.reactor.AbstractDamageableReactorComponent;
import ic2.core.ref.ItemName;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemReactorLithiumCell
extends AbstractDamageableReactorComponent {
    public ItemReactorLithiumCell() {
        super(ItemName.lithium_fuel_rod, 10000);
    }

    @Override
    public boolean acceptUraniumPulse(ItemStack stack, IReactor reactor, ItemStack pulsingStack, int youX, int youY, int pulseX, int pulseY, boolean heatrun) {
        if (heatrun) {
            int myLevel = this.getCustomDamage(stack) + reactor.getHeat() / 3000;
            if (myLevel >= this.getMaxCustomDamage(stack)) {
                reactor.setItemAt(youX, youY, new ItemStack(Ic2Items.TritiumCell.getItem()));
            } else {
                this.setCustomDamage(stack, myLevel);
            }
        }
        return true;
    }

    @Override
    public double getDurabilityForDisplay(ItemStack stack) {
        return 1.0 - super.getDurabilityForDisplay(stack);
    }
}

