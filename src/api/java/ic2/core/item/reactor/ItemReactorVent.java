/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 */
package ic2.core.item.reactor;

import ic2.api.reactor.IReactor;
import ic2.core.item.reactor.ItemReactorHeatStorage;
import ic2.core.ref.ItemName;
import net.minecraft.item.ItemStack;

public class ItemReactorVent
extends ItemReactorHeatStorage {
    public final int selfVent;
    public final int reactorVent;

    public ItemReactorVent(ItemName name, int heatStorage, int selfvent, int reactorvent) {
        super(name, heatStorage);
        this.selfVent = selfvent;
        this.reactorVent = reactorvent;
    }

    @Override
    public void processChamber(ItemStack stack, IReactor reactor, int x, int y, boolean heatrun) {
        if (heatrun) {
            int self;
            if (this.reactorVent > 0) {
                int rheat = reactor.getHeat();
                int reactorDrain = rheat;
                if (reactorDrain > this.reactorVent) {
                    reactorDrain = this.reactorVent;
                }
                rheat -= reactorDrain;
                if ((reactorDrain = this.alterHeat(stack, reactor, x, y, reactorDrain)) > 0) {
                    return;
                }
                reactor.setHeat(rheat);
            }
            if ((self = this.alterHeat(stack, reactor, x, y, - this.selfVent)) <= 0) {
                reactor.addEmitHeat(self + this.selfVent);
            }
        }
    }
}

