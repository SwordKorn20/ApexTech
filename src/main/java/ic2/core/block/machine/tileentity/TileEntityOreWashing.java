/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.EnumFacing
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidRegistry
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.FluidTank
 *  net.minecraftforge.fluids.IFluidTank
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.block.machine.tileentity;

import ic2.api.recipe.IMachineRecipeManager;
import ic2.api.recipe.RecipeOutput;
import ic2.api.recipe.Recipes;
import ic2.core.ContainerBase;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.TileEntityLiquidTankStandardMaschine;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotConsumableLiquidByList;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotProcessable;
import ic2.core.block.invslot.InvSlotProcessableGeneric;
import ic2.core.gui.dynamic.DynamicContainer;
import ic2.core.gui.dynamic.DynamicGui;
import ic2.core.gui.dynamic.GuiParser;
import ic2.core.recipe.BasicMachineRecipeManager;
import ic2.core.ref.TeBlock;
import ic2.core.upgrade.UpgradableProperty;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.mutable.MutableObject;

public class TileEntityOreWashing
extends TileEntityLiquidTankStandardMaschine {
    public final InvSlotConsumableLiquid fluidSlot;
    public final InvSlotOutput cellSlot;
    public static List<Map.Entry<ItemStack, ItemStack>> recipes = new Vector<Map.Entry<ItemStack, ItemStack>>();

    public TileEntityOreWashing() {
        super(16, 500, 3, 8);
        this.inputSlot = new InvSlotProcessableGeneric(this, "input", 1, Recipes.oreWashing);
        this.fluidSlot = new InvSlotConsumableLiquidByList((TileEntityInventory)this, "fluid", 1, FluidRegistry.WATER);
        this.cellSlot = new InvSlotOutput(this, "cell", 1);
    }

    public static void init() {
        Recipes.oreWashing = new BasicMachineRecipeManager();
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        if (this.fluidTank.getFluidAmount() < this.fluidTank.getCapacity()) {
            this.gainFluid();
        }
    }

    @Override
    public void operateOnce(RecipeOutput output, List<ItemStack> processResult) {
        super.operateOnce(output, processResult);
        this.fluidTank.drain(output.metadata.getInteger("amount"), true);
    }

    @Override
    public RecipeOutput getOutput() {
        RecipeOutput ret = super.getOutput();
        if (ret != null) {
            if (ret.metadata == null) {
                return null;
            }
            if (ret.metadata.getInteger("amount") > this.fluidTank.getFluidAmount()) {
                return null;
            }
        }
        return ret;
    }

    public boolean gainFluid() {
        boolean ret = false;
        MutableObject output = new MutableObject();
        if (this.fluidSlot.transferToTank((IFluidTank)this.fluidTank, output, true) && (output.getValue() == null || this.cellSlot.canAdd((ItemStack)output.getValue()))) {
            ret = this.fluidSlot.transferToTank((IFluidTank)this.fluidTank, output, false);
            if (output.getValue() != null) {
                this.cellSlot.add((ItemStack)output.getValue());
            }
        }
        return ret;
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return DynamicGui.create(this, player, GuiParser.parse(this.teBlock));
    }

    public ContainerBase<TileEntityOreWashing> getGuiContainer(EntityPlayer player) {
        return DynamicContainer.create(this, player, GuiParser.parse(this.teBlock));
    }

    @Override
    public boolean canFill(EnumFacing from, Fluid fluid) {
        return fluid == FluidRegistry.WATER;
    }

    @Override
    public boolean canDrain(EnumFacing from, Fluid fluid) {
        return false;
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.Processing, new UpgradableProperty[]{UpgradableProperty.Transformer, UpgradableProperty.EnergyStorage, UpgradableProperty.ItemConsuming, UpgradableProperty.ItemProducing, UpgradableProperty.FluidConsuming});
    }
}

