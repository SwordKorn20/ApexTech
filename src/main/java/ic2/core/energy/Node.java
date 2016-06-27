/*
 * Decompiled with CFR 0_114.
 */
package ic2.core.energy;

import ic2.api.energy.NodeStats;
import ic2.api.energy.tile.IEnergyConductor;
import ic2.api.energy.tile.IEnergySink;
import ic2.api.energy.tile.IEnergySource;
import ic2.api.energy.tile.IEnergyTile;
import ic2.core.IC2;
import ic2.core.energy.EnergyNetGlobal;
import ic2.core.energy.EnergyNetLocal;
import ic2.core.energy.Grid;
import ic2.core.energy.MutableNodeStats;
import ic2.core.energy.NodeLink;
import ic2.core.energy.NodeType;
import ic2.core.energy.Tile;
import ic2.core.util.Log;
import ic2.core.util.LogCategory;
import ic2.core.util.Util;
import java.util.ArrayList;
import java.util.List;

class Node {
    final int uid;
    final Tile tile;
    final NodeType nodeType;
    private final Node parent;
    private boolean isExtraNode = false;
    private int tier;
    private double amount;
    private double resistance;
    private double voltage;
    private double currentIn;
    private double currentOut;
    private Grid grid;
    List<NodeLink> links = new ArrayList<NodeLink>();
    private final MutableNodeStats lastNodeStats = new MutableNodeStats();

    Node(EnergyNetLocal energyNet, Tile tile1, NodeType nodeType1) {
        if (energyNet == null) {
            throw new NullPointerException("The energyNet parameter must not be null.");
        }
        if (tile1 == null) {
            throw new NullPointerException("The tile parameter must not be null.");
        }
        assert (nodeType1 != NodeType.Conductor || tile1.mainTile instanceof IEnergyConductor);
        assert (nodeType1 != NodeType.Sink || tile1.mainTile instanceof IEnergySink);
        assert (nodeType1 != NodeType.Source || tile1.mainTile instanceof IEnergySource);
        this.uid = EnergyNetLocal.getNextNodeUid();
        this.tile = tile1;
        this.nodeType = nodeType1;
        this.parent = null;
    }

    Node(Node node) {
        this.uid = node.uid;
        this.tile = node.tile;
        this.nodeType = node.nodeType;
        this.parent = node;
        assert (this.nodeType != NodeType.Conductor || this.tile.mainTile instanceof IEnergyConductor);
        assert (this.nodeType != NodeType.Sink || this.tile.mainTile instanceof IEnergySink);
        assert (this.nodeType != NodeType.Source || this.tile.mainTile instanceof IEnergySource);
        for (NodeLink link : node.links) {
            assert (link.getNeighbor((Node)node).links.contains(link));
            this.links.add(new NodeLink(link));
        }
    }

    double getInnerLoss() {
        switch (this.nodeType) {
            case Source: {
                return 0.4;
            }
            case Sink: {
                return 0.4;
            }
            case Conductor: {
                return ((IEnergyConductor)this.tile.mainTile).getConductionLoss();
            }
        }
        throw new RuntimeException("invalid nodetype: " + (Object)((Object)this.nodeType));
    }

    boolean isExtraNode() {
        return this.getTop().isExtraNode;
    }

    void setExtraNode(boolean isExtraNode) {
        if (this.nodeType == NodeType.Conductor) {
            throw new IllegalStateException("A conductor can't be an extra node.");
        }
        this.getTop().isExtraNode = isExtraNode;
    }

    int getTier() {
        return this.getTop().tier;
    }

    void setTier(int tier) {
        if (tier < 0 || Double.isNaN(tier)) {
            assert (false);
            if (EnergyNetGlobal.debugGrid) {
                IC2.log.warn(LogCategory.EnergyNet, "Node %s / te %s is using the invalid tier %d.", this, this.tile.mainTile, tier);
            }
            tier = 0;
        } else if (tier > 20 && (tier != Integer.MAX_VALUE || this.nodeType != NodeType.Sink)) {
            if (Util.inDev()) {
                IC2.log.debug(LogCategory.EnergyNet, "Restricting node %s to tier 20, requested %d.", this, tier);
            }
            tier = 20;
        }
        this.getTop().tier = tier;
    }

    double getAmount() {
        return this.getTop().amount;
    }

    void setAmount(double amount) {
        this.getTop().amount = amount;
    }

    double getResistance() {
        return this.getTop().resistance;
    }

    void setResistance(double resistance) {
        this.getTop().resistance = resistance;
    }

    double getVoltage() {
        return this.getTop().voltage;
    }

    void setVoltage(double voltage) {
        this.getTop().voltage = voltage;
    }

    double getMaxCurrent() {
        return this.tile.maxCurrent;
    }

    void resetCurrents() {
        this.getTop().currentIn = 0.0;
        this.getTop().currentOut = 0.0;
    }

    void addCurrent(double current) {
        if (current >= 0.0) {
            this.getTop().currentIn += current;
        } else {
            this.getTop().currentOut += - current;
        }
    }

    public String toString() {
        String type = null;
        switch (this.nodeType) {
            case Conductor: {
                type = "C";
                break;
            }
            case Sink: {
                type = "A";
                break;
            }
            case Source: {
                type = "E";
            }
        }
        return this.tile.mainTile.getClass().getSimpleName().replace("TileEntity", "") + "|" + type + "|" + this.tier + "|" + this.uid;
    }

    Node getTop() {
        if (this.parent != null) {
            return this.parent.getTop();
        }
        return this;
    }

    NodeLink getConnectionTo(Node node) {
        for (NodeLink link : this.links) {
            if (link.getNeighbor(this) != node) continue;
            return link;
        }
        return null;
    }

    NodeStats getStats() {
        return this.lastNodeStats;
    }

    void updateStats() {
        if (EnergyNetLocal.useLinearTransferModel) {
            this.lastNodeStats.set(this.currentIn * this.voltage, this.currentOut * this.voltage, this.voltage);
        } else {
            this.lastNodeStats.set(this.currentIn, this.currentOut, this.voltage);
        }
    }

    Grid getGrid() {
        return this.getTop().grid;
    }

    void setGrid(Grid grid) {
        if (grid == null) {
            throw new NullPointerException("null grid");
        }
        assert (this.getTop().grid == null);
        this.getTop().grid = grid;
    }

    void clearGrid() {
        assert (this.getTop().grid != null);
        this.getTop().grid = null;
    }

}

