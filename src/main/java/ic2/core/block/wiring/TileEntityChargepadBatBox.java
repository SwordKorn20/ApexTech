/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.item.ItemStack
 */
package ic2.core.block.wiring;

import ic2.core.block.wiring.TileEntityChargepadBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;

public class TileEntityChargepadBatBox
extends TileEntityChargepadBlock {
    public TileEntityChargepadBatBox() {
        super(1, 32, 40000);
    }

    @Override
    protected void getItems(EntityPlayer player) {
        if (player != null) {
            for (ItemStack current2 : player.inventory.armorInventory) {
                if (current2 == null) continue;
                this.chargeItem(current2, 32);
            }
            for (ItemStack current2 : player.inventory.mainInventory) {
                if (current2 == null) continue;
                this.chargeItem(current2, 32);
            }
        }
    }
}

