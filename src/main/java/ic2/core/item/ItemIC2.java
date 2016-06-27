/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.fml.common.registry.GameRegistry
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.item;

import ic2.core.IC2;
import ic2.core.init.Localization;
import ic2.core.ref.IItemModelProvider;
import ic2.core.ref.ItemName;
import ic2.core.util.StackUtil;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemIC2
extends Item
implements IItemModelProvider {
    private EnumRarity rarity = EnumRarity.COMMON;

    public ItemIC2(ItemName name) {
        this.setCreativeTab((CreativeTabs)IC2.tabIC2);
        if (name != null) {
            this.setUnlocalizedName(name.name());
            GameRegistry.registerItem((Item)this, (String)name.name());
            name.setInstance(this);
        }
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public void registerModels(ItemName name) {
        this.registerModel(0, name, null);
    }

    @SideOnly(value=Side.CLIENT)
    protected void registerModel(int meta, ItemName name, String extraName) {
        ItemIC2.registerModel(this, meta, name, extraName);
    }

    @SideOnly(value=Side.CLIENT)
    public static void registerModel(Item item, int meta, ItemName name, String extraName) {
        ModelLoader.setCustomModelResourceLocation((Item)item, (int)meta, (ModelResourceLocation)ItemIC2.getModelLocation(name, extraName));
    }

    @SideOnly(value=Side.CLIENT)
    public static ModelResourceLocation getModelLocation(ItemName name, String extraName) {
        StringBuilder loc = new StringBuilder();
        loc.append("ic2");
        loc.append(':');
        loc.append(name.getPath(extraName));
        return new ModelResourceLocation(loc.toString(), null);
    }

    public String getUnlocalizedName() {
        return "ic2." + super.getUnlocalizedName().substring(5);
    }

    public String getUnlocalizedName(ItemStack stack) {
        return this.getUnlocalizedName();
    }

    public String getItemStackDisplayName(ItemStack stack) {
        return Localization.translate(this.getUnlocalizedName(stack));
    }

    public ItemIC2 setRarity(EnumRarity rarity) {
        if (rarity == null) {
            throw new NullPointerException("null rarity");
        }
        this.rarity = rarity;
        return this;
    }

    public EnumRarity getRarity(ItemStack stack) {
        if (stack.isItemEnchanted() && this.rarity != EnumRarity.EPIC) {
            return EnumRarity.RARE;
        }
        return this.rarity;
    }

    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return ItemIC2.shouldReequip(oldStack, newStack, slotChanged);
    }

    public static boolean shouldReequip(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        if (!StackUtil.checkItemEquality(newStack, oldStack)) {
            return true;
        }
        if (oldStack == null) {
            return false;
        }
        if (oldStack.stackSize != newStack.stackSize) {
            return true;
        }
        return slotChanged && StackUtil.checkItemEqualityStrict(oldStack, newStack);
    }

    protected static int getRemainingUses(ItemStack stack) {
        return stack.getMaxDamage() - stack.getItemDamage() + 1;
    }
}

