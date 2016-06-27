/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.client.gui.FontRenderer
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.renderer.RenderHelper
 *  net.minecraft.client.renderer.RenderItem
 *  net.minecraft.client.renderer.texture.TextureManager
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.client.event.RenderGameOverlayEvent
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$ElementType
 *  net.minecraftforge.fml.common.eventhandler.EventPriority
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  org.lwjgl.opengl.GL11
 */
package ic2.core;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItemManager;
import ic2.api.item.IItemHudInfo;
import ic2.core.IC2;
import ic2.core.item.armor.ItemArmorCFPack;
import ic2.core.item.armor.ItemArmorJetpack;
import ic2.core.ref.IItemModelProvider;
import ic2.core.ref.ItemName;
import ic2.core.util.StackUtil;
import ic2.core.util.Util;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class GuiOverlayer
extends Gui {
    private final Minecraft mc;
    private int chargeproz;
    private static final ResourceLocation background = new ResourceLocation(IC2.textureDomain, "textures/gui/GUIOverlay.png");

    public GuiOverlayer(Minecraft mc) {
        this.mc = mc;
    }

    @SubscribeEvent(priority=EventPriority.NORMAL)
    public void onRenderHotBar(RenderGameOverlayEvent event) {
        if (event.isCancelable() || event.getType() != RenderGameOverlayEvent.ElementType.HOTBAR) {
            return;
        }
        ItemStack boots = this.mc.thePlayer.getItemStackFromSlot(EntityEquipmentSlot.FEET);
        ItemStack legs = this.mc.thePlayer.getItemStackFromSlot(EntityEquipmentSlot.LEGS);
        ItemStack chestplate = this.mc.thePlayer.getItemStackFromSlot(EntityEquipmentSlot.CHEST);
        ItemStack helm = this.mc.thePlayer.getItemStackFromSlot(EntityEquipmentSlot.HEAD);
        if (helm == null) {
            return;
        }
        if (helm.getItem() != ItemName.quantum_helmet.getInstance() && helm.getItem() != ItemName.nano_helmet.getInstance()) {
            return;
        }
        NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(helm);
        Short HudMode = nbtData.getShort("HudMode");
        boolean Nightvision = nbtData.getBoolean("Nightvision");
        if (HudMode > 0) {
            ItemStack item;
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            GL11.glDisable((int)2896);
            RenderItem renderItem = Minecraft.getMinecraft().getRenderItem();
            RenderHelper.enableGUIStandardItemLighting();
            this.mc.getTextureManager().bindTexture(background);
            this.drawTexturedModalRect(0, 0, 0, 0, 71, 69);
            if (helm.getItem() == ItemName.quantum_helmet.getInstance()) {
                this.chargeproz = GuiOverlayer.mapCharge(helm);
                renderItem.renderItemIntoGUI(ItemName.quantum_helmet.getItemStack(), 5, 4);
            }
            if (helm.getItem() == ItemName.nano_helmet.getInstance()) {
                this.chargeproz = GuiOverlayer.mapCharge(helm);
                renderItem.renderItemIntoGUI(ItemName.nano_helmet.getItemStack(), 5, 4);
            }
            this.mc.fontRendererObj.drawString("" + this.chargeproz + "%", 25, 9, 16777215);
            if (Nightvision) {
                renderItem.renderItemIntoGUI(ItemName.nightvision_goggles.getItemStack(), 50, 4);
            }
            if (chestplate != null) {
                NBTTagCompound nbtDatachestplate = StackUtil.getOrCreateNbtData(chestplate);
                boolean jetpack = nbtDatachestplate.getBoolean("jetpack");
                boolean hoverMode = nbtDatachestplate.getBoolean("hoverMode");
                if (chestplate.getItem() == ItemName.quantum_chestplate.getInstance()) {
                    this.chargeproz = GuiOverlayer.mapCharge(chestplate);
                    renderItem.renderItemIntoGUI(ItemName.quantum_chestplate.getItemStack(), 5, 20);
                }
                if (chestplate.getItem() == ItemName.nano_chestplate.getInstance()) {
                    this.chargeproz = GuiOverlayer.mapCharge(chestplate);
                    renderItem.renderItemIntoGUI(ItemName.nano_chestplate.getItemStack(), 5, 20);
                }
                if (chestplate.getItem() == ItemName.jetpack_electric.getInstance()) {
                    this.chargeproz = GuiOverlayer.mapCharge(chestplate);
                    renderItem.renderItemIntoGUI(ItemName.jetpack_electric.getItemStack(), 5, 20);
                }
                if (chestplate.getItem() == ItemName.jetpack.getInstance()) {
                    this.chargeproz = (int)Util.map(((ItemArmorJetpack)chestplate.getItem()).getCharge(chestplate), ((ItemArmorJetpack)chestplate.getItem()).getMaxCharge(chestplate), 100.0);
                    renderItem.renderItemIntoGUI(ItemName.jetpack.getItemStack(), 5, 20);
                }
                if (chestplate.getItem() == ItemName.batpack.getInstance()) {
                    this.chargeproz = GuiOverlayer.mapCharge(chestplate);
                    renderItem.renderItemIntoGUI(ItemName.batpack.getItemStack(), 5, 20);
                }
                if (chestplate.getItem() == ItemName.energy_pack.getInstance()) {
                    this.chargeproz = GuiOverlayer.mapCharge(chestplate);
                    renderItem.renderItemIntoGUI(ItemName.energy_pack.getItemStack(), 5, 20);
                }
                if (chestplate.getItem() == ItemName.cf_pack.getInstance()) {
                    this.chargeproz = (((ItemArmorCFPack)chestplate.getItem()).getMaxDamage(chestplate) - ((ItemArmorCFPack)chestplate.getItem()).getDamage(chestplate)) * 100 / ((ItemArmorCFPack)chestplate.getItem()).getMaxDamage(chestplate);
                    renderItem.renderItemIntoGUI(ItemName.cf_pack.getItemStack(), 5, 20);
                }
                this.mc.fontRendererObj.drawString("" + this.chargeproz + "%", 25, 25, 16777215);
                if (jetpack && !hoverMode) {
                    renderItem.renderItemIntoGUI(ItemName.jetpack.getItemStack(), 50, 20);
                }
                if (jetpack && hoverMode) {
                    renderItem.renderItemIntoGUI(ItemName.jetpack_electric.getItemStack(), 50, 20);
                }
            }
            if (legs != null) {
                if (legs.getItem() == ItemName.quantum_leggings.getInstance()) {
                    this.chargeproz = GuiOverlayer.mapCharge(legs);
                    renderItem.renderItemIntoGUI(ItemName.quantum_leggings.getItemStack(), 5, 36);
                }
                if (legs.getItem() == ItemName.nano_leggings.getInstance()) {
                    this.chargeproz = GuiOverlayer.mapCharge(legs);
                    renderItem.renderItemIntoGUI(ItemName.nano_leggings.getItemStack(), 5, 36);
                }
                this.mc.fontRendererObj.drawString("" + this.chargeproz + "%", 25, 41, 16777215);
            }
            if (boots != null) {
                if (boots.getItem() == ItemName.quantum_boots.getInstance()) {
                    this.chargeproz = GuiOverlayer.mapCharge(boots);
                    renderItem.renderItemIntoGUI(ItemName.quantum_boots.getItemStack(), 5, 52);
                }
                if (boots.getItem() == ItemName.nano_boots.getInstance()) {
                    this.chargeproz = GuiOverlayer.mapCharge(boots);
                    renderItem.renderItemIntoGUI(ItemName.nano_boots.getItemStack(), 5, 52);
                }
                this.mc.fontRendererObj.drawString("" + this.chargeproz + "%", 25, 56, 16777215);
            }
            if (HudMode == 2 && (item = this.mc.thePlayer.getActiveItemStack()) != null) {
                int l;
                renderItem.renderItemIntoGUI(item, 5, 74);
                this.mc.fontRendererObj.drawString(item.getDisplayName(), 30, 78, 16777215);
                LinkedList<String> info = new LinkedList<String>();
                if (item.getItem() instanceof IItemHudInfo) {
                    info.addAll(((IItemHudInfo)item.getItem()).getHudInfo(item));
                    if (info.size() > 0) {
                        for (l = 0; l <= info.size() - 1; ++l) {
                            this.mc.fontRendererObj.drawString(((String)info.get(l)).toString(), 8, 83 + (l + 1) * 14, 16777215);
                        }
                    }
                } else {
                    info.addAll(item.getTooltip((EntityPlayer)this.mc.thePlayer, true));
                    if (info.size() > 1) {
                        for (l = 1; l <= info.size() - 1; ++l) {
                            this.mc.fontRendererObj.drawString(((String)info.get(l)).toString(), 8, 83 + l * 14, 16777215);
                        }
                    }
                }
            }
        }
        RenderHelper.disableStandardItemLighting();
    }

    private static final int mapCharge(ItemStack stack) {
        double charge = ElectricItem.manager.getCharge(stack);
        double maxCharge = charge + ElectricItem.manager.charge(stack, Double.POSITIVE_INFINITY, Integer.MAX_VALUE, true, true);
        return (int)Util.map(charge, maxCharge, 100.0);
    }
}

