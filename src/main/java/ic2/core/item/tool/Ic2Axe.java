/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.item.Item
 *  net.minecraft.item.Item$ToolMaterial
 *  net.minecraft.item.ItemAxe
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.fml.common.registry.GameRegistry
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.item.tool;

import ic2.core.CreativeTabIC2;
import ic2.core.IC2;
import ic2.core.init.Localization;
import ic2.core.item.ItemIC2;
import ic2.core.ref.IItemModelProvider;
import ic2.core.ref.ItemName;
import ic2.core.util.Util;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class Ic2Axe
extends ItemAxe
implements IItemModelProvider {
    private final Object repairMaterial;

    public Ic2Axe() {
        super(Item.ToolMaterial.IRON);
        this.efficiencyOnProperMaterial = 5.0f;
        this.repairMaterial = "ingotBronze";
        this.setUnlocalizedName(ItemName.bronze_axe.name());
        this.setCreativeTab((CreativeTabs)IC2.tabIC2);
        GameRegistry.registerItem((Item)this, (String)ItemName.bronze_axe.name());
        ItemName.bronze_axe.setInstance(this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public void registerModels(ItemName name) {
        ItemIC2.registerModel((Item)this, 0, name, null);
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

    public int getItemEnchantability() {
        return 13;
    }

    public boolean getIsRepairable(ItemStack stack1, ItemStack stack2) {
        return stack2 != null && Util.matchesOD(stack2, this.repairMaterial);
    }
}

