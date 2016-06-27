/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockDynamicLiquid
 *  net.minecraft.block.BlockFire
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$MutableBlockPos
 *  net.minecraft.world.ChunkCache
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.FluidTank
 *  net.minecraftforge.fluids.FluidTankInfo
 *  net.minecraftforge.fluids.IFluidHandler
 *  net.minecraftforge.fluids.IFluidTank
 *  net.minecraftforge.fml.common.eventhandler.Event
 *  net.minecraftforge.fml.common.eventhandler.EventBus
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  org.apache.commons.lang3.mutable.MutableBoolean
 *  org.apache.commons.lang3.mutable.MutableObject
 *  org.apache.logging.log4j.Level
 */
package ic2.core.block.reactor.tileentity;

import ic2.api.energy.event.EnergyTileLoadEvent;
import ic2.api.energy.event.EnergyTileUnloadEvent;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.energy.tile.IMetaDelegate;
import ic2.api.reactor.IReactor;
import ic2.api.reactor.IReactorComponent;
import ic2.api.recipe.RecipeOutput;
import ic2.core.ContainerBase;
import ic2.core.ExplosionIC2;
import ic2.core.IC2;
import ic2.core.IC2DamageSource;
import ic2.core.IHasGui;
import ic2.core.Platform;
import ic2.core.audio.AudioManager;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.Redstone;
import ic2.core.block.comp.TileEntityComponent;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotConsumableLiquidByList;
import ic2.core.block.invslot.InvSlotConsumableLiquidByTank;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotReactor;
import ic2.core.block.reactor.container.ContainerNuclearReactor;
import ic2.core.block.reactor.gui.GuiNuclearReactor;
import ic2.core.block.reactor.tileentity.TileEntityReactorAccessHatch;
import ic2.core.block.reactor.tileentity.TileEntityReactorChamberElectric;
import ic2.core.block.reactor.tileentity.TileEntityReactorFluidPort;
import ic2.core.block.reactor.tileentity.TileEntityReactorRedstonePort;
import ic2.core.block.state.IIdProvider;
import ic2.core.block.type.ResourceBlock;
import ic2.core.gui.dynamic.IGuiValueProvider;
import ic2.core.init.MainConfig;
import ic2.core.item.reactor.ItemReactorHeatStorage;
import ic2.core.item.type.NuclearResourceType;
import ic2.core.network.NetworkManager;
import ic2.core.ref.BlockName;
import ic2.core.ref.FluidName;
import ic2.core.ref.IBlockModelProvider;
import ic2.core.ref.IItemModelProvider;
import ic2.core.ref.ItemName;
import ic2.core.ref.TeBlock;
import ic2.core.util.ConfigUtil;
import ic2.core.util.Log;
import ic2.core.util.LogCategory;
import ic2.core.util.SideGateway;
import ic2.core.util.StackUtil;
import ic2.core.util.Util;
import ic2.core.util.WorldSearchUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockFire;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.Level;

public class TileEntityNuclearReactorElectric
extends TileEntityInventory
implements IHasGui,
IReactor,
IEnergySource,
IMetaDelegate,
IFluidHandler,
IGuiValueProvider {
    public AudioSource audioSourceMain;
    public AudioSource audioSourceGeiger;
    private float lastOutput = 0.0f;
    public final FluidTank inputTank;
    public final FluidTank outputTank;
    private final List<IEnergyTile> subTiles = new ArrayList<IEnergyTile>();
    public final InvSlotReactor reactorSlot;
    public final InvSlotOutput coolantoutputSlot;
    public final InvSlotOutput hotcoolantoutputSlot;
    public final InvSlotConsumableLiquidByList coolantinputSlot;
    public final InvSlotConsumableLiquidByTank hotcoolinputSlot;
    public final Redstone redstone;
    public float output = 0.0f;
    public int updateTicker;
    public int heat = 0;
    public int maxHeat = 10000;
    public float hem = 1.0f;
    private int EmitHeatbuffer = 0;
    public int EmitHeat = 0;
    private boolean fluidCooled = false;
    public boolean addedToEnergyNet = false;
    private static final float huOutputModifier = 2.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/FluidReactor/outputModifier");

    public TileEntityNuclearReactorElectric() {
        this.updateTicker = IC2.random.nextInt(this.getTickRate());
        this.inputTank = new FluidTank(10000);
        this.outputTank = new FluidTank(10000);
        this.reactorSlot = new InvSlotReactor(this, "reactor", 54);
        this.coolantinputSlot = new InvSlotConsumableLiquidByList((TileEntityInventory)this, "coolantinputSlot", InvSlot.Access.I, 1, InvSlot.InvSide.ANY, InvSlotConsumableLiquid.OpType.Drain, FluidName.coolant.getInstance());
        this.hotcoolinputSlot = new InvSlotConsumableLiquidByTank(this, "hotcoolinputSlot", InvSlot.Access.I, 1, InvSlot.InvSide.ANY, InvSlotConsumableLiquid.OpType.Fill, (IFluidTank)this.outputTank);
        this.coolantoutputSlot = new InvSlotOutput(this, "coolantoutputSlot", 1);
        this.hotcoolantoutputSlot = new InvSlotOutput(this, "hotcoolantoutputSlot", 1);
        this.redstone = this.addComponent(new Redstone(this));
    }

    @Override
    protected void onLoaded() {
        super.onLoaded();
        if (!this.worldObj.isRemote && !this.isFluidCooled()) {
            this.refreshChambers();
            MinecraftForge.EVENT_BUS.post((Event)new EnergyTileLoadEvent(this));
            this.addedToEnergyNet = true;
        }
        this.createChamberRedstoneLinks();
        if (this.isFluidCooled()) {
            this.createCasingRedstoneLinks();
        }
    }

    @Override
    protected void onUnloaded() {
        if (IC2.platform.isRendering()) {
            IC2.audioManager.removeSources(this);
            this.audioSourceMain = null;
            this.audioSourceGeiger = null;
        }
        if (IC2.platform.isSimulating() && this.addedToEnergyNet) {
            MinecraftForge.EVENT_BUS.post((Event)new EnergyTileUnloadEvent(this));
            this.addedToEnergyNet = false;
        }
        super.onUnloaded();
    }

    public int gaugeHeatScaled(int i) {
        return i * this.heat / (this.maxHeat / 100 * 85);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.heat = nbt.getInteger("heat");
        this.inputTank.readFromNBT(nbt.getCompoundTag("inputTank"));
        this.outputTank.readFromNBT(nbt.getCompoundTag("outputTank"));
        this.output = nbt.getShort("output");
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
        nbt.setInteger("heat", this.heat);
        nbt.setShort("output", (short)this.getReactorEnergyOutput());
        return nbt;
    }

    @Override
    protected void onNeighborChange(Block neighbor) {
        super.onNeighborChange(neighbor);
        if (this.addedToEnergyNet) {
            this.refreshChambers();
        }
    }

    @Override
    public void drawEnergy(double amount) {
    }

    public float sendEnergy(float send) {
        return 0.0f;
    }

    @Override
    public boolean emitsEnergyTo(IEnergyAcceptor receiver, EnumFacing direction) {
        return true;
    }

    @Override
    public double getOfferedEnergy() {
        return this.getReactorEnergyOutput() * 5.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/nuclear");
    }

    @Override
    public int getSourceTier() {
        return 4;
    }

    @Override
    public double getReactorEUEnergyOutput() {
        return this.getOfferedEnergy();
    }

    @Override
    public List<IEnergyTile> getSubTiles() {
        return this.subTiles;
    }

    private void processfluidsSlots() {
        RecipeOutput outputoutputSlot;
        RecipeOutput outputinputSlot = this.processInputSlot(true);
        if (outputinputSlot != null) {
            this.processInputSlot(false);
            List<ItemStack> processResult = outputinputSlot.items;
            this.coolantoutputSlot.add(processResult);
        }
        if ((outputoutputSlot = this.processOutputSlot(true)) != null) {
            this.processOutputSlot(false);
            List<ItemStack> processResult = outputoutputSlot.items;
            this.hotcoolantoutputSlot.add(processResult);
        }
    }

    public void refreshChambers() {
        ArrayList<TileEntityBlock> newSubTiles = new ArrayList<TileEntityBlock>();
        newSubTiles.add(this);
        for (EnumFacing dir : EnumFacing.VALUES) {
            TileEntity te = this.worldObj.getTileEntity(this.pos.offset(dir));
            if (!(te instanceof TileEntityReactorChamberElectric) || te.isInvalid()) continue;
            newSubTiles.add((TileEntityReactorChamberElectric)te);
        }
        if (!newSubTiles.equals(this.subTiles)) {
            if (this.addedToEnergyNet) {
                MinecraftForge.EVENT_BUS.post((Event)new EnergyTileUnloadEvent(this));
            }
            this.subTiles.clear();
            this.subTiles.addAll(newSubTiles);
            if (this.addedToEnergyNet) {
                MinecraftForge.EVENT_BUS.post((Event)new EnergyTileLoadEvent(this));
            }
        }
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        if (this.updateTicker++ % this.getTickRate() != 0) {
            return;
        }
        if (!this.worldObj.isAreaLoaded(this.pos, 8)) {
            this.output = 0.0f;
        } else {
            boolean toFluidCooled = this.isFluidReactor();
            if (this.fluidCooled != toFluidCooled) {
                if (toFluidCooled) {
                    this.enableFluidMode();
                } else {
                    this.disableFluidMode();
                }
                this.fluidCooled = toFluidCooled;
            }
            this.dropAllUnfittingStuff();
            this.output = 0.0f;
            this.maxHeat = 10000;
            this.hem = 1.0f;
            this.processChambers();
            if (this.fluidCooled) {
                this.processfluidsSlots();
                int huOtput = (int)(huOutputModifier * (float)this.EmitHeatbuffer);
                int outputroom = this.outputTank.getCapacity() - this.outputTank.getFluidAmount();
                if (outputroom > 0) {
                    FluidStack draincoolant = huOtput < outputroom ? this.inputTank.drain(huOtput, false) : this.inputTank.drain(outputroom, false);
                    if (draincoolant != null) {
                        this.EmitHeat = draincoolant.amount;
                        huOtput -= this.inputTank.drain((int)draincoolant.amount, (boolean)true).amount;
                        this.outputTank.fill(new FluidStack(FluidName.hot_coolant.getInstance(), draincoolant.amount), true);
                    } else {
                        this.EmitHeat = 0;
                    }
                } else {
                    this.EmitHeat = 0;
                }
                this.addHeat(huOtput / 2);
            }
            this.EmitHeatbuffer = 0;
            if (this.calculateHeatEffects()) {
                return;
            }
            this.setActive(this.heat >= 1000 || this.output > 0.0f);
            this.markDirty();
        }
        IC2.network.get(true).updateTileEntityField(this, "output");
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    protected void updateEntityClient() {
        super.updateEntityClient();
        TileEntityNuclearReactorElectric.showHeatEffects(this.worldObj, this.pos, this.heat);
    }

    public static void showHeatEffects(World world, BlockPos pos, int heat) {
        Random rnd = world.rand;
        if (rnd.nextInt(8) != 0) {
            return;
        }
        int puffs = heat / 1000;
        if (puffs > 0) {
            int n;
            puffs = rnd.nextInt(puffs);
            for (n = 0; n < puffs; ++n) {
                world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, (double)((float)pos.getX() + rnd.nextFloat()), (double)((float)pos.getY() + 0.95f), (double)((float)pos.getZ() + rnd.nextFloat()), 0.0, 0.0, 0.0, new int[0]);
            }
            for (n = 0; n < (puffs -= rnd.nextInt(4) + 3); ++n) {
                world.spawnParticle(EnumParticleTypes.FLAME, (double)((float)pos.getX() + rnd.nextFloat()), (double)(pos.getY() + 1), (double)((float)pos.getZ() + rnd.nextFloat()), 0.0, 0.0, 0.0, new int[0]);
            }
        }
    }

    public void dropAllUnfittingStuff() {
        ItemStack stack;
        int i;
        for (i = 0; i < this.reactorSlot.size(); ++i) {
            stack = this.reactorSlot.get(i);
            if (stack == null || this.isUsefulItem(stack, false)) continue;
            this.reactorSlot.put(i, null);
            this.eject(stack);
        }
        for (i = this.reactorSlot.size(); i < this.reactorSlot.rawSize(); ++i) {
            stack = this.reactorSlot.get(i);
            this.reactorSlot.put(i, null);
            this.eject(stack);
        }
    }

    public boolean isUsefulItem(ItemStack stack, boolean forInsertion) {
        Item item = stack.getItem();
        if (item == null) {
            return false;
        }
        if (forInsertion && this.fluidCooled && item.getClass() == ItemReactorHeatStorage.class && ((ItemReactorHeatStorage)item).getCustomDamage(stack) > 0) {
            return false;
        }
        if (item instanceof IReactorComponent) {
            return true;
        }
        return item == ItemName.tritium_fuel_rod.getInstance() || StackUtil.checkItemEquality(stack, ItemName.nuclear.getItemStack(NuclearResourceType.depleted_uranium)) || StackUtil.checkItemEquality(stack, ItemName.nuclear.getItemStack(NuclearResourceType.depleted_dual_uranium)) || StackUtil.checkItemEquality(stack, ItemName.nuclear.getItemStack(NuclearResourceType.depleted_quad_uranium)) || StackUtil.checkItemEquality(stack, ItemName.nuclear.getItemStack(NuclearResourceType.depleted_mox)) || StackUtil.checkItemEquality(stack, ItemName.nuclear.getItemStack(NuclearResourceType.depleted_dual_mox)) || StackUtil.checkItemEquality(stack, ItemName.nuclear.getItemStack(NuclearResourceType.depleted_quad_mox));
    }

    public void eject(ItemStack drop) {
        if (!IC2.platform.isSimulating() || drop == null) {
            return;
        }
        StackUtil.dropAsEntity(this.worldObj, this.pos, drop);
    }

    public boolean calculateHeatEffects() {
        Material mat;
        BlockPos coord;
        Object state;
        if (this.heat < 4000 || !IC2.platform.isSimulating() || ConfigUtil.getFloat(MainConfig.get(), "protection/reactorExplosionPowerLimit") <= 0.0f) {
            return false;
        }
        float power = (float)this.heat / (float)this.maxHeat;
        if (power >= 1.0f) {
            this.explode();
            return true;
        }
        if (power >= 0.85f && this.worldObj.rand.nextFloat() <= 0.2f * this.hem) {
            coord = this.getRandCoord(2);
            state = this.worldObj.getBlockState(coord);
            Block block = state.getBlock();
            if (block.isAir((IBlockState)state, (IBlockAccess)this.worldObj, coord)) {
                this.worldObj.setBlockState(coord, Blocks.FIRE.getDefaultState());
            } else if (state.getBlockHardness(this.worldObj, coord) >= 0.0f && this.worldObj.getTileEntity(coord) == null) {
                Material mat2 = state.getMaterial();
                if (mat2 == Material.ROCK || mat2 == Material.IRON || mat2 == Material.LAVA || mat2 == Material.GROUND || mat2 == Material.CLAY) {
                    this.worldObj.setBlockState(coord, Blocks.FLOWING_LAVA.getDefaultState());
                } else {
                    this.worldObj.setBlockState(coord, Blocks.FIRE.getDefaultState());
                }
            }
        }
        if (power >= 0.7f) {
            List nearByEntities = this.worldObj.getEntitiesWithinAABB((Class)EntityLivingBase.class, new AxisAlignedBB((double)(this.pos.getX() - 3), (double)(this.pos.getY() - 3), (double)(this.pos.getZ() - 3), (double)(this.pos.getX() + 4), (double)(this.pos.getY() + 4), (double)(this.pos.getZ() + 4)));
            state = nearByEntities.iterator();
            while (state.hasNext()) {
                EntityLivingBase entity = (EntityLivingBase)state.next();
                entity.attackEntityFrom((DamageSource)IC2DamageSource.radiation, (float)((int)((float)this.worldObj.rand.nextInt(4) * this.hem)));
            }
        }
        if (power >= 0.5f && this.worldObj.rand.nextFloat() <= this.hem && (state = this.worldObj.getBlockState(coord = this.getRandCoord(2))).getMaterial() == Material.WATER) {
            this.worldObj.setBlockToAir(coord);
        }
        if (power >= 0.4f && this.worldObj.rand.nextFloat() <= this.hem && this.worldObj.getTileEntity(coord = this.getRandCoord(2)) == null && ((mat = (state = this.worldObj.getBlockState(coord)).getMaterial()) == Material.WOOD || mat == Material.LEAVES || mat == Material.CLOTH)) {
            this.worldObj.setBlockState(coord, Blocks.FIRE.getDefaultState());
        }
        return false;
    }

    public BlockPos getRandCoord(int radius) {
        BlockPos ret;
        if (radius <= 0) {
            return null;
        }
        while ((ret = this.pos.add(this.worldObj.rand.nextInt(2 * radius + 1) - radius, this.worldObj.rand.nextInt(2 * radius + 1) - radius, this.worldObj.rand.nextInt(2 * radius + 1) - radius)).equals((Object)this.pos)) {
        }
        return ret;
    }

    public void processChambers() {
        int size = this.getReactorSize();
        for (int pass = 0; pass < 2; ++pass) {
            for (int y = 0; y < 6; ++y) {
                for (int x = 0; x < size; ++x) {
                    ItemStack stack = this.reactorSlot.get(x, y);
                    if (stack == null || !(stack.getItem() instanceof IReactorComponent)) continue;
                    IReactorComponent comp = (IReactorComponent)stack.getItem();
                    comp.processChamber(stack, this, x, y, pass == 0);
                }
            }
        }
    }

    @Override
    public boolean produceEnergy() {
        return this.redstone.hasRedstoneInput() && ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/generator") > 0.0f;
    }

    public int getReactorSize() {
        if (this.worldObj == null) {
            return 9;
        }
        int cols = 3;
        for (EnumFacing dir : EnumFacing.VALUES) {
            TileEntity target = this.worldObj.getTileEntity(this.pos.offset(dir));
            if (!(target instanceof TileEntityReactorChamberElectric)) continue;
            ++cols;
        }
        return cols;
    }

    private boolean isFullSize() {
        return this.getReactorSize() == 9;
    }

    @Override
    public int getTickRate() {
        return 20;
    }

    @Override
    protected boolean onActivated(EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (StackUtil.checkItemEquality(heldItem, BlockName.te.getItemStack(TeBlock.reactor_chamber))) {
            return false;
        }
        return super.onActivated(player, hand, heldItem, side, hitX, hitY, hitZ);
    }

    public ContainerBase<TileEntityNuclearReactorElectric> getGuiContainer(EntityPlayer player) {
        return new ContainerNuclearReactor(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiNuclearReactor(new ContainerNuclearReactor(player, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }

    @Override
    public void onNetworkUpdate(String field) {
        if (field.equals("output")) {
            if (this.output > 0.0f) {
                if (this.lastOutput <= 0.0f) {
                    if (this.audioSourceMain == null) {
                        this.audioSourceMain = IC2.audioManager.createSource(this, PositionSpec.Center, "Generators/NuclearReactor/NuclearReactorLoop.ogg", true, false, IC2.audioManager.getDefaultVolume());
                    }
                    if (this.audioSourceMain != null) {
                        this.audioSourceMain.play();
                    }
                }
                if (this.output < 40.0f) {
                    if (this.lastOutput <= 0.0f || this.lastOutput >= 40.0f) {
                        if (this.audioSourceGeiger != null) {
                            this.audioSourceGeiger.remove();
                        }
                        this.audioSourceGeiger = IC2.audioManager.createSource(this, PositionSpec.Center, "Generators/NuclearReactor/GeigerLowEU.ogg", true, false, IC2.audioManager.getDefaultVolume());
                        if (this.audioSourceGeiger != null) {
                            this.audioSourceGeiger.play();
                        }
                    }
                } else if (this.output < 80.0f) {
                    if (this.lastOutput < 40.0f || this.lastOutput >= 80.0f) {
                        if (this.audioSourceGeiger != null) {
                            this.audioSourceGeiger.remove();
                        }
                        this.audioSourceGeiger = IC2.audioManager.createSource(this, PositionSpec.Center, "Generators/NuclearReactor/GeigerMedEU.ogg", true, false, IC2.audioManager.getDefaultVolume());
                        if (this.audioSourceGeiger != null) {
                            this.audioSourceGeiger.play();
                        }
                    }
                } else if (this.output >= 80.0f && this.lastOutput < 80.0f) {
                    if (this.audioSourceGeiger != null) {
                        this.audioSourceGeiger.remove();
                    }
                    this.audioSourceGeiger = IC2.audioManager.createSource(this, PositionSpec.Center, "Generators/NuclearReactor/GeigerHighEU.ogg", true, false, IC2.audioManager.getDefaultVolume());
                    if (this.audioSourceGeiger != null) {
                        this.audioSourceGeiger.play();
                    }
                }
            } else if (this.lastOutput > 0.0f) {
                if (this.audioSourceMain != null) {
                    this.audioSourceMain.stop();
                }
                if (this.audioSourceGeiger != null) {
                    this.audioSourceGeiger.stop();
                }
            }
            this.lastOutput = this.output;
        }
        super.onNetworkUpdate(field);
    }

    @Override
    public TileEntity getCoreTe() {
        return this;
    }

    @Override
    public int getHeat() {
        return this.heat;
    }

    @Override
    public void setHeat(int heat) {
        this.heat = heat;
    }

    @Override
    public int addHeat(int amount) {
        this.heat += amount;
        return this.heat;
    }

    @Override
    public ItemStack getItemAt(int x, int y) {
        if (x < 0 || x >= this.getReactorSize() || y < 0 || y >= 6) {
            return null;
        }
        return this.reactorSlot.get(x, y);
    }

    @Override
    public void setItemAt(int x, int y, ItemStack item) {
        if (x < 0 || x >= this.getReactorSize() || y < 0 || y >= 6) {
            return;
        }
        this.reactorSlot.put(x, y, item);
    }

    @Override
    public void explode() {
        float boomPower = 10.0f;
        float boomMod = 1.0f;
        for (int i = 0; i < this.reactorSlot.size(); ++i) {
            ItemStack stack = this.reactorSlot.get(i);
            if (stack != null && stack.getItem() instanceof IReactorComponent) {
                float f = ((IReactorComponent)stack.getItem()).influenceExplosion(stack, this);
                if (f > 0.0f && f < 1.0f) {
                    boomMod *= f;
                } else {
                    boomPower += f;
                }
            }
            this.reactorSlot.put(i, null);
        }
        IC2.log.log(LogCategory.PlayerActivity, Level.INFO, "Nuclear Reactor at %s melted (raw explosion power %f)", Util.formatPosition(this), Float.valueOf(boomPower *= this.hem * boomMod));
        boomPower = Math.min(boomPower, ConfigUtil.getFloat(MainConfig.get(), "protection/reactorExplosionPowerLimit"));
        for (EnumFacing dir : EnumFacing.VALUES) {
            TileEntity target = this.worldObj.getTileEntity(this.pos.offset(dir));
            if (!(target instanceof TileEntityReactorChamberElectric)) continue;
            this.worldObj.setBlockToAir(target.getPos());
        }
        this.worldObj.setBlockToAir(this.pos);
        ExplosionIC2 explosion = new ExplosionIC2(this.worldObj, null, this.pos, boomPower, 0.01f, ExplosionIC2.Type.Nuclear);
        explosion.doExplosion();
    }

    @Override
    public void addEmitHeat(int heat) {
        this.EmitHeatbuffer += heat;
    }

    @Override
    public int getMaxHeat() {
        return this.maxHeat;
    }

    @Override
    public void setMaxHeat(int newMaxHeat) {
        this.maxHeat = newMaxHeat;
    }

    @Override
    public float getHeatEffectModifier() {
        return this.hem;
    }

    @Override
    public void setHeatEffectModifier(float newHEM) {
        this.hem = newHEM;
    }

    @Override
    public float getReactorEnergyOutput() {
        return this.output;
    }

    @Override
    public float addOutput(float energy) {
        return this.output += energy;
    }

    private RecipeOutput processInputSlot(boolean simulate) {
        MutableObject output;
        if (!this.coolantinputSlot.isEmpty() && this.coolantinputSlot.transferToTank((IFluidTank)this.inputTank, output = new MutableObject(), simulate) && (output.getValue() == null || this.coolantoutputSlot.canAdd((ItemStack)output.getValue()))) {
            if (output.getValue() == null) {
                return new RecipeOutput(null, new ItemStack[0]);
            }
            return new RecipeOutput(null, (ItemStack)output.getValue());
        }
        return null;
    }

    private RecipeOutput processOutputSlot(boolean simulate) {
        MutableObject output;
        if (!this.hotcoolinputSlot.isEmpty() && this.hotcoolinputSlot.transferFromTank((IFluidTank)this.outputTank, output = new MutableObject(), simulate) && (output.getValue() == null || this.hotcoolantoutputSlot.canAdd((ItemStack)output.getValue()))) {
            if (output.getValue() == null) {
                return new RecipeOutput(null, new ItemStack[0]);
            }
            return new RecipeOutput(null, (ItemStack)output.getValue());
        }
        return null;
    }

    @Override
    public boolean isFluidCooled() {
        return this.fluidCooled;
    }

    private void createChamberRedstoneLinks() {
        for (EnumFacing facing : EnumFacing.VALUES) {
            BlockPos cPos = this.pos.offset(facing);
            TileEntity te = this.worldObj.getTileEntity(cPos);
            if (!(te instanceof TileEntityReactorChamberElectric)) continue;
            TileEntityReactorChamberElectric chamber = (TileEntityReactorChamberElectric)te;
            chamber.redstone.linkTo(this.redstone);
        }
    }

    private void createCasingRedstoneLinks() {
        WorldSearchUtil.findTileEntities(this.worldObj, this.pos, 2, new WorldSearchUtil.ITileEntityResultHandler(){

            @Override
            public boolean onMatch(TileEntity te) {
                if (te instanceof TileEntityReactorRedstonePort) {
                    ((TileEntityReactorRedstonePort)te).redstone.linkTo(TileEntityNuclearReactorElectric.this.redstone);
                }
                return false;
            }
        });
    }

    private void removeCasingRedstoneLinks() {
        for (Redstone rs : this.redstone.getLinkedOrigins()) {
            if (!(rs.getParent() instanceof TileEntityReactorRedstonePort)) continue;
            rs.unlinkOutbound();
        }
    }

    private void enableFluidMode() {
        if (this.addedToEnergyNet) {
            MinecraftForge.EVENT_BUS.post((Event)new EnergyTileUnloadEvent(this));
            this.addedToEnergyNet = false;
        }
        this.createCasingRedstoneLinks();
    }

    private void disableFluidMode() {
        if (!this.addedToEnergyNet) {
            this.refreshChambers();
            MinecraftForge.EVENT_BUS.post((Event)new EnergyTileLoadEvent(this));
            this.addedToEnergyNet = true;
        }
        this.removeCasingRedstoneLinks();
    }

    private boolean isFluidReactor() {
        if (!this.isFullSize()) {
            return false;
        }
        if (!this.hasFluidChamber()) {
            return false;
        }
        int range = 2;
        final MutableBoolean foundConflict = new MutableBoolean();
        WorldSearchUtil.findTileEntities(this.worldObj, this.pos, 4, new WorldSearchUtil.ITileEntityResultHandler(){

            @Override
            public boolean onMatch(TileEntity te) {
                if (!(te instanceof TileEntityNuclearReactorElectric)) {
                    return false;
                }
                if (te == TileEntityNuclearReactorElectric.this) {
                    return false;
                }
                TileEntityNuclearReactorElectric reactor = (TileEntityNuclearReactorElectric)te;
                if (reactor.isFullSize() && reactor.hasFluidChamber()) {
                    foundConflict.setTrue();
                    return true;
                }
                return false;
            }
        });
        return foundConflict.getValue() == false;
    }

    private boolean hasFluidChamber() {
        int x;
        int i;
        int y;
        int range = 2;
        ChunkCache cache = new ChunkCache(this.worldObj, this.pos.add(-2, -2, -2), this.pos.add(2, 2, 2), 0);
        BlockPos.MutableBlockPos cPos = new BlockPos.MutableBlockPos();
        for (i = 0; i < 2; ++i) {
            int y2 = this.pos.getY() + 2 * (i * 2 - 1);
            for (int z = this.pos.getZ() - 2; z <= this.pos.getZ() + 2; ++z) {
                for (x = this.pos.getX() - 2; x <= this.pos.getX() + 2; ++x) {
                    cPos.setPos(x, y2, z);
                    if (TileEntityNuclearReactorElectric.isFluidChamberBlock((IBlockAccess)cache, (BlockPos)cPos)) continue;
                    return false;
                }
            }
        }
        for (i = 0; i < 2; ++i) {
            int z = this.pos.getZ() + 2 * (i * 2 - 1);
            for (y = this.pos.getY() - 2 + 1; y <= this.pos.getY() + 2 - 1; ++y) {
                for (x = this.pos.getX() - 2; x <= this.pos.getX() + 2; ++x) {
                    cPos.setPos(x, y, z);
                    if (TileEntityNuclearReactorElectric.isFluidChamberBlock((IBlockAccess)cache, (BlockPos)cPos)) continue;
                    return false;
                }
            }
        }
        for (i = 0; i < 2; ++i) {
            int x2 = this.pos.getX() + 2 * (i * 2 - 1);
            for (y = this.pos.getY() - 2 + 1; y <= this.pos.getY() + 2 - 1; ++y) {
                for (int z = this.pos.getZ() - 2 + 1; z <= this.pos.getZ() + 2 - 1; ++z) {
                    cPos.setPos(x2, y, z);
                    if (TileEntityNuclearReactorElectric.isFluidChamberBlock((IBlockAccess)cache, (BlockPos)cPos)) continue;
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean isFluidChamberBlock(IBlockAccess world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        if (state == BlockName.resource.getBlockState(ResourceBlock.reactor_vessel)) {
            return true;
        }
        if (state.getBlock() != BlockName.te.getInstance()) {
            return false;
        }
        TileEntity te = world.getTileEntity(pos);
        if (te == null) {
            return false;
        }
        return te instanceof TileEntityReactorAccessHatch || te instanceof TileEntityReactorFluidPort || te instanceof TileEntityReactorRedstonePort;
    }

    @Override
    public double getGuiValue(String name) {
        if ("heat".equals(name)) {
            return this.maxHeat == 0 ? 0.0 : (double)this.heat / (double)this.maxHeat;
        }
        throw new IllegalArgumentException("Invalid value: " + name);
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

    public FluidTank getinputtank() {
        return this.inputTank;
    }

    public FluidTank getoutputtank() {
        return this.outputTank;
    }

    public FluidTankInfo[] getTankInfo(EnumFacing from) {
        return new FluidTankInfo[]{this.inputTank.getInfo(), this.outputTank.getInfo()};
    }

    public boolean canFill(EnumFacing from, Fluid fluid) {
        if (!this.fluidCooled) {
            return false;
        }
        return fluid == FluidName.coolant.getInstance();
    }

    public boolean canDrain(EnumFacing from, Fluid fluid) {
        if (!this.fluidCooled) {
            return false;
        }
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
    public int getInventoryStackLimit() {
        return 1;
    }

}

