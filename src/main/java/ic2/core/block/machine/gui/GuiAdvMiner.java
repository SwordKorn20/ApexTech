/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Supplier
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.machine.gui;

import com.google.common.base.Supplier;
import ic2.core.ContainerBase;
import ic2.core.GuiIC2;
import ic2.core.IC2;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.machine.container.ContainerAdvMiner;
import ic2.core.block.machine.tileentity.TileEntityAdvMiner;
import ic2.core.gui.BasicButton;
import ic2.core.gui.EnergyGauge;
import ic2.core.gui.GuiElement;
import ic2.core.gui.IClickHandler;
import ic2.core.init.Localization;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class GuiAdvMiner
extends GuiIC2<ContainerAdvMiner> {
    public GuiAdvMiner(final ContainerAdvMiner container) {
        super(container, 203);
        this.addElement(EnergyGauge.asBolt(this, 12, 55, (TileEntityBlock)container.base));
        this.addElement(BasicButton.create(this, 133, 101, this.createEventSender(0), BasicButton.ButtonStyle.AdvMinerReset).withTooltip("ic2.AdvMiner.gui.switch.reset"));
        this.addElement(BasicButton.create(this, 123, 27, this.createEventSender(1), BasicButton.ButtonStyle.AdvMinerMode).withTooltip("ic2.AdvMiner.gui.switch.mode"));
        this.addElement(BasicButton.create(this, 129, 45, this.createEventSender(2), BasicButton.ButtonStyle.AdvMinerSilkTouch).withTooltip(new Supplier<String>(){

            public String get() {
                return Localization.translate("ic2.AdvMiner.gui.switch.silktouch", ((TileEntityAdvMiner)container.base).silkTouch);
            }
        }));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        BlockPos target = ((TileEntityAdvMiner)((ContainerAdvMiner)this.container).base).getMineTarget();
        if (target != null) {
            this.fontRendererObj.drawString(Localization.translate("ic2.AdvMiner.gui.info.minelevel", target.getX(), target.getZ(), target.getY()), 28, 105, 2157374);
        }
        if (((TileEntityAdvMiner)((ContainerAdvMiner)this.container).base).blacklist) {
            this.fontRendererObj.drawString(Localization.translate("ic2.AdvMiner.gui.mode.blacklist"), 40, 31, 2157374);
        } else {
            this.fontRendererObj.drawString(Localization.translate("ic2.AdvMiner.gui.mode.whitelist"), 40, 31, 2157374);
        }
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    public ResourceLocation getTexture() {
        return new ResourceLocation(IC2.textureDomain, "textures/gui/GUIAdvMiner.png");
    }

}

