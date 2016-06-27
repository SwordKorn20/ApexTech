/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockDynamicLiquid
 *  net.minecraft.block.BlockStaticLiquid
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.MobEffects
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.fluids.BlockFluidClassic
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidRegistry
 *  net.minecraftforge.fml.common.registry.GameRegistry
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block;

import ic2.core.CreativeTabIC2;
import ic2.core.IC2;
import ic2.core.block.BlockBase;
import ic2.core.block.state.IIdProvider;
import ic2.core.block.type.ResourceBlock;
import ic2.core.item.block.ItemBlockIC2;
import ic2.core.ref.BlockName;
import ic2.core.ref.FluidName;
import ic2.core.ref.IBlockModelProvider;
import ic2.core.util.LiquidUtil;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockIC2Fluid
extends BlockFluidClassic
implements IBlockModelProvider {
    protected Fluid fluid;
    private final int color;

    public BlockIC2Fluid(FluidName name, Fluid fluid, Material material, int color) {
        super(fluid, material);
        this.setUnlocalizedName(name.name());
        this.setCreativeTab((CreativeTabs)IC2.tabIC2);
        this.fluid = fluid;
        this.color = color;
        if (this.density <= FluidRegistry.WATER.getDensity()) {
            this.displacements.put(Blocks.WATER, false);
            this.displacements.put(Blocks.FLOWING_WATER, false);
        }
        if (this.density <= FluidRegistry.LAVA.getDensity()) {
            this.displacements.put(Blocks.LAVA, false);
            this.displacements.put(Blocks.FLOWING_LAVA, false);
        }
        GameRegistry.registerBlock((Block)this, (Class)ItemBlockIC2.class, (String)name.name());
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public void registerModels(BlockName name) {
        BlockBase.registerDefaultItemModel((Block)this);
    }

    public void updateTick(World world, BlockPos pos, IBlockState state, Random random) {
        super.updateTick(world, pos, state, random);
        if (!world.isRemote) {
            if (this.fluid == FluidName.pahoehoe_lava.getInstance()) {
                if (this.isSourceBlock((IBlockAccess)world, pos) && world.getLightFromNeighbors(pos) >= world.rand.nextInt(120)) {
                    world.setBlockState(pos, BlockName.resource.getBlockState(ResourceBlock.basalt));
                } else if (!this.hardenFromNeighbors(world, pos)) {
                    world.scheduleUpdate(pos, (Block)this, this.tickRate(world));
                }
            } else if (this.fluid == FluidName.hot_water.getInstance()) {
                if (this.isSourceBlock((IBlockAccess)world, pos) && world.getBlockState(pos.down(2)).getBlock() != Blocks.FLOWING_LAVA && world.getBlockState(pos.down()).getBlock() != this && world.rand.nextInt(60) == 0) {
                    world.setBlockState(pos, Blocks.FLOWING_WATER.getDefaultState());
                } else {
                    world.scheduleUpdate(pos, (Block)this, this.tickRate(world));
                }
            }
        }
    }

    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block) {
        super.neighborChanged(state, world, pos, block);
        this.hardenFromNeighbors(world, pos);
    }

    public void onBlockAdded(World world, BlockPos pos, IBlockState state) {
        super.onBlockAdded(world, pos, state);
        this.hardenFromNeighbors(world, pos);
    }

    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        if (world.isRemote) {
            return;
        }
        if (this.fluid == FluidName.biogas.getInstance()) {
            world.setBlockToAir(pos);
        }
    }

    public void onEntityWalk(World world, BlockPos pos, Entity entity) {
        if (world.isRemote) {
            return;
        }
        if (this.fluid == FluidName.pahoehoe_lava.getInstance()) {
            entity.setFire(10);
        } else if (this.fluid == FluidName.hot_coolant.getInstance()) {
            entity.setFire(30);
        }
        if (entity instanceof EntityLivingBase) {
            EntityLivingBase living = (EntityLivingBase)entity;
            if (this.fluid == FluidName.construction_foam.getInstance()) {
                BlockIC2Fluid.addPotion(living, MobEffects.SLOWNESS, 300, 2);
            } else if (this.fluid == FluidName.uu_matter.getInstance()) {
                BlockIC2Fluid.addPotion(living, MobEffects.REGENERATION, 100, 1);
            } else if (this.fluid == FluidName.steam.getInstance() || this.fluid == FluidName.superheated_steam.getInstance()) {
                BlockIC2Fluid.addPotion(living, MobEffects.BLINDNESS, 300, 0);
            } else if (this.fluid == FluidName.hot_water.getInstance()) {
                Potion potion = ((EntityLivingBase)entity).isEntityUndead() ? MobEffects.WITHER : MobEffects.REGENERATION;
                BlockIC2Fluid.addPotion(living, potion, 100, IC2.random.nextInt(2));
            }
        }
    }

    private static void addPotion(EntityLivingBase entity, Potion potion, int duration, int amplifier) {
        if (entity.isPotionActive(potion)) {
            return;
        }
        entity.addPotionEffect(new PotionEffect(potion, duration, amplifier, true, true));
    }

    public String getUnlocalizedName() {
        return super.getUnlocalizedName().substring(5);
    }

    public int getColor() {
        return this.color;
    }

    private boolean hardenFromNeighbors(World world, BlockPos pos) {
        if (world.isRemote) {
            return false;
        }
        if (this.fluid == FluidName.pahoehoe_lava.getInstance()) {
            for (EnumFacing dir : EnumFacing.VALUES) {
                LiquidUtil.LiquidData data = LiquidUtil.getLiquid(world, pos.offset(dir));
                if (data == null || data.liquid.getTemperature() > this.fluid.getTemperature() / 4) continue;
                if (this.isSourceBlock((IBlockAccess)world, pos)) {
                    world.setBlockState(pos, BlockName.resource.getBlockState(ResourceBlock.basalt));
                } else {
                    world.setBlockToAir(pos);
                }
                return true;
            }
        }
        return false;
    }
}

