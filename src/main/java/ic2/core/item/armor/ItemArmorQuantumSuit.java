/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.init.MobEffects
 *  net.minecraft.inventory.Container
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.FoodStats
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.world.World
 *  net.minecraftforge.common.ISpecialArmor
 *  net.minecraftforge.common.ISpecialArmor$ArmorProperties
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.entity.living.LivingFallEvent
 *  net.minecraftforge.fml.common.eventhandler.EventBus
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.item.armor;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItemManager;
import ic2.core.IC2;
import ic2.core.IC2Achievements;
import ic2.core.IC2Potion;
import ic2.core.Platform;
import ic2.core.audio.AudioManager;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import ic2.core.init.InternalName;
import ic2.core.init.MainConfig;
import ic2.core.item.ItemTinCan;
import ic2.core.item.armor.ItemArmorElectric;
import ic2.core.ref.IItemModelProvider;
import ic2.core.ref.ItemName;
import ic2.core.util.ConfigUtil;
import ic2.core.util.Keyboard;
import ic2.core.util.StackUtil;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ISpecialArmor;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingFallEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemArmorQuantumSuit
extends ItemArmorElectric {
    private static final int defaultColor = -1;
    protected static final Map<Potion, Integer> potionRemovalCost = new IdentityHashMap<Potion, Integer>();
    private float jumpCharge;
    public static AudioSource audioSource;
    private static boolean lastJetpackUsed;

    public ItemArmorQuantumSuit(ItemName name, EntityEquipmentSlot armorType) {
        super(name, InternalName.quantum, armorType, 1.0E7, 12000.0, 4);
        if (armorType == EntityEquipmentSlot.FEET) {
            MinecraftForge.EVENT_BUS.register((Object)this);
        }
        potionRemovalCost.put(MobEffects.POISON, 10000);
        potionRemovalCost.put(IC2Potion.radiation, 10000);
        potionRemovalCost.put(MobEffects.WITHER, 25000);
    }

    @Override
    protected boolean hasOverlayTexture() {
        return true;
    }

    public boolean hasColor(ItemStack aStack) {
        return this.getColor(aStack) != -1;
    }

    public void removeColor(ItemStack stack) {
        NBTTagCompound nbt = this.getDisplayNbt(stack, false);
        if (nbt == null || !nbt.hasKey("color", 3)) {
            return;
        }
        nbt.removeTag("color");
    }

    public int getColor(ItemStack stack) {
        NBTTagCompound nbt = this.getDisplayNbt(stack, false);
        if (nbt == null || !nbt.hasKey("color", 3)) {
            return -1;
        }
        return nbt.getInteger("color");
    }

    public void colorQArmor(ItemStack stack, int color) {
        NBTTagCompound nbt = this.getDisplayNbt(stack, true);
        nbt.setInteger("color", color);
    }

    private NBTTagCompound getDisplayNbt(ItemStack stack, boolean create) {
        NBTTagCompound ret;
        NBTTagCompound nbt = stack.getTagCompound();
        if (nbt == null) {
            if (!create) {
                return null;
            }
            nbt = new NBTTagCompound();
            stack.setTagCompound(nbt);
        }
        if (!nbt.hasKey("display", 10)) {
            if (!create) {
                return null;
            }
            ret = new NBTTagCompound();
            nbt.setTag("display", (NBTBase)ret);
        } else {
            ret = nbt.getCompoundTag("display");
        }
        return ret;
    }

    @Override
    public ISpecialArmor.ArmorProperties getProperties(EntityLivingBase entity, ItemStack armor, DamageSource source, double damage, int slot) {
        if (source == DamageSource.fall && this.armorType == EntityEquipmentSlot.FEET) {
            int energyPerDamage = this.getEnergyPerDamage();
            int damageLimit = Integer.MAX_VALUE;
            if (energyPerDamage > 0) {
                damageLimit = (int)Math.min((double)damageLimit, 25.0 * ElectricItem.manager.getCharge(armor) / (double)energyPerDamage);
            }
            return new ISpecialArmor.ArmorProperties(10, 1.0, damageLimit);
        }
        return super.getProperties(entity, armor, source, damage, slot);
    }

    @SubscribeEvent
    public void onEntityLivingFallEvent(LivingFallEvent event) {
        ItemStack armor;
        EntityLivingBase entity;
        if (IC2.platform.isSimulating() && event.getEntity() instanceof EntityLivingBase && (armor = (entity = (EntityLivingBase)event.getEntity()).getItemStackFromSlot(EntityEquipmentSlot.FEET)) != null && armor.getItem() == this) {
            int fallDamage = Math.max((int)event.getDistance() - 10, 0);
            double energyCost = this.getEnergyPerDamage() * fallDamage;
            if (energyCost <= ElectricItem.manager.getCharge(armor)) {
                ElectricItem.manager.discharge(armor, energyCost, Integer.MAX_VALUE, true, false, false);
                event.setCanceled(true);
            }
        }
    }

    @Override
    public double getDamageAbsorptionRatio() {
        if (this.armorType == EntityEquipmentSlot.CHEST) {
            return 1.1;
        }
        return 1.0;
    }

    @Override
    public int getEnergyPerDamage() {
        return 20000;
    }

    @SideOnly(value=Side.CLIENT)
    public EnumRarity getRarity(ItemStack stack) {
        return EnumRarity.RARE;
    }

    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
        NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(stack);
        byte toggleTimer = nbtData.getByte("toggleTimer");
        boolean ret = false;
        switch (this.armorType) {
            case HEAD: {
                IC2.platform.profilerStartSection("QuantumHelmet");
                int air = player.getAir();
                if (ElectricItem.manager.canUse(stack, 1000.0) && air < 100) {
                    player.setAir(air + 200);
                    ElectricItem.manager.use(stack, 1000.0, null);
                    ret = true;
                } else if (air <= 0) {
                    IC2.achievements.issueAchievement(player, "starveWithQHelmet");
                }
                if (ElectricItem.manager.canUse(stack, 1000.0) && player.getFoodStats().needFood()) {
                    int slot = -1;
                    for (int i = 0; i < player.inventory.mainInventory.length; ++i) {
                        if (player.inventory.mainInventory[i] == null || player.inventory.mainInventory[i].getItem() != ItemName.filled_tin_can.getInstance()) continue;
                        slot = i;
                        break;
                    }
                    if (slot > -1) {
                        ItemStack cStack = player.inventory.mainInventory[slot];
                        ItemTinCan can = (ItemTinCan)cStack.getItem();
                        cStack = (ItemStack)can.onEaten(player, cStack).getResult();
                        if (cStack.stackSize <= 0) {
                            player.inventory.mainInventory[slot] = null;
                        }
                        ElectricItem.manager.use(stack, 1000.0, null);
                        ret = true;
                    }
                } else if (player.getFoodStats().getFoodLevel() <= 0) {
                    IC2.achievements.issueAchievement(player, "starveWithQHelmet");
                }
                for (PotionEffect effect : new LinkedList(player.getActivePotionEffects())) {
                    Potion potion = effect.getPotion();
                    Integer cost = potionRemovalCost.get((Object)potion);
                    if (cost == null || !ElectricItem.manager.canUse(stack, (cost = Integer.valueOf(cost * (effect.getAmplifier() + 1))).intValue())) continue;
                    ElectricItem.manager.use(stack, cost.intValue(), null);
                    IC2.platform.removePotion((EntityLivingBase)player, potion);
                }
                boolean Nightvision = nbtData.getBoolean("Nightvision");
                short hubmode = nbtData.getShort("HudMode");
                if (IC2.keyboard.isAltKeyDown(player) && IC2.keyboard.isModeSwitchKeyDown(player) && toggleTimer == 0) {
                    toggleTimer = 10;
                    boolean bl = Nightvision = !Nightvision;
                    if (IC2.platform.isSimulating()) {
                        nbtData.setBoolean("Nightvision", Nightvision);
                        if (Nightvision) {
                            IC2.platform.messagePlayer(player, "Nightvision enabled.", new Object[0]);
                        } else {
                            IC2.platform.messagePlayer(player, "Nightvision disabled.", new Object[0]);
                        }
                    }
                }
                if (IC2.keyboard.isAltKeyDown(player) && IC2.keyboard.isHudModeKeyDown(player) && toggleTimer == 0) {
                    toggleTimer = 10;
                    hubmode = hubmode == 2 ? 0 : (short)(hubmode + 1);
                    if (IC2.platform.isSimulating()) {
                        nbtData.setShort("HudMode", hubmode);
                        switch (hubmode) {
                            case 0: {
                                IC2.platform.messagePlayer(player, "HUD disabled.", new Object[0]);
                                break;
                            }
                            case 1: {
                                IC2.platform.messagePlayer(player, "HUD (basic) enabled.", new Object[0]);
                                break;
                            }
                            case 2: {
                                IC2.platform.messagePlayer(player, "HUD (extended) enabled", new Object[0]);
                            }
                        }
                    }
                }
                if (IC2.platform.isSimulating() && toggleTimer > 0) {
                    toggleTimer = (byte)(toggleTimer - 1);
                    nbtData.setByte("toggleTimer", toggleTimer);
                }
                if (Nightvision && IC2.platform.isSimulating() && ElectricItem.manager.use(stack, 1.0, (EntityLivingBase)player)) {
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
                IC2.platform.profilerEndSection();
                break;
            }
            case CHEST: {
                IC2.platform.profilerStartSection("QuantumBodyarmor");
                boolean jetpack = nbtData.getBoolean("jetpack");
                boolean hoverMode = nbtData.getBoolean("hoverMode");
                boolean jetpackUsed = false;
                if (IC2.keyboard.isJumpKeyDown(player) && IC2.keyboard.isModeSwitchKeyDown(player) && toggleTimer == 0) {
                    toggleTimer = 10;
                    boolean bl = hoverMode = !hoverMode;
                    if (IC2.platform.isSimulating()) {
                        nbtData.setBoolean("hoverMode", hoverMode);
                        if (hoverMode) {
                            IC2.platform.messagePlayer(player, "Quantum Hover Mode enabled.", new Object[0]);
                        } else {
                            IC2.platform.messagePlayer(player, "Quantum Hover Mode disabled.", new Object[0]);
                        }
                    }
                }
                if (IC2.keyboard.isBoostKeyDown(player) && IC2.keyboard.isModeSwitchKeyDown(player) && toggleTimer == 0) {
                    toggleTimer = 10;
                    boolean bl = jetpack = !jetpack;
                    if (IC2.platform.isSimulating()) {
                        nbtData.setBoolean("jetpack", jetpack);
                        if (jetpack) {
                            IC2.platform.messagePlayer(player, "Quantum Jetpack enabled.", new Object[0]);
                        } else {
                            IC2.platform.messagePlayer(player, "Quantum Jetpack disabled.", new Object[0]);
                        }
                    }
                }
                if (jetpack && (IC2.keyboard.isJumpKeyDown(player) || hoverMode && player.motionY < -0.029999999329447746)) {
                    jetpackUsed = this.useJetpack(player, hoverMode);
                }
                if (IC2.platform.isSimulating() && toggleTimer > 0) {
                    toggleTimer = (byte)(toggleTimer - 1);
                    nbtData.setByte("toggleTimer", toggleTimer);
                }
                if (IC2.platform.isRendering() && player == IC2.platform.getPlayerInstance()) {
                    if (lastJetpackUsed != jetpackUsed) {
                        if (jetpackUsed) {
                            if (audioSource == null) {
                                audioSource = IC2.audioManager.createSource((Object)player, PositionSpec.Backpack, "Tools/Jetpack/JetpackLoop.ogg", true, false, IC2.audioManager.getDefaultVolume());
                            }
                            if (audioSource != null) {
                                audioSource.play();
                            }
                        } else if (audioSource != null) {
                            audioSource.remove();
                            audioSource = null;
                        }
                        lastJetpackUsed = jetpackUsed;
                    }
                    if (audioSource != null) {
                        audioSource.updatePosition();
                    }
                }
                ret = jetpackUsed;
                player.extinguish();
                IC2.platform.profilerEndSection();
                break;
            }
            case LEGS: {
                IC2.platform.profilerStartSection("QuantumLeggings");
                boolean enableQuantumSpeedOnSprint = IC2.platform.isRendering() ? ConfigUtil.getBool(MainConfig.get(), "misc/quantumSpeedOnSprint") : true;
                if (ElectricItem.manager.canUse(stack, 1000.0) && (player.onGround || player.isInWater()) && IC2.keyboard.isForwardKeyDown(player) && (enableQuantumSpeedOnSprint && player.isSprinting() || !enableQuantumSpeedOnSprint && IC2.keyboard.isBoostKeyDown(player))) {
                    byte speedTicker = nbtData.getByte("speedTicker");
                    if ((speedTicker = (byte)(speedTicker + 1)) >= 10) {
                        speedTicker = 0;
                        ElectricItem.manager.use(stack, 1000.0, null);
                        ret = true;
                    }
                    nbtData.setByte("speedTicker", speedTicker);
                    float speed = 0.22f;
                    if (player.isInWater()) {
                        speed = 0.1f;
                        if (IC2.keyboard.isJumpKeyDown(player)) {
                            player.motionY += 0.10000000149011612;
                        }
                    }
                    if (speed > 0.0f) {
                        player.moveRelative(0.0f, 1.0f, speed);
                    }
                }
                IC2.platform.profilerEndSection();
                break;
            }
            case FEET: {
                IC2.platform.profilerStartSection("QuantumBoots");
                if (IC2.platform.isSimulating()) {
                    boolean wasOnGround;
                    boolean bl = wasOnGround = nbtData.hasKey("wasOnGround") ? nbtData.getBoolean("wasOnGround") : true;
                    if (wasOnGround && !player.onGround && IC2.keyboard.isJumpKeyDown(player) && IC2.keyboard.isBoostKeyDown(player)) {
                        ElectricItem.manager.use(stack, 4000.0, null);
                        ret = true;
                    }
                    if (player.onGround != wasOnGround) {
                        nbtData.setBoolean("wasOnGround", player.onGround);
                    }
                } else {
                    if (ElectricItem.manager.canUse(stack, 4000.0) && player.onGround) {
                        this.jumpCharge = 1.0f;
                    }
                    if (player.motionY >= 0.0 && this.jumpCharge > 0.0f && !player.isInWater()) {
                        if (IC2.keyboard.isJumpKeyDown(player) && IC2.keyboard.isBoostKeyDown(player)) {
                            if (this.jumpCharge == 1.0f) {
                                player.motionX *= 3.5;
                                player.motionZ *= 3.5;
                            }
                            player.motionY += (double)(this.jumpCharge * 0.3f);
                            this.jumpCharge = (float)((double)this.jumpCharge * 0.75);
                        } else if (this.jumpCharge < 1.0f) {
                            this.jumpCharge = 0.0f;
                        }
                    }
                }
                IC2.platform.profilerEndSection();
                break;
            }
        }
        if (ret) {
            player.inventoryContainer.detectAndSendChanges();
        }
    }

    @Override
    public int getItemEnchantability() {
        return 0;
    }

    public boolean useJetpack(EntityPlayer player, boolean hoverMode) {
        int worldHeight;
        double y;
        ItemStack jetpack = player.inventory.armorInventory[2];
        if (ElectricItem.manager.getCharge(jetpack) == 0.0) {
            return false;
        }
        float power = 1.0f;
        float dropPercentage = 0.05f;
        if ((double)((float)ElectricItem.manager.getCharge(jetpack)) / this.getMaxCharge(jetpack) <= (double)dropPercentage) {
            power = (float)((double)power * (ElectricItem.manager.getCharge(jetpack) / (this.getMaxCharge(jetpack) * (double)dropPercentage)));
        }
        if (IC2.keyboard.isForwardKeyDown(player)) {
            float forwardpower;
            float retruster = 3.5f;
            if (hoverMode) {
                retruster = 0.5f;
            }
            if ((forwardpower = power * retruster * 2.0f) > 0.0f) {
                player.moveRelative(0.0f, 0.4f * forwardpower, 0.02f);
            }
        }
        if ((y = player.posY) > (double)((worldHeight = IC2.getWorldHeight(player.worldObj)) - 25)) {
            if (y > (double)worldHeight) {
                y = worldHeight;
            }
            power = (float)((double)power * (((double)worldHeight - y) / 25.0));
        }
        double prevmotion = player.motionY;
        player.motionY = Math.min(player.motionY + (double)(power * 0.2f), 0.6000000238418579);
        if (hoverMode) {
            float maxHoverY = -0.025f;
            if (IC2.keyboard.isSneakKeyDown(player)) {
                maxHoverY = -0.1f;
            }
            if (IC2.keyboard.isJumpKeyDown(player)) {
                maxHoverY = 0.1f;
            }
            if (player.motionY > (double)maxHoverY) {
                player.motionY = maxHoverY;
                if (prevmotion > player.motionY) {
                    player.motionY = prevmotion;
                }
            }
        }
        double consume = 8.0;
        if (hoverMode) {
            consume = 10.0;
        }
        ElectricItem.manager.discharge(jetpack, consume, Integer.MAX_VALUE, true, false, false);
        player.fallDistance = 0.0f;
        player.distanceWalkedModified = 0.0f;
        IC2.platform.resetPlayerInAirTime(player);
        return true;
    }

    static {
        lastJetpackUsed = false;
    }

}

