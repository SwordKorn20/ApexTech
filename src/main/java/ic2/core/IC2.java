/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockCactus
 *  net.minecraft.block.BlockDynamicLiquid
 *  net.minecraft.block.BlockStaticLiquid
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.command.ICommand
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLiving
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.monster.EntitySkeleton
 *  net.minecraft.entity.monster.EntityZombie
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.crafting.CraftingManager
 *  net.minecraft.item.crafting.FurnaceRecipes
 *  net.minecraft.item.crafting.IRecipe
 *  net.minecraft.tileentity.TileEntityFurnace
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldProvider
 *  net.minecraftforge.client.event.EntityViewRenderEvent
 *  net.minecraftforge.client.event.EntityViewRenderEvent$FogColors
 *  net.minecraftforge.client.event.EntityViewRenderEvent$FogDensity
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.entity.living.LivingSpawnEvent
 *  net.minecraftforge.event.entity.living.LivingSpawnEvent$SpecialSpawn
 *  net.minecraftforge.event.world.WorldEvent
 *  net.minecraftforge.event.world.WorldEvent$Unload
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidRegistry
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fml.common.IFuelHandler
 *  net.minecraftforge.fml.common.IWorldGenerator
 *  net.minecraftforge.fml.common.Mod
 *  net.minecraftforge.fml.common.Mod$EventHandler
 *  net.minecraftforge.fml.common.SidedProxy
 *  net.minecraftforge.fml.common.event.FMLInitializationEvent
 *  net.minecraftforge.fml.common.event.FMLMissingMappingsEvent
 *  net.minecraftforge.fml.common.event.FMLPostInitializationEvent
 *  net.minecraftforge.fml.common.event.FMLPreInitializationEvent
 *  net.minecraftforge.fml.common.event.FMLServerStartingEvent
 *  net.minecraftforge.fml.common.eventhandler.EventBus
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.common.gameevent.PlayerEvent
 *  net.minecraftforge.fml.common.gameevent.PlayerEvent$PlayerLoggedInEvent
 *  net.minecraftforge.fml.common.gameevent.PlayerEvent$PlayerLoggedOutEvent
 *  net.minecraftforge.fml.common.registry.EntityRegistry
 *  net.minecraftforge.fml.common.registry.GameRegistry
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  net.minecraftforge.oredict.OreDictionary
 *  net.minecraftforge.oredict.OreDictionary$OreRegisterEvent
 *  net.minecraftforge.oredict.RecipeSorter
 *  net.minecraftforge.oredict.RecipeSorter$Category
 *  org.apache.logging.log4j.Logger
 *  org.lwjgl.opengl.GL11
 */
package ic2.core;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.IEnergyNet;
import ic2.api.info.IInfoProvider;
import ic2.api.info.Info;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItemManager;
import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputItemStack;
import ic2.api.tile.ExplosionWhitelist;
import ic2.api.util.IKeyboard;
import ic2.api.util.Keys;
import ic2.core.ChunkLoaderLogic;
import ic2.core.CreativeTabIC2;
import ic2.core.ExplosionIC2;
import ic2.core.IC2Achievements;
import ic2.core.IC2BucketHandler;
import ic2.core.IC2Potion;
import ic2.core.Ic2WorldDecorator;
import ic2.core.Platform;
import ic2.core.TickHandler;
import ic2.core.WorldData;
import ic2.core.apihelper.ApiHelper;
import ic2.core.audio.AudioManager;
import ic2.core.block.BlockIC2Fluid;
import ic2.core.block.EntityDynamite;
import ic2.core.block.EntityItnt;
import ic2.core.block.EntityNuke;
import ic2.core.block.EntityStickyDynamite;
import ic2.core.block.comp.Components;
import ic2.core.block.comp.Obscuration;
import ic2.core.block.generator.tileentity.TileEntitySemifluidGenerator;
import ic2.core.block.heatgenerator.tileentity.TileEntityFluidHeatGenerator;
import ic2.core.block.machine.tileentity.TileEntityBlastFurnace;
import ic2.core.block.machine.tileentity.TileEntityBlockCutter;
import ic2.core.block.machine.tileentity.TileEntityCanner;
import ic2.core.block.machine.tileentity.TileEntityCentrifuge;
import ic2.core.block.machine.tileentity.TileEntityCompressor;
import ic2.core.block.machine.tileentity.TileEntityExtractor;
import ic2.core.block.machine.tileentity.TileEntityFermenter;
import ic2.core.block.machine.tileentity.TileEntityLiquidHeatExchanger;
import ic2.core.block.machine.tileentity.TileEntityMacerator;
import ic2.core.block.machine.tileentity.TileEntityMatter;
import ic2.core.block.machine.tileentity.TileEntityMetalFormer;
import ic2.core.block.machine.tileentity.TileEntityOreWashing;
import ic2.core.block.machine.tileentity.TileEntityRecycler;
import ic2.core.block.state.IIdProvider;
import ic2.core.command.CommandIc2;
import ic2.core.crop.IC2Crops;
import ic2.core.energy.EnergyNetGateway;
import ic2.core.init.BlocksItems;
import ic2.core.init.Localization;
import ic2.core.init.MainConfig;
import ic2.core.init.OreValues;
import ic2.core.init.Rezepte;
import ic2.core.item.ElectricItemManager;
import ic2.core.item.EntityBoatCarbon;
import ic2.core.item.EntityBoatElectric;
import ic2.core.item.EntityBoatRubber;
import ic2.core.item.EntityIC2Boat;
import ic2.core.item.GatewayElectricItemManager;
import ic2.core.item.ItemFluidCell;
import ic2.core.item.tfbp.Tfbp;
import ic2.core.item.tool.EntityMiningLaser;
import ic2.core.item.tool.EntityParticle;
import ic2.core.item.type.CraftingItemType;
import ic2.core.network.NetworkManager;
import ic2.core.recipe.AdvRecipe;
import ic2.core.recipe.AdvShapelessRecipe;
import ic2.core.recipe.GradualRecipe;
import ic2.core.recipe.OreDictionaryEntries;
import ic2.core.recipe.RecipeQArmorDye;
import ic2.core.recipe.ScrapboxRecipeManager;
import ic2.core.recipe.SmeltingRecipes;
import ic2.core.ref.BlockName;
import ic2.core.ref.IItemModelProvider;
import ic2.core.ref.ItemName;
import ic2.core.ref.TeBlock;
import ic2.core.util.ConfigUtil;
import ic2.core.util.ItemInfo;
import ic2.core.util.Keyboard;
import ic2.core.util.Log;
import ic2.core.util.LogCategory;
import ic2.core.util.PriorityExecutor;
import ic2.core.util.SideGateway;
import ic2.core.util.StackUtil;
import ic2.core.util.Util;
import ic2.core.uu.UuIndex;
import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCactus;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.ICommand;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntitySkeleton;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.IFuelHandler;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLMissingMappingsEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

@Mod(modid="IC2", name="IndustrialCraft 2", version="2.5.50-ex19", useMetadata=1, certificateFingerprint="de041f9f6187debbc77034a344134053277aa3b0", dependencies="required-after:Forge@[12.17.0.1940,)", guiFactory="ic2.core.gui.Ic2GuiFactory")
public class IC2
implements IFuelHandler {
    public static final String VERSION = "2.5.50-ex19";
    public static final String MODID = "IC2";
    public static final String RESOURCE_DOMAIN = "ic2";
    private static IC2 instance;
    @SidedProxy(clientSide="ic2.core.PlatformClient", serverSide="ic2.core.Platform")
    public static Platform platform;
    public static SideGateway<NetworkManager> network;
    @SidedProxy(clientSide="ic2.core.util.KeyboardClient", serverSide="ic2.core.util.Keyboard")
    public static Keyboard keyboard;
    @SidedProxy(clientSide="ic2.core.audio.AudioManagerClient", serverSide="ic2.core.audio.AudioManager")
    public static AudioManager audioManager;
    public static Log log;
    public static IC2Achievements achievements;
    public static TickHandler tickHandler;
    public static int cableRenderId;
    public static int fenceRenderId;
    public static int miningPipeRenderId;
    public static int luminatorRenderId;
    public static int cropRenderId;
    public static Random random;
    public static boolean suddenlyHoes;
    public static boolean seasonal;
    public static boolean initialized;
    public static final CreativeTabIC2 tabIC2;
    public static final String textureDomain;
    public static final int setBlockNotify = 1;
    public static final int setBlockUpdate = 2;
    public static final int setBlockNoUpdateFromClient = 4;
    public final PriorityExecutor threadPool = new PriorityExecutor(Math.max(Runtime.getRuntime().availableProcessors(), 2));

    public IC2() {
        Info.ic2ModInstance = IC2.instance = this;
    }

    public static IC2 getInstance() {
        return instance;
    }

    @Mod.EventHandler
    public void load(FMLPreInitializationEvent event) {
        long startTime = System.nanoTime();
        log = new Log(event.getModLog());
        log.debug(LogCategory.General, "Starting pre-init.");
        MainConfig.load();
        Localization.preInit(event.getSourceFile());
        tickHandler = new TickHandler();
        audioManager.initialize();
        ElectricItem.manager = new GatewayElectricItemManager();
        ElectricItem.rawManager = new ElectricItemManager();
        Info.itemInfo = new ItemInfo();
        Keys.instance = keyboard;
        Components.init();
        BlocksItems.init();
        Blocks.OBSIDIAN.setResistance(60.0f);
        Blocks.ENCHANTING_TABLE.setResistance(60.0f);
        Blocks.ENDER_CHEST.setResistance(60.0f);
        Blocks.ANVIL.setResistance(60.0f);
        Blocks.WATER.setResistance(30.0f);
        Blocks.FLOWING_WATER.setResistance(30.0f);
        Blocks.LAVA.setResistance(30.0f);
        ExplosionWhitelist.addWhitelistedBlock(Blocks.BEDROCK);
        SmeltingRecipes.load();
        ScrapboxRecipeManager.setup();
        Tfbp.init();
        TileEntityCanner.init();
        TileEntityCompressor.init();
        TileEntityExtractor.init();
        TileEntityMacerator.init();
        TileEntityRecycler.init();
        TileEntityCentrifuge.init();
        TileEntityMatter.init();
        TileEntityMetalFormer.init();
        TileEntitySemifluidGenerator.init();
        TileEntityOreWashing.init();
        TileEntityFluidHeatGenerator.init();
        TileEntityBlockCutter.init();
        TileEntityBlastFurnace.init();
        TileEntityLiquidHeatExchanger.init();
        TileEntityFermenter.init();
        EntityIC2Boat.init();
        MinecraftForge.EVENT_BUS.register((Object)this);
        RecipeSorter.register((String)"ic2:shaped", (Class)AdvRecipe.class, (RecipeSorter.Category)RecipeSorter.Category.SHAPED, (String)"after:minecraft:shapeless");
        RecipeSorter.register((String)"ic2:shapeless", (Class)AdvShapelessRecipe.class, (RecipeSorter.Category)RecipeSorter.Category.SHAPELESS, (String)"after:ic2:shaped");
        RecipeSorter.register((String)"ic2:gradual", (Class)GradualRecipe.class, (RecipeSorter.Category)RecipeSorter.Category.SHAPELESS, (String)"after:ic2:shapeless");
        RecipeSorter.register((String)"ic2:QSuitDying", (Class)RecipeQArmorDye.class, (RecipeSorter.Category)RecipeSorter.Category.SHAPELESS, (String)"after:ic2:shapeless");
        for (String oreName : OreDictionary.getOreNames()) {
            for (ItemStack ore : OreDictionary.getOres((String)oreName)) {
                this.registerOre(new OreDictionary.OreRegisterEvent(oreName, ore));
            }
        }
        OreDictionaryEntries.load();
        EnergyNet.instance = EnergyNetGateway.init();
        IC2Crops.init();
        IC2Potion.init();
        ApiHelper.preload();
        achievements = new IC2Achievements();
        EntityRegistry.registerModEntity((Class)EntityMiningLaser.class, (String)"MiningLaser", (int)0, (Object)this, (int)160, (int)5, (boolean)true);
        EntityRegistry.registerModEntity((Class)EntityDynamite.class, (String)"Dynamite", (int)1, (Object)this, (int)160, (int)5, (boolean)true);
        EntityRegistry.registerModEntity((Class)EntityStickyDynamite.class, (String)"StickyDynamite", (int)2, (Object)this, (int)160, (int)5, (boolean)true);
        EntityRegistry.registerModEntity((Class)EntityItnt.class, (String)"Itnt", (int)3, (Object)this, (int)160, (int)5, (boolean)true);
        EntityRegistry.registerModEntity((Class)EntityNuke.class, (String)"Nuke", (int)4, (Object)this, (int)160, (int)5, (boolean)true);
        EntityRegistry.registerModEntity((Class)EntityBoatCarbon.class, (String)"BoatCarbon", (int)5, (Object)this, (int)80, (int)3, (boolean)true);
        EntityRegistry.registerModEntity((Class)EntityBoatRubber.class, (String)"BoatRubber", (int)6, (Object)this, (int)80, (int)3, (boolean)true);
        EntityRegistry.registerModEntity((Class)EntityBoatElectric.class, (String)"BoatElectric", (int)7, (Object)this, (int)80, (int)3, (boolean)true);
        EntityRegistry.registerModEntity((Class)EntityParticle.class, (String)"Particle", (int)8, (Object)this, (int)160, (int)1, (boolean)true);
        int d = Integer.parseInt(new SimpleDateFormat("Mdd").format(new Date()));
        suddenlyHoes = (double)d > Math.cbrt(6.4E7) && (double)d < Math.cbrt(6.5939264E7);
        seasonal = (double)d > Math.cbrt(1.089547389E9) && (double)d < Math.cbrt(1.338273208E9);
        GameRegistry.registerWorldGenerator((IWorldGenerator)new Ic2WorldDecorator(), (int)0);
        GameRegistry.registerFuelHandler((IFuelHandler)this);
        GameRegistry.addRecipe((IRecipe)new RecipeQArmorDye());
        MinecraftForge.EVENT_BUS.register((Object)new IC2BucketHandler());
        TeBlock.registerTeMappings();
        Obscuration.ObscurationComponentEventHandler.init();
        platform.preInit();
        initialized = true;
        log.debug(LogCategory.General, "Finished pre-init after %d ms.", (System.nanoTime() - startTime) / 1000000);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        long startTime = System.nanoTime();
        log.debug(LogCategory.General, "Starting init.");
        Rezepte.loadRecipes();
        ScrapboxRecipeManager.load();
        new ChunkLoaderLogic();
        TeBlock.buildDummies();
        log.debug(LogCategory.General, "Finished init after %d ms.", (System.nanoTime() - startTime) / 1000000);
    }

    @Mod.EventHandler
    public void modsLoaded(FMLPostInitializationEvent event) {
        long startTime = System.nanoTime();
        log.debug(LogCategory.General, "Starting post-init.");
        if (!initialized) {
            platform.displayError("IndustrialCraft 2 has failed to initialize properly.", new Object[0]);
        }
        Rezepte.loadFailedRecipes();
        for (IRecipeInput input : ConfigUtil.asRecipeInputList(MainConfig.get(), "misc/additionalValuableOres")) {
            for (ItemStack stack : input.getInputs()) {
                OreValues.add(stack, 1);
            }
        }
        Localization.postInit();
        if (IC2.loadSubModule("bcIntegration")) {
            log.debug(LogCategory.SubModule, "BuildCraft integration module loaded.");
        }
        ArrayList<IRecipeInput> purgedRecipes = new ArrayList<IRecipeInput>();
        purgedRecipes.addAll(ConfigUtil.asRecipeInputList(MainConfig.get(), "recipes/purge"));
        if (ConfigUtil.getBool(MainConfig.get(), "balance/disableEnderChest")) {
            purgedRecipes.add(new RecipeInputItemStack(new ItemStack(Blocks.ENDER_CHEST)));
        }
        Iterator it = CraftingManager.getInstance().getRecipeList().iterator();
        block2 : while (it.hasNext()) {
            IRecipe recipe = (IRecipe)it.next();
            ItemStack output = recipe.getRecipeOutput();
            if (output == null) continue;
            for (IRecipeInput input2 : purgedRecipes) {
                if (!input2.matches(output)) continue;
                it.remove();
                continue block2;
            }
        }
        if (ConfigUtil.getBool(MainConfig.get(), "recipes/smeltToIc2Items")) {
            Map smeltingMap = FurnaceRecipes.instance().getSmeltingList();
            block4 : for (Map.Entry entry : smeltingMap.entrySet()) {
                boolean found = false;
                for (int oreId : OreDictionary.getOreIDs((ItemStack)((ItemStack)entry.getValue()))) {
                    String oreName = OreDictionary.getOreName((int)oreId);
                    for (ItemStack ore : OreDictionary.getOres((String)oreName)) {
                        if (ore.getItem() == null || !Util.getName(ore.getItem()).getResourceDomain().equals("ic2")) continue;
                        entry.setValue(StackUtil.copyWithSize(ore, ((ItemStack)entry.getValue()).stackSize));
                        found = true;
                        break;
                    }
                    if (found) continue block4;
                }
            }
        }
        TileEntityRecycler.initLate();
        UuIndex.instance.init();
        UuIndex.instance.refresh(true);
        platform.onPostInit();
        log.debug(LogCategory.General, "Finished post-init after %d ms.", (System.nanoTime() - startTime) / 1000000);
        log.info(LogCategory.General, "%s version %s loaded.", "IC2", "2.5.50-ex19");
    }

    private static boolean loadSubModule(String name) {
        log.debug(LogCategory.SubModule, "Loading %s submodule: %s.", "IC2", name);
        try {
            Class subModuleClass = IC2.class.getClassLoader().loadClass("ic2." + name + ".SubModule");
            return (Boolean)subModuleClass.getMethod("init", new Class[0]).invoke(null, new Object[0]);
        }
        catch (Throwable t) {
            log.debug(LogCategory.SubModule, "Submodule %s not loaded.", name);
            return false;
        }
    }

    @Mod.EventHandler
    public void serverStart(FMLServerStartingEvent event) {
        event.registerServerCommand((ICommand)new CommandIc2());
    }

    @Mod.EventHandler
    public void onMissingMappings(FMLMissingMappingsEvent event) {
        BlocksItems.onMissingMappings(event);
    }

    public int getBurnTime(ItemStack stack) {
        if (stack != null) {
            FluidStack fs;
            Item item = stack.getItem();
            if (StackUtil.checkItemEquality(stack, BlockName.sapling.getItemStack())) {
                return 80;
            }
            if (item == Items.REEDS) {
                return 50;
            }
            if (item == Item.getItemFromBlock((Block)Blocks.CACTUS)) {
                return 50;
            }
            if (StackUtil.checkItemEquality(stack, ItemName.crafting.getItemStack(CraftingItemType.scrap))) {
                return 350;
            }
            if (StackUtil.checkItemEquality(stack, ItemName.crafting.getItemStack(CraftingItemType.scrap_box))) {
                return 3150;
            }
            if (item == ItemName.fluid_cell.getInstance() && (fs = ((ItemFluidCell)ItemName.fluid_cell.getInstance()).getFluid(stack)) != null && fs.getFluid() == FluidRegistry.LAVA) {
                int ret = TileEntityFurnace.getItemBurnTime((ItemStack)new ItemStack(Items.LAVA_BUCKET));
                return ret * fs.amount / 1000;
            }
        }
        return 0;
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (platform.isSimulating()) {
            keyboard.removePlayerReferences(event.player);
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        WorldData.onWorldUnload(event.getWorld());
    }

    public static void explodeMachineAt(World world, int x, int y, int z, boolean noDrop) {
        ExplosionIC2 explosion = new ExplosionIC2(world, null, 0.5 + (double)x, 0.5 + (double)y, 0.5 + (double)z, 2.5f, 0.75f);
        explosion.destroy(x, y, z, noDrop);
        explosion.doExplosion();
    }

    public static int getSeaLevel(World world) {
        return world.provider.getAverageGroundLevel();
    }

    public static int getWorldHeight(World world) {
        return world.getHeight();
    }

    @SubscribeEvent
    public void registerOre(OreDictionary.OreRegisterEvent event) {
        String oreClass = event.getName();
        ItemStack ore = event.getOre();
        if (!(ore.getItem() instanceof ItemBlock)) {
            return;
        }
        int multiplier = 1;
        if (oreClass.startsWith("dense")) {
            multiplier *= 3;
            oreClass = oreClass.substring("dense".length());
        }
        int value = 0;
        if (oreClass.equals("oreCoal")) {
            value = 1;
        } else if (oreClass.equals("oreCopper") || oreClass.equals("oreTin") || oreClass.equals("oreLead") || oreClass.equals("oreQuartz")) {
            value = 2;
        } else if (oreClass.equals("oreIron") || oreClass.equals("oreGold") || oreClass.equals("oreRedstone") || oreClass.equals("oreLapis") || oreClass.equals("oreSilver")) {
            value = 3;
        } else if (oreClass.equals("oreUranium") || oreClass.equals("oreGemRuby") || oreClass.equals("oreGemGreenSapphire") || oreClass.equals("oreGemSapphire") || oreClass.equals("oreRuby") || oreClass.equals("oreGreenSapphire") || oreClass.equals("oreSapphire")) {
            value = 4;
        } else if (oreClass.equals("oreDiamond") || oreClass.equals("oreEmerald") || oreClass.equals("oreTungsten")) {
            value = 5;
        } else if (oreClass.startsWith("ore")) {
            value = 1;
        }
        if (value > 0 && multiplier >= 1) {
            OreValues.add(ore, value * multiplier);
        }
    }

    @SubscribeEvent
    public void onLivingSpecialSpawn(LivingSpawnEvent.SpecialSpawn event) {
        if (seasonal && (event.getEntityLiving() instanceof EntityZombie || event.getEntityLiving() instanceof EntitySkeleton) && event.getEntityLiving().worldObj.rand.nextFloat() < 0.1f) {
            EntityLiving entity = (EntityLiving)event.getEntityLiving();
            for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
                entity.setDropChance(slot, Float.NEGATIVE_INFINITY);
            }
            if (entity instanceof EntityZombie) {
                entity.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemName.nano_saber.getItemStack());
            }
            if (entity.worldObj.rand.nextFloat() < 0.1f) {
                entity.setItemStackToSlot(EntityEquipmentSlot.HEAD, ItemName.quantum_helmet.getItemStack());
                entity.setItemStackToSlot(EntityEquipmentSlot.CHEST, ItemName.quantum_chestplate.getItemStack());
                entity.setItemStackToSlot(EntityEquipmentSlot.LEGS, ItemName.quantum_leggings.getItemStack());
                entity.setItemStackToSlot(EntityEquipmentSlot.FEET, ItemName.quantum_boots.getItemStack());
            } else {
                entity.setItemStackToSlot(EntityEquipmentSlot.HEAD, ItemName.nano_helmet.getItemStack());
                entity.setItemStackToSlot(EntityEquipmentSlot.CHEST, ItemName.nano_chestplate.getItemStack());
                entity.setItemStackToSlot(EntityEquipmentSlot.LEGS, ItemName.nano_leggings.getItemStack());
                entity.setItemStackToSlot(EntityEquipmentSlot.FEET, ItemName.nano_boots.getItemStack());
            }
        }
    }

    @SubscribeEvent
    @SideOnly(value=Side.CLIENT)
    public void onViewRenderFogDensity(EntityViewRenderEvent.FogDensity event) {
        if (!(event.getState().getBlock() instanceof BlockIC2Fluid)) {
            return;
        }
        event.setCanceled(true);
        Fluid fluid = ((BlockIC2Fluid)event.getState().getBlock()).getFluid();
        GL11.glFogi((int)2917, (int)2048);
        event.setDensity((float)Util.map(Math.abs(fluid.getDensity()), 20000.0, 2.0));
    }

    @SubscribeEvent
    @SideOnly(value=Side.CLIENT)
    public void onViewRenderFogColors(EntityViewRenderEvent.FogColors event) {
        if (!(event.getState().getBlock() instanceof BlockIC2Fluid)) {
            return;
        }
        int color = ((BlockIC2Fluid)event.getState().getBlock()).getColor();
        event.setRed((float)(color >>> 16 & 255) / 255.0f);
        event.setGreen((float)(color >>> 8 & 255) / 255.0f);
        event.setBlue((float)(color & 255) / 255.0f);
    }

    static {
        try {
            new BlockPos(1, 2, 3).add(2, 3, 4);
        }
        catch (Throwable t) {
            throw new Error("IC2 is incompatible with this environment, use the normal IC2 version, not the dev one.", t);
        }
        instance = null;
        network = new SideGateway("ic2.core.network.NetworkManager", "ic2.core.network.NetworkManagerClient");
        random = new Random();
        suddenlyHoes = false;
        seasonal = false;
        initialized = false;
        tabIC2 = new CreativeTabIC2();
        textureDomain = "IC2".toLowerCase(Locale.ENGLISH);
    }
}

