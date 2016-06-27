/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.wiring;

import com.google.common.base.Supplier;
import ic2.core.ContainerBase;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.comp.Energy;
import ic2.core.block.wiring.ContainerElectricBlock;
import ic2.core.block.wiring.TileEntityElectricBlock;
import ic2.core.gui.EnergyGauge;
import ic2.core.gui.GuiElement;
import ic2.core.gui.IClickHandler;
import ic2.core.gui.VanillaButton;
import ic2.core.init.Localization;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class GuiElectricBlock
extends GuiIC2<ContainerElectricBlock> {
    private static final ResourceLocation background = new ResourceLocation(IC2.textureDomain, "textures/gui/GUIElectricBlock.png");

    public GuiElectricBlock(final ContainerElectricBlock container) {
        super(container, 196);
        this.addElement(EnergyGauge.asBar(this, 79, 38, (TileEntityBlock)container.base));
        this.addElement(((VanillaButton)new VanillaButton(this, 152, 4, 20, 20, this.createEventSender(0)).withIcon(new Supplier<ItemStack>(){

            public ItemStack get() {
                return new ItemStack(Items.REDSTONE);
            }
        })).withTooltip(new Supplier<String>(){

            public String get() {
                return ((TileEntityElectricBlock)container.base).getRedstoneMode();
            }
        }));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        super.drawGuiContainerForegroundLayer(par1, par2);
        this.fontRendererObj.drawString(Localization.translate("ic2.EUStorage.gui.info.armor"), 8, this.ySize - 126 + 3, 4210752);
        this.fontRendererObj.drawString(Localization.translate("ic2.EUStorage.gui.info.level"), 79, 25, 4210752);
        int e = (int)Math.min(((TileEntityElectricBlock)((ContainerElectricBlock)this.container).base).energy.getEnergy(), ((TileEntityElectricBlock)((ContainerElectricBlock)this.container).base).energy.getCapacity());
        this.fontRendererObj.drawString(" " + e, 110, 35, 4210752);
        this.fontRendererObj.drawString("/" + (int)((TileEntityElectricBlock)((ContainerElectricBlock)this.container).base).energy.getCapacity(), 110, 45, 4210752);
        String output = Localization.translate("ic2.EUStorage.gui.info.output", ((TileEntityElectricBlock)((ContainerElectricBlock)this.container).base).output);
        this.fontRendererObj.drawString(output, 85, 60, 4210752);
    }

    @Override
    protected ResourceLocation getTexture() {
        return background;
    }

}

