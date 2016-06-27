/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.Loader
 *  net.minecraftforge.fml.common.ModContainer
 */
package ic2.core.ref;

import ic2.core.block.TileEntityBarrel;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.TileEntityWall;
import ic2.core.block.generator.tileentity.TileEntityCreativeGenerator;
import ic2.core.block.generator.tileentity.TileEntityGenerator;
import ic2.core.block.generator.tileentity.TileEntityGeoGenerator;
import ic2.core.block.generator.tileentity.TileEntityKineticGenerator;
import ic2.core.block.generator.tileentity.TileEntityRTGenerator;
import ic2.core.block.generator.tileentity.TileEntitySemifluidGenerator;
import ic2.core.block.generator.tileentity.TileEntitySolarGenerator;
import ic2.core.block.generator.tileentity.TileEntityStirlingGenerator;
import ic2.core.block.generator.tileentity.TileEntityWaterGenerator;
import ic2.core.block.generator.tileentity.TileEntityWindGenerator;
import ic2.core.block.heatgenerator.tileentity.TileEntityElectricHeatGenerator;
import ic2.core.block.heatgenerator.tileentity.TileEntityFluidHeatGenerator;
import ic2.core.block.heatgenerator.tileentity.TileEntityRTHeatGenerator;
import ic2.core.block.heatgenerator.tileentity.TileEntitySolidHeatGenerator;
import ic2.core.block.kineticgenerator.tileentity.TileEntityElectricKineticGenerator;
import ic2.core.block.kineticgenerator.tileentity.TileEntityManualKineticGenerator;
import ic2.core.block.kineticgenerator.tileentity.TileEntitySteamKineticGenerator;
import ic2.core.block.kineticgenerator.tileentity.TileEntityStirlingKineticGenerator;
import ic2.core.block.kineticgenerator.tileentity.TileEntityWaterKineticGenerator;
import ic2.core.block.kineticgenerator.tileentity.TileEntityWindKineticGenerator;
import ic2.core.block.machine.tileentity.ITnt;
import ic2.core.block.machine.tileentity.TileEntityAdvMiner;
import ic2.core.block.machine.tileentity.TileEntityBetterItemBuffer;
import ic2.core.block.machine.tileentity.TileEntityBlastFurnace;
import ic2.core.block.machine.tileentity.TileEntityBlockCutter;
import ic2.core.block.machine.tileentity.TileEntityCanner;
import ic2.core.block.machine.tileentity.TileEntityCentrifuge;
import ic2.core.block.machine.tileentity.TileEntityChunkloader;
import ic2.core.block.machine.tileentity.TileEntityCompressor;
import ic2.core.block.machine.tileentity.TileEntityCondenser;
import ic2.core.block.machine.tileentity.TileEntityCropHarvester;
import ic2.core.block.machine.tileentity.TileEntityCropmatron;
import ic2.core.block.machine.tileentity.TileEntityElectricFurnace;
import ic2.core.block.machine.tileentity.TileEntityElectrolyzer;
import ic2.core.block.machine.tileentity.TileEntityExtractor;
import ic2.core.block.machine.tileentity.TileEntityFermenter;
import ic2.core.block.machine.tileentity.TileEntityFluidBottler;
import ic2.core.block.machine.tileentity.TileEntityFluidDistributor;
import ic2.core.block.machine.tileentity.TileEntityFluidRegulator;
import ic2.core.block.machine.tileentity.TileEntityInduction;
import ic2.core.block.machine.tileentity.TileEntityIronFurnace;
import ic2.core.block.machine.tileentity.TileEntityItemBuffer;
import ic2.core.block.machine.tileentity.TileEntityLiquidHeatExchanger;
import ic2.core.block.machine.tileentity.TileEntityMacerator;
import ic2.core.block.machine.tileentity.TileEntityMagnetizer;
import ic2.core.block.machine.tileentity.TileEntityMatter;
import ic2.core.block.machine.tileentity.TileEntityMetalFormer;
import ic2.core.block.machine.tileentity.TileEntityMiner;
import ic2.core.block.machine.tileentity.TileEntityNuke;
import ic2.core.block.machine.tileentity.TileEntityOreWashing;
import ic2.core.block.machine.tileentity.TileEntityPatternStorage;
import ic2.core.block.machine.tileentity.TileEntityPump;
import ic2.core.block.machine.tileentity.TileEntityRecycler;
import ic2.core.block.machine.tileentity.TileEntityReplicator;
import ic2.core.block.machine.tileentity.TileEntityScanner;
import ic2.core.block.machine.tileentity.TileEntitySolarDestiller;
import ic2.core.block.machine.tileentity.TileEntitySolidCanner;
import ic2.core.block.machine.tileentity.TileEntitySortingMachine;
import ic2.core.block.machine.tileentity.TileEntitySteamGenerator;
import ic2.core.block.machine.tileentity.TileEntityTank;
import ic2.core.block.machine.tileentity.TileEntityTeleporter;
import ic2.core.block.machine.tileentity.TileEntityTerra;
import ic2.core.block.machine.tileentity.TileEntityTesla;
import ic2.core.block.personal.TileEntityEnergyOMat;
import ic2.core.block.personal.TileEntityPersonalChest;
import ic2.core.block.personal.TileEntityTradeOMat;
import ic2.core.block.reactor.tileentity.TileEntityNuclearReactorElectric;
import ic2.core.block.reactor.tileentity.TileEntityRCI_LZH;
import ic2.core.block.reactor.tileentity.TileEntityRCI_RSH;
import ic2.core.block.reactor.tileentity.TileEntityReactorAccessHatch;
import ic2.core.block.reactor.tileentity.TileEntityReactorChamberElectric;
import ic2.core.block.reactor.tileentity.TileEntityReactorFluidPort;
import ic2.core.block.reactor.tileentity.TileEntityReactorRedstonePort;
import ic2.core.block.state.IIdProvider;
import ic2.core.block.wiring.TileEntityCable;
import ic2.core.block.wiring.TileEntityCableDetector;
import ic2.core.block.wiring.TileEntityCableSplitter;
import ic2.core.block.wiring.TileEntityChargepadBatBox;
import ic2.core.block.wiring.TileEntityChargepadCESU;
import ic2.core.block.wiring.TileEntityChargepadMFE;
import ic2.core.block.wiring.TileEntityChargepadMFSU;
import ic2.core.block.wiring.TileEntityElectricBatBox;
import ic2.core.block.wiring.TileEntityElectricCESU;
import ic2.core.block.wiring.TileEntityElectricMFE;
import ic2.core.block.wiring.TileEntityElectricMFSU;
import ic2.core.block.wiring.TileEntityLuminator;
import ic2.core.block.wiring.TileEntityTransformerEV;
import ic2.core.block.wiring.TileEntityTransformerHV;
import ic2.core.block.wiring.TileEntityTransformerLV;
import ic2.core.block.wiring.TileEntityTransformerMV;
import ic2.core.ref.MetaTeBlock;
import ic2.core.util.Util;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

public enum TeBlock implements IIdProvider
{
    invalid(null, 0, false, Util.noFacings, false, HarvestTool.None, DefaultDrop.None, 5.0f, 10.0f, EnumRarity.COMMON),
    barrel(TileEntityBarrel.class, -1, true, Util.horizontalFacings, false, HarvestTool.Axe, DefaultDrop.None, 2.0f, 6.0f, EnumRarity.COMMON),
    wall(TileEntityWall.class, -1, false, Util.noFacings, false, HarvestTool.Pickaxe, DefaultDrop.Self, 3.0f, 30.0f, EnumRarity.COMMON),
    itnt(ITnt.class, 1, false, Util.horizontalFacings, false, HarvestTool.None, DefaultDrop.Self, 0.0f, 0.0f, EnumRarity.COMMON),
    nuke(TileEntityNuke.class, 2, false, Util.horizontalFacings, false, HarvestTool.None, DefaultDrop.Self, 0.0f, 0.0f, EnumRarity.UNCOMMON),
    generator(TileEntityGenerator.class, 3, true, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.Self, 5.0f, 10.0f, EnumRarity.COMMON),
    geo_generator(TileEntityGeoGenerator.class, 4, true, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.Generator, 5.0f, 10.0f, EnumRarity.COMMON),
    kinetic_generator(TileEntityKineticGenerator.class, 5, true, Util.allFacings, true, HarvestTool.Pickaxe, DefaultDrop.Generator, 5.0f, 10.0f, EnumRarity.COMMON),
    rt_generator(TileEntityRTGenerator.class, 6, true, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.Generator, 5.0f, 10.0f, EnumRarity.COMMON),
    semifluid_generator(TileEntitySemifluidGenerator.class, 7, true, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.Generator, 5.0f, 10.0f, EnumRarity.COMMON),
    solar_generator(TileEntitySolarGenerator.class, 8, true, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.Generator, 5.0f, 10.0f, EnumRarity.COMMON),
    stirling_generator(TileEntityStirlingGenerator.class, 9, true, Util.allFacings, true, HarvestTool.Pickaxe, DefaultDrop.Generator, 5.0f, 10.0f, EnumRarity.COMMON),
    water_generator(TileEntityWaterGenerator.class, 10, true, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.Self, 5.0f, 10.0f, EnumRarity.COMMON),
    wind_generator(TileEntityWindGenerator.class, 11, true, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.Generator, 5.0f, 10.0f, EnumRarity.COMMON),
    electric_heat_generator(TileEntityElectricHeatGenerator.class, 12, true, Util.allFacings, true, HarvestTool.Pickaxe, DefaultDrop.Self, 5.0f, 10.0f, EnumRarity.COMMON),
    fluid_heat_generator(TileEntityFluidHeatGenerator.class, 13, true, Util.allFacings, true, HarvestTool.Pickaxe, DefaultDrop.Self, 5.0f, 10.0f, EnumRarity.COMMON),
    rt_heat_generator(TileEntityRTHeatGenerator.class, 14, true, Util.allFacings, true, HarvestTool.Pickaxe, DefaultDrop.Self, 5.0f, 10.0f, EnumRarity.COMMON),
    solid_heat_generator(TileEntitySolidHeatGenerator.class, 15, true, Util.allFacings, true, HarvestTool.Pickaxe, DefaultDrop.Self, 5.0f, 10.0f, EnumRarity.COMMON),
    electric_kinetic_generator(TileEntityElectricKineticGenerator.class, 16, true, Util.allFacings, true, HarvestTool.Pickaxe, DefaultDrop.Self, 5.0f, 10.0f, EnumRarity.COMMON),
    manual_kinetic_generator(TileEntityManualKineticGenerator.class, 17, false, Util.allFacings, true, HarvestTool.Pickaxe, DefaultDrop.Self, 5.0f, 10.0f, EnumRarity.COMMON),
    steam_kinetic_generator(TileEntitySteamKineticGenerator.class, 18, true, Util.allFacings, true, HarvestTool.Pickaxe, DefaultDrop.Self, 5.0f, 10.0f, EnumRarity.COMMON),
    stirling_kinetic_generator(TileEntityStirlingKineticGenerator.class, 19, true, Util.allFacings, true, HarvestTool.Pickaxe, DefaultDrop.Self, 5.0f, 10.0f, EnumRarity.COMMON),
    water_kinetic_generator(TileEntityWaterKineticGenerator.class, 20, true, Util.horizontalFacings, true, HarvestTool.Pickaxe, DefaultDrop.Self, 5.0f, 10.0f, EnumRarity.COMMON),
    wind_kinetic_generator(TileEntityWindKineticGenerator.class, 21, true, Util.horizontalFacings, true, HarvestTool.Pickaxe, DefaultDrop.Self, 5.0f, 10.0f, EnumRarity.COMMON),
    nuclear_reactor(TileEntityNuclearReactorElectric.class, 22, true, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.Generator, 5.0f, 10.0f, EnumRarity.UNCOMMON),
    reactor_access_hatch(TileEntityReactorAccessHatch.class, 23, false, Util.noFacings, false, HarvestTool.Pickaxe, DefaultDrop.Self, 40.0f, 90.0f, EnumRarity.UNCOMMON),
    reactor_chamber(TileEntityReactorChamberElectric.class, 24, false, Util.noFacings, false, HarvestTool.Pickaxe, DefaultDrop.Machine, 5.0f, 10.0f, EnumRarity.UNCOMMON),
    reactor_fluid_port(TileEntityReactorFluidPort.class, 25, false, Util.noFacings, false, HarvestTool.Pickaxe, DefaultDrop.Self, 40.0f, 90.0f, EnumRarity.UNCOMMON),
    reactor_redstone_port(TileEntityReactorRedstonePort.class, 26, false, Util.noFacings, false, HarvestTool.Pickaxe, DefaultDrop.Self, 40.0f, 90.0f, EnumRarity.UNCOMMON),
    condenser(TileEntityCondenser.class, 27, true, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.Machine, 5.0f, 10.0f, EnumRarity.COMMON),
    fluid_bottler(TileEntityFluidBottler.class, 28, true, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.Machine, 5.0f, 10.0f, EnumRarity.COMMON),
    fluid_distributor(TileEntityFluidDistributor.class, 29, true, Util.allFacings, true, HarvestTool.Pickaxe, DefaultDrop.Machine, 5.0f, 10.0f, EnumRarity.COMMON),
    fluid_regulator(TileEntityFluidRegulator.class, 30, true, Util.allFacings, true, HarvestTool.Pickaxe, DefaultDrop.Machine, 5.0f, 10.0f, EnumRarity.COMMON),
    liquid_heat_exchanger(TileEntityLiquidHeatExchanger.class, 31, true, Util.allFacings, true, HarvestTool.Pickaxe, DefaultDrop.Machine, 5.0f, 10.0f, EnumRarity.COMMON),
    pump(TileEntityPump.class, 32, true, Util.allFacings, true, HarvestTool.Pickaxe, DefaultDrop.Machine, 5.0f, 10.0f, EnumRarity.COMMON),
    solar_distiller(TileEntitySolarDestiller.class, 33, false, Util.horizontalFacings, true, HarvestTool.Pickaxe, DefaultDrop.Machine, 5.0f, 10.0f, EnumRarity.COMMON),
    steam_generator(TileEntitySteamGenerator.class, 34, true, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.Self, 5.0f, 10.0f, EnumRarity.COMMON),
    item_buffer(TileEntityItemBuffer.class, 35, false, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.Machine, 5.0f, 10.0f, EnumRarity.COMMON),
    luminator_flat(TileEntityLuminator.class, 36, true, Util.allFacings, false, HarvestTool.Pickaxe, DefaultDrop.Self, 5.0f, 10.0f, EnumRarity.COMMON),
    magnetizer(TileEntityMagnetizer.class, 37, false, Util.horizontalFacings, true, HarvestTool.Pickaxe, DefaultDrop.Self, 5.0f, 10.0f, EnumRarity.COMMON),
    sorting_machine(TileEntitySortingMachine.class, 38, false, EnumSet.of(EnumFacing.NORTH), false, HarvestTool.Pickaxe, DefaultDrop.Machine, 5.0f, 10.0f, EnumRarity.COMMON),
    teleporter(TileEntityTeleporter.class, 39, true, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.AdvMachine, 5.0f, 10.0f, EnumRarity.RARE),
    terraformer(TileEntityTerra.class, 40, true, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.AdvMachine, 5.0f, 10.0f, EnumRarity.UNCOMMON),
    tesla_coil(TileEntityTesla.class, 41, false, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.Machine, 5.0f, 10.0f, EnumRarity.COMMON),
    canner(TileEntityCanner.class, 42, true, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.Machine, 5.0f, 10.0f, EnumRarity.COMMON),
    compressor(TileEntityCompressor.class, 43, true, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.Machine, 5.0f, 10.0f, EnumRarity.COMMON),
    electric_furnace(TileEntityElectricFurnace.class, 44, true, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.Self, 5.0f, 10.0f, EnumRarity.COMMON),
    extractor(TileEntityExtractor.class, 45, true, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.Machine, 5.0f, 10.0f, EnumRarity.COMMON),
    iron_furnace(TileEntityIronFurnace.class, 46, true, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.Self, 5.0f, 10.0f, EnumRarity.COMMON),
    macerator(TileEntityMacerator.class, 47, true, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.Machine, 5.0f, 10.0f, EnumRarity.COMMON),
    recycler(TileEntityRecycler.class, 48, true, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.Machine, 5.0f, 10.0f, EnumRarity.COMMON),
    solid_canner(TileEntitySolidCanner.class, 49, true, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.Machine, 5.0f, 10.0f, EnumRarity.COMMON),
    blast_furnace(TileEntityBlastFurnace.class, 50, true, Util.allFacings, true, HarvestTool.Pickaxe, DefaultDrop.Machine, 5.0f, 10.0f, EnumRarity.COMMON),
    block_cutter(TileEntityBlockCutter.class, 51, true, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.AdvMachine, 5.0f, 10.0f, EnumRarity.COMMON),
    centrifuge(TileEntityCentrifuge.class, 52, true, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.Machine, 5.0f, 10.0f, EnumRarity.COMMON),
    fermenter(TileEntityFermenter.class, 53, true, Util.allFacings, true, HarvestTool.Pickaxe, DefaultDrop.Machine, 5.0f, 10.0f, EnumRarity.COMMON),
    induction_furnace(TileEntityInduction.class, 54, true, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.AdvMachine, 5.0f, 10.0f, EnumRarity.UNCOMMON),
    metal_former(TileEntityMetalFormer.class, 55, true, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.Machine, 5.0f, 10.0f, EnumRarity.COMMON),
    ore_washing_plant(TileEntityOreWashing.class, 56, true, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.Machine, 5.0f, 10.0f, EnumRarity.COMMON),
    advanced_miner(TileEntityAdvMiner.class, 57, true, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.Self, 5.0f, 10.0f, EnumRarity.COMMON),
    crop_harvester(TileEntityCropHarvester.class, 58, false, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.Machine, 5.0f, 10.0f, EnumRarity.COMMON),
    cropmatron(TileEntityCropmatron.class, 59, false, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.Machine, 5.0f, 10.0f, EnumRarity.COMMON),
    miner(TileEntityMiner.class, 60, true, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.Self, 5.0f, 10.0f, EnumRarity.COMMON),
    matter_generator(TileEntityMatter.class, 61, true, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.AdvMachine, 5.0f, 10.0f, EnumRarity.RARE),
    pattern_storage(TileEntityPatternStorage.class, 62, false, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.AdvMachine, 5.0f, 10.0f, EnumRarity.COMMON),
    replicator(TileEntityReplicator.class, 63, true, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.AdvMachine, 5.0f, 10.0f, EnumRarity.COMMON),
    scanner(TileEntityScanner.class, 64, true, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.AdvMachine, 5.0f, 10.0f, EnumRarity.COMMON),
    energy_o_mat(TileEntityEnergyOMat.class, 65, false, Util.allFacings, false, HarvestTool.Pickaxe, DefaultDrop.Self, -1.0f, 3600000.0f, EnumRarity.COMMON),
    personal_chest(TileEntityPersonalChest.class, 66, false, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.Self, -1.0f, 3600000.0f, EnumRarity.UNCOMMON),
    trade_o_mat(TileEntityTradeOMat.class, 67, false, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.Self, -1.0f, 3600000.0f, EnumRarity.COMMON),
    cable(TileEntityCable.class, -1, false, Util.noFacings, false, HarvestTool.None, DefaultDrop.Self, 0.5f, 5.0f, EnumRarity.COMMON),
    detector_cable(TileEntityCableDetector.class, -1, false, Util.noFacings, false, HarvestTool.None, DefaultDrop.Self, 0.5f, 5.0f, EnumRarity.COMMON),
    splitter_cable(TileEntityCableSplitter.class, -1, false, Util.noFacings, false, HarvestTool.None, DefaultDrop.Self, 0.5f, 5.0f, EnumRarity.COMMON),
    chargepad_batbox(TileEntityChargepadBatBox.class, 68, true, Util.downSideFacings, true, HarvestTool.Pickaxe, DefaultDrop.Self, 5.0f, 10.0f, EnumRarity.COMMON),
    chargepad_cesu(TileEntityChargepadCESU.class, 69, true, Util.downSideFacings, true, HarvestTool.Pickaxe, DefaultDrop.Machine, 5.0f, 10.0f, EnumRarity.COMMON),
    chargepad_mfe(TileEntityChargepadMFE.class, 70, true, Util.downSideFacings, true, HarvestTool.Pickaxe, DefaultDrop.Machine, 5.0f, 10.0f, EnumRarity.COMMON),
    chargepad_mfsu(TileEntityChargepadMFSU.class, 71, true, Util.downSideFacings, true, HarvestTool.Pickaxe, DefaultDrop.AdvMachine, 5.0f, 10.0f, EnumRarity.UNCOMMON),
    batbox(TileEntityElectricBatBox.class, 72, false, Util.allFacings, true, HarvestTool.Pickaxe, DefaultDrop.Self, 5.0f, 10.0f, EnumRarity.COMMON),
    cesu(TileEntityElectricCESU.class, 73, false, Util.allFacings, true, HarvestTool.Pickaxe, DefaultDrop.Machine, 5.0f, 10.0f, EnumRarity.COMMON),
    mfe(TileEntityElectricMFE.class, 74, false, Util.allFacings, true, HarvestTool.Pickaxe, DefaultDrop.Machine, 5.0f, 10.0f, EnumRarity.COMMON),
    mfsu(TileEntityElectricMFSU.class, 75, false, Util.allFacings, true, HarvestTool.Pickaxe, DefaultDrop.AdvMachine, 5.0f, 10.0f, EnumRarity.UNCOMMON),
    electrolyzer(TileEntityElectrolyzer.class, 76, true, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.Machine, 5.0f, 10.0f, EnumRarity.COMMON),
    lv_transformer(TileEntityTransformerLV.class, 77, false, Util.allFacings, true, HarvestTool.Pickaxe, DefaultDrop.Self, 5.0f, 10.0f, EnumRarity.COMMON),
    mv_transformer(TileEntityTransformerMV.class, 78, false, Util.allFacings, true, HarvestTool.Pickaxe, DefaultDrop.Machine, 5.0f, 10.0f, EnumRarity.COMMON),
    hv_transformer(TileEntityTransformerHV.class, 79, false, Util.allFacings, true, HarvestTool.Pickaxe, DefaultDrop.Machine, 5.0f, 10.0f, EnumRarity.UNCOMMON),
    ev_transformer(TileEntityTransformerEV.class, 80, false, Util.allFacings, true, HarvestTool.Pickaxe, DefaultDrop.Machine, 5.0f, 10.0f, EnumRarity.UNCOMMON),
    tank(TileEntityTank.class, 81, false, Util.horizontalFacings, false, HarvestTool.Pickaxe, DefaultDrop.Self, 5.0f, 10.0f, EnumRarity.COMMON),
    chunk_loader(TileEntityChunkloader.class, 82, true, Util.downSideFacings, false, HarvestTool.Pickaxe, DefaultDrop.Self, 5.0f, 10.0f, EnumRarity.UNCOMMON),
    item_buffer_2(TileEntityBetterItemBuffer.class, 83, false, Util.noFacings, false, HarvestTool.None, DefaultDrop.Self, 5.0f, 10.0f, EnumRarity.COMMON),
    rci_rsh(TileEntityRCI_RSH.class, 84, true, Util.allFacings, true, HarvestTool.None, DefaultDrop.AdvMachine, 5.0f, 10.0f, EnumRarity.COMMON),
    rci_lzh(TileEntityRCI_LZH.class, 85, true, Util.allFacings, true, HarvestTool.None, DefaultDrop.AdvMachine, 5.0f, 10.0f, EnumRarity.COMMON),
    creative_generator(TileEntityCreativeGenerator.class, 86, true, Util.noFacings, false, HarvestTool.None, DefaultDrop.None, Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY, EnumRarity.COMMON);
    
    private final Class<? extends TileEntityBlock> teClass;
    private final int itemMeta;
    private final MetaTeBlock metaInactive;
    private final MetaTeBlock metaActive;
    public final Set<EnumFacing> supportedFacings;
    public final boolean allowWrenchRotating;
    public final HarvestTool harvestTool;
    public final DefaultDrop defaultDrop;
    public final float hardness;
    public final float explosionResistance;
    public final EnumRarity rarity;
    private TileEntityBlock dummyTe;
    private ITePlaceHandler placeHandler;
    public static final TeBlock[] values;
    private static final String teIdPrefix = "ic2:";
    private static final Map<String, TeBlock> nameMap;
    private static final Map<Class<? extends TileEntityBlock>, TeBlock> classMap;
    private static final List<TeBlock> idMap;
    private static int metaStateCount;

    private TeBlock(Class<? extends TileEntityBlock> teClass, int itemMeta, boolean hasActive, Set<EnumFacing> supportedFacings, boolean allowWrenchRotating, HarvestTool harvestTool, DefaultDrop defaultDrop, float hardness, float explosionResistance, EnumRarity rarity) {
        this.teClass = teClass;
        this.itemMeta = itemMeta;
        this.metaInactive = new MetaTeBlock(this, false);
        this.metaActive = hasActive ? new MetaTeBlock(this, true) : null;
        this.supportedFacings = supportedFacings;
        this.allowWrenchRotating = allowWrenchRotating;
        this.harvestTool = harvestTool;
        this.defaultDrop = defaultDrop;
        this.hardness = hardness;
        this.explosionResistance = explosionResistance;
        this.rarity = rarity;
    }

    public boolean hasItem() {
        return this.teClass != null && this.itemMeta != -1;
    }

    @Override
    public String getName() {
        return this.name();
    }

    public Class<? extends TileEntityBlock> getTeClass() {
        return this.teClass;
    }

    boolean hasActive() {
        return this.metaActive != null;
    }

    public MetaTeBlock getMeta(boolean active) {
        return active && this.hasActive() ? this.metaActive : this.metaInactive;
    }

    @Override
    public int getId() {
        return this.itemMeta;
    }

    public void setPlaceHandler(ITePlaceHandler handler) {
        if (this.placeHandler != null) {
            throw new RuntimeException("duplicate place handler");
        }
        this.placeHandler = handler;
    }

    public ITePlaceHandler getPlaceHandler() {
        return this.placeHandler;
    }

    public static void buildDummies() {
        ModContainer mc = Loader.instance().activeModContainer();
        if (mc == null || !"IC2".equals(mc.getModId())) {
            throw new IllegalAccessError("Don't mess with this please.");
        }
        for (TeBlock block : values) {
            if (block.teClass == null) continue;
            try {
                block.dummyTe = block.teClass.newInstance();
                continue;
            }
            catch (Exception e) {
                if (!Util.inDev()) continue;
                e.printStackTrace();
            }
        }
    }

    @Deprecated
    public TileEntityBlock getDummyTe() {
        return this.dummyTe;
    }

    public static TeBlock get(String name) {
        TeBlock ret = nameMap.get(name);
        return ret != null ? ret : invalid;
    }

    public static TeBlock get(Class<? extends TileEntityBlock> cls) {
        return classMap.get(cls);
    }

    public static TeBlock get(int persistentId) {
        if (persistentId < 0 || persistentId >= idMap.size()) {
            return invalid;
        }
        return idMap.get(persistentId);
    }

    public static void registerTeMappings() {
        for (TeBlock block : TeBlock.values()) {
            if (block.teClass == null) continue;
            TileEntity.addMapping(block.teClass, (String)("ic2:" + block.getName()));
        }
    }

    static int getMetaCount() {
        return metaStateCount;
    }

    private static void register(TeBlock block) {
        nameMap.put(block.getName(), block);
        classMap.put(block.getTeClass(), block);
        int id = block.itemMeta;
        if (id != -1) {
            while (idMap.size() < id) {
                idMap.add(null);
            }
            if (idMap.size() == id) {
                idMap.add(block);
            } else {
                if (idMap.get(id) != null) {
                    throw new IllegalArgumentException("the id " + id + " for " + block + " is already in use");
                }
                idMap.set(id, block);
            }
        }
        ++metaStateCount;
        if (block.metaActive != null) {
            ++metaStateCount;
        }
    }

    static {
        values = TeBlock.values();
        nameMap = new HashMap<String, TeBlock>(values.length);
        classMap = new IdentityHashMap<Class<? extends TileEntityBlock>, TeBlock>(values.length);
        idMap = new ArrayList<TeBlock>(values.length);
        for (TeBlock block : TeBlock.values()) {
            TeBlock.register(block);
        }
    }

    public static interface ITePlaceHandler {
        public boolean canReplace(World var1, BlockPos var2, EnumFacing var3, ItemStack var4);
    }

    public static enum DefaultDrop {
        Self,
        None,
        Generator,
        Machine,
        AdvMachine;
        

        private DefaultDrop() {
        }
    }

    public static enum HarvestTool {
        None(null, -1),
        Pickaxe("pickaxe", 0),
        Shovel("shovel", 0),
        Axe("axe", 0);
        
        public final String toolClass;
        public final int level;

        private HarvestTool(String toolClass, int level) {
            this.toolClass = toolClass;
            this.level = level;
        }
    }

}

