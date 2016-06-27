/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.item.ItemArmor$ArmorMaterial
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.fml.common.registry.GameRegistry
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.item.armor;

import ic2.api.item.IMetalArmor;
import ic2.core.CreativeTabIC2;
import ic2.core.IC2;
import ic2.core.init.InternalName;
import ic2.core.init.Localization;
import ic2.core.item.ItemIC2;
import ic2.core.ref.IItemModelProvider;
import ic2.core.ref.ItemName;
import ic2.core.util.Util;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemArmorIC2
extends ItemArmor
implements IItemModelProvider,
IMetalArmor {
    private final String armorName;
    private final Object repairMaterial;

    public ItemArmorIC2(ItemName name, ItemArmor.ArmorMaterial armorMaterial, InternalName armorName, EntityEquipmentSlot armorType, Object repairMaterial) {
        super(armorMaterial, -1, armorType);
        this.repairMaterial = repairMaterial;
        this.armorName = armorName.name();
        this.setMaxDamage(armorMaterial.getDurability(armorType));
        this.setUnlocalizedName(name.name());
        this.setCreativeTab((CreativeTabs)IC2.tabIC2);
        GameRegistry.registerItem((Item)this, (String)name.name());
        name.setInstance(this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public void registerModels(ItemName name) {
        ModelLoader.setCustomModelResourceLocation((Item)this, (int)0, (ModelResourceLocation)ItemIC2.getModelLocation(name, null));
    }

    public int getMetadata(ItemStack stack) {
        return 0;
    }

    public String getArmorTexture(ItemStack stack, Entity entity, EntityEquipmentSlot slot, String type) {
        String suffix1 = this.armorType == EntityEquipmentSlot.LEGS ? "2" : "1";
        String suffix2 = type != null && this.hasOverlayTexture() ? "_overlay" : "";
        return IC2.textureDomain + ":textures/armor/" + this.armorName + "_" + suffix1 + suffix2 + ".png";
    }

    protected boolean hasOverlayTexture() {
        return false;
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

    @Override
    public boolean isMetalArmor(ItemStack itemstack, EntityPlayer player) {
        return true;
    }

    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        return repair != null && Util.matchesOD(repair, this.repairMaterial);
    }
}

