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
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraft.world.biome.Biome
 *  net.minecraftforge.common.BiomeDictionary
 *  net.minecraftforge.common.BiomeDictionary$Type
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidRegistry
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
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.generator.tileentity.TileEntitySolarGenerator;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotConsumableLiquidByList;
import ic2.core.block.invslot.InvSlotConsumableLiquidByTank;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.container.ContainerSolarDestiller;
import ic2.core.block.machine.gui.GuiSolarDestiller;
import ic2.core.ref.FluidName;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.IUpgradeItem;
import ic2.core.upgrade.UpgradableProperty;
import ic2.core.util.BiomeUtil;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.mutable.MutableObject;

public class TileEntitySolarDestiller
extends TileEntityInventory
implements IHasGui,
IFluidHandler,
IUpgradableBlock {
    public final FluidTank inputTank = new FluidTank(10000);
    public final FluidTank outputTank = new FluidTank(10000);
    private int tickrate;
    private int updateTicker;
    private float skyLight;
    public final InvSlotOutput wateroutputSlot;
    public final InvSlotOutput destiwateroutputSlott;
    public final InvSlotConsumableLiquidByList waterinputSlot;
    public final InvSlotConsumableLiquidByTank destiwaterinputSlot;
    public final InvSlotUpgrade upgradeSlot;

    public TileEntitySolarDestiller() {
        this.waterinputSlot = new InvSlotConsumableLiquidByList((TileEntityInventory)this, "waterInput", InvSlot.Access.I, 1, InvSlot.InvSide.TOP, InvSlotConsumableLiquid.OpType.Drain, FluidRegistry.WATER);
        this.destiwaterinputSlot = new InvSlotConsumableLiquidByTank(this, "destilledWaterInput", InvSlot.Access.I, 1, InvSlot.InvSide.BOTTOM, InvSlotConsumableLiquid.OpType.Fill, (IFluidTank)this.outputTank);
        this.wateroutputSlot = new InvSlotOutput(this, "waterOutput", 1);
        this.destiwateroutputSlott = new InvSlotOutput(this, "destilledWaterOutput", 1);
        this.upgradeSlot = new InvSlotUpgrade(this, "upgrade", 3);
    }

    @Override
    protected void onLoaded() {
        super.onLoaded();
        this.tickrate = this.getTickRate();
        this.updateTicker = IC2.random.nextInt(this.tickrate);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.inputTank.readFromNBT(nbt.getCompoundTag("inputTank"));
        this.outputTank.readFromNBT(nbt.getCompoundTag("outputTank"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        NBTTagCompound inputTankTag = new NBTTagCompound();
        this.inputTank.writeToNBT(inputTankTag);
        nbt.setTag("inputTank", (NBTBase)inputTankTag);
        NBTTagCompound outputTankTag = new NBTTagCompound();
        this.outputTank.writeToNBT(outputTankTag);
        nbt.setTag("outputTank", (NBTBase)outputTankTag);
        return nbt;
    }

    @Override
    protected void updateEntityServer() {
        RecipeOutput outputoutputSlot;
        super.updateEntityServer();
        RecipeOutput outputinputSlot = this.processInputSlot(true);
        if (outputinputSlot != null) {
            this.processInputSlot(false);
            List<ItemStack> processResult = outputinputSlot.items;
            this.wateroutputSlot.add(processResult);
        }
        if (++this.updateTicker >= this.tickrate) {
            this.updateSunVisibility();
            if (this.canWork()) {
                this.inputTank.drain(1, true);
                this.outputTank.fill(new FluidStack(FluidName.distilled_water.getInstance(), 1), true);
            }
            this.updateTicker = 0;
        }
        if ((outputoutputSlot = this.processOutputSlot(true)) != null) {
            this.processOutputSlot(false);
            List<ItemStack> processResult = outputoutputSlot.items;
            this.destiwateroutputSlott.add(processResult);
        }
        for (int i = 0; i < this.upgradeSlot.size(); ++i) {
            ItemStack stack = this.upgradeSlot.get(i);
            if (stack == null || !(stack.getItem() instanceof IUpgradeItem) || !((IUpgradeItem)stack.getItem()).onTick(stack, this)) continue;
            super.markDirty();
        }
    }

    private RecipeOutput processInputSlot(boolean simulate) {
        MutableObject output;
        if (!this.waterinputSlot.isEmpty() && this.waterinputSlot.transferToTank((IFluidTank)this.inputTank, output = new MutableObject(), simulate) && (output.getValue() == null || this.wateroutputSlot.canAdd((ItemStack)output.getValue()))) {
            if (output.getValue() == null) {
                return new RecipeOutput(null, new ItemStack[0]);
            }
            return new RecipeOutput(null, (ItemStack)output.getValue());
        }
        return null;
    }

    private RecipeOutput processOutputSlot(boolean simulate) {
        MutableObject output;
        if (!this.destiwaterinputSlot.isEmpty() && this.destiwaterinputSlot.transferFromTank((IFluidTank)this.outputTank, output = new MutableObject(), simulate) && (output.getValue() == null || this.destiwateroutputSlott.canAdd((ItemStack)output.getValue()))) {
            if (output.getValue() == null) {
                return new RecipeOutput(null, new ItemStack[0]);
            }
            return new RecipeOutput(null, (ItemStack)output.getValue());
        }
        return null;
    }

    public void updateSunVisibility() {
        this.skyLight = TileEntitySolarGenerator.getSkyLight(this.worldObj, this.pos.up());
    }

    public ContainerBase<TileEntitySolarDestiller> getGuiContainer(EntityPlayer player) {
        return new ContainerSolarDestiller(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiSolarDestiller(new ContainerSolarDestiller(player, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }

    public int getTickRate() {
        Biome biome = BiomeUtil.getBiome(this.worldObj, this.pos);
        if (BiomeDictionary.isBiomeOfType((Biome)biome, (BiomeDictionary.Type)BiomeDictionary.Type.HOT)) {
            return 36;
        }
        if (BiomeDictionary.isBiomeOfType((Biome)biome, (BiomeDictionary.Type)BiomeDictionary.Type.COLD)) {
            return 144;
        }
        return 72;
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

    public FluidTankInfo[] getTankInfo(EnumFacing from) {
        return new FluidTankInfo[]{this.inputTank.getInfo(), this.outputTank.getInfo()};
    }

    public boolean canFill(EnumFacing from, Fluid fluid) {
        return fluid == FluidRegistry.WATER;
    }

    public boolean canDrain(EnumFacing from, Fluid fluid) {
        FluidStack fluidStack = this.outputTank.getFluid();
        if (fluidStack == null) {
            return false;
        }
        return fluidStack.isFluidEqual(new FluidStack(fluid, 1));
    }

    public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
        if (!this.canFill(from, resource.getFluid())) {
            return 0;
        }
        return this.inputTank.fill(resource, doFill);
    }

    public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
        if (resource == null || !resource.isFluidEqual(this.outputTank.getFluid())) {
            return null;
        }
        if (!this.canDrain(from, resource.getFluid())) {
            return null;
        }
        return this.outputTank.drain(resource.amount, doDrain);
    }

    public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
        return this.outputTank.drain(maxDrain, doDrain);
    }

    public boolean canWork() {
        return this.inputTank.getFluidAmount() > 0 && this.outputTank.getFluidAmount() < this.outputTank.getCapacity() && (double)this.skyLight > 0.5;
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.ItemConsuming, UpgradableProperty.ItemProducing, UpgradableProperty.FluidProducing);
    }

    @Override
    public double getEnergy() {
        return 40.0;
    }

    @Override
    public boolean useEnergy(double amount) {
        return true;
    }
}

