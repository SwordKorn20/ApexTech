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
package ic2.core.block.machine.tileentity;

import ic2.api.energy.tile.IHeatSource;
import ic2.api.recipe.IFermenterRecipeManager;
import ic2.api.recipe.RecipeOutput;
import ic2.api.recipe.Recipes;
import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotConsumableLiquidByList;
import ic2.core.block.invslot.InvSlotConsumableLiquidByTank;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.container.ContainerFermenter;
import ic2.core.block.machine.gui.GuiFermenter;
import ic2.core.block.state.IIdProvider;
import ic2.core.gui.dynamic.IGuiValueProvider;
import ic2.core.init.MainConfig;
import ic2.core.item.type.CropResItemType;
import ic2.core.recipe.FermenterRecipeManager;
import ic2.core.ref.FluidName;
import ic2.core.ref.ItemName;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.IUpgradeItem;
import ic2.core.upgrade.UpgradableProperty;
import ic2.core.util.ConfigUtil;
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

public class TileEntityFermenter
extends TileEntityInventory
implements IHasGui,
IGuiValueProvider,
IFluidHandler,
IUpgradableBlock {
    private final FluidTank inputTank = new FluidTank(10000);
    private final FluidTank outputTank = new FluidTank(2000);
    public final InvSlotConsumableLiquidByList fluidInputCellInSlot;
    public final InvSlotConsumableLiquidByTank fluidOutputCellInSlot;
    public final InvSlotOutput fluidInputCellOutSlot;
    public final InvSlotOutput fluidOutputCellOutSlot;
    public final InvSlotOutput fertiliserSlot;
    public final InvSlotUpgrade upgradeSlot;
    private int heatBuffer;
    public int progress;
    private final int maxProgress;
    private boolean newActive;

    public TileEntityFermenter() {
        this.fluidOutputCellInSlot = new InvSlotConsumableLiquidByTank(this, "biogasInput", InvSlot.Access.I, 1, InvSlot.InvSide.BOTTOM, InvSlotConsumableLiquid.OpType.Fill, (IFluidTank)this.outputTank);
        this.fluidInputCellOutSlot = new InvSlotOutput(this, "biomassOutput", 1);
        this.fluidOutputCellOutSlot = new InvSlotOutput(this, "biogassOutput", 1);
        this.fertiliserSlot = new InvSlotOutput(this, "output", 1);
        this.upgradeSlot = new InvSlotUpgrade(this, "upgrade", 2);
        this.heatBuffer = 0;
        this.progress = 0;
        this.maxProgress = ConfigUtil.getInt(MainConfig.get(), "balance/fermenter/biomass_per_fertilizier");
        this.newActive = false;
        Set<Fluid> validInputs = Recipes.fermenter.getAcceptedFluids();
        this.fluidInputCellInSlot = new InvSlotConsumableLiquidByList((TileEntityInventory)this, "biomassInput", InvSlot.Access.I, 1, InvSlot.InvSide.TOP, InvSlotConsumableLiquid.OpType.Drain, validInputs.toArray((T[])new Fluid[validInputs.size()]));
    }

    public static void init() {
        Recipes.fermenter = new FermenterRecipeManager();
        Recipes.fermenter.addRecipe(FluidName.biomass.getName(), ConfigUtil.getInt(MainConfig.get(), "balance/fermenter/need_amount_biomass_per_run"), ConfigUtil.getInt(MainConfig.get(), "balance/fermenter/hU_per_run"), FluidName.biogas.getName(), ConfigUtil.getInt(MainConfig.get(), "balance/fermenter/output_amount_biogas_per_run"));
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.inputTank.readFromNBT(nbttagcompound.getCompoundTag("inputTank"));
        this.outputTank.readFromNBT(nbttagcompound.getCompoundTag("outputTank"));
        this.progress = nbttagcompound.getInteger("progress");
        this.heatBuffer = nbttagcompound.getInteger("heatBuffer");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setTag("inputTank", (NBTBase)this.inputTank.writeToNBT(new NBTTagCompound()));
        nbt.setTag("outputTank", (NBTBase)this.outputTank.writeToNBT(new NBTTagCompound()));
        nbt.setInteger("progress", this.progress);
        nbt.setInteger("heatBuffer", this.heatBuffer);
        return nbt;
    }

    private RecipeOutput processInputSlot(boolean simulate) {
        MutableObject output;
        if (!this.fluidInputCellInSlot.isEmpty() && this.fluidInputCellInSlot.transferToTank((IFluidTank)this.inputTank, output = new MutableObject(), simulate) && (output.getValue() == null || this.fluidInputCellOutSlot.canAdd((ItemStack)output.getValue()))) {
            if (output.getValue() == null) {
                return new RecipeOutput(null, new ItemStack[0]);
            }
            return new RecipeOutput(null, (ItemStack)output.getValue());
        }
        return null;
    }

    private RecipeOutput processOutputSlot(boolean simulate) {
        MutableObject output;
        if (!this.fluidOutputCellInSlot.isEmpty() && this.fluidOutputCellInSlot.transferFromTank((IFluidTank)this.outputTank, output = new MutableObject(), simulate) && (output.getValue() == null || this.fluidOutputCellOutSlot.canAdd((ItemStack)output.getValue()))) {
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
            this.fluidInputCellOutSlot.add(processResult);
        }
        if ((outputoutputSlot = this.processOutputSlot(true)) != null) {
            this.processOutputSlot(false);
            List<ItemStack> processResult = outputoutputSlot.items;
            this.fluidOutputCellOutSlot.add(processResult);
        }
        this.newActive = this.work();
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

    private boolean work() {
        EnumFacing dir;
        IFermenterRecipeManager.FermentationProperty fp;
        TileEntity te;
        if (this.progress >= this.maxProgress) {
            this.fertiliserSlot.add(ItemName.crop_res.getItemStack(CropResItemType.fertilizer));
            this.progress = 0;
        }
        if ((te = this.worldObj.getTileEntity(this.pos.offset(dir = this.getFacing()))) instanceof IHeatSource && this.inputTank.getFluid() != null && (fp = Recipes.fermenter.getFermentationInformation(this.inputTank.getFluid().getFluid())) != null && this.inputTank.getFluidAmount() >= fp.inputAmount && fp.outputAmount <= this.outputTank.getCapacity() - this.outputTank.getFluidAmount()) {
            this.heatBuffer += ((IHeatSource)te).requestHeat(dir.getOpposite(), 100);
            if (this.heatBuffer >= fp.heat) {
                this.heatBuffer = 0;
                this.inputTank.drain(fp.inputAmount, true);
                this.outputTank.fill(fp.getOutput(), true);
                this.progress += fp.inputAmount;
            }
            return true;
        }
        return false;
    }

    public ContainerBase<TileEntityFermenter> getGuiContainer(EntityPlayer player) {
        return new ContainerFermenter(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiFermenter(new ContainerFermenter(player, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }

    @Override
    public double getGuiValue(String name) {
        if ("heat".equals(name)) {
            IFermenterRecipeManager.FermentationProperty fp;
            if (this.heatBuffer == 0) {
                return 0.0;
            }
            double maxHeatBuff = ConfigUtil.getInt(MainConfig.get(), "balance/fermenter/hU_per_run");
            if (this.inputTank.getFluid() != null && (fp = Recipes.fermenter.getFermentationInformation(this.inputTank.getFluid().getFluid())) != null) {
                maxHeatBuff = fp.heat;
            }
            return (double)this.heatBuffer / maxHeatBuff;
        }
        if ("progress".equals(name)) {
            return this.progress == 0 ? 0.0 : (double)this.progress / (double)this.maxProgress;
        }
        throw new IllegalArgumentException("Invalid GUI value: " + name);
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
        if (Recipes.fermenter.acceptsFluid(fluid)) {
            return this.inputTank.getFluid() == null || this.inputTank.getFluid().getFluid() == fluid && this.inputTank.getFluidAmount() < this.inputTank.getCapacity();
        }
        return false;
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
    public double getEnergy() {
        return 40.0;
    }

    @Override
    public boolean useEnergy(double amount) {
        return true;
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.ItemConsuming, UpgradableProperty.ItemProducing, UpgradableProperty.FluidConsuming, UpgradableProperty.FluidProducing);
    }
}

