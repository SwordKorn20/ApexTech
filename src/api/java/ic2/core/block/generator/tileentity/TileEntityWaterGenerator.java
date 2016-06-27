/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockDynamicLiquid
 *  net.minecraft.block.BlockStaticLiquid
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidRegistry
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.generator.tileentity;

import ic2.api.tile.IRotorProvider;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.generator.container.ContainerWaterGenerator;
import ic2.core.block.generator.gui.GuiWaterGenerator;
import ic2.core.block.generator.tileentity.TileEntityBaseGenerator;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotConsumableLiquidByList;
import ic2.core.init.MainConfig;
import ic2.core.util.ConfigUtil;
import ic2.core.util.Util;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityWaterGenerator
extends TileEntityBaseGenerator
implements IRotorProvider {
    private static final int tickRate = 128;
    private static final double energyMultiplier = ConfigUtil.getDouble(MainConfig.get(), "balance/energy/generator/water");
    private static final boolean allowAutomation = ConfigUtil.getBool(MainConfig.get(), "balance/watermillAutomation");
    private static final float rotationSpeed = 0.15f;
    private static final ResourceLocation rotorTexture = new ResourceLocation(IC2.textureDomain, "textures/items/rotor/iron_rotor_model.png");
    public final InvSlotConsumableLiquid fuelSlot;
    private int ticker = IC2.random.nextInt(128);
    public int water = 0;
    public int microStorage = 0;
    public int maxWater = 2000;
    private float angle = 0.0f;
    private long lastcheck;

    public TileEntityWaterGenerator() {
        super(2.0, 1, 4);
        this.production = 2.0;
        this.fuelSlot = new InvSlotConsumableLiquidByList((TileEntityInventory)this, "fuel", allowAutomation ? InvSlot.Access.IO : InvSlot.Access.NONE, 1, InvSlot.InvSide.TOP, InvSlotConsumableLiquid.OpType.Drain, FluidRegistry.WATER);
    }

    @Override
    protected void onLoaded() {
        super.onLoaded();
        this.updateWaterCount();
    }

    public int gaugeFuelScaled(int i) {
        return Util.limit(this.fuel * i / this.maxWater, 0, i);
    }

    @Override
    public boolean gainFuel() {
        if (this.fuel + 500 > this.maxWater) {
            return false;
        }
        if (!this.fuelSlot.isEmpty()) {
            ItemStack liquid = this.fuelSlot.consume(1);
            if (liquid == null) {
                return false;
            }
            this.fuel += 500;
            this.production = liquid.getItem().hasContainerItem(liquid) ? 1.0 : 2.0;
            return true;
        }
        if (this.fuel <= 0) {
            this.flowPower();
            this.production = this.microStorage / 100;
            this.microStorage = (int)((double)this.microStorage - this.production * 100.0);
            if (this.production > 0.0) {
                ++this.fuel;
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public boolean needsFuel() {
        return this.fuel <= this.maxWater;
    }

    public void flowPower() {
        if (++this.ticker % 128 == 0) {
            this.updateWaterCount();
        }
        this.water = (int)Math.round((double)this.water * energyMultiplier);
        if (this.water > 0) {
            this.microStorage += this.water;
        }
    }

    public void updateWaterCount() {
        int count = 0;
        for (int x = -1; x < 2; ++x) {
            for (int y = -1; y < 2; ++y) {
                for (int z = -1; z < 2; ++z) {
                    Block block = this.worldObj.getBlockState(this.pos.add(x, y, z)).getBlock();
                    if (block != Blocks.WATER && block != Blocks.FLOWING_WATER) continue;
                    ++count;
                }
            }
        }
        this.water = count;
    }

    @Override
    public String getOperationSoundFile() {
        return "Generators/WatermillLoop.ogg";
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

    public ContainerBase<TileEntityWaterGenerator> getGuiContainer(EntityPlayer player) {
        return new ContainerWaterGenerator(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiWaterGenerator(new ContainerWaterGenerator(player, this));
    }
}

