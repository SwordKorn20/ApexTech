/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 */
package ic2.core.item.resources;

import ic2.api.item.IKineticRotor;
import ic2.core.block.kineticgenerator.gui.GuiWaterKineticGenerator;
import ic2.core.block.kineticgenerator.gui.GuiWindKineticGenerator;
import ic2.core.init.Localization;
import ic2.core.item.ItemGradualInt;
import ic2.core.ref.ItemName;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

public class ItemWindRotor
extends ItemGradualInt
implements IKineticRotor {
    private final int maxWindStrength;
    private final int minWindStrength;
    private final int radius;
    private final float efficiency;
    private final ResourceLocation renderTexture;
    private final boolean water;

    public ItemWindRotor(ItemName name, int Radius, int durability, float efficiency, int minWindStrength, int maxWindStrength, ResourceLocation RenderTexture) {
        super(name, durability);
        this.setMaxStackSize(1);
        this.setMaxDamage(durability);
        this.radius = Radius;
        this.efficiency = efficiency;
        this.renderTexture = RenderTexture;
        this.minWindStrength = minWindStrength;
        this.maxWindStrength = maxWindStrength;
        this.water = name != ItemName.rotor_wood;
    }

    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        tooltip.add(Localization.translate("ic2.itemrotor.wind.info", this.minWindStrength, this.maxWindStrength));
        IKineticRotor.GearboxType type = null;
        if (Minecraft.getMinecraft().currentScreen != null && Minecraft.getMinecraft().currentScreen instanceof GuiWaterKineticGenerator) {
            type = IKineticRotor.GearboxType.WATER;
        } else if (Minecraft.getMinecraft().currentScreen != null && Minecraft.getMinecraft().currentScreen instanceof GuiWindKineticGenerator) {
            type = IKineticRotor.GearboxType.WIND;
        }
        if (type != null) {
            tooltip.add(Localization.translate("ic2.itemrotor.fitsin." + this.isAcceptedType(stack, type)));
        }
    }

    @Override
    public int getDiameter(ItemStack stack) {
        return this.radius;
    }

    @Override
    public ResourceLocation getRotorRenderTexture(ItemStack stack) {
        return this.renderTexture;
    }

    @Override
    public float getEfficiency(ItemStack stack) {
        return this.efficiency;
    }

    @Override
    public int getMinWindStrength(ItemStack stack) {
        return this.minWindStrength;
    }

    @Override
    public int getMaxWindStrength(ItemStack stack) {
        return this.maxWindStrength;
    }

    @Override
    public boolean isAcceptedType(ItemStack stack, IKineticRotor.GearboxType type) {
        return type == IKineticRotor.GearboxType.WIND || this.water;
    }
}

