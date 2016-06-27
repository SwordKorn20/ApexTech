/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.EnumFacing
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.FluidTank
 *  net.minecraftforge.fluids.FluidTankInfo
 *  net.minecraftforge.fluids.IFluidHandler
 *  net.minecraftforge.fluids.IFluidTank
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.block.machine.tileentity;

import ic2.api.recipe.RecipeOutput;
import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.Energy;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableId;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotConsumableLiquidByTank;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.container.ContainerCondenser;
import ic2.core.block.machine.gui.GuiCondenser;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import ic2.core.gui.dynamic.IGuiValueProvider;
import ic2.core.ref.FluidName;
import ic2.core.ref.IItemModelProvider;
import ic2.core.ref.ItemName;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.IUpgradeItem;
import ic2.core.upgrade.UpgradableProperty;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.mutable.MutableObject;

public class TileEntityCondenser
extends TileEntityElectricMachine
implements IHasGui,
IGuiValueProvider,
IFluidHandler,
IUpgradableBlock {
    private final short passiveCooling = 100;
    private final short coolingPerVent = 100;
    public final short ventEUCost = 2;
    public int progress = 0;
    public final int maxProgress = 10000;
    private boolean newActive = false;
    private final FluidTank inputTank = new FluidTank(100000);
    private final FluidTank outputTank = new FluidTank(1000);
    public final InvSlotConsumableLiquidByTank waterInputSlot;
    public final InvSlotOutput waterOutputSlot;
    public final InvSlotConsumableId ventSlots;
    public final InvSlotUpgrade upgradeSlot;

    public TileEntityCondenser() {
        super(100000, 3);
        this.waterInputSlot = new InvSlotConsumableLiquidByTank(this, "waterInputSlot", InvSlot.Access.I, 1, InvSlot.InvSide.BOTTOM, InvSlotConsumableLiquid.OpType.Fill, (IFluidTank)this.outputTank);
        this.waterOutputSlot = new InvSlotOutput(this, "waterOutputSlot", 1);
        this.ventSlots = new InvSlotConsumableId((TileEntityInventory)this, "ventSlots", 4, new Item[]{ItemName.heat_vent.getInstance()});
        this.upgradeSlot = new InvSlotUpgrade(this, "upgradeSlot", 1);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.inputTank.readFromNBT(nbttagcompound.getCompoundTag("inputTank"));
        this.outputTank.readFromNBT(nbttagcompound.getCompoundTag("outputTank"));
        this.progress = nbttagcompound.getInteger("progress");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setTag("inputTank", (NBTBase)this.inputTank.writeToNBT(new NBTTagCompound()));
        nbt.setTag("outputTank", (NBTBase)this.outputTank.writeToNBT(new NBTTagCompound()));
        nbt.setInteger("progress", this.progress);
        return nbt;
    }

    public byte getVents() {
        byte vents = 0;
        for (int slot = 0; slot < this.ventSlots.size(); ++slot) {
            if (this.ventSlots.get(slot) == null) continue;
            vents = (byte)(vents + 1);
        }
        return vents;
    }

    private RecipeOutput processOutputSlot(boolean simulate) {
        MutableObject output;
        if (!this.waterInputSlot.isEmpty() && this.waterInputSlot.transferFromTank((IFluidTank)this.outputTank, output = new MutableObject(), simulate) && (output.getValue() == null || this.waterOutputSlot.canAdd((ItemStack)output.getValue()))) {
            if (output.getValue() == null) {
                return new RecipeOutput(null, new ItemStack[0]);
            }
            return new RecipeOutput(null, (ItemStack)output.getValue());
        }
        return null;
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        RecipeOutput outputoutputSlot = this.processOutputSlot(true);
        if (outputoutputSlot != null) {
            this.processOutputSlot(false);
            List<ItemStack> processResult = outputoutputSlot.items;
            this.waterOutputSlot.add(processResult);
        }
        this.newActive = this.inputTank.getFluidAmount() > 0;
        this.work();
        if (this.getActive() != this.newActive) {
            this.setActive(this.newActive);
        }
        boolean dirty = false;
        for (int slot = 0; slot < this.upgradeSlot.size(); ++slot) {
            ItemStack stack = this.upgradeSlot.get(slot);
            if (stack == null || !(stack.getItem() instanceof IUpgradeItem)) continue;
            dirty = ((IUpgradeItem)stack.getItem()).onTick(stack, this) || dirty;
        }
        if (dirty) {
            super.markDirty();
        }
    }

    private void work() {
        if (this.inputTank.getFluidAmount() > 0 && this.outputTank.getCapacity() - this.outputTank.getFluidAmount() >= 100) {
            if (this.progress >= 10000) {
                this.outputTank.fill(new FluidStack(FluidName.distilled_water.getInstance(), 100), true);
                this.progress = 0;
            } else {
                byte vents = this.getVents();
                int drain = 100 + vents * 100;
                if (this.energy.useEnergy(vents * 2)) {
                    this.progress += this.inputTank.drain((int)drain, (boolean)true).amount;
                }
            }
        }
    }

    public ContainerBase<TileEntityCondenser> getGuiContainer(EntityPlayer player) {
        return new ContainerCondenser(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiCondenser(new ContainerCondenser(player, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }

    @Override
    public double getGuiValue(String name) {
        if ("progress".equals(name)) {
            return this.progress == 0 ? 0.0 : (double)this.progress / 10000.0;
        }
        throw new IllegalArgumentException("Invalid Gui value: " + name);
    }

    public int gaugeLiquidScaled(int i, int tank) {
        switch (tank) {
            case 0: {
                if (this.inputTank.getFluidAmount() <= 0) {
                    return 0;
                }
                return this.inputTank.getFluidAmount() * i / this.inputTank.getCapacity();
            }
            case 1: {
                if (this.outputTank.getFluidAmount() <= 0) {
                    return 0;
                }
                return this.outputTank.getFluidAmount() * i / this.outputTank.getCapacity();
            }
        }
        return 0;
    }

    public FluidTank getInputTank() {
        return this.inputTank;
    }

    public FluidTank getOutputTank() {
        return this.outputTank;
    }

    public FluidTankInfo[] getTankInfo(EnumFacing from) {
        return new FluidTankInfo[]{this.inputTank.getInfo(), this.outputTank.getInfo()};
    }

    public boolean canFill(EnumFacing from, Fluid fluid) {
        return fluid == FluidName.steam.getInstance() || fluid == FluidName.superheated_steam.getInstance();
    }

    public boolean canDrain(EnumFacing from, Fluid fluid) {
        return new FluidStack(fluid, 1).isFluidEqual(this.outputTank.getFluid());
    }

    public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
        if (resource == null || !this.canFill(from, resource.getFluid())) {
            return 0;
        }
        return this.inputTank.fill(resource, doFill);
    }

    public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
        if (resource == null || !resource.isFluidEqual(this.outputTank.getFluid()) || !this.canDrain(from, resource.getFluid())) {
            return null;
        }
        return this.outputTank.drain(resource.amount, doDrain);
    }

    public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
        return this.outputTank.drain(maxDrain, doDrain);
    }

    @Override
    public double getEnergy() {
        return 0.0;
    }

    @Override
    public boolean useEnergy(double amount) {
        return false;
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.ItemConsuming, UpgradableProperty.ItemProducing, UpgradableProperty.FluidConsuming, UpgradableProperty.FluidProducing);
    }
}

