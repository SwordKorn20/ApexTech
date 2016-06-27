/*
 * Decompiled with CFR 0_114.
 */
package ic2.core.item.tool;

import ic2.core.item.tool.ItemDrill;
import ic2.core.item.tool.ItemElectricTool;
import ic2.core.ref.ItemName;

public class ItemDrillDiamond
extends ItemDrill {
    public ItemDrillDiamond() {
        super(ItemName.diamond_drill, 80, ItemElectricTool.HarvestLevel.Diamond);
        this.maxCharge = 30000;
        this.transferLimit = 100;
        this.tier = 1;
        this.efficiencyOnProperMaterial = 16.0f;
    }
}

