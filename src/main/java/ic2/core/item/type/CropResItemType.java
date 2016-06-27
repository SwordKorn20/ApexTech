/*
 * Decompiled with CFR 0_114.
 */
package ic2.core.item.type;

import ic2.core.block.state.IIdProvider;

public enum CropResItemType implements IIdProvider
{
    coffee_beans(0),
    coffee_powder(1),
    fertilizer(2),
    grin_powder(3),
    hops(4),
    weed(5);
    
    private final int id;

    private CropResItemType(int id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return this.name();
    }

    @Override
    public int getId() {
        return this.id;
    }
}

