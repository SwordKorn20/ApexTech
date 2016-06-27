/*
 * Decompiled with CFR 0_114.
 */
package ic2.core.block.type;

import ic2.core.block.state.IIdProvider;
import ic2.core.block.type.IExtBlockType;

public enum ResourceBlock implements IIdProvider,
IExtBlockType
{
    basalt(20.0f, 45.0f),
    copper_ore(3.0f, 5.0f),
    lead_ore(2.0f, 4.0f),
    tin_ore(3.0f, 5.0f),
    uranium_ore(4.0f, 6.0f),
    bronze_block(5.0f, 10.0f),
    copper_block(4.0f, 10.0f),
    lead_block(4.0f, 10.0f),
    steel_block(8.0f, 10.0f),
    tin_block(4.0f, 10.0f),
    uranium_block(6.0f, 10.0f),
    reinforced_stone(80.0f, 180.0f),
    machine(5.0f, 10.0f),
    advanced_machine(8.0f, 10.0f),
    reactor_vessel(40.0f, 90.0f);
    
    private final float hardness;
    private final float explosionResistance;

    private ResourceBlock(float hardness, float explosionResistance) {
        this.hardness = hardness;
        this.explosionResistance = explosionResistance;
    }

    @Override
    public String getName() {
        return this.name();
    }

    @Override
    public int getId() {
        return this.ordinal();
    }

    @Override
    public float getHardness() {
        return this.hardness;
    }

    @Override
    public float getExplosionResistance() {
        return this.explosionResistance;
    }
}

