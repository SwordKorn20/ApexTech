/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.ItemMeshDefinition
 *  net.minecraft.client.renderer.block.model.ModelBakery
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.inventory.Container
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.world.World
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.item;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItemManager;
import ic2.core.item.BaseElectricItem;
import ic2.core.item.ItemIC2;
import ic2.core.ref.ItemName;
import ic2.core.util.Util;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBattery
extends BaseElectricItem {
    private static int maxLevel = 4;

    public ItemBattery(ItemName name, double maxCharge, double transferLimit, int tier) {
        super(name, maxCharge, transferLimit, tier);
        this.setMaxStackSize(16);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public void registerModels(final ItemName name) {
        ModelLoader.setCustomMeshDefinition((Item)this, (ItemMeshDefinition)new ItemMeshDefinition(){

            public ModelResourceLocation getModelLocation(ItemStack stack) {
                int damage = stack.getItemDamage();
                int maxDamage = stack.getMaxDamage() - 1;
                int level = maxDamage > 0 ? Util.limit((damage * maxLevel + maxDamage / 2) / maxDamage, 0, maxLevel) : 0;
                return ItemIC2.getModelLocation(name, Integer.toString(maxLevel - level));
            }
        });
        for (int level = 0; level <= maxLevel; ++level) {
            ModelBakery.registerItemVariants((Item)this, (ResourceLocation[])new ResourceLocation[]{ItemBattery.getModelLocation(name, Integer.toString(level))});
        }
    }

    @Override
    public boolean canProvideEnergy(ItemStack stack) {
        return true;
    }

    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if (world.isRemote || stack.stackSize != 1) {
            return new ActionResult(EnumActionResult.PASS, (Object)stack);
        }
        if (ElectricItem.manager.getCharge(stack) > 0.0) {
            boolean transferred = false;
            for (int i = 0; i < 9; ++i) {
                double transfer;
                ItemStack target = player.inventory.mainInventory[i];
                if (target == null || ElectricItem.manager.discharge(target, Double.POSITIVE_INFINITY, Integer.MAX_VALUE, true, true, true) > 0.0 || (transfer = ElectricItem.manager.discharge(stack, 2.0 * this.transferLimit, Integer.MAX_VALUE, true, true, true)) <= 0.0 || (transfer = ElectricItem.manager.charge(stack, transfer, this.tier, true, false)) <= 0.0) continue;
                ElectricItem.manager.discharge(stack, transfer, Integer.MAX_VALUE, true, true, false);
                transferred = true;
            }
            if (transferred && !world.isRemote) {
                player.openContainer.detectAndSendChanges();
            }
        }
        return new ActionResult(EnumActionResult.SUCCESS, (Object)stack);
    }

}

