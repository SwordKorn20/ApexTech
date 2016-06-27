/*
 * Decompiled with CFR 0_114.
 */
package ic2.core.item.tool;

import ic2.core.item.tool.ItemDrill;
import ic2.core.item.tool.ItemElectricTool;
import ic2.core.ref.ItemName;

public class ItemDrillStandard
extends ItemDrill {
    public ItemDrillStandard() {
        super(ItemName.drill, 50, ItemElectricTool.HarvestLevel.Iron);
        this.maxCharge = 30000;
        this.transferLimit = 100;
        this.tier = 1;
        this.efficiencyOnProperMaterial = 8.0f;
    }
}

