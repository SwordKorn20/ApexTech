/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.machine.tileentity;

import ic2.api.item.IBlockCuttingBlade;
import ic2.api.recipe.IMachineRecipeManager;
import ic2.api.recipe.RecipeOutput;
import ic2.api.recipe.Recipes;
import ic2.core.ContainerBase;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlotConsumableClass;
import ic2.core.block.invslot.InvSlotProcessable;
import ic2.core.block.invslot.InvSlotProcessableGeneric;
import ic2.core.block.machine.container.ContainerBlockCutter;
import ic2.core.block.machine.gui.GuiBlockCutter;
import ic2.core.block.machine.tileentity.TileEntityStandardMachine;
import ic2.core.recipe.BasicMachineRecipeManager;
import ic2.core.upgrade.UpgradableProperty;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityBlockCutter
extends TileEntityStandardMachine {
    private boolean bladeTooWeak = false;
    public final InvSlotConsumableClass cutterSlot;

    public TileEntityBlockCutter() {
        super(4, 450, 1);
        this.inputSlot = new InvSlotProcessableGeneric(this, "input", 1, Recipes.blockcutter);
        this.cutterSlot = new InvSlotConsumableClass(this, "cutterSlot", 1, IBlockCuttingBlade.class);
    }

    public static void init() {
        Recipes.blockcutter = new BasicMachineRecipeManager();
    }

    @Override
    public RecipeOutput getOutput() {
        RecipeOutput ret;
        if (this.cutterSlot.isEmpty()) {
            if (!this.bladeTooWeak) {
                this.bladeTooWeak = true;
            }
            return null;
        }
        if (this.bladeTooWeak) {
            this.bladeTooWeak = false;
        }
        if ((ret = super.getOutput()) == null || ret.metadata == null) {
            return null;
        }
        ItemStack bladeStack = this.cutterSlot.get();
        IBlockCuttingBlade blade = (IBlockCuttingBlade)bladeStack.getItem();
        if (ret.metadata.getInteger("hardness") > blade.getHardness(bladeStack)) {
            if (!this.bladeTooWeak) {
                this.bladeTooWeak = true;
            }
            return null;
        }
        if (this.bladeTooWeak) {
            this.bladeTooWeak = false;
        }
        return ret;
    }

    public ContainerBase<TileEntityBlockCutter> getGuiContainer(EntityPlayer player) {
        return new ContainerBlockCutter(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiBlockCutter(new ContainerBlockCutter(player, this));
    }

    public boolean isBladeTooWeak() {
        return this.bladeTooWeak;
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.Processing, UpgradableProperty.Transformer, UpgradableProperty.EnergyStorage, UpgradableProperty.ItemConsuming, UpgradableProperty.ItemProducing);
    }
}

