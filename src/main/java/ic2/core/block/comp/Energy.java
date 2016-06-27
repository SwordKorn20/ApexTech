/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.Event
 *  net.minecraftforge.fml.common.eventhandler.EventBus
 */
package ic2.core.block.comp;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.IEnergyNet;
import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.comp.TileEntityComponent;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotCharge;
import ic2.core.block.invslot.InvSlotDischarge;
import ic2.core.network.GrowingBuffer;
import ic2.core.util.Util;
import java.io.DataInput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;

public class Energy
extends TileEntityComponent {
    private double capacity;
    private double storage;
    private int sinkTier;
    private int sourceTier;
    private Set<EnumFacing> sinkDirections;
    private Set<EnumFacing> sourceDirections;
    private List<InvSlot> managedSlots;
    private EnergyNetDelegate delegate;
    private boolean loaded;
    private boolean disabled;
    private final boolean fullEnergy;

    public static Energy asBasicSink(TileEntityBlock parent, double capacity) {
        return Energy.asBasicSink(parent, capacity, 1);
    }

    public static Energy asBasicSink(TileEntityBlock parent, double capacity, int tier) {
        return new Energy(parent, capacity, Util.allFacings, Collections.<EnumFacing>emptySet(), tier);
    }

    public static Energy asBasicSource(TileEntityBlock parent, double capacity) {
        return Energy.asBasicSource(parent, capacity, 1);
    }

    public static Energy asBasicSource(TileEntityBlock parent, double capacity, int tier) {
        return new Energy(parent, capacity, Collections.<EnumFacing>emptySet(), Util.allFacings, tier);
    }

    public Energy(TileEntityBlock parent, double capacity) {
        this(parent, capacity, Collections.emptySet(), Collections.emptySet(), 1);
    }

    public Energy(TileEntityBlock parent, double capacity, Set<EnumFacing> sinkDirections, Set<EnumFacing> sourceDirections, int tier) {
        this(parent, capacity, sinkDirections, sourceDirections, tier, tier, false);
    }

    public Energy(TileEntityBlock parent, double capacity, Set<EnumFacing> sinkDirections, Set<EnumFacing> sourceDirections, int sinkTier, int sourceTier, boolean fullEnergy) {
        super(parent);
        this.capacity = capacity;
        this.sinkTier = sinkTier;
        this.sourceTier = sourceTier;
        this.sinkDirections = sinkDirections;
        this.sourceDirections = sourceDirections;
        this.fullEnergy = fullEnergy;
    }

    public Energy addManagedSlot(InvSlot slot) {
        if (slot instanceof InvSlotCharge || slot instanceof InvSlotDischarge) {
            if (this.managedSlots == null) {
                this.managedSlots = new ArrayList<InvSlot>(4);
            }
        } else {
            throw new IllegalArgumentException("No charge/discharge slot.");
        }
        this.managedSlots.add(slot);
        return this;
    }

    @Override
    public void readFromNbt(NBTTagCompound nbt) {
        this.storage = nbt.getDouble("storage");
    }

    @Override
    public NBTTagCompound writeToNbt() {
        NBTTagCompound ret = new NBTTagCompound();
        ret.setDouble("storage", this.storage);
        return ret;
    }

    @Override
    public void onLoaded() {
        assert (this.delegate == null);
        if (!(this.sinkDirections.isEmpty() && this.sourceDirections.isEmpty() || this.parent.getWorld().isRemote)) {
            this.createDelegate();
            MinecraftForge.EVENT_BUS.post((Event)new EnergyTileLoadEvent(this.delegate));
        }
        this.loaded = true;
    }

    private void createDelegate() {
        if (this.delegate != null) {
            throw new IllegalStateException();
        }
        this.delegate = new EnergyNetDelegate();
        this.delegate.setWorldObj(this.parent.getWorld());
        this.delegate.setPos(this.parent.getPos());
    }

    @Override
    public void onUnloaded() {
        if (this.delegate != null) {
            MinecraftForge.EVENT_BUS.post((Event)new EnergyTileUnloadEvent(this.delegate));
            this.delegate = null;
        }
        this.loaded = false;
    }

    @Override
    public void onContainerUpdate(EntityPlayerMP player) {
        GrowingBuffer buffer = new GrowingBuffer(16);
        buffer.writeDouble(this.capacity);
        buffer.writeDouble(this.storage);
        buffer.flip();
        this.setNetworkUpdate(player, buffer);
    }

    @Override
    public void onNetworkUpdate(DataInput is) throws IOException {
        this.capacity = is.readDouble();
        this.storage = is.readDouble();
    }

    @Override
    public boolean enableWorldTick() {
        return !this.parent.getWorld().isRemote && this.managedSlots != null;
    }

    @Override
    public void onWorldTick() {
        for (InvSlot slot : this.managedSlots) {
            double space;
            if (slot instanceof InvSlotCharge) {
                if (this.storage <= 0.0) continue;
                this.storage -= ((InvSlotCharge)slot).charge(this.storage);
                continue;
            }
            if (!(slot instanceof InvSlotDischarge) || (space = this.capacity - this.storage) <= 0.0) continue;
            this.storage += ((InvSlotDischarge)slot).discharge(space, false);
        }
    }

    public double getCapacity() {
        return this.capacity;
    }

    public void setCapacity(double capacity) {
        this.capacity = capacity;
    }

    public double getEnergy() {
        return this.storage;
    }

    public double getFreeEnergy() {
        return Math.max(0.0, this.capacity - this.storage);
    }

    public double getFillRatio() {
        return this.storage / this.capacity;
    }

    public int getComparatorValue() {
        return Math.min((int)(this.storage * 15.0 / this.capacity), 15);
    }

    public double addEnergy(double amount) {
        amount = Math.min(this.capacity - this.storage, amount);
        this.storage += amount;
        return amount;
    }

    public void forceAddEnergy(double amount) {
        this.storage += amount;
    }

    public boolean canUseEnergy(double amount) {
        return this.storage >= amount;
    }

    public boolean useEnergy(double amount) {
        if (this.storage >= amount) {
            this.storage -= amount;
            return true;
        }
        return false;
    }

    public double useEnergy(double amount, boolean simulate) {
        double ret = Math.abs(Math.max(0.0, amount - this.storage) - amount);
        if (simulate) {
            return ret;
        }
        this.storage -= ret;
        return ret;
    }

    public int getSinkTier() {
        return this.sinkTier;
    }

    public void setSinkTier(int tier) {
        this.sinkTier = tier;
    }

    public int getSourceTier() {
        return this.sourceTier;
    }

    public void setSourceTier(int tier) {
        this.sourceTier = tier;
    }

    public void setEnabled(boolean enabled) {
        this.disabled = !enabled;
    }

    public void setDirections(Set<EnumFacing> sinkDirections, Set<EnumFacing> sourceDirections) {
        if (sinkDirections.equals(this.sinkDirections) && sourceDirections.equals(this.sourceDirections)) {
            return;
        }
        if (this.delegate != null) {
            MinecraftForge.EVENT_BUS.post((Event)new EnergyTileUnloadEvent(this.delegate));
        }
        this.sinkDirections = sinkDirections;
        this.sourceDirections = sourceDirections;
        if (sinkDirections.isEmpty() && sourceDirections.isEmpty()) {
            this.delegate = null;
        } else if (this.delegate == null && this.loaded) {
            this.createDelegate();
        }
        if (this.delegate != null) {
            MinecraftForge.EVENT_BUS.post((Event)new EnergyTileLoadEvent(this.delegate));
        }
    }

    public Set<EnumFacing> getSourceDirs() {
        return Collections.unmodifiableSet(this.sourceDirections);
    }

    public Set<EnumFacing> getSinkDirs() {
        return Collections.unmodifiableSet(this.sinkDirections);
    }

    public IEnergyTile getDelegate() {
        return this.delegate;
    }

    private class EnergyNetDelegate
    extends TileEntity
    implements IEnergySink,
    IEnergySource {
        private EnergyNetDelegate() {
        }

        @Override
        public boolean acceptsEnergyFrom(IEnergyEmitter emitter, EnumFacing dir) {
            return Energy.this.sinkDirections.contains((Object)dir);
        }

        @Override
        public boolean emitsEnergyTo(IEnergyAcceptor receiver, EnumFacing dir) {
            return Energy.this.sourceDirections.contains((Object)dir);
        }

        @Override
        public double getDemandedEnergy() {
            return !Energy.this.disabled && !Energy.this.sinkDirections.isEmpty() && Energy.this.storage < Energy.this.capacity ? Energy.this.capacity - Energy.this.storage : 0.0;
        }

        @Override
        public double getOfferedEnergy() {
            if (Energy.this.fullEnergy) {
                return !Energy.this.disabled && !Energy.this.sourceDirections.isEmpty() && Energy.this.storage >= EnergyNet.instance.getPowerFromTier(Energy.this.sourceTier) ? EnergyNet.instance.getPowerFromTier(Energy.this.sourceTier) : 0.0;
            }
            return !Energy.this.disabled && !Energy.this.sourceDirections.isEmpty() && Energy.this.storage > 0.0 ? Math.min(Energy.this.storage, EnergyNet.instance.getPowerFromTier(Energy.this.sourceTier)) : 0.0;
        }

        @Override
        public int getSinkTier() {
            return Energy.this.sinkTier;
        }

        @Override
        public int getSourceTier() {
            return Energy.this.sourceTier;
        }

        @Override
        public double injectEnergy(EnumFacing directionFrom, double amount, double voltage) {
            Energy.this.storage = Energy.this.storage + amount;
            return 0.0;
        }

        @Override
        public void drawEnergy(double amount) {
            Energy.this.storage = Energy.this.storage - amount;
        }
    }

}

