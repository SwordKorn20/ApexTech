/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.wiring;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.IEnergyNet;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.Energy;
import ic2.core.block.comp.TileEntityComponent;
import ic2.core.block.wiring.ContainerTransformer;
import ic2.core.block.wiring.GuiTransformer;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileEntityTransformer
extends TileEntityInventory
implements IHasGui,
INetworkClientTileEntityEventListener {
    private double inputflow = 0.0;
    private double outputflow = 0.0;
    private final int defaultTier;
    protected final Energy energy;
    public Mode redstoneMode = Mode.redstone;
    public Mode transformMode = Mode.notset;

    public TileEntityTransformer(int tier) {
        this.defaultTier = tier;
        this.energy = this.addComponent(new Energy(this, EnergyNet.instance.getPowerFromTier(tier) * 8.0, Collections.<EnumFacing>emptySet(), Collections.<EnumFacing>emptySet(), tier, tier, true));
    }

    public String getType() {
        switch (this.energy.getSourceTier()) {
            case 1: {
                return "LV";
            }
            case 2: {
                return "MV";
            }
            case 3: {
                return "HV";
            }
            case 4: {
                return "EV";
            }
        }
        return "";
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.redstoneMode = Mode.values()[nbt.getInteger("mode")];
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("mode", this.redstoneMode.ordinal());
        return nbt;
    }

    @Override
    public void onLoaded() {
        super.onLoaded();
        if (!this.worldObj.isRemote) {
            this.updateRedstone();
        }
    }

    public Mode getMode() {
        return this.redstoneMode;
    }

    @Override
    public void onNetworkEvent(EntityPlayer player, int event) {
        if (event >= Mode.values().length || event < 0) {
            return;
        }
        this.redstoneMode = Mode.values()[event];
        this.updateRedstone();
        this.outputflow = EnergyNet.instance.getPowerFromTier(this.energy.getSourceTier());
        this.inputflow = EnergyNet.instance.getPowerFromTier(this.energy.getSinkTier());
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        this.updateRedstone();
    }

    public void updateRedstone() {
        Mode newMode;
        switch (this.redstoneMode) {
            case redstone: {
                newMode = this.worldObj.isBlockPowered(this.pos) ? Mode.stepup : Mode.stepdown;
                break;
            }
            case stepdown: 
            case stepup: {
                newMode = this.redstoneMode;
                break;
            }
            default: {
                return;
            }
        }
        this.energy.setEnabled(true);
        if (!this.transformMode.valid || newMode != this.transformMode) {
            this.transformMode = newMode;
            this.setActive(this.transformMode.enabled);
            if (this.transformMode.enabled) {
                this.energy.setDirections(EnumSet.complementOf(EnumSet.of(this.getFacing())), EnumSet.of(this.getFacing()));
                this.energy.setSourceTier(this.defaultTier + 1);
                this.energy.setSinkTier(this.defaultTier);
            } else {
                this.energy.setDirections(EnumSet.of(this.getFacing()), EnumSet.complementOf(EnumSet.of(this.getFacing())));
                this.energy.setSourceTier(this.defaultTier);
                this.energy.setSinkTier(this.defaultTier + 1);
            }
        }
    }

    @Override
    public void setFacing(EnumFacing facing) {
        super.setFacing(facing);
        switch (this.transformMode) {
            case stepdown: {
                this.energy.setDirections(EnumSet.of(this.getFacing()), EnumSet.complementOf(EnumSet.of(this.getFacing())));
                break;
            }
            case stepup: {
                this.energy.setDirections(EnumSet.complementOf(EnumSet.of(this.getFacing())), EnumSet.of(this.getFacing()));
                break;
            }
        }
    }

    public ContainerBase<TileEntityTransformer> getGuiContainer(EntityPlayer player) {
        return new ContainerTransformer(player, this, 219);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiTransformer(new ContainerTransformer(player, this, 219));
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }

    public double getinputflow() {
        if (!this.transformMode.enabled) {
            return this.inputflow;
        }
        return this.outputflow;
    }

    public double getoutputflow() {
        if (this.transformMode.enabled) {
            return this.inputflow;
        }
        return this.outputflow;
    }

    public static enum Mode {
        redstone(false, false),
        stepdown(true, false),
        stepup(true, true),
        notset(false, false);
        
        private final boolean valid;
        private final boolean enabled;

        private Mode(boolean valid, boolean enabled) {
            this.valid = valid;
            this.enabled = enabled;
        }
    }

}

