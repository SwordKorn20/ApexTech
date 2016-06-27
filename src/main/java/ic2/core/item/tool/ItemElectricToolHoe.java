/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockGrass
 *  net.minecraft.block.BlockMycelium
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.entity.player.UseHoeEvent
 *  net.minecraftforge.fml.common.eventhandler.Event
 *  net.minecraftforge.fml.common.eventhandler.EventBus
 */
package ic2.core.item.tool;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItemManager;
import ic2.core.IC2;
import ic2.core.Platform;
import ic2.core.item.tool.ItemElectricTool;
import ic2.core.ref.ItemName;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockMycelium;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.UseHoeEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;

public class ItemElectricToolHoe
extends ItemElectricTool {
    public ItemElectricToolHoe() {
        super(ItemName.electric_hoe, 50, ItemElectricTool.HarvestLevel.Iron, EnumSet.of(ItemElectricTool.ToolClass.Hoe));
        this.maxCharge = 10000;
        this.transferLimit = 100;
        this.tier = 1;
        this.efficiencyOnProperMaterial = 16.0f;
    }

    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {
        ElectricItem.manager.use(stack, this.operationEnergyCost, (EntityLivingBase)player);
        return false;
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!player.canPlayerEdit(pos, side, stack)) {
            return EnumActionResult.PASS;
        }
        if (!ElectricItem.manager.canUse(stack, this.operationEnergyCost)) {
            return EnumActionResult.PASS;
        }
        if (MinecraftForge.EVENT_BUS.post((Event)new UseHoeEvent(player, stack, world, pos))) {
            return EnumActionResult.PASS;
        }
        Block block = world.getBlockState(pos).getBlock();
        if (side != EnumFacing.DOWN && world.isAirBlock(pos.up()) && (block == Blocks.MYCELIUM || block == Blocks.GRASS || block == Blocks.DIRT)) {
            block = Blocks.FARMLAND;
            SoundType stepSound = block.getSoundType();
            world.playSound(null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, stepSound.getStepSound(), SoundCategory.BLOCKS, (stepSound.getVolume() + 1.0f) / 2.0f, stepSound.getPitch() * 0.8f);
            if (IC2.platform.isSimulating()) {
                world.setBlockState(pos, block.getDefaultState());
                ElectricItem.manager.use(stack, this.operationEnergyCost, (EntityLivingBase)player);
            }
            return EnumActionResult.SUCCESS;
        }
        return super.onItemUse(stack, player, world, pos, hand, side, hitX, hitY, hitZ);
    }
}

