/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$MutableBlockPos
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.World
 */
package ic2.core.block.machine.tileentity;

import com.google.common.base.Predicate;
import ic2.api.item.ITerraformingBP;
import ic2.core.IC2;
import ic2.core.Platform;
import ic2.core.audio.AudioManager;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.Energy;
import ic2.core.block.invslot.InvSlotConsumableClass;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import ic2.core.util.Ic2BlockPos;
import ic2.core.util.StackUtil;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class TileEntityTerra
extends TileEntityElectricMachine {
    public int failedAttempts = 0;
    private BlockPos lastPos;
    public AudioSource audioSource;
    public int inactiveTicks = 0;
    public final InvSlotConsumableClass tfbpSlot;

    public TileEntityTerra() {
        super(100000, 4);
        this.tfbpSlot = new InvSlotConsumableClass(this, "tfbp", 1, ITerraformingBP.class);
    }

    @Override
    protected void onUnloaded() {
        if (IC2.platform.isRendering() && this.audioSource != null) {
            IC2.audioManager.removeSources(this);
            this.audioSource = null;
        }
        super.onUnloaded();
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        boolean newActive = false;
        ItemStack stack = this.tfbpSlot.get();
        if (stack != null) {
            ITerraformingBP tfbp = (ITerraformingBP)stack.getItem();
            if (this.energy.getEnergy() >= tfbp.getConsume(stack)) {
                BlockPos nextPos;
                newActive = true;
                if (this.lastPos != null) {
                    int range = tfbp.getRange(stack) / 10;
                    nextPos = new BlockPos(this.lastPos.getX() - this.worldObj.rand.nextInt(range + 1) + this.worldObj.rand.nextInt(range + 1), this.pos.getY(), this.lastPos.getZ() - this.worldObj.rand.nextInt(range + 1) + this.worldObj.rand.nextInt(range + 1));
                } else {
                    if (this.failedAttempts > 4) {
                        this.failedAttempts = 4;
                    }
                    int range = tfbp.getRange(stack) * (this.failedAttempts + 1) / 5;
                    nextPos = new BlockPos(this.pos.getX() - this.worldObj.rand.nextInt(range + 1) + this.worldObj.rand.nextInt(range + 1), this.pos.getY(), this.pos.getZ() - this.worldObj.rand.nextInt(range + 1) + this.worldObj.rand.nextInt(range + 1));
                }
                if (tfbp.terraform(stack, this.worldObj, nextPos)) {
                    this.energy.useEnergy(tfbp.getConsume(stack));
                    this.failedAttempts = 0;
                    this.lastPos = nextPos;
                } else {
                    this.energy.useEnergy(tfbp.getConsume(stack) / 10.0);
                    ++this.failedAttempts;
                    this.lastPos = null;
                }
            }
        }
        if (newActive) {
            this.inactiveTicks = 0;
            this.setActive(true);
        } else if (!newActive && this.getActive() && this.inactiveTicks++ > 30) {
            this.setActive(false);
        }
    }

    @Override
    public boolean onActivated(final EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!player.isSneaking() && !this.worldObj.isRemote) {
            if (this.ejectBlueprint()) {
                return true;
            }
            ItemStack stack = StackUtil.consumeFromPlayerHand(player, new Predicate<ItemStack>(){

                public boolean apply(ItemStack input) {
                    Item item = input.getItem();
                    return item instanceof ITerraformingBP && ((ITerraformingBP)item).canInsert(input, player, TileEntityTerra.this.worldObj, TileEntityTerra.this.pos);
                }
            }, 1);
            if (stack != null) {
                this.insertBlueprint(StackUtil.copyWithSize(stack, 1));
                return true;
            }
        }
        return super.onActivated(player, hand, heldItem, side, hitX, hitY, hitZ);
    }

    private boolean ejectBlueprint() {
        if (this.tfbpSlot.isEmpty()) {
            return false;
        }
        StackUtil.dropAsEntity(this.worldObj, this.pos, this.tfbpSlot.get());
        this.tfbpSlot.clear();
        return true;
    }

    private void insertBlueprint(ItemStack tfbp) {
        if (!this.tfbpSlot.isEmpty()) {
            throw new IllegalStateException("not empty");
        }
        this.tfbpSlot.put(tfbp);
    }

    public static BlockPos getFirstSolidBlockFrom(World world, BlockPos pos, int yOffset) {
        Ic2BlockPos ret = new Ic2BlockPos(pos.getX(), pos.getY() + yOffset, pos.getZ());
        while (ret.getY() >= 0) {
            if (world.isBlockNormalCube((BlockPos)ret, false)) {
                return new BlockPos((Vec3i)ret);
            }
            ret.moveDown();
        }
        return null;
    }

    public static BlockPos getFirstBlockFrom(World world, BlockPos pos, int yOffset) {
        BlockPos.MutableBlockPos ret = new BlockPos.MutableBlockPos(pos.getX(), pos.getY() + yOffset, pos.getZ());
        while (ret.getY() >= 0) {
            if (!world.isAirBlock((BlockPos)ret)) {
                return new BlockPos((Vec3i)ret);
            }
            ret.setPos(ret.getX(), ret.getY() - 1, ret.getZ());
        }
        return null;
    }

    public static boolean switchGround(World world, BlockPos pos, Block from, IBlockState to, boolean upwards) {
        BlockPos.MutableBlockPos cPos = new BlockPos.MutableBlockPos(pos.getX(), pos.getY(), pos.getZ());
        while (cPos.getY() >= 0) {
            Block block = world.getBlockState((BlockPos)cPos).getBlock();
            if (upwards && block != from || !upwards && block == from) break;
            cPos.setPos(cPos.getX(), cPos.getY() - 1, cPos.getZ());
        }
        if (upwards && cPos.getY() == pos.getY() || !upwards && cPos.getY() < 0) {
            return false;
        }
        world.setBlockState(upwards ? cPos.up() : new BlockPos((Vec3i)cPos), to);
        return true;
    }

    @Override
    public void onNetworkUpdate(String field) {
        if (field.equals("active")) {
            if (this.audioSource == null) {
                this.audioSource = IC2.audioManager.createSource(this, PositionSpec.Center, "Terraformers/TerraformerGenericloop.ogg", true, false, IC2.audioManager.getDefaultVolume());
            }
            if (this.getActive()) {
                if (this.audioSource != null) {
                    this.audioSource.play();
                }
            } else if (this.audioSource != null) {
                this.audioSource.stop();
            }
        }
        super.onNetworkUpdate(field);
    }

}

