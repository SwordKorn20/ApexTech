/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.block.Block
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.CompressedStreamTools
 *  net.minecraft.nbt.NBTSizeTracker
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.potion.Potion
 *  net.minecraft.stats.Achievement
 *  net.minecraft.stats.AchievementList
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.ChunkPos
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.util.registry.RegistryNamespaced
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldProvider
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidRegistry
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.FluidTank
 */
package ic2.core.network;

import com.mojang.authlib.GameProfile;
import ic2.api.crops.CropCard;
import ic2.api.crops.Crops;
import ic2.core.IC2;
import ic2.core.Platform;
import ic2.core.block.comp.TileEntityComponent;
import ic2.core.block.invslot.InvSlot;
import ic2.core.network.GrowingBuffer;
import ic2.core.util.StackUtil;
import ic2.core.util.Tuple;
import ic2.core.util.Util;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTSizeTracker;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public final class DataEncoder {
    private static final Map<Class<?>, EncodedType> classToTypeCache = Collections.synchronizedMap(new IdentityHashMap());

    public static void encode(GrowingBuffer os, Object o) throws IOException {
        try {
            DataEncoder.encode(os, o, true);
        }
        catch (IllegalArgumentException e) {
            IC2.platform.displayError(e, "An unknown data type was attempted to be encoded for sending through\nmultiplayer.\nThis could happen due to a bug.", new Object[0]);
        }
    }

    public static void encode(GrowingBuffer os, Object o, boolean withType) throws IOException {
        EncodedType type = DataEncoder.typeFromObject(o);
        if (withType) {
            os.writeByte(DataEncoder.idFromType(type));
        }
        switch (type) {
            case Achievement: {
                os.writeString(((Achievement)o).statId);
                break;
            }
            case Array: {
                int i;
                Class componentClass = o.getClass().getComponentType();
                EncodedType componentType = DataEncoder.typeFromClass(componentClass);
                os.writeByte(DataEncoder.idFromType(componentType));
                os.writeBoolean(componentClass.isPrimitive());
                int len = Array.getLength(o);
                os.writeVarInt(len);
                boolean anyTypeMismatch = false;
                for (i = 0; i < len; ++i) {
                    Object value = Array.get(o, i);
                    if (value != null && DataEncoder.typeFromClass(value.getClass()) == componentType) continue;
                    anyTypeMismatch = true;
                    break;
                }
                os.writeBoolean(anyTypeMismatch);
                for (i = 0; i < len; ++i) {
                    DataEncoder.encode(os, Array.get(o, i), anyTypeMismatch);
                }
                break;
            }
            case Block: {
                DataEncoder.encode(os, (Object)Util.getName((Block)o), false);
                break;
            }
            case BlockPos: {
                BlockPos pos = (BlockPos)o;
                os.writeInt(pos.getX());
                os.writeInt(pos.getY());
                os.writeInt(pos.getZ());
                break;
            }
            case Boolean: {
                os.writeBoolean((Boolean)o);
                break;
            }
            case Byte: {
                os.writeByte(((Byte)o).byteValue());
                break;
            }
            case Character: {
                os.writeChar(((Character)o).charValue());
                break;
            }
            case ChunkPos: {
                ChunkPos pos = (ChunkPos)o;
                os.writeInt(pos.chunkXPos);
                os.writeInt(pos.chunkZPos);
                break;
            }
            case Collection: {
                DataEncoder.encode(os, ((Collection)o).toArray(), false);
                break;
            }
            case Component: {
                NBTTagCompound nbt = ((TileEntityComponent)o).writeToNbt();
                DataEncoder.encode(os, (Object)(nbt == null ? new NBTTagCompound() : nbt), false);
                break;
            }
            case CropCard: {
                CropCard cropCard = (CropCard)o;
                os.writeString(cropCard.getOwner());
                os.writeString(cropCard.getName());
                break;
            }
            case Double: {
                os.writeDouble((Double)o);
                break;
            }
            case Enchantment: {
                DataEncoder.encode(os, Enchantment.REGISTRY.getNameForObject((Object)((Enchantment)o)), false);
                break;
            }
            case Enum: {
                os.writeVarInt(((Enum)o).ordinal());
                break;
            }
            case Float: {
                os.writeFloat(((Float)o).floatValue());
                break;
            }
            case Fluid: {
                os.writeString(((Fluid)o).getName());
                break;
            }
            case FluidStack: {
                FluidStack fs = (FluidStack)o;
                DataEncoder.encode(os, (Object)fs.getFluid(), false);
                os.writeInt(fs.amount);
                DataEncoder.encode(os, (Object)fs.tag, true);
                break;
            }
            case FluidTank: {
                FluidTank tank = (FluidTank)o;
                DataEncoder.encode(os, (Object)tank.getFluid(), true);
                os.writeInt(tank.getCapacity());
                break;
            }
            case GameProfile: {
                GameProfile gp = (GameProfile)o;
                DataEncoder.encode(os, gp.getId(), true);
                os.writeString(gp.getName());
                break;
            }
            case Integer: {
                os.writeInt((Integer)o);
                break;
            }
            case InvSlot: {
                InvSlot slot = (InvSlot)o;
                ItemStack[] contents = new ItemStack[slot.size()];
                for (int i = 0; i < slot.size(); ++i) {
                    contents[i] = slot.get(i);
                }
                DataEncoder.encode(os, contents, false);
                break;
            }
            case Item: {
                DataEncoder.encode(os, (Object)Util.getName((Item)o), false);
                break;
            }
            case ItemStack: {
                ItemStack stack = (ItemStack)o;
                Item item = stack.getItem();
                if (item == null) {
                    throw new IllegalArgumentException("invalid itemstack");
                }
                DataEncoder.encode(os, (Object)item, false);
                os.writeByte(stack.stackSize);
                os.writeShort(stack.getItemDamage());
                DataEncoder.encode(os, (Object)stack.getTagCompound(), true);
                break;
            }
            case Long: {
                os.writeLong((Long)o);
                break;
            }
            case NBTTagCompound: {
                CompressedStreamTools.write((NBTTagCompound)((NBTTagCompound)o), (DataOutput)os);
                break;
            }
            case Null: {
                if (withType) break;
                throw new IllegalArgumentException("o has to be non-null without types");
            }
            case Object: {
                throw new IllegalArgumentException("unhandled class: " + o.getClass());
            }
            case Potion: {
                DataEncoder.encode(os, Potion.REGISTRY.getNameForObject((Object)((Potion)o)), false);
                break;
            }
            case ResourceLocation: {
                ResourceLocation loc = (ResourceLocation)o;
                os.writeString(loc.getResourceDomain());
                os.writeString(loc.getResourcePath());
                break;
            }
            case Short: {
                os.writeShort(((Short)o).shortValue());
                break;
            }
            case String: {
                os.writeString((String)o);
                break;
            }
            case TileEntity: {
                TileEntity te = (TileEntity)o;
                DataEncoder.encode(os, (Object)te.getWorld(), false);
                DataEncoder.encode(os, (Object)te.getPos(), false);
                break;
            }
            case TupleT2: {
                Tuple.T2 t = (Tuple.T2)o;
                DataEncoder.encode(os, t.a, true);
                DataEncoder.encode(os, t.b, true);
                break;
            }
            case TupleT3: {
                Tuple.T3 t = (Tuple.T3)o;
                DataEncoder.encode(os, t.a, true);
                DataEncoder.encode(os, t.b, true);
                DataEncoder.encode(os, t.c, true);
                break;
            }
            case UUID: {
                UUID uuid = (UUID)o;
                os.writeLong(uuid.getMostSignificantBits());
                os.writeLong(uuid.getLeastSignificantBits());
                break;
            }
            case Vec3: {
                Vec3d v = (Vec3d)o;
                os.writeDouble(v.xCoord);
                os.writeDouble(v.yCoord);
                os.writeDouble(v.zCoord);
                break;
            }
            case World: {
                os.writeInt(((World)o).provider.getDimension());
                break;
            }
            default: {
                throw new IllegalArgumentException("unhandled type: " + (Object)((Object)type));
            }
        }
    }

    public static Object decode(GrowingBuffer is) throws IOException {
        try {
            return DataEncoder.decode(is, DataEncoder.typeFromId(is.readUnsignedByte()));
        }
        catch (IllegalArgumentException e) {
            String msg = "An unknown data type was received over multiplayer to be decoded.\nThis could happen due to corrupted data or a bug.";
            IC2.platform.displayError(e, msg, new Object[0]);
            return null;
        }
    }

    public static <T> T decode(GrowingBuffer is, Class<T> clazz) throws IOException {
        EncodedType type = DataEncoder.typeFromClass(clazz);
        if (type.threadSafe) {
            return (T)DataEncoder.decode(is, type);
        }
        throw new IllegalArgumentException("requesting decode for non thread safe type");
    }

    public static Object decodeDeferred(GrowingBuffer is, Class<?> clazz) throws IOException {
        EncodedType type = DataEncoder.typeFromClass(clazz);
        return DataEncoder.decode(is, type);
    }

    public static Object decode(GrowingBuffer is, EncodedType type) throws IOException {
        switch (type) {
            case Achievement: {
                String id = is.readString();
                for (Object achievement : AchievementList.ACHIEVEMENTS) {
                    if (!((Achievement)achievement).statId.equals(id)) continue;
                    return achievement;
                }
                return null;
            }
            case Array: {
                int i;
                EncodedType componentType = DataEncoder.typeFromId(is.readUnsignedByte());
                boolean primitive = is.readBoolean();
                final Class componentClass = primitive ? DataEncoder.unbox(componentType.cls) : componentType.cls;
                final int len = is.readVarInt();
                boolean anyTypeMismatch = is.readBoolean();
                boolean needsResolving = !componentType.threadSafe;
                Object array = !needsResolving ? Array.newInstance(componentClass, len) : new Object[len];
                if (!anyTypeMismatch) {
                    for (i = 0; i < len; ++i) {
                        Array.set(array, i, DataEncoder.decode(is, componentType));
                    }
                } else {
                    for (i = 0; i < len; ++i) {
                        EncodedType cType = DataEncoder.typeFromId(is.readUnsignedByte());
                        if (!cType.threadSafe && !needsResolving) {
                            needsResolving = true;
                            if (componentClass != Object.class) {
                                Object[] newArray = new Object[len];
                                System.arraycopy(array, 0, newArray, 0, i);
                                array = newArray;
                            }
                        }
                        Array.set(array, i, DataEncoder.decode(is, cType));
                    }
                }
                if (!needsResolving) {
                    return array;
                }
                final Object[] tmpArray = array;
                return new IResolvableValue<Object>(){

                    @Override
                    public Object get() {
                        Object ret = Array.newInstance(componentClass, len);
                        for (int i = 0; i < len; ++i) {
                            Array.set(ret, i, DataEncoder.getValue(Array.get(tmpArray, i)));
                        }
                        return ret;
                    }
                };
            }
            case Block: {
                return Util.getBlock((ResourceLocation)DataEncoder.decode(is, EncodedType.ResourceLocation));
            }
            case BlockPos: {
                return new BlockPos(is.readInt(), is.readInt(), is.readInt());
            }
            case Boolean: {
                return is.readBoolean();
            }
            case Byte: {
                return Byte.valueOf(is.readByte());
            }
            case Character: {
                return Character.valueOf(is.readChar());
            }
            case ChunkPos: {
                return new ChunkPos(is.readInt(), is.readInt());
            }
            case Collection: {
                final Object ret = DataEncoder.decode(is, EncodedType.Array);
                if (ret instanceof IResolvableValue) {
                    return new IResolvableValue<List<Object>>(){

                        @Override
                        public List<Object> get() {
                            return Arrays.asList((Object[])((IResolvableValue)ret).get());
                        }
                    };
                }
                return Arrays.asList((Object[])ret);
            }
            case Component: {
                return DataEncoder.decode(is, EncodedType.NBTTagCompound);
            }
            case CropCard: {
                return Crops.instance.getCropCard(is.readString(), is.readString());
            }
            case Double: {
                return is.readDouble();
            }
            case Enchantment: {
                return Enchantment.REGISTRY.getObject((Object)((ResourceLocation)DataEncoder.decode(is, EncodedType.ResourceLocation)));
            }
            case Enum: {
                return is.readVarInt();
            }
            case Float: {
                return Float.valueOf(is.readFloat());
            }
            case Fluid: {
                return FluidRegistry.getFluid((String)is.readString());
            }
            case FluidStack: {
                FluidStack ret = new FluidStack((Fluid)DataEncoder.decode(is, EncodedType.Fluid), is.readInt());
                ret.tag = (NBTTagCompound)DataEncoder.decode(is);
                return ret;
            }
            case FluidTank: {
                return new FluidTank((FluidStack)DataEncoder.decode(is), is.readInt());
            }
            case GameProfile: {
                return new GameProfile((UUID)DataEncoder.decode(is), is.readString());
            }
            case Integer: {
                return is.readInt();
            }
            case InvSlot: {
                ItemStack[] contents = (ItemStack[])DataEncoder.decode(is, EncodedType.Array);
                InvSlot ret = new InvSlot(contents.length);
                for (int i = 0; i < contents.length; ++i) {
                    ret.put(i, contents[i]);
                }
                return ret;
            }
            case Item: {
                return Util.getItem((ResourceLocation)DataEncoder.decode(is, EncodedType.ResourceLocation));
            }
            case ItemStack: {
                Item item = (Item)DataEncoder.decode(is, Item.class);
                byte size = is.readByte();
                short meta = is.readShort();
                NBTTagCompound nbt = (NBTTagCompound)DataEncoder.decode(is);
                ItemStack ret = new ItemStack(item, (int)size, (int)meta);
                ret.setTagCompound(nbt);
                return ret;
            }
            case Long: {
                return is.readLong();
            }
            case NBTTagCompound: {
                return CompressedStreamTools.read((DataInput)is, (NBTSizeTracker)NBTSizeTracker.INFINITE);
            }
            case Null: {
                return null;
            }
            case Object: {
                return new Object();
            }
            case Potion: {
                return Potion.REGISTRY.getObject((Object)((ResourceLocation)DataEncoder.decode(is, EncodedType.ResourceLocation)));
            }
            case ResourceLocation: {
                return new ResourceLocation(is.readString(), is.readString());
            }
            case Short: {
                return is.readShort();
            }
            case String: {
                return is.readString();
            }
            case TileEntity: {
                final IResolvableValue deferredWorld = (IResolvableValue)DataEncoder.decode(is, EncodedType.World);
                final BlockPos pos = (BlockPos)DataEncoder.decode(is, EncodedType.BlockPos);
                return new IResolvableValue<TileEntity>(){

                    @Override
                    public TileEntity get() {
                        World world = (World)deferredWorld.get();
                        if (world == null) {
                            return null;
                        }
                        return world.getTileEntity(pos);
                    }
                };
            }
            case TupleT2: {
                return new Tuple.T2<Object, Object>(DataEncoder.decode(is), DataEncoder.decode(is));
            }
            case TupleT3: {
                return new Tuple.T3<Object, Object, Object>(DataEncoder.decode(is), DataEncoder.decode(is), DataEncoder.decode(is));
            }
            case UUID: {
                return new UUID(is.readLong(), is.readLong());
            }
            case Vec3: {
                return new Vec3d(is.readDouble(), is.readDouble(), is.readDouble());
            }
            case World: {
                final int dimensionId = is.readInt();
                return new IResolvableValue<World>(){

                    @Override
                    public World get() {
                        return IC2.platform.getWorld(dimensionId);
                    }
                };
            }
        }
        throw new IllegalArgumentException("unhandled type: " + (Object)((Object)type));
    }

    public static <T> T getValue(Object decoded) {
        if (decoded instanceof IResolvableValue) {
            return ((IResolvableValue)decoded).get();
        }
        return (T)decoded;
    }

    public static <T> boolean copyValue(T src, T dst) {
        if (src == null || dst == null) {
            return false;
        }
        if (dst instanceof ItemStack) {
            ItemStack srcT = (ItemStack)src;
            ItemStack dstT = (ItemStack)dst;
            StackUtil.copyStack(srcT, dstT);
        } else if (dst instanceof FluidTank) {
            FluidTank srcT = (FluidTank)src;
            FluidTank dstT = (FluidTank)dst;
            dstT.setFluid(srcT.getFluid());
            dstT.setCapacity(srcT.getCapacity());
        } else if (dst instanceof InvSlot) {
            InvSlot srcT = (InvSlot)src;
            InvSlot dstT = (InvSlot)dst;
            if (srcT.size() != dstT.size()) {
                throw new RuntimeException("Can't sync InvSlots with mismatched sizes.");
            }
            for (int i = 0; i < srcT.size(); ++i) {
                if (DataEncoder.copyValue(srcT.get(i), dstT.get(i))) continue;
                dstT.put(i, srcT.get(i));
            }
        } else if (dst instanceof TileEntityComponent) {
            NBTTagCompound nbt = (NBTTagCompound)src;
            ((TileEntityComponent)dst).readFromNbt(nbt);
        } else if (dst instanceof Collection) {
            Collection srcT = (Collection)src;
            Collection dstT = (Collection)dst;
            dstT.clear();
            dstT.addAll(srcT);
        } else {
            return false;
        }
        return true;
    }

    private static Class<?> box(Class<?> clazz) {
        if (clazz == Byte.TYPE) {
            return Byte.class;
        }
        if (clazz == Short.TYPE) {
            return Short.class;
        }
        if (clazz == Integer.TYPE) {
            return Integer.class;
        }
        if (clazz == Long.TYPE) {
            return Long.class;
        }
        if (clazz == Float.TYPE) {
            return Float.class;
        }
        if (clazz == Double.TYPE) {
            return Double.class;
        }
        if (clazz == Boolean.TYPE) {
            return Boolean.class;
        }
        if (clazz == Character.TYPE) {
            return Character.class;
        }
        return clazz;
    }

    private static Class<?> unbox(Class<?> clazz) {
        if (clazz == Byte.class) {
            return Byte.TYPE;
        }
        if (clazz == Short.class) {
            return Short.TYPE;
        }
        if (clazz == Integer.class) {
            return Integer.TYPE;
        }
        if (clazz == Long.class) {
            return Long.TYPE;
        }
        if (clazz == Float.class) {
            return Float.TYPE;
        }
        if (clazz == Double.class) {
            return Double.TYPE;
        }
        if (clazz == Boolean.class) {
            return Boolean.TYPE;
        }
        if (clazz == Character.class) {
            return Character.TYPE;
        }
        return clazz;
    }

    private static int idFromType(EncodedType type) {
        return type.ordinal();
    }

    private static EncodedType typeFromId(int id) {
        if (id < 0 || id >= EncodedType.types.length) {
            throw new IllegalArgumentException("invalid type id: " + id);
        }
        return EncodedType.types[id];
    }

    private static EncodedType typeFromObject(Object o) {
        if (o == null) {
            return EncodedType.Null;
        }
        return DataEncoder.typeFromClass(o.getClass());
    }

    private static EncodedType typeFromClass(Class<?> cls) {
        EncodedType ret;
        if (cls == null) {
            return EncodedType.Null;
        }
        if (cls.isArray()) {
            return EncodedType.Array;
        }
        if (cls.isPrimitive()) {
            cls = DataEncoder.box(cls);
        }
        if ((ret = EncodedType.classToTypeMap.get(cls)) != null) {
            return ret;
        }
        ret = classToTypeCache.get(cls);
        if (ret != null) {
            return ret;
        }
        for (EncodedType type : EncodedType.types) {
            if (type.cls == null || !type.cls.isAssignableFrom(cls)) continue;
            classToTypeCache.put(cls, type);
            return type;
        }
        throw new IllegalStateException("unmatched " + cls);
    }

    private static enum EncodedType {
        Null(null),
        Array(null),
        Byte(Byte.class),
        Short(Short.class),
        Integer(Integer.class),
        Long(Long.class),
        Float(Float.class),
        Double(Double.class),
        Boolean(Boolean.class),
        Character(Character.class),
        String(String.class),
        Enum(Enum.class),
        UUID(UUID.class),
        Block(Block.class),
        Item(Item.class),
        TileEntity(TileEntity.class, false),
        ItemStack(ItemStack.class),
        World(World.class, false),
        NBTTagCompound(NBTTagCompound.class),
        ResourceLocation(ResourceLocation.class),
        GameProfile(GameProfile.class),
        Potion(Potion.class),
        Enchantment(Enchantment.class),
        Achievement(Achievement.class),
        BlockPos(BlockPos.class),
        ChunkPos(ChunkPos.class),
        Vec3(Vec3d.class),
        Fluid(Fluid.class),
        FluidStack(FluidStack.class),
        FluidTank(FluidTank.class),
        InvSlot(InvSlot.class),
        Component(TileEntityComponent.class, false),
        CropCard(CropCard.class),
        TupleT2(Tuple.T2.class),
        TupleT3(Tuple.T3.class),
        Collection(Collection.class),
        Object(Object.class);
        
        final Class<?> cls;
        final boolean threadSafe;
        static final EncodedType[] types;
        static final Map<Class<?>, EncodedType> classToTypeMap;

        private EncodedType(Class<?> cls) {
            this(cls, true);
        }

        private EncodedType(Class<?> cls, boolean threadSafe) {
            this.cls = cls;
            this.threadSafe = threadSafe;
        }

        static {
            types = EncodedType.values();
            classToTypeMap = new IdentityHashMap(types.length - 2);
            for (EncodedType type : types) {
                if (type.cls == null) continue;
                classToTypeMap.put(type.cls, type);
            }
            if (types.length > 255) {
                throw new RuntimeException("too many types");
            }
        }
    }

    private static interface IResolvableValue<T> {
        public T get();
    }

}

