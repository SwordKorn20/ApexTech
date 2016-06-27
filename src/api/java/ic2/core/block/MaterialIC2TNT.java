/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.material.MapColor
 *  net.minecraft.block.material.Material
 */
package ic2.core.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class MaterialIC2TNT
extends Material {
    public static Material instance = new MaterialIC2TNT();

    public MaterialIC2TNT() {
        super(MapColor.TNT);
        this.setAdventureModeExempt();
        this.setBurning();
    }

    public boolean isOpaque() {
        return false;
    }
}

