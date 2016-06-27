/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumFacing$Axis
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.ChunkCache
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.kineticgenerator.tileentity;

import ic2.api.energy.tile.IKineticSource;
import ic2.api.item.IKineticRotor;
import ic2.api.tile.IRotorProvider;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.WindSim;
import ic2.core.WorldData;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableClass;
import ic2.core.block.invslot.InvSlotConsumableKineticRotor;
import ic2.core.block.kineticgenerator.container.ContainerWindKineticGenerator;
import ic2.core.block.kineticgenerator.gui.GuiWindKineticGenerator;
import ic2.core.init.Localization;
import ic2.core.init.MainConfig;
import ic2.core.network.NetworkManager;
import ic2.core.util.ConfigUtil;
import ic2.core.util.SideGateway;
import ic2.core.util.Util;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityWindKineticGenerator
extends TileEntityInventory
implements IKineticSource,
IRotorProvider,
IHasGui {
    public final InvSlotConsumableClass rotorSlot;
    private double windStrength;
    private int obstructedCrossSection;
    private int crossSection;
    private int updateTicker;
    private float rotationSpeed;
    private float angle = 0.0f;
    private long lastcheck;
    private static final double efficiencyRollOffExponent = 2.0;
    public static final float outputModifier = 10.0f * ConfigUtil.getFloat(MainConfig.get(), "balance/energy/kineticgenerator/wind");
    private static final ResourceLocation woodenRotorTexture = new ResourceLocation(IC2.textureDomain, "textures/items/rotor/wood_rotor_model.png");

    public TileEntityWindKineticGenerator() {
        this.updateTicker = IC2.random.nextInt(this.getTickRate());
        this.rotorSlot = new InvSlotConsumableKineticRotor(this, "rotorslot", InvSlot.Access.IO, 1, InvSlot.InvSide.ANY, IKineticRotor.GearboxType.WIND);
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        if (this.updateTicker++ % this.getTickRate() != 0) {
            return;
        }
        boolean needsInvUpdate = false;
        if (!this.rotorSlot.isEmpty()) {
            if (this.checkSpace(1, true) == 0) {
                if (!this.getActive()) {
                    this.setActive(true);
                }
                needsInvUpdate = true;
            } else {
                if (this.getActive()) {
                    this.setActive(false);
                }
                needsInvUpdate = true;
            }
        } else {
            if (this.getActive()) {
                this.setActive(false);
            }
            needsInvUpdate = true;
        }
        if (this.getActive()) {
            this.crossSection = this.getRotorDiameter() / 2 * 2 * 2 + 1;
            this.crossSection *= this.crossSection;
            this.obstructedCrossSection = this.checkSpace(this.getRotorDiameter() * 3, false);
            if (this.obstructedCrossSection > 0 && this.obstructedCrossSection <= (this.getRotorDiameter() + 1) / 2) {
                this.obstructedCrossSection = 0;
            }
            if (this.obstructedCrossSection < 0) {
                this.windStrength = 0.0;
                this.setRotationSpeed(0.0f);
            } else {
                this.windStrength = this.calcWindStrength();
                float speed = (float)Util.limit((this.windStrength - (double)this.getMinWindStrength()) / (double)this.getMaxWindStrength(), 0.0, 2.0);
                this.setRotationSpeed(speed);
                if (this.windStrength >= (double)this.getMinWindStrength()) {
                    if (this.windStrength <= (double)this.getMaxWindStrength()) {
                        this.rotorSlot.damage(1, false);
                    } else {
                        this.rotorSlot.damage(4, false);
                    }
                }
            }
        }
        if (needsInvUpdate) {
            this.markDirty();
        }
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("rotationSpeed");
        ret.add("rotorSlot");
        return ret;
    }

    public ContainerBase<TileEntityWindKineticGenerator> getGuiContainer(EntityPlayer player) {
        return new ContainerWindKineticGenerator(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiWindKineticGenerator(new ContainerWindKineticGenerator(player, this));
    }

    public boolean facingMatchesDirection(EnumFacing direction) {
        return direction == this.getFacing();
    }

    public String getRotorHealth() {
        if (!this.rotorSlot.isEmpty()) {
            return Localization.translate("ic2.WindKineticGenerator.gui.rotorhealth", (int)(100.0f - (float)this.rotorSlot.get().getItemDamage() / (float)this.rotorSlot.get().getMaxDamage() * 100.0f));
        }
        return "";
    }

    @Override
    public int maxrequestkineticenergyTick(EnumFacing directionFrom) {
        return this.getKuOutput();
    }

    @Override
    public int requestkineticenergy(EnumFacing directionFrom, int requestkineticenergy) {
        if (this.facingMatchesDirection(directionFrom.getOpposite())) {
            return Math.min(requestkineticenergy, this.getKuOutput());
        }
        return 0;
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }

    public int checkSpace(int length, boolean onlyrotor) {
        int box = this.getRotorDiameter() / 2;
        int lentemp = 0;
        if (onlyrotor) {
            length = 1;
            lentemp = length + 1;
        }
        if (!onlyrotor) {
            box *= 2;
        }
        EnumFacing fwdDir = this.getFacing();
        EnumFacing rightDir = fwdDir.rotateAround(EnumFacing.DOWN.getAxis());
        int xMaxDist = Math.abs(length * fwdDir.getFrontOffsetX() + box * rightDir.getFrontOffsetX());
        int zMaxDist = Math.abs(length * fwdDir.getFrontOffsetZ() + box * rightDir.getFrontOffsetZ());
        ChunkCache chunkCache = new ChunkCache(this.worldObj, this.pos.add(- xMaxDist, - box, - zMaxDist), this.pos.add(xMaxDist, box, zMaxDist), 0);
        int ret = 0;
        int xCoord = this.pos.getX();
        int yCoord = this.pos.getY();
        int zCoord = this.pos.getZ();
        for (int up = - box; up <= box; ++up) {
            int y = yCoord + up;
            for (int right = - box; right <= box; ++right) {
                boolean occupied = false;
                for (int fwd = lentemp - length; fwd <= length; ++fwd) {
                    int x = xCoord + fwd * fwdDir.getFrontOffsetX() + right * rightDir.getFrontOffsetX();
                    int z = zCoord + fwd * fwdDir.getFrontOffsetZ() + right * rightDir.getFrontOffsetZ();
                    BlockPos pos = new BlockPos(x, y, z);
                    assert (Math.abs(x - xCoord) <= xMaxDist);
                    assert (Math.abs(z - zCoord) <= zMaxDist);
                    IBlockState state = chunkCache.getBlockState(pos);
                    Block block = state.getBlock();
                    if (block.isAir(state, (IBlockAccess)chunkCache, pos)) continue;
                    occupied = true;
                    if (up == 0 && right == 0 && fwd == 0 || !(chunkCache.getTileEntity(pos) instanceof TileEntityWindKineticGenerator) || onlyrotor) continue;
                    return -1;
                }
                if (!occupied) continue;
                ++ret;
            }
        }
        return ret;
    }

    public boolean hasRotor() {
        return !this.rotorSlot.isEmpty();
    }

    public boolean rotorHasSpace() {
        return this.checkSpace(1, true) == 0;
    }

    private void setRotationSpeed(float speed) {
        if (this.rotationSpeed != speed) {
            this.rotationSpeed = speed;
            IC2.network.get(true).updateTileEntityField(this, "rotationSpeed");
        }
    }

    public int getTickRate() {
        return 32;
    }

    public double calcWindStrength() {
        double windStr = WorldData.get((World)this.worldObj).windSim.getWindAt(this.pos.getY());
        return Math.max(0.0, windStr *= 1.0 - Math.pow((double)this.obstructedCrossSection / (double)this.crossSection, 2.0));
    }

    @Override
    public float getAngle() {
        if (this.rotationSpeed != 0.0f) {
            this.angle += (float)(System.currentTimeMillis() - this.lastcheck) * this.rotationSpeed;
            this.angle %= 360.0f;
        }
        this.lastcheck = System.currentTimeMillis();
        return this.angle;
    }

    public float getEfficiency() {
        ItemStack stack = this.rotorSlot.get();
        if (stack != null && stack.getItem() instanceof IKineticRotor) {
            return ((IKineticRotor)stack.getItem()).getEfficiency(stack);
        }
        return 0.0f;
    }

    public int getMinWindStrength() {
        ItemStack stack = this.rotorSlot.get();
        if (stack != null && stack.getItem() instanceof IKineticRotor) {
            return ((IKineticRotor)stack.getItem()).getMinWindStrength(stack);
        }
        return 0;
    }

    public int getMaxWindStrength() {
        ItemStack stack = this.rotorSlot.get();
        if (stack != null && stack.getItem() instanceof IKineticRotor) {
            return ((IKineticRotor)stack.getItem()).getMaxWindStrength(stack);
        }
        return 0;
    }

    @Override
    public int getRotorDiameter() {
        ItemStack stack = this.rotorSlot.get();
        if (stack != null && stack.getItem() instanceof IKineticRotor) {
            return ((IKineticRotor)stack.getItem()).getDiameter(stack);
        }
        return 0;
    }

    @Override
    public ResourceLocation getRotorRenderTexture() {
        ItemStack stack = this.rotorSlot.get();
        if (stack != null && stack.getItem() instanceof IKineticRotor) {
            return ((IKineticRotor)stack.getItem()).getRotorRenderTexture(stack);
        }
        return woodenRotorTexture;
    }

    public boolean isRotorOverloaded() {
        return this.hasRotor() && this.rotorHasSpace() && this.isWindStrongEnough() && this.windStrength > (double)this.getMaxWindStrength();
    }

    public boolean isWindStrongEnough() {
        return this.windStrength >= (double)this.getMinWindStrength();
    }

    public int getKuOutput() {
        if (this.windStrength >= (double)this.getMinWindStrength() && this.getActive()) {
            return (int)(this.windStrength * (double)outputModifier * (double)this.getEfficiency());
        }
        return 0;
    }

    public int getWindStrength() {
        return (int)this.windStrength;
    }

    @Override
    public void setActive(boolean active) {
        if (active != this.getActive()) {
            IC2.network.get(true).updateTileEntityField(this, "rotorSlot");
        }
        super.setActive(active);
    }
}

