/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Throwables
 *  com.mojang.authlib.GameProfile
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.inventory.Container
 *  net.minecraft.inventory.IContainerListener
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.network.INetHandler
 *  net.minecraft.network.NetHandlerPlayServer
 *  net.minecraft.network.PacketBuffer
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.management.PlayerChunkMap
 *  net.minecraft.server.management.PlayerChunkMapEntry
 *  net.minecraft.server.management.PlayerList
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 *  net.minecraftforge.fml.common.FMLCommonHandler
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.network.FMLEventChannel
 *  net.minecraftforge.fml.common.network.FMLNetworkEvent
 *  net.minecraftforge.fml.common.network.FMLNetworkEvent$ServerCustomPacketEvent
 *  net.minecraftforge.fml.common.network.NetworkRegistry
 *  net.minecraftforge.fml.common.network.internal.FMLProxyPacket
 */
package ic2.core.network;

import com.google.common.base.Throwables;
import ic2.api.network.ClientModifiable;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.api.network.INetworkDataProvider;
import ic2.api.network.INetworkItemEventListener;
import ic2.api.network.INetworkManager;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.WorldData;
import ic2.core.block.TileEntityBlock;
import ic2.core.item.IHandHeldInventory;
import ic2.core.ref.TeBlock;
import ic2.core.util.LogCategory;
import ic2.core.util.ReflectionUtil;
import ic2.core.util.StackUtil;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerChunkMap;
import net.minecraft.server.management.PlayerChunkMapEntry;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLEventChannel;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

public class NetworkManager
implements INetworkManager {
    private static Field playerInstancePlayers = ReflectionUtil.getField(PlayerChunkMapEntry.class, List.class);
    private static FMLEventChannel channel;
    private static final int maxPacketDataLength = 32766;
    public static final String channelName = "ic2";

    public NetworkManager() {
        if (channel == null) {
            channel = NetworkRegistry.INSTANCE.newEventDrivenChannel("ic2");
        }
        channel.register((Object)this);
    }

    protected boolean isClient() {
        return false;
    }

    public void onTickEnd(WorldData worldData) {
        try {
            TeUpdate.send(worldData, this);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public final /* varargs */ void sendPlayerItemData(EntityPlayer player, int slot, Object ... data) {
        GrowingBuffer buffer = new GrowingBuffer(256);
        try {
            SubPacketType.PlayerItemData.writeTo(buffer);
            buffer.writeByte(slot);
            DataEncoder.encode(buffer, (Object)player.inventory.mainInventory[slot].getItem(), false);
            buffer.writeVarInt(data.length);
            for (Object o : data) {
                DataEncoder.encode(buffer, o);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        buffer.flip();
        if (!this.isClient()) {
            this.sendPacket(buffer, true, (EntityPlayerMP)player);
        } else {
            this.sendPacket(buffer);
        }
    }

    @Override
    public final void updateTileEntityField(TileEntity te, String field) {
        if (!this.isClient()) {
            NetworkManager.getTeUpdateData(te).addGlobalField(field);
        } else if (this.getClientModifiableField(te.getClass(), field) == null) {
            IC2.log.warn(LogCategory.Network, "Field update for %s failed.", new Object[]{te});
        } else {
            GrowingBuffer buffer = new GrowingBuffer(64);
            try {
                SubPacketType.TileEntityData.writeTo(buffer);
                DataEncoder.encode(buffer, (Object)te, false);
                NetworkManager.writeFieldData((Object)te, field, buffer);
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
            buffer.flip();
            this.sendPacket(buffer);
        }
    }

    private Field getClientModifiableField(Class<?> cls, String fieldName) {
        Field field = ReflectionUtil.getFieldRecursive(cls, fieldName);
        if (field == null) {
            IC2.log.warn(LogCategory.Network, "Can't find field %s in %s.", fieldName, cls.getName());
            return null;
        }
        if (field.getAnnotation(ClientModifiable.class) == null) {
            IC2.log.warn(LogCategory.Network, "The field %s in %s is not modifiable.", fieldName, cls.getName());
            return null;
        }
        return field;
    }

    private static TeUpdateDataServer getTeUpdateData(TileEntity te) {
        assert (IC2.platform.isSimulating());
        if (te == null) {
            throw new NullPointerException();
        }
        WorldData worldData = WorldData.get(te.getWorld());
        TeUpdateDataServer ret = worldData.tesToUpdate.get((Object)te);
        if (ret == null) {
            ret = new TeUpdateDataServer();
            worldData.tesToUpdate.put(te, ret);
        }
        return ret;
    }

    public final void updateTileEntityFieldTo(TileEntity te, String field, EntityPlayerMP player) {
        assert (!this.isClient());
        NetworkManager.getTeUpdateData(te).addPlayerField(field, player);
    }

    public final void sendComponentUpdate(TileEntityBlock te, String componentName, EntityPlayerMP player, GrowingBuffer data) {
        assert (!this.isClient());
        if (player.worldObj != te.getWorld()) {
            throw new IllegalArgumentException("mismatched world (te " + (Object)te.getWorld() + ", player " + (Object)player.worldObj + ")");
        }
        GrowingBuffer buffer = new GrowingBuffer(64);
        try {
            SubPacketType.TileEntityBlockComponent.writeTo(buffer);
            DataEncoder.encode(buffer, te, false);
            buffer.writeString(componentName);
            buffer.writeVarInt(data.available());
            data.writeTo(buffer);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        buffer.flip();
        this.sendPacket(buffer, true, player);
    }

    @Override
    public final void initiateTileEntityEvent(TileEntity te, int event, boolean limitRange) {
        assert (!this.isClient());
        if (te.getWorld().playerEntities.isEmpty()) {
            return;
        }
        GrowingBuffer buffer = new GrowingBuffer(32);
        try {
            SubPacketType.TileEntityEvent.writeTo(buffer);
            DataEncoder.encode(buffer, (Object)te, false);
            buffer.writeInt(event);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        buffer.flip();
        for (EntityPlayerMP target : NetworkManager.getPlayersInRange(te.getWorld(), te.getPos(), new ArrayList())) {
            int dZ;
            int dX;
            if (limitRange && (dX = (int)((double)te.getPos().getX() + 0.5 - target.posX)) * dX + (dZ = (int)((double)te.getPos().getZ() + 0.5 - target.posZ)) * dZ > 400) continue;
            this.sendPacket(buffer, false, target);
        }
    }

    @Override
    public final void initiateItemEvent(EntityPlayer player, ItemStack stack, int event, boolean limitRange) {
        if (stack == null) {
            throw new NullPointerException("null stack");
        }
        if (!StackUtil.check(stack)) {
            throw new NullPointerException("invalid stack");
        }
        assert (!this.isClient());
        GrowingBuffer buffer = new GrowingBuffer(256);
        try {
            SubPacketType.ItemEvent.writeTo(buffer);
            DataEncoder.encode(buffer, (Object)player.getGameProfile(), false);
            DataEncoder.encode(buffer, (Object)stack, false);
            buffer.writeInt(event);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        buffer.flip();
        for (EntityPlayerMP target : NetworkManager.getPlayersInRange(player.worldObj, player.getPosition(), new ArrayList())) {
            int dZ;
            int dX;
            if (limitRange && (dX = (int)(player.posX - target.posX)) * dX + (dZ = (int)(player.posZ - target.posZ)) * dZ > 400) continue;
            this.sendPacket(buffer, false, target);
        }
    }

    @Override
    public void initiateClientItemEvent(ItemStack stack, int event) {
        assert (false);
    }

    @Override
    public void initiateClientTileEntityEvent(TileEntity te, int event) {
        assert (false);
    }

    public void initiateRpc(int id, Class<? extends IRpcProvider<?>> provider, Object[] args) {
        assert (false);
    }

    public final void initiateGuiDisplay(EntityPlayerMP player, IHasGui inventory, int windowId) {
        assert (!this.isClient());
        try {
            GrowingBuffer buffer = new GrowingBuffer(32);
            SubPacketType.GuiDisplay.writeTo(buffer);
            MinecraftServer server = FMLCommonHandler.instance().getMinecraftServerInstance();
            boolean isAdmin = server.getPlayerList().canSendCommands(player.getGameProfile());
            buffer.writeBoolean(isAdmin);
            if (inventory instanceof TileEntity) {
                TileEntity te = (TileEntity)inventory;
                buffer.writeByte(0);
                DataEncoder.encode(buffer, (Object)te, false);
            } else if (player.inventory.getCurrentItem() != null && player.inventory.getCurrentItem().getItem() instanceof IHandHeldInventory) {
                buffer.writeByte(1);
                buffer.writeInt(player.inventory.currentItem);
            } else {
                IC2.platform.displayError("An unknown GUI type was attempted to be displayed.\nThis could happen due to corrupted data from a player or a bug.\n\n(Technical information: " + inventory + ")", new Object[0]);
            }
            buffer.writeInt(windowId);
            buffer.flip();
            this.sendPacket(buffer, true, player);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public final void sendInitialData(TileEntity te, EntityPlayerMP player) {
        assert (!this.isClient());
        if (te instanceof INetworkDataProvider) {
            TeUpdateDataServer updateData = NetworkManager.getTeUpdateData(te);
            for (String field : ((INetworkDataProvider)te).getNetworkedFields()) {
                updateData.addPlayerField(field, player);
            }
        }
    }

    public final void sendInitialData(TileEntity te) {
        assert (!this.isClient());
        if (te instanceof INetworkDataProvider) {
            TeUpdateDataServer updateData = NetworkManager.getTeUpdateData(te);
            for (String field : ((INetworkDataProvider)te).getNetworkedFields()) {
                updateData.addGlobalField(field);
            }
        }
    }

    public final void sendChat(EntityPlayerMP player, String message) {
        assert (!this.isClient());
        GrowingBuffer buffer = new GrowingBuffer(message.length() * 2);
        buffer.writeString(message);
        buffer.flip();
        this.sendLargePacket(player, 1, buffer);
    }

    public final void sendConsole(EntityPlayerMP player, String message) {
        assert (!this.isClient());
        GrowingBuffer buffer = new GrowingBuffer(message.length() * 2);
        buffer.writeString(message);
        buffer.flip();
        this.sendLargePacket(player, 2, buffer);
    }

    public final /* varargs */ void sendContainerFields(ContainerBase<?> container, String ... fieldNames) {
        for (String fieldName : fieldNames) {
            this.sendContainerField(container, fieldName);
        }
    }

    public final void sendContainerField(ContainerBase<?> container, String fieldName) {
        if (this.isClient() && this.getClientModifiableField(container.getClass(), fieldName) == null) {
            IC2.log.warn(LogCategory.Network, "Field update for %s failed.", new Object[]{container});
            return;
        }
        GrowingBuffer buffer = new GrowingBuffer(256);
        try {
            SubPacketType.ContainerData.writeTo(buffer);
            buffer.writeInt(container.windowId);
            NetworkManager.writeFieldData(container, fieldName, buffer);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        buffer.flip();
        if (!this.isClient()) {
            for (IContainerListener listener : container.getListeners()) {
                if (!(listener instanceof EntityPlayerMP)) continue;
                this.sendPacket(buffer, false, (EntityPlayerMP)listener);
            }
        } else {
            this.sendPacket(buffer);
        }
    }

    public final void sendContainerEvent(ContainerBase<?> container, String event) {
        GrowingBuffer buffer = new GrowingBuffer(64);
        SubPacketType.ContainerEvent.writeTo(buffer);
        buffer.writeInt(container.windowId);
        buffer.writeString(event);
        buffer.flip();
        if (!this.isClient()) {
            for (IContainerListener listener : container.getListeners()) {
                if (!(listener instanceof EntityPlayerMP)) continue;
                this.sendPacket(buffer, false, (EntityPlayerMP)listener);
            }
        } else {
            this.sendPacket(buffer);
        }
    }

    public final void initiateTeblockLandEffect(World world, double x, double y, double z, int count, TeBlock teBlock) {
        assert (!this.isClient());
        GrowingBuffer buffer = new GrowingBuffer(64);
        try {
            SubPacketType.TileEntityBlockLandEffect.writeTo(buffer);
            DataEncoder.encode(buffer, (Object)world, false);
            buffer.writeDouble(x);
            buffer.writeDouble(y);
            buffer.writeDouble(z);
            buffer.writeInt(count);
            buffer.writeString(teBlock.getName());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        buffer.flip();
        for (EntityPlayer player : world.playerEntities) {
            double distance;
            if (!(player instanceof EntityPlayerMP) || (distance = player.getDistanceSq(x, y, z)) > 1024.0) continue;
            this.sendPacket(buffer, false, (EntityPlayerMP)player);
        }
    }

    final void sendLargePacket(EntityPlayerMP player, int id, GrowingBuffer data) {
        GrowingBuffer buffer = new GrowingBuffer(16384);
        buffer.writeShort(0);
        try {
            DeflaterOutputStream deflate = new DeflaterOutputStream(buffer);
            data.writeTo(deflate);
            deflate.close();
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        buffer.flip();
        boolean firstPacket = true;
        while (buffer.hasAvailable()) {
            if (!firstPacket) {
                buffer.skipBytes(-2);
            }
            boolean lastPacket = buffer.available() <= 32766;
            SubPacketType.LargePacket.writeTo(buffer);
            int state = 0;
            if (firstPacket) {
                state |= true;
            }
            if (lastPacket) {
                state |= 2;
            }
            buffer.write(state |= id << 2);
            buffer.skipBytes(-2);
            if (lastPacket) {
                this.sendPacket(buffer, true, player);
            } else {
                this.sendPacket(buffer.copy(32766), true, player);
            }
            firstPacket = false;
        }
    }

    @SubscribeEvent
    public void onPacket(FMLNetworkEvent.ServerCustomPacketEvent event) {
        if (this.getClass() == NetworkManager.class) {
            try {
                this.onPacketData(GrowingBuffer.wrap(event.getPacket().payload()), (EntityPlayer)((NetHandlerPlayServer)event.getHandler()).playerEntity);
            }
            catch (Throwable t) {
                IC2.log.warn(LogCategory.Network, t, "Network read failed");
                Throwables.propagate((Throwable)t);
            }
        }
    }

    private void onPacketData(GrowingBuffer is, final EntityPlayer player) throws IOException {
        if (!is.hasAvailable()) {
            return;
        }
        SubPacketType packetType = SubPacketType.read(is, true);
        if (packetType == null) {
            return;
        }
        switch (packetType) {
            case ItemEvent: {
                final ItemStack stack = (ItemStack)DataEncoder.decode(is, ItemStack.class);
                final int event = is.readInt();
                if (!(stack.getItem() instanceof INetworkItemEventListener)) break;
                IC2.platform.requestTick(true, new Runnable(){

                    @Override
                    public void run() {
                        ((INetworkItemEventListener)stack.getItem()).onNetworkEvent(stack, player, event);
                    }
                });
                break;
            }
            case KeyUpdate: {
                final int keyState = is.readInt();
                IC2.platform.requestTick(true, new Runnable(){

                    @Override
                    public void run() {
                        IC2.keyboard.processKeyUpdate(player, keyState);
                    }
                });
                break;
            }
            case TileEntityEvent: {
                final Object teDeferred = DataEncoder.decodeDeferred(is, TileEntity.class);
                final int event = is.readInt();
                IC2.platform.requestTick(true, new Runnable(){

                    @Override
                    public void run() {
                        TileEntity te = (TileEntity)DataEncoder.getValue(teDeferred);
                        if (te instanceof INetworkClientTileEntityEventListener) {
                            ((INetworkClientTileEntityEventListener)te).onNetworkEvent(player, event);
                        }
                    }
                });
                break;
            }
            case Rpc: {
                RpcHandler.processRpcRequest(is, (EntityPlayerMP)player);
                break;
            }
            default: {
                this.onCommonPacketData(packetType, true, is, player);
            }
        }
    }

    protected void onCommonPacketData(SubPacketType packetType, boolean simulating, GrowingBuffer is, final EntityPlayer player) throws IOException {
        switch (packetType) {
            case PlayerItemData: {
                final byte slot = is.readByte();
                final Item item = (Item)DataEncoder.decode(is, Item.class);
                int dataCount = is.readVarInt();
                final Object[] subData = new Object[dataCount];
                for (int i = 0; i < dataCount; ++i) {
                    subData[i] = DataEncoder.decode(is);
                }
                if (slot < 0 || slot >= 9) break;
                IC2.platform.requestTick(simulating, new Runnable(){

                    @Override
                    public void run() {
                        for (int i = 0; i < subData.length; ++i) {
                            subData[i] = DataEncoder.getValue(subData[i]);
                        }
                        ItemStack stack = player.inventory.mainInventory[slot];
                        if (stack != null && stack.getItem() == item && item instanceof IPlayerItemDataListener) {
                            ((IPlayerItemDataListener)item).onPlayerItemNetworkData(player, slot, subData);
                        }
                    }
                });
                break;
            }
            case ContainerData: {
                final int windowId = is.readInt();
                final String fieldName = is.readString();
                final Object value = DataEncoder.decode(is);
                IC2.platform.requestTick(simulating, new Runnable(){

                    @Override
                    public void run() {
                        if (player.openContainer instanceof ContainerBase && player.openContainer.windowId == windowId && (NetworkManager.this.isClient() || NetworkManager.this.getClientModifiableField(player.openContainer.getClass(), fieldName) != null)) {
                            ReflectionUtil.setValueRecursive((Object)player.openContainer, fieldName, DataEncoder.getValue(value));
                        }
                    }
                });
                break;
            }
            case ContainerEvent: {
                final int windowId = is.readInt();
                final String event = is.readString();
                IC2.platform.requestTick(simulating, new Runnable(){

                    @Override
                    public void run() {
                        if (player.openContainer instanceof ContainerBase && player.openContainer.windowId == windowId) {
                            ((ContainerBase)player.openContainer).onContainerEvent(event);
                        }
                    }
                });
                break;
            }
            case TileEntityData: {
                final Object teDeferred = DataEncoder.decodeDeferred(is, TileEntity.class);
                final String fieldName = is.readString();
                final Object value = DataEncoder.decode(is);
                IC2.platform.requestTick(simulating, new Runnable(){

                    @Override
                    public void run() {
                        TileEntity te = (TileEntity)DataEncoder.getValue(teDeferred);
                        if (te != null && (NetworkManager.this.isClient() || NetworkManager.this.getClientModifiableField(te.getClass(), fieldName) != null)) {
                            ReflectionUtil.setValueRecursive((Object)te, fieldName, DataEncoder.getValue(value));
                        }
                    }
                });
                break;
            }
            default: {
                IC2.log.warn(LogCategory.Network, "Unhandled packet type: %s", packetType.name());
            }
        }
    }

    public void initiateKeyUpdate(int keyState) {
    }

    public void sendLoginData() {
    }

    public final void initiateExplosionEffect(World world, Vec3d pos) {
        assert (!this.isClient());
        try {
            GrowingBuffer buffer = new GrowingBuffer(32);
            SubPacketType.ExplosionEffect.writeTo(buffer);
            DataEncoder.encode(buffer, (Object)world, false);
            DataEncoder.encode(buffer, (Object)pos, false);
            buffer.flip();
            for (Object obj : world.playerEntities) {
                EntityPlayerMP player;
                if (!(obj instanceof EntityPlayerMP) || (player = (EntityPlayerMP)obj).getDistanceSq(pos.xCoord, pos.yCoord, pos.zCoord) >= 128.0) continue;
                this.sendPacket(buffer, false, player);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected final void sendPacket(GrowingBuffer buffer) {
        if (!this.isClient()) {
            channel.sendToAll(NetworkManager.makePacket(buffer, true));
        } else {
            channel.sendToServer(NetworkManager.makePacket(buffer, true));
        }
    }

    protected final void sendPacket(GrowingBuffer buffer, boolean advancePos, EntityPlayerMP player) {
        assert (!this.isClient());
        channel.sendTo(NetworkManager.makePacket(buffer, advancePos), player);
    }

    static <T extends Collection<EntityPlayerMP>> T getPlayersInRange(World world, BlockPos pos, T result) {
        if (!(world instanceof WorldServer)) {
            return result;
        }
        PlayerChunkMap playerManager = ((WorldServer)world).getPlayerChunkMap();
        PlayerChunkMapEntry instance = playerManager.getEntry(pos.getX() >> 4, pos.getZ() >> 4);
        if (instance == null) {
            return result;
        }
        result.addAll((Collection)ReflectionUtil.getFieldValue(playerInstancePlayers, (Object)instance));
        return result;
    }

    static void writeFieldData(Object object, String fieldName, GrowingBuffer out) throws IOException {
        int pos = fieldName.indexOf(61);
        if (pos != -1) {
            out.writeString(fieldName.substring(0, pos));
            DataEncoder.encode(out, fieldName.substring(pos + 1));
        } else {
            out.writeString(fieldName);
            try {
                DataEncoder.encode(out, ReflectionUtil.getValueRecursive(object, fieldName));
            }
            catch (NoSuchFieldException e) {
                throw new RuntimeException("Can't find field " + fieldName + " in " + object.getClass().getName(), e);
            }
        }
    }

    private static FMLProxyPacket makePacket(GrowingBuffer buffer, boolean advancePos) {
        return new FMLProxyPacket(new PacketBuffer(buffer.toByteBuf(advancePos)), "ic2");
    }

}

