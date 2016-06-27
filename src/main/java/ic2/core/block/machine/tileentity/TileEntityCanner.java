/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.EnumFacing
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

import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.api.recipe.ICannerBottleRecipeManager;
import ic2.api.recipe.ICannerEnrichRecipeManager;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.recipe.RecipeInputOreDict;
import ic2.api.recipe.RecipeOutput;
import ic2.api.recipe.Recipes;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.audio.AudioManager;
import ic2.core.audio.AudioSource;
import ic2.core.block.invslot.InvSlotConsumableCanner;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotProcessable;
import ic2.core.block.invslot.InvSlotProcessableCanner;
import ic2.core.block.machine.CannerBottleRecipeManager;
import ic2.core.block.machine.CannerEnrichRecipeManager;
import ic2.core.block.machine.container.ContainerCanner;
import ic2.core.block.machine.gui.GuiCanner;
import ic2.core.block.machine.tileentity.TileEntityStandardMachine;
import ic2.core.block.state.IIdProvider;
import ic2.core.item.type.CraftingItemType;
import ic2.core.item.type.NuclearResourceType;
import ic2.core.ref.FluidName;
import ic2.core.ref.ItemName;
import ic2.core.upgrade.UpgradableProperty;
import ic2.core.util.StackUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
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

public class TileEntityCanner
extends TileEntityStandardMachine
implements IFluidHandler,
INetworkClientTileEntityEventListener {
    private Mode mode = Mode.BottleSolid;
    public static final int eventSetModeBase = 0;
    public static final int eventSwapTanks = 0 + Mode.values.length + 1;
    public final FluidTank inputTank;
    public final FluidTank outputTank;
    public final InvSlotConsumableCanner canInputSlot;

    public TileEntityCanner() {
        super(4, 200, 1);
        this.inputSlot = new InvSlotProcessableCanner(this, "input", 1);
        this.canInputSlot = new InvSlotConsumableCanner(this, "canInput", 1);
        this.inputTank = new FluidTank(8000);
        this.outputTank = new FluidTank(8000);
    }

    public static void init() {
        Recipes.cannerBottle = new CannerBottleRecipeManager();
        Recipes.cannerEnrich = new CannerEnrichRecipeManager();
        ItemStack fuelRod = ItemName.crafting.getItemStack(CraftingItemType.fuel_rod);
        TileEntityCanner.addBottleRecipe(fuelRod, ItemName.nuclear.getItemStack(NuclearResourceType.uranium), ItemName.uranium_fuel_rod.getItemStack());
        TileEntityCanner.addBottleRecipe(fuelRod, ItemName.nuclear.getItemStack(NuclearResourceType.mox), ItemName.mox_fuel_rod.getItemStack());
        ItemStack tinCan = ItemName.crafting.getItemStack(CraftingItemType.tin_can);
        ItemStack filledTinCan = ItemName.filled_tin_can.getItemStack();
        TileEntityCanner.addBottleRecipe(tinCan, new ItemStack(Items.POTATO), filledTinCan);
        TileEntityCanner.addBottleRecipe(tinCan, 2, new ItemStack(Items.COOKIE), StackUtil.copyWithSize(filledTinCan, 2));
        TileEntityCanner.addBottleRecipe(tinCan, 2, new ItemStack(Items.MELON), StackUtil.copyWithSize(filledTinCan, 2));
        TileEntityCanner.addBottleRecipe(tinCan, 2, new ItemStack(Items.FISH), StackUtil.copyWithSize(filledTinCan, 2));
        TileEntityCanner.addBottleRecipe(tinCan, 2, new ItemStack(Items.CHICKEN), StackUtil.copyWithSize(filledTinCan, 2));
        TileEntityCanner.addBottleRecipe(tinCan, 3, new ItemStack(Items.PORKCHOP), StackUtil.copyWithSize(filledTinCan, 3));
        TileEntityCanner.addBottleRecipe(tinCan, 3, new ItemStack(Items.BEEF), StackUtil.copyWithSize(filledTinCan, 3));
        TileEntityCanner.addBottleRecipe(tinCan, 4, new ItemStack(Items.APPLE), StackUtil.copyWithSize(filledTinCan, 4));
        TileEntityCanner.addBottleRecipe(tinCan, 4, new ItemStack(Items.CARROT), StackUtil.copyWithSize(filledTinCan, 4));
        TileEntityCanner.addBottleRecipe(tinCan, 5, new ItemStack(Items.BREAD), StackUtil.copyWithSize(filledTinCan, 5));
        TileEntityCanner.addBottleRecipe(tinCan, 5, new ItemStack(Items.COOKED_FISH), StackUtil.copyWithSize(filledTinCan, 5));
        TileEntityCanner.addBottleRecipe(tinCan, 6, new ItemStack(Items.COOKED_CHICKEN), StackUtil.copyWithSize(filledTinCan, 6));
        TileEntityCanner.addBottleRecipe(tinCan, 6, new ItemStack(Items.BAKED_POTATO), StackUtil.copyWithSize(filledTinCan, 6));
        TileEntityCanner.addBottleRecipe(tinCan, 6, new ItemStack(Items.MUSHROOM_STEW), StackUtil.copyWithSize(filledTinCan, 6));
        TileEntityCanner.addBottleRecipe(tinCan, 6, new ItemStack(Items.PUMPKIN_PIE), StackUtil.copyWithSize(filledTinCan, 6));
        TileEntityCanner.addBottleRecipe(tinCan, 8, new ItemStack(Items.COOKED_PORKCHOP), StackUtil.copyWithSize(filledTinCan, 8));
        TileEntityCanner.addBottleRecipe(tinCan, 8, new ItemStack(Items.COOKED_BEEF), StackUtil.copyWithSize(filledTinCan, 8));
        TileEntityCanner.addBottleRecipe(tinCan, 12, new ItemStack(Items.CAKE), StackUtil.copyWithSize(filledTinCan, 12));
        TileEntityCanner.addBottleRecipe(tinCan, new ItemStack(Items.POISONOUS_POTATO), 2, filledTinCan);
        TileEntityCanner.addBottleRecipe(tinCan, new ItemStack(Items.ROTTEN_FLESH), 2, filledTinCan);
        TileEntityCanner.addEnrichRecipe(FluidRegistry.WATER, ItemName.crafting.getItemStack(CraftingItemType.cf_powder), FluidName.construction_foam.getInstance());
        TileEntityCanner.addEnrichRecipe(FluidRegistry.WATER, (IRecipeInput)new RecipeInputOreDict("dustLapis", 8), FluidName.coolant.getInstance());
        TileEntityCanner.addEnrichRecipe(FluidName.distilled_water.getInstance(), (IRecipeInput)new RecipeInputOreDict("dustLapis", 1), FluidName.coolant.getInstance());
        TileEntityCanner.addEnrichRecipe(FluidRegistry.WATER, ItemName.crafting.getItemStack(CraftingItemType.bio_chaff), FluidName.biomass.getInstance());
        TileEntityCanner.addEnrichRecipe(new FluidStack(FluidRegistry.WATER, 6000), (IRecipeInput)new RecipeInputItemStack(new ItemStack(Items.STICK)), new FluidStack(FluidName.hot_water.getInstance(), 1000));
    }

    public static void addBottleRecipe(ItemStack container, int conamount, ItemStack fill, int fillamount, ItemStack output) {
        TileEntityCanner.addBottleRecipe(new RecipeInputItemStack(container, conamount), new RecipeInputItemStack(fill, fillamount), output);
    }

    public static void addBottleRecipe(ItemStack container, ItemStack fill, int fillamount, ItemStack output) {
        TileEntityCanner.addBottleRecipe(new RecipeInputItemStack(container, 1), new RecipeInputItemStack(fill, fillamount), output);
    }

    public static void addBottleRecipe(ItemStack container, int conamount, ItemStack fill, ItemStack output) {
        TileEntityCanner.addBottleRecipe(new RecipeInputItemStack(container, conamount), new RecipeInputItemStack(fill, 1), output);
    }

    public static void addBottleRecipe(ItemStack container, ItemStack fill, ItemStack output) {
        TileEntityCanner.addBottleRecipe(new RecipeInputItemStack(container, 1), new RecipeInputItemStack(fill, 1), output);
    }

    public static void addBottleRecipe(IRecipeInput container, IRecipeInput fill, ItemStack output) {
        Recipes.cannerBottle.addRecipe(container, fill, output);
    }

    public static void addEnrichRecipe(Fluid input, ItemStack additive, Fluid output) {
        TileEntityCanner.addEnrichRecipe(new FluidStack(input, 1000), (IRecipeInput)new RecipeInputItemStack(additive, 1), new FluidStack(output, 1000));
    }

    public static void addEnrichRecipe(Fluid input, IRecipeInput additive, Fluid output) {
        TileEntityCanner.addEnrichRecipe(new FluidStack(input, 1000), additive, new FluidStack(output, 1000));
    }

    public static void addEnrichRecipe(FluidStack input, IRecipeInput additive, FluidStack output) {
        Recipes.cannerEnrich.addRecipe(input, additive, output);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.inputTank.readFromNBT(nbt.getCompoundTag("inputTank"));
        this.outputTank.readFromNBT(nbt.getCompoundTag("outputTank"));
        this.setMode(Mode.values[nbt.getInteger("mode")]);
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
        nbt.setInteger("mode", this.mode.ordinal());
        return nbt;
    }

    @Override
    public void operateOnce(RecipeOutput output, List<ItemStack> processResult) {
        MutableObject outputContainer;
        super.operateOnce(output, processResult);
        if (this.mode == Mode.EmptyLiquid || this.mode == Mode.EnrichLiquid) {
            FluidStack fluid = FluidStack.loadFluidStackFromNBT((NBTTagCompound)output.metadata.getCompoundTag("output"));
            int amount = this.outputTank.fill(fluid, true);
            assert (amount == fluid.amount);
        }
        if (this.mode == Mode.EnrichLiquid && this.canInputSlot.transferFromTank((IFluidTank)this.outputTank, outputContainer = new MutableObject(), true) && (outputContainer.getValue() == null || this.outputSlot.canAdd((ItemStack)outputContainer.getValue()))) {
            this.canInputSlot.transferFromTank((IFluidTank)this.outputTank, outputContainer, false);
            if (outputContainer.getValue() != null) {
                this.outputSlot.add((ItemStack)outputContainer.getValue());
            }
        }
    }

    @Override
    public RecipeOutput getOutput() {
        FluidStack fluid;
        int amount;
        if (this.mode == Mode.EmptyLiquid || this.mode == Mode.BottleLiquid ? this.canInputSlot.isEmpty() : this.inputSlot.isEmpty()) {
            return null;
        }
        RecipeOutput output = this.inputSlot.process();
        if (output == null) {
            return null;
        }
        if (!this.outputSlot.canAdd(output.items)) {
            return null;
        }
        if ((this.mode == Mode.EmptyLiquid || this.mode == Mode.EnrichLiquid) && (amount = this.outputTank.fill(fluid = FluidStack.loadFluidStackFromNBT((NBTTagCompound)output.metadata.getCompoundTag("output")), false)) != fluid.amount) {
            return null;
        }
        return output;
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
        return this.inputTank.getFluid() == null || this.inputTank.getFluid().isFluidEqual(new FluidStack(fluid, 1));
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
    public List<String> getNetworkedFields() {
        ArrayList<String> ret = new ArrayList<String>();
        ret.add("canInputSlot");
        ret.addAll(super.getNetworkedFields());
        return ret;
    }

    @Override
    protected void onUnloaded() {
        if (this.audioSource != null) {
            IC2.audioManager.removeSources(this);
            this.audioSource = null;
        }
        super.onUnloaded();
    }

    @Override
    public String getStartSoundFile() {
        return null;
    }

    @Override
    public String getInterruptSoundFile() {
        return null;
    }

    public ContainerBase<TileEntityCanner> getGuiContainer(EntityPlayer player) {
        return new ContainerCanner(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiCanner(new ContainerCanner(player, this));
    }

    @Override
    public void onNetworkUpdate(String field) {
        super.onNetworkUpdate(field);
        if (field.equals("mode")) {
            this.setMode(this.mode);
        }
    }

    @Override
    public void onNetworkEvent(EntityPlayer player, int event) {
        if (event >= 0 && event < 0 + Mode.values.length) {
            this.setMode(Mode.values[event - 0]);
        } else if (event == eventSwapTanks) {
            this.switchTanks();
        }
    }

    public Mode getMode() {
        return this.mode;
    }

    public void setMode(Mode mode) {
        this.mode = mode;
        switch (mode) {
            case BottleSolid: {
                this.canInputSlot.setOpType(InvSlotConsumableLiquid.OpType.None);
                break;
            }
            case BottleLiquid: {
                this.canInputSlot.setOpType(InvSlotConsumableLiquid.OpType.Fill);
                break;
            }
            case EmptyLiquid: {
                this.canInputSlot.setOpType(InvSlotConsumableLiquid.OpType.Drain);
                break;
            }
            case EnrichLiquid: {
                this.canInputSlot.setOpType(InvSlotConsumableLiquid.OpType.Both);
            }
        }
    }

    private boolean switchTanks() {
        if (this.progress != 0) {
            return false;
        }
        FluidStack inputStack = this.inputTank.getFluid();
        FluidStack outputStack = this.outputTank.getFluid();
        this.inputTank.setFluid(outputStack);
        this.outputTank.setFluid(inputStack);
        return true;
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.Processing, new UpgradableProperty[]{UpgradableProperty.Transformer, UpgradableProperty.EnergyStorage, UpgradableProperty.ItemConsuming, UpgradableProperty.ItemProducing, UpgradableProperty.FluidConsuming, UpgradableProperty.FluidProducing});
    }

    public static enum Mode {
        BottleSolid,
        EmptyLiquid,
        BottleLiquid,
        EnrichLiquid;
        
        public static final Mode[] values;

        private Mode() {
        }

        static {
            values = Mode.values();
        }
    }

}

