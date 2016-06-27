/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package ic2.core.audio;

import ic2.core.audio.PositionSpec;
import java.lang.ref.WeakReference;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AudioPosition {
    private final WeakReference<World> worldRef;
    public final float x;
    public final float y;
    public final float z;

    public static AudioPosition getFrom(Object obj, PositionSpec positionSpec) {
        if (obj instanceof AudioPosition) {
            return (AudioPosition)obj;
        }
        if (obj instanceof Entity) {
            Entity e = (Entity)obj;
            return new AudioPosition(e.worldObj, (float)e.posX, (float)e.posY, (float)e.posZ);
        }
        if (obj instanceof TileEntity) {
            TileEntity te = (TileEntity)obj;
            return new AudioPosition(te.getWorld(), (float)te.getPos().getX() + 0.5f, (float)te.getPos().getY() + 0.5f, (float)te.getPos().getZ() + 0.5f);
        }
        return null;
    }

    public AudioPosition(World world, float x, float y, float z) {
        this.worldRef = new WeakReference<World>(world);
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public AudioPosition(World world, BlockPos pos) {
        this(world, (float)pos.getX() + 0.5f, (float)pos.getY() + 0.5f, (float)pos.getZ() + 0.5f);
    }

    public World getWorld() {
        return this.worldRef.get();
    }
}

