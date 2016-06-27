/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumHand
 *  net.minecraft.world.World
 */
package ic2.core.item.tool;

import ic2.core.IC2;
import ic2.core.Platform;
import ic2.core.item.tool.EntityParticle;
import ic2.core.item.tool.ItemElectricTool;
import ic2.core.ref.ItemName;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class PlasmaLauncher
extends ItemElectricTool {
    public PlasmaLauncher() {
        super(ItemName.plasma_launcher, 100);
        this.maxCharge = 40000;
        this.transferLimit = 128;
        this.tier = 3;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if (!IC2.platform.isSimulating()) {
            return new ActionResult(EnumActionResult.PASS, (Object)stack);
        }
        EntityParticle particle = new EntityParticle(world, (EntityLivingBase)player, 8.0f, 1.0, 2.0);
        world.spawnEntityInWorld((Entity)particle);
        return super.onItemRightClick(stack, world, player, hand);
    }
}

