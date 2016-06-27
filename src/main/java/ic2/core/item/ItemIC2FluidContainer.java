/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidRegistry
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.IFluidContainerItem
 */
package ic2.core.item;

import ic2.api.item.IItemHudInfo;
import ic2.core.init.Localization;
import ic2.core.item.ItemIC2;
import ic2.core.ref.FluidName;
import ic2.core.ref.IMultiItem;
import ic2.core.ref.ItemName;
import ic2.core.util.StackUtil;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;

public abstract class ItemIC2FluidContainer
extends ItemIC2
implements IMultiItem<FluidName>,
IFluidContainerItem,
IItemHudInfo {
    protected final int capacity;

    public ItemIC2FluidContainer(ItemName name, int capacity) {
        super(name);
        this.capacity = capacity;
        this.setHasSubtypes(true);
    }

    @Override
    public ItemStack getItemStack(FluidName type) {
        ItemStack ret = new ItemStack((Item)this);
        if (type == null) {
            return ret;
        }
        if (this.fill(ret, new FluidStack(type.getInstance(), Integer.MAX_VALUE), true) > 0) {
            return ret;
        }
        return null;
    }

    @Override
    public ItemStack getItemStack(String variant) {
        if (variant == null || variant.isEmpty()) {
            return new ItemStack((Item)this);
        }
        Fluid fluid = FluidRegistry.getFluid((String)variant);
        if (fluid == null) {
            return null;
        }
        ItemStack ret = new ItemStack((Item)this);
        if (this.fill(ret, new FluidStack(fluid, Integer.MAX_VALUE), true) > 0) {
            return ret;
        }
        return null;
    }

    @Override
    public String getVariant(ItemStack stack) {
        if (stack == null) {
            throw new NullPointerException("null stack");
        }
        if (stack.getItem() != this) {
            throw new IllegalArgumentException("The stack " + (Object)stack + " doesn't match " + this);
        }
        FluidStack fs = this.getFluid(stack);
        if (fs == null || fs.getFluid() == null) {
            return null;
        }
        return fs.getFluid().getName();
    }

    public boolean hasContainerItem(ItemStack stack) {
        return this.getFluid(stack) != null;
    }

    public ItemStack getContainerItem(ItemStack stack) {
        if (!this.hasContainerItem(stack)) {
            return super.getContainerItem(stack);
        }
        ItemStack ret = StackUtil.copyWithSize(stack, 1);
        this.drain(ret, Integer.MAX_VALUE, true);
        return ret;
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
            info.add("< " + FluidRegistry.getFluidName((FluidStack)fs) + ", " + fs.amount + " mB >");
        } else {
            info.add(Localization.translate("ic2.item.FluidContainer.Empty"));
        }
        return info;
    }

    public FluidStack getFluid(ItemStack stack) {
        NBTTagCompound nbtTagCompound = StackUtil.getOrCreateNbtData(stack);
        NBTTagCompound fluidTag = nbtTagCompound.getCompoundTag("fluid");
        return FluidStack.loadFluidStackFromNBT((NBTTagCompound)fluidTag);
    }

    public boolean isEmpty(ItemStack stack) {
        return this.getFluid(stack) == null;
    }

    public int getCapacity(ItemStack container) {
        return this.capacity;
    }

    public abstract boolean canfill(Fluid var1);

    public int fill(ItemStack stack, FluidStack resource, boolean doFill) {
        if (stack.stackSize != 1) {
            return 0;
        }
        if (resource == null) {
            return 0;
        }
        if (!this.canfill(resource.getFluid())) {
            return 0;
        }
        NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
        NBTTagCompound fluidTag = nbt.getCompoundTag("fluid");
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
            nbt.setTag("fluid", (NBTBase)fluidTag);
        }
        return amount;
    }

    public FluidStack drain(ItemStack stack, int maxDrain, boolean doDrain) {
        if (stack.stackSize != 1) {
            return null;
        }
        NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
        NBTTagCompound fluidTag = nbt.getCompoundTag("fluid");
        FluidStack fs = FluidStack.loadFluidStackFromNBT((NBTTagCompound)fluidTag);
        if (fs == null) {
            return null;
        }
        maxDrain = Math.min(fs.amount, maxDrain);
        if (doDrain) {
            fs.amount -= maxDrain;
            if (fs.amount <= 0) {
                nbt.removeTag("fluid");
            } else {
                fs.writeToNBT(fluidTag);
                nbt.setTag("fluid", (NBTBase)fluidTag);
            }
        }
        return new FluidStack(fs, maxDrain);
    }
}

