/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.machine.tileentity;

import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.Platform;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.Energy;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.container.ContainerSortingMachine;
import ic2.core.block.machine.gui.GuiSortingMachine;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.IUpgradeItem;
import ic2.core.upgrade.UpgradableProperty;
import ic2.core.util.StackUtil;
import java.util.EnumSet;
import java.util.Set;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntitySortingMachine
extends TileEntityElectricMachine
implements IHasGui,
INetworkClientTileEntityEventListener,
IUpgradableBlock {
    public final int defaultTier = 1;
    public final InvSlotUpgrade upgradeSlot;
    public final InvSlot buffer;
    private final ItemStack[][] filters;
    private int amount = 0;
    public EnumFacing defaultRoute = EnumFacing.DOWN;

    public TileEntitySortingMachine() {
        super(100000, 2, false);
        this.upgradeSlot = new InvSlotUpgrade(this, "upgrade", 3);
        this.buffer = new InvSlot(this, "Buffer", InvSlot.Access.I, 11);
        this.filters = new ItemStack[6][];
        for (int i = 0; i < this.filters.length; ++i) {
            this.filters[i] = new ItemStack[7];
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        NBTTagList filtersTag = nbt.getTagList("filters", 10);
        for (int i = 0; i < filtersTag.tagCount(); ++i) {
            ItemStack stack;
            NBTTagCompound filterTag = filtersTag.getCompoundTagAt(i);
            int index = filterTag.getByte("index") & 255;
            this.filters[index / 7][index % 7] = stack = ItemStack.loadItemStackFromNBT((NBTTagCompound)filterTag);
        }
        byte defaultRouteIdx = nbt.getByte("defaultroute");
        if (defaultRouteIdx >= 0 && defaultRouteIdx < EnumFacing.VALUES.length) {
            this.defaultRoute = EnumFacing.VALUES[defaultRouteIdx];
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        NBTTagList filtersTag = new NBTTagList();
        for (int i = 0; i < 42; ++i) {
            ItemStack stack = this.filters[i / 7][i % 7];
            if (stack == null) continue;
            NBTTagCompound contentTag = new NBTTagCompound();
            contentTag.setByte("index", (byte)i);
            stack.writeToNBT(contentTag);
            filtersTag.appendTag((NBTBase)contentTag);
        }
        nbt.setTag("filters", (NBTBase)filtersTag);
        nbt.setByte("defaultroute", (byte)this.defaultRoute.ordinal());
        return nbt;
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        block0 : for (int index = 0; index < this.buffer.size(); ++index) {
            if (this.energy.getEnergy() < 20.0) {
                return;
            }
            ItemStack stack = this.buffer.get(index);
            if (stack == null) continue;
            if (stack.stackSize <= 0) {
                this.buffer.put(index, null);
                continue;
            }
            block1 : for (StackUtil.AdjacentInv inv : StackUtil.getAdjacentInventories(this)) {
                if (inv.dir != this.defaultRoute) {
                    for (ItemStack filterStack : this.getFilterSlots(inv.dir)) {
                        if (filterStack == null || stack.stackSize < filterStack.stackSize || !StackUtil.checkItemEquality(filterStack, stack) || !this.energy.canUseEnergy(filterStack.stackSize * 20)) continue;
                        ItemStack transferStack = StackUtil.copyWithSize(stack, filterStack.stackSize);
                        this.amount = StackUtil.putInInventory(inv.te, inv.dir, transferStack, true);
                        if (this.amount != filterStack.stackSize) continue block1;
                        this.amount = StackUtil.putInInventory(inv.te, inv.dir, transferStack, false);
                        stack.stackSize -= this.amount;
                        this.energy.useEnergy(this.amount * 20);
                        if (stack.stackSize > 0) continue block1;
                        this.buffer.put(index, null);
                        continue block0;
                    }
                    continue;
                }
                boolean inFilter = false;
                ItemStack[][] arritemStack = this.filters;
                int n = arritemStack.length;
                for (int filterStack = 0; filterStack < n; ++filterStack) {
                    ItemStack[] sideFilters;
                    for (ItemStack filter : sideFilters = arritemStack[filterStack]) {
                        if (!StackUtil.checkItemEquality(filter, stack)) continue;
                        inFilter = true;
                    }
                }
                if (inFilter) continue;
                this.amount = StackUtil.putInInventory(inv.te, inv.dir, StackUtil.copyWithSize(stack, 1), false);
                if (this.amount <= 0) continue block0;
                stack.stackSize -= this.amount;
                this.energy.useEnergy(20.0);
                if (stack.stackSize > 0) continue block0;
                this.buffer.put(index, null);
                continue block0;
            }
        }
    }

    @Override
    public void onNetworkEvent(EntityPlayer player, int event) {
        if (event >= 0 && event <= 5) {
            this.defaultRoute = EnumFacing.VALUES[event];
        }
    }

    public ContainerBase<TileEntitySortingMachine> getGuiContainer(EntityPlayer player) {
        return new ContainerSortingMachine(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiSortingMachine(new ContainerSortingMachine(player, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.Transformer);
    }

    @Override
    public void markDirty() {
        super.markDirty();
        if (IC2.platform.isSimulating()) {
            this.setUpgradableBlock();
        }
    }

    public void setUpgradableBlock() {
        int extraTier = 0;
        for (int i = 0; i < this.upgradeSlot.size(); ++i) {
            ItemStack stack = this.upgradeSlot.get(i);
            if (stack == null || !(stack.getItem() instanceof IUpgradeItem)) continue;
            IUpgradeItem upgrade = (IUpgradeItem)stack.getItem();
            extraTier += upgrade.getExtraTier(stack, this) * stack.stackSize;
        }
        this.energy.setSinkTier(TileEntitySortingMachine.applyModifier(this.defaultTier, extraTier, 1.0));
    }

    private static int applyModifier(int base, int extra, double multiplier) {
        double ret = Math.round(((double)base + (double)extra) * multiplier);
        return ret > 2.147483647E9 ? Integer.MAX_VALUE : (int)ret;
    }

    @Override
    public double getEnergy() {
        return this.energy.getEnergy();
    }

    @Override
    public boolean useEnergy(double amount) {
        return this.energy.useEnergy(amount);
    }

    public ItemStack[] getFilterSlots(EnumFacing side) {
        return this.filters[side.ordinal()];
    }
}

