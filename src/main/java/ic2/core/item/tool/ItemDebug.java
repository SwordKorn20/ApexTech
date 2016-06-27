/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.ITickable
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.RayTraceResult$Type
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.FMLCommonHandler
 *  net.minecraftforge.fml.relauncher.Side
 */
package ic2.core.item.tool;

import com.mojang.authlib.GameProfile;
import ic2.api.crops.CropCard;
import ic2.api.item.IBoxable;
import ic2.api.item.IDebuggable;
import ic2.api.item.IElectricItemManager;
import ic2.api.item.ISpecialElectricItem;
import ic2.api.reactor.IReactor;
import ic2.api.tile.IEnergyStorage;
import ic2.core.IC2;
import ic2.core.Platform;
import ic2.core.WorldData;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.comp.Energy;
import ic2.core.block.comp.Redstone;
import ic2.core.block.comp.TileEntityComponent;
import ic2.core.block.generator.tileentity.TileEntityBaseGenerator;
import ic2.core.block.personal.IPersonalBlock;
import ic2.core.crop.TileEntityCrop;
import ic2.core.energy.EnergyNetLocal;
import ic2.core.item.InfiniteElectricItemManager;
import ic2.core.item.ItemIC2;
import ic2.core.network.NetworkManager;
import ic2.core.ref.ItemName;
import ic2.core.util.Keyboard;
import ic2.core.util.Log;
import ic2.core.util.LogCategory;
import ic2.core.util.SideGateway;
import ic2.core.util.StackUtil;
import ic2.core.util.Util;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;

public class ItemDebug
extends ItemIC2
implements ISpecialElectricItem,
IBoxable {
    private static IElectricItemManager manager = null;

    public ItemDebug() {
        super(ItemName.debug_item);
        this.setHasSubtypes(false);
        if (!Util.inDev()) {
            this.setCreativeTab(null);
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        TileEntity tileentity = world.getTileEntity(pos);
        if (tileentity instanceof IDebuggable) {
            if (world.isRemote) {
                return EnumActionResult.PASS;
            }
            IDebuggable dbg = (IDebuggable)tileentity;
            if (!dbg.isDebuggable() || world.isRemote) return world.isRemote ? EnumActionResult.PASS : EnumActionResult.SUCCESS;
            IC2.platform.messagePlayer(player, dbg.getDebugText(), new Object[0]);
            return world.isRemote ? EnumActionResult.PASS : EnumActionResult.SUCCESS;
        }
        NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(stack);
        int modeIdx = nbtData.getInteger("mode");
        if (modeIdx > Mode.modes.length) {
            modeIdx = 0;
        }
        Mode mode = Mode.modes[modeIdx];
        if (IC2.keyboard.isModeSwitchKeyDown(player)) {
            if (world.isRemote) return EnumActionResult.SUCCESS;
            mode = Mode.modes[(mode.ordinal() + 1) % Mode.modes.length];
            nbtData.setInteger("mode", mode.ordinal());
            IC2.platform.messagePlayer(player, "Debug Item Mode: " + mode.getName(), new Object[0]);
            return EnumActionResult.SUCCESS;
        }
        ByteArrayOutputStream consoleBuffer = new ByteArrayOutputStream();
        PrintStream console = new PrintStream(consoleBuffer);
        ByteArrayOutputStream chatBuffer = new ByteArrayOutputStream();
        PrintStream chat = new PrintStream(chatBuffer);
        switch (mode) {
            Object te;
            case InterfacesFields: {
                RayTraceResult position = this.rayTrace(world, player, true);
                if (position == null) {
                    return EnumActionResult.PASS;
                }
                RayTraceResult entityPosition = Util.traceEntities(player, position.hitVec, true);
                if (entityPosition != null) {
                    position = entityPosition;
                }
                String plat = FMLCommonHandler.instance().getSide().isClient() ? (!world.isRemote ? "sp server" : (player.getServer() == null ? "mp client" : "sp client")) : "mp server";
                if (position.typeOfHit == RayTraceResult.Type.BLOCK) {
                    pos = position.getBlockPos();
                    IBlockState state = world.getBlockState(pos);
                    Block block = state.getBlock();
                    TileEntity te2 = world.getTileEntity(pos);
                    String message = String.format("[%s] block state: %s%nname: %s%ncls: %s%nte: %s", new Object[]{plat, state.getActualState((IBlockAccess)world, pos), block.getUnlocalizedName(), block.getClass().getName(), te2});
                    chat.println(message);
                    console.println(message);
                    if (te2 != null) {
                        message = "[" + (String)plat + "] interfaces:";
                        Class c = te2.getClass();
                        do {
                            for (Class i : c.getInterfaces()) {
                                message = message + " " + i.getName();
                            }
                        } while ((c = c.getSuperclass()) != null);
                        chat.println(message);
                        console.println(message);
                    }
                    console.println("block fields:");
                    ItemDebug.dumpObjectFields(console, (Object)block);
                    if (te2 == null) break;
                    console.println("tile entity fields:");
                    ItemDebug.dumpObjectFields(console, (Object)te2);
                    break;
                }
                if (position.typeOfHit != RayTraceResult.Type.ENTITY) return EnumActionResult.PASS;
                String message = "[" + (String)plat + "] entity: " + (Object)position.entityHit;
                chat.println(message);
                console.println(message);
                if (!(position.entityHit instanceof EntityItem)) break;
                ItemStack entStack = ((EntityItem)position.entityHit).getEntityItem();
                String name = Util.getName(entStack.getItem()).toString();
                message = "[" + (String)plat + "] item id: " + name + " meta: " + entStack.getItemDamage() + " size: " + entStack.stackSize + " name: " + entStack.getUnlocalizedName();
                chat.println(message);
                console.println(message);
                console.println("NBT: " + (Object)entStack.getTagCompound());
                break;
            }
            case TileData: {
                if (world.isRemote) {
                    return EnumActionResult.PASS;
                }
                TileEntity tileEntity = world.getTileEntity(pos);
                if (tileEntity instanceof TileEntityBlock) {
                    te = (TileEntityBlock)tileEntity;
                    chat.println("Block: Active=" + te.getActive() + " Facing=" + (Object)te.getFacing());
                    for (TileEntityComponent comp : te.getComponents()) {
                        if (comp instanceof Energy) {
                            Energy energy = (Energy)comp;
                            chat.printf("Energy: %.2f / %.2f%n", energy.getEnergy(), energy.getCapacity());
                            continue;
                        }
                        if (!(comp instanceof Redstone)) continue;
                        Redstone redstone = (Redstone)comp;
                        chat.printf("Redstone: %d%n", redstone.getRedstoneInput());
                    }
                }
                if (tileEntity instanceof TileEntityBaseGenerator) {
                    te = (TileEntityBaseGenerator)tileEntity;
                    chat.println("BaseGen: Fuel=" + te.fuel);
                }
                if (tileEntity instanceof IEnergyStorage) {
                    te = (IEnergyStorage)tileEntity;
                    chat.println("EnergyStorage: Stored=" + te.getStored());
                }
                if (tileEntity instanceof IReactor) {
                    te = (IReactor)tileEntity;
                    chat.println("Reactor: Heat=" + te.getHeat() + " MaxHeat=" + te.getMaxHeat() + " HEM=" + te.getHeatEffectModifier() + " Output=" + te.getReactorEnergyOutput());
                }
                if (tileEntity instanceof IPersonalBlock) {
                    te = (IPersonalBlock)tileEntity;
                    chat.println("PersonalBlock: CanAccess=" + te.permitsAccess(player.getGameProfile()));
                }
                if (!(tileEntity instanceof TileEntityCrop)) break;
                te = (TileEntityCrop)tileEntity;
                CropCard crop = te.getCrop();
                String name = crop != null ? crop.getOwner() + ":" + crop.getName() : "none";
                chat.printf("Crop: Crop=%s Size=%d Growth=%d Gain=%d Resistance=%d Nutrients=%d Water=%d GrowthPoints=%d%n", name, te.getCurrentSize(), te.getStatGrowth(), te.getStatGain(), te.getStatResistance(), te.getStorageNutrient(), te.getStorageWater(), te.getGrowthPoints());
                break;
            }
            case EnergyNet: {
                if (world.isRemote) {
                    return EnumActionResult.PASS;
                }
                EnergyNetLocal enet = WorldData.get((World)world).energyNet;
                if (enet != null && enet.dumpDebugInfo(console, chat, pos)) break;
                return EnumActionResult.PASS;
            }
            case Accelerate: 
            case AccelerateX100: {
                if (world.isRemote) {
                    return EnumActionResult.PASS;
                }
                te = world.getTileEntity(pos);
                if (te == null) {
                    chat.println("No tile entity.");
                    break;
                }
                if (!(te instanceof ITickable)) break;
                ITickable tickable = (ITickable)te;
                int count = mode == Mode.Accelerate ? 1000 : 100000;
                chat.println("Running " + count + " ticks on " + te + ".");
                int changes = 0;
                int interruptCount = -1;
                for (int i = 0; i < count; ++i) {
                    if (te.isInvalid()) {
                        ++changes;
                        te = world.getTileEntity(pos);
                        if (!(te instanceof ITickable) || te.isInvalid()) {
                            interruptCount = i;
                            break;
                        }
                        tickable = (ITickable)te;
                    }
                    tickable.update();
                }
                if (changes > 0) {
                    if (interruptCount != -1) {
                        chat.println("The tile entity changed " + changes + " time(s), interrupted after " + interruptCount + " updates.");
                        break;
                    }
                    chat.println("The tile entity changed " + changes + " time(s).");
                    break;
                } else {
                    break;
                }
            }
        }
        console.flush();
        chat.flush();
        if (world.isRemote) {
            try {
                consoleBuffer.writeTo(new FileOutputStream(FileDescriptor.out));
            }
            catch (IOException e) {
                IC2.log.warn(LogCategory.Item, e, "Stdout write failed.");
            }
            for (String line : chatBuffer.toString().split("[\\r\\n]+")) {
                IC2.platform.messagePlayer(player, line, new Object[0]);
            }
            return world.isRemote ? EnumActionResult.PASS : EnumActionResult.SUCCESS;
        }
        if (!(player instanceof EntityPlayerMP)) return world.isRemote ? EnumActionResult.PASS : EnumActionResult.SUCCESS;
        try {
            IC2.network.get(true).sendConsole((EntityPlayerMP)player, consoleBuffer.toString("UTF-8"));
            IC2.network.get(true).sendChat((EntityPlayerMP)player, chatBuffer.toString("UTF-8"));
            return world.isRemote ? EnumActionResult.PASS : EnumActionResult.SUCCESS;
        }
        catch (UnsupportedEncodingException e) {
            IC2.log.warn(LogCategory.Item, e, "String encoding failed.");
        }
        return world.isRemote ? EnumActionResult.PASS : EnumActionResult.SUCCESS;
    }

    private static void dumpObjectFields(PrintStream ps, Object o) {
        Class fieldDeclaringClass = o.getClass();
        do {
            Field[] fields;
            for (Field field : fields = fieldDeclaringClass.getDeclaredFields()) {
                if ((field.getModifiers() & 8) != 0 && (fieldDeclaringClass == Block.class || fieldDeclaringClass == TileEntity.class)) continue;
                boolean accessible = field.isAccessible();
                field.setAccessible(true);
                try {
                    ArrayList<Object> value = field.get(o);
                    ps.println(field.getName() + " class: " + fieldDeclaringClass.getName() + " type: " + field.getType());
                    ps.println("    identity hash: " + System.identityHashCode(o) + " hash: " + o.hashCode() + " modifiers: " + field.getModifiers());
                    if (field.getType().isArray()) {
                        ArrayList<Object> array = new ArrayList<Object>();
                        for (int i = 0; i < Array.getLength(value); ++i) {
                            array.add(Array.get(value, i));
                        }
                        value = array;
                    }
                    if (value instanceof Iterable) {
                        ps.println("    values:");
                        int i = 0;
                        Iterator it = ((Iterable)value).iterator();
                        while (it.hasNext()) {
                            ps.println("      [" + i + "] " + ItemDebug.getValueString(it.next()));
                            ++i;
                        }
                    } else {
                        ps.println("    value: " + ItemDebug.getValueString(value));
                    }
                }
                catch (IllegalAccessException e) {
                    ps.println("name: " + fieldDeclaringClass.getName() + "." + field.getName() + " type: " + field.getType() + " value: <can't access>");
                }
                catch (NullPointerException e) {
                    ps.println("name: " + fieldDeclaringClass.getName() + "." + field.getName() + " type: " + field.getType() + " value: <null>");
                }
                field.setAccessible(accessible);
            }
        } while ((fieldDeclaringClass = fieldDeclaringClass.getSuperclass()) != null);
    }

    private static String getValueString(Object o) {
        if (o == null) {
            return "<null>";
        }
        String ret = o.toString();
        if (o.getClass().isArray()) {
            for (int i = 0; i < Array.getLength(o); ++i) {
                ret = ret + " [" + i + "] " + Array.get(o, i);
            }
        }
        if (ret.length() > 100) {
            ret = ret.substring(0, 90) + "... (" + ret.length() + " more)";
        }
        return ret;
    }

    @Override
    public IElectricItemManager getManager(ItemStack stack) {
        if (manager == null) {
            manager = new InfiniteElectricItemManager();
        }
        return manager;
    }

    @Override
    public boolean canBeStoredInToolbox(ItemStack itemstack) {
        return true;
    }

    private static enum Mode {
        InterfacesFields("Interfaces and Fields"),
        TileData("Tile Data"),
        EnergyNet("Energy Net"),
        Accelerate("Accelerate"),
        AccelerateX100("Accelerate x100");
        
        static final Mode[] modes;
        private final String name;

        private Mode(String name) {
            this.name = name;
        }

        String getName() {
            return this.name;
        }

        static {
            modes = Mode.values();
        }
    }

}

