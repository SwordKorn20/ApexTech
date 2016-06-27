/*
 * Decompiled with CFR 0_114.
 */
package ic2.core.item.type;

import ic2.core.block.state.IIdProvider;

public enum CasingResourceType implements IIdProvider
{
    bronze(0),
    copper(1),
    gold(2),
    iron(3),
    lead(4),
    steel(5),
    tin(6);
    
    private final int id;

    private CasingResourceType(int id) {
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

