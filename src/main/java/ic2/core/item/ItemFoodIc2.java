/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemFood
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.fml.common.registry.GameRegistry
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.item;

import ic2.core.CreativeTabIC2;
import ic2.core.IC2;
import ic2.core.init.Localization;
import ic2.core.item.ItemIC2;
import ic2.core.ref.IItemModelProvider;
import ic2.core.ref.ItemName;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemFoodIc2
extends ItemFood
implements IItemModelProvider {
    protected ItemFoodIc2(ItemName name, int amount, float saturation, boolean isWolfFood) {
        super(amount, saturation, isWolfFood);
        this.setUnlocalizedName(name.name());
        this.setCreativeTab((CreativeTabs)IC2.tabIC2);
        GameRegistry.registerItem((Item)this, (String)name.name());
        name.setInstance(this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public void registerModels(ItemName name) {
        ItemIC2.registerModel((Item)this, 0, name, null);
    }

    public String getUnlocalizedName(ItemStack stack) {
        return this.getUnlocalizedName();
    }

    public String getItemStackDisplayName(ItemStack stack) {
        return Localization.translate(this.getUnlocalizedName(stack));
    }
}

