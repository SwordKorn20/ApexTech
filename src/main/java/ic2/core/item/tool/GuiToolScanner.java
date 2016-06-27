/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.item.tool;

import ic2.core.ContainerBase;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.init.Localization;
import ic2.core.item.tool.ContainerToolScanner;
import ic2.core.util.Tuple;
import java.util.List;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class GuiToolScanner
extends GuiIC2<ContainerToolScanner> {
    public GuiToolScanner(ContainerToolScanner container) {
        super(container, 230);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        super.drawGuiContainerForegroundLayer(par1, par2);
        this.fontRendererObj.drawString(Localization.translate("ic2.itemScanner.found"), 10, 20, 2157374);
        if (((ContainerToolScanner)this.container).scanResults != null) {
            int count = 0;
            for (Tuple.T2<ItemStack, Integer> result : ((ContainerToolScanner)this.container).scanResults) {
                String name = ((ItemStack)result.a).getItem().getItemStackDisplayName((ItemStack)result.a);
                this.fontRendererObj.drawString(result.b + "x " + name, 10, 35 + count * 11, 5752026);
                if (++count != 10) continue;
                break;
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        super.drawGuiContainerBackgroundLayer(f, x, y);
    }

    @Override
    public ResourceLocation getTexture() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUIToolScanner.png");
    }
}

