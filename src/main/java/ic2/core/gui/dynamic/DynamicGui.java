/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.texture.TextureManager
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fluids.IFluidTank
 */
package ic2.core.gui.dynamic;

import ic2.core.ContainerBase;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.Energy;
import ic2.core.block.comp.TileEntityComponent;
import ic2.core.block.invslot.InvSlot;
import ic2.core.gui.EnergyGauge;
import ic2.core.gui.FluidSlot;
import ic2.core.gui.Gauge;
import ic2.core.gui.GuiElement;
import ic2.core.gui.Image;
import ic2.core.gui.LinkedGauge;
import ic2.core.gui.SlotGrid;
import ic2.core.gui.TankGauge;
import ic2.core.gui.Text;
import ic2.core.gui.dynamic.DynamicContainer;
import ic2.core.gui.dynamic.GuiEnvironment;
import ic2.core.gui.dynamic.GuiParser;
import ic2.core.gui.dynamic.IFluidTankProvider;
import ic2.core.gui.dynamic.IGuiValueProvider;
import ic2.core.gui.dynamic.TextProvider;
import ic2.core.upgrade.IUpgradableBlock;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.IFluidTank;

public class DynamicGui<T extends ContainerBase<? extends IInventory>>
extends GuiIC2<T> {
    public static <T extends IInventory> DynamicGui<ContainerBase<T>> create(T base, EntityPlayer player, GuiParser.GuiNode guiNode) {
        DynamicContainer<T> container = DynamicContainer.create(base, player, guiNode);
        return new DynamicGui<ContainerBase<T>>(player, container, guiNode);
    }

    protected DynamicGui(EntityPlayer player, T container, GuiParser.GuiNode guiNode) {
        super(container, guiNode.width, guiNode.height);
        this.initializeWidgets(player, guiNode);
    }

    private void initializeWidgets(EntityPlayer player, GuiParser.ParentNode parentNode) {
        block18 : for (GuiParser.Node rawNode : parentNode.getNodes()) {
            switch (rawNode.getType()) {
                case environment: {
                    if (((GuiParser.EnvironmentNode)rawNode).environment == GuiEnvironment.GAME) break;
                    continue block18;
                }
                case energygauge: {
                    if (!(this.container.base instanceof TileEntityBlock) || !((TileEntityBlock)this.container.base).hasComponent(Energy.class)) {
                        throw new RuntimeException("invalid base " + this.container.base + " for energygauge elements");
                    }
                    GuiParser.EnergyGaugeNode node = (GuiParser.EnergyGaugeNode)rawNode;
                    this.addElement(new EnergyGauge(this, node.x, node.y, (TileEntityBlock)this.container.base, node.style));
                    break;
                }
                case gauge: {
                    if (!(this.container.base instanceof IGuiValueProvider)) {
                        throw new RuntimeException("invalid base " + this.container.base + " for gauge elements");
                    }
                    GuiParser.GaugeNode node = (GuiParser.GaugeNode)rawNode;
                    this.addElement(new LinkedGauge(this, node.x, node.y, (IGuiValueProvider)this.container.base, node.name, node.style));
                    break;
                }
                case image: {
                    GuiParser.ImageNode node = (GuiParser.ImageNode)rawNode;
                    this.addElement(Image.create(this, node.x, node.y, node.width, node.height, node.src, node.u, node.v));
                    break;
                }
                case playerinventory: {
                    GuiParser.PlayerInventoryNode node = (GuiParser.PlayerInventoryNode)rawNode;
                    int hotbarOffset = 58;
                    this.addElement(new SlotGrid(this, node.x, node.y, 9, 3, SlotGrid.SlotStyle.Normal));
                    this.addElement(new SlotGrid(this, node.x, node.y + 58, 9, 1, SlotGrid.SlotStyle.Normal));
                    this.addElement(Text.create(this, node.x + 1, node.y - 10, TextProvider.ofTranslated(player.inventory.getName()), 4210752, false));
                    break;
                }
                case slot: {
                    GuiParser.SlotNode node = (GuiParser.SlotNode)rawNode;
                    this.addElement(new SlotGrid(this, node.x, node.y, 1, 1, node.style));
                    break;
                }
                case slotgrid: {
                    if (!(this.container.base instanceof TileEntityInventory)) {
                        throw new RuntimeException("invalid base " + this.container.base + " for slot elements");
                    }
                    GuiParser.SlotGridNode node = (GuiParser.SlotGridNode)rawNode;
                    InvSlot slot = ((TileEntityInventory)this.container.base).getInvSlot(node.name);
                    if (slot == null) {
                        throw new RuntimeException("invalid invslot name " + node.name + " for base " + this.container.base);
                    }
                    int size = slot.size();
                    if (size <= node.offset) break;
                    GuiParser.SlotGridNode.SlotGridDimension dim = node.getDimension(size);
                    this.addElement(new SlotGrid(this, node.x, node.y, dim.cols, dim.rows, node.style));
                    break;
                }
                case text: {
                    int x;
                    GuiParser.TextNode node = (GuiParser.TextNode)rawNode;
                    switch (node.align) {
                        case Start: {
                            x = node.x;
                            break;
                        }
                        case Center: {
                            x = node.x + this.xSize / 2;
                            break;
                        }
                        case End: {
                            x = node.x + this.xSize;
                            break;
                        }
                        default: {
                            throw new IllegalArgumentException("invalid alignment: " + (Object)((Object)node.align));
                        }
                    }
                    this.addElement(Text.create(this, x, node.y, node.width, node.height, node.text, node.color, node.shadow, node.xOffset, node.yOffset, node.centerX, node.centerY));
                    break;
                }
                case gui: {
                    break;
                }
                case fluidtank: {
                    if (!(this.container.base instanceof IFluidTankProvider)) {
                        throw new RuntimeException("invalid base " + this.container.base + " for tank elements");
                    }
                    GuiParser.FluidTankNode node = (GuiParser.FluidTankNode)rawNode;
                    this.addElement(TankGauge.createNormal(this, node.x, node.y, ((IFluidTankProvider)this.container.base).getFluidTank(node.name)));
                    break;
                }
                case fluidslot: {
                    if (!(this.container.base instanceof IFluidTankProvider)) {
                        throw new RuntimeException("invalid base " + this.container.base + " for tank elements");
                    }
                    GuiParser.FluidSlotNode node = (GuiParser.FluidSlotNode)rawNode;
                    this.addElement(FluidSlot.createFluidSlot(this, node.x, node.y, ((IFluidTankProvider)this.container.base).getFluidTank(node.name)));
                    break;
                }
            }
            if (!(rawNode instanceof GuiParser.ParentNode)) continue;
            this.initializeWidgets(player, (GuiParser.ParentNode)rawNode);
        }
    }

    @Override
    public void addElement(GuiElement<?> element) {
        super.addElement(element);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mouseX -= this.guiLeft;
        mouseY -= this.guiTop;
        GlStateManager.color((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GuiElement.bindCommonTexture();
        this.drawBackground();
        if (this.container.base instanceof IUpgradableBlock) {
            this.mc.getTextureManager().bindTexture(new ResourceLocation(IC2.textureDomain, "textures/gui/infobutton.png"));
            this.drawTexturedRect(3.0, 3.0, 10.0, 10.0, 0.0, 0.0);
        }
        for (GuiElement element : this.elements) {
            if (!element.isEnabled()) continue;
            element.drawBackground(mouseX, mouseY);
        }
    }

    private void drawBackground() {
        int side;
        this.drawTexturedRect(-16.0, -16.0, 32.0, 32.0, 0.0, 0.0);
        this.drawTexturedRect(this.xSize - 16, -16.0, 32.0, 32.0, 64.0, 0.0);
        this.drawTexturedRect(-16.0, this.ySize - 16, 32.0, 32.0, 0.0, 64.0);
        this.drawTexturedRect(this.xSize - 16, this.ySize - 16, 32.0, 32.0, 64.0, 64.0);
        for (side = 0; side < 2; ++side) {
            int y = this.ySize * side - 16;
            int v = 64 * side;
            for (int x = 16; x < this.xSize - 16; x += 32) {
                int width = Math.min(32, this.xSize - 16 - x);
                this.drawTexturedRect(x, y, width, 32.0, 32.0, v);
            }
        }
        for (side = 0; side < 2; ++side) {
            int x = this.xSize * side - 16;
            int u = 64 * side;
            for (int y = 16; y < this.ySize - 16; y += 32) {
                int height = Math.min(32, this.ySize - 16 - y);
                this.drawTexturedRect(x, y, 32.0, height, u, 32.0);
            }
        }
        for (int y = 16; y < this.ySize - 16; y += 32) {
            int height = Math.min(32, this.ySize - 16 - y);
            for (int x = 16; x < this.xSize - 16; x += 32) {
                int width = Math.min(32, this.xSize - 16 - x);
                this.drawTexturedRect(x, y, width, height, 32.0, 32.0);
            }
        }
    }

    @Override
    protected ResourceLocation getTexture() {
        return null;
    }

}

