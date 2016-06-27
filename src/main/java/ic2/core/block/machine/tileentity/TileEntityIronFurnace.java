/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumFacing$Axis
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.machine.tileentity;

import ic2.api.recipe.RecipeOutput;
import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlotConsumableFuel;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotProcessable;
import ic2.core.block.invslot.InvSlotProcessableSmelting;
import ic2.core.gui.dynamic.DynamicContainer;
import ic2.core.gui.dynamic.DynamicGui;
import ic2.core.gui.dynamic.GuiParser;
import ic2.core.gui.dynamic.IGuiValueProvider;
import ic2.core.network.GuiSynced;
import ic2.core.ref.TeBlock;
import java.util.List;
import java.util.Random;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityIronFurnace
extends TileEntityInventory
implements IHasGui,
IGuiValueProvider {
    public final InvSlotProcessable inputSlot;
    public final InvSlotOutput outputSlot;
    public final InvSlotConsumableFuel fuelSlot;
    @GuiSynced
    public int fuel = 0;
    @GuiSynced
    public int totalFuel = 0;
    @GuiSynced
    public short progress = 0;
    public final short operationLength = 160;

    public TileEntityIronFurnace() {
        this.inputSlot = new InvSlotProcessableSmelting(this, "input", 1);
        this.outputSlot = new InvSlotOutput(this, "output", 1);
        this.fuelSlot = new InvSlotConsumableFuel(this, "fuel", 1, true);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.fuel = nbt.getInteger("fuel");
        this.totalFuel = nbt.getInteger("totalFuel");
        this.progress = nbt.getShort("progress");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("fuel", this.fuel);
        nbt.setInteger("totalFuel", this.totalFuel);
        nbt.setShort("progress", this.progress);
        return nbt;
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        boolean needsInvUpdate = false;
        if (this.fuel <= 0 && this.canOperate()) {
            this.fuel = this.totalFuel = this.fuelSlot.consumeFuel();
            if (this.fuel > 0) {
                needsInvUpdate = true;
            }
        }
        if (this.fuel > 0 && this.canOperate()) {
            this.progress = (short)(this.progress + 1);
            if (this.progress >= 160) {
                this.progress = 0;
                this.operate();
                needsInvUpdate = true;
            }
        } else {
            this.progress = 0;
        }
        if (this.fuel > 0) {
            --this.fuel;
            this.setActive(true);
        } else {
            this.setActive(false);
        }
        if (needsInvUpdate) {
            this.markDirty();
        }
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    protected void updateEntityClient() {
        super.updateEntityClient();
        if (this.getActive()) {
            TileEntityIronFurnace.showFlames(this.worldObj, this.pos, this.getFacing());
        }
    }

    public static void showFlames(World world, BlockPos pos, EnumFacing facing) {
        if (world.rand.nextInt(8) != 0) {
            return;
        }
        double width = 0.625;
        double height = 0.375;
        double depthOffset = 0.02;
        double x = (double)pos.getX() + ((double)facing.getFrontOffsetX() * 1.04 + 1.0) / 2.0;
        double y = (double)pos.getY() + (double)world.rand.nextFloat() * 0.375;
        double z = (double)pos.getZ() + ((double)facing.getFrontOffsetZ() * 1.04 + 1.0) / 2.0;
        if (facing.getAxis() == EnumFacing.Axis.X) {
            z += (double)world.rand.nextFloat() * 0.625 - 0.3125;
        } else {
            x += (double)world.rand.nextFloat() * 0.625 - 0.3125;
        }
        world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0.0, 0.0, 0.0, new int[0]);
        world.spawnParticle(EnumParticleTypes.FLAME, x, y, z, 0.0, 0.0, 0.0, new int[0]);
    }

    private void operate() {
        this.outputSlot.add(this.inputSlot.process().items);
        this.inputSlot.consume();
    }

    private boolean canOperate() {
        RecipeOutput output = this.inputSlot.process();
        if (output == null) {
            return false;
        }
        return this.outputSlot.canAdd(output.items);
    }

    public double getProgress() {
        return (double)this.progress / 160.0;
    }

    public double getFuelRatio() {
        if (this.fuel <= 0) {
            return 0.0;
        }
        return (double)this.fuel / (double)this.totalFuel;
    }

    public ContainerBase<TileEntityIronFurnace> getGuiContainer(EntityPlayer player) {
        return DynamicContainer.create(this, player, GuiParser.parse(this.teBlock));
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return DynamicGui.create(this, player, GuiParser.parse(this.teBlock));
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }

    @Override
    public double getGuiValue(String name) {
        if (name.equals("fuel")) {
            return this.fuel == 0 ? 0.0 : (double)this.fuel / (double)this.totalFuel;
        }
        if (name.equals("progress")) {
            return this.progress == 0 ? 0.0 : (double)this.progress / 160.0;
        }
        throw new IllegalArgumentException();
    }
}

