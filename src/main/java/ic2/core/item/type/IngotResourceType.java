/*
 * Decompiled with CFR 0_114.
 */
package ic2.core.item.type;

import ic2.core.block.state.IIdProvider;

public enum IngotResourceType implements IIdProvider
{
    alloy(0),
    bronze(1),
    copper(2),
    lead(3),
    silver(4),
    steel(5),
    tin(6);
    
    private final int id;

    private IngotResourceType(int id) {
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

