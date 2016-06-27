/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package ic2.core.item.reactor;

import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorComponent;
import ic2.core.item.reactor.AbstractReactorComponent;
import ic2.core.ref.ItemName;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemReactorIridiumReflector
extends AbstractReactorComponent {
    public ItemReactorIridiumReflector(ItemName name) {
        super(name);
    }

    @Override
    public boolean acceptUraniumPulse(ItemStack stack, IReactor reactor, ItemStack pulsingStack, int youX, int youY, int pulseX, int pulseY, boolean heatrun) {
        if (!heatrun) {
            IReactorComponent source = (IReactorComponent)pulsingStack.getItem();
            source.acceptUraniumPulse(pulsingStack, reactor, stack, pulseX, pulseY, youX, youY, heatrun);
        }
        return true;
    }

    @Override
    public float influenceExplosion(ItemStack stack, IReactor reactor) {
        return -1.0f;
    }
}

