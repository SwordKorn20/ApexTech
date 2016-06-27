/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.generator.tileentity;

import ic2.api.energy.tile.IHeatSource;
import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.Energy;
import ic2.core.block.comp.TileEntityComponent;
import ic2.core.block.generator.container.ContainerStirlingGenerator;
import ic2.core.block.generator.gui.GuiStirlingGenerator;
import ic2.core.init.MainConfig;
import ic2.core.util.ConfigUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityStirlingGenerator
extends TileEntityInventory
implements IHasGui {
    protected final Energy energy;
    public double production = 0.0;
    public int receivedheat = 0;
    public final double productionpeerheat;

    public TileEntityStirlingGenerator() {
        this.energy = this.addComponent(Energy.asBasicSource(this, 30000.0, 2));
        this.productionpeerheat = 0.5f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/Stirling");
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        boolean needsInvUpdate = false;
        boolean newActive = this.gainEnergy();
        if (needsInvUpdate) {
            this.markDirty();
        }
        if (this.getActive() != newActive) {
            this.setActive(newActive);
        }
    }

    protected boolean gainEnergy() {
        EnumFacing dir = this.getFacing();
        TileEntity te = this.worldObj.getTileEntity(this.pos.offset(dir));
        if (te instanceof IHeatSource) {
            int heatbandwith = ((IHeatSource)te).maxrequestHeatTick(dir.getOpposite());
            double freeEUstorage = this.energy.getFreeEnergy();
            if (freeEUstorage >= this.productionpeerheat * (double)heatbandwith) {
                this.receivedheat = ((IHeatSource)te).requestHeat(dir.getOpposite(), heatbandwith);
                if (this.receivedheat != 0) {
                    this.production = (double)this.receivedheat * this.productionpeerheat;
                    this.energy.addEnergy(this.production);
                    return true;
                }
            }
        }
        this.production = 0.0;
        this.receivedheat = 0;
        return false;
    }

    public ContainerBase<TileEntityStirlingGenerator> getGuiContainer(EntityPlayer player) {
        return new ContainerStirlingGenerator(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiStirlingGenerator(new ContainerStirlingGenerator(player, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }
}

