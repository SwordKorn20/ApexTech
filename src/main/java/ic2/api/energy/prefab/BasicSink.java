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

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
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

public class BasicSink
extends TileEntity
implements IEnergySink,
ITickable {
    public final TileEntity parent;
    protected int capacity;
    protected int tier;
    protected double energyStored;
    protected boolean addedToEnet;

    public BasicSink(TileEntity parent1, int capacity1, int tier1) {
        this.parent = parent1;
        this.capacity = capacity1;
        this.tier = tier1;
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
        NBTTagCompound data = tag.getCompoundTag("IC2BasicSink");
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
        tag.setTag("IC2BasicSink", (NBTBase)data);
        return tag;
    }

    public int getCapacity() {
        return this.capacity;
    }

    public void setCapacity(int capacity1) {
        this.capacity = capacity1;
    }

    public int getTier() {
        return this.tier;
    }

    public void setTier(int tier1) {
        this.tier = tier1;
    }

    public double getEnergyStored() {
        return this.energyStored;
    }

    public void setEnergyStored(double amount) {
        this.energyStored = amount;
    }

    public boolean canUseEnergy(double amount) {
        return this.energyStored >= amount;
    }

    public boolean useEnergy(double amount) {
        if (this.canUseEnergy(amount) && !FMLCommonHandler.instance().getEffectiveSide().isClient()) {
            this.energyStored -= amount;
            return true;
        }
        return false;
    }

    public boolean discharge(ItemStack stack, int limit) {
        if (stack == null || !Info.isIc2Available()) {
            return false;
        }
        double amount = (double)this.capacity - this.energyStored;
        if (amount <= 0.0) {
            return false;
        }
        if (limit > 0 && (double)limit < amount) {
            amount = limit;
        }
        amount = ElectricItem.manager.discharge(stack, amount, this.tier, limit > 0, true, false);
        this.energyStored += amount;
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
    public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing direction) {
        return true;
    }

    @Override
    public double getDemandedEnergy() {
        return Math.max(0.0, (double)this.capacity - this.energyStored);
    }

    @Override
    public double injectEnergy(EnumFacing directionFrom, double amount, double voltage) {
        this.energyStored += amount;
        return 0.0;
    }

    @Override
    public int getSinkTier() {
        return this.tier;
    }
}

