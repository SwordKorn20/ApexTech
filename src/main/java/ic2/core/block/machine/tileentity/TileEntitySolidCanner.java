/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.machine.tileentity;

import ic2.api.recipe.RecipeOutput;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.audio.AudioManager;
import ic2.core.audio.AudioSource;
import ic2.core.block.invslot.InvSlotConsumableSolidCanner;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotProcessable;
import ic2.core.block.invslot.InvSlotProcessableSolidCanner;
import ic2.core.block.machine.container.ContainerSolidCanner;
import ic2.core.block.machine.gui.GuiSolidCanner;
import ic2.core.block.machine.tileentity.TileEntityStandardMachine;
import ic2.core.upgrade.UpgradableProperty;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntitySolidCanner
extends TileEntityStandardMachine {
    public final InvSlotConsumableSolidCanner canInputSlot;

    public TileEntitySolidCanner() {
        super(2, 200, 4);
        this.inputSlot = new InvSlotProcessableSolidCanner(this, "input", 1);
        this.canInputSlot = new InvSlotConsumableSolidCanner(this, "canInput", 1);
    }

    @Override
    public RecipeOutput getOutput() {
        if (this.inputSlot.isEmpty()) {
            return null;
        }
        RecipeOutput output = this.inputSlot.process();
        if (output == null) {
            return null;
        }
        if (!this.outputSlot.canAdd(output.items)) {
            return null;
        }
        return output;
    }

    public ContainerBase<TileEntitySolidCanner> getGuiContainer(EntityPlayer player) {
        return new ContainerSolidCanner(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiSolidCanner(new ContainerSolidCanner(player, this));
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

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.Processing, UpgradableProperty.Transformer, UpgradableProperty.EnergyStorage, UpgradableProperty.ItemConsuming, UpgradableProperty.ItemProducing);
    }
}

