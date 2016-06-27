/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.Container
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package ic2.core.item.tool;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.IEnergyNet;
import ic2.api.energy.tile.IEnergyConductor;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.item.IBoxable;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.Platform;
import ic2.core.item.IHandHeldInventory;
import ic2.core.item.ItemIC2;
import ic2.core.item.tool.ContainerMeter;
import ic2.core.item.tool.HandHeldMeter;
import ic2.core.ref.ItemName;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemToolMeter
extends ItemIC2
implements IBoxable,
IHandHeldInventory {
    public ItemToolMeter() {
        super(ItemName.meter);
        this.maxStackSize = 1;
        this.setMaxDamage(0);
    }

    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (world.isRemote) {
            return EnumActionResult.PASS;
        }
        IEnergyTile tile = EnergyNet.instance.getTile(world, pos);
        if (tile instanceof IEnergySource || tile instanceof IEnergyConductor || tile instanceof IEnergySink) {
            if (IC2.platform.launchGui(player, this.getInventory(player, stack))) {
                ContainerMeter container = (ContainerMeter)player.openContainer;
                container.setUut(tile);
                return EnumActionResult.SUCCESS;
            }
        } else {
            IC2.platform.messagePlayer(player, "Not an energy net tile", new Object[0]);
        }
        return EnumActionResult.SUCCESS;
    }

    @Override
    public boolean canBeStoredInToolbox(ItemStack itemstack) {
        return true;
    }

    @Override
    public IHasGui getInventory(EntityPlayer player, ItemStack stack) {
        return new HandHeldMeter(player, stack);
    }
}

