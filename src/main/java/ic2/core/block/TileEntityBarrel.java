/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumFacing$Axis
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.world.World
 *  net.minecraftforge.fluids.Fluid
 *  net.minecraftforge.fluids.FluidRegistry
 *  net.minecraftforge.fluids.FluidStack
 *  org.apache.commons.lang3.mutable.MutableObject
 */
package ic2.core.block;

import ic2.core.block.BlockScaffold;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.state.IIdProvider;
import ic2.core.item.ItemBooze;
import ic2.core.item.type.CropResItemType;
import ic2.core.ref.BlockName;
import ic2.core.ref.IItemModelProvider;
import ic2.core.ref.ItemName;
import ic2.core.util.LiquidUtil;
import ic2.core.util.StackUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import org.apache.commons.lang3.mutable.MutableObject;

public class TileEntityBarrel
extends TileEntityBlock {
    private int type = 0;
    private int boozeAmount = 0;
    private int age = 0;
    private boolean opened;
    private byte hopsCount = 0;
    private byte wheatCount = 0;
    private byte solidRatio = 0;
    private byte hopsRatio = 0;
    private byte timeRatio = 0;

    public TileEntityBarrel() {
    }

    public TileEntityBarrel(int value) {
        this.type = ItemBooze.getTypeOfValue(value);
        if (this.type > 0) {
            this.boozeAmount = ItemBooze.getAmountOfValue(value);
        }
        if (this.type == 1) {
            this.opened = true;
            this.hopsRatio = (byte)ItemBooze.getHopsRatioOfBeerValue(value);
            this.solidRatio = (byte)ItemBooze.getSolidRatioOfBeerValue(value);
            this.timeRatio = (byte)ItemBooze.getTimeRatioOfBeerValue(value);
        }
        if (this.type == 2) {
            this.opened = false;
            this.age = this.timeNedForRum(this.boozeAmount) * ItemBooze.getProgressOfRumValue(value) / 100;
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.type = nbt.getByte("type");
        this.boozeAmount = nbt.getByte("waterCount");
        this.age = nbt.getInteger("age");
        this.opened = nbt.getBoolean("opened");
        if (this.type == 1) {
            if (!this.opened) {
                this.hopsCount = nbt.getByte("hopsCount");
                this.wheatCount = nbt.getByte("wheatCount");
            }
            this.solidRatio = nbt.getByte("solidRatio");
            this.hopsRatio = nbt.getByte("hopsRatio");
            this.timeRatio = nbt.getByte("timeRatio");
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setByte("type", (byte)this.type);
        nbt.setByte("waterCount", (byte)this.boozeAmount);
        nbt.setInteger("age", this.age);
        nbt.setBoolean("opened", this.opened);
        if (this.type == 1) {
            if (!this.opened) {
                nbt.setByte("hopsCount", this.hopsCount);
                nbt.setByte("wheatCount", this.wheatCount);
            }
            nbt.setByte("solidRatio", this.solidRatio);
            nbt.setByte("hopsRatio", this.hopsRatio);
            nbt.setByte("timeRatio", this.timeRatio);
        }
        return nbt;
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        if (!this.isEmpty() && !this.getActive()) {
            ++this.age;
            if (this.type == 1 && this.timeRatio < 5) {
                int x = this.timeRatio;
                if (x == 4) {
                    x += 2;
                }
                if ((double)this.age >= 24000.0 * Math.pow(3.0, x)) {
                    this.age = 0;
                    this.timeRatio = (byte)(this.timeRatio + 1);
                }
            }
        }
    }

    public boolean isEmpty() {
        return this.type == 0 || this.boozeAmount <= 0;
    }

    @Override
    protected boolean onActivated(EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (heldItem == null) {
            return false;
        }
        if (side.getAxis() != EnumFacing.Axis.Y && !this.getActive() && StackUtil.consumeFromPlayerHand(player, StackUtil.sameStack(ItemName.treetap.getItemStack()), 1) != null) {
            this.setActive(true);
            this.setFacing(side);
            return true;
        }
        if (!(this.type != 0 && this.type != 1 || this.opened)) {
            FluidStack fs;
            int minAmount = 1000;
            int space = (32 - this.boozeAmount) * minAmount;
            if (player.isSneaking()) {
                space = Math.min(space, minAmount);
            }
            MutableObject output = new MutableObject();
            if (space >= minAmount && (fs = LiquidUtil.drainContainer(heldItem, FluidRegistry.WATER, space, output, LiquidUtil.FluidContainerOutputMode.InPlacePreferred, true)) != null && fs.amount >= minAmount) {
                int amount = fs.amount / minAmount * minAmount;
                fs = LiquidUtil.drainContainer(heldItem, FluidRegistry.WATER, amount, output, LiquidUtil.FluidContainerOutputMode.InPlacePreferred, true);
                if (fs.amount != amount) {
                    return false;
                }
                if (output.getValue() != null && !StackUtil.storeInventoryItem((ItemStack)output.getValue(), player, false)) {
                    return false;
                }
                LiquidUtil.drainContainer(heldItem, FluidRegistry.WATER, amount, output, LiquidUtil.FluidContainerOutputMode.InPlacePreferred, false);
                this.type = 1;
                this.boozeAmount += amount / minAmount;
                return true;
            }
            if (heldItem.getItem() == Items.WHEAT) {
                this.type = 1;
                int wantgive = heldItem.stackSize;
                if (player.isSneaking()) {
                    wantgive = 1;
                }
                if (wantgive > 64 - this.wheatCount) {
                    wantgive = 64 - this.wheatCount;
                }
                if (wantgive <= 0) {
                    return false;
                }
                this.wheatCount = (byte)(this.wheatCount + wantgive);
                heldItem.stackSize -= wantgive;
                this.alterComposition();
                return true;
            }
            if (StackUtil.checkItemEquality(heldItem, ItemName.crop_res.getItemStack(CropResItemType.hops))) {
                this.type = 1;
                int wantgive = heldItem.stackSize;
                if (player.isSneaking()) {
                    wantgive = 1;
                }
                if (wantgive > 64 - this.hopsCount) {
                    wantgive = 64 - this.hopsCount;
                }
                if (wantgive <= 0) {
                    return false;
                }
                this.hopsCount = (byte)(this.hopsCount + wantgive);
                heldItem.stackSize -= wantgive;
                this.alterComposition();
                return true;
            }
        } else if ((this.type == 0 || this.type == 2) && heldItem.getItem() == Items.REEDS) {
            if (this.age > 600) {
                return false;
            }
            this.type = 2;
            int wantgive = heldItem.stackSize;
            if (player.isSneaking()) {
                wantgive = 1;
            }
            if (this.boozeAmount + wantgive > 32) {
                wantgive = 32 - this.boozeAmount;
            }
            if (wantgive <= 0) {
                return false;
            }
            this.boozeAmount += wantgive;
            heldItem.stackSize -= wantgive;
            return true;
        }
        return false;
    }

    @Override
    protected void onClicked(EntityPlayer player) {
        super.onClicked(player);
        if (this.getActive()) {
            if (!this.worldObj.isRemote) {
                StackUtil.dropAsEntity(this.worldObj, this.pos, ItemName.treetap.getItemStack());
            }
            this.setActive(false);
            this.drainLiquid(1);
            return;
        }
        if (!this.worldObj.isRemote) {
            StackUtil.dropAsEntity(this.worldObj, this.pos, new ItemStack(ItemName.barrel.getInstance(), 1, this.calculateMetaValue()));
        }
        this.worldObj.setBlockState(this.pos, BlockName.scaffold.getBlockState(BlockScaffold.ScaffoldType.wood));
    }

    private void alterComposition() {
        if (this.timeRatio <= 0) {
            this.age = 0;
        } else if (this.timeRatio == 1) {
            if (this.worldObj.rand.nextBoolean()) {
                this.timeRatio = 0;
            } else if (this.worldObj.rand.nextBoolean()) {
                this.timeRatio = 5;
            }
        } else if (this.timeRatio == 2) {
            if (this.worldObj.rand.nextBoolean()) {
                this.timeRatio = 5;
            }
        } else {
            this.timeRatio = 5;
        }
    }

    public boolean drainLiquid(int amount) {
        if (this.isEmpty()) {
            return false;
        }
        if (amount > this.boozeAmount) {
            return false;
        }
        this.open();
        if (this.type == 2) {
            int progress = this.age * 100 / this.timeNedForRum(this.boozeAmount);
            this.boozeAmount -= amount;
            this.age = progress / 100 * this.timeNedForRum(this.boozeAmount);
        } else {
            this.boozeAmount -= amount;
        }
        if (this.boozeAmount <= 0) {
            if (this.type == 1) {
                this.hopsCount = 0;
                this.wheatCount = 0;
                this.hopsRatio = 0;
                this.solidRatio = 0;
                this.timeRatio = 0;
            }
            this.type = 0;
            this.opened = false;
            this.boozeAmount = 0;
        }
        return true;
    }

    private void open() {
        if (this.opened) {
            return;
        }
        this.opened = true;
        if (this.type == 1) {
            float ratio = this.hopsCount <= 0 ? 0.0f : (float)this.hopsCount / (float)this.wheatCount;
            if (ratio <= 0.25f) {
                this.hopsRatio = 0;
            } else if (ratio <= 0.33333334f) {
                this.hopsRatio = 1;
            } else if (ratio <= 0.5f) {
                this.hopsRatio = 2;
            } else if (ratio < 2.0f) {
                this.hopsRatio = 3;
            } else {
                this.hopsRatio = (byte)Math.min(6.0, Math.floor(ratio) + 2.0);
                if (ratio >= 5.0f) {
                    this.timeRatio = 5;
                }
            }
            ratio = this.boozeAmount <= 0 ? Float.POSITIVE_INFINITY : (float)(this.hopsCount + this.wheatCount) / (float)this.boozeAmount;
            if (ratio <= 0.41666666f) {
                this.solidRatio = 0;
            } else if (ratio <= 0.5f) {
                this.solidRatio = 1;
            } else if (ratio < 1.0f) {
                this.solidRatio = 2;
            } else if (ratio == 1.0f) {
                this.solidRatio = 3;
            } else if (ratio < 2.0f) {
                this.solidRatio = 4;
            } else if (ratio < 2.4f) {
                this.solidRatio = 5;
            } else {
                this.solidRatio = 6;
                if (ratio >= 4.0f) {
                    this.timeRatio = 5;
                }
            }
        }
    }

    public int calculateMetaValue() {
        if (this.isEmpty()) {
            return 0;
        }
        if (this.type == 1) {
            this.open();
            int value = 0;
            value |= this.timeRatio;
            value <<= 3;
            value |= this.hopsRatio;
            value <<= 3;
            value |= this.solidRatio;
            value <<= 5;
            value |= this.boozeAmount - 1;
            value <<= 2;
            return value |= this.type;
        }
        if (this.type == 2) {
            this.open();
            int value = 0;
            int progress = this.age * 100 / this.timeNedForRum(this.boozeAmount);
            if (progress > 100) {
                progress = 100;
            }
            value |= progress;
            value <<= 5;
            value |= this.boozeAmount - 1;
            value <<= 2;
            return value |= this.type;
        }
        return 0;
    }

    public int timeNedForRum(int amount) {
        return (int)((double)(1200 * amount) * Math.pow(0.95, amount - 1));
    }

    @Override
    protected ItemStack getPickBlock(EntityPlayer player, RayTraceResult target) {
        return BlockName.scaffold.getItemStack(BlockScaffold.ScaffoldType.wood);
    }

    @Override
    protected List<ItemStack> getAuxDrops(int fortune) {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>(super.getAuxDrops(fortune));
        ret.add(ItemName.barrel.getItemStack());
        return ret;
    }
}

