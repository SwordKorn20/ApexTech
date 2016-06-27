/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.machine.tileentity;

import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.container.ContainerItemBuffer;
import ic2.core.block.machine.gui.GuiItemBuffer;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.IUpgradeItem;
import ic2.core.upgrade.UpgradableProperty;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityItemBuffer
extends TileEntityInventory
implements IHasGui,
IUpgradableBlock {
    public final InvSlot rightcontentSlot;
    public final InvSlot leftcontentSlot;
    public final InvSlotUpgrade upgradeSlot;
    private boolean tick = true;

    public TileEntityItemBuffer() {
        this.rightcontentSlot = new InvSlot(this, "rightcontent", InvSlot.Access.IO, 24, InvSlot.InvSide.SIDE);
        this.leftcontentSlot = new InvSlot(this, "leftcontent", InvSlot.Access.IO, 24, InvSlot.InvSide.NOTSIDE);
        this.upgradeSlot = new InvSlotUpgrade(this, "upgrade", 2);
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        ItemStack Upgradeleft = this.upgradeSlot.get(0);
        ItemStack Upgraderight = this.upgradeSlot.get(1);
        if (Upgradeleft != null && Upgraderight != null) {
            if (this.tick) {
                if (((IUpgradeItem)Upgradeleft.getItem()).onTick(Upgradeleft, this)) {
                    super.markDirty();
                }
            } else if (((IUpgradeItem)Upgraderight.getItem()).onTick(Upgraderight, this)) {
                super.markDirty();
            }
            this.tick = !this.tick;
        } else {
            if (Upgradeleft != null) {
                this.tick = true;
                if (((IUpgradeItem)Upgradeleft.getItem()).onTick(Upgradeleft, this)) {
                    super.markDirty();
                }
            }
            if (Upgraderight != null) {
                this.tick = false;
                if (((IUpgradeItem)Upgraderight.getItem()).onTick(Upgraderight, this)) {
                    super.markDirty();
                }
            }
        }
    }

    public ContainerBase<TileEntityItemBuffer> getGuiContainer(EntityPlayer player) {
        return new ContainerItemBuffer(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiItemBuffer(new ContainerItemBuffer(player, this));
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.ItemProducing);
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }

    @Override
    public double getEnergy() {
        return 40.0;
    }

    @Override
    public boolean useEnergy(double amount) {
        return true;
    }
}

