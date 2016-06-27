/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.RenderItem
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.machine.gui;

import com.google.common.base.Supplier;
import ic2.core.ContainerBase;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.machine.container.ContainerPatternStorage;
import ic2.core.block.machine.tileentity.TileEntityPatternStorage;
import ic2.core.gui.CustomButton;
import ic2.core.gui.GuiElement;
import ic2.core.gui.IClickHandler;
import ic2.core.gui.IEnableHandler;
import ic2.core.gui.Text;
import ic2.core.gui.dynamic.TextProvider;
import ic2.core.init.Localization;
import ic2.core.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class GuiPatternStorage
extends GuiIC2<ContainerPatternStorage> {
    private static final ResourceLocation background = new ResourceLocation(IC2.textureDomain, "textures/gui/GUIPatternStorage.png");

    public GuiPatternStorage(ContainerPatternStorage container) {
        super(container);
        this.addElement(new CustomButton(this, 7, 19, 9, 18, this.createEventSender(0)).withTooltip("ic2.PatternStorage.gui.info.last"));
        this.addElement(new CustomButton(this, 36, 19, 9, 18, this.createEventSender(1)).withTooltip("ic2.PatternStorage.gui.info.next"));
        this.addElement(new CustomButton(this, 10, 37, 16, 8, this.createEventSender(2)).withTooltip("ic2.PatternStorage.gui.info.export"));
        this.addElement(new CustomButton(this, 26, 37, 16, 8, this.createEventSender(3)).withTooltip("ic2.PatternStorage.gui.info.import"));
        this.addElement(Text.create(this, this.xSize / 2, 30, TextProvider.of(new Supplier<String>(){

            public String get() {
                TileEntityPatternStorage te = (TileEntityPatternStorage)((ContainerPatternStorage)GuiPatternStorage.access$000((GuiPatternStorage)GuiPatternStorage.this)).base;
                return "" + Math.min(te.index + 1, te.maxIndex) + " / " + te.maxIndex;
            }
        }), 4210752, false, true, false));
        this.addElement(Text.create(this, 10, 48, "ic2.generic.text.Name", 16777215, false));
        this.addElement(Text.create(this, 10, 59, "ic2.generic.text.UUMatte", 16777215, false));
        this.addElement(Text.create(this, 10, 70, "ic2.generic.text.Energy", 16777215, false));
        IEnableHandler patternInfoEnabler = new IEnableHandler(){

            @Override
            public boolean isEnabled() {
                return ((TileEntityPatternStorage)((ContainerPatternStorage)GuiPatternStorage.access$100((GuiPatternStorage)GuiPatternStorage.this)).base).pattern != null;
            }
        };
        this.addElement(Text.create(this, 80, 48, TextProvider.of(new Supplier<String>(){

            public String get() {
                ItemStack pattern = ((TileEntityPatternStorage)((ContainerPatternStorage)GuiPatternStorage.access$200((GuiPatternStorage)GuiPatternStorage.this)).base).pattern;
                return pattern != null ? pattern.getDisplayName() : null;
            }
        }), 16777215, false).withEnableHandler(patternInfoEnabler));
        this.addElement(Text.create(this, 80, 59, TextProvider.of(new Supplier<String>(){

            public String get() {
                return Util.toSiString(((TileEntityPatternStorage)((ContainerPatternStorage)GuiPatternStorage.access$300((GuiPatternStorage)GuiPatternStorage.this)).base).patternUu, 4) + Localization.translate("ic2.generic.text.bucketUnit");
            }
        }), 16777215, false).withEnableHandler(patternInfoEnabler));
        this.addElement(Text.create(this, 80, 70, TextProvider.of(new Supplier<String>(){

            public String get() {
                return Util.toSiString(((TileEntityPatternStorage)((ContainerPatternStorage)GuiPatternStorage.access$400((GuiPatternStorage)GuiPatternStorage.this)).base).patternEu, 4) + Localization.translate("ic2.generic.text.EU");
            }
        }), 16777215, false).withEnableHandler(patternInfoEnabler));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float f, int x, int y) {
        super.drawGuiContainerBackgroundLayer(f, x, y);
        TileEntityPatternStorage te = (TileEntityPatternStorage)((ContainerPatternStorage)this.container).base;
        if (te.pattern != null) {
            this.mc.getRenderItem().renderItemIntoGUI(te.pattern, this.guiLeft + 152, this.guiTop + 29);
        }
    }

    @Override
    protected ResourceLocation getTexture() {
        return background;
    }

    static /* synthetic */ ContainerBase access$000(GuiPatternStorage x0) {
        return x0.container;
    }

    static /* synthetic */ ContainerBase access$100(GuiPatternStorage x0) {
        return x0.container;
    }

    static /* synthetic */ ContainerBase access$200(GuiPatternStorage x0) {
        return x0.container;
    }

    static /* synthetic */ ContainerBase access$300(GuiPatternStorage x0) {
        return x0.container;
    }

    static /* synthetic */ ContainerBase access$400(GuiPatternStorage x0) {
        return x0.container;
    }

}

