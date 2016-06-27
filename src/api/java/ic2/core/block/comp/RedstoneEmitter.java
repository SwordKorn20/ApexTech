/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package ic2.core.block.comp;

import ic2.core.block.BlockTileEntity;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.comp.TileEntityComponent;
import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class RedstoneEmitter
extends TileEntityComponent {
    private int level;

    public RedstoneEmitter(TileEntityBlock parent) {
        super(parent);
    }

    public int getLevel() {
        return this.level;
    }

    public void setLevel(int newLevel) {
        if (newLevel == this.level) {
            return;
        }
        this.level = newLevel;
        this.parent.getWorld().notifyNeighborsOfStateChange(this.parent.getPos(), (Block)this.parent.getBlockType());
    }
}

