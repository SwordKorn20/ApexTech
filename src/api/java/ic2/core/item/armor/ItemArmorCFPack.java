/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.Container
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.item.armor;

import ic2.core.init.InternalName;
import ic2.core.item.armor.ItemArmorFluidTank;
import ic2.core.ref.FluidName;
import ic2.core.ref.ItemName;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemArmorCFPack
extends ItemArmorFluidTank {
    public ItemArmorCFPack() {
        super(ItemName.cf_pack, InternalName.batpack, FluidName.construction_foam.getInstance(), 80000);
    }

    public FluidStack drainfromCFpack(EntityPlayer player, ItemStack pack, int amount) {
        if (this.isEmpty(pack)) {
            return null;
        }
        if (this.drain((ItemStack)pack, (int)amount, (boolean)false).amount < amount) {
            return null;
        }
        FluidStack fluid = this.drain(pack, amount, true);
        this.Updatedamage(pack);
        player.inventoryContainer.detectAndSendChanges();
        return fluid;
    }

    @SideOnly(value=Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> subItems) {
        ItemStack stack = new ItemStack(item, 1);
        this.filltank(stack);
        stack.setItemDamage(1);
        subItems.add(stack);
        stack = new ItemStack(item, 1);
        stack.setItemDamage(this.getMaxDamage());
        subItems.add(stack);
    }
}

