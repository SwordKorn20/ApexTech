/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldProvider
 */
package ic2.core.network;

import ic2.api.network.INetworkUpdateListener;
import ic2.core.IC2;
import ic2.core.Platform;
import ic2.core.WorldData;
import ic2.core.block.TileEntityBlock;
import ic2.core.network.DataEncoder;
import ic2.core.network.GrowingBuffer;
import ic2.core.network.NetworkManager;
import ic2.core.network.TeUpdateDataClient;
import ic2.core.network.TeUpdateDataServer;
import ic2.core.ref.BlockName;
import ic2.core.ref.IBlockModelProvider;
import ic2.core.ref.TeBlock;
import ic2.core.util.Log;
import ic2.core.util.LogCategory;
import ic2.core.util.ReflectionUtil;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;

class TeUpdate {
    private static final boolean debug = System.getProperty("ic2.network.debug.teupdate") != null;

    TeUpdate() {
    }

    public static void send(WorldData worldData, NetworkManager network) throws IOException {
        if (worldData.tesToUpdate.isEmpty()) {
            return;
        }
        IdentityHashMap<EntityPlayerMP, GrowingBuffer> buffers = new IdentityHashMap<EntityPlayerMP, GrowingBuffer>();
        ArrayList playersInRange = new ArrayList();
        GrowingBuffer commonBuffer = new GrowingBuffer();
        for (Map.Entry<TileEntity, TeUpdateDataServer> entry2 : worldData.tesToUpdate.entrySet()) {
            TileEntity te = entry2.getKey();
            NetworkManager.getPlayersInRange(te.getWorld(), te.getPos(), playersInRange);
            if (playersInRange.isEmpty()) continue;
            TeUpdateDataServer updateData = entry2.getValue();
            DataEncoder.encode(commonBuffer, (Object)te.getPos(), false);
            commonBuffer.mark();
            commonBuffer.writeShort(0);
            for (String field : updateData.getGlobalFields()) {
                NetworkManager.writeFieldData((Object)te, field, commonBuffer);
            }
            commonBuffer.flip();
            for (EntityPlayerMP player : playersInRange) {
                Collection<String> playerFields = updateData.getPlayerFields(player);
                int fieldCount = updateData.getGlobalFields().size() + playerFields.size();
                if (fieldCount == 0) continue;
                if (fieldCount > 65535) {
                    throw new RuntimeException("too many fields for " + (Object)te + ": " + fieldCount);
                }
                commonBuffer.reset();
                commonBuffer.writeShort(fieldCount);
                commonBuffer.rewind();
                GrowingBuffer playerBuffer = (GrowingBuffer)buffers.get((Object)player);
                if (playerBuffer == null) {
                    playerBuffer = new GrowingBuffer(0);
                    buffers.put(player, playerBuffer);
                    playerBuffer.writeInt(player.worldObj.provider.getDimension());
                }
                commonBuffer.writeTo(playerBuffer);
                commonBuffer.rewind();
                for (String field2 : playerFields) {
                    NetworkManager.writeFieldData((Object)te, field2, playerBuffer);
                }
            }
            commonBuffer.clear();
            playersInRange.clear();
        }
        worldData.tesToUpdate.clear();
        for (Map.Entry entry : buffers.entrySet()) {
            EntityPlayerMP player = (EntityPlayerMP)entry.getKey();
            GrowingBuffer playerBuffer = (GrowingBuffer)entry.getValue();
            playerBuffer.flip();
            network.sendLargePacket(player, 0, playerBuffer);
        }
    }

    static void receive(GrowingBuffer buffer) throws IOException {
        final int dimensionId = buffer.readInt();
        final TeUpdateDataClient updateData = new TeUpdateDataClient();
        while (buffer.hasAvailable()) {
            BlockPos pos = (BlockPos)DataEncoder.decode(buffer, BlockPos.class);
            int fieldCount = buffer.readUnsignedShort();
            TeUpdateDataClient.TeData teData = updateData.addTe(pos, fieldCount);
            for (int i = 0; i < fieldCount; ++i) {
                String fieldName = buffer.readString();
                Object value = DataEncoder.decode(buffer);
                if (fieldName.equals("teBlk")) {
                    teData.teClass = TeBlock.get((String)value).getTeClass();
                    continue;
                }
                teData.addField(fieldName, value);
            }
            if (teData.teClass == null) continue;
            for (TeUpdateDataClient.FieldData fieldData : teData.getFields()) {
                fieldData.field = ReflectionUtil.getFieldRecursive(teData.teClass, fieldData.name);
            }
        }
        if (debug) {
            TeUpdate.printDebugOutput(dimensionId, updateData);
        }
        IC2.platform.requestTick(false, new Runnable(){

            @Override
            public void run() {
                World world = IC2.platform.getPlayerWorld();
                if (world == null || world.provider.getDimension() != dimensionId) {
                    return;
                }
                for (TeUpdateDataClient.TeData update : updateData.getTes()) {
                    TeUpdate.apply(update, world);
                }
            }
        });
    }

    private static void printDebugOutput(int dimensionId, TeUpdateDataClient data) {
        StringBuilder out = new StringBuilder();
        out.append("dimension: ");
        out.append(dimensionId);
        out.append(", ");
        out.append(data.getTes().size());
        out.append("tes:\n");
        for (TeUpdateDataClient.TeData te : data.getTes()) {
            out.append("  pos: ");
            out.append(te.pos.getX());
            out.append('/');
            out.append(te.pos.getY());
            out.append('/');
            out.append(te.pos.getZ());
            out.append(", ");
            out.append(te.getFields().size());
            out.append(" fields:\n");
            for (TeUpdateDataClient.FieldData field : te.getFields()) {
                out.append("    ");
                out.append(field.name);
                out.append(" = ");
                out.append(field.value);
                if (field.value != null) {
                    out.append(" (");
                    out.append(field.value.getClass().getSimpleName());
                    out.append(')');
                }
                out.append('\n');
            }
            if (te.teClass != null) {
                out.append("    TE Class: ");
                out.append(te.teClass.getName());
                out.append('\n');
                continue;
            }
            out.append("    no TE Class\n");
        }
        out.setLength(out.length() - 1);
        IC2.log.info(LogCategory.Network, "Received TE Update:\n" + out.toString());
    }

    private static void apply(TeUpdateDataClient.TeData update, World world) {
        IBlockState state = world.getBlockState(update.pos);
        if (state.getBlock() != BlockName.te.getInstance()) {
            if (debug) {
                IC2.log.info(LogCategory.Network, "Can't apply update to %d/%d/%d, invalid state %s.", new Object[]{update.pos.getX(), update.pos.getY(), update.pos.getZ(), state});
            }
            return;
        }
        TileEntity te = world.getTileEntity(update.pos);
        if (update.teClass != null && (te == null || te.getClass() != update.teClass)) {
            if (debug) {
                IC2.log.info(LogCategory.Network, "Instantiating %d/%d/%d with %s", update.pos.getX(), update.pos.getY(), update.pos.getZ(), update.teClass.getName());
            }
            te = TileEntityBlock.instantiate(update.teClass);
            world.setTileEntity(update.pos, te);
        } else {
            if (te == null) {
                if (debug) {
                    IC2.log.info(LogCategory.Network, "Can't apply update to %d/%d/%d, no te/teClass.", update.pos.getX(), update.pos.getY(), update.pos.getZ());
                }
                return;
            }
            if (debug) {
                IC2.log.info(LogCategory.Network, "TE class %d/%d/%d unchanged.", update.pos.getX(), update.pos.getY(), update.pos.getZ());
            }
        }
        for (TeUpdateDataClient.FieldData fieldUpdate : update.getFields()) {
            Object value = DataEncoder.getValue(fieldUpdate.value);
            if (fieldUpdate.field != null) {
                ReflectionUtil.setValue((Object)te, fieldUpdate.field, value);
            } else {
                ReflectionUtil.setValueRecursive((Object)te, fieldUpdate.name, value);
            }
            if (!(te instanceof INetworkUpdateListener)) continue;
            ((INetworkUpdateListener)te).onNetworkUpdate(fieldUpdate.name);
        }
    }

}

