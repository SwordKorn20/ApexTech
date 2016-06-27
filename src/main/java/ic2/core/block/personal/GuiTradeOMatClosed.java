/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.personal;

import ic2.core.ContainerBase;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.personal.ContainerTradeOMatClosed;
import ic2.core.block.personal.TileEntityTradeOMat;
import ic2.core.init.Localization;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class GuiTradeOMatClosed
extends GuiIC2<ContainerTradeOMatClosed> {
    private static final ResourceLocation background = new ResourceLocation(IC2.textureDomain, "textures/gui/GUITradeOMatClosed.png");

    public GuiTradeOMatClosed(ContainerTradeOMatClosed container) {
        super(container);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        super.drawGuiContainerForegroundLayer(par1, par2);
        this.fontRendererObj.drawString(Localization.translate("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
        this.fontRendererObj.drawString(Localization.translate("ic2.container.personalTrader.want"), 12, 23, 4210752);
        this.fontRendererObj.drawString(Localization.translate("ic2.container.personalTrader.offer"), 12, 42, 4210752);
        this.fontRendererObj.drawString(Localization.translate("ic2.container.personalTrader.stock"), 12, 60, 4210752);
        this.fontRendererObj.drawString(((TileEntityTradeOMat)((ContainerTradeOMatClosed)this.container).base).stock < 0 ? "\u221e" : "" + ((TileEntityTradeOMat)((ContainerTradeOMatClosed)this.container).base).stock, 50, 60, ((TileEntityTradeOMat)((ContainerTradeOMatClosed)this.container).base).stock != 0 ? 4210752 : 16733525);
    }

    @Override
    protected ResourceLocation getTexture() {
        return background;
    }
}

