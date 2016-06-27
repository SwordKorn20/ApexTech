/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.Vec3i
 */
package ic2.core.energy;

import ic2.api.energy.EnergyNet;
import ic2.api.energy.IEnergyNet;
import ic2.api.energy.tile.IEnergyTile;
import ic2.core.energy.Node;
import ic2.core.energy.Tile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

class NodeLink {
    Node nodeA;
    Node nodeB;
    EnumFacing dirFromA;
    EnumFacing dirFromB;
    double loss;
    List<Node> skippedNodes = new ArrayList<Node>();

    NodeLink(Node nodeA1, Node nodeB1, double loss1) {
        this(nodeA1, nodeB1, loss1, null, null);
        this.calculateDirections();
    }

    NodeLink(NodeLink link) {
        this(link.nodeA, link.nodeB, link.loss, link.dirFromA, link.dirFromB);
        this.skippedNodes.addAll(link.skippedNodes);
    }

    private NodeLink(Node nodeA1, Node nodeB1, double loss1, EnumFacing dirFromA, EnumFacing dirFromB) {
        assert (nodeA1 != nodeB1);
        this.nodeA = nodeA1;
        this.nodeB = nodeB1;
        this.loss = loss1;
        this.dirFromA = dirFromA;
        this.dirFromB = dirFromB;
    }

    Node getNeighbor(Node node) {
        if (this.nodeA == node) {
            return this.nodeB;
        }
        return this.nodeA;
    }

    Node getNeighbor(int uid) {
        if (this.nodeA.uid == uid) {
            return this.nodeB;
        }
        return this.nodeA;
    }

    void replaceNode(Node oldNode, Node newNode) {
        if (this.nodeA == oldNode) {
            this.nodeA = newNode;
        } else if (this.nodeB == oldNode) {
            this.nodeB = newNode;
        } else {
            throw new IllegalArgumentException("Node " + oldNode + " isn't in " + this + ".");
        }
    }

    EnumFacing getDirFrom(Node node) {
        if (this.nodeA == node) {
            return this.dirFromA;
        }
        if (this.nodeB == node) {
            return this.dirFromB;
        }
        return null;
    }

    void updateCurrent() {
        assert (!Double.isNaN(this.nodeA.getVoltage()));
        assert (!Double.isNaN(this.nodeB.getVoltage()));
        double currentAB = (this.nodeA.getVoltage() - this.nodeB.getVoltage()) / this.loss;
        this.nodeA.addCurrent(- currentAB);
        this.nodeB.addCurrent(currentAB);
    }

    public String toString() {
        return "NodeLink:" + this.nodeA + "@" + (Object)this.dirFromA + "->" + this.nodeB + "@" + (Object)this.dirFromB;
    }

    private void calculateDirections() {
        for (IEnergyTile posA : this.nodeA.tile.subTiles) {
            for (IEnergyTile posB : this.nodeB.tile.subTiles) {
                BlockPos delta = EnergyNet.instance.getPos(posA).subtract((Vec3i)EnergyNet.instance.getPos(posB));
                for (EnumFacing dir : EnumFacing.VALUES) {
                    if (dir.getFrontOffsetX() != delta.getX() || dir.getFrontOffsetY() != delta.getY() || dir.getFrontOffsetZ() != delta.getZ()) continue;
                    this.dirFromA = dir;
                    this.dirFromB = dir.getOpposite();
                    return;
                }
            }
        }
        assert (false);
        this.dirFromA = null;
        this.dirFromB = null;
    }
}

