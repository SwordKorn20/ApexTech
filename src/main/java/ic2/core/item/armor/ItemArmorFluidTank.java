/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.IFluidContainerItem
 */
package ic2.core.item.armor;

import ic2.api.item.IItemHudInfo;
import ic2.core.init.InternalName;
import ic2.core.init.Localization;
import ic2.core.item.armor.ItemArmorUtility;
import ic2.core.ref.ItemName;
import ic2.core.util.StackUtil;
import ic2.core.util.Util;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

public abstract class ItemArmorFluidTank
extends ItemArmorUtility
implements IFluidContainerItem,
IItemHudInfo {
    protected final int capacity;
    protected final Fluid allowfluid;

    public ItemArmorFluidTank(ItemName name, InternalName armorName, Fluid allowfluid, int capacity) {
        super(name, armorName, EntityEquipmentSlot.CHEST);
        this.setMaxDamage(27);
        this.setMaxStackSize(1);
        this.capacity = capacity;
        this.allowfluid = allowfluid;
    }

    public void filltank(ItemStack stack) {
        NBTTagCompound nbtTagCompound = StackUtil.getOrCreateNbtData(stack);
        NBTTagCompound fluidTag = nbtTagCompound.getCompoundTag("Fluid");
        FluidStack fs = new FluidStack(this.allowfluid, this.getCapacity(stack));
        fs.writeToNBT(fluidTag);
        nbtTagCompound.setTag("Fluid", (NBTBase)fluidTag);
    }

    public double getCharge(ItemStack stack) {
        if (this.getFluid(stack) == null) {
            return 0.0;
        }
        double ret = this.getFluid((ItemStack)stack).amount;
        return ret > 0.0 ? ret : 0.0;
    }

    public double getMaxCharge(ItemStack stack) {
        return this.getCapacity(stack);
    }

    protected void Updatedamage(ItemStack stack) {
        stack.setItemDamage(stack.getMaxDamage() - 1 - (int)Util.map(this.getCharge(stack), this.getMaxCharge(stack), stack.getMaxDamage() - 2));
    }

    public FluidStack getFluid(ItemStack stack) {
        NBTTagCompound nbtTagCompound = StackUtil.getOrCreateNbtData(stack);
        NBTTagCompound fluidTag = nbtTagCompound.getCompoundTag("Fluid");
        return FluidStack.loadFluidStackFromNBT((NBTTagCompound)fluidTag);
    }

    public boolean isEmpty(ItemStack stack) {
        return this.getFluid(stack) == null;
    }

    public int getCapacity(ItemStack container) {
        return this.capacity;
    }

    public FluidStack drain(ItemStack stack, int maxDrain, boolean doDrain) {
        if (stack.stackSize != 1) {
            return null;
        }
        NBTTagCompound nbtTagCompound = StackUtil.getOrCreateNbtData(stack);
        NBTTagCompound fluidTag = nbtTagCompound.getCompoundTag("Fluid");
        FluidStack fs = FluidStack.loadFluidStackFromNBT((NBTTagCompound)fluidTag);
        if (fs == null) {
            return null;
        }
        maxDrain = Math.min(fs.amount, maxDrain);
        if (doDrain) {
            fs.amount -= maxDrain;
            if (fs.amount <= 0) {
                nbtTagCompound.removeTag("Fluid");
            } else {
                fs.writeToNBT(fluidTag);
                nbtTagCompound.setTag("Fluid", (NBTBase)fluidTag);
            }
        }
        this.Updatedamage(stack);
        return new FluidStack(fs, maxDrain);
    }

    public int fill(ItemStack stack, FluidStack resource, boolean doFill) {
        if (stack.stackSize != 1) {
            return 0;
        }
        if (resource == null) {
            return 0;
        }
        if (resource.getFluid() != this.allowfluid) {
            return 0;
        }
        NBTTagCompound nbtTagCompound = StackUtil.getOrCreateNbtData(stack);
        NBTTagCompound fluidTag = nbtTagCompound.getCompoundTag("Fluid");
        FluidStack fs = FluidStack.loadFluidStackFromNBT((NBTTagCompound)fluidTag);
        if (fs == null) {
            fs = new FluidStack(resource, 0);
        }
        if (!fs.isFluidEqual(resource)) {
            return 0;
        }
        int amount = Math.min(this.capacity - fs.amount, resource.amount);
        if (doFill && amount > 0) {
            fs.amount += amount;
            fs.writeToNBT(fluidTag);
            nbtTagCompound.setTag("Fluid", (NBTBase)fluidTag);
        }
        this.Updatedamage(stack);
        return amount;
    }

    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        FluidStack fs = this.getFluid(stack);
        if (fs != null) {
            tooltip.add("< " + fs.getLocalizedName() + ", " + fs.amount + " mB >");
        } else {
            tooltip.add(Localization.translate("ic2.item.FluidContainer.Empty"));
        }
    }

    @Override
    public List<String> getHudInfo(ItemStack stack) {
        LinkedList<String> info = new LinkedList<String>();
        FluidStack fs = this.getFluid(stack);
        if (fs != null) {
            info.add("< " + fs.getLocalizedName() + ", " + fs.amount + " mB >");
        } else {
            info.add(Localization.translate("ic2.item.FluidContainer.Empty"));
        }
        return info;
    }

    @Override
    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack) {
        return false;
    }
}

