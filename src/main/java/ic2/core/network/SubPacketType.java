/*
 * Decompiled with CFR 0_114.
 */
package ic2.core.network;

import ic2.core.IC2;
import ic2.core.network.GrowingBuffer;
import ic2.core.util.Log;
import ic2.core.util.LogCategory;

public enum SubPacketType {
    Rpc(true, true),
    TileEntityEvent(true, true),
    ItemEvent(true, true),
    PlayerItemData(true, true),
    ContainerData(true, true),
    ContainerEvent(true, true),
    LargePacket(true, false),
    GuiDisplay(true, false),
    ExplosionEffect(true, false),
    TileEntityBlockComponent(true, false),
    TileEntityBlockLandEffect(true, false),
    KeyUpdate(false, true),
    TileEntityData(false, true);
    
    private boolean serverToClient;
    private boolean clientToServer;
    private static final SubPacketType[] values;

    private SubPacketType(boolean serverToClient, boolean clientToServer) {
        this.serverToClient = serverToClient;
        this.clientToServer = clientToServer;
    }

    public void writeTo(GrowingBuffer out) {
        out.writeByte(this.getId());
    }

    public int getId() {
        return this.ordinal() + 1;
    }

    public static SubPacketType read(GrowingBuffer in, boolean simulating) {
        int id = in.readUnsignedByte() - 1;
        if (id < 0 || id >= values.length) {
            IC2.log.warn(LogCategory.Network, "Invalid sub packet type: %d", id);
            return null;
        }
        SubPacketType ret = values[id];
        if (simulating && !ret.clientToServer || !simulating && !ret.serverToClient) {
            Object[] arrobject = new Object[2];
            arrobject[0] = ret.name();
            arrobject[1] = simulating ? "server" : "client";
            IC2.log.warn(LogCategory.Network, "Invalid sub packet type %s for side %s", arrobject);
            return null;
        }
        return ret;
    }

    static {
        values = SubPacketType.values();
        if (values.length > 255) {
            throw new RuntimeException("too many sub packet types");
        }
    }
}

