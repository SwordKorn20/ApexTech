/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.inventory.Container
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.world.World
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.item.armor;

import ic2.core.IC2;
import ic2.core.Platform;
import ic2.core.audio.AudioManager;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import ic2.core.init.InternalName;
import ic2.core.item.armor.ItemArmorFluidTank;
import ic2.core.ref.FluidName;
import ic2.core.ref.IItemModelProvider;
import ic2.core.ref.ItemName;
import ic2.core.util.Keyboard;
import ic2.core.util.StackUtil;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemArmorJetpack
extends ItemArmorFluidTank {
    protected static AudioSource audioSource;
    private static boolean lastJetpackUsed;

    public ItemArmorJetpack() {
        this(ItemName.jetpack);
    }

    protected ItemArmorJetpack(ItemName name) {
        super(name, InternalName.jetpack, FluidName.biogas.getInstance(), 30000);
    }

    public void use(ItemStack stack, double amount) {
        this.drainfromJetpack(stack, (int)amount);
        this.Updatedamage(stack);
    }

    public boolean useJetpack(EntityPlayer player, boolean hoverMode) {
        ItemStack jetpack = player.inventory.armorInventory[2];
        if (this.getCharge(jetpack) <= 0.0) {
            return false;
        }
        boolean electric = jetpack.getItem() == ItemName.jetpack_electric.getInstance();
        float power = 1.0f;
        float dropPercentage = 0.2f;
        if (electric) {
            power = 0.7f;
            dropPercentage = 0.05f;
        }
        if (this.getCharge(jetpack) / this.getMaxCharge(jetpack) <= (double)dropPercentage) {
            power = (float)((double)power * (this.getCharge(jetpack) / (this.getMaxCharge(jetpack) * (double)dropPercentage)));
        }
        if (IC2.keyboard.isForwardKeyDown(player)) {
            float forwardpower;
            float retruster = 0.15f;
            if (hoverMode) {
                retruster = 1.0f;
            }
            if (electric) {
                retruster += 0.15f;
            }
            if ((forwardpower = power * retruster * 2.0f) > 0.0f) {
                player.moveRelative(0.0f, 0.4f * forwardpower, 0.02f);
            }
        }
        int worldHeight = IC2.getWorldHeight(player.worldObj);
        double y = player.posY;
        int maxFlightHeight = electric ? (int)((float)worldHeight / 1.28f) : worldHeight;
        if (y > (double)(maxFlightHeight - 25)) {
            if (y > (double)maxFlightHeight) {
                y = maxFlightHeight;
            }
            power = (float)((double)power * (((double)maxFlightHeight - y) / 25.0));
        }
        double prevmotion = player.motionY;
        player.motionY = Math.min(player.motionY + (double)(power * 0.2f), 0.6000000238418579);
        if (hoverMode) {
            float maxHoverY = 0.0f;
            if (IC2.keyboard.isJumpKeyDown(player)) {
                maxHoverY = electric ? 0.1f : 0.2f;
            }
            if (IC2.keyboard.isSneakKeyDown(player)) {
                maxHoverY = electric ? -0.1f : -0.2f;
            }
            if (player.motionY > (double)maxHoverY) {
                player.motionY = maxHoverY;
                if (prevmotion > player.motionY) {
                    player.motionY = prevmotion;
                }
            }
        }
        int consume = 2;
        if (hoverMode) {
            consume = 1;
        }
        if (electric) {
            consume += 6;
        }
        if (!player.onGround) {
            this.use(jetpack, consume);
        }
        player.fallDistance = 0.0f;
        player.distanceWalkedModified = 0.0f;
        IC2.platform.resetPlayerInAirTime(player);
        return true;
    }

    public boolean drainfromJetpack(ItemStack pack, int amount) {
        if (this.isEmpty(pack)) {
            return false;
        }
        if (this.drain((ItemStack)pack, (int)amount, (boolean)false).amount < amount) {
            return false;
        }
        this.drain(pack, amount, true);
        return true;
    }

    public void onArmorTick(World world, EntityPlayer player, ItemStack stack) {
        if (player.inventory.armorInventory[2] != stack) {
            return;
        }
        NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(stack);
        boolean hoverMode = nbtData.getBoolean("hoverMode");
        byte toggleTimer = nbtData.getByte("toggleTimer");
        boolean jetpackUsed = false;
        if (IC2.keyboard.isJumpKeyDown(player) && IC2.keyboard.isModeSwitchKeyDown(player) && toggleTimer == 0) {
            toggleTimer = 10;
            boolean bl = hoverMode = !hoverMode;
            if (IC2.platform.isSimulating()) {
                nbtData.setBoolean("hoverMode", hoverMode);
                if (hoverMode) {
                    IC2.platform.messagePlayer(player, "Hover Mode enabled.", new Object[0]);
                } else {
                    IC2.platform.messagePlayer(player, "Hover Mode disabled.", new Object[0]);
                }
            }
        }
        if (IC2.keyboard.isJumpKeyDown(player) || hoverMode) {
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
        if (jetpackUsed) {
            player.inventoryContainer.detectAndSendChanges();
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> subItems) {
        ItemStack stack = new ItemStack(item, 1);
        this.filltank(stack);
        stack.setItemDamage(1);
        subItems.add(stack);
        stack = new ItemStack(item, 1);
        stack.setItemDamage(this.getMaxDamage());
        subItems.add(stack);
    }

    static {
        lastJetpackUsed = false;
    }
}

