/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.world.World
 *  net.minecraftforge.event.entity.player.FillBucketEvent
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package ic2.core;

import ic2.core.block.BlockIC2Fluid;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.FillBucketEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class IC2BucketHandler {
    @SubscribeEvent
    public void onBucketFill(FillBucketEvent event) {
        Block block;
        if (event.getTarget() != null && (block = event.getWorld().getBlockState(event.getTarget().getBlockPos()).getBlock()) instanceof BlockIC2Fluid && event.isCancelable()) {
            event.setCanceled(true);
        }
    }
}

