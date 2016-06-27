/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.nbt.NBTTagList
 *  net.minecraft.util.EnumFacing
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
import ic2.core.block.invslot.InvSlot;
import ic2.core.block.invslot.InvSlotConsumableId;
import ic2.core.block.machine.container.ContainerPatternStorage;
import ic2.core.block.machine.gui.GuiPatternStorage;
import ic2.core.item.ItemCrystalMemory;
import ic2.core.ref.IItemModelProvider;
import ic2.core.ref.ItemName;
import ic2.core.ref.TeBlock;
import ic2.core.util.StackUtil;
import ic2.core.uu.UuIndex;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityPatternStorage
extends TileEntityInventory
implements IHasGui,
INetworkClientTileEntityEventListener,
IPatternStorage {
    public final InvSlotConsumableId diskSlot;
    private final List<ItemStack> patterns = new ArrayList<ItemStack>();
    public int index = 0;
    public int maxIndex;
    public ItemStack pattern;
    public double patternUu;
    public double patternEu;

    public TileEntityPatternStorage() {
        this.diskSlot = new InvSlotConsumableId((TileEntityInventory)this, "SaveSlot", InvSlot.Access.IO, 1, InvSlot.InvSide.ANY, new Item[]{ItemName.crystal_memory.getInstance()});
    }

    @Override
    public void readFromNBT(NBTTagCompound nbttagcompound) {
        super.readFromNBT(nbttagcompound);
        this.readContents(nbttagcompound);
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        this.writeContentsAsNbtList(nbt);
        return nbt;
    }

    @Override
    public void onPlaced(ItemStack stack, EntityLivingBase placer, EnumFacing facing) {
        super.onPlaced(stack, placer, facing);
        if (!this.worldObj.isRemote) {
            NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
            this.readContents(nbt);
        }
    }

    @Override
    protected ItemStack adjustDrop(ItemStack drop, boolean wrench) {
        drop = super.adjustDrop(drop, wrench);
        if (wrench || this.teBlock.defaultDrop == TeBlock.DefaultDrop.Self) {
            NBTTagCompound nbt = StackUtil.getOrCreateNbtData(drop);
            this.writeContentsAsNbtList(nbt);
        }
        return drop;
    }

    public void readContents(NBTTagCompound nbt) {
        NBTTagList patternList = nbt.getTagList("patterns", 10);
        for (int i = 0; i < patternList.tagCount(); ++i) {
            NBTTagCompound contentTag = patternList.getCompoundTagAt(i);
            ItemStack Item2 = ItemStack.loadItemStackFromNBT((NBTTagCompound)contentTag);
            this.addPattern(Item2);
        }
        this.refreshInfo();
    }

    private void writeContentsAsNbtList(NBTTagCompound nbt) {
        NBTTagList list = new NBTTagList();
        for (ItemStack stack : this.patterns) {
            NBTTagCompound contentTag = new NBTTagCompound();
            stack.writeToNBT(contentTag);
            list.appendTag((NBTBase)contentTag);
        }
        nbt.setTag("patterns", (NBTBase)list);
    }

    public ContainerBase<TileEntityPatternStorage> getGuiContainer(EntityPlayer player) {
        return new ContainerPatternStorage(player, this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public GuiScreen getGui(EntityPlayer player, boolean isAdmin) {
        return new GuiPatternStorage(new ContainerPatternStorage(player, this));
    }

    @Override
    public void onNetworkEvent(EntityPlayer player, int event) {
        switch (event) {
            case 0: {
                if (this.patterns.isEmpty()) break;
                this.index = this.index <= 0 ? this.patterns.size() - 1 : --this.index;
                this.refreshInfo();
                break;
            }
            case 1: {
                if (this.patterns.isEmpty()) break;
                this.index = this.index >= this.patterns.size() - 1 ? 0 : ++this.index;
                this.refreshInfo();
                break;
            }
            case 2: {
                ItemStack crystalMemory;
                if (this.index < 0 || this.index >= this.patterns.size() || this.diskSlot.isEmpty() || !((crystalMemory = this.diskSlot.get()).getItem() instanceof ItemCrystalMemory)) break;
                ((ItemCrystalMemory)crystalMemory.getItem()).writecontentsTag(crystalMemory, this.patterns.get(this.index));
                break;
            }
            case 3: {
                ItemStack record;
                ItemStack crystalMemory;
                if (this.diskSlot.isEmpty() || !((crystalMemory = this.diskSlot.get()).getItem() instanceof ItemCrystalMemory) || (record = ((ItemCrystalMemory)crystalMemory.getItem()).readItemStack(crystalMemory)) == null) break;
                this.addPattern(record);
            }
        }
    }

    public void refreshInfo() {
        if (this.index < 0 || this.index >= this.patterns.size()) {
            this.index = 0;
        }
        this.maxIndex = this.patterns.size();
        if (this.patterns.isEmpty()) {
            this.pattern = null;
        } else {
            this.pattern = this.patterns.get(this.index);
            this.patternUu = UuIndex.instance.getInBuckets(this.pattern);
        }
    }

    @Override
    public void onGuiClosed(EntityPlayer player) {
    }

    @Override
    public boolean addPattern(ItemStack itemstack) {
        for (ItemStack pattern : this.patterns) {
            if (!StackUtil.checkItemEquality(pattern, itemstack)) continue;
            return false;
        }
        this.patterns.add(itemstack);
        this.refreshInfo();
        return true;
    }

    @Override
    public List<ItemStack> getPatterns() {
        return this.patterns;
    }
}

