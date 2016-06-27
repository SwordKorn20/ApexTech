/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntityFurnace
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidContainerRegistry
 *  net.minecraftforge.fluids.FluidRegistry
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.IFluidContainerItem
 */
package ic2.core.util;

import ic2.api.info.IInfoProvider;
import ic2.core.block.state.IIdProvider;
import ic2.core.init.MainConfig;
import ic2.core.item.type.CraftingItemType;
import ic2.core.item.type.DustResourceType;
import ic2.core.ref.ItemName;
import ic2.core.util.ConfigUtil;
import ic2.core.util.StackUtil;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

public class ItemInfo
implements IInfoProvider {
    @Override
    public double getEnergyValue(ItemStack stack) {
        Item item = stack.getItem();
        if (item == null) {
            return 0.0;
        }
        if (StackUtil.checkItemEquality(stack, ItemName.single_use_battery.getItemStack())) {
            return 1200.0;
        }
        if (StackUtil.checkItemEquality(stack, Items.REDSTONE)) {
            return 800.0;
        }
        if (StackUtil.checkItemEquality(stack, ItemName.dust.getItemStack(DustResourceType.energium))) {
            return 16000.0;
        }
        return 0.0;
    }

    @Override
    public int getFuelValue(ItemStack stack, boolean allowLava) {
        boolean isLava;
        if (stack == null) {
            return 0;
        }
        Item item = stack.getItem();
        if (item == null) {
            return 0;
        }
        if ((StackUtil.checkItemEquality(stack, ItemName.crafting.getItemStack(CraftingItemType.scrap)) || StackUtil.checkItemEquality(stack, ItemName.crafting.getItemStack(CraftingItemType.scrap_box))) && !ConfigUtil.getBool(MainConfig.get(), "misc/allowBurningScrap")) {
            return 0;
        }
        FluidStack liquid = FluidContainerRegistry.getFluidForFilledItem((ItemStack)stack);
        if (liquid == null && item instanceof IFluidContainerItem) {
            liquid = ((IFluidContainerItem)item).getFluid(stack);
        }
        boolean bl = isLava = liquid != null && liquid.amount > 0 && liquid.getFluid() == FluidRegistry.LAVA;
        if (isLava && !allowLava) {
            return 0;
        }
        int ret = TileEntityFurnace.getItemBurnTime((ItemStack)stack);
        return isLava ? ret / 10 : ret;
    }
}

