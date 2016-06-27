/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraft.world.chunk.Chunk
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.item.block;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.IEnergyNet;
import ic2.api.tile.IEnergyStorage;
import ic2.core.IC2;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.comp.Energy;
import ic2.core.block.comp.TileEntityComponent;
import ic2.core.block.wiring.TileEntityTransformer;
import ic2.core.init.Localization;
import ic2.core.item.block.ItemBlockIC2;
import ic2.core.network.NetworkManager;
import ic2.core.ref.BlockName;
import ic2.core.ref.IBlockModelProvider;
import ic2.core.ref.TeBlock;
import ic2.core.util.SideGateway;
import ic2.core.util.StackUtil;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBlockTileEntity
extends ItemBlockIC2 {
    public ItemBlockTileEntity(Block block) {
        super(block);
        this.setHasSubtypes(true);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        TeBlock teBlock = ItemBlockTileEntity.getTeBlock(stack);
        String name = teBlock == null ? "invalid" : teBlock.name();
        return super.getUnlocalizedName() + "." + name;
    }

    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        TileEntityBlock te;
        TeBlock block = ItemBlockTileEntity.getTeBlock(stack);
        if (block != null && block.getDummyTe() != null && (te = block.getDummyTe()).hasComponent(Energy.class)) {
            Energy energy = (Energy)te.getComponent(Energy.class);
            if (!energy.getSourceDirs().isEmpty()) {
                tooltip.add(Localization.translate("ic2.item.tooltip.PowerTier", energy.getSourceTier()));
            } else if (!energy.getSinkDirs().isEmpty()) {
                tooltip.add(Localization.translate("ic2.item.tooltip.PowerTier", energy.getSinkTier()));
            }
            if (te instanceof IEnergyStorage) {
                IEnergyStorage storage = (IEnergyStorage)((Object)te);
                tooltip.add(String.format("%s %.0f %s %s %d %s", Localization.translate("ic2.item.tooltip.Output"), EnergyNet.instance.getPowerFromTier(energy.getSourceTier()), Localization.translate("ic2.generic.text.EUt"), Localization.translate("ic2.item.tooltip.Capacity"), storage.getCapacity(), Localization.translate("ic2.generic.text.EU")));
                tooltip.add(Localization.translate("ic2.item.tooltip.Store") + " " + (long)StackUtil.getOrCreateNbtData(stack).getDouble("energy") + " " + Localization.translate("ic2.generic.text.EU"));
            }
            if (te instanceof TileEntityTransformer) {
                tooltip.add(String.format("%s %.0f %s %s %.0f %s", Localization.translate("ic2.item.tooltip.Low"), EnergyNet.instance.getPowerFromTier(energy.getSinkTier()), Localization.translate("ic2.generic.text.EUt"), Localization.translate("ic2.item.tooltip.High"), EnergyNet.instance.getPowerFromTier(energy.getSourceTier() + 1), Localization.translate("ic2.generic.text.EUt")));
            }
        }
    }

    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        assert (newState.getBlock() == this.block);
        TeBlock teBlock = ItemBlockTileEntity.getTeBlock(stack);
        if (teBlock == null) {
            return false;
        }
        Class<? extends TileEntityBlock> teClass = teBlock.getTeClass();
        if (teClass == null) {
            return false;
        }
        TileEntityBlock te = TileEntityBlock.instantiate(teClass);
        if (!ItemBlockTileEntity.placeTeBlock(stack, (EntityLivingBase)player, world, pos, side, te)) {
            return false;
        }
        return true;
    }

    public static boolean placeTeBlock(ItemStack stack, EntityLivingBase placer, World world, BlockPos pos, EnumFacing side, TileEntityBlock te) {
        IBlockState oldState = world.getBlockState(pos);
        IBlockState newState = BlockName.te.getInstance().getDefaultState();
        if (!world.setBlockState(pos, newState, 0)) {
            return false;
        }
        world.setTileEntity(pos, (TileEntity)te);
        te.onPlaced(stack, placer, side);
        world.markAndNotifyBlock(pos, world.getChunkFromBlockCoords(pos), oldState, newState, 3);
        if (!world.isRemote) {
            IC2.network.get(true).sendInitialData(te);
        }
        return true;
    }

    @Override
    public EnumRarity getRarity(ItemStack stack) {
        TeBlock teblock = ItemBlockTileEntity.getTeBlock(stack);
        return teblock != null ? teblock.rarity : EnumRarity.COMMON;
    }

    private static TeBlock getTeBlock(ItemStack stack) {
        if (stack == null) {
            return null;
        }
        return TeBlock.get(stack.getItemDamage());
    }
}

