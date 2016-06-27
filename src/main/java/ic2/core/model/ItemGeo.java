/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  net.minecraft.client.renderer.vertex.VertexFormat
 *  net.minecraft.util.EnumFacing
 */
package ic2.core.model;

import java.nio.IntBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;

public class ItemGeo {
    public static final int quadVertexCount = 4;
    public static final VertexFormat vertexFormat = DefaultVertexFormats.ITEM;
    public static final int dataStride = vertexFormat.getNextOffset() / 4;

    public static IntBuffer getQuadBuffer() {
        return IntBuffer.allocate(4 * dataStride);
    }

    public static void generateVertex(float x, float y, float z, int color, float u, float v, EnumFacing facing, IntBuffer out) {
        ItemGeo.generateVertex(x, y, z, color, u, v, facing.getFrontOffsetX(), facing.getFrontOffsetY(), facing.getFrontOffsetZ(), out);
    }

    public static void generateVertex(float x, float y, float z, int color, float u, float v, float nx, float ny, float nz, IntBuffer out) {
        out.put(Float.floatToRawIntBits(x));
        out.put(Float.floatToRawIntBits(y));
        out.put(Float.floatToRawIntBits(z));
        out.put(color);
        out.put(Float.floatToRawIntBits(u));
        out.put(Float.floatToRawIntBits(v));
        out.put(ItemGeo.packNormals(nx, ny, nz));
    }

    private static int packNormals(float nx, float ny, float nz) {
        return ItemGeo.mapFloatToByte(nx) | ItemGeo.mapFloatToByte(ny) << 8 | ItemGeo.mapFloatToByte(nz) << 16;
    }

    private static int mapFloatToByte(float f) {
        assert (f >= -1.0f && f <= 1.0f);
        return Math.round(f * 127.0f) & 255;
    }
}

