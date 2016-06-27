/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  org.apache.logging.log4j.Level
 */
package ic2.core.block.machine.tileentity;

import com.mojang.authlib.GameProfile;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.block.BlockTileEntity;
import ic2.core.block.EntityIC2Explosive;
import ic2.core.block.EntityNuke;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlotConsumable;
import ic2.core.block.invslot.InvSlotConsumableItemStack;
import ic2.core.block.machine.container.ContainerNuke;
import ic2.core.block.machine.gui.GuiNuke;
import ic2.core.block.machine.tileentity.Explosive;
import ic2.core.block.state.IIdProvider;
import ic2.core.block.type.ResourceBlock;
import ic2.core.init.MainConfig;
import ic2.core.item.type.NuclearResourceType;
import ic2.core.ref.BlockName;
import ic2.core.ref.ItemName;
import ic2.core.ref.TeBlock;
import ic2.core.util.ConfigUtil;
import ic2.core.util.Log;
import ic2.core.util.LogCategory;
import ic2.core.util.StackUtil;
import ic2.core.util.Util;
import java.util.UUID;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Level;

public class TileEntityNuke
extends Explosive
implements IHasGui {
    public int RadiationRange;
    public final InvSlotConsumable outsideSlot;
    public final InvSlotConsumable insideSlot;

    public TileEntityNuke() {
        this.insideSlot = new InvSlotConsumableItemStack((TileEntityInventory)this, "insideSlot", 1, BlockName.resource.getItemStack(ResourceBlock.uranium_block), ItemName.nuclear.getItemStack(NuclearResourceType.uranium_238), ItemName.nuclear.getItemStack(NuclearResourceType.uranium_235), ItemName.nuclear.getItemStack(NuclearResourceType.small_uranium_235), ItemName.nuclear.getItemStack(NuclearResourceType.plutonium), ItemName.nuclear.getItemStack(NuclearResourceType.small_plutonium));
        this.outsideSlot = new InvSlotConsumableItemStack((TileEntityInventory)this, "outsideSlot", 1, this.getBlockType().getItemStack(TeBlock.itnt));
    }

    public int getRadiationRange() {
        return this.RadiationRange;
    }

    public void setRadiationRange(int range) {
        if (range != this.RadiationRange) {
            this.RadiationRange = range;
        }
    }

    public float getNukeExplosivePower() {
        if (this.outsideSlot.isEmpty()) {
            return -1.0f;
        }
        int itntCount = this.outsideSlot.get().stackSize;
        double ret = 5.0 * Math.pow(itntCount, 0.3333333333333333);
        if (this.insideSlot.isEmpty()) {
            this.setRadiationRange(0);
        } else {
            ItemStack inside = this.insideSlot.get();
            if (StackUtil.checkItemEquality(inside, ItemName.nuclear.getItemStack(NuclearResourceType.uranium_238))) {
                this.setRadiationRange(itntCount);
            } else if (StackUtil.checkItemEquality(inside, BlockName.resource.getItemStack(ResourceBlock.uranium_block))) {
                this.setRadiationRange(itntCount * 6);
            } else if (StackUtil.checkItemEquality(inside, ItemName.nuclear.getItemStack(NuclearResourceType.small_uranium_235))) {
                this.setRadiationRange(itntCount * 2);
                if (itntCount >= 64) {
                    ret += 0.05555555555555555 * Math.pow(inside.stackSize, 1.6);
                }
            } else if (StackUtil.checkItemEquality(inside, ItemName.nuclear.getItemStack(NuclearResourceType.uranium_235))) {
                this.setRadiationRange(itntCount * 2);
                if (itntCount >= 32) {
                    ret += 0.5 * Math.pow(inside.stackSize, 1.4);
                }
            } else if (StackUtil.checkItemEquality(inside, ItemName.nuclear.getItemStack(NuclearResourceType.small_plutonium))) {
                this.setRadiationRange(itntCount * 3);
                if (itntCount >= 32) {
                    ret += 0.05555555555555555 * Math.pow(inside.stackSize, 2.0);
                }
            } else if (StackUtil.checkItemEquality(inside, ItemName.nuclear.getItemStack(NuclearResourceType.plutonium))) {
                this.setRadiationRange(itntCount * 4);
                if (itntCount >= 16) {
                    ret += 0.5 * Math.pow(inside.stackSize, 1.8);
                }
            }
        }
        ret = Math.min(ret, (double)ConfigUtil.getFloat(MainConfig.get(), "protection/nukeExplosionPowerLimit"));
        return (float)ret;
    }

    public ContainerBase<TileEntityNuke> getGuiContainer(EntityPlayer player) {
        return new ContainerNuke(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiNuke(new ContainerNuke(player, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }

    @Override
    public void onPlaced(ItemStack stack, EntityLivingBase placer, EnumFacing facing) {
        super.onPlaced(stack, placer, facing);
        if (placer instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)placer;
            String playerName = player.getGameProfile().getName() + "/" + player.getGameProfile().getId();
            IC2.log.log(LogCategory.PlayerActivity, Level.INFO, "Player %s placed a nuke at %s.", playerName, Util.formatPosition(this));
        }
    }

    @Override
    protected EntityIC2Explosive getEntity(EntityLivingBase igniter) {
        if (!ConfigUtil.getBool(MainConfig.get(), "protection/enableNuke")) {
            return null;
        }
        float power = this.getNukeExplosivePower();
        if (power < 0.0f) {
            return null;
        }
        int radiationRange = this.getRadiationRange();
        return new EntityNuke(this.worldObj, (double)this.pos.getX() + 0.5, (double)this.pos.getY() + 0.5, (double)this.pos.getZ() + 0.5, power, radiationRange);
    }

    @Override
    protected void onIgnite(EntityLivingBase igniter) {
        String cause = igniter == null ? "indirectly" : "by " + igniter.getClass().getSimpleName() + " " + igniter.getName();
        IC2.log.log(LogCategory.PlayerActivity, Level.INFO, "Nuke at %s was ignited %s.", Util.formatPosition(this), cause);
        this.outsideSlot.clear();
        this.insideSlot.clear();
    }
}

