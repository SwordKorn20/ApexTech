/*
 * Decompiled with CFR 0_114.
 */
package ic2.core.item.type;

import ic2.core.block.state.IIdProvider;

public enum DustResourceType implements IIdProvider
{
    bronze(0),
    clay(1),
    coal(2),
    coal_fuel(3),
    copper(4),
    diamond(5),
    energium(6),
    gold(7),
    iron(8),
    lapis(9),
    lead(10),
    lithium(11),
    obsidian(12),
    silicon_dioxide(13),
    silver(14),
    stone(15),
    sulfur(16),
    tin(17),
    small_bronze(18),
    small_copper(19),
    small_gold(20),
    small_iron(21),
    small_lapis(22),
    small_lead(23),
    small_lithium(24),
    small_obsidian(25),
    small_silver(26),
    small_sulfur(27),
    small_tin(28);
    
    private final int id;

    private DustResourceType(int id) {
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

