/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableSet
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.nbt.NBTTagLong
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.ChunkPos
 *  net.minecraft.world.World
 *  net.minecraftforge.common.ForgeChunkManager
 *  net.minecraftforge.common.ForgeChunkManager$Ticket
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.machine.tileentity;

import com.google.common.collect.ImmutableSet;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.core.ChunkLoaderLogic;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.Platform;
import ic2.core.block.comp.Energy;
import ic2.core.block.machine.container.ContainerChunkLoader;
import ic2.core.block.machine.gui.GuiChunkLoader;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import ic2.core.util.Log;
import ic2.core.util.LogCategory;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagLong;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeChunkManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityChunkloader
extends TileEntityElectricMachine
implements INetworkClientTileEntityEventListener,
IHasGui {
    private ForgeChunkManager.Ticket ticket;
    private final Set<ChunkPos> loadedChunks = new HashSet<ChunkPos>();

    public TileEntityChunkloader() {
        super(2500, 1, true);
    }

    @Override
    public void updateEntityServer() {
        super.updateEntityServer();
        boolean active = this.energy.useEnergy(this.getLoadedChunks().size());
        if (active != this.getActive()) {
            this.setActive(active);
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        NBTTagList list = nbt.getTagList("loadedChunks", 4);
        this.loadedChunks.clear();
        for (int i = 0; i < list.tagCount(); ++i) {
            NBTTagLong currentNBT = (NBTTagLong)list.get(i);
            long value = currentNBT.getLong();
            this.loadedChunks.add(ChunkLoaderLogic.deserialize(value));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        NBTTagList list = new NBTTagList();
        nbt.setTag("loadedChunks", (NBTBase)list);
        for (ChunkPos chunk : this.loadedChunks) {
            list.appendTag((NBTBase)new NBTTagLong(ChunkLoaderLogic.serialize(chunk)));
        }
        return nbt;
    }

    @Override
    public void setActive(boolean active) {
        if (!this.worldObj.isRemote && this.getActive() != active) {
            if (active) {
                if (this.ticket != null) {
                    throw new IllegalStateException("Cannot activate ChunkLoader: " + (Object)this.pos + " " + (Object)this.ticket);
                }
                this.ticket = ChunkLoaderLogic.getInstance().createTicket(this.worldObj, this.pos);
                for (ChunkPos coords : this.loadedChunks) {
                    ChunkLoaderLogic.getInstance().addChunkToTicket(this.ticket, coords);
                }
            } else {
                if (this.ticket == null) {
                    throw new IllegalStateException("Cannot deactivate ChunkLoader: " + (Object)this.pos + " " + (Object)this.ticket);
                }
                ChunkLoaderLogic.getInstance().removeTicket(this.ticket);
                this.ticket = null;
            }
        }
        super.setActive(active);
    }

    @Override
    public void onLoaded() {
        super.onLoaded();
        if (!this.worldObj.isRemote) {
            this.ticket = ChunkLoaderLogic.getInstance().getTicket(this.worldObj, this.pos, false);
            if (this.ticket != null) {
                this.loadedChunks.clear();
                this.loadedChunks.addAll((Collection<ChunkPos>)this.ticket.getChunkList());
            }
            super.setActive(this.ticket != null);
        }
    }

    @Override
    protected void onUnloaded() {
        super.onUnloaded();
        this.ticket = null;
    }

    @Override
    public void onPlaced(ItemStack stack, EntityLivingBase placer, EnumFacing facing) {
        super.onPlaced(stack, placer, facing);
        this.loadedChunks.add(ChunkLoaderLogic.getChunkCoords(this.pos));
    }

    @Override
    protected boolean onActivated(EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (this.worldObj.isRemote) {
            return true;
        }
        return IC2.platform.launchGui(player, this);
    }

    public ContainerBase<TileEntityChunkloader> getGuiContainer(EntityPlayer player) {
        return new ContainerChunkLoader(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiChunkLoader(new ContainerChunkLoader(player, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }

    public void addChunkToLoaded(ChunkPos chunk) {
        if (this.worldObj.isRemote) {
            new RuntimeException("Something tried to change the ChunkLoaderState on the client.").printStackTrace();
            return;
        }
        if (!this.isChunkInRange(chunk)) {
            IC2.log.warn(LogCategory.Block, "Trying to add a Chunk to loaded, however the chunk is too far away. Aborting.");
            return;
        }
        if (this.getLoadedChunks().size() < ChunkLoaderLogic.getInstance().getMaxChunksPerTicket()) {
            if (this.ticket != null) {
                ChunkLoaderLogic.getInstance().addChunkToTicket(this.ticket, chunk);
            }
            this.loadedChunks.add(chunk);
            this.markDirty();
        }
    }

    public void removeChunkFromLoaded(ChunkPos chunk) {
        if (this.worldObj.isRemote) {
            new RuntimeException("Something tried to change the ChunkLoaderState on the client.").printStackTrace();
            return;
        }
        if (ChunkLoaderLogic.getChunkCoords(this.pos).equals((Object)chunk)) {
            return;
        }
        if (this.ticket != null) {
            ChunkLoaderLogic.getInstance().removeChunkFromTicket(this.ticket, chunk);
        }
        this.loadedChunks.remove((Object)chunk);
        this.markDirty();
    }

    public ImmutableSet<ChunkPos> getLoadedChunks() {
        return ImmutableSet.copyOf(this.loadedChunks);
    }

    public boolean isChunkInRange(ChunkPos chunk) {
        ChunkPos mainChunk = ChunkLoaderLogic.getChunkCoords(this.pos);
        return Math.abs(chunk.chunkXPos - mainChunk.chunkXPos) <= 4 && Math.abs(chunk.chunkZPos - mainChunk.chunkZPos) <= 4;
    }

    @Override
    public void onNetworkEvent(EntityPlayer player, int event) {
        int x = (event & 15) - 8;
        int z = (event >> 4 & 15) - 8;
        ChunkPos mainChunk = ChunkLoaderLogic.getChunkCoords(this.pos);
        ChunkPos chunk = new ChunkPos(mainChunk.chunkXPos + x, mainChunk.chunkZPos + z);
        if (this.isChunkInRange(chunk)) {
            if (this.getLoadedChunks().contains((Object)chunk)) {
                this.removeChunkFromLoaded(chunk);
            } else {
                this.addChunkToLoaded(chunk);
            }
        } else {
            return;
        }
    }

    @Override
    protected void onBlockBreak() {
        super.onBlockBreak();
        if (this.ticket != null) {
            ChunkLoaderLogic.getInstance().removeTicket(this.ticket);
            this.ticket = null;
        }
    }
}

