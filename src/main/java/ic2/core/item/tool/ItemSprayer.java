/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.RayTraceResult$Type
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidStack
 */
package ic2.core.item.tool;

import ic2.api.item.IBoxable;
import ic2.core.IC2;
import ic2.core.Platform;
import ic2.core.block.BlockFoam;
import ic2.core.block.BlockIC2Fence;
import ic2.core.block.BlockScaffold;
import ic2.core.block.state.EnumProperty;
import ic2.core.block.state.IIdProvider;
import ic2.core.block.wiring.TileEntityCable;
import ic2.core.item.ItemIC2FluidContainer;
import ic2.core.item.armor.ItemArmorCFPack;
import ic2.core.ref.BlockName;
import ic2.core.ref.FluidName;
import ic2.core.ref.IBlockModelProvider;
import ic2.core.ref.IItemModelProvider;
import ic2.core.ref.ItemName;
import ic2.core.util.Keyboard;
import ic2.core.util.LiquidUtil;
import ic2.core.util.StackUtil;
import java.util.ArrayDeque;
import java.util.HashSet;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public class ItemSprayer
extends ItemIC2FluidContainer
implements IBoxable {
    public ItemSprayer() {
        super(ItemName.foam_sprayer, 8000);
        this.setMaxStackSize(1);
    }

    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if (IC2.platform.isSimulating() && IC2.keyboard.isModeSwitchKeyDown(player)) {
            NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(stack);
            int mode = nbtData.getInteger("mode");
            mode = mode == 0 ? 1 : 0;
            nbtData.setInteger("mode", mode);
            String sMode = mode == 0 ? "ic2.tooltip.mode.normal" : "ic2.tooltip.mode.single";
            IC2.platform.messagePlayer(player, "ic2.tooltip.mode", sMode);
        }
        return super.onItemRightClick(stack, world, player, hand);
    }

    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float xOffset, float yOffset, float zOffset) {
        ItemStack pack;
        BlockPos fluidPos;
        Target target;
        if (IC2.keyboard.isModeSwitchKeyDown(player)) {
            return EnumActionResult.PASS;
        }
        if (!IC2.platform.isSimulating()) {
            return EnumActionResult.SUCCESS;
        }
        RayTraceResult rtResult = this.rayTrace(world, player, true);
        if (rtResult == null) {
            return EnumActionResult.PASS;
        }
        if (rtResult.typeOfHit == RayTraceResult.Type.BLOCK && !pos.equals((Object)rtResult.getBlockPos()) && LiquidUtil.drainBlockToContainer(world, fluidPos = rtResult.getBlockPos(), stack, player)) {
            return EnumActionResult.SUCCESS;
        }
        int maxFoamBlocks = 0;
        FluidStack fluid = this.getFluid(stack);
        if (fluid != null && fluid.amount > 0) {
            maxFoamBlocks += fluid.amount / this.getFluidPerFoam();
        }
        if ((pack = player.inventory.armorInventory[2]) != null && pack.getItem() == ItemName.cf_pack.getInstance()) {
            fluid = ((ItemArmorCFPack)pack.getItem()).getFluid(pack);
            if (fluid != null && fluid.amount > 0) {
                maxFoamBlocks += fluid.amount / this.getFluidPerFoam();
            } else {
                pack = null;
            }
        } else {
            pack = null;
        }
        if (maxFoamBlocks == 0) {
            return EnumActionResult.FAIL;
        }
        maxFoamBlocks = Math.min(maxFoamBlocks, this.getMaxFoamBlocks(stack));
        if (ItemSprayer.canPlaceFoam(world, pos, Target.Scaffold)) {
            target = Target.Scaffold;
        } else if (ItemSprayer.canPlaceFoam(world, pos, Target.Cable)) {
            target = Target.Cable;
        } else {
            pos = pos.offset(side);
            target = Target.Any;
        }
        Vec3d viewVec = player.getLookVec();
        EnumFacing playerViewFacing = EnumFacing.getFacingFromVector((float)((float)viewVec.xCoord), (float)((float)viewVec.yCoord), (float)((float)viewVec.zCoord));
        int amount = this.sprayFoam(world, pos, playerViewFacing.getOpposite(), target, maxFoamBlocks);
        if ((amount *= this.getFluidPerFoam()) > 0) {
            if (pack != null) {
                fluid = ((ItemArmorCFPack)pack.getItem()).drainfromCFpack(player, pack, amount);
                amount -= fluid.amount;
            }
            if (amount > 0) {
                this.drain(stack, amount, true);
            }
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

    public int sprayFoam(World world, BlockPos pos, EnumFacing excludedDir, Target target, int maxFoamBlocks) {
        BlockPos cPos;
        if (!ItemSprayer.canPlaceFoam(world, pos, target)) {
            return 0;
        }
        ArrayDeque<BlockPos> toCheck = new ArrayDeque<BlockPos>();
        HashSet<BlockPos> positions = new HashSet<BlockPos>();
        toCheck.add(pos);
        while ((cPos = (BlockPos)toCheck.poll()) != null && positions.size() < maxFoamBlocks) {
            if (!ItemSprayer.canPlaceFoam(world, cPos, target) || !positions.add(cPos)) continue;
            for (EnumFacing dir : EnumFacing.VALUES) {
                if (dir == excludedDir) continue;
                toCheck.add(cPos.offset(dir));
            }
        }
        toCheck.clear();
        int failedPlacements = 0;
        for (BlockPos targetPos : positions) {
            IBlockState state = world.getBlockState(targetPos);
            Block targetBlock = state.getBlock();
            if (targetBlock == BlockName.scaffold.getInstance()) {
                BlockScaffold scaffold = (BlockScaffold)targetBlock;
                switch ((BlockScaffold.ScaffoldType)((Object)state.getValue(scaffold.getTypeProperty()))) {
                    case wood: 
                    case reinforced_wood: {
                        scaffold.dropBlockAsItem(world, targetPos, state, 0);
                        world.setBlockState(targetPos, BlockName.foam.getBlockState(BlockFoam.FoamType.normal));
                        break;
                    }
                    case reinforced_iron: {
                        StackUtil.dropAsEntity(world, targetPos, BlockName.fence.getItemStack(BlockIC2Fence.IC2FenceType.iron));
                    }
                    case iron: {
                        scaffold.dropBlockAsItem(world, targetPos, state, 0);
                        world.setBlockState(targetPos, BlockName.foam.getBlockState(BlockFoam.FoamType.reinforced));
                    }
                }
                continue;
            }
            if (targetBlock == BlockName.te.getInstance()) {
                TileEntity te = world.getTileEntity(targetPos);
                if (!(te instanceof TileEntityCable) || ((TileEntityCable)te).foam()) continue;
                ++failedPlacements;
                continue;
            }
            if (world.setBlockState(targetPos, BlockName.foam.getBlockState(BlockFoam.FoamType.normal))) continue;
            ++failedPlacements;
        }
        return positions.size() - failedPlacements;
    }

    protected int getMaxFoamBlocks(ItemStack stack) {
        NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(stack);
        if (nbtData.getInteger("mode") == 0) {
            return 10;
        }
        return 1;
    }

    protected int getFluidPerFoam() {
        return 100;
    }

    @Override
    public boolean canBeStoredInToolbox(ItemStack itemstack) {
        return true;
    }

    @Override
    public boolean canfill(Fluid fluid) {
        return fluid == FluidName.construction_foam.getInstance();
    }

    private static boolean canPlaceFoam(World world, BlockPos pos, Target target) {
        switch (target) {
            case Any: {
                return BlockName.foam.getInstance().canReplace(world, pos, EnumFacing.DOWN, BlockName.foam.getItemStack(BlockFoam.FoamType.normal));
            }
            case Scaffold: {
                return world.getBlockState(pos).getBlock() == BlockName.scaffold.getInstance();
            }
            case Cable: {
                if (world.getBlockState(pos).getBlock() != BlockName.te.getInstance()) {
                    return false;
                }
                TileEntity te = world.getTileEntity(pos);
                if (!(te instanceof TileEntityCable)) break;
                return !((TileEntityCable)te).isFoamed();
            }
            default: {
                assert (false);
                break;
            }
        }
        return false;
    }

    private static enum Target {
        Any,
        Scaffold,
        Cable;
        

        private Target() {
        }
    }

}

