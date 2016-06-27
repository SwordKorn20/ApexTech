/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.World
 */
package ic2.core.energy;

import ic2.api.energy.IEnergyNet;
import ic2.core.WorldData;
import ic2.core.energy.EnergyNetGlobal;
import ic2.core.energy.EnergyNetLocal;
import ic2.core.energy.leg.EnergyNetGlobalLeg;
import ic2.core.energy.leg.EnergyNetLocalLeg;
import net.minecraft.world.World;

public final class EnergyNetGateway {
    private static final boolean useEnergyNetLeg = System.getProperty("IC2ExpEnet") == null;

    private EnergyNetGateway() {
    }

    public static IEnergyNet init() {
        if (EnergyNetGateway.useEnergyNetLeg()) {
            return EnergyNetGlobalLeg.initialize();
        }
        return EnergyNetGlobal.initialize();
    }

    public static void initWorldData(WorldData data, World world) {
        if (EnergyNetGateway.useEnergyNetLeg()) {
            data.energyNetLeg = new EnergyNetLocalLeg(world);
        } else {
            data.energyNet = new EnergyNetLocal(world);
        }
    }

    public static boolean useEnergyNetLeg() {
        return useEnergyNetLeg;
    }

    public static void onTickEnd(World world) {
        if (!EnergyNetGateway.useEnergyNetLeg()) {
            EnergyNetGlobal.onTickEnd(world);
        }
    }
}

