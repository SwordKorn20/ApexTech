/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.client.renderer.RenderHelper
 *  net.minecraft.client.renderer.RenderItem
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.wiring;

import ic2.core.ContainerBase;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.wiring.ContainerTransformer;
import ic2.core.block.wiring.TileEntityTransformer;
import ic2.core.init.Localization;
import ic2.core.network.NetworkManager;
import ic2.core.ref.ItemName;
import ic2.core.util.SideGateway;
import java.io.IOException;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class GuiTransformer
extends GuiIC2<ContainerTransformer> {
    public String[] mode = new String[]{"", "", "", ""};
    private static final ResourceLocation background = new ResourceLocation(IC2.textureDomain, "textures/gui/GUITransfomer.png");

    public GuiTransformer(ContainerTransformer container) {
        super(container, 219);
        this.mode[1] = Localization.translate("ic2.Transformer.gui.switch.mode1");
        this.mode[2] = Localization.translate("ic2.Transformer.gui.switch.mode2");
        this.mode[3] = Localization.translate("ic2.Transformer.gui.switch.mode3");
    }

    protected void actionPerformed(GuiButton guibutton) throws IOException {
        super.actionPerformed(guibutton);
        IC2.network.get(false).initiateClientTileEntityEvent((TileEntity)((ContainerTransformer)this.container).base, guibutton.id);
    }

    @Override
    protected void mouseClicked(int i, int j, int k) throws IOException {
        super.mouseClicked(i, j, k);
        int x = i - this.guiLeft;
        int y = j - this.guiTop;
        if (x >= 150 && y >= 32 && x <= 167 && y <= 49) {
            IC2.network.get(false).initiateClientTileEntityEvent((TileEntity)((ContainerTransformer)this.container).base, 3);
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
        this.fontRendererObj.drawString(Localization.translate("ic2.Transformer.gui.Output"), 6, 30, 4210752);
        this.fontRendererObj.drawString(Localization.translate("ic2.Transformer.gui.Input"), 6, 43, 4210752);
        this.fontRendererObj.drawString("" + ((TileEntityTransformer)((ContainerTransformer)this.container).base).getoutputflow() + " " + Localization.translate("ic2.generic.text.EUt"), 52, 30, 2157374);
        this.fontRendererObj.drawString("" + ((TileEntityTransformer)((ContainerTransformer)this.container).base).getinputflow() + " " + Localization.translate("ic2.generic.text.EUt"), 52, 45, 2157374);
        RenderItem renderItem = this.mc.getRenderItem();
        RenderHelper.enableGUIStandardItemLighting();
        switch (((TileEntityTransformer)((ContainerTransformer)this.container).base).getMode()) {
            case redstone: {
                renderItem.renderItemIntoGUI(ItemName.wrench.getItemStack(), 152, 67);
                break;
            }
            case stepdown: {
                renderItem.renderItemIntoGUI(ItemName.wrench.getItemStack(), 152, 87);
                break;
            }
            case stepup: {
                renderItem.renderItemIntoGUI(ItemName.wrench.getItemStack(), 152, 107);
                break;
            }
        }
        RenderHelper.disableStandardItemLighting();
    }

    public void initGui() {
        super.initGui();
        this.buttonList.add(new GuiButton(0, (this.width - this.xSize) / 2 + 7, (this.height - this.ySize) / 2 + 65, 144, 20, this.mode[1]));
        this.buttonList.add(new GuiButton(1, (this.width - this.xSize) / 2 + 7, (this.height - this.ySize) / 2 + 85, 144, 20, this.mode[2]));
        this.buttonList.add(new GuiButton(2, (this.width - this.xSize) / 2 + 7, (this.height - this.ySize) / 2 + 105, 144, 20, this.mode[3]));
    }

    @Override
    protected ResourceLocation getTexture() {
        return background;
    }

}

