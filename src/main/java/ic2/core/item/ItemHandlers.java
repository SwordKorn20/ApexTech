/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockPistonBase
 *  net.minecraft.block.BlockStaticLiquid
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyBool
 *  net.minecraft.block.properties.PropertyDirection
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.PlayerCapabilities
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.RayTraceResult$Type
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.fluids.Fluid
 */
package ic2.core.item;

import ic2.api.recipe.IScrapboxManager;
import ic2.api.recipe.Recipes;
import ic2.core.IC2Potion;
import ic2.core.block.BlockSheet;
import ic2.core.block.reactor.tileentity.TileEntityNuclearReactorElectric;
import ic2.core.block.state.IIdProvider;
import ic2.core.item.ItemMulti;
import ic2.core.item.armor.ItemArmorHazmat;
import ic2.core.item.type.IRadioactiveItemType;
import ic2.core.ref.BlockName;
import ic2.core.ref.FluidName;
import ic2.core.ref.IBlockModelProvider;
import ic2.core.ref.TeBlock;
import ic2.core.util.Util;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;

public class ItemHandlers {
    public static ItemMulti.IItemRightClickHandler cfPowderApply = new ItemMulti.IItemRightClickHandler(){

        @Override
        public ActionResult<ItemStack> onRightClick(ItemStack stack, EntityPlayer player, EnumHand hand) {
            RayTraceResult position = Util.traceBlocks(player, true);
            if (position == null) {
                return new ActionResult(EnumActionResult.PASS, (Object)stack);
            }
            if (position.typeOfHit == RayTraceResult.Type.BLOCK) {
                if (!player.worldObj.canMineBlockBody(player, position.getBlockPos())) {
                    return new ActionResult(EnumActionResult.FAIL, (Object)stack);
                }
                if (player.worldObj.getBlockState(position.getBlockPos()).getBlock() == Blocks.WATER) {
                    --stack.stackSize;
                    player.worldObj.setBlockState(position.getBlockPos(), FluidName.construction_foam.getInstance().getBlock().getDefaultState());
                    new ActionResult(EnumActionResult.SUCCESS, (Object)stack);
                }
            }
            return new ActionResult(EnumActionResult.FAIL, (Object)stack);
        }
    };
    public static ItemMulti.IItemRightClickHandler scrapBoxUnpack = new ItemMulti.IItemRightClickHandler(){

        @Override
        public ActionResult<ItemStack> onRightClick(ItemStack stack, EntityPlayer player, EnumHand hand) {
            ItemStack drop;
            if (!player.worldObj.isRemote && (drop = Recipes.scrapboxDrops.getDrop(stack, false)) != null && player.dropItem(drop, false) != null && !player.capabilities.isCreativeMode) {
                --stack.stackSize;
                return new ActionResult(EnumActionResult.SUCCESS, (Object)stack);
            }
            return new ActionResult(EnumActionResult.PASS, (Object)stack);
        }
    };
    public static ItemMulti.IItemUseHandler resinUse = new ItemMulti.IItemUseHandler(){

        @Override
        public EnumActionResult onUse(ItemStack stack, EntityPlayer player, BlockPos pos, EnumHand hand, EnumFacing side) {
            IBlockState state = player.worldObj.getBlockState(pos);
            if (state.getBlock() == Blocks.PISTON && state.getValue((IProperty)BlockPistonBase.FACING) == side) {
                IBlockState newState = Blocks.STICKY_PISTON.getDefaultState().withProperty((IProperty)BlockPistonBase.FACING, (Comparable)side).withProperty((IProperty)BlockPistonBase.EXTENDED, state.getValue((IProperty)BlockPistonBase.EXTENDED));
                player.worldObj.setBlockState(pos, newState, 3);
                if (!player.capabilities.isCreativeMode) {
                    --stack.stackSize;
                }
                return EnumActionResult.SUCCESS;
            }
            if (side != EnumFacing.UP) {
                return EnumActionResult.PASS;
            }
            pos = pos.up();
            if (!state.getBlock().isAir(state, (IBlockAccess)player.worldObj, pos) || !BlockName.sheet.getInstance().canReplace(player.worldObj, pos, side, BlockName.sheet.getItemStack(BlockSheet.SheetType.resin))) {
                return EnumActionResult.PASS;
            }
            player.worldObj.setBlockState(pos, BlockName.sheet.getBlockState(BlockSheet.SheetType.resin));
            if (!player.capabilities.isCreativeMode) {
                --stack.stackSize;
            }
            return EnumActionResult.PASS;
        }
    };
    public static ItemMulti.IItemUpdateHandler radioactiveUpdate = new ItemMulti.IItemUpdateHandler(){

        @Override
        public void onUpdate(ItemStack stack, World world, Entity rawEntity, int slotIndex, boolean isCurrentItem) {
            Item item = stack.getItem();
            if (item == null || !(item instanceof ItemMulti)) {
                return;
            }
            Object rawType = ((ItemMulti)item).getType(stack);
            if (!(rawType instanceof IRadioactiveItemType)) {
                return;
            }
            IRadioactiveItemType type = (IRadioactiveItemType)rawType;
            if (!(rawEntity instanceof EntityLivingBase)) {
                return;
            }
            EntityLivingBase entity = (EntityLivingBase)rawEntity;
            if (ItemArmorHazmat.hasCompleteHazmat(entity)) {
                return;
            }
            IC2Potion.radiation.applyTo(entity, type.getRadiationDuration() * 20, type.getRadiationAmplifier());
        }
    };
    public static TeBlock.ITePlaceHandler reactorChamberPlace = new TeBlock.ITePlaceHandler(){

        @Override
        public boolean canReplace(World world, BlockPos pos, EnumFacing side, ItemStack stack) {
            int count = 0;
            for (EnumFacing dir : EnumFacing.VALUES) {
                TileEntity te = world.getTileEntity(pos.offset(dir));
                if (!(te instanceof TileEntityNuclearReactorElectric)) continue;
                ++count;
            }
            return count == 1;
        }
    };

}

