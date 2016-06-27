/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumFacing$Axis
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldProvider
 *  net.minecraft.world.chunk.Chunk
 *  net.minecraftforge.common.DimensionManager
 */
package ic2.core.energy;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.IEnergyNet;
import ic2.api.energy.NodeStats;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.energy.tile.IMetaDelegate;
import ic2.core.IC2;
import ic2.core.Platform;
import ic2.core.TickHandler;
import ic2.core.energy.Change;
import ic2.core.energy.EnergyNetGlobal;
import ic2.core.energy.Grid;
import ic2.core.energy.GridInfo;
import ic2.core.energy.Node;
import ic2.core.energy.NodeLink;
import ic2.core.energy.NodeType;
import ic2.core.energy.Tile;
import ic2.core.init.MainConfig;
import ic2.core.util.ConfigUtil;
import ic2.core.util.Log;
import ic2.core.util.LogCategory;
import ic2.core.util.PriorityExecutor;
import ic2.core.util.Util;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.DimensionManager;

public final class EnergyNetLocal {
    public static final boolean useLinearTransferModel = ConfigUtil.getBool(MainConfig.get(), "misc/useLinearTransferModel");
    public static final double nonConductorResistance = 0.2;
    public static final double sourceResistanceFactor = 0.0625;
    public static final double sinkResistanceFactor = 1.0;
    public static final double sourceCurrent = 17.0;
    public static final boolean enableCache = true;
    private static int nextGridUid = 0;
    private static int nextNodeUid = 0;
    private final World world;
    protected final Set<Grid> grids = new HashSet<Grid>();
    protected List<Change> changes = new ArrayList<Change>();
    private final Map<BlockPos, Tile> registeredTiles = new HashMap<BlockPos, Tile>();
    private final Map<IEnergyTile, Integer> pendingAdds = new WeakHashMap<IEnergyTile, Integer>();
    private final Set<Tile> removedTiles = new HashSet<Tile>();
    private boolean locked = false;
    private static final long logSuppressionTimeout = 300000000000L;
    private final Map<String, Long> recentLogs = new HashMap<String, Long>();

    public EnergyNetLocal(World world) {
        this.world = world;
    }

    protected void addTile(IEnergyTile mainTile) {
        this.addTile(mainTile, 0);
    }

    protected void addTile(IEnergyTile mainTile, int retry) {
        if (EnergyNetGlobal.debugTileManagement) {
            IC2.log.debug(LogCategory.EnergyNet, "EnergyNet.addTile(%s, %d), world=%s, chunk=%s, this=%s", new Object[]{mainTile, retry, EnergyNet.instance.getWorld(mainTile), EnergyNet.instance.getWorld(mainTile).getChunkFromBlockCoords(EnergyNet.instance.getPos(mainTile)), this});
        }
        if (EnergyNetGlobal.checkApi && !Util.checkInterfaces(mainTile.getClass())) {
            IC2.log.warn(LogCategory.EnergyNet, "EnergyNet.addTile: %s doesn't implement its advertised interfaces completely.", mainTile);
        }
        if (mainTile instanceof TileEntity && ((TileEntity)mainTile).isInvalid()) {
            this.logWarn("EnergyNet.addTile: " + mainTile + " is invalid (TileEntity.isInvalid()), aborting");
            return;
        }
        if (this.world != DimensionManager.getWorld((int)this.world.provider.getDimension())) {
            this.logDebug("EnergyNet.addTile: " + mainTile + " is in an unloaded world, aborting");
            return;
        }
        if (this.locked) {
            this.logDebug("EnergyNet.addTileEntity: adding " + mainTile + " while locked, postponing.");
            this.pendingAdds.put(mainTile, retry);
            return;
        }
        Tile tile = new Tile(this, mainTile);
        if (EnergyNetGlobal.debugTileManagement) {
            ArrayList<String> posStrings = new ArrayList<String>(tile.subTiles.size());
            for (IEnergyTile subTile : tile.subTiles) {
                posStrings.add(String.format("%s (%s)", new Object[]{subTile, EnergyNet.instance.getPos(subTile)}));
            }
            IC2.log.debug(LogCategory.EnergyNet, "positions: %s", posStrings);
        }
        Iterator it = tile.subTiles.listIterator();
        while (it.hasNext()) {
            IEnergyTile subTile = it.next();
            BlockPos pos = EnergyNet.instance.getPos(subTile).toImmutable();
            Tile conflicting = this.registeredTiles.get((Object)pos);
            boolean abort = false;
            if (conflicting != null) {
                if (mainTile == conflicting.mainTile) {
                    this.logDebug("EnergyNet.addTileEntity: " + subTile + " (" + mainTile + ") is already added using the same position, aborting");
                } else if (retry < 2) {
                    this.pendingAdds.put(mainTile, retry + 1);
                } else if (conflicting.mainTile instanceof TileEntity && ((TileEntity)mainTile).isInvalid() || EnergyNetGlobal.replaceConflicting) {
                    this.logDebug("EnergyNet.addTileEntity: " + subTile + " (" + mainTile + ") is conflicting with " + conflicting.mainTile + " (invalid=" + (conflicting.mainTile instanceof TileEntity && ((TileEntity)conflicting.mainTile).isInvalid()) + ") using the same position, which is abandoned (prev. te not removed), replacing");
                    this.removeTile(conflicting.mainTile);
                    conflicting = null;
                } else {
                    this.logWarn("EnergyNet.addTileEntity: " + subTile + " (" + mainTile + ") is still conflicting with " + conflicting.mainTile + " using the same position (overlapping), aborting");
                }
                if (conflicting != null) {
                    abort = true;
                }
            }
            if (!abort && !this.world.isBlockLoaded(pos)) {
                if (retry < 1) {
                    this.logWarn("EnergyNet.addTileEntity: " + subTile + " (" + mainTile + ") was added too early, postponing");
                    this.pendingAdds.put(mainTile, retry + 1);
                } else {
                    this.logWarn("EnergyNet.addTileEntity: " + subTile + " (" + mainTile + ") unloaded, aborting");
                }
                abort = true;
            }
            if (abort) {
                it.previous();
                while (it.hasPrevious()) {
                    subTile = (IEnergyTile)it.previous();
                    this.registeredTiles.remove((Object)EnergyNet.instance.getPos(subTile));
                }
                return;
            }
            this.registeredTiles.put(pos, tile);
            this.notifyLoadedNeighbors(pos, tile.subTiles);
        }
        this.addTileToGrids(tile);
        if (EnergyNetGlobal.verifyGrid()) {
            for (Node node : tile.nodes) {
                assert (node.getGrid() != null);
            }
        }
    }

    private void notifyLoadedNeighbors(BlockPos pos, List<IEnergyTile> excluded) {
        HashSet<BlockPos> excludedPositions = new HashSet<BlockPos>(excluded.size());
        for (IEnergyTile subTile : excluded) {
            excludedPositions.add(EnergyNet.instance.getPos(subTile).toImmutable());
        }
        Block block = this.world.getBlockState(pos).getBlock();
        int ocx = pos.getX() >> 4;
        int ocz = pos.getZ() >> 4;
        for (EnumFacing dir : EnumFacing.VALUES) {
            BlockPos cPos = pos.offset(dir);
            if (excludedPositions.contains((Object)cPos)) continue;
            int ccx = cPos.getX() >> 4;
            int ccz = cPos.getZ() >> 4;
            if (!dir.getAxis().isVertical() && (ccx != ocx || ccz != ocz) && !this.world.isBlockLoaded(cPos)) continue;
            this.world.notifyBlockOfStateChange(cPos, block);
        }
    }

    protected void removeTile(IEnergyTile mainTile) {
        boolean wasPending;
        if (this.locked) {
            throw new IllegalStateException("removeTile isn't allowed from this context");
        }
        if (EnergyNetGlobal.debugTileManagement) {
            IC2.log.debug(LogCategory.EnergyNet, "EnergyNet.removeTile(%s), world=%s, chunk=%s, this=%s", new Object[]{mainTile, EnergyNet.instance.getWorld(mainTile), EnergyNet.instance.getWorld(mainTile).getChunkFromBlockCoords(EnergyNet.instance.getPos(mainTile)), this});
        }
        List<IEnergyTile> subTiles = mainTile instanceof IMetaDelegate ? ((IMetaDelegate)mainTile).getSubTiles() : Arrays.asList(mainTile);
        boolean bl = wasPending = this.pendingAdds.remove(mainTile) != null;
        if (EnergyNetGlobal.debugTileManagement) {
            ArrayList<String> posStrings = new ArrayList<String>(subTiles.size());
            for (IEnergyTile subTile : subTiles) {
                posStrings.add(String.format("%s (%s)", new Object[]{subTile, EnergyNet.instance.getPos(subTile)}));
            }
            IC2.log.debug(LogCategory.EnergyNet, "positions: %s", posStrings);
        }
        boolean removed = false;
        for (IEnergyTile subTile : subTiles) {
            BlockPos pos = EnergyNet.instance.getPos(subTile);
            Tile tile = this.registeredTiles.get((Object)pos);
            if (tile == null) {
                if (wasPending) continue;
                this.logDebug("EnergyNet.removeTileEntity: " + subTile + " (" + mainTile + ") wasn't found (added), skipping");
                continue;
            }
            if (tile.mainTile != mainTile) {
                this.logWarn("EnergyNet.removeTileEntity: " + subTile + " (" + mainTile + ") doesn't match the registered tile " + tile.mainTile + ", skipping");
                continue;
            }
            if (!removed) {
                assert (new HashSet<IEnergyTile>(subTiles).equals(new HashSet<IEnergyTile>(tile.subTiles)));
                this.removeTileFromGrids(tile);
                removed = true;
                this.removedTiles.add(tile);
            }
            this.registeredTiles.remove((Object)pos);
            if (!this.world.isBlockLoaded(pos)) continue;
            this.notifyLoadedNeighbors(pos, tile.subTiles);
        }
    }

    protected double getTotalEnergyEmitted(TileEntity tileEntity) {
        BlockPos coords = new BlockPos((Vec3i)tileEntity.getPos());
        Tile tile = this.registeredTiles.get((Object)coords);
        if (tile == null) {
            this.logWarn("EnergyNet.getTotalEnergyEmitted: " + (Object)tileEntity + " is not added to the enet, aborting");
            return 0.0;
        }
        double ret = 0.0;
        Iterable<NodeStats> stats = tile.getStats();
        for (NodeStats stat : stats) {
            ret += stat.getEnergyOut();
        }
        return ret;
    }

    protected double getTotalEnergySunken(TileEntity tileEntity) {
        BlockPos coords = new BlockPos((Vec3i)tileEntity.getPos());
        Tile tile = this.registeredTiles.get((Object)coords);
        if (tile == null) {
            this.logWarn("EnergyNet.getTotalEnergySunken: " + (Object)tileEntity + " is not added to the enet, aborting");
            return 0.0;
        }
        double ret = 0.0;
        Iterable<NodeStats> stats = tile.getStats();
        for (NodeStats stat : stats) {
            ret += stat.getEnergyIn();
        }
        return ret;
    }

    protected NodeStats getNodeStats(IEnergyTile energyTile) {
        BlockPos coords = EnergyNet.instance.getPos(energyTile);
        Tile tile = this.registeredTiles.get((Object)coords);
        if (tile == null) {
            this.logWarn("EnergyNet.getTotalEnergySunken: " + energyTile + " is not added to the enet");
            return new NodeStats(0.0, 0.0, 0.0);
        }
        double in = 0.0;
        double out = 0.0;
        double voltage = 0.0;
        Iterable<NodeStats> stats = tile.getStats();
        for (NodeStats stat : stats) {
            in += stat.getEnergyIn();
            out += stat.getEnergyOut();
            voltage = Math.max(voltage, stat.getVoltage());
        }
        return new NodeStats(in, out, voltage);
    }

    protected Tile getTile(BlockPos pos) {
        return this.registeredTiles.get((Object)pos);
    }

    public boolean dumpDebugInfo(PrintStream console, PrintStream chat, BlockPos pos) {
        Tile tile = this.registeredTiles.get((Object)pos);
        if (tile == null) {
            return false;
        }
        chat.println("Tile " + tile + " info:");
        chat.println(" main: " + tile.mainTile);
        chat.println(" sub: " + tile.subTiles);
        chat.println(" nodes: " + tile.nodes.size());
        HashSet<Grid> processedGrids = new HashSet<Grid>();
        for (Node node : tile.nodes) {
            Grid grid = node.getGrid();
            if (!processedGrids.add(grid)) continue;
            grid.dumpNodeInfo(chat, true, node);
            grid.dumpStats(chat, true);
            grid.dumpMatrix(console, true, true, true);
            console.println("dumping graph for " + grid);
            grid.dumpGraph(true);
        }
        return true;
    }

    public List<GridInfo> getGridInfos() {
        ArrayList<GridInfo> ret = new ArrayList<GridInfo>();
        for (Grid grid : this.grids) {
            ret.add(grid.getInfo());
        }
        return ret;
    }

    protected void onTickEnd() {
        if (!IC2.platform.isSimulating()) {
            return;
        }
        this.locked = true;
        for (Grid grid22 : this.grids) {
            grid22.finishCalculation();
            grid22.updateStats();
        }
        this.locked = false;
        this.processChanges();
        if (!this.pendingAdds.isEmpty()) {
            ArrayList<Map.Entry<IEnergyTile, Integer>> pending = new ArrayList<Map.Entry<IEnergyTile, Integer>>(this.pendingAdds.entrySet());
            this.pendingAdds.clear();
            Iterator grid22 = pending.iterator();
            while (grid22.hasNext()) {
                Map.Entry entry = (Map.Entry)grid22.next();
                this.addTile((IEnergyTile)entry.getKey(), (Integer)entry.getValue());
            }
        }
        this.locked = true;
        for (Grid grid : this.grids) {
            grid.prepareCalculation();
        }
        ArrayList<Runnable> tasks = new ArrayList<Runnable>();
        for (Grid grid3 : this.grids) {
            Runnable task = grid3.startCalculation();
            if (task == null) continue;
            tasks.add(task);
        }
        IC2.getInstance().threadPool.executeAll(tasks);
        this.locked = false;
    }

    protected void addChange(Node node, EnumFacing dir, double amount, double voltage) {
        this.changes.add(new Change(node, dir, amount, voltage));
    }

    protected static int getNextGridUid() {
        return nextGridUid++;
    }

    protected static int getNextNodeUid() {
        return nextNodeUid++;
    }

    private void addTileToGrids(Tile tile) {
        ArrayList<Node> extraNodes = new ArrayList<Node>();
        for (Node node2 : tile.nodes) {
            Grid grid;
            if (EnergyNetGlobal.debugGrid) {
                IC2.log.debug(LogCategory.EnergyNet, "Adding node %s.", node2);
            }
            ArrayList<Node> neighbors = new ArrayList<Node>();
            for (IEnergyTile subTile2 : tile.subTiles) {
                for (EnumFacing dir : EnumFacing.VALUES) {
                    BlockPos coords = EnergyNet.instance.getPos(subTile2).offset(dir);
                    Tile neighborTile = this.registeredTiles.get((Object)coords);
                    if (neighborTile == null || neighborTile == node2.tile) continue;
                    for (Node neighbor : neighborTile.nodes) {
                        if (neighbor.isExtraNode()) continue;
                        boolean canEmit = false;
                        if ((node2.nodeType == NodeType.Source || node2.nodeType == NodeType.Conductor) && neighbor.nodeType != NodeType.Source) {
                            IEnergyEmitter emitter = (IEnergyEmitter)(subTile2 instanceof IEnergyEmitter ? subTile2 : node2.tile.mainTile);
                            IEnergyTile neighborSubTe = neighborTile.getSubTileAt(coords);
                            IEnergyAcceptor acceptor = (IEnergyAcceptor)(neighborSubTe instanceof IEnergyAcceptor ? neighborSubTe : neighbor.tile.mainTile);
                            canEmit = emitter.emitsEnergyTo((IEnergyAcceptor)neighbor.tile.mainTile, dir) && acceptor.acceptsEnergyFrom((IEnergyEmitter)node2.tile.mainTile, dir.getOpposite());
                        }
                        boolean canAccept = false;
                        if (!(canEmit || node2.nodeType != NodeType.Sink && node2.nodeType != NodeType.Conductor || neighbor.nodeType == NodeType.Sink)) {
                            IEnergyAcceptor acceptor = (IEnergyAcceptor)(subTile2 instanceof IEnergyAcceptor ? subTile2 : node2.tile.mainTile);
                            IEnergyTile neighborSubTe = neighborTile.getSubTileAt(coords);
                            IEnergyEmitter emitter = (IEnergyEmitter)(neighborSubTe instanceof IEnergyEmitter ? neighborSubTe : neighbor.tile.mainTile);
                            boolean bl = canAccept = acceptor.acceptsEnergyFrom((IEnergyEmitter)neighbor.tile.mainTile, dir) && emitter.emitsEnergyTo((IEnergyAcceptor)node2.tile.mainTile, dir.getOpposite());
                        }
                        if (!canEmit && !canAccept) continue;
                        neighbors.add(neighbor);
                    }
                }
            }
            if (neighbors.isEmpty()) {
                if (EnergyNetGlobal.debugGrid) {
                    IC2.log.debug(LogCategory.EnergyNet, "Creating new grid for %s.", node2);
                }
                grid = new Grid(this);
                grid.add(node2, neighbors);
                continue;
            }
            switch (node2.nodeType) {
                Node neighbor2;
                case Conductor: {
                    grid = null;
                    for (Node neighbor : neighbors) {
                        if (neighbor.nodeType != NodeType.Conductor && !neighbor.links.isEmpty()) continue;
                        if (EnergyNetGlobal.debugGrid) {
                            IC2.log.debug(LogCategory.EnergyNet, "Using %s for %s with neighbors %s.", neighbor.getGrid(), node2, neighbors);
                        }
                        grid = neighbor.getGrid();
                        break;
                    }
                    if (grid == null) {
                        if (EnergyNetGlobal.debugGrid) {
                            IC2.log.debug(LogCategory.EnergyNet, "Creating new grid for %s with neighbors %s.", node2, neighbors);
                        }
                        grid = new Grid(this);
                    }
                    HashMap<Node, Node> neighborReplacements = new HashMap<Node, Node>();
                    ListIterator<Node> it = neighbors.listIterator();
                    while (it.hasNext()) {
                        Node neighbor3 = it.next();
                        if (neighbor3.getGrid() == grid) continue;
                        if (neighbor3.nodeType != NodeType.Conductor && !neighbor3.links.isEmpty()) {
                            boolean found = false;
                            for (int i = 0; i < it.previousIndex(); ++i) {
                                neighbor2 = neighbors.get(i);
                                if (neighbor2.tile != neighbor3.tile || neighbor2.nodeType != neighbor3.nodeType || neighbor2.getGrid() != grid) continue;
                                if (EnergyNetGlobal.debugGrid) {
                                    IC2.log.debug(LogCategory.EnergyNet, "Using neighbor node %s instead of %s.", neighbor2, neighbors);
                                }
                                found = true;
                                it.set(neighbor2);
                                break;
                            }
                            if (found) continue;
                            if (EnergyNetGlobal.debugGrid) {
                                IC2.log.debug(LogCategory.EnergyNet, "Creating new extra node for neighbor %s.", neighbor3);
                            }
                            neighbor3 = new Node(this, neighbor3.tile, neighbor3.nodeType);
                            neighbor3.tile.addExtraNode(neighbor3);
                            grid.add(neighbor3, Collections.<Node>emptyList());
                            it.set(neighbor3);
                            assert (neighbor3.getGrid() != null);
                            continue;
                        }
                        grid.merge(neighbor3.getGrid(), neighborReplacements);
                    }
                    it = neighbors.listIterator();
                    while (it.hasNext()) {
                        Node neighbor4 = it.next();
                        Node replacement = (Node)neighborReplacements.get(neighbor4);
                        if (replacement != null) {
                            neighbor4 = replacement;
                            it.set(replacement);
                        }
                        assert (neighbor4.getGrid() == grid);
                    }
                    grid.add(node2, neighbors);
                    assert (node2.getGrid() != null);
                    break;
                }
                case Sink: 
                case Source: {
                    ArrayList neighborGroups = new ArrayList();
                    for (Node neighbor : neighbors) {
                        boolean found = false;
                        if (node2.nodeType == NodeType.Conductor) {
                            for (List nodeList : neighborGroups) {
                                neighbor2 = (Node)nodeList.get(0);
                                if (neighbor2.nodeType != NodeType.Conductor || neighbor2.getGrid() != neighbor.getGrid()) continue;
                                nodeList.add(neighbor);
                                found = true;
                                break;
                            }
                        }
                        if (found) continue;
                        ArrayList<Node> nodeList = new ArrayList<Node>();
                        nodeList.add(neighbor);
                        neighborGroups.add(nodeList);
                    }
                    if (EnergyNetGlobal.debugGrid) {
                        IC2.log.debug(LogCategory.EnergyNet, "Neighbor groups detected for %s: %s.", node2, neighborGroups);
                    }
                    assert (!neighborGroups.isEmpty());
                    boolean i = false;
                    while (++i < neighborGroups.size()) {
                        Node currentNode;
                        List nodeList = (List)neighborGroups.get((int)i);
                        Node neighbor3 = (Node)nodeList.get(0);
                        if (neighbor3.nodeType != NodeType.Conductor && !neighbor3.links.isEmpty()) {
                            assert (nodeList.size() == 1);
                            if (EnergyNetGlobal.debugGrid) {
                                IC2.log.debug(LogCategory.EnergyNet, "Creating new extra node for neighbor %s.", neighbor3);
                            }
                            neighbor3 = new Node(this, neighbor3.tile, neighbor3.nodeType);
                            neighbor3.tile.addExtraNode(neighbor3);
                            new Grid(this).add(neighbor3, Collections.<Node>emptyList());
                            nodeList.set(0, neighbor3);
                            assert (neighbor3.getGrid() != null);
                        }
                        if (i == false) {
                            currentNode = node2;
                        } else {
                            if (EnergyNetGlobal.debugGrid) {
                                IC2.log.debug(LogCategory.EnergyNet, "Creating new extra node for %s.", node2);
                            }
                            currentNode = new Node(this, tile, node2.nodeType);
                            currentNode.setExtraNode(true);
                            extraNodes.add(currentNode);
                        }
                        neighbor3.getGrid().add(currentNode, nodeList);
                        if ($assertionsDisabled || currentNode.getGrid() != null) continue;
                        throw new AssertionError();
                    }
                    break block0;
                }
            }
        }
        for (Node node : extraNodes) {
            tile.addExtraNode(node);
        }
    }

    private void removeTileFromGrids(Tile tile) {
        for (Node node : tile.nodes) {
            node.getGrid().remove(node);
        }
    }

    private void processChanges() {
        for (Tile tile : this.removedTiles) {
            Iterator<Change> it = this.changes.iterator();
            while (it.hasNext()) {
                Change change = it.next();
                if (change.node.tile != tile) continue;
                Tile replacement = this.registeredTiles.get((Object)EnergyNet.instance.getPos(change.node.tile.mainTile));
                boolean validReplacement = false;
                if (replacement != null) {
                    for (Node node2 : replacement.nodes) {
                        if (node2.nodeType != change.node.nodeType || node2.getGrid() != change.node.getGrid()) continue;
                        if (EnergyNetGlobal.debugGrid) {
                            IC2.log.debug(LogCategory.EnergyNet, "Redirecting change %s to replacement node %s.", change, node2);
                        }
                        change.node = node2;
                        validReplacement = true;
                        break;
                    }
                }
                if (validReplacement) continue;
                it.remove();
                Iterator<Change> sameGridSourceChanges = new ArrayList();
                for (Change change22 : this.changes) {
                    if (change22.node.nodeType != NodeType.Source || change.node.getGrid() != change22.node.getGrid()) continue;
                    sameGridSourceChanges.add(change22);
                }
                if (EnergyNetGlobal.debugGrid) {
                    IC2.log.debug(LogCategory.EnergyNet, "Redistributing change %s to remaining source nodes %s.", change, sameGridSourceChanges);
                }
                Iterator node2 = sameGridSourceChanges.iterator();
                while (node2.hasNext()) {
                    Change change2 = (Change)node2.next();
                    change2.setAmount(change2.getAmount() - Math.abs(change.getAmount()) / (double)sameGridSourceChanges.size());
                }
            }
        }
        this.removedTiles.clear();
        for (Change change2 : this.changes) {
            if (change2.node.nodeType != NodeType.Sink) continue;
            assert (change2.getAmount() > 0.0);
            IEnergySink sink = (IEnergySink)change2.node.tile.mainTile;
            double returned = sink.injectEnergy(change2.dir, change2.getAmount(), change2.getVoltage());
            if (EnergyNetGlobal.debugGrid) {
                IC2.log.debug(LogCategory.EnergyNet, "Applied change %s, %f EU returned.", change2, returned);
            }
            if (returned <= 0.0) continue;
            ArrayList<Change> sameGridSourceChanges = new ArrayList<Change>();
            for (Change change23 : this.changes) {
                if (change23.node.nodeType != NodeType.Source || change2.node.getGrid() != change23.node.getGrid()) continue;
                sameGridSourceChanges.add(change23);
            }
            if (EnergyNetGlobal.debugGrid) {
                IC2.log.debug(LogCategory.EnergyNet, "Redistributing returned amount to source nodes %s.", sameGridSourceChanges);
            }
            for (Change change22 : sameGridSourceChanges) {
                change22.setAmount(change22.getAmount() - returned / (double)sameGridSourceChanges.size());
            }
        }
        for (Change change : this.changes) {
            if (change.node.nodeType != NodeType.Source) continue;
            assert (change.getAmount() <= 0.0);
            if (change.getAmount() >= 0.0) continue;
            IEnergySource source = (IEnergySource)change.node.tile.mainTile;
            source.drawEnergy(change.getAmount());
            if (!EnergyNetGlobal.debugGrid) continue;
            IC2.log.debug(LogCategory.EnergyNet, "Applied change %s.", change);
        }
        this.changes.clear();
    }

    private void logDebug(String msg) {
        if (!this.shouldLog(msg)) {
            return;
        }
        IC2.log.debug(LogCategory.EnergyNet, msg);
        if (EnergyNetGlobal.debugTileManagement) {
            IC2.log.debug(LogCategory.EnergyNet, new Throwable(), "stack trace");
            if (TickHandler.getLastDebugTrace() != null) {
                IC2.log.debug(LogCategory.EnergyNet, TickHandler.getLastDebugTrace(), "parent stack trace");
            }
        }
    }

    private void logWarn(String msg) {
        if (!this.shouldLog(msg)) {
            return;
        }
        IC2.log.warn(LogCategory.EnergyNet, msg);
        if (EnergyNetGlobal.debugTileManagement) {
            IC2.log.debug(LogCategory.EnergyNet, new Throwable(), "stack trace");
            if (TickHandler.getLastDebugTrace() != null) {
                IC2.log.debug(LogCategory.EnergyNet, TickHandler.getLastDebugTrace(), "parent stack trace");
            }
        }
    }

    private boolean shouldLog(String msg) {
        if (EnergyNetGlobal.logAll) {
            return true;
        }
        this.cleanRecentLogs();
        msg = msg.replaceAll("@[0-9a-f]+", "@x");
        long time = System.nanoTime();
        Long lastLog = this.recentLogs.put(msg, time);
        return lastLog == null || lastLog < time - 300000000000L;
    }

    private void cleanRecentLogs() {
        if (this.recentLogs.size() < 100) {
            return;
        }
        long minTime = System.nanoTime() - 300000000000L;
        Iterator<Long> it = this.recentLogs.values().iterator();
        while (it.hasNext()) {
            long recTime = it.next();
            if (recTime >= minTime) continue;
            it.remove();
        }
    }

}

