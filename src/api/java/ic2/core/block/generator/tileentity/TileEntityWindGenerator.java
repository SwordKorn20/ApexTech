/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.generator.tileentity;

import ic2.api.tile.IRotorProvider;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.WindSim;
import ic2.core.WorldData;
import ic2.core.block.generator.container.ContainerWindGenerator;
import ic2.core.block.generator.gui.GuiWindGenerator;
import ic2.core.block.generator.tileentity.TileEntityBaseGenerator;
import ic2.core.init.MainConfig;
import ic2.core.util.ConfigUtil;
import ic2.core.util.StackUtil;
import ic2.core.util.Util;
import java.util.Random;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityWindGenerator
extends TileEntityBaseGenerator
implements IRotorProvider {
    private static final int tickRate = 128;
    private static final double energyMultiplier = ConfigUtil.getDouble(MainConfig.get(), "balance/energy/generator/wind");
    private static final double windToEnergy = 0.1 * energyMultiplier;
    private static final double safeWindRatio = 0.5;
    private static final float rotationSpeed = 0.15f;
    private static final ResourceLocation rotorTexture = new ResourceLocation(IC2.textureDomain, "textures/items/rotor/iron_rotor_model.png");
    private int ticker = IC2.random.nextInt(128);
    private int obstructedBlockCount;
    private double overheatRatio;
    private float angle = 0.0f;
    private long lastcheck;

    public TileEntityWindGenerator() {
        super(4.0, 1, 5);
    }

    public int getOverheatScaled(int i) {
        if (this.overheatRatio >= 0.0) {
            return 0;
        }
        return 1 + (int)Math.min((double)(i - 1), this.overheatRatio / (double)(i - 1));
    }

    @Override
    protected void onLoaded() {
        super.onLoaded();
        this.updateObscuratedBlockCount();
    }

    @Override
    public boolean gainEnergy() {
        if (++this.ticker % 128 == 0) {
            if (this.ticker % 1024 == 0) {
                this.updateObscuratedBlockCount();
            }
            this.production = 0.0;
            this.overheatRatio = 0.0;
            if (windToEnergy <= 0.0) {
                return false;
            }
            WindSim windSim = WorldData.get((World)this.worldObj).windSim;
            double wind = windSim.getWindAt(this.pos.getY()) * (1.0 - (double)this.obstructedBlockCount / 567.0);
            if (wind <= 0.0) {
                return false;
            }
            double windRatio = wind / windSim.getMaxWind();
            this.overheatRatio = Math.max(0.0, (windRatio - 0.5) / 0.5);
            if (wind > windSim.getMaxWind() * 0.5 && (double)this.worldObj.rand.nextInt(5000) <= this.production - 5.0) {
                if (Util.harvestBlock(this.worldObj, this.pos)) {
                    for (int i = this.worldObj.rand.nextInt((int)5); i > 0; --i) {
                        StackUtil.dropAsEntity(this.worldObj, this.pos, new ItemStack(Items.IRON_INGOT));
                    }
                }
                return false;
            }
            this.production = wind * windToEnergy;
        }
        return super.gainEnergy();
    }

    @Override
    public boolean gainFuel() {
        return false;
    }

    public void updateObscuratedBlockCount() {
        this.obstructedBlockCount = -1;
        for (int x = -4; x < 5; ++x) {
            for (int y = -2; y < 5; ++y) {
                for (int z = -4; z < 5; ++z) {
                    if (this.worldObj.isAirBlock(this.pos.add(x, y, z))) continue;
                    ++this.obstructedBlockCount;
                }
            }
        }
    }

    @Override
    public boolean needsFuel() {
        return false;
    }

    @Override
    public String getOperationSoundFile() {
        return "Generators/WindGenLoop.ogg";
    }

    @Override
    protected boolean delayActiveUpdate() {
        return true;
    }

    @Override
    public int getRotorDiameter() {
        return 2;
    }

    @Override
    public float getAngle() {
        if (this.getActive()) {
            this.angle += (float)(System.currentTimeMillis() - this.lastcheck) * 0.15f;
            this.angle %= 360.0f;
        }
        this.lastcheck = System.currentTimeMillis();
        return this.angle;
    }

    @Override
    public ResourceLocation getRotorRenderTexture() {
        return rotorTexture;
    }

    public ContainerBase<TileEntityWindGenerator> getGuiContainer(EntityPlayer player) {
        return new ContainerWindGenerator(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiWindGenerator(new ContainerWindGenerator(player, this));
    }
}

