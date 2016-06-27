/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fluids.Fluid
 */
package ic2.core.ref;

import ic2.core.block.state.IIdProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;

public enum FluidName implements IIdProvider
{
    air(false),
    biogas(false),
    biomass,
    construction_foam,
    coolant,
    distilled_water,
    hot_coolant,
    hot_water,
    pahoehoe_lava(false),
    steam(false),
    superheated_steam(false),
    uu_matter,
    weed_ex(false),
    oxygen(false),
    hydrogen(false),
    heavy_water,
    deuterium(false);
    
    public static final FluidName[] values;
    private final boolean hasFlowTexture;
    private Fluid instance;

    private FluidName() {
        this(true);
    }

    private FluidName(boolean hasFlowTexture) {
        this.hasFlowTexture = hasFlowTexture;
    }

    @Override
    public String getName() {
        return "ic2" + this.name();
    }

    @Override
    public int getId() {
        throw new UnsupportedOperationException();
    }

    public ResourceLocation getTextureLocation(boolean flowing) {
        String type = flowing && this.hasFlowTexture ? "flow" : "still";
        return new ResourceLocation("ic2", "blocks/fluid/" + this.name() + "_" + type);
    }

    public void setInstance(Fluid fluid) {
        if (fluid == null) {
            throw new NullPointerException("null fluid");
        }
        if (this.instance != null) {
            throw new IllegalStateException("conflicting instance");
        }
        this.instance = fluid;
    }

    public Fluid getInstance() {
        return this.instance;
    }

    static {
        values = FluidName.values();
    }
}

