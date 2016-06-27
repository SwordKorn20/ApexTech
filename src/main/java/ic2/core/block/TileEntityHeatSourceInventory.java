/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.EnumFacing
 */
package ic2.core.block;

import ic2.api.energy.tile.IHeatSource;
import ic2.core.IC2;
import ic2.core.Platform;
import ic2.core.block.TileEntityInventory;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public abstract class TileEntityHeatSourceInventory
extends TileEntityInventory
implements IHeatSource {
    protected int transmitHeat;
    protected int maxHeatEmitpeerTick;
    protected int HeatBuffer;

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        int amount = this.getMaxHeatEmittedPerTick() - this.HeatBuffer;
        if (amount > 0) {
            this.addtoHeatBuffer(this.fillHeatBuffer(amount));
        }
    }

    @Override
    public int maxrequestHeatTick(EnumFacing directionFrom) {
        if (this.facingMatchesDirection(directionFrom)) {
            return this.getMaxHeatEmittedPerTick();
        }
        return 0;
    }

    @Override
    public int requestHeat(EnumFacing directionFrom, int requestheat) {
        if (this.facingMatchesDirection(directionFrom)) {
            int heatbuffertemp = this.getHeatBuffer();
            if (this.getHeatBuffer() >= requestheat) {
                this.setHeatBuffer(this.getHeatBuffer() - requestheat);
                this.transmitHeat = requestheat;
                return requestheat;
            }
            this.transmitHeat = heatbuffertemp;
            this.setHeatBuffer(0);
            return heatbuffertemp;
        }
        return 0;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);
        this.HeatBuffer = nbtTagCompound.getInteger("HeatBuffer");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("HeatBuffer", this.HeatBuffer);
        return nbt;
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (IC2.platform.isSimulating()) {
            this.maxHeatEmitpeerTick = this.getMaxHeatEmittedPerTick();
        }
    }

    @Override
    protected void onLoaded() {
        super.onLoaded();
        if (IC2.platform.isSimulating()) {
            this.maxHeatEmitpeerTick = this.getMaxHeatEmittedPerTick();
        }
    }

    public boolean facingMatchesDirection(EnumFacing direction) {
        return direction == this.getFacing();
    }

    public int getHeatBuffer() {
        return this.HeatBuffer;
    }

    public void setHeatBuffer(int HeatBuffer) {
        this.HeatBuffer = HeatBuffer;
    }

    public void addtoHeatBuffer(int heat) {
        this.setHeatBuffer(this.getHeatBuffer() + heat);
    }

    public int gettransmitHeat() {
        return this.transmitHeat;
    }

    protected abstract int fillHeatBuffer(int var1);

    public abstract int getMaxHeatEmittedPerTick();
}

