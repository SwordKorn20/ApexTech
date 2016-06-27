/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.nbt.NBTTagCompound
 */
package ic2.core.block.comp;

import ic2.core.IC2;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.comp.Components;
import ic2.core.network.GrowingBuffer;
import ic2.core.network.NetworkManager;
import ic2.core.util.SideGateway;
import java.io.DataInput;
import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

public abstract class TileEntityComponent {
    protected final TileEntityBlock parent;

    public TileEntityComponent(TileEntityBlock parent) {
        this.parent = parent;
    }

    public TileEntityBlock getParent() {
        return this.parent;
    }

    public void readFromNbt(NBTTagCompound nbt) {
    }

    public NBTTagCompound writeToNbt() {
        return null;
    }

    public void onLoaded() {
    }

    public void onUnloaded() {
    }

    public void onNeighborChange(Block srcBlock) {
    }

    public void onContainerUpdate(EntityPlayerMP player) {
    }

    public void onNetworkUpdate(DataInput is) throws IOException {
    }

    public boolean enableWorldTick() {
        return false;
    }

    public void onWorldTick() {
    }

    protected void setNetworkUpdate(EntityPlayerMP player, GrowingBuffer data) {
        IC2.network.get(true).sendComponentUpdate(this.parent, Components.getId(this.getClass()), player, data);
    }
}

