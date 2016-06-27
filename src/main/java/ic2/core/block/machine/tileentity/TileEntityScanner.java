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
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.machine.tileentity;

import ic2.api.network.INetworkClientTileEntityEventListener;
import ic2.api.recipe.IPatternStorage;
import ic2.core.ContainerBase;
import ic2.core.IHasGui;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.comp.Energy;
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumable;
import ic2.core.block.invslot.InvSlotConsumableId;
import ic2.core.block.invslot.InvSlotScannable;
import ic2.core.block.machine.container.ContainerScanner;
import ic2.core.block.machine.gui.GuiScanner;
import ic2.core.block.machine.tileentity.TileEntityElectricMachine;
import ic2.core.item.ItemCrystalMemory;
import ic2.core.ref.IItemModelProvider;
import ic2.core.ref.ItemName;
import ic2.core.util.StackUtil;
import ic2.core.uu.UuGraph;
import ic2.core.uu.UuIndex;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityScanner
extends TileEntityElectricMachine
implements IHasGui,
INetworkClientTileEntityEventListener {
    private ItemStack currentStack = null;
    private ItemStack pattern = null;
    private final int energyusecycle = 256;
    public int progress = 0;
    public final int duration = 3300;
    public final InvSlotConsumable inputSlot;
    public final InvSlot diskSlot;
    private State state = State.IDLE;
    public double patternUu;
    public double patternEu;

    public TileEntityScanner() {
        super(512000, 4);
        this.inputSlot = new InvSlotScannable(this, "input", 1);
        this.diskSlot = new InvSlotConsumableId((TileEntityInventory)this, "disk", InvSlot.Access.IO, 1, InvSlot.InvSide.ANY, new Item[]{ItemName.crystal_memory.getInstance()});
    }

    @Override
    protected void updateEntityServer() {
        super.updateEntityServer();
        boolean newActive = false;
        if (this.progress < 3300) {
            if (this.inputSlot.isEmpty() || this.currentStack != null && !StackUtil.checkItemEquality(this.currentStack, this.inputSlot.get())) {
                this.state = State.IDLE;
                this.reset();
            } else if (this.getPatternStorage() == null && this.diskSlot.isEmpty()) {
                this.state = State.NO_STORAGE;
                this.reset();
            } else if (this.energy.getEnergy() >= 256.0) {
                if (this.currentStack == null) {
                    this.currentStack = StackUtil.copyWithSize(this.inputSlot.get(), 1);
                }
                this.pattern = UuGraph.find(this.currentStack);
                if (this.pattern == null) {
                    this.state = State.FAILED;
                } else if (this.isPatternRecorded(this.pattern)) {
                    this.state = State.ALREADY_RECORDED;
                    this.reset();
                } else {
                    newActive = true;
                    this.state = State.SCANNING;
                    this.energy.useEnergy(256.0);
                    ++this.progress;
                    if (this.progress >= 3300) {
                        this.refreshInfo();
                        if (this.patternUu != Double.POSITIVE_INFINITY) {
                            this.state = State.COMPLETED;
                            this.inputSlot.consume(1, false, true);
                            this.markDirty();
                        } else {
                            this.state = State.FAILED;
                        }
                    }
                }
            } else {
                this.state = State.NO_ENERGY;
            }
        } else if (this.pattern == null) {
            this.state = State.IDLE;
            this.progress = 0;
        }
        this.setActive(newActive);
    }

    public void reset() {
        this.progress = 0;
        this.currentStack = null;
        this.pattern = null;
    }

    private boolean isPatternRecorded(ItemStack stack) {
        ItemStack crystalMemory;
        if (!this.diskSlot.isEmpty() && this.diskSlot.get().getItem() instanceof ItemCrystalMemory && StackUtil.checkItemEquality(((ItemCrystalMemory)(crystalMemory = this.diskSlot.get()).getItem()).readItemStack(crystalMemory), stack)) {
            return true;
        }
        IPatternStorage storage = this.getPatternStorage();
        if (storage == null) {
            return false;
        }
        for (ItemStack stored : storage.getPatterns()) {
            if (!StackUtil.checkItemEquality(stored, stack)) continue;
            return true;
        }
        return false;
    }

    private void record() {
        if (this.pattern == null || this.patternUu == Double.POSITIVE_INFINITY) {
            this.reset();
            return;
        }
        if (!this.savetoDisk(this.pattern)) {
            IPatternStorage storage = this.getPatternStorage();
            if (storage != null) {
                if (!storage.addPattern(this.pattern)) {
                    this.state = State.TRANSFER_ERROR;
                    return;
                }
            } else {
                this.state = State.TRANSFER_ERROR;
                return;
            }
        }
        this.reset();
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.progress = nbttagcompound.getInteger("progress");
        NBTTagCompound contentTag = nbttagcompound.getCompoundTag("currentStack");
        this.currentStack = ItemStack.loadItemStackFromNBT((NBTTagCompound)contentTag);
        contentTag = nbttagcompound.getCompoundTag("pattern");
        this.pattern = ItemStack.loadItemStackFromNBT((NBTTagCompound)contentTag);
        int stateIdx = nbttagcompound.getInteger("state");
        this.state = stateIdx < State.values().length ? State.values()[stateIdx] : State.IDLE;
        this.refreshInfo();
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        NBTTagCompound contentTag;
        super.writeToNBT(nbt);
        nbt.setInteger("progress", this.progress);
        if (this.currentStack != null) {
            contentTag = new NBTTagCompound();
            this.currentStack.writeToNBT(contentTag);
            nbt.setTag("currentStack", (NBTBase)contentTag);
        }
        if (this.pattern != null) {
            contentTag = new NBTTagCompound();
            this.pattern.writeToNBT(contentTag);
            nbt.setTag("pattern", (NBTBase)contentTag);
        }
        nbt.setInteger("state", this.state.ordinal());
        return nbt;
    }

    public ContainerBase<TileEntityScanner> getGuiContainer(EntityPlayer player) {
        return new ContainerScanner(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiScanner(new ContainerScanner(player, this));
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }

    public IPatternStorage getPatternStorage() {
        for (EnumFacing dir : EnumFacing.VALUES) {
            TileEntity target = this.worldObj.getTileEntity(this.pos.offset(dir));
            if (!(target instanceof IPatternStorage)) continue;
            return (IPatternStorage)target;
        }
        return null;
    }

    public boolean savetoDisk(ItemStack stack) {
        if (this.diskSlot.isEmpty() || stack == null) {
            return false;
        }
        if (this.diskSlot.get().getItem() instanceof ItemCrystalMemory) {
            ItemStack crystalMemory = this.diskSlot.get();
            ((ItemCrystalMemory)crystalMemory.getItem()).writecontentsTag(crystalMemory, stack);
            return true;
        }
        return false;
    }

    @Override
    public void onNetworkEvent(EntityPlayer player, int event) {
        switch (event) {
            case 0: {
                this.reset();
                break;
            }
            case 1: {
                if (this.progress < 3300) break;
                this.record();
            }
        }
    }

    private void refreshInfo() {
        if (this.pattern != null) {
            this.patternUu = UuIndex.instance.getInBuckets(this.pattern);
        }
    }

    public int getPercentageDone() {
        return 100 * this.progress / 3300;
    }

    public int getSubPercentageDoneScaled(int width) {
        return width * (100 * this.progress % 3300) / 3300;
    }

    public boolean isDone() {
        return this.progress >= 3300;
    }

    public State getState() {
        return this.state;
    }

    public static enum State {
        IDLE,
        SCANNING,
        COMPLETED,
        FAILED,
        NO_STORAGE,
        NO_ENERGY,
        TRANSFER_ERROR,
        ALREADY_RECORDED;
        

        private State() {
        }
    }

}

