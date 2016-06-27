/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Multimap
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.enchantment.Enchantment
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.attributes.AttributeModifier
 *  net.minecraft.entity.ai.attributes.IAttribute
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.monster.EntityCreeper
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.init.Enchantments
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.stats.StatBase
 *  net.minecraft.stats.StatList
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.common.IShearable
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$EntityInteract
 *  net.minecraftforge.fml.common.eventhandler.EventBus
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.item.tool;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItemManager;
import ic2.core.IC2;
import ic2.core.IC2Achievements;
import ic2.core.IHitSoundOverride;
import ic2.core.Platform;
import ic2.core.item.tool.ItemElectricTool;
import ic2.core.ref.ItemName;
import ic2.core.util.Keyboard;
import ic2.core.util.StackUtil;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.stats.StatBase;
import net.minecraft.stats.StatList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemElectricToolChainsaw
extends ItemElectricTool
implements IHitSoundOverride {
    public ItemElectricToolChainsaw() {
        super(ItemName.chainsaw, 100, ItemElectricTool.HarvestLevel.Iron, EnumSet.of(ItemElectricTool.ToolClass.Axe, ItemElectricTool.ToolClass.Sword, ItemElectricTool.ToolClass.Shears));
        this.maxCharge = 30000;
        this.transferLimit = 100;
        this.tier = 1;
        this.efficiencyOnProperMaterial = 12.0f;
        MinecraftForge.EVENT_BUS.register((Object)this);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if (world.isRemote) {
            return super.onItemRightClick(stack, world, player, hand);
        }
        if (IC2.keyboard.isModeSwitchKeyDown(player)) {
            NBTTagCompound compoundTag = StackUtil.getOrCreateNbtData(stack);
            if (compoundTag.getBoolean("disableShear")) {
                compoundTag.setBoolean("disableShear", false);
                IC2.platform.messagePlayer(player, "ic2.tooltip.mode", "ic2.tooltip.mode.normal");
            } else {
                compoundTag.setBoolean("disableShear", true);
                IC2.platform.messagePlayer(player, "ic2.tooltip.mode", "ic2.tooltip.mode.noShear");
            }
        }
        return super.onItemRightClick(stack, world, player, hand);
    }

    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        if (slot != EntityEquipmentSlot.MAINHAND) {
            return super.getAttributeModifiers(slot, stack);
        }
        HashMultimap ret = HashMultimap.create();
        if (ElectricItem.manager.canUse(stack, this.operationEnergyCost)) {
            ret.put((Object)SharedMonsterAttributes.ATTACK_SPEED.getAttributeUnlocalizedName(), (Object)new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", (double)this.attackSpeed, 0));
            ret.put((Object)SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName(), (Object)new AttributeModifier(Item.ATTACK_DAMAGE_MODIFIER, "Tool modifier", 9.0, 0));
        }
        return ret;
    }

    @Override
    public boolean hitEntity(ItemStack itemstack, EntityLivingBase entityliving, EntityLivingBase attacker) {
        ElectricItem.manager.use(itemstack, this.operationEnergyCost, attacker);
        if (attacker instanceof EntityPlayer && entityliving instanceof EntityCreeper && entityliving.getHealth() <= 0.0f) {
            IC2.achievements.issueAchievement((EntityPlayer)attacker, "killCreeperChainsaw");
        }
        return true;
    }

    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        BlockPos pos;
        IShearable target;
        if (!IC2.platform.isSimulating()) {
            return;
        }
        Entity entity = event.getTarget();
        EntityPlayer player = event.getEntityPlayer();
        ItemStack itemstack = player.inventory.getStackInSlot(player.inventory.currentItem);
        if (itemstack != null && itemstack.getItem() == this && entity instanceof IShearable && !StackUtil.getOrCreateNbtData(itemstack).getBoolean("disableShear") && ElectricItem.manager.use(itemstack, this.operationEnergyCost, (EntityLivingBase)player) && (target = (IShearable)entity).isShearable(itemstack, (IBlockAccess)entity.worldObj, pos = new BlockPos(entity.posX, entity.posY, entity.posZ))) {
            List drops = target.onSheared(itemstack, (IBlockAccess)entity.worldObj, pos, EnchantmentHelper.getEnchantmentLevel((Enchantment)Enchantments.FORTUNE, (ItemStack)itemstack));
            for (ItemStack stack : drops) {
                EntityItem ent = entity.entityDropItem(stack, 1.0f);
                ent.motionY += (double)(itemRand.nextFloat() * 0.05f);
                ent.motionX += (double)((itemRand.nextFloat() - itemRand.nextFloat()) * 0.1f);
                ent.motionZ += (double)((itemRand.nextFloat() - itemRand.nextFloat()) * 0.1f);
            }
        }
    }

    public boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, EntityPlayer player) {
        IShearable target;
        if (!IC2.platform.isSimulating()) {
            return false;
        }
        if (StackUtil.getOrCreateNbtData(itemstack).getBoolean("disableShear")) {
            return false;
        }
        World world = player.worldObj;
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (block instanceof IShearable && (target = (IShearable)block).isShearable(itemstack, (IBlockAccess)player.worldObj, pos) && ElectricItem.manager.use(itemstack, this.operationEnergyCost, (EntityLivingBase)player)) {
            List drops = target.onSheared(itemstack, (IBlockAccess)player.worldObj, pos, EnchantmentHelper.getEnchantmentLevel((Enchantment)Enchantments.FORTUNE, (ItemStack)itemstack));
            for (ItemStack stack : drops) {
                StackUtil.dropAsEntity(world, pos, stack);
            }
            player.addStat(StatList.getBlockStats((Block)block), 1);
        }
        return false;
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public String getHitSoundForBlock(EntityPlayerSP player, World world, BlockPos pos, ItemStack stack) {
        return null;
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public String getBreakSoundForBlock(EntityPlayerSP player, World world, BlockPos pos, ItemStack stack) {
        return null;
    }

    @Override
    protected String getIdleSound(EntityLivingBase player, ItemStack stack) {
        return "Tools/Chainsaw/ChainsawIdle.ogg";
    }

    @Override
    protected String getStopSound(EntityLivingBase player, ItemStack stack) {
        return "Tools/Chainsaw/ChainsawStop.ogg";
    }
}

