/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package ic2.core.block.invslot;

import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.Redstone;
import ic2.core.block.comp.TileEntityComponent;
import ic2.core.block.invslot.InvSlot;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.IUpgradeItem;
import ic2.core.upgrade.UpgradableProperty;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class InvSlotUpgrade
extends InvSlot {
    public int augmentation;
    public int extraProcessTime;
    public double processTimeMultiplier;
    public int extraEnergyDemand;
    public double energyDemandMultiplier;
    public int extraEnergyStorage;
    public double energyStorageMultiplier;
    public int extraTier;
    private List<Redstone.IRedstoneModifier> redstoneModifiers = Collections.emptyList();

    public InvSlotUpgrade(TileEntityInventory base1, String name1, int count) {
        super(base1, name1, InvSlot.Access.NONE, count);
        if (!(base1 instanceof IUpgradableBlock)) {
            throw new IllegalArgumentException("Base needs to be an IUpgradableBlock.");
        }
        this.resetRates();
    }

    @Override
    public boolean accepts(ItemStack stack) {
        Item rawItem = stack.getItem();
        if (!(rawItem instanceof IUpgradeItem)) {
            return false;
        }
        IUpgradeItem item = (IUpgradeItem)rawItem;
        return item.isSuitableFor(stack, ((IUpgradableBlock)((Object)this.base)).getUpgradableProperties());
    }

    @Override
    public void onChanged() {
        this.resetRates();
        IUpgradableBlock block = (IUpgradableBlock)((Object)this.base);
        List newRedstoneModifiers = new ArrayList<Redstone.IRedstoneModifier>();
        for (int i = 0; i < this.size(); ++i) {
            ItemStack stack = this.get(i);
            if (stack == null || !this.accepts(stack)) continue;
            IUpgradeItem upgrade = (IUpgradeItem)stack.getItem();
            this.augmentation += upgrade.getAugmentation(stack, block) * stack.stackSize;
            this.extraProcessTime += upgrade.getExtraProcessTime(stack, block) * stack.stackSize;
            this.processTimeMultiplier *= Math.pow(upgrade.getProcessTimeMultiplier(stack, block), stack.stackSize);
            this.extraEnergyDemand += upgrade.getExtraEnergyDemand(stack, block) * stack.stackSize;
            this.energyDemandMultiplier *= Math.pow(upgrade.getEnergyDemandMultiplier(stack, block), stack.stackSize);
            this.extraEnergyStorage += upgrade.getExtraEnergyStorage(stack, block) * stack.stackSize;
            this.energyStorageMultiplier *= Math.pow(upgrade.getEnergyStorageMultiplier(stack, block), stack.stackSize);
            this.extraTier += upgrade.getExtraTier(stack, block) * stack.stackSize;
            if (!upgrade.modifiesRedstoneInput(stack, block)) continue;
            newRedstoneModifiers.add(new UpgradeRedstoneModifier(upgrade, stack, block));
        }
        for (TileEntityComponent component : this.base.getComponents()) {
            if (!(component instanceof Redstone)) continue;
            Redstone rs = (Redstone)component;
            rs.removeRedstoneModifiers(this.redstoneModifiers);
            rs.addRedstoneModifiers(newRedstoneModifiers);
            rs.update();
        }
        this.redstoneModifiers = newRedstoneModifiers.isEmpty() ? Collections.emptyList() : newRedstoneModifiers;
    }

    private void resetRates() {
        this.augmentation = 0;
        this.extraProcessTime = 0;
        this.processTimeMultiplier = 1.0;
        this.extraEnergyDemand = 0;
        this.energyDemandMultiplier = 1.0;
        this.extraEnergyStorage = 0;
        this.energyStorageMultiplier = 1.0;
        this.extraTier = 0;
    }

    private static class UpgradeRedstoneModifier
    implements Redstone.IRedstoneModifier {
        private final IUpgradeItem upgrade;
        private final ItemStack stack;
        private final IUpgradableBlock block;

        UpgradeRedstoneModifier(IUpgradeItem upgrade, ItemStack stack, IUpgradableBlock block) {
            this.upgrade = upgrade;
            this.stack = stack.copy();
            this.block = block;
        }

        @Override
        public int getRedstoneInput(int redstoneInput) {
            return this.upgrade.getRedstoneInput(this.stack, this.block, redstoneInput);
        }
    }

}

