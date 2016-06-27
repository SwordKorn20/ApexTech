/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.EnumFacing
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.kineticgenerator.tileentity;

import ic2.api.energy.tile.IKineticSource;
import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.Energy;
import ic2.core.block.comp.TileEntityComponent;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableItemStack;
import ic2.core.block.invslot.InvSlotDischarge;
import ic2.core.block.kineticgenerator.container.ContainerElectricKineticGenerator;
import ic2.core.block.kineticgenerator.gui.GuiElectricKineticGenertor;
import ic2.core.block.state.IIdProvider;
import ic2.core.init.MainConfig;
import ic2.core.item.type.CraftingItemType;
import ic2.core.ref.ItemName;
import ic2.core.util.ConfigUtil;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityElectricKineticGenerator
extends TileEntityInventory
implements IKineticSource,
IHasGui {
    public InvSlotConsumableItemStack slotMotor;
    public InvSlotDischarge dischargeSlot;
    private final float kuPerEU = 4.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/kineticgenerator/electric");
    private boolean newActive;
    public double ku = 0.0;
    public final int maxKU = 1000;
    protected final Energy energy;

    public TileEntityElectricKineticGenerator() {
        this.slotMotor = new InvSlotConsumableItemStack((TileEntityInventory)this, "slotMotor", 10, ItemName.crafting.getItemStack(CraftingItemType.electric_motor));
        this.slotMotor.setStackSizeLimit(1);
        this.dischargeSlot = new InvSlotDischarge(this, InvSlot.Access.NONE, 4);
        this.energy = this.addComponent(Energy.asBasicSink(this, 10000.0, 4).addManagedSlot(this.dischargeSlot));
        this.newActive = false;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.updateDirections();
    }

    @Override
    public void setFacing(EnumFacing facing) {
        super.setFacing(facing);
        this.updateDirections();
    }

    private void updateDirections() {
        this.energy.setDirections(EnumSet.complementOf(EnumSet.of(this.getFacing())), Collections.<EnumFacing>emptySet());
    }

    @Override
    public int maxrequestkineticenergyTick(EnumFacing directionFrom) {
        if (directionFrom != this.getFacing()) {
            return 0;
        }
        return (int)Math.min((double)this.getMaxKU(), this.ku);
    }

    public int getMaxKU() {
        int counter = 0;
        int a = this.getMaxKUForGUI() / 10;
        for (int i = 0; i < this.slotMotor.size(); ++i) {
            if (this.slotMotor.get(i) == null) continue;
            counter += a;
        }
        return counter;
    }

    public int getMaxKUForGUI() {
        return 1000;
    }

    @Override
    public int requestkineticenergy(EnumFacing directionFrom, int requestkineticenergy) {
        int max = this.maxrequestkineticenergyTick(directionFrom);
        int out = max > requestkineticenergy ? requestkineticenergy : max;
        this.ku -= (double)out;
        this.markDirty();
        return out;
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        if (1000.0 - this.ku > 1.0) {
            double max = Math.min(1000.0 - this.ku, this.energy.getEnergy() * (double)this.kuPerEU);
            this.energy.useEnergy(max / (double)this.kuPerEU);
            this.ku += max;
            if (max > 0.0) {
                this.markDirty();
                this.newActive = true;
            } else {
                this.newActive = false;
            }
        } else {
            this.newActive = false;
        }
        if (this.getActive() != this.newActive) {
            this.setActive(this.newActive);
        }
    }

    public ContainerBase<TileEntityElectricKineticGenerator> getGuiContainer(EntityPlayer player) {
        return new ContainerElectricKineticGenerator(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiElectricKineticGenertor((ContainerElectricKineticGenerator)this.getGuiContainer(player));
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }
}

