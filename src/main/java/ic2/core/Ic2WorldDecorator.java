/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraft.world.biome.Biome
 *  net.minecraft.world.chunk.Chunk
 *  net.minecraft.world.chunk.IChunkGenerator
 *  net.minecraft.world.chunk.IChunkProvider
 *  net.minecraft.world.gen.feature.WorldGenMinable
 *  net.minecraftforge.common.BiomeDictionary
 *  net.minecraftforge.common.BiomeDictionary$Type
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.world.ChunkDataEvent
 *  net.minecraftforge.event.world.ChunkDataEvent$Load
 *  net.minecraftforge.event.world.ChunkDataEvent$Save
 *  net.minecraftforge.event.world.ChunkEvent
 *  net.minecraftforge.event.world.ChunkEvent$Unload
 *  net.minecraftforge.fml.common.IWorldGenerator
 *  net.minecraftforge.fml.common.eventhandler.EventBus
 *  net.minecraftforge.fml.common.eventhandler.EventPriority
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 */
package ic2.core;

import ic2.core.IC2;
import ic2.core.WorldData;
import ic2.core.block.WorldGenRubTree;
import ic2.core.block.state.IIdProvider;
import ic2.core.block.type.ResourceBlock;
import ic2.core.init.MainConfig;
import ic2.core.ref.BlockName;
import ic2.core.util.BiomeUtil;
import ic2.core.util.ConfigUtil;
import ic2.core.util.Ic2BlockPos;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenMinable;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkDataEvent;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.IWorldGenerator;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Ic2WorldDecorator
implements IWorldGenerator {
    private static final String chunkDataTag = "ic2WorldGen";
    private static final String keyRubberTree = "rubberTree";
    private static final String keyCopperOre = "copperOre";
    private static final String keyLeadOre = "leadOre";
    private static final String keyTinOre = "tinOre";
    private static final String keyUraniumOre = "uraniumOre";

    public Ic2WorldDecorator() {
        MinecraftForge.EVENT_BUS.register((Object)this);
    }

    @SubscribeEvent
    public void onChunkLoad(ChunkDataEvent.Load event) {
        assert (!event.getWorld().isRemote);
        NBTTagCompound nbt = event.getData().getCompoundTag("ic2WorldGen");
        WorldData.get((World)event.getWorld()).worldGenData.put(event.getChunk(), nbt);
        Ic2WorldDecorator.checkRetroGen(event.getChunk(), nbt);
    }

    private static void checkRetroGen(Chunk chunk, NBTTagCompound nbt) {
        if (!chunk.isTerrainPopulated()) {
            return;
        }
        if (Ic2WorldDecorator.getCheckLimit() <= 0 || Ic2WorldDecorator.getUpdateLimit() <= 0) {
            return;
        }
        float epsilon = 1.0E-5f;
        float treeScale = Ic2WorldDecorator.getTreeScale() - epsilon;
        float oreScale = Ic2WorldDecorator.getOreScale(chunk.getWorld()) - epsilon;
        if (treeScale <= 0.0f && oreScale <= 0.0f) {
            return;
        }
        if (Ic2WorldDecorator.rubberTreeGenEnabled() && nbt.getFloat("rubberTree") < treeScale || Ic2WorldDecorator.copperOreGenEnabled() && nbt.getFloat("copperOre") < oreScale || Ic2WorldDecorator.leadOreGenEnabled() && nbt.getFloat("leadOre") < oreScale || Ic2WorldDecorator.tinOreGenEnabled() && nbt.getFloat("tinOre") < oreScale || Ic2WorldDecorator.uraniumOreGenEnabled() && nbt.getFloat("uraniumOre") < oreScale) {
            WorldData.get((World)chunk.getWorld()).chunksToDecorate.add(chunk);
        }
    }

    @SubscribeEvent
    public void onChunkSave(ChunkDataEvent.Save event) {
        assert (!event.getWorld().isRemote);
        NBTTagCompound nbt = WorldData.get((World)event.getWorld()).worldGenData.get((Object)event.getChunk());
        if (nbt != null && !nbt.hasNoTags()) {
            event.getData().setTag("ic2WorldGen", (NBTBase)nbt);
        }
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public void onChunkUnload(ChunkEvent.Unload event) {
        if (event.getWorld().isRemote) {
            return;
        }
        WorldData worldData = WorldData.get(event.getWorld(), false);
        if (worldData == null) {
            return;
        }
        worldData.worldGenData.remove((Object)event.getChunk());
        worldData.chunksToDecorate.remove((Object)event.getChunk());
    }

    public static void onTick(World world, WorldData worldData) {
        if (worldData.chunksToDecorate.isEmpty()) {
            return;
        }
        int chunksToCheck = Ic2WorldDecorator.getCheckLimit();
        int chunksToDecorate = Ic2WorldDecorator.getUpdateLimit();
        long worldSeed = world.getSeed();
        Random rnd = new Random(worldSeed);
        long xSeed = rnd.nextLong() >> 3;
        long zSeed = rnd.nextLong() >> 3;
        int baseHeight = world.getSeaLevel() + 1;
        float treeScale = Ic2WorldDecorator.getTreeScale();
        float oreScale = Ic2WorldDecorator.getOreScale(world);
        int skip = worldData.chunksToDecorate.size() - chunksToCheck;
        if (skip > 0) {
            skip = IC2.random.nextInt(skip + 1);
        }
        Iterator<Chunk> it = worldData.chunksToDecorate.iterator();
        while (skip > 0) {
            --skip;
            it.next();
        }
        while (it.hasNext()) {
            Chunk chunk = it.next();
            if (Ic2WorldDecorator.hasNeighborChunks(chunk)) {
                float extra;
                NBTTagCompound nbt = worldData.worldGenData.get((Object)chunk);
                if (nbt == null) {
                    nbt = new NBTTagCompound();
                }
                long chunkSeed = xSeed * (long)chunk.xPosition + zSeed * (long)chunk.zPosition ^ worldSeed;
                rnd.setSeed(chunkSeed);
                long rubberTreeSeed = rnd.nextLong();
                long copperOreSeed = rnd.nextLong();
                long tinOreSeed = rnd.nextLong();
                long uraniumOreSeed = rnd.nextLong();
                long leadOreSeed = rnd.nextLong();
                if (Ic2WorldDecorator.rubberTreeGenEnabled() && (extra = treeScale - nbt.getFloat("rubberTree")) > 0.0f) {
                    Ic2WorldDecorator.genRubberTree(rnd, rubberTreeSeed, chunk, extra);
                }
                if (Ic2WorldDecorator.copperOreGenEnabled() && (extra = oreScale - nbt.getFloat("copperOre")) > 0.0f) {
                    Ic2WorldDecorator.genCopperOre(rnd, copperOreSeed, chunk, baseHeight, extra);
                }
                if (Ic2WorldDecorator.leadOreGenEnabled() && (extra = oreScale - nbt.getFloat("leadOre")) > 0.0f) {
                    Ic2WorldDecorator.genLeadOre(rnd, leadOreSeed, chunk, baseHeight, extra);
                }
                if (Ic2WorldDecorator.tinOreGenEnabled() && (extra = oreScale - nbt.getFloat("tinOre")) > 0.0f) {
                    Ic2WorldDecorator.genTinOre(rnd, tinOreSeed, chunk, baseHeight, extra);
                }
                if (Ic2WorldDecorator.uraniumOreGenEnabled() && (extra = oreScale - nbt.getFloat("uraniumOre")) > 0.0f) {
                    Ic2WorldDecorator.genUraniumOre(rnd, uraniumOreSeed, chunk, baseHeight, extra);
                }
                it.remove();
                if (--chunksToDecorate == 0) break;
            }
            if (--chunksToCheck != 0) continue;
            break;
        }
    }

    private static boolean hasNeighborChunks(Chunk chunk) {
        World world = chunk.getWorld();
        Ic2BlockPos pos = new Ic2BlockPos();
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dz = -1; dz <= 1; ++dz) {
                if (dx == 0 && dz == 0) continue;
                pos.set(chunk.xPosition + dx << 4, 0, chunk.zPosition + dz << 4);
                if (world.isBlockLoaded((BlockPos)pos, false)) continue;
                return false;
            }
        }
        return true;
    }

    public void generate(Random rnd, int chunkX, int chunkZ, World world, IChunkGenerator chunkGenerator, IChunkProvider chunkProvider) {
        Chunk chunk = chunkProvider.provideChunk(chunkX, chunkZ);
        long rubberTreeSeed = rnd.nextLong();
        long copperOreSeed = rnd.nextLong();
        long tinOreSeed = rnd.nextLong();
        long uraniumOreSeed = rnd.nextLong();
        long leadOreSeed = rnd.nextLong();
        int baseHeight = world.getSeaLevel() + 1;
        float treeScale = Ic2WorldDecorator.getTreeScale();
        float oreScale = Ic2WorldDecorator.getOreScale(world);
        if (Ic2WorldDecorator.rubberTreeGenEnabled() && treeScale > 0.0f) {
            Ic2WorldDecorator.genRubberTree(rnd, rubberTreeSeed, chunk, treeScale);
        }
        if (oreScale > 0.0f) {
            if (Ic2WorldDecorator.copperOreGenEnabled()) {
                Ic2WorldDecorator.genCopperOre(rnd, copperOreSeed, chunk, baseHeight, oreScale);
            }
            if (Ic2WorldDecorator.leadOreGenEnabled()) {
                Ic2WorldDecorator.genLeadOre(rnd, leadOreSeed, chunk, baseHeight, oreScale);
            }
            if (Ic2WorldDecorator.tinOreGenEnabled()) {
                Ic2WorldDecorator.genTinOre(rnd, tinOreSeed, chunk, baseHeight, oreScale);
            }
            if (Ic2WorldDecorator.uraniumOreGenEnabled()) {
                Ic2WorldDecorator.genUraniumOre(rnd, uraniumOreSeed, chunk, baseHeight, oreScale);
            }
        }
    }

    private static float getTreeScale() {
        return ConfigUtil.getFloat(MainConfig.get(), "worldgen/treeDensityFactor");
    }

    private static float getOreScale(World world) {
        float quantityScale = ConfigUtil.getFloat(MainConfig.get(), "worldgen/oreDensityFactor");
        int baseHeight = world.getSeaLevel() + 1;
        return (float)baseHeight * quantityScale;
    }

    private static void genRubberTree(Random rnd, long seed, Chunk chunk, float baseScale) {
        rnd.setSeed(seed);
        Biome[] biomes = new Biome[4];
        for (int i = 0; i < 4; ++i) {
            int x = chunk.xPosition * 16 + (i & 1) * 15;
            int z = chunk.zPosition * 16 + ((i & 2) >>> 1) * 15;
            BlockPos pos = new BlockPos(x, chunk.getWorld().getSeaLevel(), z);
            biomes[i] = BiomeUtil.getOriginalBiome(chunk.getWorld(), pos);
        }
        int rubberTrees = 0;
        for (Biome biome : biomes) {
            if (biome == null) continue;
            if (BiomeDictionary.isBiomeOfType((Biome)biome, (BiomeDictionary.Type)BiomeDictionary.Type.SWAMP)) {
                rubberTrees += rnd.nextInt(10) + 5;
            }
            if (!BiomeDictionary.isBiomeOfType((Biome)biome, (BiomeDictionary.Type)BiomeDictionary.Type.FOREST) && !BiomeDictionary.isBiomeOfType((Biome)biome, (BiomeDictionary.Type)BiomeDictionary.Type.JUNGLE)) continue;
            rubberTrees += rnd.nextInt(5) + 1;
        }
        rubberTrees = Math.round((float)rubberTrees * baseScale);
        if ((rubberTrees /= 2) > 0 && rnd.nextInt(100) < rubberTrees) {
            WorldGenRubTree gen = new WorldGenRubTree(false);
            for (int i2 = 0; i2 < rubberTrees; ++i2) {
                if (gen.generate(chunk.getWorld(), rnd, new BlockPos(chunk.xPosition * 16 + rnd.nextInt(16), chunk.getWorld().getSeaLevel(), chunk.zPosition * 16 + rnd.nextInt(16)))) continue;
                rubberTrees -= 3;
            }
        }
        Ic2WorldDecorator.updateScale(chunk, "rubberTree", baseScale);
    }

    private static void genCopperOre(Random rnd, long seed, Chunk chunk, int baseHeight, float baseScale) {
        rnd.setSeed(seed);
        float baseCount = 15.0f * baseScale / 64.0f;
        int count = (int)Math.round(rnd.nextGaussian() * Math.sqrt(baseCount) + (double)baseCount);
        WorldGenMinable gen = new WorldGenMinable(BlockName.resource.getBlockState(ResourceBlock.copper_ore), 10);
        for (int n = 0; n < count; ++n) {
            int x = chunk.xPosition * 16 + rnd.nextInt(16);
            int y = Ic2WorldDecorator.zeroRnd(rnd, 40 * baseHeight / 64) + Ic2WorldDecorator.zeroRnd(rnd, 20 * baseHeight / 64) + 10 * baseHeight / 64;
            int z = chunk.zPosition * 16 + rnd.nextInt(16);
            gen.generate(chunk.getWorld(), rnd, new BlockPos(x, y, z));
        }
        Ic2WorldDecorator.updateScale(chunk, "copperOre", baseScale);
    }

    private static void genLeadOre(Random rnd, long seed, Chunk chunk, int baseHeight, float baseScale) {
        rnd.setSeed(seed);
        float baseCount = 8.0f * baseScale / 64.0f;
        int count = (int)Math.round(rnd.nextGaussian() * Math.sqrt(baseCount) + (double)baseCount);
        WorldGenMinable gen = new WorldGenMinable(BlockName.resource.getBlockState(ResourceBlock.lead_ore), 4);
        for (int n = 0; n < count; ++n) {
            int x = chunk.xPosition * 16 + rnd.nextInt(16);
            int y = Ic2WorldDecorator.zeroRnd(rnd, 64 * baseHeight / 64);
            int z = chunk.zPosition * 16 + rnd.nextInt(16);
            gen.generate(chunk.getWorld(), rnd, new BlockPos(x, y, z));
        }
        Ic2WorldDecorator.updateScale(chunk, "leadOre", baseScale);
    }

    private static void genTinOre(Random rnd, long seed, Chunk chunk, int baseHeight, float baseScale) {
        rnd.setSeed(seed);
        float baseCount = 25.0f * baseScale / 64.0f;
        int count = (int)Math.round(rnd.nextGaussian() * Math.sqrt(baseCount) + (double)baseCount);
        WorldGenMinable gen = new WorldGenMinable(BlockName.resource.getBlockState(ResourceBlock.tin_ore), 6);
        for (int n = 0; n < count; ++n) {
            int x = chunk.xPosition * 16 + rnd.nextInt(16);
            int y = Ic2WorldDecorator.zeroRnd(rnd, 40 * baseHeight / 64);
            int z = chunk.zPosition * 16 + rnd.nextInt(16);
            gen.generate(chunk.getWorld(), rnd, new BlockPos(x, y, z));
        }
        Ic2WorldDecorator.updateScale(chunk, "tinOre", baseScale);
    }

    private static void genUraniumOre(Random rnd, long seed, Chunk chunk, int baseHeight, float baseScale) {
        rnd.setSeed(seed);
        float baseCount = 20.0f * baseScale / 64.0f;
        int count = (int)Math.round(rnd.nextGaussian() * Math.sqrt(baseCount) + (double)baseCount);
        WorldGenMinable gen = new WorldGenMinable(BlockName.resource.getBlockState(ResourceBlock.uranium_ore), 3);
        for (int n = 0; n < count; ++n) {
            int x = chunk.xPosition * 16 + Ic2WorldDecorator.zeroRnd(rnd, 16);
            int y = Ic2WorldDecorator.zeroRnd(rnd, 64 * baseHeight / 64);
            int z = chunk.zPosition * 16 + Ic2WorldDecorator.zeroRnd(rnd, 16);
            gen.generate(chunk.getWorld(), rnd, new BlockPos(x, y, z));
        }
        Ic2WorldDecorator.updateScale(chunk, "uraniumOre", baseScale);
    }

    private static boolean rubberTreeGenEnabled() {
        return ConfigUtil.getBool(MainConfig.get(), "worldgen/rubberTree");
    }

    private static boolean copperOreGenEnabled() {
        return ConfigUtil.getBool(MainConfig.get(), "worldgen/copperOre");
    }

    private static boolean leadOreGenEnabled() {
        return ConfigUtil.getBool(MainConfig.get(), "worldgen/leadOre");
    }

    private static boolean tinOreGenEnabled() {
        return ConfigUtil.getBool(MainConfig.get(), "worldgen/tinOre");
    }

    private static boolean uraniumOreGenEnabled() {
        return ConfigUtil.getBool(MainConfig.get(), "worldgen/uraniumOre");
    }

    private static int getCheckLimit() {
        return ConfigUtil.getInt(MainConfig.get(), "worldgen/retrogenCheckLimit");
    }

    private static int getUpdateLimit() {
        return ConfigUtil.getInt(MainConfig.get(), "worldgen/retrogenUpdateLimit");
    }

    private static void updateScale(Chunk chunk, String key, float scale) {
        WorldData worldData = WorldData.get(chunk.getWorld());
        NBTTagCompound nbt = worldData.worldGenData.get((Object)chunk);
        if (nbt == null) {
            nbt = new NBTTagCompound();
            worldData.worldGenData.put(chunk, nbt);
        }
        nbt.setFloat(key, nbt.getFloat(key) + scale);
        chunk.setModified(true);
    }

    private static int zeroRnd(Random rnd, int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("The limit must not be negative: " + limit);
        }
        if (limit == 0) {
            return 0;
        }
        return rnd.nextInt(limit);
    }
}

