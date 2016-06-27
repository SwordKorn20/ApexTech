/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockDoor
 *  net.minecraft.block.BlockDoor$EnumDoorHalf
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyBool
 *  net.minecraft.block.properties.PropertyEnum
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.block.statemap.IStateMapper
 *  net.minecraft.client.renderer.block.statemap.StateMap
 *  net.minecraft.client.renderer.block.statemap.StateMap$Builder
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.fml.common.registry.GameRegistry
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block;

import ic2.core.CreativeTabIC2;
import ic2.core.IC2;
import ic2.core.block.BlockBase;
import ic2.core.item.block.ItemIC2Door;
import ic2.core.ref.BlockName;
import ic2.core.ref.IBlockModelProvider;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDoor;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockIC2Door
extends BlockDoor
implements IBlockModelProvider {
    public BlockIC2Door() {
        super(Material.IRON);
        this.setHardness(50.0f);
        this.setResistance(150.0f);
        this.setSoundType(SoundType.METAL);
        this.disableStats();
        this.setUnlocalizedName(BlockName.reinforced_door.name());
        this.setCreativeTab((CreativeTabs)IC2.tabIC2);
        GameRegistry.registerBlock((Block)this, (Class)ItemIC2Door.class, (String)BlockName.reinforced_door.name());
        BlockName.reinforced_door.setInstance(this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public void registerModels(BlockName name) {
        StateMap mapper = new StateMap.Builder().ignore(new IProperty[]{POWERED}).build();
        ModelLoader.setCustomStateMapper((Block)this, (IStateMapper)mapper);
        BlockBase.registerDefaultVanillaItemModel((Block)this, null);
    }

    public String getUnlocalizedName() {
        return "ic2." + super.getUnlocalizedName().substring(5);
    }

    public boolean canReplace(World world, BlockPos pos, EnumFacing side, ItemStack stack) {
        if (side != EnumFacing.UP) {
            return false;
        }
        return super.canReplace(world, pos, side, stack);
    }

    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        if (state.getValue((IProperty)HALF) == BlockDoor.EnumDoorHalf.UPPER) {
            return null;
        }
        return Item.getItemFromBlock((Block)this);
    }
}

