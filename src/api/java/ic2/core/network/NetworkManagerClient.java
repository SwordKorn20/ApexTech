/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Throwables
 *  com.mojang.authlib.GameProfile
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.multiplayer.WorldClient
 *  net.minecraft.client.network.NetHandlerPlayClient
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.inventory.Container
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.Packet
 *  net.minecraft.network.play.client.CPacketCloseWindow
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldProvider
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.network.FMLNetworkEvent
 *  net.minecraftforge.fml.common.network.FMLNetworkEvent$ClientCustomPacketEvent
 *  net.minecraftforge.fml.common.network.internal.FMLProxyPacket
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.network;

import com.google.common.base.Throwables;
import com.mojang.authlib.GameProfile;
import ic2.api.network.INetworkItemEventListener;
import ic2.api.network.INetworkTileEntityEventListener;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.Platform;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.comp.Components;
import ic2.core.block.comp.TileEntityComponent;
import ic2.core.item.IHandHeldInventory;
import ic2.core.network.DataEncoder;
import ic2.core.network.GrowingBuffer;
import ic2.core.network.IRpcProvider;
import ic2.core.network.NetworkManager;
import ic2.core.network.SubPacketType;
import ic2.core.network.TeUpdate;
import ic2.core.ref.TeBlock;
import ic2.core.util.Log;
import ic2.core.util.LogCategory;
import ic2.core.util.ParticleUtil;
import io.netty.buffer.ByteBuf;
import java.io.ByteArrayInputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.zip.InflaterOutputStream;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(value=Side.CLIENT)
public class NetworkManagerClient
extends NetworkManager {
    private GrowingBuffer largePacketBuffer;

    @Override
    protected boolean isClient() {
        return true;
    }

    @Override
    public void initiateClientItemEvent(ItemStack stack, int event) {
        try {
            GrowingBuffer buffer = new GrowingBuffer(256);
            SubPacketType.ItemEvent.writeTo(buffer);
            DataEncoder.encode(buffer, (Object)stack, false);
            buffer.writeInt(event);
            buffer.flip();
            this.sendPacket(buffer);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initiateKeyUpdate(int keyState) {
        GrowingBuffer buffer = new GrowingBuffer(5);
        SubPacketType.KeyUpdate.writeTo(buffer);
        buffer.writeInt(keyState);
        buffer.flip();
        this.sendPacket(buffer);
    }

    @Override
    public void initiateClientTileEntityEvent(TileEntity te, int event) {
        try {
            GrowingBuffer buffer = new GrowingBuffer(32);
            SubPacketType.TileEntityEvent.writeTo(buffer);
            DataEncoder.encode(buffer, (Object)te, false);
            buffer.writeInt(event);
            buffer.flip();
            this.sendPacket(buffer);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void initiateRpc(int id, Class<? extends IRpcProvider<?>> provider, Object[] args) {
        try {
            GrowingBuffer buffer = new GrowingBuffer(256);
            SubPacketType.Rpc.writeTo(buffer);
            buffer.writeInt(id);
            buffer.writeString(provider.getName());
            DataEncoder.encode(buffer, args);
            buffer.flip();
            this.sendPacket(buffer);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @SubscribeEvent
    public void onPacket(FMLNetworkEvent.ClientCustomPacketEvent event) {
        assert (!this.getClass().getName().equals(NetworkManager.class.getName()));
        try {
            this.onPacketData(GrowingBuffer.wrap(event.getPacket().payload()), (EntityPlayer)Minecraft.getMinecraft().thePlayer);
        }
        catch (Throwable t) {
            IC2.log.warn(LogCategory.Network, t, "Network read failed");
            Throwables.propagate((Throwable)t);
        }
    }

    private void onPacketData(GrowingBuffer is, final EntityPlayer player) throws IOException {
        if (!is.hasAvailable()) {
            return;
        }
        SubPacketType packetType = SubPacketType.read(is, false);
        if (packetType == null) {
            return;
        }
        block0 : switch (packetType) {
            case LargePacket: {
                int state = is.readUnsignedByte();
                if ((state & 1) != 0) {
                    this.largePacketBuffer = new GrowingBuffer(16384);
                }
                is.writeTo(this.largePacketBuffer);
                if ((state & 2) == 0) break;
                GrowingBuffer decompBuffer = new GrowingBuffer(16384);
                InflaterOutputStream inflate = new InflaterOutputStream(decompBuffer);
                this.largePacketBuffer.flip();
                this.largePacketBuffer.writeTo(inflate);
                this.largePacketBuffer = null;
                inflate.close();
                decompBuffer.flip();
                switch (state >> 2) {
                    case 0: {
                        TeUpdate.receive(decompBuffer);
                        break block0;
                    }
                    case 1: {
                        NetworkManagerClient.processChatPacket(decompBuffer);
                        break block0;
                    }
                    case 2: {
                        NetworkManagerClient.processConsolePacket(decompBuffer);
                    }
                }
                break;
            }
            case TileEntityEvent: {
                final Object teDeferred = DataEncoder.decodeDeferred(is, TileEntity.class);
                final int event = is.readInt();
                IC2.platform.requestTick(false, new Runnable(){

                    @Override
                    public void run() {
                        TileEntity te = (TileEntity)DataEncoder.getValue(teDeferred);
                        if (te instanceof INetworkTileEntityEventListener) {
                            ((INetworkTileEntityEventListener)te).onNetworkEvent(event);
                        }
                    }
                });
                break;
            }
            case ItemEvent: {
                final GameProfile profile = (GameProfile)DataEncoder.decode(is, GameProfile.class);
                final ItemStack stack = (ItemStack)DataEncoder.decode(is, ItemStack.class);
                final int event = is.readInt();
                IC2.platform.requestTick(false, new Runnable(){

                    @Override
                    public void run() {
                        WorldClient world = Minecraft.getMinecraft().theWorld;
                        for (Object obj : world.playerEntities) {
                            EntityPlayer player = (EntityPlayer)obj;
                            if ((profile.getId() == null || !profile.getId().equals(player.getGameProfile().getId())) && (profile.getId() != null || !profile.getName().equals(player.getGameProfile().getName()))) continue;
                            if (!(stack.getItem() instanceof INetworkItemEventListener)) break;
                            ((INetworkItemEventListener)stack.getItem()).onNetworkEvent(stack, player, event);
                            break;
                        }
                    }
                });
                break;
            }
            case GuiDisplay: {
                final boolean isAdmin = is.readBoolean();
                switch (is.readByte()) {
                    case 0: {
                        final Object teDeferred = DataEncoder.decodeDeferred(is, TileEntity.class);
                        final int windowId = is.readInt();
                        IC2.platform.requestTick(false, new Runnable(){

                            @Override
                            public void run() {
                                EntityPlayer player = IC2.platform.getPlayerInstance();
                                TileEntity te = (TileEntity)DataEncoder.getValue(teDeferred);
                                if (te instanceof IHasGui) {
                                    IC2.platform.launchGuiClient(player, (IHasGui)te, isAdmin);
                                    player.openContainer.windowId = windowId;
                                } else if (player instanceof EntityPlayerSP) {
                                    ((EntityPlayerSP)player).connection.sendPacket((Packet)new CPacketCloseWindow(windowId));
                                }
                            }
                        });
                        break block0;
                    }
                    case 1: {
                        final int currentItemPosition = is.readInt();
                        final int windowId = is.readInt();
                        IC2.platform.requestTick(false, new Runnable(){

                            @Override
                            public void run() {
                                EntityPlayer player = IC2.platform.getPlayerInstance();
                                if (currentItemPosition != player.inventory.currentItem) {
                                    return;
                                }
                                ItemStack currentItem = player.inventory.getCurrentItem();
                                if (currentItem != null && currentItem.getItem() instanceof IHandHeldInventory) {
                                    IC2.platform.launchGuiClient(player, ((IHandHeldInventory)currentItem.getItem()).getInventory(player, currentItem), isAdmin);
                                }
                                player.openContainer.windowId = windowId;
                            }
                        });
                        break block0;
                    }
                }
                break;
            }
            case ExplosionEffect: {
                final Object worldDeferred = DataEncoder.decodeDeferred(is, World.class);
                final Vec3d pos = (Vec3d)DataEncoder.decode(is, Vec3d.class);
                IC2.platform.requestTick(false, new Runnable(){

                    @Override
                    public void run() {
                        World world = (World)DataEncoder.getValue(worldDeferred);
                        if (world != null) {
                            world.playSound(player, new BlockPos(pos), SoundEvents.ENTITY_GENERIC_EXPLODE, SoundCategory.MASTER, 4.0f, (1.0f + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.2f) * 0.7f);
                            world.spawnParticle(EnumParticleTypes.EXPLOSION_HUGE, pos.xCoord, pos.yCoord, pos.zCoord, 0.0, 0.0, 0.0, new int[0]);
                        }
                    }
                });
                break;
            }
            case Rpc: {
                throw new RuntimeException("Received unexpected RPC packet");
            }
            case TileEntityBlockComponent: {
                final int dimensionId = is.readInt();
                final BlockPos pos = (BlockPos)DataEncoder.decode(is, BlockPos.class);
                String componentName = is.readString();
                final Class componentCls = Components.getClass(componentName);
                if (componentCls == null) {
                    throw new IOException("invalid component: " + componentName);
                }
                int dataLen = is.readVarInt();
                if (dataLen > 65536) {
                    throw new IOException("data length limit exceeded");
                }
                final byte[] data = new byte[dataLen];
                is.readFully(data);
                IC2.platform.requestTick(false, new Runnable(){

                    @Override
                    public void run() {
                        WorldClient world = Minecraft.getMinecraft().theWorld;
                        if (world.provider.getDimension() != dimensionId) {
                            return;
                        }
                        TileEntity teRaw = world.getTileEntity(pos);
                        if (!(teRaw instanceof TileEntityBlock)) {
                            return;
                        }
                        Object component = ((TileEntityBlock)teRaw).getComponent(componentCls);
                        if (component == null) {
                            return;
                        }
                        DataInputStream dataIs = new DataInputStream(new ByteArrayInputStream(data));
                        try {
                            component.onNetworkUpdate(dataIs);
                        }
                        catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
                break;
            }
            case TileEntityBlockLandEffect: {
                final Object worldDeferred = DataEncoder.decodeDeferred(is, World.class);
                final double x = is.readDouble();
                final double y = is.readDouble();
                final double z = is.readDouble();
                final int count = is.readInt();
                final TeBlock teBlock = TeBlock.get(is.readString());
                IC2.platform.requestTick(false, new Runnable(){

                    @Override
                    public void run() {
                        World world = (World)DataEncoder.getValue(worldDeferred);
                        if (world == null) {
                            return;
                        }
                        ParticleUtil.spawnBlockLandParticles(world, x, y, z, count, teBlock);
                    }
                });
                break;
            }
            default: {
                this.onCommonPacketData(packetType, false, is, player);
            }
        }
    }

    private static void processChatPacket(GrowingBuffer buffer) {
        final String messages = buffer.readString();
        IC2.platform.requestTick(false, new Runnable(){

            @Override
            public void run() {
                for (String line : messages.split("[\\r\\n]+")) {
                    IC2.platform.messagePlayer(null, line, new Object[0]);
                }
            }
        });
    }

    private static void processConsolePacket(GrowingBuffer buffer) {
        String messages = buffer.readString();
        PrintStream console = new PrintStream(new FileOutputStream(FileDescriptor.out));
        for (String line : messages.split("[\\r\\n]+")) {
            console.println(line);
        }
        console.flush();
    }

}

