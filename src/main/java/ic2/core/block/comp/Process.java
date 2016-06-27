/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 */
package ic2.core.block.comp;

import ic2.api.recipe.IMachineRecipeManager;
import ic2.api.recipe.RecipeOutput;
import ic2.api.recipe.Recipes;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.TileEntityComponent;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotProcessable;
import ic2.core.block.invslot.InvSlotProcessableGeneric;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.IUpgradeItem;
import java.util.List;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class Process
extends TileEntityComponent {
    protected int progress = 0;
    public int defaultEnergyConsume;
    public int operationDuration;
    public int defaultTier;
    public int defaultEnergyStorage;
    public int energyConsume;
    public int operationLength;
    public int operationsPerTick;
    private final InvSlotProcessable inputSlot;
    private final InvSlotOutput outputSlot;
    private InvSlotUpgrade upgradeSlot;

    public static Process asFurnace(TileEntityInventory parent) {
        return Process.asFurnace(parent, 3, 100, 1, 4);
    }

    public static Process asFurnace(TileEntityInventory parent, int operationCost, int operationDuration, int outputSlots, int upgradeSlots) {
        return new Process(parent, Recipes.furnace, operationCost, operationDuration, outputSlots, upgradeSlots);
    }

    public static Process asMacerator(TileEntityInventory parent) {
        return Process.asMacerator(parent, 2, 300, 1, 4);
    }

    public static Process asMacerator(TileEntityInventory parent, int operationCost, int operationDuration, int outputSlots, int upgradeSlots) {
        return new Process(parent, Recipes.macerator, operationCost, operationDuration, outputSlots, upgradeSlots);
    }

    public static Process asExtractor(TileEntityInventory parent) {
        return Process.asExtractor(parent, 2, 300, 1, 4);
    }

    public static Process asExtractor(TileEntityInventory parent, int operationCost, int operationDuration, int outputSlots, int upgradeSlots) {
        return new Process(parent, Recipes.extractor, operationCost, operationDuration, outputSlots, upgradeSlots);
    }

    public static Process asCompressor(TileEntityInventory parent) {
        return Process.asCompressor(parent, 2, 300, 1, 4);
    }

    public static Process asCompressor(TileEntityInventory parent, int operationCost, int operationDuration, int outputSlots, int upgradeSlots) {
        return new Process(parent, Recipes.compressor, operationCost, operationDuration, outputSlots, upgradeSlots);
    }

    public static Process asCentrifuge(TileEntityInventory parent) {
        return Process.asCentrifuge(parent, 48, 500, 3, 4);
    }

    public static Process asCentrifuge(TileEntityInventory parent, int operationCost, int operationDuration, int outputSlots, int upgradeSlots) {
        return new Process(parent, Recipes.centrifuge, operationCost, operationDuration, outputSlots, upgradeSlots);
    }

    public static Process asRecycler(TileEntityInventory parent) {
        return Process.asRecycler(parent, 1, 45, 1, 4);
    }

    public static Process asRecycler(TileEntityInventory parent, int operationCost, int operationDuration, int outputSlots, int upgradeSlots) {
        return new Process(parent, Recipes.recycler, operationCost, operationDuration, outputSlots, upgradeSlots);
    }

    public static Process asOreWasher(TileEntityInventory parent) {
        return Process.asOreWasher(parent, 16, 500, 3, 4);
    }

    public static Process asOreWasher(TileEntityInventory parent, int operationCost, int operationDuration, int outputSlots, int upgradeSlots) {
        return new Process(parent, Recipes.oreWashing, operationCost, operationDuration, outputSlots, upgradeSlots);
    }

    public static Process asBlockCutter(TileEntityInventory parent) {
        return Process.asBlockCutter(parent, 48, 900, 1, 4);
    }

    public static Process asBlockCutter(TileEntityInventory parent, int operationCost, int operationDuration, int outputSlots, int upgradeSlots) {
        return new Process(parent, Recipes.blockcutter, operationCost, operationDuration, outputSlots, upgradeSlots);
    }

    public static Process asBlastFurnace(TileEntityInventory parent) {
        return Process.asBlastFurnace(parent, 2, 300, 1, 4);
    }

    public static Process asBlastFurnace(TileEntityInventory parent, int operationCost, int operationDuration, int outputSlots, int upgradeSlots) {
        return new Process(parent, Recipes.blastfurnace, operationCost, operationDuration, outputSlots, upgradeSlots);
    }

    public static Process asExtruder(TileEntityInventory parent) {
        return Process.asExtruder(parent, 10, 200, 1, 4);
    }

    public static Process asExtruder(TileEntityInventory parent, int operationCost, int operationDuration, int outputSlots, int upgradeSlots) {
        return new Process(parent, Recipes.metalformerExtruding, operationCost, operationDuration, outputSlots, upgradeSlots);
    }

    public static Process asCutter(TileEntityInventory parent) {
        return Process.asCutter(parent, 10, 200, 1, 4);
    }

    public static Process asCutter(TileEntityInventory parent, int operationCost, int operationDuration, int outputSlots, int upgradeSlots) {
        return new Process(parent, Recipes.metalformerCutting, operationCost, operationDuration, outputSlots, upgradeSlots);
    }

    public static Process asRollingMachine(TileEntityInventory parent) {
        return Process.asRollingMachine(parent, 10, 200, 1, 4);
    }

    public static Process asRollingMachine(TileEntityInventory parent, int operationCost, int operationDuration, int outputSlots, int upgradeSlots) {
        return new Process(parent, Recipes.metalformerRolling, operationCost, operationDuration, outputSlots, upgradeSlots);
    }

    public Process(TileEntityInventory parent, IMachineRecipeManager recipeManager) {
        this(parent, recipeManager, 2, 100, 1, 0);
    }

    public Process(TileEntityInventory parent, IMachineRecipeManager recipeManager, int operationCost, int operationDuration, int outputSlots, int upgradeSlots) {
        super(parent);
        this.operationDuration = operationDuration;
        this.inputSlot = new InvSlotProcessableGeneric(parent, "input", 1, recipeManager);
        this.outputSlot = new InvSlotOutput(parent, "output", outputSlots);
        if (parent instanceof IUpgradableBlock && upgradeSlots > 0) {
            this.upgradeSlot = new InvSlotUpgrade(parent, "upgrade", upgradeSlots);
        }
    }

    public void readFromNBT(NBTTagCompound nbttagcompound) {
        this.progress = nbttagcompound.getInteger("progress");
    }

    public void writeToNBT(NBTTagCompound nbttagcompound) {
        nbttagcompound.setInteger("progress", this.progress);
    }

    public static int applyModifier(int base, int extra, double multiplier) {
        double ret = Math.round(((double)base + (double)extra) * multiplier);
        return ret > 2.147483647E9 ? Integer.MAX_VALUE : (int)ret;
    }

    public void setOverclockRates() {
        this.upgradeSlot.onChanged();
        double previousProgress = (double)this.progress / (double)this.operationDuration;
        double stackOpLen = ((double)this.operationDuration + (double)this.upgradeSlot.extraProcessTime) * 64.0 * this.upgradeSlot.processTimeMultiplier;
        this.operationsPerTick = (int)Math.min(Math.ceil(64.0 / stackOpLen), 2.147483647E9);
        this.operationDuration = (int)Math.round(stackOpLen * (double)this.operationsPerTick / 64.0);
        this.energyConsume = Process.applyModifier(this.defaultEnergyConsume, this.upgradeSlot.extraEnergyDemand, this.upgradeSlot.energyDemandMultiplier);
        if (this.operationDuration < 1) {
            this.operationDuration = 1;
        }
        this.progress = (short)Math.floor(previousProgress * (double)this.operationDuration + 0.1);
    }

    public void operate(RecipeOutput output) {
        for (int i = 0; i < this.operationsPerTick; ++i) {
            List<ItemStack> processResult = output.items;
            if (this.parent instanceof IUpgradableBlock) {
                for (int j = 0; j < this.upgradeSlot.size(); ++j) {
                    ItemStack stack = this.upgradeSlot.get(j);
                    if (stack == null || !(stack.getItem() instanceof IUpgradeItem)) continue;
                    ((IUpgradeItem)stack.getItem()).onProcessEnd(stack, (IUpgradableBlock)((Object)this.parent), processResult);
                }
            }
            this.operateOnce(output, processResult);
            output = this.getOutput();
            if (output == null) break;
        }
    }

    public void operateOnce(RecipeOutput output, List<ItemStack> processResult) {
        this.inputSlot.consume();
        this.outputSlot.add(processResult);
    }

    public RecipeOutput getOutput() {
        if (this.inputSlot.isEmpty()) {
            return null;
        }
        RecipeOutput output = this.inputSlot.process();
        if (output == null) {
            return null;
        }
        if (this.outputSlot.canAdd(output.items)) {
            return output;
        }
        return null;
    }

    public int getProgress() {
        return this.progress;
    }

    public double getProgressRatio() {
        return this.progress / this.operationDuration;
    }
}

