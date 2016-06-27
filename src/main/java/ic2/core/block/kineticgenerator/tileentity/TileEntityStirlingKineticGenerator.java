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
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
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
package ic2.core.block.kineticgenerator.tileentity;

import ic2.api.energy.tile.IHeatSource;
import ic2.api.energy.tile.IKineticSource;
import ic2.api.recipe.ILiquidAcceptManager;
import ic2.api.recipe.ILiquidHeatExchangerManager;
import ic2.api.recipe.RecipeOutput;
import ic2.api.recipe.Recipes;
import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotConsumableLiquidByManager;
import ic2.core.block.invslot.InvSlotConsumableLiquidByTank;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.kineticgenerator.container.ContainerStirlingKineticGenerator;
import ic2.core.block.kineticgenerator.gui.GuiStirlingKineticGenerator;
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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.mutable.MutableObject;

public class TileEntityStirlingKineticGenerator
extends TileEntityInventory
implements IKineticSource,
IUpgradableBlock,
IFluidHandler,
IHasGui {
    public FluidTank inputTank = new FluidTank(2000);
    public FluidTank outputTank = new FluidTank(2000);
    public InvSlotOutput hotoutputSlot;
    public InvSlotOutput cooloutputSlot;
    public InvSlotConsumableLiquidByTank hotfluidinputSlot;
    public InvSlotConsumableLiquidByManager coolfluidinputSlot;
    public InvSlotUpgrade upgradeSlot;
    private int heatbuffer = 0;
    private final int maxHeatbuffer;
    private int kUBuffer;
    private final int maxkUBuffer;
    private boolean newActive;
    private int liquidHeatStored;
    private static final int PARTS_KU = 3;
    private static final int PARTS_LIQUID = 1;
    private static final int PARTS_TOTAL = 4;

    public TileEntityStirlingKineticGenerator() {
        this.hotoutputSlot = new InvSlotOutput(this, "outputSlot", 1);
        this.cooloutputSlot = new InvSlotOutput(this, "outputSlot", 1);
        this.coolfluidinputSlot = new InvSlotConsumableLiquidByManager(this, "coolfluidinputSlot", InvSlot.Access.I, 1, InvSlot.InvSide.TOP, InvSlotConsumableLiquid.OpType.Drain, Recipes.liquidHeatupManager.getSingleDirectionLiquidManager());
        this.hotfluidinputSlot = new InvSlotConsumableLiquidByTank(this, "hotfluidoutputSlot", InvSlot.Access.I, 1, InvSlot.InvSide.BOTTOM, InvSlotConsumableLiquid.OpType.Fill, (IFluidTank)this.outputTank);
        this.upgradeSlot = new InvSlotUpgrade(this, "upgrade", 3);
        this.maxHeatbuffer = 1000;
        this.maxkUBuffer = 2000;
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.inputTank.readFromNBT(nbt.getCompoundTag("inputTank"));
        this.outputTank.readFromNBT(nbt.getCompoundTag("outputTank"));
        this.heatbuffer = nbt.getInteger("heatbuffer");
        this.kUBuffer = nbt.getInteger("kubuffer");
        this.liquidHeatStored = nbt.getInteger("liquidHeatStored");
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
        nbt.setInteger("heatbuffer", this.heatbuffer);
        nbt.setInteger("kUBuffer", this.kUBuffer);
        nbt.setInteger("liquidHeatStored", this.liquidHeatStored);
        return nbt;
    }

    private RecipeOutput processInputSlot(boolean simulate) {
        MutableObject output;
        if (!this.coolfluidinputSlot.isEmpty() && this.coolfluidinputSlot.transferToTank((IFluidTank)this.inputTank, output = new MutableObject(), simulate) && (output.getValue() == null || this.cooloutputSlot.canAdd((ItemStack)output.getValue()))) {
            if (output.getValue() == null) {
                return new RecipeOutput(null, new ItemStack[0]);
            }
            return new RecipeOutput(null, (ItemStack)output.getValue());
        }
        return null;
    }

    private RecipeOutput processOutputSlot(boolean simulate) {
        MutableObject output;
        if (!this.hotfluidinputSlot.isEmpty() && this.hotfluidinputSlot.transferFromTank((IFluidTank)this.outputTank, output = new MutableObject(), simulate) && (output.getValue() == null || this.hotoutputSlot.canAdd((ItemStack)output.getValue()))) {
            if (output.getValue() == null) {
                return new RecipeOutput(null, new ItemStack[0]);
            }
            return new RecipeOutput(null, (ItemStack)output.getValue());
        }
        return null;
    }

    @Override
    protected void updateEntityServer() {
        RecipeOutput outputoutputSlot;
        super.updateEntityServer();
        RecipeOutput outputinputSlot = this.processInputSlot(true);
        if (outputinputSlot != null) {
            this.processInputSlot(false);
            List<ItemStack> processResult = outputinputSlot.items;
            this.cooloutputSlot.add(processResult);
        }
        if ((outputoutputSlot = this.processOutputSlot(true)) != null) {
            this.processOutputSlot(false);
            List<ItemStack> processResult = outputoutputSlot.items;
            this.hotoutputSlot.add(processResult);
        }
        if (this.heatbuffer < this.maxHeatbuffer) {
            this.heatbuffer += this.drawHu(this.maxHeatbuffer - this.heatbuffer);
        }
        this.newActive = false;
        if (this.inputTank.getFluidAmount() > 0 && this.outputTank.getFluidAmount() < this.outputTank.getCapacity() && Recipes.liquidHeatupManager.getSingleDirectionLiquidManager().acceptsFluid(this.inputTank.getFluid().getFluid()) && this.kUBuffer < this.maxkUBuffer) {
            ILiquidHeatExchangerManager.HeatExchangeProperty property = Recipes.liquidHeatupManager.getHeatExchangeProperty(this.inputTank.getFluid().getFluid());
            if (this.outputTank.getFluid() == null || new FluidStack(property.outputFluid, 0).isFluidEqual(this.outputTank.getFluid())) {
                int heatbufferToUse = this.heatbuffer / 4;
                heatbufferToUse = Math.min(heatbufferToUse, (Math.min(this.outputTank.getCapacity() - this.outputTank.getFluidAmount(), this.inputTank.getFluidAmount()) * property.huPerMB - this.liquidHeatStored) / 1);
                if ((heatbufferToUse = Math.min(heatbufferToUse, (this.maxkUBuffer - this.kUBuffer) / 3)) > 0) {
                    this.kUBuffer += heatbufferToUse * 3 * 4;
                    this.liquidHeatStored += heatbufferToUse * 1;
                    this.heatbuffer -= heatbufferToUse * 4;
                    this.newActive = true;
                }
                if (this.liquidHeatStored >= property.huPerMB) {
                    int mbToConvert = this.liquidHeatStored / property.huPerMB;
                    mbToConvert = this.inputTank.drain((int)mbToConvert, (boolean)false).amount;
                    mbToConvert = this.outputTank.fill(new FluidStack(property.outputFluid, mbToConvert), false);
                    this.liquidHeatStored -= mbToConvert * property.huPerMB;
                    this.inputTank.drain(mbToConvert, true);
                    this.outputTank.fill(new FluidStack(property.outputFluid, mbToConvert), true);
                }
            }
        }
        if (this.getActive() != this.newActive) {
            this.setActive(this.newActive);
        }
        for (int i = 0; i < this.upgradeSlot.size(); ++i) {
            ItemStack stack = this.upgradeSlot.get(i);
            if (stack == null || !(stack.getItem() instanceof IUpgradeItem) || !((IUpgradeItem)stack.getItem()).onTick(stack, this)) continue;
            super.markDirty();
        }
    }

    private int drawHu(int amount) {
        IHeatSource hs;
        EnumFacing dir;
        TileEntity te;
        int request;
        if (amount <= 0) {
            return 0;
        }
        int tmpAmount = amount;
        EnumFacing[] arrenumFacing = EnumFacing.VALUES;
        int n = arrenumFacing.length;
        for (int i = 0; !(i >= n || (dir = arrenumFacing[i]) != this.getFacing() && (te = this.worldObj.getTileEntity(this.pos.offset(dir))) != null && te instanceof IHeatSource && (request = Math.min((hs = (IHeatSource)te).maxrequestHeatTick(dir.getOpposite()), tmpAmount)) > 0 && (tmpAmount -= hs.requestHeat(dir.getOpposite(), request)) <= 0); ++i) {
        }
        return amount - tmpAmount;
    }

    @Override
    public int maxrequestkineticenergyTick(EnumFacing directionFrom) {
        if (directionFrom != this.getFacing()) {
            return 0;
        }
        return Math.min(this.kUBuffer, this.maxkUBuffer);
    }

    @Override
    public int requestkineticenergy(EnumFacing directionFrom, int requestkineticenergy) {
        if (directionFrom != this.getFacing()) {
            return 0;
        }
        if (requestkineticenergy > this.kUBuffer) {
            return 0;
        }
        this.kUBuffer -= requestkineticenergy;
        return requestkineticenergy;
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.ItemConsuming, UpgradableProperty.ItemProducing, UpgradableProperty.FluidConsuming, UpgradableProperty.FluidProducing);
    }

    @Override
    public double getEnergy() {
        return 40.0;
    }

    @Override
    public boolean useEnergy(double amount) {
        return true;
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
        return Recipes.liquidHeatupManager.getSingleDirectionLiquidManager().acceptsFluid(fluid);
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

    @Override
    public ContainerBase<?> getGuiContainer(EntityPlayer player) {
        return new ContainerStirlingKineticGenerator(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiStirlingKineticGenerator(new ContainerStirlingKineticGenerator(player, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }
}

