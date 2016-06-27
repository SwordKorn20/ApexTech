/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.Slot
 */
package ic2.core.block.reactor.container;

import ic2.core.ContainerBase;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableLiquidByList;
import ic2.core.block.invslot.InvSlotConsumableLiquidByTank;
import ic2.core.block.invslot.InvSlotOutput;
import ic2.core.block.invslot.InvSlotReactor;
import ic2.core.block.reactor.tileentity.TileEntityNuclearReactorElectric;
import ic2.core.slot.SlotInvSlot;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

public class ContainerNuclearReactor
extends ContainerBase<TileEntityNuclearReactorElectric> {
    private final int size;

    public ContainerNuclearReactor(EntityPlayer player, TileEntityNuclearReactorElectric te) {
        super(te);
        this.size = te.getReactorSize();
        int startX = 26;
        int startY = 25;
        int slotCount = te.reactorSlot.size();
        for (int i = 0; i < slotCount; ++i) {
            int x = i % this.size;
            int y = i / this.size;
            this.addSlotToContainer((Slot)new SlotInvSlot(te.reactorSlot, i, startX + 18 * x, startY + 18 * y));
        }
        this.addPlayerInventorySlots(player, 212, 243);
        this.addSlotToContainer((Slot)new SlotInvSlot(te.coolantinputSlot, 0, 8, 25));
        this.addSlotToContainer((Slot)new SlotInvSlot(te.hotcoolinputSlot, 0, 188, 25));
        this.addSlotToContainer((Slot)new SlotInvSlot(te.coolantoutputSlot, 0, 8, 115));
        this.addSlotToContainer((Slot)new SlotInvSlot(te.hotcoolantoutputSlot, 0, 188, 115));
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("heat");
        ret.add("maxHeat");
        ret.add("EmitHeat");
        ret.add("inputTank");
        ret.add("outputTank");
        ret.add("fluidCooled");
        return ret;
    }
}

