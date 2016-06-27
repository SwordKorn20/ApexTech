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
import ic2.core.block.invslot.InvSlotConsumable;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.kineticgenerator.tileentity.TileEntitySteamKineticGenerator;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

public class ContainerSteamKineticGenerator
extends ContainerFullInv<TileEntitySteamKineticGenerator> {
    public ContainerSteamKineticGenerator(EntityPlayer player, TileEntitySteamKineticGenerator te) {
        super(player, te, 166);
        this.addSlotToContainer((Slot)new SlotInvSlot(te.upgradeSlot, 0, 152, 26));
        this.addSlotToContainer((Slot)new SlotInvSlot(te.turbineSlot, 0, 80, 26));
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("distilledWaterTank");
        ret.add("steamTank");
        ret.add("kUoutput");
        ret.add("isTurbineFilledWithWater");
        return ret;
    }
}

