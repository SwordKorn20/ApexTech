/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.EnumSkyBlock
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldProvider
 *  net.minecraft.world.biome.Biome
 *  net.minecraftforge.common.BiomeDictionary
 *  net.minecraftforge.common.BiomeDictionary$Type
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.generator.tileentity;

import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.block.comp.Energy;
import ic2.core.block.generator.container.ContainerSolarGenerator;
import ic2.core.block.generator.gui.GuiSolarGenerator;
import ic2.core.block.generator.tileentity.TileEntityBaseGenerator;
import ic2.core.init.MainConfig;
import ic2.core.util.BiomeUtil;
import ic2.core.util.ConfigUtil;
import ic2.core.util.Util;
import java.util.Random;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntitySolarGenerator
extends TileEntityBaseGenerator {
    private static final int tickRate = 128;
    private static final double energyMultiplier = ConfigUtil.getDouble(MainConfig.get(), "balance/energy/generator/solar");
    private int ticker = IC2.random.nextInt(128);
    public float skyLight;

    public TileEntitySolarGenerator() {
        super(1.0, 1, 2);
    }

    @Override
    protected void onLoaded() {
        super.onLoaded();
        this.updateSunVisibility();
    }

    @Override
    public boolean gainEnergy() {
        if (++this.ticker % 128 == 0) {
            this.updateSunVisibility();
        }
        if (this.skyLight > 0.0f) {
            this.energy.addEnergy(energyMultiplier * (double)this.skyLight);
            return true;
        }
        return false;
    }

    @Override
    public boolean gainFuel() {
        return false;
    }

    public void updateSunVisibility() {
        this.skyLight = TileEntitySolarGenerator.getSkyLight(this.worldObj, this.pos.up());
    }

    public static float getSkyLight(World world, BlockPos pos) {
        if (world.provider.getHasNoSky()) {
            return 0.0f;
        }
        float sunBrightness = Util.limit((float)Math.cos(world.getCelestialAngleRadians(1.0f)) * 2.0f + 0.2f, 0.0f, 1.0f);
        if (!BiomeDictionary.isBiomeOfType((Biome)BiomeUtil.getBiome(world, pos), (BiomeDictionary.Type)BiomeDictionary.Type.SANDY)) {
            sunBrightness *= 1.0f - world.getRainStrength(1.0f) * 5.0f / 16.0f;
            sunBrightness *= 1.0f - world.getThunderStrength(1.0f) * 5.0f / 16.0f;
            sunBrightness = Util.limit(sunBrightness, 0.0f, 1.0f);
        }
        return (float)world.getLightFor(EnumSkyBlock.SKY, pos) / 15.0f * sunBrightness;
    }

    @Override
    public boolean needsFuel() {
        return false;
    }

    public ContainerBase<TileEntitySolarGenerator> getGuiContainer(EntityPlayer player) {
        return new ContainerSolarGenerator(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiSolarGenerator(new ContainerSolarGenerator(player, this));
    }

    @Override
    protected boolean delayActiveUpdate() {
        return true;
    }
}

