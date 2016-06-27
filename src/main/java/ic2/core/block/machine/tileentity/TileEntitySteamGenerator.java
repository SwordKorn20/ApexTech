/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
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
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.machine.tileentity;

import ic2.api.energy.tile.IHeatSource;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.core.ContainerBase;
import ic2.core.ExplosionIC2;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.machine.container.ContainerSteamGenerator;
import ic2.core.block.machine.gui.GuiSteamGenerator;
import ic2.core.gui.dynamic.IGuiValueProvider;
import ic2.core.init.Localization;
import ic2.core.ref.FluidName;
import ic2.core.util.BiomeUtil;
import ic2.core.util.LiquidUtil;
import java.util.Random;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntitySteamGenerator
extends TileEntityInventory
implements IHasGui,
IGuiValueProvider,
IFluidHandler,
INetworkClientTileEntityEventListener {
    private final float maxHeat = 500.0f;
    private final int maxCalcification = 100000;
    private int heatInput = 0;
    private int inputMB = 0;
    public FluidTank waterTank = new FluidTank(10000);
    private int calcification = 0;
    private int outputMB = 0;
    private outputType outputFluid = outputType.NONE;
    private float systemHeat;
    private int pressure = 0;
    private boolean newActive = false;

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.waterTank.readFromNBT(nbttagcompound.getCompoundTag("WaterTank"));
        this.inputMB = nbttagcompound.getInteger("inputmb");
        this.pressure = nbttagcompound.getInteger("pressurevalve");
        this.systemHeat = nbttagcompound.getFloat("systemheat");
        this.calcification = nbttagcompound.getInteger("calcification");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setTag("WaterTank", (NBTBase)this.waterTank.writeToNBT(new NBTTagCompound()));
        nbt.setInteger("inputmb", this.inputMB);
        nbt.setInteger("pressurevalve", this.pressure);
        nbt.setFloat("systemheat", this.systemHeat);
        nbt.setInteger("calcification", this.calcification);
        return nbt;
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        this.systemHeat = Math.max(this.systemHeat, (float)BiomeUtil.getBiomeTemperature(this.worldObj, this.pos));
        if (this.isCalcified()) {
            if (this.getActive()) {
                this.setActive(false);
            }
        } else {
            this.newActive = this.work();
            if (this.getActive() != this.newActive) {
                this.setActive(this.newActive);
            }
        }
        if (!this.getActive()) {
            this.cooldown(0.01f);
        }
    }

    private boolean work() {
        FluidStack output;
        if (this.waterTank.getFluidAmount() > 0 && this.inputMB > 0 && (output = this.getOutputfluid()) != null) {
            this.outputMB = output.amount;
            this.outputFluid = this.getOutputType(output);
            output.amount -= LiquidUtil.distribute(this, output, false);
            if (output.amount > 0) {
                if (this.outputFluid.isSteam() && this.worldObj.rand.nextInt(10) == 0) {
                    new ExplosionIC2(this.worldObj, null, this.pos, 1, 1.0f, ExplosionIC2.Type.Heat).doExplosion();
                } else {
                    this.waterTank.fill(output, true);
                }
            }
            return true;
        }
        this.outputMB = 0;
        this.outputFluid = outputType.NONE;
        this.heatInput = 0;
        return this.heatupmax();
    }

    private boolean heatupmax() {
        this.heatInput = this.requestHeat(1200);
        if (this.heatInput > 0) {
            this.heatup(this.heatInput);
            return true;
        }
        return false;
    }

    private outputType getOutputType(FluidStack fluid) {
        if (fluid.getFluid().equals((Object)FluidName.superheated_steam.getInstance())) {
            return outputType.SUPERHEATEDSTEAM;
        }
        if (fluid.getFluid().equals((Object)FluidName.steam.getInstance())) {
            return outputType.STEAM;
        }
        if (fluid.getFluid().equals((Object)FluidName.distilled_water.getInstance())) {
            return outputType.DISTILLEDWATER;
        }
        if (fluid.getFluid().equals((Object)FluidRegistry.WATER)) {
            return outputType.WATER;
        }
        return outputType.NONE;
    }

    private FluidStack getOutputfluid() {
        if (this.waterTank.getFluid() == null) {
            return null;
        }
        Fluid fluidInTank = this.waterTank.getFluid().getFluid();
        boolean cancalcification = fluidInTank.equals((Object)FluidRegistry.WATER);
        if (this.systemHeat < 100.0f) {
            this.heatupmax();
            return this.waterTank.drain(this.inputMB, true);
        }
        int hUneeded = 100 + Math.round((float)this.pressure / 220.0f * 100.0f);
        int targetTemp = (int)(100 + Math.round((double)((float)this.pressure / 220.0f * 100.0f) * 2.74));
        if (this.getSystemHeat() == (float)targetTemp) {
            int heat;
            this.heatInput = heat = this.requestHeat(this.inputMB * hUneeded);
            if (heat == this.inputMB * hUneeded) {
                if (cancalcification) {
                    ++this.calcification;
                }
                this.waterTank.drain(this.inputMB, true);
                return new FluidStack(this.systemHeat >= 374.0f ? FluidName.superheated_steam.getInstance() : FluidName.steam.getInstance(), this.inputMB * 100);
            }
            this.heatup(heat);
            return this.waterTank.drain(this.inputMB, true);
        }
        if (this.systemHeat > (float)targetTemp) {
            this.heatInput = 0;
            int count = this.inputMB;
            while (this.systemHeat > (float)targetTemp) {
                this.cooldown(0.1f);
                if (cancalcification) {
                    ++this.calcification;
                }
                if (--count != 0) continue;
            }
            this.waterTank.drain(this.inputMB - count, true);
            return new FluidStack(FluidName.steam.getInstance(), (this.inputMB - count) * 100);
        }
        this.heatupmax();
        return this.waterTank.drain(this.inputMB, true);
    }

    private void heatup(int heatinput) {
        this.systemHeat += (float)heatinput * 5.0E-4f;
        if (this.systemHeat > 500.0f) {
            this.worldObj.setBlockToAir(this.pos);
            new ExplosionIC2(this.worldObj, null, this.pos, 10, 0.01f, ExplosionIC2.Type.Heat).doExplosion();
        }
    }

    private void cooldown(float cool) {
        this.systemHeat = Math.max(this.systemHeat - cool, (float)BiomeUtil.getBiomeTemperature(this.worldObj, this.pos));
    }

    private int requestHeat(int requestHeat) {
        int targetHeat = requestHeat;
        for (EnumFacing dir : EnumFacing.VALUES) {
            int amount;
            TileEntity target = this.worldObj.getTileEntity(this.pos.offset(dir));
            if (!(target instanceof IHeatSource) || (amount = ((IHeatSource)target).requestHeat(dir.getOpposite(), targetHeat)) <= 0 || (targetHeat -= amount) != 0) continue;
            return requestHeat;
        }
        return requestHeat - targetHeat;
    }

    @Override
    public void onNetworkEvent(EntityPlayer player, int event) {
        if (event > 2000 || event < -2000) {
            if (event > 2000) {
                this.pressure = Math.min(this.pressure + (event - 2000), 300);
            }
            if (event < -2000) {
                this.pressure = Math.max(this.pressure + (event + 2000), 0);
            }
        } else {
            this.inputMB = Math.max(Math.min(this.inputMB + event, 1000), 0);
        }
    }

    public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
        return null;
    }

    public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
        return !this.canFill(from, resource.getFluid()) ? 0 : this.waterTank.fill(resource, doFill);
    }

    public int gaugeLiquidScaled(int i, int tank) {
        if (tank == 0) {
            if (this.waterTank.getFluidAmount() <= 0) {
                return 0;
            }
            return this.waterTank.getFluidAmount() * i / this.waterTank.getCapacity();
        }
        return 0;
    }

    public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
        return null;
    }

    public boolean canDrain(EnumFacing from, Fluid fluid) {
        return false;
    }

    public boolean canFill(EnumFacing from, Fluid fluid) {
        return fluid.equals((Object)FluidName.distilled_water.getInstance()) || fluid.equals((Object)FluidRegistry.WATER);
    }

    public FluidTankInfo[] getTankInfo(EnumFacing from) {
        return new FluidTankInfo[]{this.waterTank.getInfo()};
    }

    public ContainerBase<TileEntitySteamGenerator> getGuiContainer(EntityPlayer player) {
        return new ContainerSteamGenerator(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiSteamGenerator(new ContainerSteamGenerator(player, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }

    @Override
    public double getGuiValue(String name) {
        if ("heat".equals(name)) {
            return this.systemHeat == 0.0f ? 0.0 : (double)this.systemHeat / 500.0;
        }
        if ("calcification".equals(name)) {
            return this.calcification == 0 ? 0.0 : (double)this.calcification / 100000.0;
        }
        throw new IllegalArgumentException();
    }

    public int getOutputMB() {
        return this.outputMB;
    }

    public int getInputMB() {
        return this.inputMB;
    }

    public int getHeatInput() {
        return this.heatInput;
    }

    public int getPressure() {
        return this.pressure;
    }

    public float getSystemHeat() {
        return (float)Math.round(this.systemHeat * 10.0f) / 10.0f;
    }

    public float getCalcification() {
        return (float)Math.round((float)this.calcification / 100000.0f * 100.0f * 100.0f) / 100.0f;
    }

    public boolean isCalcified() {
        return this.calcification >= 100000;
    }

    public String getOutputFluidName() {
        return this.outputFluid.getName();
    }

    private static enum outputType {
        NONE(""),
        WATER(Localization.translate("ic2.SteamGenerator.output.water")),
        DISTILLEDWATER(Localization.translate("ic2.SteamGenerator.output.destiwater")),
        STEAM(Localization.translate("ic2.SteamGenerator.output.steam")),
        SUPERHEATEDSTEAM(Localization.translate("ic2.SteamGenerator.output.hotsteam"));
        
        private final String name;

        private outputType(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }

        public boolean isWater() {
            return this == WATER || this == DISTILLEDWATER;
        }

        public boolean isSteam() {
            return this == STEAM || this == SUPERHEATEDSTEAM;
        }
    }

}

