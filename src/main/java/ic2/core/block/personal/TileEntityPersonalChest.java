/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.SoundEvents
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTUtil
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.management.PlayerList
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.personal;

import com.mojang.authlib.GameProfile;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.Platform;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.personal.IPersonalBlock;
import ic2.core.gui.dynamic.DynamicContainer;
import ic2.core.gui.dynamic.DynamicGui;
import ic2.core.gui.dynamic.GuiParser;
import ic2.core.network.NetworkManager;
import ic2.core.ref.TeBlock;
import ic2.core.util.SideGateway;
import ic2.core.util.Util;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.WeakHashMap;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityPersonalChest
extends TileEntityInventory
implements IPersonalBlock,
IHasGui {
    private GameProfile owner = null;
    private static final int openingSteps = 10;
    private static final List<AxisAlignedBB> aabbs = Arrays.asList(new AxisAlignedBB[]{new AxisAlignedBB(0.0625, 0.0, 0.0625, 0.9375, 1.0, 0.9375)});
    public final InvSlot contentSlot;
    private final Set<EntityPlayer> usingPlayers = Collections.newSetFromMap(new WeakHashMap());
    private int usingPlayerCount;
    private byte lidAngle;
    private byte prevLidAngle;

    public TileEntityPersonalChest() {
        this.contentSlot = new InvSlot(this, "content", InvSlot.Access.NONE, 54);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (nbt.hasKey("ownerGameProfile")) {
            this.owner = NBTUtil.readGameProfileFromNBT((NBTTagCompound)nbt.getCompoundTag("ownerGameProfile"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        if (this.owner != null) {
            NBTTagCompound ownerNbt = new NBTTagCompound();
            NBTUtil.writeGameProfile((NBTTagCompound)ownerNbt, (GameProfile)this.owner);
            nbt.setTag("ownerGameProfile", (NBTBase)ownerNbt);
        }
        return nbt;
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    protected void updateEntityClient() {
        super.updateEntityClient();
        this.prevLidAngle = this.lidAngle;
        if (this.usingPlayerCount > 0 && this.lidAngle <= 0) {
            this.worldObj.playSound(null, this.pos, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.5f, this.worldObj.rand.nextFloat() * 0.1f + 0.9f);
        }
        if (this.usingPlayerCount == 0 && this.lidAngle > 0 || this.usingPlayerCount > 0 && this.lidAngle < 10) {
            this.lidAngle = this.usingPlayerCount > 0 ? (byte)(this.lidAngle + 1) : (byte)(this.lidAngle - 1);
            byte closeThreshold = 5;
            if (this.lidAngle < closeThreshold && this.prevLidAngle >= closeThreshold) {
                this.worldObj.playSound(null, this.pos, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5f, this.worldObj.rand.nextFloat() * 0.1f + 0.9f);
            }
        }
    }

    @Override
    protected List<AxisAlignedBB> getAabbs(boolean forCollision) {
        return aabbs;
    }

    @Override
    public void openInventory(EntityPlayer player) {
        if (!this.worldObj.isRemote) {
            this.usingPlayers.add(player);
            this.updateUsingPlayerCount();
        }
    }

    @Override
    public void closeInventory(EntityPlayer player) {
        if (!this.worldObj.isRemote) {
            this.usingPlayers.remove((Object)player);
            this.updateUsingPlayerCount();
        }
    }

    private void updateUsingPlayerCount() {
        this.usingPlayerCount = this.usingPlayers.size();
        IC2.network.get(true).updateTileEntityField(this, "usingPlayerCount");
    }

    @Override
    public List<String> getNetworkedFields() {
        List<String> ret = super.getNetworkedFields();
        ret.add("owner");
        ret.add("usingPlayerCount");
        return ret;
    }

    @Override
    public boolean wrenchCanRemove(EntityPlayer player) {
        if (!this.permitsAccess(player.getGameProfile())) {
            IC2.platform.messagePlayer(player, "This safe is owned by " + this.owner.getName(), new Object[0]);
            return false;
        }
        if (!this.contentSlot.isEmpty()) {
            IC2.platform.messagePlayer(player, "Can't wrench non-empty safe", new Object[0]);
            return false;
        }
        return true;
    }

    @Override
    public boolean permitsAccess(GameProfile profile) {
        return TileEntityPersonalChest.checkAccess(this, profile);
    }

    public static <T extends TileEntity> boolean checkAccess(T te, GameProfile profile) {
        if (profile == null) {
            return ((IPersonalBlock)te).getOwner() == null;
        }
        if (!te.getWorld().isRemote) {
            if (((IPersonalBlock)te).getOwner() == null) {
                ((IPersonalBlock)te).setOwner(profile);
                IC2.network.get(true).updateTileEntityField((TileEntity)te, "owner");
                return true;
            }
            if (te.getWorld().getMinecraftServer().getPlayerList().canSendCommands(profile)) {
                return true;
            }
        } else if (((IPersonalBlock)te).getOwner() == null) {
            return true;
        }
        return ((IPersonalBlock)te).getOwner().equals((Object)profile);
    }

    @Override
    public GameProfile getOwner() {
        return this.owner;
    }

    @Override
    public void setOwner(GameProfile owner) {
        this.owner = owner;
    }

    @Override
    protected boolean canEntityDestroy(Entity entity) {
        return false;
    }

    @Override
    protected boolean onActivated(EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!this.worldObj.isRemote && !this.permitsAccess(player.getGameProfile())) {
            IC2.platform.messagePlayer(player, "This safe is owned by " + this.getOwner().getName(), new Object[0]);
            return false;
        }
        return super.onActivated(player, hand, heldItem, side, hitX, hitY, hitZ);
    }

    public ContainerBase<TileEntityPersonalChest> getGuiContainer(EntityPlayer player) {
        return DynamicContainer.create(this, player, GuiParser.parse(this.teBlock));
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return DynamicGui.create(this, player, GuiParser.parse(this.teBlock));
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }

    public float getLidAngle(float partialTicks) {
        return Util.lerp(this.prevLidAngle, this.lidAngle, partialTicks) / 10.0f;
    }
}

