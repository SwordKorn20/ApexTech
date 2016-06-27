/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.init.MobEffects
 *  net.minecraft.item.EnumAction
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package ic2.core.item;

import ic2.core.block.TileEntityBarrel;
import ic2.core.block.state.IIdProvider;
import ic2.core.item.ItemMulti;
import ic2.core.ref.BlockName;
import ic2.core.ref.IItemModelProvider;
import ic2.core.ref.ItemName;
import ic2.core.ref.TeBlock;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ItemMug
extends ItemMulti<MugType> {
    public ItemMug() {
        super(ItemName.mug, MugType.class);
        this.setMaxStackSize(1);
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        MugType type = (MugType)this.getType(stack);
        if (type == MugType.empty) {
            if (world.isRemote) {
                return EnumActionResult.FAIL;
            }
            if (world.getBlockState(pos) != BlockName.te.getBlockState(TeBlock.barrel)) {
                return EnumActionResult.PASS;
            }
            TileEntityBarrel barrel = (TileEntityBarrel)world.getTileEntity(pos);
            if (!barrel.getActive() || barrel.getFacing() != side) {
                return EnumActionResult.PASS;
            }
            int value = barrel.calculateMetaValue();
            if (barrel.drainLiquid(1)) {
                if (--stack.stackSize > 0) {
                    ItemStack is = new ItemStack(ItemName.booze_mug.getInstance(), 1, value);
                    if (!player.inventory.addItemStackToInventory(is)) {
                        player.dropItem(is, false);
                    }
                } else {
                    stack.stackSize = 1;
                    stack.setItemDamage(value);
                }
                return EnumActionResult.SUCCESS;
            }
        }
        return EnumActionResult.PASS;
    }

    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, EntityLivingBase entityLiving) {
        int maxAmplifier;
        int extraDuration;
        if (!(entityLiving instanceof EntityPlayer)) {
            return stack;
        }
        EntityPlayer player = (EntityPlayer)entityLiving;
        MugType type = (MugType)this.getType(stack);
        if (type == null || type == MugType.empty) {
            return stack;
        }
        switch (type) {
            case cold_coffee: {
                maxAmplifier = 1;
                extraDuration = 600;
                break;
            }
            case dark_coffee: {
                maxAmplifier = 5;
                extraDuration = 1200;
                break;
            }
            case coffee: {
                maxAmplifier = 6;
                extraDuration = 1200;
                break;
            }
            default: {
                throw new IllegalStateException("unexpected type: " + type);
            }
        }
        int highest = 0;
        int x = this.amplifyEffect(player, MobEffects.SPEED, maxAmplifier, extraDuration);
        if (x > highest) {
            highest = x;
        }
        if ((x = this.amplifyEffect(player, MobEffects.HASTE, maxAmplifier, extraDuration)) > highest) {
            highest = x;
        }
        if (type == MugType.coffee) {
            highest -= 2;
        }
        if (highest >= 3) {
            player.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, (highest - 2) * 200, 0));
            if (highest >= 4) {
                player.addPotionEffect(new PotionEffect(MobEffects.INSTANT_DAMAGE, 1, highest - 3));
            }
        }
        return this.getItemStack(MugType.empty);
    }

    private int amplifyEffect(EntityPlayer player, Potion potion, int maxAmplifier, int extraDuration) {
        PotionEffect eff = player.getActivePotionEffect(potion);
        if (eff != null) {
            int newAmp = eff.getAmplifier();
            int newDur = eff.getDuration();
            if (newAmp < maxAmplifier) {
                ++newAmp;
            }
            eff.combine(new PotionEffect(eff.getPotion(), newDur += extraDuration, newAmp));
            return newAmp;
        }
        player.addPotionEffect(new PotionEffect(potion, 300, 0));
        return 1;
    }

    public int getMaxItemUseDuration(ItemStack stack) {
        MugType type = (MugType)this.getType(stack);
        if (type == null || type == MugType.empty) {
            return 0;
        }
        return 32;
    }

    public EnumAction getItemUseAction(ItemStack stack) {
        MugType type = (MugType)this.getType(stack);
        if (type == null || type == MugType.empty) {
            return EnumAction.NONE;
        }
        return EnumAction.DRINK;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        MugType type = (MugType)this.getType(stack);
        if (type != null && type != MugType.empty) {
            player.setActiveHand(hand);
        }
        return super.onItemRightClick(stack, world, player, hand);
    }

    public static enum MugType implements IIdProvider
    {
        empty,
        cold_coffee,
        dark_coffee,
        coffee;
        

        private MugType() {
        }

        @Override
        public String getName() {
            return this.name();
        }

        @Override
        public int getId() {
            return this.ordinal();
        }
    }

}

