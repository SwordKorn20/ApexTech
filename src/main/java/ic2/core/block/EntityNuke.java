/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumHand
 *  net.minecraft.world.World
 */
package ic2.core.block;

import ic2.core.IC2;
import ic2.core.Platform;
import ic2.core.block.EntityIC2Explosive;
import ic2.core.block.state.IIdProvider;
import ic2.core.item.tool.ItemToolWrench;
import ic2.core.ref.BlockName;
import ic2.core.ref.TeBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class EntityNuke
extends EntityIC2Explosive {
    public EntityNuke(World world, double x, double y, double z, float power, int radiationRange) {
        super(world, x, y, z, 300, power, 0.05f, 1.5f, BlockName.te.getBlockState(TeBlock.nuke), radiationRange);
    }

    public EntityNuke(World world) {
        this(world, 0.0, 0.0, 0.0, 0.0f, 0);
    }

    public boolean processInitialInteract(EntityPlayer player, ItemStack stack, EnumHand hand) {
        ItemToolWrench wrench;
        if (IC2.platform.isSimulating() && stack != null && stack.getItem() instanceof ItemToolWrench && (wrench = (ItemToolWrench)stack.getItem()).canTakeDamage(stack, 1)) {
            wrench.damage(stack, 1, player);
            this.setDead();
        }
        return false;
    }
}

