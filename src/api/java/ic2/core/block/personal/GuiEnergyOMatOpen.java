/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.util.ResourceLocation
 */
package ic2.core.block.personal;

import ic2.core.ContainerBase;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.personal.ContainerEnergyOMatOpen;
import ic2.core.block.personal.TileEntityEnergyOMat;
import ic2.core.gui.GuiElement;
import ic2.core.gui.IClickHandler;
import ic2.core.gui.VanillaButton;
import ic2.core.init.Localization;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;

public class GuiEnergyOMatOpen
extends GuiIC2<ContainerEnergyOMatOpen> {
    private static final ResourceLocation background = new ResourceLocation(IC2.textureDomain, "textures/gui/GUIEnergyOMatOpen.png");

    public GuiEnergyOMatOpen(ContainerEnergyOMatOpen container) {
        super(container);
        this.addElement(new VanillaButton(this, 102, 16, 32, 10, this.createEventSender(0)).withText("-100k"));
        this.addElement(new VanillaButton(this, 102, 26, 32, 10, this.createEventSender(1)).withText("-10k"));
        this.addElement(new VanillaButton(this, 102, 36, 32, 10, this.createEventSender(2)).withText("-1k"));
        this.addElement(new VanillaButton(this, 102, 46, 32, 10, this.createEventSender(3)).withText("-100"));
        this.addElement(new VanillaButton(this, 134, 16, 32, 10, this.createEventSender(4)).withText("+100k"));
        this.addElement(new VanillaButton(this, 134, 26, 32, 10, this.createEventSender(5)).withText("+10k"));
        this.addElement(new VanillaButton(this, 134, 36, 32, 10, this.createEventSender(6)).withText("+1k"));
        this.addElement(new VanillaButton(this, 134, 46, 32, 10, this.createEventSender(7)).withText("+100"));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        super.drawGuiContainerForegroundLayer(par1, par2);
        this.fontRendererObj.drawString(Localization.translate("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
        this.fontRendererObj.drawString(Localization.translate("ic2.container.personalTrader.offer"), 100, 60, 4210752);
        this.fontRendererObj.drawString("" + ((TileEntityEnergyOMat)((ContainerEnergyOMatOpen)this.container).base).euOffer + " EU", 100, 68, 4210752);
    }

    @Override
    protected ResourceLocation getTexture() {
        return background;
    }
}

