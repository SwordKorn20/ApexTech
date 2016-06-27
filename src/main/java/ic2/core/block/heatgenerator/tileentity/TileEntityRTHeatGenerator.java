/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.heatgenerator.tileentity;

import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityHeatSourceInventory;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.heatgenerator.container.ContainerRTHeatGenerator;
import ic2.core.block.heatgenerator.gui.GuiRTHeatGenerator;
import ic2.core.block.invslot.InvSlotConsumable;
import ic2.core.block.invslot.InvSlotConsumableItemStack;
import ic2.core.block.state.IIdProvider;
import ic2.core.init.MainConfig;
import ic2.core.item.type.NuclearResourceType;
import ic2.core.ref.ItemName;
import ic2.core.util.ConfigUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityRTHeatGenerator
extends TileEntityHeatSourceInventory
implements IHasGui {
    private boolean newActive;
    public final InvSlotConsumable fuelSlot;
    public static final float outputMultiplier = 2.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/heatgenerator/radioisotope");

    public TileEntityRTHeatGenerator() {
        this.fuelSlot = new InvSlotConsumableItemStack((TileEntityInventory)this, "fuelSlot", 6, ItemName.nuclear.getItemStack(NuclearResourceType.rtg_pellet));
        this.newActive = false;
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        this.newActive = this.HeatBuffer > 0;
        if (this.getActive() != this.newActive) {
            this.setActive(this.newActive);
        }
    }

    @Override
    protected int fillHeatBuffer(int maxAmount) {
        if (maxAmount >= this.getMaxHeatEmittedPerTick()) {
            return this.getMaxHeatEmittedPerTick();
        }
        return maxAmount;
    }

    @Override
    public int getMaxHeatEmittedPerTick() {
        int counter = 0;
        for (int i = 0; i < this.fuelSlot.size(); ++i) {
            if (this.fuelSlot.get(i) == null) continue;
            ++counter;
        }
        if (counter == 0) {
            return 0;
        }
        return (int)(Math.pow(2.0, counter - 1) * (double)outputMultiplier);
    }

    public ContainerBase<TileEntityRTHeatGenerator> getGuiContainer(EntityPlayer player) {
        return new ContainerRTHeatGenerator(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiRTHeatGenerator(new ContainerRTHeatGenerator(player, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }
}

