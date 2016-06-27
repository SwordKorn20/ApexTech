/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  mezz.jei.api.IGuiHelper
 *  mezz.jei.api.gui.IDrawable
 *  mezz.jei.api.gui.IDrawableAnimated
 *  mezz.jei.api.gui.IDrawableAnimated$StartDirection
 *  mezz.jei.api.gui.IDrawableStatic
 *  mezz.jei.api.recipe.IRecipeWrapper
 *  net.minecraft.client.Minecraft
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 */
package ic2.jeiIntegration.recipe.machine;

import ic2.core.block.TileEntityBlock;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.machine.tileentity.TileEntityStandardMachine;
import ic2.core.gui.EnergyGauge;
import ic2.core.gui.Gauge;
import ic2.core.gui.GuiElement;
import ic2.core.gui.SlotGrid;
import ic2.core.gui.dynamic.GuiParser;
import ic2.core.ref.TeBlock;
import ic2.core.util.Tuple;
import ic2.jeiIntegration.SlotPosition;
import ic2.jeiIntegration.recipe.machine.IORecipeCategory;
import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.IGuiHelper;
import mezz.jei.api.gui.IDrawable;
import mezz.jei.api.gui.IDrawableAnimated;
import mezz.jei.api.gui.IDrawableStatic;
import mezz.jei.api.recipe.IRecipeWrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class DynamicCategory<T>
extends IORecipeCategory<T>
implements IDrawable {
    private static final int xOffset = 0;
    private static final int yOffset = -16;
    private List<Tuple.T2<IDrawable, SlotPosition>> elements = new ArrayList<Tuple.T2<IDrawable, SlotPosition>>();
    private List<SlotPosition> inputSlots = new ArrayList<SlotPosition>();
    private List<SlotPosition> outputSlots = new ArrayList<SlotPosition>();

    public DynamicCategory(TeBlock block, T recipeManager, IGuiHelper guiHelper) {
        super(block, recipeManager);
        this.initializeWidgets(guiHelper, GuiParser.parse(block));
    }

    private void initializeWidgets(IGuiHelper guiHelper, GuiParser.ParentNode parentNode) {
        block8 : for (GuiParser.Node rawNode : parentNode.getNodes()) {
            block0 : switch (rawNode.getType()) {
                SlotPosition pos;
                case energygauge: {
                    GuiParser.EnergyGaugeNode node = (GuiParser.EnergyGaugeNode)rawNode;
                    pos = new SlotPosition(node.x + node.style.properties.bgXOffset + 0, node.y + node.style.properties.bgYOffset + -16);
                    IDrawableStatic energyBackground = guiHelper.createDrawable(node.style.properties.texture, (int)node.style.properties.uBgInactive, (int)node.style.properties.vBgInactive, (int)node.style.properties.bgWidth, (int)node.style.properties.bgHeight);
                    this.elements.add(new Tuple.T2<IDrawableStatic, SlotPosition>(energyBackground, pos));
                    energyBackground = guiHelper.createDrawable(node.style.properties.texture, (int)node.style.properties.uInner, (int)node.style.properties.vInner, (int)node.style.properties.innerWidth, (int)node.style.properties.innerHeight);
                    IDrawableAnimated energyAnimated = guiHelper.createAnimatedDrawable(energyBackground, 300, node.style.properties.reverse ? (node.style.properties.vertical ? IDrawableAnimated.StartDirection.TOP : IDrawableAnimated.StartDirection.RIGHT) : (node.style.properties.vertical ? IDrawableAnimated.StartDirection.BOTTOM : IDrawableAnimated.StartDirection.LEFT), true);
                    this.elements.add(new Tuple.T2<IDrawableAnimated, SlotPosition>(energyAnimated, new SlotPosition(node.x + 0, node.y + -16)));
                    break;
                }
                case gauge: {
                    GuiParser.GaugeNode node = (GuiParser.GaugeNode)rawNode;
                    pos = new SlotPosition(node.x + node.style.properties.bgXOffset + 0, node.y + node.style.properties.bgYOffset + -16);
                    IDrawableStatic guageBackground = guiHelper.createDrawable(node.style.properties.texture, (int)node.style.properties.uBgActive, (int)node.style.properties.vBgActive, (int)node.style.properties.bgWidth, (int)node.style.properties.bgHeight);
                    this.elements.add(new Tuple.T2<IDrawableStatic, SlotPosition>(guageBackground, pos));
                    guageBackground = guiHelper.createDrawable(node.style.properties.texture, (int)node.style.properties.uInner, (int)node.style.properties.vInner, (int)node.style.properties.innerWidth, (int)node.style.properties.innerHeight);
                    IDrawableStatic gaugeForeground = node.style == Gauge.GaugeStyle.HeatCentrifuge ? guageBackground : guiHelper.createAnimatedDrawable(guageBackground, this.getProcessSpeed(node.name), node.style.properties.reverse ? (node.style.properties.vertical ? IDrawableAnimated.StartDirection.BOTTOM : IDrawableAnimated.StartDirection.RIGHT) : (node.style.properties.vertical ? IDrawableAnimated.StartDirection.TOP : IDrawableAnimated.StartDirection.LEFT), false);
                    this.elements.add(new Tuple.T2<IDrawableStatic, SlotPosition>(gaugeForeground, new SlotPosition(node.x + 0, node.y + -16)));
                    break;
                }
                case image: {
                    GuiParser.ImageNode node = (GuiParser.ImageNode)rawNode;
                    pos = new SlotPosition(node.x + 0, node.y + -16);
                    IDrawableStatic image = guiHelper.createDrawable(node.src, node.u, node.v, node.width, node.height);
                    this.elements.add(new Tuple.T2<IDrawableStatic, SlotPosition>(image, pos));
                    break;
                }
                case slot: {
                    GuiParser.SlotNode node = (GuiParser.SlotNode)rawNode;
                    pos = new SlotPosition(node.x + 0, node.y + -16, node.style);
                    IDrawableStatic drawable = guiHelper.createDrawable(GuiElement.commonTexture, pos.getStyle().u, pos.getStyle().v, pos.getStyle().width, pos.getStyle().height);
                    this.elements.add(new Tuple.T2<IDrawableStatic, SlotPosition>(drawable, pos));
                    if (node.name.toLowerCase().contains("input")) {
                        this.inputSlots.add(pos);
                        break;
                    }
                    if (!node.name.toLowerCase().contains("output")) break;
                    this.outputSlots.add(pos);
                    break;
                }
                case slotgrid: {
                    GuiParser.SlotGridNode node = (GuiParser.SlotGridNode)rawNode;
                    if (node.name.contains("upgrade")) continue block8;
                    TileEntityInventory dummyTe = (TileEntityInventory)this.block.getDummyTe();
                    if (dummyTe == null) {
                        throw new NullPointerException("Received null dummy for " + this.block + " in the JeiPlugin.");
                    }
                    InvSlot slot = dummyTe.getInvSlot(node.name);
                    if (slot == null) {
                        throw new RuntimeException("invalid invslot name " + node.name + " for base " + dummyTe);
                    }
                    int size = slot.size();
                    if (size <= node.offset) break;
                    GuiParser.SlotGridNode.SlotGridDimension dim = node.getDimension(size);
                    IDrawableStatic drawable = guiHelper.createDrawable(GuiElement.commonTexture, node.style.u, node.style.v, node.style.width, node.style.height);
                    boolean isInput = node.name.toLowerCase().contains("input");
                    boolean isOutput = node.name.toLowerCase().contains("output");
                    int i = 0;
                    do {
                        if (i >= dim.cols) continue block8;
                        for (int j = 0; j < dim.rows; ++j) {
                            if (i * dim.rows + j > size) break block0;
                            SlotPosition pos2 = new SlotPosition(node.x + 0 + i * node.style.width, node.y + -16 + j * node.style.height, node.style);
                            this.elements.add(new Tuple.T2<IDrawableStatic, SlotPosition>(drawable, pos2));
                            if (isInput) {
                                this.inputSlots.add(pos2);
                                continue;
                            }
                            if (!isOutput) continue;
                            this.outputSlots.add(pos2);
                        }
                        ++i;
                    } while (true);
                }
                case text: {
                    GuiParser.TextNode node = (GuiParser.TextNode)rawNode;
                    pos = new SlotPosition(node.x + 0, node.y + -16);
                    break;
                }
            }
        }
    }

    public IDrawable getBackground() {
        return this;
    }

    @Override
    public void drawAnimations(Minecraft minecraft) {
        for (Tuple.T2<IDrawable, SlotPosition> element : this.elements) {
            ((IDrawable)element.a).draw(minecraft, ((SlotPosition)element.b).getX(), ((SlotPosition)element.b).getY());
        }
    }

    @Override
    protected List<SlotPosition> getInputSlotPos() {
        return this.inputSlots;
    }

    @Override
    protected List<SlotPosition> getOutputSlotPos() {
        return this.outputSlots;
    }

    public void draw(Minecraft arg0) {
    }

    public void draw(Minecraft arg0, int arg1, int arg2) {
    }

    public int getHeight() {
        return 60;
    }

    public int getWidth() {
        return 160;
    }

    private int getProcessSpeed(String name) {
        TileEntityBlock te;
        if ("progress".equals(name) && (te = this.block.getDummyTe()) != null && te instanceof TileEntityStandardMachine) {
            return ((TileEntityStandardMachine)te).defaultOperationLength / 3;
        }
        return 200;
    }

    @Override
    protected List<List<ItemStack>> getInputStacks(IRecipeWrapper wrapper) {
        return wrapper.getInputs();
    }

    @Override
    protected List<ItemStack> getOutputStacks(IRecipeWrapper wrapper) {
        return wrapper.getOutputs();
    }

}

