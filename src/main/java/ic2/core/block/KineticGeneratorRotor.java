/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.model.ModelBase
 *  net.minecraft.client.model.ModelRenderer
 *  net.minecraft.entity.Entity
 */
package ic2.core.block;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class KineticGeneratorRotor
extends ModelBase {
    ModelRenderer rotor1;
    ModelRenderer rotor2;
    ModelRenderer rotor3;
    ModelRenderer rotor4;

    public KineticGeneratorRotor(int radius) {
        this.textureWidth = 32;
        this.textureHeight = 256;
        this.rotor1 = new ModelRenderer((ModelBase)this, 0, 0);
        this.rotor1.addBox(0.0f, 0.0f, -4.0f, 1, radius * 8, 8);
        this.rotor1.setRotationPoint(-8.0f, 0.0f, 0.0f);
        this.rotor1.setTextureSize(32, 256);
        this.rotor1.mirror = true;
        KineticGeneratorRotor.setRotation(this.rotor1, 0.0f, -0.5f, 0.0f);
        this.rotor2 = new ModelRenderer((ModelBase)this, 0, 0);
        this.rotor2.addBox(0.0f, 0.0f, -4.0f, 1, radius * 8, 8);
        this.rotor2.setRotationPoint(-8.0f, 0.0f, 0.0f);
        this.rotor2.setTextureSize(32, 256);
        this.rotor2.mirror = true;
        KineticGeneratorRotor.setRotation(this.rotor2, 3.1f, 0.5f, 0.0f);
        this.rotor3 = new ModelRenderer((ModelBase)this, 0, 0);
        this.rotor3.addBox(0.0f, 0.0f, -4.0f, 1, radius * 8, 8);
        this.rotor3.setRotationPoint(-8.0f, 0.0f, 0.0f);
        this.rotor3.setTextureSize(32, 256);
        this.rotor3.mirror = true;
        KineticGeneratorRotor.setRotation(this.rotor3, 4.7f, 0.0f, 0.5f);
        this.rotor4 = new ModelRenderer((ModelBase)this, 0, 0);
        this.rotor4.addBox(0.0f, 0.0f, -4.0f, 1, radius * 8, 8);
        this.rotor4.setRotationPoint(-8.0f, 0.0f, 0.0f);
        this.rotor4.setTextureSize(32, 256);
        this.rotor4.mirror = true;
        KineticGeneratorRotor.setRotation(this.rotor4, 1.5f, 0.0f, -0.5f);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float scale) {
        this.rotor1.render(scale);
        this.rotor2.render(scale);
        this.rotor3.render(scale);
        this.rotor4.render(scale);
    }

    private static void setRotation(ModelRenderer model, float x, float y, float z) {
        model.rotateAngleX = x;
        model.rotateAngleY = y;
        model.rotateAngleZ = z;
    }
}

