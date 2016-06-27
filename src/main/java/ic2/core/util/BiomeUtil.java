/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.init.Biomes
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraft.world.biome.Biome
 *  net.minecraft.world.biome.BiomeProvider
 *  net.minecraft.world.chunk.Chunk
 *  net.minecraftforge.common.BiomeDictionary
 *  net.minecraftforge.common.BiomeDictionary$Type
 */
package ic2.core.util;

import net.minecraft.init.Biomes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.BiomeDictionary;

public final class BiomeUtil {
    public static Biome getOriginalBiome(World world, BlockPos pos) {
        return world.getBiomeProvider().getBiomeGenerator(pos, Biomes.PLAINS);
    }

    public static Biome getBiome(World world, BlockPos pos) {
        return world.getBiomeGenForCoords(pos);
    }

    public static void setBiome(World world, BlockPos pos, Biome biome) {
        byte[] biomeArray = world.getChunkFromBlockCoords(pos).getBiomeArray();
        int index = (pos.getZ() & 15) << 4 | pos.getX() & 15;
        biomeArray[index] = (byte)Biome.getIdForBiome((Biome)biome);
    }

    public static int getBiomeTemperature(World world, BlockPos pos) {
        Biome biome = BiomeUtil.getBiome(world, pos);
        if (BiomeDictionary.isBiomeOfType((Biome)biome, (BiomeDictionary.Type)BiomeDictionary.Type.HOT)) {
            return 45;
        }
        if (BiomeDictionary.isBiomeOfType((Biome)biome, (BiomeDictionary.Type)BiomeDictionary.Type.COLD)) {
            return 0;
        }
        return 25;
    }
}

