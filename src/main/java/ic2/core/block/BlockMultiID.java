/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.world.Explosion
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block;

import ic2.core.block.BlockBase;
import ic2.core.block.state.EnumProperty;
import ic2.core.block.state.IIdProvider;
import ic2.core.block.type.IExtBlockType;
import ic2.core.item.block.ItemBlockMulti;
import ic2.core.ref.BlockName;
import ic2.core.ref.IMultiBlock;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockMultiID<T extends Enum<T>>
extends BlockBase
implements IMultiBlock<T> {
    private static final ThreadLocal<EnumProperty<? extends Enum<?>>> currentTypeProperty = new ThreadLocal<EnumProperty<? extends Enum<?>>>(){

        @Override
        protected EnumProperty<? extends Enum<?>> initialValue() {
            throw new UnsupportedOperationException();
        }
    };
    protected final EnumProperty<T> typeProperty;

    public static <T extends Enum<T>> BlockMultiID<T> create(BlockName name, Material material, Class<T> typeClass) {
        EnumProperty<T> typeProperty = BlockMultiID.createTypeProperty(typeClass);
        currentTypeProperty.set(typeProperty);
        BlockMultiID<T> ret = new BlockMultiID<T>(name, material);
        currentTypeProperty.remove();
        return ret;
    }

    private static <T extends Enum<T>> EnumProperty<T> createTypeProperty(Class<T> typeClass) {
        EnumProperty<T> ret = new EnumProperty<T>("type", typeClass);
        if (ret.getAllowedValues().size() > 16) {
            throw new IllegalArgumentException("Too many values to fit in 16 meta values for " + typeClass);
        }
        return ret;
    }

    protected static /* varargs */ <T extends Enum<T>, U extends BlockMultiID<T>> U create(Class<U> blockClass, Class<T> typeClass, Object ... ctorArgs) {
        BlockMultiID ret;
        EnumProperty<T> typeProperty = BlockMultiID.createTypeProperty(typeClass);
        Constructor ctor = null;
        block5 : for (Constructor cCtor : blockClass.getDeclaredConstructors()) {
            Class<?>[] parameterTypes = cCtor.getParameterTypes();
            if (parameterTypes.length != ctorArgs.length) continue;
            for (int i = 0; i < parameterTypes.length; ++i) {
                Class type = parameterTypes[i];
                Object arg = ctorArgs[i];
                if (arg == null && type.isPrimitive() || arg != null && !parameterTypes[i].isInstance(arg)) continue block5;
            }
            if (ctor != null) {
                throw new IllegalArgumentException("ambiguous constructor");
            }
            ctor = cCtor;
        }
        if (ctor == null) {
            throw new IllegalArgumentException("no matching constructor");
        }
        currentTypeProperty.set(typeProperty);
        try {
            ctor.setAccessible(true);
            ret = (BlockMultiID)ctor.newInstance(ctorArgs);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            currentTypeProperty.remove();
        }
        return (U)ret;
    }

    protected BlockMultiID(BlockName name, Material material) {
        this(name, material, ItemBlockMulti.class);
    }

    protected BlockMultiID(BlockName name, Material material, Class<? extends ItemBlock> itemClass) {
        super(name, material, itemClass);
        this.typeProperty = this.getTypeProperty();
        this.setDefaultState(this.blockState.getBaseState().withProperty(this.typeProperty, this.typeProperty.getDefault()));
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public void registerModels(BlockName name) {
        BlockMultiID.registerItemModels(this, this.getTypeStates());
    }

    protected final List<IBlockState> getTypeStates() {
        ArrayList<IBlockState> ret = new ArrayList<IBlockState>(this.typeProperty.getAllowedValues().size());
        for (Enum type : this.typeProperty.getAllowedValues()) {
            ret.add(this.getDefaultState().withProperty(this.typeProperty, (Comparable)((Object)type)));
        }
        return ret;
    }

    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer((Block)this, new IProperty[]{this.getTypeProperty()});
    }

    public IBlockState getStateFromMeta(int meta) {
        EnumProperty<T> typeProperty = this.getTypeProperty();
        return this.getDefaultState().withProperty(typeProperty, typeProperty.getValueOrDefault(meta));
    }

    public int getMetaFromState(IBlockState state) {
        return ((IIdProvider)((Object)((Enum)((Object)state.getValue(this.getTypeProperty()))))).getId();
    }

    protected T getType(IBlockAccess world, BlockPos pos) {
        return this.getType(world.getBlockState(pos));
    }

    protected final T getType(IBlockState state) {
        if (state.getBlock() != this) {
            return null;
        }
        return (T)((Enum)((Object)state.getValue(this.typeProperty)));
    }

    @Override
    public IBlockState getState(T type) {
        if (type == null) {
            throw new IllegalArgumentException("invalid type: " + type);
        }
        return this.getDefaultState().withProperty(this.typeProperty, type);
    }

    @Override
    public ItemStack getItemStack(T type) {
        return this.getItemStack(this.getState(type));
    }

    @Override
    public ItemStack getItemStack(String variant) {
        if (variant == null) {
            throw new IllegalArgumentException("invalid type: " + variant);
        }
        T type = this.typeProperty.getValue(variant);
        if (type == null) {
            throw new IllegalArgumentException("invalid variant " + variant + " for " + this);
        }
        return this.getItemStack(type);
    }

    @Override
    public String getVariant(ItemStack stack) {
        if (stack == null) {
            throw new NullPointerException("null stack");
        }
        Item item = Item.getItemFromBlock((Block)this);
        if (stack.getItem() != item) {
            throw new IllegalArgumentException("The stack " + (Object)stack + " doesn't match " + (Object)item + " (" + this + ")");
        }
        IBlockState state = this.getStateFromMeta(stack.getMetadata());
        T type = this.getType(state);
        return ((IIdProvider)type).getName();
    }

    @Override
    public ItemStack getItemStack(IBlockState state) {
        if (state.getBlock() != this) {
            return null;
        }
        Item item = Item.getItemFromBlock((Block)this);
        if (item == null) {
            throw new RuntimeException("no matching item for " + this);
        }
        int meta = this.getMetaFromState(state);
        return new ItemStack(item, 1, meta);
    }

    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        ItemStack stack = this.getItemStack(state);
        if (stack == null) {
            return new ArrayList<ItemStack>();
        }
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        ret.add(stack);
        return ret;
    }

    public void getSubBlocks(Item item, CreativeTabs tabs, List<ItemStack> itemList) {
        for (Enum type : this.typeProperty.getAllowedValues()) {
            itemList.add(this.getItemStack(type));
        }
    }

    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        return this.getItemStack(world.getBlockState(pos));
    }

    public final EnumProperty<T> getTypeProperty() {
        EnumProperty ret2;
        EnumProperty ret2;
        if (this.typeProperty != null) {
            ret2 = this.typeProperty;
        } else {
            ret2 = currentTypeProperty.get();
            if (ret2 == null) {
                throw new IllegalStateException("The type property can't be obtained.");
            }
        }
        return ret2;
    }

    public float getBlockHardness(IBlockState state, World world, BlockPos pos) {
        T type;
        if (IExtBlockType.class.isAssignableFrom(this.typeProperty.getValueClass()) && (type = this.getType((IBlockAccess)world, pos)) != null) {
            return ((IExtBlockType)type).getHardness();
        }
        return super.getBlockHardness(state, world, pos);
    }

    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
        T type;
        if (IExtBlockType.class.isAssignableFrom(this.typeProperty.getValueClass()) && (type = this.getType((IBlockAccess)world, pos)) != null) {
            return ((IExtBlockType)type).getExplosionResistance();
        }
        return super.getExplosionResistance(world, pos, exploder, explosion);
    }

}

