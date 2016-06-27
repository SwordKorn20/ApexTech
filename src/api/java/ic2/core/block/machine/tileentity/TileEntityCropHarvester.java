/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.machine.tileentity;

import ic2.api.crops.CropCard;
import ic2.api.crops.ICropTile;
import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.Energy;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableId;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.container.ContainerCropHavester;
import ic2.core.block.machine.gui.GuiCropHavester;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import ic2.core.crop.TileEntityCrop;
import ic2.core.ref.IItemModelProvider;
import ic2.core.ref.ItemName;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.IUpgradeItem;
import ic2.core.upgrade.UpgradableProperty;
import ic2.core.util.StackUtil;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityCropHarvester
extends TileEntityElectricMachine
implements IHasGui,
IUpgradableBlock {
    public final InvSlot contentSlot;
    public final InvSlotUpgrade upgradeSlot;
    public final InvSlotConsumableId cropnalyzerSlot;
    public int scanX = -5;
    public int scanY = -1;
    public int scanZ = -5;

    public TileEntityCropHarvester() {
        super(10000, 1, false);
        this.contentSlot = new InvSlot(this, "content", InvSlot.Access.IO, 15);
        this.upgradeSlot = new InvSlotUpgrade(this, "upgrade", 1);
        this.cropnalyzerSlot = new InvSlotConsumableId((TileEntityInventory)this, "cropnalyzer", 7, new Item[]{ItemName.cropnalyzer.getInstance()});
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        ItemStack upgrade = this.upgradeSlot.get(0);
        if (upgrade != null && ((IUpgradeItem)upgrade.getItem()).onTick(upgrade, this)) {
            super.markDirty();
        }
        if (this.energy.getEnergy() >= 201.0) {
            this.scan();
        }
    }

    public void scan() {
        ItemStack cropnalyzer = this.cropnalyzerSlot.get(0);
        ++this.scanX;
        if (this.scanX > 5) {
            this.scanX = -5;
            ++this.scanZ;
            if (this.scanZ > 5) {
                this.scanZ = -5;
                ++this.scanY;
                if (this.scanY > 1) {
                    this.scanY = -1;
                }
            }
        }
        this.energy.useEnergy(1.0);
        TileEntity te = this.worldObj.getTileEntity(this.pos.add(this.scanX, this.scanY, this.scanZ));
        if (te instanceof TileEntityCrop && !this.isInvFull()) {
            TileEntityCrop crop = (TileEntityCrop)te;
            List<ItemStack> drops = null;
            if (cropnalyzer == null && crop.getCurrentSize() == crop.getCrop().getMaxSize()) {
                drops = crop.performHarvest();
            }
            if (cropnalyzer != null && crop.getCurrentSize() == crop.getCrop().getOptimalHarvestSize(crop)) {
                drops = crop.performHarvest();
            }
            if (drops != null) {
                for (ItemStack drop : drops) {
                    if (StackUtil.putInInventory((TileEntity)this, EnumFacing.WEST, drop, true) == 0) {
                        StackUtil.dropAsEntity(this.worldObj, this.pos, drop);
                    } else {
                        StackUtil.putInInventory((TileEntity)this, EnumFacing.WEST, drop, false);
                    }
                    this.energy.useEnergy(100.0);
                    if (cropnalyzer == null) continue;
                    this.energy.useEnergy(100.0);
                }
            }
        }
    }

    public ContainerBase<TileEntityCropHarvester> getGuiContainer(EntityPlayer player) {
        return new ContainerCropHavester(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiCropHavester(new ContainerCropHavester(player, this));
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.ItemProducing);
    }

    @Override
    public double getEnergy() {
        return this.energy.getEnergy();
    }

    @Override
    public boolean useEnergy(double amount) {
        return this.energy.useEnergy(amount);
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }

    private boolean isInvFull() {
        for (int i = 0; i < this.contentSlot.size(); ++i) {
            ItemStack stack = this.contentSlot.get(i);
            if (stack != null && stack.stackSize >= 64) continue;
            return false;
        }
        return true;
    }
}

