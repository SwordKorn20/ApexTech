/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.machine.tileentity;

import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.Energy;
import ic2.core.block.comp.Redstone;
import ic2.core.block.comp.TileEntityComponent;
import ic2.core.block.invslot.InvSlotDischarge;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.container.ContainerMagnetizer;
import ic2.core.block.machine.gui.GuiMagnetizer;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import ic2.core.block.machine.tileentity.TileEntityStandardMachine;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.UpgradableProperty;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityMagnetizer
extends TileEntityElectricMachine
implements IHasGui,
IUpgradableBlock {
    public InvSlotUpgrade upgradeSlot;
    public static final int defaultMaxEnergy = 100;
    public static final int defaultTier = 1;
    private static final double boostEnergy = 2.0;
    protected final Redstone redstone;

    public TileEntityMagnetizer() {
        super(100, 1);
        this.upgradeSlot = new InvSlotUpgrade(this, "upgrade", 4);
        this.redstone = this.addComponent(new Redstone(this));
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (!this.worldObj.isRemote) {
            this.setOverclockRates();
        }
    }

    public void setOverclockRates() {
        this.upgradeSlot.onChanged();
        this.energy.setSinkTier(TileEntityStandardMachine.applyModifier(1, this.upgradeSlot.extraTier, 1.0));
        this.energy.setCapacity(TileEntityStandardMachine.applyModifier(100, this.upgradeSlot.extraEnergyStorage, this.upgradeSlot.energyStorageMultiplier));
        this.dischargeSlot.setTier(this.energy.getSinkTier());
    }

    private int distance() {
        return 20 + this.upgradeSlot.augmentation;
    }

    @Override
    public ContainerBase<?> getGuiContainer(EntityPlayer player) {
        return new ContainerMagnetizer(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiMagnetizer(new ContainerMagnetizer(player, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }

    @Override
    public double getEnergy() {
        return this.energy.getEnergy();
    }

    @Override
    public boolean useEnergy(double amount) {
        return this.energy.useEnergy(amount);
    }

    public boolean canBoost() {
        return this.energy.getEnergy() >= 2.0;
    }

    public void boost(double multiplier) {
        this.energy.useEnergy(2.0 * multiplier);
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.Augmentable, UpgradableProperty.RedstoneSensitive, UpgradableProperty.Transformer, UpgradableProperty.EnergyStorage);
    }
}

