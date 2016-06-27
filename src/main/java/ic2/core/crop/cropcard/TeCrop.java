/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.common.property.IUnlistedProperty
 */
package ic2.core.crop.cropcard;

import ic2.api.crops.CropCard;
import ic2.core.block.state.UnlistedProperty;
import net.minecraftforge.common.property.IUnlistedProperty;

public class TeCrop {
    public static final IUnlistedProperty<CropRenderState> renderStateProperty = new UnlistedProperty<CropRenderState>("renderstate", CropRenderState.class);

    public static class CropRenderState {
        public final CropCard crop;
        public final int size;

        public CropRenderState(CropCard crop, int size) {
            this.crop = crop;
            this.size = size;
        }

        public int hashCode() {
            int ret = this.crop.hashCode();
            ret = ret * 31 + this.size;
            return ret;
        }

        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof CropRenderState)) {
                return false;
            }
            CropRenderState o = (CropRenderState)obj;
            return o.crop == this.crop && o.size == this.size;
        }
    }

}

