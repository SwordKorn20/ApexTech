/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumHand
 *  net.minecraft.world.World
 */
package ic2.core.item.tool;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItemManager;
import ic2.core.IC2;
import ic2.core.Platform;
import ic2.core.WindSim;
import ic2.core.WorldData;
import ic2.core.init.Localization;
import ic2.core.item.tool.ItemElectricTool;
import ic2.core.ref.ItemName;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemWindmeter
extends ItemElectricTool {
    public ItemWindmeter() {
        super(ItemName.wind_meter, 50);
        this.setMaxStackSize(1);
        this.maxCharge = 10000;
        this.transferLimit = 100;
        this.tier = 1;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer, EnumHand hand) {
        if (!IC2.platform.isSimulating()) {
            return new ActionResult(EnumActionResult.PASS, (Object)itemstack);
        }
        if (!ElectricItem.manager.canUse(itemstack, this.operationEnergyCost)) {
            return new ActionResult(EnumActionResult.PASS, (Object)itemstack);
        }
        ElectricItem.manager.use(itemstack, this.operationEnergyCost, (EntityLivingBase)entityplayer);
        double windStrength = WorldData.get((World)world).windSim.getWindAt(entityplayer.posY);
        if (windStrength < 0.0) {
            windStrength = 0.0;
        }
        IC2.platform.messagePlayer(entityplayer, Localization.translate("ic2.itemwindmeter.info", Float.valueOf((float)Math.round(windStrength * 100.0) / 100.0f)), new Object[0]);
        return new ActionResult(EnumActionResult.SUCCESS, (Object)itemstack);
    }
}

