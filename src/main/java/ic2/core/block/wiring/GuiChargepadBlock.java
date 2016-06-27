/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 */
package ic2.core.block.wiring;

import com.google.common.base.Supplier;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.comp.Energy;
import ic2.core.block.wiring.ContainerChargepadBlock;
import ic2.core.block.wiring.TileEntityChargepadBlock;
import ic2.core.gui.EnergyGauge;
import ic2.core.gui.GuiElement;
import ic2.core.gui.IClickHandler;
import ic2.core.gui.Text;
import ic2.core.gui.VanillaButton;
import ic2.core.gui.dynamic.TextProvider;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class GuiChargepadBlock
extends GuiIC2<ContainerChargepadBlock> {
    private static final ResourceLocation background = new ResourceLocation(IC2.textureDomain, "textures/gui/GUIChargepadBlock.png");

    public GuiChargepadBlock(final ContainerChargepadBlock container) {
        super(container);
        this.addElement(EnergyGauge.asBar(this, 79, 38, (TileEntityBlock)container.base));
        this.addElement(((VanillaButton)new VanillaButton(this, 152, 4, 20, 20, this.createEventSender(0)).withIcon(new Supplier<ItemStack>(){

            public ItemStack get() {
                return new ItemStack(Items.REDSTONE);
            }
        })).withTooltip(new Supplier<String>(){

            public String get() {
                return ((TileEntityChargepadBlock)container.base).getRedstoneMode();
            }
        }));
        this.addElement(Text.create(this, 79, 25, "ic2.EUStorage.gui.info.level", 4210752, false));
        this.addElement(Text.create(this, 110, 35, TextProvider.of(new Supplier<String>(){

            public String get() {
                return " " + (int)Math.min(((TileEntityChargepadBlock)container.base).energy.getEnergy(), ((TileEntityChargepadBlock)container.base).energy.getCapacity());
            }
        }), 4210752, false));
        this.addElement(Text.create(this, 110, 45, TextProvider.of(new Supplier<String>(){

            public String get() {
                return "/" + (int)((TileEntityChargepadBlock)container.base).energy.getCapacity();
            }
        }), 4210752, false));
    }

    @Override
    protected ResourceLocation getTexture() {
        return background;
    }

}

