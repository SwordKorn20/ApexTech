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
package ic2.core.block.machine.gui;

import ic2.core.ContainerBase;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.machine.container.ContainerScanner;
import ic2.core.block.machine.tileentity.TileEntityScanner;
import ic2.core.gui.CustomButton;
import ic2.core.gui.EnergyGauge;
import ic2.core.gui.GuiElement;
import ic2.core.gui.IClickHandler;
import ic2.core.gui.IEnableHandler;
import ic2.core.init.Localization;
import ic2.core.util.Util;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class GuiScanner
extends GuiIC2<ContainerScanner> {
    private static final ResourceLocation background = new ResourceLocation(IC2.textureDomain, "textures/gui/GUIScanner.png");
    private final String[] info = new String[9];

    public GuiScanner(final ContainerScanner container) {
        super(container);
        this.addElement(EnergyGauge.asBolt(this, 12, 25, (TileEntityBlock)container.base));
        this.addElement(((CustomButton)new CustomButton(this, 102, 49, 12, 12, 176, 57, background, this.createEventSender(0)).withEnableHandler(new IEnableHandler(){

            @Override
            public boolean isEnabled() {
                return ((TileEntityScanner)container.base).getState() == TileEntityScanner.State.COMPLETED || ((TileEntityScanner)container.base).getState() == TileEntityScanner.State.TRANSFER_ERROR || ((TileEntityScanner)container.base).getState() == TileEntityScanner.State.FAILED;
            }
        })).withTooltip("ic2.Scanner.gui.button.delete"));
        this.addElement(((CustomButton)new CustomButton(this, 143, 49, 24, 12, 176, 69, background, this.createEventSender(1)).withEnableHandler(new IEnableHandler(){

            @Override
            public boolean isEnabled() {
                return ((TileEntityScanner)container.base).getState() == TileEntityScanner.State.COMPLETED || ((TileEntityScanner)container.base).getState() == TileEntityScanner.State.TRANSFER_ERROR;
            }
        })).withTooltip("ic2.Scanner.gui.button.save"));
        this.info[1] = Localization.translate("ic2.Scanner.gui.info1");
        this.info[2] = Localization.translate("ic2.Scanner.gui.info2");
        this.info[3] = Localization.translate("ic2.Scanner.gui.info3");
        this.info[4] = Localization.translate("ic2.Scanner.gui.info4");
        this.info[5] = Localization.translate("ic2.Scanner.gui.info5");
        this.info[6] = Localization.translate("ic2.Scanner.gui.info6");
        this.info[7] = Localization.translate("ic2.Scanner.gui.info7");
        this.info[8] = Localization.translate("ic2.Scanner.gui.info8");
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        this.fontRendererObj.drawString(this.info[5] + ":", 105, 6, 4210752);
        TileEntityScanner te = (TileEntityScanner)((ContainerScanner)this.container).base;
        switch (te.getState()) {
            case IDLE: {
                this.fontRendererObj.drawString(Localization.translate("ic2.Scanner.gui.idle"), 10, 69, 15461152);
                break;
            }
            case NO_STORAGE: {
                this.fontRendererObj.drawString(this.info[2], 10, 69, 15461152);
                break;
            }
            case SCANNING: {
                this.fontRendererObj.drawString(this.info[1], 10, 69, 2157374);
                this.fontRendererObj.drawString("" + te.getPercentageDone() + "%", 125, 69, 2157374);
                break;
            }
            case NO_ENERGY: {
                this.fontRendererObj.drawString(this.info[3], 10, 69, 14094352);
                break;
            }
            case ALREADY_RECORDED: {
                this.fontRendererObj.drawString(this.info[8], 10, 69, 14094352);
                break;
            }
            case FAILED: {
                this.fontRendererObj.drawString(this.info[4], 10, 69, 2157374);
                this.fontRendererObj.drawString(this.info[6], 110, 30, 14094352);
                break;
            }
            case COMPLETED: 
            case TRANSFER_ERROR: {
                if (te.getState() == TileEntityScanner.State.COMPLETED) {
                    this.fontRendererObj.drawString(this.info[4], 10, 69, 2157374);
                }
                if (te.getState() == TileEntityScanner.State.TRANSFER_ERROR) {
                    this.fontRendererObj.drawString(this.info[7], 10, 69, 14094352);
                }
                this.fontRendererObj.drawString(Util.toSiString(te.patternUu, 4) + "B UUM", 105, 25, 16777215);
                this.fontRendererObj.drawString(Util.toSiString(te.patternEu, 4) + "EU", 105, 36, 16777215);
            }
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        this.bindTexture();
        TileEntityScanner te = (TileEntityScanner)((ContainerScanner)this.container).base;
        int scanningloop = te.getSubPercentageDoneScaled(66);
        if (scanningloop > 0) {
            this.drawTexturedModalRect(this.guiLeft + 30, this.guiTop + 20, 176, 14, scanningloop, 43);
        }
    }

    @Override
    protected ResourceLocation getTexture() {
        return background;
    }

}

