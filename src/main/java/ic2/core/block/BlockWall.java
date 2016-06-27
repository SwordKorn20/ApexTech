/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.EnumDyeColor
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.Event
 *  net.minecraftforge.fml.common.eventhandler.EventBus
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package ic2.core.block;

import ic2.api.event.RetextureEvent;
import ic2.core.Ic2Player;
import ic2.core.block.BlockMultiID;
import ic2.core.block.TileEntityWall;
import ic2.core.block.state.EnumProperty;
import ic2.core.item.block.ItemBlockTileEntity;
import ic2.core.ref.BlockName;
import ic2.core.ref.IBlockModelProvider;
import ic2.core.util.Ic2Color;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BlockWall
extends BlockMultiID<Ic2Color> {
    public static final Ic2Color defaultColor = Ic2Color.light_gray;

    public static BlockWall create() {
        return (BlockWall)BlockMultiID.create(BlockWall.class, Ic2Color.class, new Object[0]);
    }

    private BlockWall() {
        super(BlockName.wall, Material.ROCK);
        this.setHardness(3.0f);
        this.setResistance(30.0f);
        this.setSoundType(SoundType.STONE);
        this.setDefaultState(this.blockState.getBaseState().withProperty((IProperty)this.typeProperty, (Comparable)((Object)defaultColor)));
        MinecraftForge.EVENT_BUS.register((Object)this);
    }

    public boolean recolorBlock(World world, BlockPos pos, EnumFacing side, EnumDyeColor color) {
        IBlockState state = world.getBlockState(pos);
        Ic2Color type = (Ic2Color)this.getType(state);
        if (type == null) {
            return false;
        }
        Ic2Color newColor = Ic2Color.get(color);
        if (type != newColor) {
            world.setBlockState(pos, state.withProperty((IProperty)this.typeProperty, (Comparable)((Object)newColor)));
            return true;
        }
        return false;
    }

    @SubscribeEvent
    public void onRetexture(RetextureEvent event) {
        if (event.state.getBlock() != this) {
            return;
        }
        World world = event.getWorld();
        Ic2Color color = (Ic2Color)((Object)event.state.getValue((IProperty)this.typeProperty));
        if (!ItemBlockTileEntity.placeTeBlock(null, (EntityLivingBase)Ic2Player.get(world), world, event.pos, EnumFacing.DOWN, new TileEntityWall(color))) {
            return;
        }
        IBlockState newState = BlockName.te.getInstance().getDefaultState();
        RetextureEvent event2 = new RetextureEvent(world, event.pos, newState, event.side, event.player, event.refState, event.refVariant, event.refSide, event.refColorMultipliers);
        MinecraftForge.EVENT_BUS.post((Event)event2);
        if (event2.applied) {
            event.applied = true;
            event.setCanceled(true);
        } else {
            world.setBlockState(event.pos, event.state);
        }
    }
}

