/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.text.TextFormatting
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.item;

import ic2.api.item.ElectricItem;
import ic2.api.item.IBoxable;
import ic2.api.item.IElectricItem;
import ic2.api.item.IElectricItemManager;
import ic2.core.IC2;
import ic2.core.Platform;
import ic2.core.init.Localization;
import ic2.core.item.ItemBattery;
import ic2.core.item.tool.Guitoolbox;
import ic2.core.ref.ItemName;
import ic2.core.util.StackUtil;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBatteryChargeHotbar
extends ItemBattery
implements IBoxable {
    public ItemBatteryChargeHotbar(ItemName name, double maxCharge, double transferLimit, int tier) {
        super(name, maxCharge, transferLimit, tier);
    }

    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean advanced) {
        super.addInformation(stack, player, list, advanced);
        Mode mode = this.getMode(stack);
        list.add(this.getNameOfMode(mode));
        if (Minecraft.getMinecraft().currentScreen != null && Minecraft.getMinecraft().currentScreen instanceof Guitoolbox) {
            list.add((Object)(mode.enabled ? TextFormatting.RED : TextFormatting.GREEN) + Localization.translate("ic2.tooltip.mode.boxable"));
        }
    }

    public void onUpdate(ItemStack stack, World world, Entity entity, int itemSlot, boolean isSelected) {
        Mode mode = this.getMode(stack);
        if (entity instanceof EntityPlayerMP && world.getTotalWorldTime() % 10 < (long)this.getTier(stack) && mode.enabled) {
            EntityPlayer thePlayer = (EntityPlayer)entity;
            ItemStack[] inventory = thePlayer.inventory.mainInventory;
            double limit = this.getTransferLimit(stack);
            int tier = this.getTier(stack);
            for (int i = 0; i < 9; ++i) {
                ItemStack toCharge = inventory[i];
                if (toCharge == null || !(toCharge.getItem() instanceof IElectricItem) || toCharge.getItem() instanceof ItemBatteryChargeHotbar || mode == Mode.NOT_IN_HAND && i == thePlayer.inventory.currentItem) continue;
                double charge = ElectricItem.manager.charge(toCharge, limit, tier, false, true);
                charge = ElectricItem.manager.discharge(stack, charge, tier, true, false, false);
                ElectricItem.manager.charge(toCharge, charge, tier, true, false);
                if ((limit -= charge) <= 0.0) break;
            }
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if (world.isRemote) {
            return new ActionResult(EnumActionResult.PASS, (Object)stack);
        }
        Mode mode = this.getMode(stack);
        mode = Mode.values[(mode.ordinal() + 1) % Mode.values.length];
        this.setMode(stack, mode);
        IC2.platform.messagePlayer(player, Localization.translate("ic2.tooltip.mode", this.getNameOfMode(mode)), new Object[0]);
        return new ActionResult(EnumActionResult.SUCCESS, (Object)stack);
    }

    private String getNameOfMode(Mode mode) {
        return Localization.translate("ic2.tooltip.mode." + mode.toString().toLowerCase());
    }

    public void setMode(ItemStack stack, Mode mode) {
        NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
        nbt.setByte("mode", (byte)mode.ordinal());
    }

    public Mode getMode(ItemStack stack) {
        NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
        if (!nbt.hasKey("mode")) {
            return Mode.ENABLED;
        }
        return this.getMode(nbt.getByte("mode"));
    }

    private Mode getMode(int mode) {
        if (mode < 0 || mode >= Mode.values.length) {
            mode = 0;
        }
        return Mode.values[mode];
    }

    @Override
    public boolean canBeStoredInToolbox(ItemStack itemstack) {
        return this.getMode(itemstack) == Mode.DISABLED;
    }

    private static enum Mode {
        ENABLED(true),
        DISABLED(false),
        NOT_IN_HAND(true);
        
        private boolean enabled;
        public static final Mode[] values;

        private Mode(boolean enabled) {
            this.enabled = enabled;
        }

        static {
            values = Mode.values();
        }
    }

}

