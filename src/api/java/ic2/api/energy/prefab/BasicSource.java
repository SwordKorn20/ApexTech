/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.ITickable
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.FMLCommonHandler
 *  net.minecraftforge.fml.common.eventhandler.Event
 *  net.minecraftforge.fml.common.eventhandler.EventBus
 *  net.minecraftforge.fml.relauncher.Side
 */
package ic2.api.energy.prefab;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.IEnergyNet;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.info.Info;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItemManager;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.relauncher.Side;

public class BasicSource
extends TileEntity
implements IEnergySource,
ITickable {
    public final TileEntity parent;
    protected double capacity;
    protected int tier;
    protected double power;
    protected double energyStored;
    protected boolean addedToEnet;

    public BasicSource(TileEntity parent1, double capacity1, int tier1) {
        double power = EnergyNet.instance.getPowerFromTier(tier1);
        this.parent = parent1;
        this.capacity = capacity1 < power ? power : capacity1;
        this.tier = tier1;
        this.power = power;
    }

    public void update() {
        if (!this.addedToEnet) {
            this.onLoaded();
        }
    }

    public void onLoaded() {
        if (!this.addedToEnet && !this.parent.getWorld().isRemote && Info.isIc2Available()) {
            this.worldObj = this.parent.getWorld();
            this.pos = this.parent.getPos();
            MinecraftForge.EVENT_BUS.post((Event)new EnergyTileLoadEvent(this));
            this.addedToEnet = true;
        }
    }

    public void invalidate() {
        super.invalidate();
        this.onChunkUnload();
    }

    public void onChunkUnload() {
        if (this.addedToEnet && Info.isIc2Available()) {
            MinecraftForge.EVENT_BUS.post((Event)new EnergyTileUnloadEvent(this));
            this.addedToEnet = false;
        }
    }

    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        NBTTagCompound data = tag.getCompoundTag("IC2BasicSource");
        this.energyStored = data.getDouble("energy");
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        try {
            super.writeToNBT(tag);
        }
        catch (RuntimeException var2_2) {
            // empty catch block
        }
        NBTTagCompound data = new NBTTagCompound();
        data.setDouble("energy", this.energyStored);
        tag.setTag("IC2BasicSource", (NBTBase)data);
        return tag;
    }

    public double getCapacity() {
        return this.capacity;
    }

    public void setCapacity(double capacity1) {
        if (capacity1 < this.power) {
            capacity1 = this.power;
        }
        this.capacity = capacity1;
    }

    public int getTier() {
        return this.tier;
    }

    public void setTier(int tier1) {
        double power = EnergyNet.instance.getPowerFromTier(tier1);
        if (this.capacity < power) {
            this.capacity = power;
        }
        this.tier = tier1;
        this.power = power;
    }

    public double getEnergyStored() {
        return this.energyStored;
    }

    public void setEnergyStored(double amount) {
        this.energyStored = amount;
    }

    public double getFreeCapacity() {
        return this.capacity - this.energyStored;
    }

    public double addEnergy(double amount) {
        if (FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            return 0.0;
        }
        if (amount > this.capacity - this.energyStored) {
            amount = this.capacity - this.energyStored;
        }
        this.energyStored += amount;
        return amount;
    }

    public boolean charge(ItemStack stack) {
        if (stack == null || !Info.isIc2Available()) {
            return false;
        }
        double amount = ElectricItem.manager.charge(stack, this.energyStored, this.tier, false, false);
        this.energyStored -= amount;
        return amount > 0.0;
    }

    @Deprecated
    public void onupdate() {
        this.update();
    }

    @Deprecated
    public void onInvalidate() {
        this.invalidate();
    }

    @Deprecated
    public void onOnChunkUnload() {
        this.onChunkUnload();
    }

    @Deprecated
    public void onReadFromNbt(NBTTagCompound tag) {
        this.readFromNBT(tag);
    }

    @Deprecated
    public void onWriteToNbt(NBTTagCompound tag) {
        this.writeToNBT(tag);
    }

    @Override
    public boolean emitsEnergyTo(IEnergyAcceptor receiver, EnumFacing direction) {
        return true;
    }

    @Override
    public double getOfferedEnergy() {
        return Math.min(this.energyStored, this.power);
    }

    @Override
    public void drawEnergy(double amount) {
        this.energyStored -= amount;
    }

    @Override
    public int getSourceTier() {
        return this.tier;
    }
}

