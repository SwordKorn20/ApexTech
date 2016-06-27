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
package ic2.core.block.generator.tileentity;

import ic2.core.ContainerBase;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.generator.tileentity.TileEntityBaseGenerator;
import ic2.core.block.invslot.InvSlotConsumableFuel;
import ic2.core.block.machine.tileentity.TileEntityIronFurnace;
import ic2.core.gui.dynamic.DynamicContainer;
import ic2.core.gui.dynamic.DynamicGui;
import ic2.core.gui.dynamic.GuiParser;
import ic2.core.gui.dynamic.IGuiValueProvider;
import ic2.core.init.MainConfig;
import ic2.core.network.GuiSynced;
import ic2.core.ref.TeBlock;
import ic2.core.util.ConfigUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityGenerator
extends TileEntityBaseGenerator
implements IGuiValueProvider {
    public final InvSlotConsumableFuel fuelSlot;
    @GuiSynced
    public int totalFuel = 0;

    public TileEntityGenerator() {
        super(Math.round(10.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/generator")), 1, 4000);
        this.fuelSlot = new InvSlotConsumableFuel(this, "fuel", 1, false);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    protected void updateEntityClient() {
        super.updateEntityClient();
        if (this.getActive()) {
            TileEntityIronFurnace.showFlames(this.worldObj, this.pos, this.getFacing());
        }
    }

    public double getFuelRatio() {
        if (this.fuel <= 0) {
            return 0.0;
        }
        return (double)this.fuel / (double)this.totalFuel;
    }

    @Override
    public boolean gainFuel() {
        int fuelValue = this.fuelSlot.consumeFuel() / 4;
        if (fuelValue == 0) {
            return false;
        }
        this.fuel += fuelValue;
        this.totalFuel = fuelValue;
        return true;
    }

    @Override
    public boolean isConverting() {
        return this.fuel > 0;
    }

    @Override
    public String getOperationSoundFile() {
        return "Generators/GeneratorLoop.ogg";
    }

    public ContainerBase<TileEntityGenerator> getGuiContainer(EntityPlayer player) {
        return DynamicContainer.create(this, player, GuiParser.parse(this.teBlock));
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return DynamicGui.create(this, player, GuiParser.parse(this.teBlock));
    }

    @Override
    public double getGuiValue(String name) {
        if ("fuel".equals(name)) {
            return this.getFuelRatio();
        }
        throw new IllegalArgumentException();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.totalFuel = nbt.getInteger("totalFuel");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("totalFuel", this.totalFuel);
        return nbt;
    }
}

