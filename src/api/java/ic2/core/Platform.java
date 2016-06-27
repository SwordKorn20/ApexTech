/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.util.concurrent.ListenableFuture
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.inventory.Container
 *  net.minecraft.inventory.IContainerListener
 *  net.minecraft.network.NetHandlerPlayServer
 *  net.minecraft.potion.Potion
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraft.util.text.TextComponentTranslation
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.common.DimensionManager
 *  net.minecraftforge.fml.common.FMLCommonHandler
 *  net.minecraftforge.fml.common.ObfuscationReflectionHelper
 *  net.minecraftforge.fml.relauncher.Side
 */
package ic2.core;

import com.google.common.util.concurrent.ListenableFuture;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.network.NetworkManager;
import ic2.core.util.SideGateway;
import ic2.core.util.Util;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.potion.Potion;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;

public class Platform {
    public boolean isSimulating() {
        return !FMLCommonHandler.instance().getEffectiveSide().isClient();
    }

    public boolean isRendering() {
        return !this.isSimulating();
    }

    public /* varargs */ void displayError(String error, Object ... args) {
        if (args.length > 0) {
            error = String.format(error, args);
        }
        error = "IndustrialCraft 2 Error\n\n == = IndustrialCraft 2 Error = == \n\n" + error + "\n\n == == == == == == == == == == ==\n";
        error = error.replace("\n", System.getProperty("line.separator"));
        throw new RuntimeException(error);
    }

    public /* varargs */ void displayError(Exception e, String error, Object ... args) {
        if (args.length > 0) {
            error = String.format(error, args);
        }
        this.displayError("An unexpected Exception occured.\n\n" + this.getStackTrace(e) + "\n" + error, new Object[0]);
    }

    public String getStackTrace(Exception e) {
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        e.printStackTrace(printWriter);
        return writer.toString();
    }

    public EntityPlayer getPlayerInstance() {
        return null;
    }

    public World getWorld(int dimId) {
        return DimensionManager.getWorld((int)dimId);
    }

    public World getPlayerWorld() {
        return null;
    }

    public void preInit() {
    }

    public /* varargs */ void messagePlayer(EntityPlayer player, String message, Object ... args) {
        if (player instanceof EntityPlayerMP) {
            TextComponentTranslation msg = args.length > 0 ? new TextComponentTranslation(message, (Object[])this.getMessageComponents(args)) : new TextComponentTranslation(message, new Object[0]);
            ((EntityPlayerMP)player).addChatMessage((ITextComponent)msg);
        }
    }

    public boolean launchGui(EntityPlayer player, IHasGui inventory) {
        if (!Util.isFakePlayer(player, true)) {
            EntityPlayerMP playerMp = (EntityPlayerMP)player;
            playerMp.getNextWindowId();
            playerMp.closeContainer();
            int windowId = playerMp.currentWindowId;
            IC2.network.get(true).initiateGuiDisplay(playerMp, inventory, windowId);
            player.openContainer = inventory.getGuiContainer(player);
            player.openContainer.windowId = windowId;
            player.openContainer.addListener((IContainerListener)playerMp);
            return true;
        }
        return false;
    }

    public boolean launchGuiClient(EntityPlayer player, IHasGui inventory, boolean isAdmin) {
        return false;
    }

    public void profilerStartSection(String section) {
    }

    public void profilerEndSection() {
    }

    public void profilerEndStartSection(String section) {
    }

    public File getMinecraftDir() {
        return new File(".");
    }

    public void playSoundSp(String sound, float f, float g) {
    }

    public void resetPlayerInAirTime(EntityPlayer player) {
        if (!(player instanceof EntityPlayerMP)) {
            return;
        }
        ObfuscationReflectionHelper.setPrivateValue((Class)NetHandlerPlayServer.class, (Object)((EntityPlayerMP)player).connection, (Object)0, (String[])new String[]{"field_147365_f", "floatingTickCount"});
    }

    public int getBlockTexture(Block block, IBlockAccess world, int x, int y, int z, int side) {
        return 0;
    }

    public void removePotion(EntityLivingBase entity, Potion potion) {
        entity.removePotionEffect(potion);
    }

    public void onPostInit() {
    }

    protected /* varargs */ ITextComponent[] getMessageComponents(Object ... args) {
        ITextComponent[] encodedArgs = new ITextComponent[args.length];
        for (int i = 0; i < args.length; ++i) {
            encodedArgs[i] = args[i] instanceof String && ((String)args[i]).startsWith("ic2.") ? new TextComponentTranslation((String)args[i], new Object[0]) : new TextComponentString(args[i].toString());
        }
        return encodedArgs;
    }

    public void requestTick(boolean simulating, Runnable runnable) {
        if (!simulating) {
            throw new IllegalStateException();
        }
        FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(runnable);
    }

    public int getColorMultiplier(IBlockState state, IBlockAccess world, BlockPos pos, int tint) {
        throw new UnsupportedOperationException("client only");
    }
}

