/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 */
package ic2.core.item.tool;

import ic2.api.item.IBoxable;
import ic2.api.item.IItemHudInfo;
import ic2.core.IC2;
import ic2.core.init.Localization;
import ic2.core.item.ItemIC2;
import ic2.core.ref.ItemName;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class ItemToolHammer
extends ItemIC2
implements IItemHudInfo,
IBoxable {
    public ItemToolHammer() {
        super(ItemName.forge_hammer);
        this.setMaxDamage(79);
        this.setMaxStackSize(1);
        this.canRepair = false;
    }

    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        tooltip.add(Localization.translate("ic2.item.ItemTool.tooltip.UsesLeft") + " " + ItemToolHammer.getRemainingUses(stack));
    }

    @Override
    public boolean canBeStoredInToolbox(ItemStack itemstack) {
        return true;
    }

    @Override
    public List<String> getHudInfo(ItemStack stack) {
        LinkedList<String> info = new LinkedList<String>();
        info.add("Uses left: " + ItemToolHammer.getRemainingUses(stack));
        return info;
    }

    public boolean hasContainerItem(ItemStack stack) {
        return true;
    }

    public ItemStack getContainerItem(ItemStack stack) {
        ItemStack ret = stack.copy();
        ret.attemptDamageItem(1, IC2.random);
        return ret;
    }
}

