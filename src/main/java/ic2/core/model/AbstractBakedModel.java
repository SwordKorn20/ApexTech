/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.block.model.IBakedModel
 *  net.minecraft.client.renderer.block.model.ItemCameraTransforms
 *  net.minecraft.client.renderer.block.model.ItemOverrideList
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 */
package ic2.core.model;

import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;

public abstract class AbstractBakedModel
implements IBakedModel {
    public boolean isAmbientOcclusion() {
        return true;
    }

    public boolean isGui3d() {
        return false;
    }

    public boolean isBuiltInRenderer() {
        return false;
    }

    public TextureAtlasSprite getParticleTexture() {
        return null;
    }

    public ItemCameraTransforms getItemCameraTransforms() {
        return null;
    }

    public ItemOverrideList getOverrides() {
        return null;
    }
}

