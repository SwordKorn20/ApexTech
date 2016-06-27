/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.MobEffects
 *  net.minecraft.inventory.Container
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.item.armor;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItem;
import ic2.api.item.IElectricItemManager;
import ic2.api.item.IItemHudInfo;
import ic2.core.IC2;
import ic2.core.Platform;
import ic2.core.init.InternalName;
import ic2.core.item.ElectricItemManager;
import ic2.core.item.armor.ItemArmorUtility;
import ic2.core.ref.ItemName;
import ic2.core.util.Keyboard;
import ic2.core.util.StackUtil;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemArmorNightvisionGoggles
extends ItemArmorUtility
implements IElectricItem,
IItemHudInfo {
    public ItemArmorNightvisionGoggles() {
        super(ItemName.nightvision_goggles, InternalName.nightvision, EntityEquipmentSlot.HEAD);
        this.setMaxDamage(27);
        this.setNoRepair();
    }

    @Override
    public boolean canProvideEnergy(ItemStack stack) {
        return false;
    }

    @Override
    public double getMaxCharge(ItemStack stack) {
        return 200000.0;
    }

    @Override
    public int getTier(ItemStack stack) {
        return 1;
    }

    @Override
    public double getTransferLimit(ItemStack stack) {
        return 200.0;
    }

    @Override
    public List<String> getHudInfo(ItemStack stack) {
        LinkedList<String> info = new LinkedList<String>();
        info.add(ElectricItem.manager.getToolTip(stack));
        return info;
    }

    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
        NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(stack);
        boolean active = nbtData.getBoolean("active");
        byte toggleTimer = nbtData.getByte("toggleTimer");
        if (IC2.keyboard.isAltKeyDown(player) && IC2.keyboard.isModeSwitchKeyDown(player) && toggleTimer == 0) {
            toggleTimer = 10;
            boolean bl = active = !active;
            if (IC2.platform.isSimulating()) {
                nbtData.setBoolean("active", active);
                if (active) {
                    IC2.platform.messagePlayer(player, "Nightvision enabled.", new Object[0]);
                } else {
                    IC2.platform.messagePlayer(player, "Nightvision disabled.", new Object[0]);
                }
            }
        }
        if (IC2.platform.isSimulating() && toggleTimer > 0) {
            toggleTimer = (byte)(toggleTimer - 1);
            nbtData.setByte("toggleTimer", toggleTimer);
        }
        boolean ret = false;
        if (active && IC2.platform.isSimulating() && ElectricItem.manager.use(stack, 1.0, (EntityLivingBase)player)) {
            BlockPos pos = new BlockPos(MathHelper.floor_double((double)player.posX), MathHelper.floor_double((double)player.posZ), MathHelper.floor_double((double)player.posY));
            int skylight = player.worldObj.getLightFromNeighbors(pos);
            if (skylight > 8) {
                IC2.platform.removePotion((EntityLivingBase)player, MobEffects.NIGHT_VISION);
                player.addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 100, 0, true, true));
            } else {
                IC2.platform.removePotion((EntityLivingBase)player, MobEffects.BLINDNESS);
                player.addPotionEffect(new PotionEffect(MobEffects.NIGHT_VISION, 300, 0, true, true));
            }
            ret = true;
        }
        if (ret) {
            player.inventoryContainer.detectAndSendChanges();
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> subItems) {
        ElectricItemManager.addChargeVariants(item, subItems);
    }

    @Override
    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack) {
        return false;
    }
}

