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
import ic2.core.block.comp.Energy;
import ic2.core.block.comp.TileEntityComponent;
import ic2.core.block.heatgenerator.container.ContainerElectricHeatGenerator;
import ic2.core.block.heatgenerator.gui.GuiElectricHeatGenerator;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumable;
import ic2.core.block.invslot.InvSlotConsumableItemStack;
import ic2.core.block.invslot.InvSlotDischarge;
import ic2.core.block.state.IIdProvider;
import ic2.core.init.MainConfig;
import ic2.core.item.type.CraftingItemType;
import ic2.core.ref.ItemName;
import ic2.core.util.ConfigUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityElectricHeatGenerator
extends TileEntityHeatSourceInventory
implements IHasGui {
    private boolean newActive;
    public final InvSlotDischarge dischargeSlot;
    public final InvSlotConsumable CoilSlot;
    protected final Energy energy;
    public static final double outputMultiplier = ConfigUtil.getFloat(MainConfig.get(), "balance/energy/heatgenerator/electric");

    public TileEntityElectricHeatGenerator() {
        this.CoilSlot = new InvSlotConsumableItemStack((TileEntityInventory)this, "CoilSlot", 10, ItemName.crafting.getItemStack(CraftingItemType.coil));
        this.CoilSlot.setStackSizeLimit(1);
        this.dischargeSlot = new InvSlotDischarge(this, InvSlot.Access.NONE, 4);
        this.energy = this.addComponent(Energy.asBasicSink(this, 10000.0, 4).addManagedSlot(this.dischargeSlot));
        this.newActive = false;
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        if (this.getActive() != this.newActive) {
            this.setActive(this.newActive);
        }
    }

    public ContainerBase<TileEntityElectricHeatGenerator> getGuiContainer(EntityPlayer player) {
        return new ContainerElectricHeatGenerator(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiElectricHeatGenerator(new ContainerElectricHeatGenerator(player, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }

    @Override
    protected int fillHeatBuffer(int maxAmount) {
        int amount = Math.min(maxAmount, (int)(this.energy.getEnergy() / outputMultiplier));
        if (amount > 0) {
            this.energy.useEnergy((double)amount / outputMultiplier);
            this.newActive = true;
        } else {
            this.newActive = false;
        }
        return amount;
    }

    @Override
    public int getMaxHeatEmittedPerTick() {
        int counter = 0;
        for (int i = 0; i < this.CoilSlot.size(); ++i) {
            if (this.CoilSlot.get(i) == null) continue;
            ++counter;
        }
        return counter * 10;
    }

    public final float getChargeLevel() {
        return (float)Math.min(1.0, this.energy.getFillRatio());
    }
}

