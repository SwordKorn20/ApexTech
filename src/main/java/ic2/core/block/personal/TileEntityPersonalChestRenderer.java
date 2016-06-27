/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.personal;

import ic2.core.IC2;
import ic2.core.block.personal.ModelPersonalChest;
import ic2.core.block.personal.TileEntityPersonalChest;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class TileEntityPersonalChestRenderer
extends TileEntitySpecialRenderer<TileEntityPersonalChest> {
    private static final ResourceLocation texture = new ResourceLocation(IC2.textureDomain, "textures/models/newsafe.png");
    private final ModelPersonalChest model = new ModelPersonalChest();

    public void renderTileEntityAt(TileEntityPersonalChest te, double x, double y, double z, float partialTicks, int destroyStage) {
        float angle;
        this.bindTexture(texture);
        float doorHingeX = 0.84375f;
        float doorHingeZ = 0.15625f;
        GlStateManager.pushMatrix();
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GlStateManager.translate((double)x, (double)y, (double)z);
        GlStateManager.translate((float)0.5f, (float)0.5f, (float)0.5f);
        switch (te.getFacing()) {
            case SOUTH: {
                angle = 180.0f;
                break;
            }
            case WEST: {
                angle = 90.0f;
                break;
            }
            case EAST: {
                angle = -90.0f;
                break;
            }
            default: {
                angle = 0.0f;
            }
        }
        GlStateManager.rotate((float)angle, (float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.translate((float)-0.5f, (float)-0.5f, (float)-0.5f);
        angle = te.getLidAngle(partialTicks);
        angle = 1.0f - angle * angle * angle;
        GlStateManager.translate((float)0.84375f, (float)0.0f, (float)0.15625f);
        GlStateManager.rotate((float)(angle * 90.0f - 90.0f), (float)0.0f, (float)1.0f, (float)0.0f);
        GlStateManager.translate((float)-0.84375f, (float)0.0f, (float)-0.15625f);
        this.model.render();
        GlStateManager.popMatrix();
    }

}

