/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockDynamicLiquid
 *  net.minecraft.block.BlockStaticLiquid
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$MutableBlockPos
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.fluids.BlockFluidClassic
 *  net.minecraftforge.fluids.IFluidBlock
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.machine.tileentity;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItemManager;
import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.Energy;
import ic2.core.block.comp.Redstone;
import ic2.core.block.comp.TileEntityComponent;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableId;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.container.ContainerAdvMiner;
import ic2.core.block.machine.gui.GuiAdvMiner;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import ic2.core.init.MainConfig;
import ic2.core.init.OreValues;
import ic2.core.item.tool.ItemScanner;
import ic2.core.item.tool.ItemScannerAdv;
import ic2.core.ref.IItemModelProvider;
import ic2.core.ref.ItemName;
import ic2.core.ref.TeBlock;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.UpgradableProperty;
import ic2.core.util.ConfigUtil;
import ic2.core.util.StackUtil;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityAdvMiner
extends TileEntityElectricMachine
implements IHasGui,
INetworkClientTileEntityEventListener,
IUpgradableBlock {
    private int blockScanCount;
    private int maxBlockScanCount;
    public final int defaultTier;
    public final int workTick;
    public boolean blacklist = true;
    public boolean silkTouch = false;
    public boolean redstonePowered = false;
    public int energyConsume;
    private BlockPos mineTarget;
    private short ticker = 0;
    public final InvSlotConsumableId scannerSlot;
    public final InvSlotUpgrade upgradeSlot;
    public final InvSlot filterSlot;
    protected final Redstone redstone;

    public TileEntityAdvMiner() {
        super(4000000, 3);
        this.scannerSlot = new InvSlotConsumableId((TileEntityInventory)this, "scanner", InvSlot.Access.IO, 1, InvSlot.InvSide.BOTTOM, new Item[]{ItemName.scanner.getInstance(), ItemName.advanced_scanner.getInstance()});
        this.upgradeSlot = new InvSlotUpgrade(this, "upgrade", 4);
        this.filterSlot = new InvSlot(this, "list", null, 15);
        this.energyConsume = 512;
        this.defaultTier = 3;
        this.workTick = 20;
        this.redstone = this.addComponent(new Redstone(this));
    }

    @Override
    protected void onLoaded() {
        super.onLoaded();
        if (!this.worldObj.isRemote) {
            this.setUpgradestat();
        }
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        if (nbt.hasKey("mineTargetX")) {
            this.mineTarget = new BlockPos(nbt.getInteger("mineTargetX"), nbt.getInteger("mineTargetY"), nbt.getInteger("mineTargetZ"));
        }
        this.blacklist = nbt.getBoolean("blacklist");
        this.silkTouch = nbt.getBoolean("silkTouch");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        if (this.mineTarget != null) {
            nbt.setInteger("mineTargetX", this.mineTarget.getX());
            nbt.setInteger("mineTargetY", this.mineTarget.getY());
            nbt.setInteger("mineTargetZ", this.mineTarget.getZ());
        }
        nbt.setBoolean("blacklist", this.blacklist);
        nbt.setBoolean("silkTouch", this.silkTouch);
        return nbt;
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        this.chargeTool();
        this.setUpgradestat();
        if (this.work()) {
            this.markDirty();
            if (!this.getActive()) {
                this.setActive(true);
            }
        } else if (this.getActive()) {
            this.setActive(false);
        }
    }

    private boolean work() {
        if (this.energy.getEnergy() < (double)this.energyConsume) {
            return false;
        }
        if (this.redstone.hasRedstoneInput()) {
            return false;
        }
        if (this.mineTarget != null && this.mineTarget.getY() < 0) {
            return false;
        }
        if (this.scannerSlot.isEmpty()) {
            return false;
        }
        if (this.scannerSlot.get().getItem() instanceof ItemScanner && !((ItemScanner)this.scannerSlot.get().getItem()).haveChargeforScan(this.scannerSlot.get())) {
            return false;
        }
        this.ticker = (short)(this.ticker + 1);
        if (this.ticker != this.workTick) {
            return true;
        }
        this.ticker = 0;
        int range = this.scannerSlot.get().getItem() instanceof ItemScannerAdv ? 32 : (this.scannerSlot.get().getItem() instanceof ItemScanner ? 16 : 0);
        if (this.mineTarget == null) {
            this.mineTarget = new BlockPos(this.pos.getX() - range - 1, this.pos.getY() - 1, this.pos.getZ() - range);
            if (this.mineTarget.getY() < 0) {
                return false;
            }
        }
        this.blockScanCount = this.maxBlockScanCount;
        BlockPos.MutableBlockPos scanPos = new BlockPos.MutableBlockPos(this.mineTarget.getX(), this.mineTarget.getY(), this.mineTarget.getZ());
        while (this.blockScanCount > 0) {
            IBlockState state;
            Block block;
            if (scanPos.getX() < this.pos.getX() + range) {
                scanPos = new BlockPos.MutableBlockPos(scanPos.getX() + 1, scanPos.getY(), scanPos.getZ());
            } else if (scanPos.getZ() < this.pos.getZ() + range) {
                scanPos = new BlockPos.MutableBlockPos(this.pos.getX() - range, scanPos.getY(), scanPos.getZ() + 1);
            } else {
                scanPos = new BlockPos.MutableBlockPos(this.pos.getX() - range, scanPos.getY() - 1, this.pos.getZ() - range);
                if (scanPos.getY() < 0) {
                    this.mineTarget = new BlockPos((Vec3i)scanPos);
                    return true;
                }
            }
            if (!this.scannerSlot.isEmpty()) {
                ElectricItem.manager.discharge(this.scannerSlot.get(), 64.0, Integer.MAX_VALUE, true, true, false);
            }
            if (!(block = (state = this.worldObj.getBlockState((BlockPos)scanPos)).getBlock()).isAir(state, (IBlockAccess)this.worldObj, (BlockPos)scanPos) && this.canMine((BlockPos)scanPos, block, state)) {
                this.mineTarget = new BlockPos((Vec3i)scanPos);
                this.doMine(this.mineTarget, block, state);
                break;
            }
            this.mineTarget = new BlockPos((Vec3i)scanPos);
            --this.blockScanCount;
        }
        return true;
    }

    private void chargeTool() {
        if (!this.scannerSlot.isEmpty()) {
            this.energy.useEnergy(ElectricItem.manager.charge(this.scannerSlot.get(), this.energy.getEnergy(), 2, false, false));
        }
    }

    public void doMine(BlockPos pos, Block block, IBlockState state) {
        StackUtil.distributeDrops(this, StackUtil.getDrops((IBlockAccess)this.worldObj, pos, state, null, 0, this.silkTouch));
        this.worldObj.setBlockToAir(pos);
        this.energy.useEnergy(this.energyConsume);
    }

    public boolean canMine(BlockPos pos, Block block, IBlockState state) {
        if (block instanceof IFluidBlock || block instanceof BlockFluidClassic || block instanceof BlockStaticLiquid || block instanceof BlockDynamicLiquid) {
            return false;
        }
        if (state.getBlockHardness(this.worldObj, pos) < 0.0f) {
            return false;
        }
        List<ItemStack> drops = StackUtil.getDrops((IBlockAccess)this.worldObj, pos, state, null, 0, this.silkTouch);
        if (drops.isEmpty()) {
            return false;
        }
        if (block.hasTileEntity(state) && OreValues.get(drops) <= 0) {
            return false;
        }
        if (this.blacklist) {
            for (ItemStack drop : drops) {
                for (ItemStack filter : this.filterSlot) {
                    if (!StackUtil.checkItemEquality(drop, filter)) continue;
                    return false;
                }
            }
            return true;
        }
        for (ItemStack drop : drops) {
            for (ItemStack filter : this.filterSlot) {
                if (!StackUtil.checkItemEquality(drop, filter)) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public void onNetworkEvent(EntityPlayer player, int event) {
        switch (event) {
            case 0: {
                if (this.getActive()) break;
                this.mineTarget = null;
                break;
            }
            case 1: {
                if (this.getActive()) break;
                this.blacklist = !this.blacklist;
                break;
            }
            case 2: {
                if (this.getActive()) break;
                this.silkTouch = !this.silkTouch;
            }
        }
    }

    public void setUpgradestat() {
        this.upgradeSlot.onChanged();
        this.energy.setSinkTier(TileEntityAdvMiner.applyModifier(this.defaultTier, this.upgradeSlot.extraTier, 1.0));
        this.maxBlockScanCount = 5 * (this.upgradeSlot.augmentation + 1);
    }

    private static int applyModifier(int base, int extra, double multiplier) {
        double ret = Math.round(((double)base + (double)extra) * multiplier);
        return ret > 2.147483647E9 ? Integer.MAX_VALUE : (int)ret;
    }

    public ContainerBase<TileEntityAdvMiner> getGuiContainer(EntityPlayer player) {
        return new ContainerAdvMiner(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiAdvMiner(new ContainerAdvMiner(player, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }

    @Override
    public double getEnergy() {
        return this.energy.getEnergy();
    }

    @Override
    public boolean useEnergy(double amount) {
        return this.energy.useEnergy(amount);
    }

    public BlockPos getMineTarget() {
        return this.mineTarget;
    }

    @Override
    public void onPlaced(ItemStack stack, EntityLivingBase placer, EnumFacing facing) {
        super.onPlaced(stack, placer, facing);
        if (!this.worldObj.isRemote) {
            NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
            this.energy.addEnergy(nbt.getDouble("energy"));
        }
    }

    @Override
    protected ItemStack adjustDrop(ItemStack drop, boolean wrench) {
        double retainedRatio;
        drop = super.adjustDrop(drop, wrench);
        if ((wrench || this.teBlock.defaultDrop == TeBlock.DefaultDrop.Self) && (retainedRatio = ConfigUtil.getDouble(MainConfig.get(), "balance/energyRetainedInStorageBlockDrops")) > 0.0) {
            NBTTagCompound nbt = StackUtil.getOrCreateNbtData(drop);
            nbt.setDouble("energy", this.energy.getEnergy() * retainedRatio);
        }
        return drop;
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.Augmentable, UpgradableProperty.RedstoneSensitive, UpgradableProperty.Transformer);
    }
}

