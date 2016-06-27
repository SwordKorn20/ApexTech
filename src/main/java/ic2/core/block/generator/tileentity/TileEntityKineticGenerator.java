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

import ic2.api.energy.EnergyNet;
import ic2.api.energy.IEnergyNet;
import ic2.api.energy.tile.IKineticSource;
import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.Energy;
import ic2.core.block.comp.TileEntityComponent;
import ic2.core.block.generator.container.ContainerKineticGenerator;
import ic2.core.block.generator.gui.GuiKineticGenerator;
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

public class TileEntityKineticGenerator
extends TileEntityInventory
implements IHasGui {
    protected final Energy energy;
    private double production = 0.0;
    private int receivedkinetic = 0;
    private final double productionpeerkineticunit;

    public TileEntityKineticGenerator() {
        this.energy = this.addComponent(Energy.asBasicSource(this, EnergyNet.instance.getPowerFromTier(3) * 8.0, 3));
        this.productionpeerkineticunit = 0.25 * (double)ConfigUtil.getFloat(MainConfig.get(), "balance/energy/generator/Kinetic");
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        boolean newActive = this.gainEnergy();
        if (this.getActive() != newActive) {
            this.setActive(newActive);
        }
    }

    protected boolean gainEnergy() {
        EnumFacing dir = this.getFacing();
        TileEntity te = this.worldObj.getTileEntity(this.pos.offset(dir));
        if (te instanceof IKineticSource) {
            int kineticbandwith = ((IKineticSource)te).maxrequestkineticenergyTick(dir.getOpposite());
            this.receivedkinetic = ((IKineticSource)te).requestkineticenergy(dir.getOpposite(), kineticbandwith);
            if (this.receivedkinetic != 0) {
                this.production = (double)this.receivedkinetic * this.productionpeerkineticunit;
                this.energy.addEnergy(this.production);
                return true;
            }
        }
        this.production = 0.0;
        this.receivedkinetic = 0;
        return false;
    }

    public ContainerBase<TileEntityKineticGenerator> getGuiContainer(EntityPlayer player) {
        return new ContainerKineticGenerator(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiKineticGenerator(new ContainerKineticGenerator(player, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }

    public double getproduction() {
        return this.production;
    }
}

