/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockChest
 *  net.minecraft.block.BlockDynamicLiquid
 *  net.minecraft.block.BlockStaticLiquid
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$MutableBlockPos
 *  net.minecraft.util.math.Vec3i
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.common.ForgeHooks
 *  net.minecraftforge.fluids.FluidStack
 *  net.minecraftforge.fluids.IFluidBlock
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.machine.tileentity;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItemManager;
import ic2.core.ContainerBase;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.Ic2Player;
import ic2.core.InvSlotConsumableBlock;
import ic2.core.Platform;
import ic2.core.audio.AudioManager;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.Energy;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumable;
import ic2.core.block.invslot.InvSlotConsumableId;
import ic2.core.block.invslot.InvSlotUpgrade;
import ic2.core.block.machine.BlockMiningPipe;
import ic2.core.block.machine.container.ContainerMiner;
import ic2.core.block.machine.gui.GuiMiner;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import ic2.core.block.machine.tileentity.TileEntityPump;
import ic2.core.block.state.IIdProvider;
import ic2.core.init.MainConfig;
import ic2.core.init.OreValues;
import ic2.core.item.tool.ItemScanner;
import ic2.core.ref.BlockName;
import ic2.core.ref.IBlockModelProvider;
import ic2.core.ref.IItemModelProvider;
import ic2.core.ref.ItemName;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.IUpgradeItem;
import ic2.core.upgrade.UpgradableProperty;
import ic2.core.util.ConfigUtil;
import ic2.core.util.Ic2BlockPos;
import ic2.core.util.LiquidUtil;
import ic2.core.util.StackUtil;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockStaticLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityMiner
extends TileEntityElectricMachine
implements IHasGui,
IUpgradableBlock {
    private Mode lastMode = Mode.None;
    public int progress = 0;
    private int scannedLevel = -1;
    private int scanRange = 0;
    private int lastX;
    private int lastZ;
    public boolean pumpMode = false;
    public boolean canProvideLiquid = false;
    public BlockPos liquidPos;
    private AudioSource audioSource;
    public final InvSlot buffer;
    public final InvSlotUpgrade upgradeSlot;
    public final InvSlotConsumable drillSlot;
    public final InvSlotConsumable pipeSlot;
    public final InvSlotConsumable scannerSlot;

    public TileEntityMiner() {
        super(1000, ConfigUtil.getInt(MainConfig.get(), "balance/minerDischargeTier"), false);
        this.drillSlot = new InvSlotConsumableId((TileEntityInventory)this, "drill", InvSlot.Access.IO, 1, InvSlot.InvSide.TOP, new Item[]{ItemName.drill.getInstance(), ItemName.diamond_drill.getInstance(), ItemName.iridium_drill.getInstance()});
        this.pipeSlot = new InvSlotConsumableBlock(this, "pipe", InvSlot.Access.IO, 1, InvSlot.InvSide.TOP);
        this.scannerSlot = new InvSlotConsumableId((TileEntityInventory)this, "scanner", InvSlot.Access.IO, 1, InvSlot.InvSide.BOTTOM, new Item[]{ItemName.scanner.getInstance(), ItemName.advanced_scanner.getInstance()});
        this.upgradeSlot = new InvSlotUpgrade(this, "upgrade", 1);
        this.buffer = new InvSlot(this, "buffer", InvSlot.Access.IO, 15, InvSlot.InvSide.SIDE);
    }

    @Override
    protected void onLoaded() {
        super.onLoaded();
        this.scannedLevel = -1;
        this.lastX = this.pos.getX();
        this.lastZ = this.pos.getZ();
        this.canProvideLiquid = false;
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
    public void readFromNBT(NBTTagCompound nbtTagCompound) {
        super.readFromNBT(nbtTagCompound);
        this.lastMode = Mode.values()[nbtTagCompound.getInteger("lastMode")];
        this.progress = nbtTagCompound.getInteger("progress");
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("lastMode", this.lastMode.ordinal());
        nbt.setInteger("progress", this.progress);
        return nbt;
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        this.chargeTools();
        for (int i = 0; i < this.upgradeSlot.size(); ++i) {
            ItemStack stack = this.upgradeSlot.get(i);
            if (stack == null || !(stack.getItem() instanceof IUpgradeItem) || !((IUpgradeItem)stack.getItem()).onTick(stack, this)) continue;
            super.markDirty();
        }
        if (this.work()) {
            this.markDirty();
            this.setActive(true);
        } else {
            this.setActive(false);
        }
    }

    private void chargeTools() {
        if (!this.scannerSlot.isEmpty()) {
            this.energy.useEnergy(ElectricItem.manager.charge(this.scannerSlot.get(), this.energy.getEnergy(), 2, false, false));
        }
        if (!this.drillSlot.isEmpty()) {
            this.energy.useEnergy(ElectricItem.manager.charge(this.drillSlot.get(), this.energy.getEnergy(), 1, false, false));
        }
    }

    private boolean work() {
        Ic2BlockPos operatingPos = this.getOperationPos();
        if (this.drillSlot.isEmpty()) {
            return this.withDrawPipe(operatingPos);
        }
        if (!operatingPos.isBelowMap()) {
            IBlockState state = this.worldObj.getBlockState((BlockPos)operatingPos);
            if (state != BlockName.mining_pipe.getBlockState(BlockMiningPipe.MiningPipeType.tip)) {
                if (operatingPos.getY() > 0) {
                    return this.digDown(operatingPos, state, false);
                }
                return false;
            }
            MineResult result = this.mineLevel(operatingPos.getY());
            if (result == MineResult.Done) {
                operatingPos.moveDown();
                state = this.worldObj.getBlockState((BlockPos)operatingPos);
                return this.digDown(operatingPos, state, true);
            }
            if (result == MineResult.Working) {
                return true;
            }
            return false;
        }
        return false;
    }

    private Ic2BlockPos getOperationPos() {
        Ic2BlockPos ret = new Ic2BlockPos((Vec3i)this.pos).moveDown();
        IBlockState pipeState = BlockName.mining_pipe.getBlockState(BlockMiningPipe.MiningPipeType.pipe);
        while (!ret.isBelowMap()) {
            IBlockState state = ret.getBlockState((IBlockAccess)this.worldObj);
            if (state != pipeState) {
                return ret;
            }
            ret.moveDown();
        }
        return ret;
    }

    private boolean withDrawPipe(Ic2BlockPos operatingPos) {
        if (this.lastMode != Mode.Withdraw) {
            this.lastMode = Mode.Withdraw;
            this.progress = 0;
        }
        if (operatingPos.isBelowMap() || this.worldObj.getBlockState((BlockPos)operatingPos) != BlockName.mining_pipe.getBlockState(BlockMiningPipe.MiningPipeType.tip)) {
            operatingPos.moveUp();
        }
        if (operatingPos.getY() != this.pos.getY() && this.energy.getEnergy() >= 3.0) {
            if (this.progress < 20) {
                this.energy.useEnergy(3.0);
                ++this.progress;
            } else {
                this.progress = 0;
                this.removePipe(operatingPos);
            }
            return true;
        }
        return false;
    }

    private void removePipe(Ic2BlockPos operatingPos) {
        ItemStack filler;
        Item fillerItem;
        this.worldObj.setBlockToAir((BlockPos)operatingPos);
        this.storeDrop(BlockName.mining_pipe.getItemStack(BlockMiningPipe.MiningPipeType.pipe));
        ItemStack pipe = this.pipeSlot.consume(1, true, false);
        if (pipe != null && !StackUtil.checkItemEquality(pipe, BlockName.mining_pipe.getItemStack(BlockMiningPipe.MiningPipeType.pipe)) && (fillerItem = (filler = this.pipeSlot.consume(1)).getItem()) instanceof ItemBlock) {
            ((ItemBlock)fillerItem).onItemUse(filler, (EntityPlayer)Ic2Player.get(this.worldObj), this.worldObj, operatingPos.up(), EnumHand.MAIN_HAND, EnumFacing.DOWN, 0.0f, 0.0f, 0.0f);
        }
    }

    private boolean digDown(Ic2BlockPos operatingPos, IBlockState state, boolean removeTipAbove) {
        ItemStack pipe = this.pipeSlot.consume(1, true, false);
        if (pipe == null || !StackUtil.checkItemEquality(pipe, BlockName.mining_pipe.getItemStack(BlockMiningPipe.MiningPipeType.pipe))) {
            return false;
        }
        if (operatingPos.isBelowMap()) {
            if (removeTipAbove) {
                this.worldObj.setBlockState((BlockPos)operatingPos.setY(0), BlockName.mining_pipe.getBlockState(BlockMiningPipe.MiningPipeType.pipe));
            }
            return false;
        }
        MineResult result = this.mineBlock(operatingPos, state);
        if (result == MineResult.Failed_Temp || result == MineResult.Failed_Perm) {
            if (removeTipAbove) {
                this.worldObj.setBlockState((BlockPos)operatingPos.moveUp(), BlockName.mining_pipe.getBlockState(BlockMiningPipe.MiningPipeType.pipe));
            }
            return false;
        }
        if (result == MineResult.Done) {
            if (removeTipAbove) {
                this.worldObj.setBlockState(operatingPos.up(), BlockName.mining_pipe.getBlockState(BlockMiningPipe.MiningPipeType.pipe));
            }
            this.pipeSlot.consume(1);
            this.worldObj.setBlockState((BlockPos)operatingPos, BlockName.mining_pipe.getBlockState(BlockMiningPipe.MiningPipeType.tip));
        }
        return true;
    }

    private MineResult mineLevel(int y) {
        if (this.scannerSlot.isEmpty()) {
            return MineResult.Done;
        }
        if (this.scannedLevel != y) {
            this.scanRange = ((ItemScanner)this.scannerSlot.get().getItem()).startLayerScan(this.scannerSlot.get());
        }
        if (this.scanRange > 0) {
            this.scannedLevel = y;
            BlockPos.MutableBlockPos target = new BlockPos.MutableBlockPos();
            Ic2Player player = Ic2Player.get(this.worldObj);
            for (int x = this.pos.getX() - this.scanRange; x <= this.pos.getX() + this.scanRange; ++x) {
                for (int z = this.pos.getZ() - this.scanRange; z <= this.pos.getZ() + this.scanRange; ++z) {
                    LiquidUtil.LiquidData liquid;
                    target.setPos(x, y, z);
                    IBlockState state = this.worldObj.getBlockState((BlockPos)target);
                    boolean isValidTarget = false;
                    if ((OreValues.get(StackUtil.getDrops((IBlockAccess)this.worldObj, (BlockPos)target, state, 0)) > 0 || OreValues.get(StackUtil.getPickStack(this.worldObj, (BlockPos)target, state, player)) > 0) && this.canMine((BlockPos)target, state)) {
                        isValidTarget = true;
                    } else if (this.pumpMode && (liquid = LiquidUtil.getLiquid(this.worldObj, (BlockPos)target)) != null && this.canPump((BlockPos)target)) {
                        isValidTarget = true;
                    }
                    if (!isValidTarget) continue;
                    MineResult result = this.mineTowards((BlockPos)target);
                    if (result == MineResult.Done) {
                        return MineResult.Working;
                    }
                    if (result == MineResult.Failed_Perm) continue;
                    return result;
                }
            }
            return MineResult.Done;
        }
        return MineResult.Failed_Temp;
    }

    private MineResult mineTowards(BlockPos dst) {
        int dx = Math.abs(dst.getX() - this.pos.getX());
        int sx = this.pos.getX() < dst.getX() ? 1 : -1;
        int dz = - Math.abs(dst.getZ() - this.pos.getZ());
        int sz = this.pos.getZ() < dst.getZ() ? 1 : -1;
        int err = dx + dz;
        BlockPos.MutableBlockPos target = new BlockPos.MutableBlockPos();
        int cx = this.pos.getX();
        int cz = this.pos.getZ();
        while (cx != dst.getX() || cz != dst.getZ()) {
            LiquidUtil.LiquidData liquid;
            boolean isCurrentPos = cx == this.lastX && cz == this.lastZ;
            int e2 = 2 * err;
            if (e2 > dz) {
                err += dz;
                cx += sx;
            } else if (e2 < dx) {
                err += dx;
                cz += sz;
            }
            target.setPos(cx, dst.getY(), cz);
            IBlockState state = this.worldObj.getBlockState((BlockPos)target);
            boolean isBlocking = false;
            if (isCurrentPos) {
                isBlocking = true;
            } else if (!state.getBlock().isAir(state, (IBlockAccess)this.worldObj, (BlockPos)target) && ((liquid = LiquidUtil.getLiquid(this.worldObj, (BlockPos)target)) == null || liquid.isSource || this.pumpMode && this.canPump((BlockPos)target))) {
                isBlocking = true;
            }
            if (!isBlocking) continue;
            MineResult result = this.mineBlock((BlockPos)target, state);
            if (result == MineResult.Done) {
                this.lastX = cx;
                this.lastZ = cz;
            }
            return result;
        }
        this.lastX = this.pos.getX();
        this.lastZ = this.pos.getZ();
        return MineResult.Done;
    }

    private MineResult mineBlock(BlockPos target, IBlockState state) {
        int duration;
        int energyPerTick;
        Mode mode;
        Block block = state.getBlock();
        boolean isAirBlock = true;
        if (!block.isAir(state, (IBlockAccess)this.worldObj, target)) {
            isAirBlock = false;
            LiquidUtil.LiquidData liquidData = LiquidUtil.getLiquid(this.worldObj, target);
            if (liquidData != null) {
                if (liquidData.isSource || this.pumpMode && this.canPump(target)) {
                    this.liquidPos = new BlockPos((Vec3i)target);
                    this.canProvideLiquid = true;
                    return this.pumpMode ? MineResult.Failed_Temp : MineResult.Failed_Perm;
                }
            } else if (!this.canMine(target, state)) {
                return MineResult.Failed_Perm;
            }
        }
        this.canProvideLiquid = false;
        if (isAirBlock) {
            mode = Mode.MineAir;
            energyPerTick = 3;
            duration = 20;
        } else if (this.drillSlot.get().getItem() == ItemName.drill.getInstance()) {
            mode = Mode.MineDrill;
            energyPerTick = 6;
            duration = 200;
        } else if (this.drillSlot.get().getItem() == ItemName.diamond_drill.getInstance()) {
            mode = Mode.MineDDrill;
            energyPerTick = 20;
            duration = 50;
        } else if (this.drillSlot.get().getItem() == ItemName.iridium_drill.getInstance()) {
            mode = Mode.MineIDrill;
            energyPerTick = 200;
            duration = 20;
        } else {
            throw new IllegalStateException("invalid drill: " + (Object)this.drillSlot.get());
        }
        if (this.lastMode != mode) {
            this.lastMode = mode;
            this.progress = 0;
        }
        if (this.progress < duration) {
            if (this.energy.useEnergy(energyPerTick)) {
                ++this.progress;
                return MineResult.Working;
            }
        } else if (isAirBlock || this.harvestBlock(target, state)) {
            this.progress = 0;
            return MineResult.Done;
        }
        return MineResult.Failed_Temp;
    }

    private boolean harvestBlock(BlockPos target, IBlockState state) {
        int energyCost = 2 * (this.pos.getY() - target.getY());
        if (this.energy.getEnergy() < (double)energyCost) {
            return false;
        }
        if (this.drillSlot.get().getItem() == ItemName.drill.getInstance()) {
            if (!ElectricItem.manager.use(this.drillSlot.get(), 50.0, null)) {
                return false;
            }
        } else if (this.drillSlot.get().getItem() == ItemName.diamond_drill.getInstance()) {
            if (!ElectricItem.manager.use(this.drillSlot.get(), 80.0, null)) {
                return false;
            }
        } else if (this.drillSlot.get().getItem() == ItemName.iridium_drill.getInstance()) {
            if (!ElectricItem.manager.use(this.drillSlot.get(), 800.0, null)) {
                return false;
            }
        } else {
            throw new IllegalStateException("invalid drill: " + (Object)this.drillSlot.get());
        }
        this.energy.useEnergy(energyCost);
        List drops = state.getBlock().getDrops((IBlockAccess)this.worldObj, target, state, this.lastMode == Mode.MineIDrill ? 3 : 0);
        if (drops != null) {
            for (ItemStack drop : drops) {
                this.storeDrop(drop);
            }
        }
        this.worldObj.setBlockToAir(target);
        return true;
    }

    private void storeDrop(ItemStack stack) {
        if (StackUtil.putInInventory((TileEntity)this, EnumFacing.WEST, stack, true) == 0) {
            StackUtil.dropAsEntity(this.worldObj, this.pos, stack);
        } else {
            StackUtil.putInInventory((TileEntity)this, EnumFacing.WEST, stack, false);
        }
    }

    public boolean canPump(BlockPos target) {
        return false;
    }

    public boolean canMine(BlockPos target, IBlockState state) {
        Block block = state.getBlock();
        if (block.isAir(state, (IBlockAccess)this.worldObj, target)) {
            return true;
        }
        if (block == BlockName.mining_pipe.getInstance() || block == Blocks.CHEST) {
            return false;
        }
        if (block instanceof IFluidBlock && this.isPumpConnected(target)) {
            return true;
        }
        if ((block == Blocks.WATER || block == Blocks.FLOWING_WATER || block == Blocks.LAVA || block == Blocks.FLOWING_LAVA) && this.isPumpConnected(target)) {
            return true;
        }
        if (state.getBlockHardness(this.worldObj, target) < 0.0f) {
            return false;
        }
        if (block.canCollideCheck(state, false) && state.getMaterial().isToolNotRequired()) {
            return true;
        }
        if (block == Blocks.WEB) {
            return true;
        }
        if (!this.drillSlot.isEmpty()) {
            return ForgeHooks.canToolHarvestBlock((IBlockAccess)this.worldObj, (BlockPos)target, (ItemStack)this.drillSlot.get()) || this.drillSlot.get().canHarvestBlock(state);
        }
        return false;
    }

    public boolean isPumpConnected(BlockPos target) {
        for (EnumFacing dir : EnumFacing.VALUES) {
            TileEntity te = this.worldObj.getTileEntity(this.pos.offset(dir));
            if (!(te instanceof TileEntityPump) || ((TileEntityPump)te).pump(target, true, this) == null) continue;
            return true;
        }
        return false;
    }

    public boolean isAnyPumpConnected() {
        for (EnumFacing dir : EnumFacing.VALUES) {
            TileEntity te = this.worldObj.getTileEntity(this.pos.offset(dir));
            if (!(te instanceof TileEntityPump)) continue;
            return true;
        }
        return false;
    }

    public ContainerBase<TileEntityMiner> getGuiContainer(EntityPlayer player) {
        return new ContainerMiner(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiMiner(new ContainerMiner(player, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }

    @Override
    public void onNetworkUpdate(String field) {
        if (field.equals("active")) {
            if (this.audioSource == null) {
                this.audioSource = IC2.audioManager.createSource(this, PositionSpec.Center, "Machines/MinerOp.ogg", true, false, IC2.audioManager.getDefaultVolume());
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

    @Override
    public double getEnergy() {
        return this.energy.getEnergy();
    }

    @Override
    public boolean useEnergy(double amount) {
        return this.energy.useEnergy(amount);
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.ItemConsuming, UpgradableProperty.ItemProducing);
    }

    static enum MineResult {
        Working,
        Done,
        Failed_Temp,
        Failed_Perm;
        

        private MineResult() {
        }
    }

    static enum Mode {
        None,
        Withdraw,
        MineAir,
        MineDrill,
        MineDDrill,
        MineIDrill;
        

        private Mode() {
        }
    }

}

