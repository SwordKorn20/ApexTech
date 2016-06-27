/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.EnumFacing
 */
package ic2.api.energy.tile;

import net.minecraft.util.EnumFacing;

public interface IHeatSource {
    public int maxrequestHeatTick(EnumFacing var1);

    public int requestHeat(EnumFacing var1, int var2);
}

