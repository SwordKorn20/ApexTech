/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldProvider
 */
package ic2.core.energy.leg;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.IEnergyNet;
import ic2.api.energy.tile.IEnergyAcceptor;
import ic2.api.energy.tile.IEnergyConductor;
import ic2.api.energy.tile.IEnergyEmitter;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import ic2.api.energy.tile.IExplosionPowerOverride;
import ic2.api.energy.tile.IMetaDelegate;
import ic2.api.energy.tile.IMultiEnergySource;
import ic2.api.energy.tile.IOverloadHandler;
import ic2.core.ExplosionIC2;
import ic2.core.IC2;
import ic2.core.IC2DamageSource;
import ic2.core.IWorldTickCallback;
import ic2.core.Platform;
import ic2.core.TickHandler;
import ic2.core.WorldData;
import ic2.core.energy.leg.EnergyPath;
import ic2.core.energy.leg.Tile;
import ic2.core.util.Log;
import ic2.core.util.LogCategory;
import ic2.core.util.Tuple;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;

public final class EnergyNetLocalLeg {
    private final Map<Tile, List<EnergyPath>> energySourceToEnergyPathMap = new HashMap<Tile, List<EnergyPath>>();
    private final Map<EntityLivingBase, Integer> entityLivingToShockEnergyMap = new HashMap<EntityLivingBase, Integer>();
    private final Map<Tile, Tuple.T2<Iterable<EnergyPath>, Iterable<EnergyPath>>> pathCache = new HashMap<Tile, Tuple.T2<Iterable<EnergyPath>, Iterable<EnergyPath>>>();
    private final Set<BlockPos> blocksToUpdate = new HashSet<BlockPos>();
    final Map<BlockPos, Tile> registeredTiles = new HashMap<BlockPos, Tile>();
    private final World world;
    private final List<Tile> sources = new ArrayList<Tile>();
    private static int apiDemandsErrorCooldown = 0;
    private static int apiEmitErrorCooldown = 0;
    private static final double minConductionLoss = 1.0E-4;

    public EnergyNetLocalLeg(World world) {
        this.world = world;
    }

    public static EnergyNetLocalLeg getForWorld(World world) {
        WorldData worldData = WorldData.get(world);
        return worldData.energyNetLeg;
    }

    synchronized void tickEnd() {
        IC2.platform.profilerStartSection("EnergyNet");
        for (int i = 0; i < this.sources.size(); ++i) {
            IMultiEnergySource source1;
            int unused;
            int offer;
            Tile tile = this.sources.get(i);
            IEnergySource source = (IEnergySource)tile.entity;
            List<EnergyPath> paths = this.energySourceToEnergyPathMap.get(tile);
            if (paths != null) {
                for (EnergyPath path : this.energySourceToEnergyPathMap.get(tile)) {
                    path.energyConducted = 0;
                    path.maxPacketConducted = 0;
                }
            }
            int amount = 1;
            if (source instanceof IMultiEnergySource && (source1 = (IMultiEnergySource)source).sendMultipleEnergyPackets()) {
                amount = source1.getMultipleEnergyPacketAmount();
            }
            for (int j = 0; j < amount && (offer = (int)source.getOfferedEnergy()) > 0 && (unused = this.emitEnergyFrom(tile, offer)) < offer; ++j) {
                source.drawEnergy(offer - unused);
            }
        }
        if (this.world.provider.getDimension() == 0) {
            if (apiDemandsErrorCooldown > 0) {
                --apiDemandsErrorCooldown;
            }
            if (apiEmitErrorCooldown > 0) {
                --apiEmitErrorCooldown;
            }
        }
        IC2.platform.profilerEndSection();
    }

    synchronized void tickStart() {
        IC2.platform.profilerEndStartSection("EnergyNet");
        for (Map.Entry<EntityLivingBase, Integer> entry : this.entityLivingToShockEnergyMap.entrySet()) {
            EntityLivingBase target = entry.getKey();
            int damage = (entry.getValue() + 63) / 64;
            if (!target.isEntityAlive() || damage <= 0) continue;
            target.attackEntityFrom((DamageSource)IC2DamageSource.electricity, (float)damage);
        }
        this.entityLivingToShockEnergyMap.clear();
        for (BlockPos pos : this.blocksToUpdate) {
            this.world.notifyBlockOfStateChange(pos, this.world.getBlockState(pos).getBlock());
        }
        this.blocksToUpdate.clear();
        IC2.platform.profilerEndSection();
    }

    public synchronized void addTileEntity(IEnergyTile te) {
        if (te instanceof TileEntity && ((TileEntity)te).isInvalid()) {
            IC2.log.warn(LogCategory.EnergyNet, "EnergyNet.addTileEntity: " + te + " is invalid (TileEntity.isInvalid()), aborting");
            return;
        }
        BlockPos coords = EnergyNet.instance.getPos(te);
        World world = EnergyNet.instance.getWorld(te);
        if (this.registeredTiles.containsKey((Object)coords)) {
            IC2.log.warn(LogCategory.EnergyNet, "EnergyNet.addTileEntity: " + te + " is already added, aborting");
            return;
        }
        IBlockState state = world.getBlockState(coords);
        if (state.getBlock().isAir(state, (IBlockAccess)world, coords)) {
            IC2.log.warn(LogCategory.EnergyNet, "EnergyNet.addTileEntity: " + te + " was added too early, postponing");
            IC2.tickHandler.requestSingleWorldTick(world, new PostPonedAddCallback(te));
            return;
        }
        List subTiles = new LinkedList();
        subTiles = te instanceof IMetaDelegate ? ((IMetaDelegate)te).getSubTiles() : Collections.singletonList(te);
        for (IEnergyTile subTile : subTiles) {
            Tile tile = new Tile(this, te, subTile);
            BlockPos pos = EnergyNet.instance.getPos(subTile).toImmutable();
            this.registeredTiles.put(pos, tile);
            if (te instanceof IEnergyAcceptor) {
                if (te instanceof IEnergyConductor && tile.getAmountNeighbors() < 2) {
                    this.markBlockForUpdateWithNeighbors(pos);
                    continue;
                }
                List<EnergyPath> reverseEnergyPaths = this.discover(tile, true, Integer.MAX_VALUE);
                for (EnergyPath reverseEnergyPath : reverseEnergyPaths) {
                    Tile srcTile = reverseEnergyPath.target;
                    if (!this.energySourceToEnergyPathMap.containsKey(srcTile) || (double)EnergyNetLocalLeg.getMaxOutput((IEnergySource)srcTile.entity) <= reverseEnergyPath.loss) continue;
                    this.energySourceToEnergyPathMap.remove(srcTile);
                    this.pathCache.clear();
                }
            }
            if (te instanceof IEnergySource && ((IEnergySource)te).getSourceTier() > 0) {
                this.sources.add(tile);
            }
            this.markBlockForUpdateWithNeighbors(pos);
        }
    }

    public synchronized void removeTileEntity(IEnergyTile te) {
        if (!(te instanceof IEnergyTile)) {
            IC2.log.warn(LogCategory.EnergyNet, "EnergyNet.removeTileEntity: " + te + " doesn't implement IEnergyTile, aborting");
            return;
        }
        List<IEnergyTile> subTiles = te instanceof IMetaDelegate ? ((IMetaDelegate)te).getSubTiles() : Collections.singletonList(te);
        for (IEnergyTile subTile : subTiles) {
            BlockPos subPos = EnergyNet.instance.getPos(subTile);
            Tile tile = this.registeredTiles.get((Object)subPos);
            if (tile == null) {
                IC2.log.warn(LogCategory.EnergyNet, "EnergyNet.removeTileEntity: " + te + " is already removed, aborting");
                return;
            }
            if (tile.entity instanceof IEnergyAcceptor) {
                List<EnergyPath> reverseEnergyPaths = this.discover(tile, true, Integer.MAX_VALUE);
                for (EnergyPath reverseEnergyPath : reverseEnergyPaths) {
                    Tile srcTile = reverseEnergyPath.target;
                    if (!this.energySourceToEnergyPathMap.containsKey(srcTile) || (double)EnergyNetLocalLeg.getMaxOutput((IEnergySource)srcTile.entity) <= reverseEnergyPath.loss) continue;
                    if (tile.entity instanceof IEnergyConductor) {
                        this.energySourceToEnergyPathMap.remove(srcTile);
                    } else {
                        Iterator<EnergyPath> it = this.energySourceToEnergyPathMap.get(srcTile).iterator();
                        while (it.hasNext()) {
                            if (it.next().target != tile) continue;
                            it.remove();
                            break;
                        }
                    }
                    this.pathCache.clear();
                }
            }
            if (te instanceof IEnergySource) {
                this.pathCache.clear();
                this.energySourceToEnergyPathMap.remove(tile);
                this.sources.remove(tile);
            }
            tile.destroy();
            this.registeredTiles.remove((Object)EnergyNet.instance.getPos(tile.subTile));
            this.markBlockForUpdateWithNeighbors(EnergyNet.instance.getPos(te));
        }
    }

    public synchronized int emitEnergyFrom(Tile tile, int amount) {
        Object energyLoss;
        Iterator<Tile> dstTile;
        IEnergyTile srcTe = tile.entity;
        if (srcTe instanceof TileEntity && ((TileEntity)srcTe).isInvalid()) {
            IC2.log.warn(LogCategory.EnergyNet, "EnergyNet.emitEnergyFrom: " + srcTe + " is invalid (TileEntity.isInvalid()), aborting");
            return amount;
        }
        if (!this.energySourceToEnergyPathMap.containsKey(tile)) {
            this.pathCache.clear();
            this.energySourceToEnergyPathMap.put(tile, this.discover(tile, false, EnergyNetLocalLeg.getMaxOutput((IEnergySource)tile.entity)));
        }
        Vector<EnergyPath> activeEnergyPaths = new Vector<EnergyPath>();
        double totalInvLoss = 0.0;
        for (EnergyPath energyPath : this.energySourceToEnergyPathMap.get(tile)) {
            IEnergySink sink = (IEnergySink)energyPath.target.entity;
            if (sink.getDemandedEnergy() <= 0.0 || energyPath.loss >= (double)amount) continue;
            totalInvLoss += 1.0 / energyPath.loss;
            activeEnergyPaths.add(energyPath);
        }
        Collections.shuffle(activeEnergyPaths);
        for (int i = activeEnergyPaths.size() - amount; i > 0; --i) {
            EnergyPath removedEnergyPath = (EnergyPath)activeEnergyPaths.remove(activeEnergyPaths.size() - 1);
            totalInvLoss -= 1.0 / removedEnergyPath.loss;
        }
        HashMap<EnergyPath, Integer> suppliedEnergyPaths = new HashMap<EnergyPath, Integer>();
        LinkedList<IEnergyTile> blocksToExplode = new LinkedList<IEnergyTile>();
        while (!activeEnergyPaths.isEmpty() && amount > 0) {
            int energyConsumed = 0;
            double newTotalInvLoss = 0.0;
            Vector<EnergyPath> currentActiveEnergyPaths = activeEnergyPaths;
            activeEnergyPaths = new Vector();
            activeEnergyPaths.iterator();
            for (EnergyPath energyPath2 : currentActiveEnergyPaths) {
                dstTile = energyPath2.target;
                IEnergySink sink = (IEnergySink)dstTile.entity;
                int energyProvided = (int)Math.floor((double)Math.round((double)amount / totalInvLoss / energyPath2.loss * 100000.0) / 100000.0);
                if (energyProvided > (energyLoss = (int)Math.floor(energyPath2.loss))) {
                    int injected = energyProvided - energyLoss;
                    int energyReturned = (int)sink.injectEnergy(energyPath2.targetDirection, injected, EnergyNet.instance.getTierFromPower(injected));
                    if (energyReturned == 0 && sink.getDemandedEnergy() > 0.0) {
                        activeEnergyPaths.add(energyPath2);
                        newTotalInvLoss += 1.0 / energyPath2.loss;
                        blocksToExplode.add(0, dstTile.entity);
                    } else if (energyReturned >= injected) {
                        energyReturned = injected;
                        if (apiDemandsErrorCooldown == 0) {
                            apiDemandsErrorCooldown = 600;
                            IEnergyTile te = dstTile.entity;
                            World world = EnergyNet.instance.getWorld(te);
                            BlockPos pos = EnergyNet.instance.getPos(te);
                            String c = (world == null ? "unknown" : Integer.valueOf(world.provider.getDimension())) + ":" + (Object)pos;
                            IC2.log.warn(LogCategory.EnergyNet, "API ERROR: " + dstTile + " (" + c + ") didn't implement demandsEnergy() properly, no energy from injectEnergy accepted (" + energyReturned + ") although demandsEnergy() requested " + (energyProvided - energyLoss) + ".");
                        }
                    }
                    energyConsumed += energyProvided - energyReturned;
                    int energyInjected = energyProvided - energyLoss - energyReturned;
                    if (!suppliedEnergyPaths.containsKey(energyPath2)) {
                        suppliedEnergyPaths.put(energyPath2, energyInjected);
                        continue;
                    }
                    suppliedEnergyPaths.put(energyPath2, energyInjected + (Integer)suppliedEnergyPaths.get(energyPath2));
                    continue;
                }
                activeEnergyPaths.add(energyPath2);
                newTotalInvLoss += 1.0 / energyPath2.loss;
            }
            if (energyConsumed == 0 && !activeEnergyPaths.isEmpty()) {
                EnergyPath removedEnergyPath = (EnergyPath)activeEnergyPaths.remove(activeEnergyPaths.size() - 1);
                newTotalInvLoss -= 1.0 / removedEnergyPath.loss;
            }
            totalInvLoss = newTotalInvLoss;
            amount -= energyConsumed;
        }
        World world = EnergyNet.instance.getWorld(srcTe);
        for (Map.Entry entry : suppliedEnergyPaths.entrySet()) {
            EnergyPath energyPath3 = (EnergyPath)entry.getKey();
            int energyInjected = (Integer)entry.getValue();
            energyPath3.energyConducted += (long)energyInjected;
            energyPath3.maxPacketConducted = Math.max(energyPath3.maxPacketConducted, energyInjected);
            if (energyInjected > energyPath3.minInsulationEnergyAbsorption) {
                List entitiesNearEnergyPath = world.getEntitiesWithinAABB((Class)EntityLivingBase.class, new AxisAlignedBB((double)(energyPath3.minX - 1), (double)(energyPath3.minY - 1), (double)(energyPath3.minZ - 1), (double)(energyPath3.maxX + 2), (double)(energyPath3.maxY + 2), (double)(energyPath3.maxZ + 2)));
                dstTile = entitiesNearEnergyPath.iterator();
                while (dstTile.hasNext()) {
                    EntityLivingBase entityLiving = (EntityLivingBase)dstTile.next();
                    int maxShockEnergy = 0;
                    energyLoss = energyPath3.conductors.iterator();
                    while (energyLoss.hasNext()) {
                        Tile condTile = (Tile)energyLoss.next();
                        IEnergyTile te = condTile.entity;
                        IEnergyConductor conductor = (IEnergyConductor)te;
                        BlockPos tilePos = EnergyNet.instance.getPos(te);
                        if (!entityLiving.getEntityBoundingBox().intersectsWith(new AxisAlignedBB((double)(tilePos.getX() - 1), (double)(tilePos.getY() - 1), (double)(tilePos.getZ() - 1), (double)(tilePos.getX() + 2), (double)(tilePos.getY() + 2), (double)(tilePos.getZ() + 2)))) continue;
                        int shockEnergy = (int)((double)energyInjected - conductor.getInsulationEnergyAbsorption());
                        if (shockEnergy > maxShockEnergy) {
                            maxShockEnergy = shockEnergy;
                        }
                        if (conductor.getInsulationEnergyAbsorption() != (double)energyPath3.minInsulationEnergyAbsorption) continue;
                        break;
                    }
                    if (this.entityLivingToShockEnergyMap.containsKey((Object)entityLiving)) {
                        this.entityLivingToShockEnergyMap.put(entityLiving, this.entityLivingToShockEnergyMap.get((Object)entityLiving) + maxShockEnergy);
                        continue;
                    }
                    this.entityLivingToShockEnergyMap.put(entityLiving, maxShockEnergy);
                }
                if (energyInjected >= energyPath3.minInsulationBreakdownEnergy) {
                    for (Tile condTile : energyPath3.conductors) {
                        IEnergyConductor conductor = (IEnergyConductor)condTile.entity;
                        if ((double)energyInjected < conductor.getInsulationBreakdownEnergy()) continue;
                        conductor.removeInsulation();
                        if (conductor.getInsulationEnergyAbsorption() >= (double)energyPath3.minInsulationEnergyAbsorption) continue;
                        energyPath3.minInsulationEnergyAbsorption = (int)conductor.getInsulationEnergyAbsorption();
                    }
                }
            }
            if (energyInjected >= energyPath3.minConductorBreakdownEnergy) {
                for (Tile condTile : energyPath3.conductors) {
                    IEnergyConductor conductor = (IEnergyConductor)condTile.entity;
                    if ((double)energyInjected < conductor.getConductorBreakdownEnergy()) continue;
                    conductor.removeConductor();
                }
            }
            if ((double)energyInjected <= EnergyNet.instance.getPowerFromTier(((IEnergySink)energyPath3.target.entity).getSinkTier())) continue;
            EnergyNetLocalLeg.explodeTile(energyPath3.target.entity, EnergyNet.instance.getTierFromPower(energyInjected));
        }
        return amount;
    }

    public synchronized Tuple.T2<Iterable<EnergyPath>, Iterable<EnergyPath>> getEnergyPathsContaining(Tile tile) {
        if (this.pathCache.containsKey(tile)) {
            return this.pathCache.get(tile);
        }
        LinkedList<EnergyPath> in = new LinkedList<EnergyPath>();
        LinkedList<EnergyPath> out = new LinkedList<EnergyPath>();
        if (this.energySourceToEnergyPathMap.containsKey(tile)) {
            out.addAll((Collection)this.energySourceToEnergyPathMap.get(tile));
        }
        if (tile.entity instanceof IEnergyConductor || tile.entity instanceof IEnergySink) {
            List<EnergyPath> reverseEnergyPaths = this.discover(tile, true, Integer.MAX_VALUE);
            for (EnergyPath reverseEnergyPath : reverseEnergyPaths) {
                Tile srcTile = reverseEnergyPath.target;
                if (!this.energySourceToEnergyPathMap.containsKey(srcTile) || (double)EnergyNetLocalLeg.getMaxOutput((IEnergySource)srcTile.entity) <= reverseEnergyPath.loss) continue;
                for (EnergyPath energyPath : this.energySourceToEnergyPathMap.get(srcTile)) {
                    if (tile.entity instanceof IEnergySink && energyPath.target == tile) {
                        in.add(energyPath);
                        continue;
                    }
                    if (!(tile.entity instanceof IEnergyConductor) || !energyPath.conductors.contains(tile)) continue;
                    out.add(energyPath);
                    in.add(energyPath);
                }
            }
        }
        Tuple.T2<Iterable<EnergyPath>, Iterable<EnergyPath>> ret = new Tuple.T2<Iterable<EnergyPath>, Iterable<EnergyPath>>(in, out);
        this.pathCache.put(tile, ret);
        return ret;
    }

    private Tile getNeighbor(Tile te, EnumFacing dir) {
        return te.neighbors[dir.ordinal()];
    }

    private List<EnergyPath> discover(Tile emitter, boolean reverse, int lossLimit) {
        HashMap<Tile, EnergyBlockLink> reachedTileEntities = new HashMap<Tile, EnergyBlockLink>();
        LinkedList<Tile> tileEntitiesToCheck = new LinkedList<Tile>();
        tileEntitiesToCheck.add(emitter);
        World world = EnergyNet.instance.getWorld(emitter.subTile);
        while (!tileEntitiesToCheck.isEmpty()) {
            Tile tile = (Tile)tileEntitiesToCheck.remove();
            assert (world == EnergyNet.instance.getWorld(tile.subTile));
            if (!world.isBlockLoaded(EnergyNet.instance.getPos(tile.subTile))) continue;
            double currentLoss = 0.0;
            if (tile != emitter) {
                currentLoss = ((EnergyBlockLink)reachedTileEntities.get((Object)tile)).loss;
            }
            List<EnergyTarget> validReceivers = this.getValidReceivers(tile, reverse);
            for (EnergyTarget validReceiver : validReceivers) {
                if (validReceiver.tile == emitter) continue;
                double additionalLoss = 0.0;
                if (validReceiver.tile.entity instanceof IEnergyConductor) {
                    additionalLoss = ((IEnergyConductor)validReceiver.tile.entity).getConductionLoss();
                    if (additionalLoss < 1.0E-4) {
                        additionalLoss = 1.0E-4;
                    }
                    if (currentLoss + additionalLoss >= (double)lossLimit) continue;
                }
                if (reachedTileEntities.containsKey(validReceiver.tile) && ((EnergyBlockLink)reachedTileEntities.get((Object)validReceiver.tile)).loss <= currentLoss + additionalLoss) continue;
                reachedTileEntities.put(validReceiver.tile, new EnergyBlockLink(validReceiver.direction, currentLoss + additionalLoss));
                if (!(validReceiver.tile.entity instanceof IEnergyConductor)) continue;
                tileEntitiesToCheck.remove(validReceiver.tile);
                tileEntitiesToCheck.add(validReceiver.tile);
            }
        }
        LinkedList<EnergyPath> energyPaths = new LinkedList<EnergyPath>();
        block2 : for (Map.Entry entry : reachedTileEntities.entrySet()) {
            Tile tile = (Tile)entry.getKey();
            if ((reverse || !(tile.entity instanceof IEnergySink)) && (!reverse || !(tile.entity instanceof IEnergySource))) continue;
            EnergyBlockLink energyBlockLink = (EnergyBlockLink)entry.getValue();
            EnergyPath energyPath = new EnergyPath();
            energyPath.loss = energyBlockLink.loss > 0.1 ? energyBlockLink.loss : 0.1;
            energyPath.target = tile;
            energyPath.targetDirection = energyBlockLink.direction;
            if (!reverse && emitter.entity instanceof IEnergySource) {
                while ((tile = this.getNeighbor(tile, energyBlockLink.direction)) != emitter) {
                    if (tile.entity instanceof IEnergyConductor) {
                        IEnergyTile te = tile.entity;
                        IEnergyConductor energyConductor = (IEnergyConductor)te;
                        BlockPos pos = EnergyNet.instance.getPos(te);
                        if (pos.getX() < energyPath.minX) {
                            energyPath.minX = pos.getX();
                        }
                        if (pos.getY() < energyPath.minY) {
                            energyPath.minY = pos.getY();
                        }
                        if (pos.getZ() < energyPath.minZ) {
                            energyPath.minZ = pos.getZ();
                        }
                        if (pos.getX() > energyPath.maxX) {
                            energyPath.maxX = pos.getX();
                        }
                        if (pos.getY() > energyPath.maxY) {
                            energyPath.maxY = pos.getY();
                        }
                        if (pos.getZ() > energyPath.maxZ) {
                            energyPath.maxZ = pos.getZ();
                        }
                        energyPath.conductors.add(tile);
                        if (energyConductor.getInsulationEnergyAbsorption() < (double)energyPath.minInsulationEnergyAbsorption) {
                            energyPath.minInsulationEnergyAbsorption = (int)energyConductor.getInsulationEnergyAbsorption();
                        }
                        if (energyConductor.getInsulationBreakdownEnergy() < (double)energyPath.minInsulationBreakdownEnergy) {
                            energyPath.minInsulationBreakdownEnergy = (int)energyConductor.getInsulationBreakdownEnergy();
                        }
                        if (energyConductor.getConductorBreakdownEnergy() < (double)energyPath.minConductorBreakdownEnergy) {
                            energyPath.minConductorBreakdownEnergy = (int)energyConductor.getConductorBreakdownEnergy();
                        }
                        if ((energyBlockLink = (EnergyBlockLink)reachedTileEntities.get(tile)) != null) continue;
                        IEnergyTile srcTe = emitter.entity;
                        IEnergyTile dstTe = energyPath.target.entity;
                        IC2.platform.displayError("An energy network pathfinding entry is corrupted.\nThis could happen due to incorrect Minecraft behavior or a bug.\n\n(Technical information: energyBlockLink, tile entities below)\nE: " + srcTe + " (" + (Object)EnergyNet.instance.getPos(srcTe) + ")\n" + "C: " + te + " (" + (Object)EnergyNet.instance.getPos(te) + ")\n" + "R: " + dstTe + " (" + (Object)EnergyNet.instance.getPos(dstTe) + ")", new Object[0]);
                        continue;
                    }
                    IC2.log.warn(LogCategory.EnergyNet, "EnergyNet: EnergyBlockLink corrupted (" + energyPath.target.entity + " [" + (Object)EnergyNet.instance.getPos(energyPath.target.entity) + "] -> " + tile.entity + " [" + (Object)EnergyNet.instance.getPos(tile.entity) + "] -> " + emitter.entity + " [" + (Object)EnergyNet.instance.getPos(emitter.entity) + "])");
                    continue block2;
                }
            }
            energyPaths.add(energyPath);
        }
        return energyPaths;
    }

    private List<EnergyTarget> getValidReceivers(Tile emitter, boolean reverse) {
        LinkedList<EnergyTarget> validReceivers = new LinkedList<EnergyTarget>();
        for (EnumFacing direction : EnumFacing.VALUES) {
            Tile target = this.getNeighbor(emitter, direction);
            if (target == null) continue;
            EnumFacing inverseDirection = direction.getOpposite();
            IEnergyEmitter emitterSource = EnergyNetLocalLeg.asEmitter(emitter);
            IEnergyAcceptor emitterSink = EnergyNetLocalLeg.asAcceptor(emitter);
            IEnergyEmitter targetSource = EnergyNetLocalLeg.asEmitter(target);
            IEnergyAcceptor targetSink = EnergyNetLocalLeg.asAcceptor(target);
            if ((reverse || emitterSource == null || targetSink == null || !emitterSource.emitsEnergyTo(targetSink, direction)) && (!reverse || emitterSink == null || targetSource == null || !emitterSink.acceptsEnergyFrom(targetSource, direction)) || (reverse || targetSink == null || emitterSource == null || !targetSink.acceptsEnergyFrom(emitterSource, inverseDirection)) && (!reverse || targetSource == null || emitterSink == null || !targetSource.emitsEnergyTo(emitterSink, inverseDirection))) continue;
            validReceivers.add(new EnergyTarget(target, inverseDirection));
        }
        return validReceivers;
    }

    private void markBlockForUpdate(BlockPos pos) {
        this.blocksToUpdate.add(pos.toImmutable());
    }

    private void markBlockForUpdateWithNeighbors(BlockPos pos) {
        this.markBlockForUpdate(pos);
        for (EnumFacing facing : EnumFacing.VALUES) {
            this.markBlockForUpdate(pos.offset(facing));
        }
    }

    private static int getMaxOutput(IEnergySource source) {
        return (int)EnergyNet.instance.getPowerFromTier(source.getSourceTier());
    }

    private static void explodeTile(IEnergyTile te, int tier) {
        World world = EnergyNet.instance.getWorld(te);
        List<IEnergyTile> toExplode = te instanceof IMetaDelegate ? ((IMetaDelegate)te).getSubTiles() : Collections.singletonList(te);
        for (IEnergyTile current : toExplode) {
            IExplosionPowerOverride override;
            BlockPos pos = EnergyNet.instance.getPos(current);
            TileEntity realTe = world.getTileEntity(pos);
            if (te instanceof IOverloadHandler && ((IOverloadHandler)((Object)te)).onOverload(tier) || realTe instanceof IOverloadHandler && ((IOverloadHandler)realTe).onOverload(tier)) continue;
            float power = 2.5f;
            if (te instanceof IExplosionPowerOverride) {
                override = (IExplosionPowerOverride)((Object)te);
                if (!override.shouldExplode()) continue;
                power = override.getExplosionPower(tier, power);
            } else if (realTe instanceof IExplosionPowerOverride) {
                override = (IExplosionPowerOverride)realTe;
                if (!override.shouldExplode()) continue;
                power = override.getExplosionPower(tier, power);
            }
            world.setBlockToAir(pos);
            ExplosionIC2 explosion = new ExplosionIC2(world, null, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, power, 0.75f);
            explosion.doExplosion();
        }
    }

    private static IEnergyAcceptor asAcceptor(Tile tile) {
        if (tile.subTile instanceof IEnergyAcceptor) {
            return (IEnergyAcceptor)tile.subTile;
        }
        if (tile.entity instanceof IEnergyAcceptor) {
            return (IEnergyAcceptor)tile.entity;
        }
        return null;
    }

    private static IEnergyEmitter asEmitter(Tile tile) {
        if (tile.subTile instanceof IEnergyEmitter) {
            return (IEnergyEmitter)tile.subTile;
        }
        if (tile.entity instanceof IEnergyEmitter) {
            return (IEnergyEmitter)tile.entity;
        }
        return null;
    }

    private static class EnergyTarget {
        Tile tile;
        EnumFacing direction;

        EnergyTarget(Tile tile, EnumFacing direction) {
            this.tile = tile;
            this.direction = direction;
        }
    }

    private static class EnergyBlockLink {
        EnumFacing direction;
        double loss;

        EnergyBlockLink(EnumFacing direction, double loss) {
            this.direction = direction;
            this.loss = loss;
        }
    }

    private static class PostPonedAddCallback
    implements IWorldTickCallback {
        private final IEnergyTile te;

        public PostPonedAddCallback(IEnergyTile te) {
            this.te = te;
        }

        @Override
        public void onTick(World world) {
            BlockPos pos = EnergyNet.instance.getPos(this.te);
            IBlockState state = world.getBlockState(pos);
            if (!state.getBlock().isAir(state, (IBlockAccess)world, pos)) {
                EnergyNetLocalLeg.getForWorld(world).addTileEntity(this.te);
            } else {
                IC2.log.info(LogCategory.EnergyNet, "EnergyNet.addTileEntity: " + this.te + " unloaded, aborting");
            }
        }
    }

}

