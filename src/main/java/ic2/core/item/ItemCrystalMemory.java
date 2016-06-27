/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 */
package ic2.core.item;

import ic2.core.init.Localization;
import ic2.core.item.ItemIC2;
import ic2.core.ref.ItemName;
import ic2.core.util.StackUtil;
import ic2.core.util.Util;
import ic2.core.uu.UuIndex;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;

public class ItemCrystalMemory
extends ItemIC2 {
    public ItemCrystalMemory() {
        super(ItemName.crystal_memory);
        this.setMaxStackSize(1);
    }

    public boolean isRepairable() {
        return false;
    }

    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        ItemStack recorded = this.readItemStack(stack);
        if (recorded != null) {
            tooltip.add(Localization.translate("ic2.item.CrystalMemory.tooltip.Item") + " " + recorded.getDisplayName());
            tooltip.add(Localization.translate("ic2.item.CrystalMemory.tooltip.UU-Matter") + " " + Util.toSiString(UuIndex.instance.getInBuckets(recorded), 4) + "B");
        } else {
            tooltip.add(Localization.translate("ic2.item.CrystalMemory.tooltip.Empty"));
        }
    }

    public ItemStack readItemStack(ItemStack stack) {
        NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
        NBTTagCompound contentTag = nbt.getCompoundTag("Pattern");
        ItemStack Item2 = ItemStack.loadItemStackFromNBT((NBTTagCompound)contentTag);
        return Item2;
    }

    public void writecontentsTag(ItemStack stack, ItemStack recorded) {
        NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
        NBTTagCompound contentTag = new NBTTagCompound();
        recorded.writeToNBT(contentTag);
        nbt.setTag("Pattern", (NBTBase)contentTag);
    }
}

