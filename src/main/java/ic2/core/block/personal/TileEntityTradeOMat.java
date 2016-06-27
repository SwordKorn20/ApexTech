/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTUtil
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.management.PlayerList
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.personal;

import com.mojang.authlib.GameProfile;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.api.network.INetworkTileEntityEventListener;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.Platform;
import ic2.core.audio.AudioManager;
import ic2.core.audio.PositionSpec;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableLinked;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.personal.ContainerTradeOMatClosed;
import ic2.core.block.personal.ContainerTradeOMatOpen;
import ic2.core.block.personal.GuiTradeOMatClosed;
import ic2.core.block.personal.GuiTradeOMatOpen;
import ic2.core.block.personal.IPersonalBlock;
import ic2.core.block.personal.TileEntityPersonalChest;
import ic2.core.network.NetworkManager;
import ic2.core.util.Log;
import ic2.core.util.LogCategory;
import ic2.core.util.SideGateway;
import ic2.core.util.StackUtil;
import ic2.core.util.Util;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityTradeOMat
extends TileEntityInventory
implements IPersonalBlock,
IHasGui,
INetworkTileEntityEventListener,
INetworkClientTileEntityEventListener {
    private int ticker = IC2.random.nextInt(64);
    private GameProfile owner = null;
    public int totalTradeCount = 0;
    public int stock = 0;
    public boolean infinite = false;
    private static final int stockUpdateRate = 64;
    private static final int EventTrade = 0;
    public final InvSlot demandSlot;
    public final InvSlot offerSlot;
    public final InvSlotConsumableLinked inputSlot;
    public final InvSlotOutput outputSlot;

    public TileEntityTradeOMat() {
        this.demandSlot = new InvSlot(this, "demand", InvSlot.Access.NONE, 1);
        this.offerSlot = new InvSlot(this, "offer", InvSlot.Access.NONE, 1);
        this.inputSlot = new InvSlotConsumableLinked(this, "input", 1, this.demandSlot);
        this.outputSlot = new InvSlotOutput(this, "output", 1);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (nbt.hasKey("ownerGameProfile")) {
            this.owner = NBTUtil.readGameProfileFromNBT((NBTTagCompound)nbt.getCompoundTag("ownerGameProfile"));
        }
        this.totalTradeCount = nbt.getInteger("totalTradeCount");
        if (nbt.hasKey("infinite")) {
            this.infinite = nbt.getBoolean("infinite");
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        if (this.owner != null) {
            NBTTagCompound ownerNbt = new NBTTagCompound();
            NBTUtil.writeGameProfile((NBTTagCompound)ownerNbt, (GameProfile)this.owner);
            nbt.setTag("ownerGameProfile", (NBTBase)ownerNbt);
        }
        nbt.setInteger("totalTradeCount", this.totalTradeCount);
        if (this.infinite) {
            nbt.setBoolean("infinite", this.infinite);
        }
        return nbt;
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("owner");
        return ret;
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        this.trade();
        if (this.infinite) {
            this.stock = -1;
        } else if (++this.ticker % 64 == 0) {
            this.updateStock();
        }
    }

    private void trade() {
        ItemStack tradedIn = this.inputSlot.consumeLinked(true);
        if (tradedIn == null || tradedIn.stackSize <= 0) {
            return;
        }
        ItemStack offer = this.offerSlot.get();
        if (offer == null || offer.stackSize <= 0) {
            return;
        }
        if (!this.outputSlot.canAdd(offer)) {
            return;
        }
        if (this.infinite) {
            this.inputSlot.consumeLinked(false);
            this.outputSlot.add(offer);
        } else {
            ItemStack transferredIn = StackUtil.fetch(this, offer, true);
            if (transferredIn == null || transferredIn.stackSize != offer.stackSize) {
                return;
            }
            int transferredOut = StackUtil.distribute(this, tradedIn, true);
            if (transferredOut != tradedIn.stackSize) {
                return;
            }
            transferredIn = StackUtil.fetch(this, offer, false);
            if (transferredIn == null) {
                return;
            }
            if (transferredIn.stackSize != offer.stackSize) {
                IC2.log.warn(LogCategory.Block, "The Trade-O-Mat at %s received an inconsistent result from an adjacent trade supply inventory, the item stack %s will be lost.", new Object[]{Util.formatPosition(this), transferredIn});
                return;
            }
            StackUtil.distribute(this, this.inputSlot.consumeLinked(false), false);
            this.outputSlot.add(transferredIn);
            this.stock -= offer.stackSize;
        }
        ++this.totalTradeCount;
        IC2.network.get(true).initiateTileEntityEvent(this, 0, true);
        this.markDirty();
    }

    @Override
    protected void onLoaded() {
        super.onLoaded();
        if (IC2.platform.isSimulating()) {
            this.updateStock();
        }
    }

    public void updateStock() {
        this.stock = 0;
        ItemStack offer = this.offerSlot.get();
        if (offer != null) {
            ItemStack available = StackUtil.fetch(this, StackUtil.copyWithSize(offer, Integer.MAX_VALUE), true);
            this.stock = available == null ? 0 : available.stackSize / offer.stackSize;
        }
    }

    @Override
    public boolean wrenchCanRemove(EntityPlayer player) {
        return this.permitsAccess(player.getGameProfile());
    }

    @Override
    public boolean permitsAccess(GameProfile profile) {
        return TileEntityPersonalChest.checkAccess(this, profile);
    }

    @Override
    public GameProfile getOwner() {
        return this.owner;
    }

    @Override
    public void setOwner(GameProfile owner) {
        this.owner = owner;
    }

    @Override
    protected boolean canEntityDestroy(Entity entity) {
        return false;
    }

    public ContainerBase<TileEntityTradeOMat> getGuiContainer(EntityPlayer player) {
        if (this.permitsAccess(player.getGameProfile())) {
            return new ContainerTradeOMatOpen(player, this);
        }
        return new ContainerTradeOMatClosed(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        if (isAdmin || this.permitsAccess(player.getGameProfile())) {
            return new GuiTradeOMatOpen(new ContainerTradeOMatOpen(player, this), isAdmin);
        }
        return new GuiTradeOMatClosed(new ContainerTradeOMatClosed(player, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }

    @Override
    public void onNetworkEvent(int event) {
        switch (event) {
            case 0: {
                IC2.audioManager.playOnce(this, PositionSpec.Center, "Machines/o-mat.ogg", true, IC2.audioManager.getDefaultVolume());
                break;
            }
            default: {
                IC2.platform.displayError("An unknown event type was received over multiplayer.\nThis could happen due to corrupted data or a bug.\n\n(Technical information: event ID " + event + ", tile entity below)\n" + "T: " + this + " (" + (Object)this.pos + ")", new Object[0]);
            }
        }
    }

    @Override
    public void onNetworkEvent(EntityPlayer player, int event) {
        if (event == 0 && this.getWorld().getMinecraftServer().getPlayerList().canSendCommands(player.getGameProfile())) {
            boolean bl = this.infinite = !this.infinite;
            if (!this.infinite) {
                this.updateStock();
            }
        }
    }
}

