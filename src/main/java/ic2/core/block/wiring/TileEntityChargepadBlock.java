/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.particle.Particle
 *  net.minecraft.client.particle.ParticleManager
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.client.FMLClientHandler
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.wiring;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.IElectricItemManager;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.Platform;
import ic2.core.block.comp.Energy;
import ic2.core.block.wiring.ContainerChargepadBlock;
import ic2.core.block.wiring.GuiChargepadBlock;
import ic2.core.block.wiring.TileEntityElectricBlock;
import ic2.core.ref.IItemModelProvider;
import ic2.core.ref.ItemName;
import ic2.core.util.EntityIC2FX;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class TileEntityChargepadBlock
extends TileEntityElectricBlock
implements IHasGui {
    private static final List<AxisAlignedBB> aabbs = Arrays.asList(new AxisAlignedBB[]{new AxisAlignedBB(0.0, 0.0, 0.0, 1.0, 0.9375, 1.0)});
    private int updateTicker;
    private EntityPlayer player = null;
    public static byte redstoneModes = 2;

    public TileEntityChargepadBlock(int tier1, int output1, int maxStorage1) {
        super(tier1, output1, maxStorage1);
        this.updateTicker = IC2.random.nextInt(this.getTickRate());
    }

    @Override
    protected List<AxisAlignedBB> getAabbs(boolean forCollision) {
        return aabbs;
    }

    @Override
    protected void onEntityCollision(Entity entity) {
        super.onEntityCollision(entity);
        if (!this.worldObj.isRemote && entity instanceof EntityPlayer) {
            this.updatePlayer((EntityPlayer)entity);
        }
    }

    private void updatePlayer(EntityPlayer entity) {
        this.player = entity;
    }

    protected int getTickRate() {
        return 2;
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        boolean needsInvUpdate = false;
        if (this.updateTicker++ % this.getTickRate() != 0) {
            return;
        }
        if (this.player != null && this.energy.getEnergy() >= 1.0) {
            if (!this.getActive()) {
                this.setActive(true);
            }
            this.getItems(this.player);
            this.player = null;
            needsInvUpdate = true;
        } else if (this.getActive()) {
            this.setActive(false);
            needsInvUpdate = true;
        }
        if (needsInvUpdate) {
            this.markDirty();
        }
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    protected void updateEntityClient() {
        super.updateEntityClient();
        Random rnd = this.worldObj.rand;
        if (rnd.nextInt(8) != 0) {
            return;
        }
        if (this.getActive()) {
            ParticleManager effect = FMLClientHandler.instance().getClient().effectRenderer;
            for (int particles = 20; particles > 0; --particles) {
                double x = (float)this.pos.getX() + 0.0f + rnd.nextFloat();
                double y = (float)this.pos.getY() + 0.9f + rnd.nextFloat();
                double z = (float)this.pos.getZ() + 0.0f + rnd.nextFloat();
                effect.addEffect((Particle)new EntityIC2FX(this.worldObj, x, y, z, 60, new double[]{0.0, 0.1, 0.0}, new float[]{0.2f, 0.2f, 1.0f}));
            }
        }
    }

    protected abstract void getItems(EntityPlayer var1);

    @Override
    protected boolean shouldEmitRedstone() {
        return this.redstoneMode == 0 && this.getActive() || this.redstoneMode == 1 && !this.getActive();
    }

    @Override
    public void setFacing(EnumFacing facing) {
        this.energy.setDirections(EnumSet.complementOf(EnumSet.of(facing, EnumFacing.UP)), EnumSet.of(facing));
        super.setFacing(facing);
    }

    public ContainerBase<TileEntityChargepadBlock> getGuiContainer(EntityPlayer player) {
        return new ContainerChargepadBlock(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiChargepadBlock(new ContainerChargepadBlock(player, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }

    @Override
    public void onNetworkEvent(EntityPlayer player, int event) {
        this.redstoneMode = (byte)(this.redstoneMode + 1);
        if (this.redstoneMode >= redstoneModes) {
            this.redstoneMode = 0;
        }
        IC2.platform.messagePlayer(player, this.getRedstoneMode(), new Object[0]);
    }

    @Override
    public String getRedstoneMode() {
        if (this.redstoneMode > 1 || this.redstoneMode < 0) {
            return "";
        }
        return "ic2.blockChargepad.gui.mod.redstone" + this.redstoneMode;
    }

    protected void chargeItem(ItemStack stack, int chargeFactor) {
        if (!(stack.getItem() instanceof IElectricItem)) {
            return;
        }
        if (stack.getItem() == ItemName.debug_item.getInstance()) {
            return;
        }
        double freeAmount = ElectricItem.manager.charge(stack, Double.POSITIVE_INFINITY, this.energy.getSourceTier(), true, true);
        double charge = 0.0;
        if (freeAmount >= 0.0) {
            charge = freeAmount >= (double)(chargeFactor * this.getTickRate()) ? (double)(chargeFactor * this.getTickRate()) : freeAmount;
            if (this.energy.getEnergy() < charge) {
                charge = this.energy.getEnergy();
            }
            this.energy.useEnergy(ElectricItem.manager.charge(stack, charge, this.energy.getSourceTier(), true, false));
        }
    }
}

