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
import ic2.core.block.invslot.InvSlotConsumableLiquidByList;
import ic2.core.block.invslot.InvSlotConsumableLiquidByTank;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.tileentity.TileEntitySolarDestiller;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

public class ContainerSolarDestiller
extends ContainerFullInv<TileEntitySolarDestiller> {
    public ContainerSolarDestiller(EntityPlayer player, TileEntitySolarDestiller tileEntite) {
        super(player, tileEntite, 184);
        this.addSlotToContainer((Slot)new SlotInvSlot(tileEntite.waterinputSlot, 0, 17, 27));
        this.addSlotToContainer((Slot)new SlotInvSlot(tileEntite.destiwaterinputSlot, 0, 136, 64));
        this.addSlotToContainer((Slot)new SlotInvSlot(tileEntite.wateroutputSlot, 0, 17, 45));
        this.addSlotToContainer((Slot)new SlotInvSlot(tileEntite.destiwateroutputSlott, 0, 136, 82));
        for (int i = 0; i < 2; ++i) {
            this.addSlotToContainer((Slot)new SlotInvSlot(tileEntite.upgradeSlot, i, 152, 8 + i * 18));
        }
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("skyLight");
        ret.add("inputTank");
        ret.add("outputTank");
        return ret;
    }
}

