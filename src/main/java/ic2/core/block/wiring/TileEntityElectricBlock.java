/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.wiring;

import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.api.tile.IEnergyStorage;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.Platform;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.Energy;
import ic2.core.block.comp.RedstoneEmitter;
import ic2.core.block.comp.TileEntityComponent;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotCharge;
import ic2.core.block.invslot.InvSlotDischarge;
import ic2.core.block.wiring.ContainerElectricBlock;
import ic2.core.block.wiring.GuiElectricBlock;
import ic2.core.init.Localization;
import ic2.core.init.MainConfig;
import ic2.core.ref.TeBlock;
import ic2.core.util.ConfigUtil;
import ic2.core.util.StackUtil;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileEntityElectricBlock
extends TileEntityInventory
implements IHasGui,
INetworkClientTileEntityEventListener,
IEnergyStorage {
    public boolean hasRedstone = false;
    protected double output;
    protected final int tier;
    public byte redstoneMode = 0;
    public static byte redstoneModes = 7;
    public final InvSlotCharge chargeSlot;
    public final InvSlotDischarge dischargeSlot;
    public final Energy energy;
    public final RedstoneEmitter rsEmitter;

    public TileEntityElectricBlock(int tier, int output, int maxStorage) {
        this.output = output;
        this.chargeSlot = new InvSlotCharge(this, tier);
        this.dischargeSlot = new InvSlotDischarge(this, InvSlot.Access.IO, tier, InvSlot.InvSide.BOTTOM);
        this.tier = tier;
        this.energy = this.addComponent(new Energy(this, maxStorage, EnumSet.complementOf(EnumSet.of(EnumFacing.DOWN)), EnumSet.of(EnumFacing.DOWN), this.tier, tier, true).addManagedSlot(this.chargeSlot).addManagedSlot(this.dischargeSlot));
        this.rsEmitter = this.addComponent(new RedstoneEmitter(this));
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.energy.setDirections(EnumSet.complementOf(EnumSet.of(this.getFacing())), EnumSet.of(this.getFacing()));
        this.redstoneMode = nbt.getByte("redstoneMode");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setByte("redstoneMode", this.redstoneMode);
        return nbt;
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        if (this.redstoneMode == 5 || this.redstoneMode == 6) {
            this.hasRedstone = this.worldObj.isBlockPowered(this.pos);
        }
        this.updateTier();
        this.rsEmitter.setLevel(this.shouldEmitRedstone() ? 15 : 0);
    }

    public ContainerBase<? extends TileEntityElectricBlock> getGuiContainer(EntityPlayer player) {
        return new ContainerElectricBlock(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiElectricBlock(new ContainerElectricBlock(player, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }

    @Override
    public void setFacing(EnumFacing facing) {
        super.setFacing(facing);
        this.energy.setDirections(EnumSet.complementOf(EnumSet.of(this.getFacing())), EnumSet.of(this.getFacing()));
    }

    protected boolean shouldEmitRedstone() {
        switch (this.redstoneMode) {
            case 1: {
                return this.energy.getEnergy() >= this.energy.getCapacity() - this.output * 20.0;
            }
            case 2: {
                return this.energy.getEnergy() > this.output && this.energy.getEnergy() < this.energy.getCapacity();
            }
            case 3: {
                return this.energy.getEnergy() > this.output || this.energy.getEnergy() < this.energy.getCapacity();
            }
            case 4: {
                return this.energy.getEnergy() < this.output;
            }
        }
        return false;
    }

    protected void updateTier() {
        switch (this.redstoneMode) {
            case 5: {
                if (this.hasRedstone) {
                    this.energy.setSourceTier(-1);
                    break;
                }
            }
            case 6: {
                if (this.hasRedstone && this.getStored() < this.getCapacity()) {
                    this.energy.setSourceTier(-1);
                    break;
                }
            }
            default: {
                this.energy.setSourceTier(this.tier);
            }
        }
    }

    @Override
    public void onNetworkEvent(EntityPlayer player, int event) {
        this.redstoneMode = (byte)(this.redstoneMode + 1);
        if (this.redstoneMode >= redstoneModes) {
            this.redstoneMode = 0;
        }
        IC2.platform.messagePlayer(player, this.getRedstoneMode(), new Object[0]);
    }

    public String getRedstoneMode() {
        if (this.redstoneMode >= redstoneModes || this.redstoneMode < 0) {
            return "";
        }
        return Localization.translate("ic2.EUStorage.gui.mod.redstone" + this.redstoneMode);
    }

    @Override
    protected int getComparatorInputOverride() {
        return this.energy.getComparatorValue();
    }

    @Override
    public void onPlaced(ItemStack stack, EntityLivingBase placer, EnumFacing facing) {
        super.onPlaced(stack, placer, facing);
        if (!this.worldObj.isRemote) {
            NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
            this.energy.addEnergy(nbt.getDouble("energy"));
        }
    }

    @Override
    protected ItemStack adjustDrop(ItemStack drop, boolean wrench) {
        drop = super.adjustDrop(drop, wrench);
        if (wrench || this.teBlock.defaultDrop == TeBlock.DefaultDrop.Self) {
            double retainedRatio = ConfigUtil.getDouble(MainConfig.get(), "balance/energyRetainedInStorageBlockDrops");
            double totalEnergy = this.energy.getEnergy();
            if (retainedRatio > 0.0 && totalEnergy > 0.0) {
                NBTTagCompound nbt = StackUtil.getOrCreateNbtData(drop);
                nbt.setDouble("energy", totalEnergy * retainedRatio);
            }
        }
        return drop;
    }

    @Override
    public int getOutput() {
        return (int)this.output;
    }

    @Override
    public double getOutputEnergyUnitsPerTick() {
        return this.output;
    }

    @Override
    public void setStored(int energy) {
    }

    @Override
    public int addEnergy(int amount) {
        this.energy.addEnergy(amount);
        return amount;
    }

    @Override
    public int getStored() {
        return (int)this.energy.getEnergy();
    }

    @Override
    public int getCapacity() {
        return (int)this.energy.getCapacity();
    }

    @Override
    public boolean isTeleporterCompatible(EnumFacing side) {
        return true;
    }
}

