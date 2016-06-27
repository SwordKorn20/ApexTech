/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.Slot
 */
package ic2.core.block.machine.container;

import ic2.core.ContainerFullInv;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumable;
import ic2.core.block.invslot.InvSlotConsumableLiquid;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.tileentity.TileEntityLiquidHeatExchanger;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

public class ContainerLiquidHeatExchanger
extends ContainerFullInv<TileEntityLiquidHeatExchanger> {
    public ContainerLiquidHeatExchanger(EntityPlayer player, TileEntityLiquidHeatExchanger tileEntite) {
        int i;
        super(player, tileEntite, 204);
        this.addSlotToContainer((Slot)new SlotInvSlot(tileEntite.hotfluidinputSlot, 0, 8, 103));
        this.addSlotToContainer((Slot)new SlotInvSlot(tileEntite.cooloutputSlot, 0, 152, 103));
        this.addSlotToContainer((Slot)new SlotInvSlot(tileEntite.coolfluidinputSlot, 0, 134, 103));
        this.addSlotToContainer((Slot)new SlotInvSlot(tileEntite.hotoutputSlot, 0, 26, 103));
        for (i = 0; i < 3; ++i) {
            this.addSlotToContainer((Slot)new SlotInvSlot(tileEntite.upgradeSlot, i, 62 + i * 18, 103));
        }
        for (i = 0; i < 5; ++i) {
            this.addSlotToContainer((Slot)new SlotInvSlot(tileEntite.heatexchangerslots, i, 46 + i * 17, 50));
        }
        for (i = 5; i < 10; ++i) {
            this.addSlotToContainer((Slot)new SlotInvSlot(tileEntite.heatexchangerslots, i, 46 + (i - 5) * 17, 72));
        }
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("inputTank");
        ret.add("outputTank");
        ret.add("transmitHeat");
        ret.add("maxHeatEmitpeerTick");
        return ret;
    }
}

