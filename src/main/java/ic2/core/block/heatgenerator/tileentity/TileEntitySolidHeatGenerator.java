/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.heatgenerator.tileentity;

import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityHeatSourceInventory;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.heatgenerator.container.ContainerSolidHeatGenerator;
import ic2.core.block.heatgenerator.gui.GuiSolidHeatGenerator;
import ic2.core.block.invslot.InvSlotConsumableFuel;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.state.IIdProvider;
import ic2.core.gui.dynamic.IGuiValueProvider;
import ic2.core.init.MainConfig;
import ic2.core.item.type.MiscResourceType;
import ic2.core.ref.ItemName;
import ic2.core.util.ConfigUtil;
import java.util.Random;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntitySolidHeatGenerator
extends TileEntityHeatSourceInventory
implements IHasGui,
IGuiValueProvider {
    private int heatbuffer = 0;
    public int activityMeter = 0;
    public int ticksSinceLastActiveUpdate;
    public int fuel = 0;
    public int itemFuelTime = 0;
    public final InvSlotConsumableFuel fuelSlot;
    public final InvSlotOutput outputslot;
    public static final int emittedHU = Math.round(20.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/heatgenerator/solid"));

    public TileEntitySolidHeatGenerator() {
        this.fuelSlot = new InvSlotConsumableFuel(this, "fuel", 1, false);
        this.outputslot = new InvSlotOutput(this, "output", 1);
        this.ticksSinceLastActiveUpdate = IC2.random.nextInt(256);
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        boolean needsInvUpdate = false;
        if (this.needsFuel()) {
            needsInvUpdate = this.gainFuel();
        }
        boolean newActive = this.gainheat();
        if (needsInvUpdate) {
            this.markDirty();
        }
        if (!this.delayActiveUpdate()) {
            this.setActive(newActive);
        } else {
            if (this.ticksSinceLastActiveUpdate % 256 == 0) {
                this.setActive(this.activityMeter > 0);
                this.activityMeter = 0;
            }
            this.activityMeter = newActive ? ++this.activityMeter : --this.activityMeter;
            ++this.ticksSinceLastActiveUpdate;
        }
    }

    public boolean gainheat() {
        if (this.isConverting()) {
            this.heatbuffer += this.getMaxHeatEmittedPerTick();
            --this.fuel;
            if (this.fuel == 0 && (int)(Math.random() * 2.0) == 1) {
                this.outputslot.add(ItemName.misc_resource.getItemStack(MiscResourceType.ashes));
            }
            return true;
        }
        return false;
    }

    public boolean needsFuel() {
        return this.fuel <= 0 && this.getHeatBuffer() == 0;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.fuel = nbt.getInteger("fuel");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("fuel", this.fuel);
        return nbt;
    }

    public boolean delayActiveUpdate() {
        return false;
    }

    public boolean gainFuel() {
        if (this.outputslot.canAdd(ItemName.misc_resource.getItemStack(MiscResourceType.ashes))) {
            int fuelValue = this.fuelSlot.consumeFuel() / 4;
            if (fuelValue == 0) {
                return false;
            }
            this.fuel += fuelValue;
            this.itemFuelTime = fuelValue;
            return true;
        }
        return false;
    }

    public boolean isConverting() {
        return this.fuel > 0;
    }

    @Override
    protected int fillHeatBuffer(int maxAmount) {
        if (this.heatbuffer - maxAmount >= 0) {
            this.heatbuffer -= maxAmount;
            return maxAmount;
        }
        maxAmount = this.heatbuffer;
        this.heatbuffer = 0;
        return maxAmount;
    }

    @Override
    public int getMaxHeatEmittedPerTick() {
        return emittedHU;
    }

    public ContainerBase<TileEntitySolidHeatGenerator> getGuiContainer(EntityPlayer player) {
        return new ContainerSolidHeatGenerator(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiSolidHeatGenerator(new ContainerSolidHeatGenerator(player, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }

    @Override
    public double getGuiValue(String name) {
        if (name.equals("fuel")) {
            return this.fuel == 0 ? 0.0 : (double)this.fuel / (double)this.itemFuelTime;
        }
        throw new IllegalArgumentException();
    }
}

