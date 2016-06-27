/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
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
package ic2.core.block.reactor.tileentity;

import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.Energy;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableItemStack;
import ic2.core.block.invslot.InvSlotReactor;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import ic2.core.block.reactor.tileentity.TileEntityNuclearReactorElectric;
import ic2.core.block.reactor.tileentity.TileEntityReactorChamberElectric;
import ic2.core.gui.dynamic.DynamicContainer;
import ic2.core.gui.dynamic.DynamicGui;
import ic2.core.gui.dynamic.GuiParser;
import ic2.core.item.reactor.ItemReactorCondensator;
import ic2.core.ref.TeBlock;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.IUpgradeItem;
import ic2.core.upgrade.UpgradableProperty;
import ic2.core.util.StackUtil;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.block.Block;
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

public abstract class TileEntityAbstractRCI
extends TileEntityElectricMachine
implements IUpgradableBlock,
IHasGui {
    private TileEntityNuclearReactorElectric reactor;
    private final ItemStack target;
    private final double energyPerOperation = 1000.0;
    public final InvSlotConsumableItemStack inputSlot;
    public final InvSlotUpgrade upgradeSlot;

    public TileEntityAbstractRCI(ItemStack target, ItemStack coolant) {
        super(48000, 2);
        this.target = target;
        this.inputSlot = new InvSlotConsumableItemStack((TileEntityInventory)this, "input", InvSlot.Access.I, 9, InvSlot.InvSide.ANY, coolant);
        this.upgradeSlot = new InvSlotUpgrade(this, "upgrade", 4);
    }

    @Override
    protected void onLoaded() {
        super.onLoaded();
        if (!this.worldObj.isRemote) {
            this.updateEnergyFacings();
        }
        this.updateReactor();
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        boolean needsInvUpdate = false;
        if (!this.inputSlot.isEmpty() && this.energy.getEnergy() >= 1000.0 && this.reactor != null) {
            this.setActive(true);
        } else {
            this.setActive(false);
        }
        if (this.getActive()) {
            for (ItemStack comp : this.reactor.reactorSlot) {
                ItemReactorCondensator cond;
                if (comp == null || !StackUtil.checkItemEquality(comp, this.target) || (cond = (ItemReactorCondensator)comp.getItem()).getDurabilityForDisplay(comp) <= 0.85 || this.inputSlot.consume(1) == null || !this.energy.useEnergy(1000.0)) continue;
                cond.setCustomDamage(comp, 0);
                needsInvUpdate = true;
            }
        }
        for (int i = 0; i < this.upgradeSlot.size(); ++i) {
            ItemStack stack = this.upgradeSlot.get(i);
            if (stack == null || !(stack.getItem() instanceof IUpgradeItem) || !((IUpgradeItem)stack.getItem()).onTick(stack, this)) continue;
            needsInvUpdate = true;
        }
        if (needsInvUpdate) {
            super.markDirty();
        }
    }

    @Override
    protected void onNeighborChange(Block neighbor) {
        super.onNeighborChange(neighbor);
        this.updateEnergyFacings();
        this.updateReactor();
    }

    @Override
    public void setFacing(EnumFacing facing) {
        super.setFacing(facing);
        this.updateEnergyFacings();
        this.updateReactor();
    }

    public void updateEnergyFacings() {
        HashSet<EnumFacing> ret = new HashSet<EnumFacing>();
        for (EnumFacing facing : EnumFacing.VALUES) {
            TileEntity te = this.worldObj.getTileEntity(this.pos.offset(facing));
            if (te instanceof TileEntityNuclearReactorElectric || te instanceof TileEntityReactorChamberElectric) continue;
            ret.add(facing);
        }
        this.energy.setDirections(ret, Collections.<EnumFacing>emptySet());
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.ItemConsuming);
    }

    @Override
    public double getEnergy() {
        return 0.0;
    }

    @Override
    public boolean useEnergy(double amount) {
        return false;
    }

    public ContainerBase<TileEntityAbstractRCI> getGuiContainer(EntityPlayer player) {
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

    private void updateReactor() {
        if (!this.worldObj.isAreaLoaded(this.pos, 2)) {
            this.reactor = null;
            return;
        }
        TileEntity tileEntity = this.worldObj.getTileEntity(this.pos.offset(this.getFacing().getOpposite()));
        if (tileEntity instanceof TileEntityNuclearReactorElectric) {
            this.reactor = (TileEntityNuclearReactorElectric)tileEntity;
            return;
        }
        if (tileEntity instanceof TileEntityReactorChamberElectric) {
            this.reactor = ((TileEntityReactorChamberElectric)tileEntity).getReactorInstance();
            return;
        }
        this.reactor = null;
    }
}

