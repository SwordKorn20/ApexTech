/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.HashMultimap
 *  com.google.common.collect.Multimap
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.ItemMeshDefinition
 *  net.minecraft.client.renderer.block.model.ModelBakery
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.attributes.AttributeModifier
 *  net.minecraft.entity.ai.attributes.IAttribute
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.item.tool;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItemManager;
import ic2.core.IC2;
import ic2.core.Platform;
import ic2.core.audio.AudioManager;
import ic2.core.audio.PositionSpec;
import ic2.core.item.ItemIC2;
import ic2.core.item.armor.ItemArmorNanoSuit;
import ic2.core.item.armor.ItemArmorQuantumSuit;
import ic2.core.item.tool.ItemElectricTool;
import ic2.core.ref.ItemName;
import ic2.core.slot.ArmorSlot;
import ic2.core.util.StackUtil;
import java.util.EnumSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemNanoSaber
extends ItemElectricTool {
    public static int ticker = 0;
    private int soundTicker = 0;

    public ItemNanoSaber() {
        super(ItemName.nano_saber, 10, ItemElectricTool.HarvestLevel.Diamond, EnumSet.of(ItemElectricTool.ToolClass.Sword));
        this.maxCharge = 160000;
        this.transferLimit = 500;
        this.tier = 3;
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public void registerModels(final ItemName name) {
        String activeSuffix = "active";
        ModelLoader.setCustomMeshDefinition((Item)this, (ItemMeshDefinition)new ItemMeshDefinition(){

            public ModelResourceLocation getModelLocation(ItemStack stack) {
                return ItemIC2.getModelLocation(name, ItemNanoSaber.isActive(stack) ? "active" : null);
            }
        });
        ModelBakery.registerItemVariants((Item)this, (ResourceLocation[])new ResourceLocation[]{ItemIC2.getModelLocation(name, null)});
        ModelBakery.registerItemVariants((Item)this, (ResourceLocation[])new ResourceLocation[]{ItemIC2.getModelLocation(name, "active")});
    }

    @Override
    public float getStrVsBlock(ItemStack stack, IBlockState state) {
        if (ItemNanoSaber.isActive(stack)) {
            ++this.soundTicker;
            if (this.soundTicker % 4 == 0) {
                IC2.platform.playSoundSp(this.getRandomSwingSound(), 1.0f, 1.0f);
            }
            return 4.0f;
        }
        return 1.0f;
    }

    public Multimap<String, AttributeModifier> getAttributeModifiers(EntityEquipmentSlot slot, ItemStack stack) {
        if (slot != EntityEquipmentSlot.MAINHAND) {
            return super.getAttributeModifiers(slot, stack);
        }
        int dmg = 4;
        if (ElectricItem.manager.canUse(stack, 400.0) && ItemNanoSaber.isActive(stack)) {
            dmg = 20;
        }
        HashMultimap ret = HashMultimap.create();
        ret.put((Object)SharedMonsterAttributes.ATTACK_SPEED.getAttributeUnlocalizedName(), (Object)new AttributeModifier(ATTACK_SPEED_MODIFIER, "Tool modifier", (double)this.attackSpeed, 0));
        ret.put((Object)SharedMonsterAttributes.ATTACK_DAMAGE.getAttributeUnlocalizedName(), (Object)new AttributeModifier(Item.ATTACK_DAMAGE_MODIFIER, "Tool modifier", (double)dmg, 0));
        return ret;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase source) {
        if (!ItemNanoSaber.isActive(stack)) {
            return true;
        }
        if (IC2.platform.isSimulating()) {
            ItemNanoSaber.drainSaber(stack, 400.0, source);
            if (!(source instanceof EntityPlayerMP) || !(target instanceof EntityPlayer) || ((EntityPlayerMP)source).canAttackPlayer((EntityPlayer)target)) {
                for (EntityEquipmentSlot slot : ArmorSlot.getAll()) {
                    if (!ElectricItem.manager.canUse(stack, 2000.0)) break;
                    ItemStack armor = target.getItemStackFromSlot(slot);
                    if (armor == null) continue;
                    double amount = 0.0;
                    if (armor.getItem() instanceof ItemArmorNanoSuit) {
                        amount = 48000.0;
                    } else if (armor.getItem() instanceof ItemArmorQuantumSuit) {
                        amount = 300000.0;
                    }
                    if (amount <= 0.0) continue;
                    ElectricItem.manager.discharge(armor, amount, this.tier, true, false, false);
                    if (!ElectricItem.manager.canUse(armor, 1.0)) {
                        target.setItemStackToSlot(slot, null);
                    }
                    ItemNanoSaber.drainSaber(stack, 2000.0, source);
                }
            }
        }
        if (IC2.platform.isRendering()) {
            IC2.platform.playSoundSp(this.getRandomSwingSound(), 1.0f, 1.0f);
        }
        return true;
    }

    public String getRandomSwingSound() {
        switch (IC2.random.nextInt(3)) {
            default: {
                return "Tools/Nanosabre/NanosabreSwing1.ogg";
            }
            case 1: {
                return "Tools/Nanosabre/NanosabreSwing2.ogg";
            }
            case 2: 
        }
        return "Tools/Nanosabre/NanosabreSwing3.ogg";
    }

    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, EntityPlayer player) {
        if (ItemNanoSaber.isActive(stack)) {
            ItemNanoSaber.drainSaber(stack, 80.0, (EntityLivingBase)player);
        }
        return false;
    }

    public boolean isFull3D() {
        return true;
    }

    public static void drainSaber(ItemStack stack, double amount, EntityLivingBase entity) {
        if (!ElectricItem.manager.use(stack, amount, entity)) {
            NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
            ItemNanoSaber.setActive(nbt, false);
        }
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer entityplayer, EnumHand hand) {
        if (world.isRemote) {
            return new ActionResult(EnumActionResult.PASS, (Object)stack);
        }
        NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
        if (ItemNanoSaber.isActive(nbt)) {
            ItemNanoSaber.setActive(nbt, false);
            return new ActionResult(EnumActionResult.SUCCESS, (Object)stack);
        }
        if (ElectricItem.manager.canUse(stack, 16.0)) {
            ItemNanoSaber.setActive(nbt, true);
            return new ActionResult(EnumActionResult.SUCCESS, (Object)stack);
        }
        return super.onItemRightClick(stack, world, entityplayer, hand);
    }

    @Override
    public void onUpdate(ItemStack stack, World world, Entity entity, int slot, boolean par5) {
        super.onUpdate(stack, world, entity, slot, par5 && ItemNanoSaber.isActive(stack));
        if (!ItemNanoSaber.isActive(stack)) {
            return;
        }
        if (ticker % 16 == 0 && entity instanceof EntityPlayerMP) {
            if (slot < 9) {
                ItemNanoSaber.drainSaber(stack, 64.0, (EntityLivingBase)((EntityPlayer)entity));
            } else if (ticker % 64 == 0) {
                ItemNanoSaber.drainSaber(stack, 16.0, (EntityLivingBase)((EntityPlayer)entity));
            }
        }
    }

    @SideOnly(value=Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.UNCOMMON;
    }

    private static boolean isActive(ItemStack stack) {
        NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
        return ItemNanoSaber.isActive(nbt);
    }

    private static boolean isActive(NBTTagCompound nbt) {
        return nbt.getBoolean("active");
    }

    private static void setActive(NBTTagCompound nbt, boolean active) {
        nbt.setBoolean("active", active);
    }

    public boolean onEntitySwing(EntityLivingBase entity, ItemStack stack) {
        if (IC2.platform.isRendering() && ItemNanoSaber.isActive(stack)) {
            IC2.audioManager.playOnce((Object)entity, PositionSpec.Hand, this.getRandomSwingSound(), true, IC2.audioManager.getDefaultVolume());
        }
        return false;
    }

    @Override
    protected String getIdleSound(EntityLivingBase player, ItemStack stack) {
        return "Tools/Nanosabre/NanosabreIdle.ogg";
    }

    @Override
    protected String getStartSound(EntityLivingBase player, ItemStack stack) {
        return "Tools/Nanosabre/NanosabrePowerup.ogg";
    }

}

