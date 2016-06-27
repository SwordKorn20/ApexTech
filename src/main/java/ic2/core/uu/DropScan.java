/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.profiler.Profiler
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$MutableBlockPos
 *  net.minecraft.util.math.ChunkPos
 *  net.minecraft.world.DimensionType
 *  net.minecraft.world.EnumSkyBlock
 *  net.minecraft.world.MinecraftException
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldProvider
 *  net.minecraft.world.WorldServer
 *  net.minecraft.world.chunk.Chunk
 *  net.minecraft.world.chunk.EmptyChunk
 *  net.minecraft.world.chunk.IChunkGenerator
 *  net.minecraft.world.chunk.IChunkProvider
 *  net.minecraft.world.chunk.storage.IChunkLoader
 *  net.minecraft.world.gen.ChunkProviderServer
 *  net.minecraft.world.gen.structure.template.TemplateManager
 *  net.minecraft.world.storage.IPlayerFileData
 *  net.minecraft.world.storage.ISaveHandler
 *  net.minecraft.world.storage.WorldInfo
 *  net.minecraft.world.storage.loot.LootTableManager
 *  net.minecraftforge.common.DimensionManager
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.world.ChunkEvent
 *  net.minecraftforge.event.world.ChunkEvent$Load
 *  net.minecraftforge.event.world.ChunkEvent$Unload
 *  net.minecraftforge.fml.common.eventhandler.Event
 *  net.minecraftforge.fml.common.eventhandler.EventBus
 *  org.apache.commons.lang3.mutable.MutableLong
 */
package ic2.core.uu;

import ic2.core.IC2;
import ic2.core.Ic2Player;
import ic2.core.init.MainConfig;
import ic2.core.util.Config;
import ic2.core.util.ConfigUtil;
import ic2.core.util.ItemComparableItemStack;
import ic2.core.util.Log;
import ic2.core.util.LogCategory;
import ic2.core.util.ReflectionUtil;
import ic2.core.util.Util;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.profiler.Profiler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.DimensionType;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.ChunkProviderServer;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.loot.LootTableManager;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.ChunkEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import org.apache.commons.lang3.mutable.MutableLong;

public class DropScan {
    private static final Field WorldServer_pendingTickListEntriesHashSet = ReflectionUtil.getField(WorldServer.class, "pendingTickListEntriesHashSet", "field_73064_N");
    private static final Field WorldServer_pendingTickListEntriesTreeSet = ReflectionUtil.getField(WorldServer.class, "pendingTickListEntriesTreeSet", "field_73065_O");
    private final WorldServer parentWorld;
    private final int range;
    private final List<Collection<?>> collectionsToClear = new ArrayList();
    private final File tmpDir;
    private final int dimensionId;
    private final DummyWorld world;
    private final EntityPlayer player;
    private final Map<ItemComparableItemStack, MutableLong> drops = new HashMap<ItemComparableItemStack, MutableLong>();
    private final Map<IBlockState, DropDesc> typicalDrops = new IdentityHashMap<IBlockState, DropDesc>();

    public DropScan(WorldServer parentWorld, int range) {
        int id;
        if (parentWorld == null) {
            throw new NullPointerException("null world");
        }
        if (range < 4) {
            throw new IllegalArgumentException("range has to be at least 4");
        }
        this.parentWorld = parentWorld;
        this.range = range;
        try {
            this.tmpDir = File.createTempFile("ic2uuscan", null);
            if (!this.tmpDir.delete() || !this.tmpDir.mkdir()) {
                throw new IOException("Can't create a temporary directory for map storage");
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        IC2.log.info(LogCategory.Uu, "Using %s for temporary data.", this.tmpDir);
        while (DimensionManager.getWorld((int)(id = parentWorld.rand.nextInt())) != null) {
        }
        this.dimensionId = id;
        DimensionManager.registerDimension((int)this.dimensionId, (DimensionType)parentWorld.provider.getDimensionType());
        this.world = new DummyWorld();
        this.player = Ic2Player.get((World)this.world);
        this.updateCollectionsToClear();
    }

    private void updateCollectionsToClear() {
        this.collectionsToClear.add((Collection)ReflectionUtil.getFieldValue(WorldServer_pendingTickListEntriesHashSet, (Object)this.world));
        this.collectionsToClear.add((Collection)ReflectionUtil.getFieldValue(WorldServer_pendingTickListEntriesTreeSet, (Object)this.world));
        this.collectionsToClear.add(this.world.loadedEntityList);
        this.collectionsToClear.add(this.world.loadedTileEntityList);
        this.collectionsToClear.add(this.world.tickableTileEntities);
    }

    public void start(int area, int areaCount) {
        long lastPrint = 0;
        for (int i = 0; i < areaCount; ++i) {
            int x = IC2.random.nextInt(area) - area / 2;
            int z = IC2.random.nextInt(area) - area / 2;
            try {
                this.scanArea(x, z);
            }
            catch (Exception e) {
                IC2.log.warn(LogCategory.Uu, e, "Scan failed.");
            }
            if (i % 4 != 0 || lastPrint > System.nanoTime() - 10000000000L) continue;
            lastPrint = System.nanoTime();
            IC2.log.info(LogCategory.Uu, "World scan progress: %.1f%%.", Float.valueOf(100.0f * (float)i / (float)areaCount));
        }
        this.analyze();
    }

    public void cleanup() {
        DimensionManager.setWorld((int)this.dimensionId, (WorldServer)null, (MinecraftServer)this.parentWorld.getMinecraftServer());
        DimensionManager.unregisterDimension((int)this.dimensionId);
        DropScan.deleteRecursive(this.tmpDir, false);
    }

    private static void deleteRecursive(File file, boolean deleteFiles) {
        if (!file.isDirectory()) {
            throw new IllegalArgumentException("no dir: " + file);
        }
        for (File subFile : file.listFiles()) {
            if (subFile.isDirectory()) {
                DropScan.deleteRecursive(subFile, deleteFiles);
                continue;
            }
            if (!deleteFiles) continue;
            subFile.delete();
        }
        file.delete();
    }

    private void analyze() {
        double normalizeBy;
        ItemComparableItemStack cobblestone = new ItemComparableItemStack(new ItemStack(Blocks.COBBLESTONE), false);
        ItemComparableItemStack netherrack = new ItemComparableItemStack(new ItemStack(Blocks.NETHERRACK), false);
        if (!this.drops.containsKey(cobblestone)) {
            if (!this.drops.containsKey(netherrack)) {
                IC2.log.warn(LogCategory.Uu, "UU scan failed, there was no cobblestone or netherrack dropped");
                return;
            }
            normalizeBy = this.drops.get(netherrack).getValue().longValue();
        } else {
            normalizeBy = this.drops.get(cobblestone).getValue().longValue();
            if (this.drops.containsKey(netherrack)) {
                normalizeBy = Math.max(normalizeBy, (double)this.drops.get(netherrack).getValue().longValue());
            }
        }
        Config config = MainConfig.get().getSub("balance/uu-values/world scan");
        if (config == null) {
            config = MainConfig.get().getSub("balance/uu-values").addSub("world scan", "Initial uu values from scanning the world.\nRun /ic2 uu-world-scan <small|medium|large> to calibrate them for your world.\nDelete this whole section to revert to the default predefined values.");
        }
        ArrayList<Map.Entry<ItemComparableItemStack, MutableLong>> sorted = new ArrayList<Map.Entry<ItemComparableItemStack, MutableLong>>(this.drops.entrySet());
        this.drops.clear();
        Collections.sort(sorted, new Comparator<Map.Entry<ItemComparableItemStack, MutableLong>>(){

            @Override
            public int compare(Map.Entry<ItemComparableItemStack, MutableLong> a, Map.Entry<ItemComparableItemStack, MutableLong> b) {
                return Long.compare(b.getValue().getValue(), a.getValue().getValue());
            }
        });
        IC2.log.info(LogCategory.Uu, "total");
        for (Map.Entry<ItemComparableItemStack, MutableLong> entry : sorted) {
            ItemStack stack = entry.getKey().toStack();
            long count = entry.getValue().getValue();
            IC2.log.info(LogCategory.Uu, "%d %s", count, stack.getItem().getItemStackDisplayName(stack));
            config.set(ConfigUtil.fromStack(stack), normalizeBy / (double)count);
        }
        MainConfig.save();
    }

    private void scanArea(int xStart, int zStart) {
        DummyChunkProvider provider = this.world.getChunkProvider();
        ArrayList<Chunk> chunks = new ArrayList<Chunk>(Util.square(this.range));
        ArrayList<Chunk> toDecorate = new ArrayList<Chunk>(Util.square(this.range - 1));
        ArrayList<Chunk> toScan = new ArrayList<Chunk>(Util.square(this.range - 3));
        provider.enableGenerate();
        for (int x = xStart; x < xStart + this.range; ++x) {
            for (int z = zStart; z < zStart + this.range; ++z) {
                Chunk chunk = this.world.getChunkFromChunkCoords(x, z);
                chunks.add(chunk);
                if (x == xStart + this.range - 1 || z == zStart + this.range - 1) continue;
                toDecorate.add(chunk);
                if (x == xStart || x == xStart + this.range - 2 || z == zStart || z == zStart + this.range - 2) continue;
                toScan.add(chunk);
            }
        }
        provider.setChunks(chunks, xStart, zStart);
        for (Chunk chunk : toDecorate) {
            MinecraftForge.EVENT_BUS.post((Event)new ChunkEvent.Load(chunk));
        }
        for (Chunk chunk2 : toDecorate) {
            chunk2.populateChunk((IChunkProvider)provider, provider.chunkGenerator);
        }
        provider.disableGenerate();
        for (Chunk chunk3 : toScan) {
            this.scanChunk(this.world, chunk3);
        }
        for (Chunk chunk4 : toDecorate) {
            MinecraftForge.EVENT_BUS.post((Event)new ChunkEvent.Unload(chunk4));
        }
        this.world.clear();
    }

    private void scanChunk(DummyWorld world, Chunk chunk) {
        assert (world.getChunkFromChunkCoords(chunk.xPosition, chunk.zPosition) == chunk);
        int xMax = (chunk.xPosition + 1) * 16;
        int yMax = world.getHeight();
        int zMax = (chunk.zPosition + 1) * 16;
        BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();
        for (int y = 0; y < yMax; ++y) {
            for (int z = chunk.zPosition * 16; z < zMax; ++z) {
                for (int x = chunk.xPosition * 16; x < xMax; ++x) {
                    pos.setPos(x, y, z);
                    IBlockState state = chunk.getBlockState((BlockPos)pos);
                    Block block = state.getBlock();
                    if (block == Blocks.AIR) continue;
                    for (ItemStack drop : this.getDrops(world, (BlockPos)pos, block, state)) {
                        this.addDrop(drop);
                    }
                }
            }
        }
    }

    private List<ItemStack> getDrops(DummyWorld world, BlockPos pos, Block block, IBlockState state) {
        DropDesc typicalDrop = this.typicalDrops.get((Object)state);
        if (typicalDrop == null || typicalDrop.dropCount.get() < 1000) {
            block.onBlockHarvested((World)world, pos, state, this.player);
            if (block.removedByPlayer(state, (World)world, pos, this.player, true)) {
                block.onBlockDestroyedByPlayer((World)world, pos, state);
                block.dropBlockAsItem((World)world, pos, state, 0);
            } else {
                IC2.log.info(LogCategory.Uu, "Can't harvest %s.", new Object[]{block});
            }
            ArrayList<ItemStack> drops = new ArrayList<ItemStack>(world.spawnedEntities.size());
            for (Entity entity : world.spawnedEntities) {
                if (!(entity instanceof EntityItem)) continue;
                drops.add(((EntityItem)entity).getEntityItem());
            }
            world.spawnedEntities.clear();
            if (typicalDrop == null) {
                typicalDrop = new DropDesc(drops);
                this.typicalDrops.put(state, typicalDrop);
            }
            if (typicalDrop.dropCount.get() >= 0) {
                boolean equal;
                boolean bl = equal = typicalDrop.drops.size() == drops.size();
                if (equal) {
                    Iterator<ItemStack> it = drops.iterator();
                    Iterator<ItemStack> it2 = typicalDrop.drops.iterator();
                    while (it.hasNext()) {
                        ItemStack b;
                        ItemStack a = it.next();
                        if (ItemStack.areItemStacksEqual((ItemStack)a, (ItemStack)(b = it2.next()))) continue;
                        equal = false;
                        break;
                    }
                }
                if (equal) {
                    int prev = typicalDrop.dropCount.incrementAndGet();
                    if (prev < 0) {
                        typicalDrop.dropCount.set(Integer.MIN_VALUE);
                    }
                } else {
                    typicalDrop.dropCount.set(Integer.MIN_VALUE);
                }
            }
            return drops;
        }
        return typicalDrop.drops;
    }

    private void addDrop(ItemStack stack) {
        ItemComparableItemStack key = new ItemComparableItemStack(stack, false);
        MutableLong amount = this.drops.get(key);
        if (amount == null) {
            amount = new MutableLong();
            this.drops.put(key.copy(), amount);
        }
        amount.add((long)stack.stackSize);
    }

    private static final class DropDesc {
        List<ItemStack> drops;
        AtomicInteger dropCount = new AtomicInteger();

        DropDesc(List<ItemStack> drops) {
            this.drops = drops;
        }
    }

    private class DummySaveHandler
    implements ISaveHandler {
        private final TemplateManager templateManager;

        private DummySaveHandler() {
            this.templateManager = new TemplateManager();
        }

        public WorldInfo loadWorldInfo() {
            return DropScan.this.world.getWorldInfo();
        }

        public void checkSessionLock() throws MinecraftException {
        }

        public IChunkLoader getChunkLoader(WorldProvider provider) {
            throw new UnsupportedOperationException();
        }

        public void saveWorldInfoWithPlayer(WorldInfo worldInformation, NBTTagCompound tagCompound) {
        }

        public void saveWorldInfo(WorldInfo worldInformation) {
        }

        public IPlayerFileData getPlayerNBTManager() {
            throw new UnsupportedOperationException();
        }

        public void flush() {
        }

        public File getWorldDirectory() {
            throw new UnsupportedOperationException();
        }

        public File getMapFileFromName(String mapName) {
            throw new UnsupportedOperationException();
        }

        public TemplateManager getStructureTemplateManager() {
            return this.templateManager;
        }
    }

    class DummyChunkProvider
    extends ChunkProviderServer {
        private final Chunk emptyChunk;
        private final Map<Long, Chunk> extraChunks;
        private final Chunk[] chunks;
        private int xStart;
        private int zStart;
        private boolean disableGenerate;

        public DummyChunkProvider(WorldServer world, IChunkGenerator chunkGenerator) {
            super(world, null, chunkGenerator);
            this.extraChunks = new HashMap<Long, Chunk>();
            this.emptyChunk = new EmptyChunk((World)world, 0, 0);
            this.chunks = new Chunk[Util.square(DropScan.this.range)];
        }

        public void setChunks(List<Chunk> newChunks, int xStart, int zStart) {
            this.clear();
            this.xStart = xStart;
            this.zStart = zStart;
            for (Chunk chunk : newChunks) {
                int index = this.getIndex(chunk.xPosition, chunk.zPosition);
                if (index < 0) {
                    throw new IllegalArgumentException("out of range");
                }
                this.chunks[index] = chunk;
            }
        }

        public void enableGenerate() {
            this.disableGenerate = false;
        }

        public void disableGenerate() {
            this.disableGenerate = true;
        }

        public void clear() {
            this.extraChunks.clear();
            Arrays.fill(this.chunks, null);
        }

        public String makeString() {
            return "Dummy";
        }

        public Chunk getLoadedChunk(int x, int z) {
            int index = this.getIndex(x, z);
            if (index >= 0) {
                return this.chunks[index];
            }
            return this.extraChunks.get(ChunkPos.chunkXZ2Int((int)x, (int)z));
        }

        public Chunk provideChunk(int x, int z) {
            Chunk ret = this.getLoadedChunk(x, z);
            if (ret == null) {
                if (this.disableGenerate) {
                    return this.emptyChunk;
                }
                ret = this.chunkGenerator.provideChunk(x, z);
                int index = this.getIndex(x, z);
                if (index >= 0) {
                    this.chunks[index] = ret;
                } else {
                    this.extraChunks.put(ChunkPos.chunkXZ2Int((int)x, (int)z), ret);
                }
            }
            return ret;
        }

        public boolean saveChunks(boolean all) {
            return true;
        }

        public void saveExtraData() {
        }

        public boolean unloadQueuedChunks() {
            return false;
        }

        private int getIndex(int x, int z) {
            if ((x -= this.xStart) < 0 || x >= DropScan.this.range || z < 0 || (z -= this.zStart) >= DropScan.this.range) {
                return -1;
            }
            return x * DropScan.this.range + z;
        }
    }

    class DummyWorld
    extends WorldServer {
        List<Entity> spawnedEntities;

        public DummyWorld() {
            super(DropScan.this.parentWorld.getMinecraftServer(), (ISaveHandler)new DummySaveHandler(), DropScan.this.parentWorld.getWorldInfo(), DropScan.this.dimensionId, DropScan.access$000((DropScan)DropScan.this).theProfiler);
            this.spawnedEntities = new ArrayList<Entity>();
            this.lootTable = DropScan.this.parentWorld.getLootTableManager();
        }

        protected IChunkProvider createChunkProvider() {
            return new DummyChunkProvider(this, this.provider.createChunkGenerator());
        }

        public DummyChunkProvider getChunkProvider() {
            return (DummyChunkProvider)super.getChunkProvider();
        }

        public File getChunkSaveLocation() {
            return DropScan.this.tmpDir;
        }

        protected boolean isChunkLoaded(int x, int z, boolean allowEmpty) {
            return this.getChunkProvider().getLoadedChunk(x, z) != null;
        }

        public Entity getEntityByID(int i) {
            return null;
        }

        public boolean setBlockState(BlockPos pos, IBlockState state, int flags) {
            if (pos.getY() >= 256 || pos.getY() < 0) {
                return false;
            }
            Chunk chunk = this.getChunkFromChunkCoords(pos.getX() >> 4, pos.getZ() >> 4);
            return chunk.setBlockState(pos, state) != null;
        }

        public boolean checkLightFor(EnumSkyBlock lightType, BlockPos pos) {
            return true;
        }

        public void tick() {
        }

        public boolean spawnEntityInWorld(Entity entity) {
            this.spawnedEntities.add(entity);
            return true;
        }

        public void clear() {
            this.getChunkProvider().clear();
            for (Collection c : DropScan.this.collectionsToClear) {
                c.clear();
            }
        }
    }

}

