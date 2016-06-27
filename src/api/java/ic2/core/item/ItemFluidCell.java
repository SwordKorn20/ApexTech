/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.Container
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.RayTraceResult$Type
 *  net.minecraft.world.World
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.item;

import ic2.core.item.ItemIC2FluidContainer;
import ic2.core.ref.ItemName;
import ic2.core.util.LiquidUtil;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.mutable.MutableObject;

public class ItemFluidCell
extends ItemIC2FluidContainer {
    public ItemFluidCell() {
        super(ItemName.fluid_cell, 1000);
    }

    public boolean isRepairable() {
        return false;
    }

    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float xOffset, float yOffset, float zOffset) {
        if (world.isRemote) {
            return EnumActionResult.FAIL;
        }
        if (this.interactWithTank(stack, player, world, pos, side)) {
            player.inventoryContainer.detectAndSendChanges();
            return EnumActionResult.SUCCESS;
        }
        RayTraceResult position = this.rayTrace(world, player, true);
        if (position == null) {
            return EnumActionResult.FAIL;
        }
        if (position.typeOfHit == RayTraceResult.Type.BLOCK) {
            pos = position.getBlockPos();
            if (!world.canMineBlockBody(player, pos)) {
                return EnumActionResult.FAIL;
            }
            if (!player.canPlayerEdit(pos, position.sideHit, stack)) {
                return EnumActionResult.FAIL;
            }
            if (LiquidUtil.drainBlockToContainer(world, pos, stack, player) || LiquidUtil.fillBlockFromContainer(world, pos, stack, player) || LiquidUtil.fillBlockFromContainer(world, pos.offset(side), stack, player)) {
                player.inventoryContainer.detectAndSendChanges();
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.FAIL;
    }

    @Override
    public boolean canfill(Fluid fluid) {
        return true;
    }

    @SideOnly(value=Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> subItems) {
        ItemStack emptyStack = new ItemStack(item);
        subItems.add(emptyStack);
        for (Fluid fluid : LiquidUtil.getAllFluids()) {
            ItemStack stack;
            if (fluid == null || this.fill(stack = emptyStack.copy(), new FluidStack(fluid, Integer.MAX_VALUE), true) <= 0) continue;
            subItems.add(stack);
        }
    }

    private boolean interactWithTank(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side) {
        assert (!world.isRemote);
        TileEntity te = world.getTileEntity(pos);
        if (!LiquidUtil.isFluidTile(te, side)) {
            return false;
        }
        FluidStack fs = this.getFluid(stack);
        MutableObject output = new MutableObject();
        if (fs == null || player.isSneaking() && fs.amount < this.capacity) {
            int amount = fs == null ? this.capacity : this.capacity - fs.amount;
            FluidStack input = LiquidUtil.drainTile(te, side, amount, true);
            if (input == null || input.amount <= 0) {
                return true;
            }
            amount = LiquidUtil.fillContainer(stack, input, output, LiquidUtil.FluidContainerOutputMode.InPlacePreferred, true);
            if (amount <= 0 || !LiquidUtil.storeOutputContainer(output, player)) {
                return true;
            }
            amount = LiquidUtil.fillContainer(stack, input, output, LiquidUtil.FluidContainerOutputMode.InPlacePreferred, false);
            LiquidUtil.drainTile(te, side, amount, false);
            return true;
        }
        int amount = LiquidUtil.fillTile(te, side, fs, true);
        if (amount <= 0) {
            return true;
        }
        fs = LiquidUtil.drainContainer(stack, null, amount, output, LiquidUtil.FluidContainerOutputMode.InPlacePreferred, true);
        if (fs == null || fs.amount <= 0 || !LiquidUtil.storeOutputContainer(output, player)) {
            return true;
        }
        fs = LiquidUtil.drainContainer(stack, null, amount, output, LiquidUtil.FluidContainerOutputMode.InPlacePreferred, false);
        LiquidUtil.fillTile(te, side, fs, false);
        return true;
    }
}

