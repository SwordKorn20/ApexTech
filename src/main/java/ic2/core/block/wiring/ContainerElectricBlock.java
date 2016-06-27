/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.inventory.Slot
 */
package ic2.core.block.wiring;

import ic2.core.ContainerFullInv;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotCharge;
import ic2.core.block.invslot.InvSlotDischarge;
import ic2.core.block.wiring.TileEntityElectricBlock;
import ic2.core.slot.ArmorSlot;
import ic2.core.slot.SlotArmor;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;

public class ContainerElectricBlock
extends ContainerFullInv<TileEntityElectricBlock> {
    public ContainerElectricBlock(EntityPlayer player, TileEntityElectricBlock tileEntity1) {
        super(player, tileEntity1, 196);
        for (int col = 0; col < ArmorSlot.getCount(); ++col) {
            this.addSlotToContainer((Slot)new SlotArmor(player.inventory, ArmorSlot.get(col), 8 + col * 18, 84));
        }
        this.addSlotToContainer((Slot)new SlotInvSlot(tileEntity1.chargeSlot, 0, 56, 17));
        this.addSlotToContainer((Slot)new SlotInvSlot(tileEntity1.dischargeSlot, 0, 56, 53));
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("redstoneMode");
        return ret;
    }
}

