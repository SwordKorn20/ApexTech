/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.util.concurrent.ListenableFuture
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.gui.GuiIngame
 *  net.minecraft.client.gui.GuiNewChat
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.multiplayer.WorldClient
 *  net.minecraft.client.renderer.RenderItem
 *  net.minecraft.client.renderer.color.BlockColors
 *  net.minecraft.client.renderer.color.IBlockColor
 *  net.minecraft.client.renderer.entity.Render
 *  net.minecraft.client.renderer.entity.RenderManager
 *  net.minecraft.client.renderer.entity.RenderSnowball
 *  net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer
 *  net.minecraft.entity.item.EntityBoat
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.profiler.Profiler
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.text.ITextComponent
 *  net.minecraft.util.text.TextComponentString
 *  net.minecraft.util.text.TextComponentTranslation
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldProvider
 *  net.minecraftforge.client.model.ICustomModelLoader
 *  net.minecraftforge.client.model.IModel
 *  net.minecraftforge.client.model.ModelLoaderRegistry
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fml.client.registry.ClientRegistry
 *  net.minecraftforge.fml.client.registry.IRenderFactory
 *  net.minecraftforge.fml.client.registry.RenderingRegistry
 *  net.minecraftforge.fml.common.FMLLog
 *  net.minecraftforge.fml.common.eventhandler.EventBus
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  org.lwjgl.opengl.Display
 */
package ic2.core;

import com.google.common.util.concurrent.ListenableFuture;
import ic2.core.GuiOverlayer;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.Platform;
import ic2.core.audio.AudioManager;
import ic2.core.audio.PositionSpec;
import ic2.core.block.EntityDynamite;
import ic2.core.block.EntityIC2Explosive;
import ic2.core.block.Ic2Leaves;
import ic2.core.block.KineticGeneratorRenderer;
import ic2.core.block.RenderBlockWall;
import ic2.core.block.RenderExplosiveBlock;
import ic2.core.block.generator.tileentity.TileEntityWaterGenerator;
import ic2.core.block.generator.tileentity.TileEntityWindGenerator;
import ic2.core.block.kineticgenerator.tileentity.TileEntityWaterKineticGenerator;
import ic2.core.block.kineticgenerator.tileentity.TileEntityWindKineticGenerator;
import ic2.core.block.personal.TileEntityPersonalChest;
import ic2.core.block.personal.TileEntityPersonalChestRenderer;
import ic2.core.block.wiring.CableModel;
import ic2.core.item.ElectricItemTooltipHandler;
import ic2.core.item.EntityIC2Boat;
import ic2.core.item.FluidCellModel;
import ic2.core.item.RenderIC2Boat;
import ic2.core.item.tool.EntityMiningLaser;
import ic2.core.item.tool.RenderCrossed;
import ic2.core.item.tool.RenderObscurator;
import ic2.core.model.Ic2ModelLoader;
import ic2.core.network.RpcHandler;
import ic2.core.ref.BlockName;
import ic2.core.ref.FluidName;
import ic2.core.ref.IBlockModelProvider;
import ic2.core.ref.IFluidModelProvider;
import ic2.core.ref.IItemModelProvider;
import ic2.core.ref.ItemName;
import ic2.core.util.Log;
import ic2.core.util.LogCategory;
import ic2.core.util.Util;
import java.awt.Component;
import java.io.File;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.opengl.Display;

@SideOnly(value=Side.CLIENT)
public class PlatformClient
extends Platform {
    private final Minecraft mc = Minecraft.getMinecraft();

    @Override
    public void preInit() {
        Object provider;
        for (BlockName name22 : BlockName.values) {
            provider = name22.getInstance();
            if (provider == null) {
                IC2.log.warn(LogCategory.Block, "The block " + (Object)((Object)name22) + " is not initialized.");
                continue;
            }
            ((IBlockModelProvider)provider).registerModels(name22);
        }
        for (BlockName name : ItemName.values) {
            provider = name.getInstance();
            if (provider == null) {
                IC2.log.warn(LogCategory.Item, "The item " + (Object)((Object)name) + " is not initialized.");
                continue;
            }
            ((IItemModelProvider)provider).registerModels((ItemName)((Object)name));
        }
        for (BlockName name2 : FluidName.values) {
            provider = name2.getInstance();
            if (provider == null) {
                IC2.log.warn(LogCategory.Block, "The fluid " + (Object)((Object)name2) + " is not initialized.");
                continue;
            }
            if (!(provider instanceof IFluidModelProvider)) continue;
            ((IFluidModelProvider)provider).registerModels((FluidName)((Object)name2));
        }
        Ic2ModelLoader loader = new Ic2ModelLoader();
        loader.register("models/block/cf/wall", (IModel)new RenderBlockWall());
        loader.register("models/block/wiring/cable", (IModel)new CableModel());
        loader.register("models/item/cell/fluid_cell", (IModel)new FluidCellModel());
        loader.register("models/item/tool/electric/obscurator", (IModel)new RenderObscurator());
        ModelLoaderRegistry.registerLoader((ICustomModelLoader)loader);
        ClientRegistry.bindTileEntitySpecialRenderer((Class)TileEntityPersonalChest.class, (TileEntitySpecialRenderer)new TileEntityPersonalChestRenderer());
        KineticGeneratorRenderer kineticRenderer = new KineticGeneratorRenderer();
        ClientRegistry.bindTileEntitySpecialRenderer((Class)TileEntityWindKineticGenerator.class, kineticRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer((Class)TileEntityWaterKineticGenerator.class, kineticRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer((Class)TileEntityWindGenerator.class, kineticRenderer);
        ClientRegistry.bindTileEntitySpecialRenderer((Class)TileEntityWaterGenerator.class, kineticRenderer);
        RenderingRegistry.registerEntityRenderingHandler((Class)EntityIC2Explosive.class, (IRenderFactory)new IRenderFactory<EntityIC2Explosive>(){

            public Render<EntityIC2Explosive> createRenderFor(RenderManager manager) {
                return new RenderExplosiveBlock(manager);
            }
        });
        RenderingRegistry.registerEntityRenderingHandler((Class)EntityMiningLaser.class, (IRenderFactory)new IRenderFactory<EntityMiningLaser>(){

            public Render<EntityMiningLaser> createRenderFor(RenderManager manager) {
                return new RenderCrossed(manager, new ResourceLocation(IC2.textureDomain, "textures/models/laser.png"));
            }
        });
        RenderingRegistry.registerEntityRenderingHandler((Class)EntityIC2Boat.class, (IRenderFactory)new IRenderFactory<EntityBoat>(){

            public Render<EntityBoat> createRenderFor(RenderManager manager) {
                return new RenderIC2Boat(manager);
            }
        });
        RenderingRegistry.registerEntityRenderingHandler((Class)EntityDynamite.class, (IRenderFactory)new IRenderFactory<EntityDynamite>(){

            public Render<EntityDynamite> createRenderFor(RenderManager manager) {
                return new RenderSnowball(manager, ItemName.dynamite.getInstance(), PlatformClient.this.mc.getRenderItem());
            }
        });
    }

    @Override
    public /* varargs */ void displayError(String error, Object ... args) {
        if (!this.mc.isCallingFromMinecraftThread()) {
            super.displayError(error, args);
            return;
        }
        if (args.length > 0) {
            error = String.format(error, args);
        }
        error = "IndustrialCraft 2 Error\n\n" + error;
        String dialogError = error.replaceAll("([^\n]{80,}?) ", "$1\n");
        error = error.replace("\n", System.getProperty("line.separator"));
        dialogError = dialogError.replace("\n", System.getProperty("line.separator"));
        FMLLog.severe((String)"%s", (Object[])new Object[]{error});
        this.mc.setIngameNotInFocus();
        try {
            Display.destroy();
            JFrame frame = new JFrame("IndustrialCraft 2 Error");
            frame.setUndecorated(true);
            frame.setVisible(true);
            frame.setLocationRelativeTo(null);
            JOptionPane.showMessageDialog(frame, dialogError, "IndustrialCraft 2 Error", 0);
        }
        catch (Throwable t) {
            IC2.log.error(LogCategory.General, t, "Exception caught while showing an error.");
        }
        Util.exit(1);
    }

    @Override
    public EntityPlayer getPlayerInstance() {
        return this.mc.thePlayer;
    }

    @Override
    public World getWorld(int dimId) {
        if (this.isSimulating()) {
            return super.getWorld(dimId);
        }
        WorldClient world = this.mc.theWorld;
        return world.provider.getDimension() == dimId ? world : null;
    }

    @Override
    public World getPlayerWorld() {
        return this.mc.theWorld;
    }

    @Override
    public /* varargs */ void messagePlayer(EntityPlayer player, String message, Object ... args) {
        if (args.length > 0) {
            this.mc.ingameGUI.getChatGUI().printChatMessage((ITextComponent)new TextComponentTranslation(message, (Object[])this.getMessageComponents(args)));
        } else {
            this.mc.ingameGUI.getChatGUI().printChatMessage((ITextComponent)new TextComponentString(message));
        }
    }

    @Override
    public boolean launchGuiClient(EntityPlayer player, IHasGui inventory, boolean isAdmin) {
        this.mc.displayGuiScreen(inventory.getGui(player, isAdmin));
        return true;
    }

    @Override
    public void profilerStartSection(String section) {
        if (this.isRendering()) {
            this.mc.mcProfiler.startSection(section);
        } else {
            super.profilerStartSection(section);
        }
    }

    @Override
    public void profilerEndSection() {
        if (this.isRendering()) {
            this.mc.mcProfiler.endSection();
        } else {
            super.profilerEndSection();
        }
    }

    @Override
    public void profilerEndStartSection(String section) {
        if (this.isRendering()) {
            this.mc.mcProfiler.endStartSection(section);
        } else {
            super.profilerEndStartSection(section);
        }
    }

    @Override
    public File getMinecraftDir() {
        return this.mc.mcDataDir;
    }

    @Override
    public void playSoundSp(String sound, float f, float g) {
        IC2.audioManager.playOnce((Object)this.getPlayerInstance(), PositionSpec.Hand, sound, true, IC2.audioManager.getDefaultVolume());
    }

    @Override
    public void onPostInit() {
        MinecraftForge.EVENT_BUS.register((Object)new GuiOverlayer(Minecraft.getMinecraft()));
        new RpcHandler();
        new ElectricItemTooltipHandler();
        Ic2Leaves leaves = (Ic2Leaves)BlockName.leaves.getInstance();
        this.mc.getBlockColors().registerBlockColorHandler(new IBlockColor(){

            public int colorMultiplier(IBlockState state, IBlockAccess worldIn, BlockPos pos, int tintIndex) {
                return 6723908;
            }
        }, new Block[]{leaves});
    }

    @Override
    public void requestTick(boolean simulating, Runnable runnable) {
        if (simulating) {
            super.requestTick(simulating, runnable);
        } else {
            this.mc.addScheduledTask(runnable);
        }
    }

    @Override
    public int getColorMultiplier(IBlockState state, IBlockAccess world, BlockPos pos, int tint) {
        return this.mc.getBlockColors().colorMultiplier(state, world, pos, tint);
    }

}

