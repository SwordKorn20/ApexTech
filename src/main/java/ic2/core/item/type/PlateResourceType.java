/*
 * Decompiled with CFR 0_114.
 */
package ic2.core.item.type;

import ic2.core.block.state.IIdProvider;

public enum PlateResourceType implements IIdProvider
{
    bronze(0),
    copper(1),
    gold(2),
    iron(3),
    lapis(4),
    lead(5),
    obsidian(6),
    steel(7),
    tin(8),
    dense_bronze(9),
    dense_copper(10),
    dense_gold(11),
    dense_iron(12),
    dense_lapis(13),
    dense_lead(14),
    dense_obsidian(15),
    dense_steel(16),
    dense_tin(17);
    
    private final int id;

    private PlateResourceType(int id) {
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

