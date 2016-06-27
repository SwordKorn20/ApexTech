/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
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
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.Energy;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumable;
import ic2.core.block.invslot.InvSlotConsumableItemStack;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotConsumableLiquidByTank;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.machine.container.ContainerCropmatron;
import ic2.core.block.machine.gui.GuiCropmatron;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import ic2.core.block.state.IIdProvider;
import ic2.core.crop.TileEntityCrop;
import ic2.core.item.type.CropResItemType;
import ic2.core.ref.FluidName;
import ic2.core.ref.ItemName;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
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

public class TileEntityCropmatron
extends TileEntityElectricMachine
implements IHasGui,
IFluidHandler {
    public int scanX = -5;
    public int scanY = -1;
    public int scanZ = -5;
    public final InvSlotConsumable fertilizerSlot;
    public final InvSlotOutput wasseroutputSlot;
    public final InvSlotOutput exOutputSlot;
    public final InvSlotConsumableLiquidByTank wasserinputSlot;
    public final InvSlotConsumableLiquidByTank exInputSlot;
    protected final FluidTank waterTank = new FluidTank(2000);
    protected final FluidTank exTank = new FluidTank(2000);

    public TileEntityCropmatron() {
        super(10000, 1);
        this.fertilizerSlot = new InvSlotConsumableItemStack((TileEntityInventory)this, "fertilizer", 7, ItemName.crop_res.getItemStack(CropResItemType.fertilizer));
        this.wasserinputSlot = new InvSlotConsumableLiquidByTank(this, "wasserinputSlot", InvSlot.Access.I, 1, InvSlot.InvSide.TOP, InvSlotConsumableLiquid.OpType.Drain, (IFluidTank)this.waterTank);
        this.exInputSlot = new InvSlotConsumableLiquidByTank(this, "exInputSlot", InvSlot.Access.I, 1, InvSlot.InvSide.TOP, InvSlotConsumableLiquid.OpType.Drain, (IFluidTank)this.exTank);
        this.wasseroutputSlot = new InvSlotOutput(this, "wasseroutputSlot", 1);
        this.exOutputSlot = new InvSlotOutput(this, "exOutputSlot", 1);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.waterTank.readFromNBT(nbttagcompound.getCompoundTag("waterTank"));
        this.exTank.readFromNBT(nbttagcompound.getCompoundTag("exTank"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        NBTTagCompound waterTankTag = new NBTTagCompound();
        this.waterTank.writeToNBT(waterTankTag);
        nbt.setTag("waterTank", (NBTBase)waterTankTag);
        NBTTagCompound exTankTag = new NBTTagCompound();
        this.exTank.writeToNBT(exTankTag);
        nbt.setTag("exTank", (NBTBase)exTankTag);
        return nbt;
    }

    @Override
    protected void updateEntityServer() {
        List<ItemStack> processResult;
        super.updateEntityServer();
        RecipeOutput outputinputSlot = this.processWaterInputSlot(true);
        if (outputinputSlot != null) {
            this.processWaterInputSlot(false);
            processResult = outputinputSlot.items;
            this.wasseroutputSlot.add(processResult);
        }
        if ((outputinputSlot = this.processExInputSlot(true)) != null) {
            this.processExInputSlot(false);
            processResult = outputinputSlot.items;
            this.exOutputSlot.add(processResult);
        }
        this.fertilizerSlot.organize();
        if (this.energy.getEnergy() >= 31.0) {
            this.scan();
        }
    }

    public void scan() {
        ++this.scanX;
        if (this.scanX > 5) {
            this.scanX = -5;
            ++this.scanZ;
            if (this.scanZ > 5) {
                this.scanZ = -5;
                ++this.scanY;
                if (this.scanY > 1) {
                    this.scanY = -1;
                }
            }
        }
        this.energy.useEnergy(1.0);
        TileEntity te = this.worldObj.getTileEntity(this.pos.add(this.scanX, this.scanY, this.scanZ));
        if (te instanceof TileEntityCrop) {
            TileEntityCrop crop = (TileEntityCrop)te;
            if (!this.fertilizerSlot.isEmpty() && this.fertilizerSlot.consume(1, true, false) != null && crop.applyFertilizer(false)) {
                this.energy.useEnergy(10.0);
                this.fertilizerSlot.consume(1);
            }
            if (this.waterTank.getFluidAmount() > 0 && crop.applyWater(this.getWaterTank())) {
                this.energy.useEnergy(10.0);
            }
            if (this.exTank.getFluidAmount() > 0 && crop.applyWeedEX(this.getExTank())) {
                this.energy.useEnergy(10.0);
            }
        }
    }

    private RecipeOutput processWaterInputSlot(boolean simulate) {
        MutableObject output;
        if (!this.wasserinputSlot.isEmpty() && this.wasserinputSlot.transferToTank((IFluidTank)this.waterTank, output = new MutableObject(), simulate) && (output.getValue() == null || this.wasseroutputSlot.canAdd((ItemStack)output.getValue()))) {
            if (output.getValue() == null) {
                return new RecipeOutput(null, new ItemStack[0]);
            }
            return new RecipeOutput(null, (ItemStack)output.getValue());
        }
        return null;
    }

    private RecipeOutput processExInputSlot(boolean simulate) {
        MutableObject output;
        if (!this.exInputSlot.isEmpty() && this.exInputSlot.transferToTank((IFluidTank)this.exTank, output = new MutableObject(), simulate) && (output.getValue() == null || this.exOutputSlot.canAdd((ItemStack)output.getValue()))) {
            if (output.getValue() == null) {
                return new RecipeOutput(null, new ItemStack[0]);
            }
            return new RecipeOutput(null, (ItemStack)output.getValue());
        }
        return null;
    }

    public ContainerBase<TileEntityCropmatron> getGuiContainer(EntityPlayer player) {
        return new ContainerCropmatron(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiCropmatron(new ContainerCropmatron(player, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }

    public boolean canDrain(EnumFacing from, Fluid fluid) {
        return false;
    }

    public FluidTankInfo[] getTankInfo(EnumFacing from) {
        return new FluidTankInfo[]{this.waterTank.getInfo(), this.exTank.getInfo()};
    }

    public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
        if (resource.getFluid() == FluidRegistry.WATER) {
            this.getWaterTank().fill(resource, doFill);
        }
        if (resource.getFluid() == FluidName.weed_ex.getInstance()) {
            this.getExTank().fill(resource, doFill);
        }
        return 0;
    }

    public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
        return null;
    }

    public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
        return null;
    }

    public boolean canFill(EnumFacing from, Fluid fluid) {
        return false;
    }

    public FluidTank getWaterTank() {
        return this.waterTank;
    }

    public FluidTank getExTank() {
        return this.exTank;
    }
}

