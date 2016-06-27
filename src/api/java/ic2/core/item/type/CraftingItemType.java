/*
 * Decompiled with CFR 0_114.
 */
package ic2.core.item.type;

import ic2.core.block.state.IIdProvider;

public enum CraftingItemType implements IIdProvider
{
    rubber(0),
    circuit(1),
    advanced_circuit(2),
    alloy(3),
    iridium(4),
    coil(5),
    electric_motor(6),
    heat_conductor(7),
    copper_boiler(8),
    fuel_rod(9),
    tin_can(10),
    small_power_unit(11),
    power_unit(12),
    carbon_fibre(13),
    carbon_mesh(14),
    carbon_plate(15),
    coal_ball(16),
    coal_block(17),
    coal_chunk(18),
    industrial_diamond(19),
    plant_ball(20),
    bio_chaff(21),
    compressed_hydrated_coal(22),
    scrap(23),
    scrap_box(24),
    cf_powder(25),
    pellet(26),
    raw_crystal_memory(27),
    iron_shaft(29),
    steel_shaft(30),
    wood_rotor_blade(31),
    iron_rotor_blade(32),
    steel_rotor_blade(33),
    carbon_rotor_blade(34),
    steam_turbine_blade(35),
    steam_turbine(36);
    
    private final int id;

    private CraftingItemType(int id) {
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

