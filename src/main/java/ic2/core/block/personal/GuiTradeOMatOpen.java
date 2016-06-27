/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.personal;

import ic2.core.ContainerBase;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.personal.ContainerTradeOMatOpen;
import ic2.core.block.personal.TileEntityTradeOMat;
import ic2.core.init.Localization;
import ic2.core.network.NetworkManager;
import ic2.core.util.SideGateway;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.inventory.IInventory;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class GuiTradeOMatOpen
extends GuiIC2<ContainerTradeOMatOpen> {
    private static final ResourceLocation background = new ResourceLocation(IC2.textureDomain, "textures/gui/GUITradeOMatOpen.png");
    private final boolean isAdmin;

    public GuiTradeOMatOpen(ContainerTradeOMatOpen container, boolean isAdmin) {
        super(container);
        this.isAdmin = isAdmin;
    }

    public void initGui() {
        super.initGui();
        if (this.isAdmin) {
            this.buttonList.add(new GuiButton(0, (this.width - this.xSize) / 2 + 152, (this.height - this.ySize) / 2 + 4, 20, 20, "\u221e"));
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        super.drawGuiContainerForegroundLayer(par1, par2);
        this.fontRendererObj.drawString(Localization.translate("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
        this.fontRendererObj.drawString(Localization.translate("ic2.container.personalTrader.want"), 12, 23, 4210752);
        this.fontRendererObj.drawString(Localization.translate("ic2.container.personalTrader.offer"), 12, 57, 4210752);
        this.fontRendererObj.drawString(Localization.translate("ic2.container.personalTrader.totalTrades0"), 108, 28, 4210752);
        this.fontRendererObj.drawString(Localization.translate("ic2.container.personalTrader.totalTrades1"), 108, 36, 4210752);
        this.fontRendererObj.drawString("" + ((TileEntityTradeOMat)((ContainerTradeOMatOpen)this.container).base).totalTradeCount, 112, 44, 4210752);
        this.fontRendererObj.drawString(Localization.translate("ic2.container.personalTrader.stock") + " " + (((TileEntityTradeOMat)((ContainerTradeOMatOpen)this.container).base).stock < 0 ? "\u221e" : new StringBuilder().append("").append(((TileEntityTradeOMat)((ContainerTradeOMatOpen)this.container).base).stock).toString()), 108, 60, 4210752);
    }

    protected void actionPerformed(GuiButton guibutton) throws IOException {
        super.actionPerformed(guibutton);
        if (guibutton.id == 0) {
            IC2.network.get(false).initiateClientTileEntityEvent((TileEntity)((ContainerTradeOMatOpen)this.container).base, 0);
        }
    }

    @Override
    protected ResourceLocation getTexture() {
        return background;
    }
}

