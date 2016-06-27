/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package ic2.core.item.tool;

import com.google.common.base.Predicate;
import ic2.api.item.IBoxable;
import ic2.api.item.IItemHudInfo;
import ic2.core.IC2;
import ic2.core.audio.AudioManager;
import ic2.core.audio.AudioPosition;
import ic2.core.audio.PositionSpec;
import ic2.core.block.state.IIdProvider;
import ic2.core.block.wiring.TileEntityCable;
import ic2.core.init.Localization;
import ic2.core.item.ItemIC2;
import ic2.core.item.type.CraftingItemType;
import ic2.core.ref.ItemName;
import ic2.core.util.StackUtil;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemToolCutter
extends ItemIC2
implements IBoxable,
IItemHudInfo {
    public ItemToolCutter() {
        super(ItemName.cutter);
        this.setMaxDamage(59);
        this.setMaxStackSize(1);
        this.canRepair = false;
    }

    public EnumActionResult onItemUse(ItemStack itemstack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityCable) {
            TileEntityCable cable = (TileEntityCable)te;
            Predicate<ItemStack> request = StackUtil.sameStack(ItemName.crafting.getItemStack(CraftingItemType.rubber));
            if (StackUtil.consumeFromPlayerInventory(player, request, 1, true) && cable.tryAddInsulation()) {
                StackUtil.consumeFromPlayerInventory(player, request, 1, false);
                ItemToolCutter.damageCutter(itemstack, 1);
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.PASS;
    }

    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        tooltip.add(Localization.translate("ic2.item.ItemTool.tooltip.UsesLeft") + " " + ItemToolCutter.getRemainingUses(stack));
    }

    public static void onInsulationRemoved(ItemStack stack, World world, BlockPos pos) {
        ItemToolCutter.damageCutter(stack, 3);
        if (world.isRemote) {
            IC2.audioManager.playOnce(new AudioPosition(world, pos), PositionSpec.Center, "Tools/InsulationCutters.ogg", true, IC2.audioManager.getDefaultVolume());
        }
    }

    private static void damageCutter(ItemStack stack, int damage) {
        if (!stack.isItemStackDamageable()) {
            return;
        }
        stack.setItemDamage(stack.getItemDamage() + damage);
        if (stack.getItemDamage() > stack.getMaxDamage()) {
            --stack.stackSize;
            if (stack.stackSize < 0) {
                stack.stackSize = 0;
            }
            stack.setItemDamage(0);
        }
    }

    @Override
    public boolean canBeStoredInToolbox(ItemStack stack) {
        return true;
    }

    @Override
    public List<String> getHudInfo(ItemStack stack) {
        LinkedList<String> info = new LinkedList<String>();
        info.add("Uses left: " + ItemToolCutter.getRemainingUses(stack));
        return info;
    }

    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }

    public ItemStack getContainerItem(ItemStack stack) {
        ItemStack ret = stack.copy();
        if (ret.attemptDamageItem(1, IC2.random)) {
            return null;
        }
        return ret;
    }
}

