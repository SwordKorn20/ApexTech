/*
 * Decompiled with CFR 0_114.
 */
package ic2.core.crop;

import ic2.api.crops.CropCard;

public abstract class IC2CropCard
extends CropCard {
    @Override
    public String getOwner() {
        return "IC2";
    }

    @Override
    public String getDisplayName() {
        return "ic2.crop." + this.getName();
    }

    @Override
    public String getDiscoveredBy() {
        return "IC2 Team";
    }
}

