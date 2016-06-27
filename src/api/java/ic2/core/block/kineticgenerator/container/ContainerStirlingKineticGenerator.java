/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.Slot
 */
package ic2.core.block.kineticgenerator.container;

import ic2.core.ContainerFullInv;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableLiquidByManager;
import ic2.core.block.invslot.InvSlotConsumableLiquidByTank;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.kineticgenerator.tileentity.TileEntityStirlingKineticGenerator;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

public class ContainerStirlingKineticGenerator
extends ContainerFullInv<TileEntityStirlingKineticGenerator> {
    public ContainerStirlingKineticGenerator(EntityPlayer player, TileEntityStirlingKineticGenerator te) {
        super(player, te, 204);
        this.addSlotToContainer((Slot)new SlotInvSlot(te.coolfluidinputSlot, 0, 8, 103));
        this.addSlotToContainer((Slot)new SlotInvSlot(te.cooloutputSlot, 0, 26, 103));
        this.addSlotToContainer((Slot)new SlotInvSlot(te.hotfluidinputSlot, 0, 134, 103));
        this.addSlotToContainer((Slot)new SlotInvSlot(te.hotoutputSlot, 0, 152, 103));
        for (int i = 0; i < 3; ++i) {
            this.addSlotToContainer((Slot)new SlotInvSlot(te.upgradeSlot, i, 62 + i * 18, 103));
        }
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("inputTank");
        ret.add("outputTank");
        return ret;
    }
}

