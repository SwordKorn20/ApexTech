/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.inventory.Slot
 *  net.minecraft.tileentity.TileEntity
 */
package ic2.core.gui.dynamic;

import ic2.core.ContainerBase;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlot;
import ic2.core.gui.SlotGrid;
import ic2.core.gui.dynamic.GuiEnvironment;
import ic2.core.gui.dynamic.GuiParser;
import ic2.core.network.GuiSynced;
import ic2.core.slot.SlotInvSlot;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;

public class DynamicContainer<T extends IInventory>
extends ContainerBase<T> {
    private static Map<Class<?>, List<String>> networkedFieldCache = new IdentityHashMap();

    public static <T extends IInventory> DynamicContainer<T> create(T base, EntityPlayer player, GuiParser.GuiNode guiNode) {
        return new DynamicContainer<T>(base, player, guiNode);
    }

    protected DynamicContainer(T base, EntityPlayer player, GuiParser.GuiNode guiNode) {
        super(base);
        this.initialize(player, guiNode, guiNode);
    }

    private void initialize(EntityPlayer player, GuiParser.GuiNode guiNode, GuiParser.ParentNode parentNode) {
        block6 : for (GuiParser.Node rawNode : parentNode.getNodes()) {
            switch (rawNode.getType()) {
                InvSlot slot;
                case environment: {
                    if (((GuiParser.EnvironmentNode)rawNode).environment == GuiEnvironment.GAME) break;
                    continue block6;
                }
                case playerinventory: {
                    GuiParser.PlayerInventoryNode node = (GuiParser.PlayerInventoryNode)rawNode;
                    SlotGrid.SlotStyle style = SlotGrid.SlotStyle.Normal;
                    int xOffset = (style.width - 16) / 2;
                    int yOffset = (style.height - 16) / 2;
                    int hotbarOffset = 58;
                    for (int row = 0; row < 3; ++row) {
                        for (int col = 0; col < 9; ++col) {
                            this.addSlotToContainer(new Slot((IInventory)player.inventory, col + row * 9 + 9, node.x + col * style.width + xOffset, node.y + row * style.height + yOffset));
                        }
                    }
                    for (int col = 0; col < 9; ++col) {
                        this.addSlotToContainer(new Slot((IInventory)player.inventory, col, node.x + col * style.width + xOffset, node.y + 58 + yOffset));
                    }
                    break;
                }
                case slot: {
                    if (!(this.base instanceof TileEntityInventory)) {
                        throw new RuntimeException("invalid base " + (Object)this.base + " for slot elements");
                    }
                    GuiParser.SlotNode node = (GuiParser.SlotNode)rawNode;
                    slot = ((TileEntityInventory)this.base).getInvSlot(node.name);
                    if (slot == null) {
                        throw new RuntimeException("invalid invslot name " + node.name + " for base " + (Object)this.base);
                    }
                    int x = node.x + (node.style.width - 16) / 2;
                    int y = node.y + (node.style.height - 16) / 2;
                    this.addSlotToContainer((Slot)new SlotInvSlot(slot, node.index, x, y));
                    break;
                }
                case slotgrid: {
                    if (!(this.base instanceof TileEntityInventory)) {
                        throw new RuntimeException("invalid base " + (Object)this.base + " for slot elements");
                    }
                    GuiParser.SlotGridNode node = (GuiParser.SlotGridNode)rawNode;
                    slot = ((TileEntityInventory)this.base).getInvSlot(node.name);
                    if (slot == null) {
                        throw new RuntimeException("invalid invslot name " + node.name + " for base " + (Object)this.base);
                    }
                    int size = slot.size();
                    if (size <= node.offset) break;
                    int x0 = node.x + (node.style.width - 16) / 2;
                    int y0 = node.y + (node.style.height - 16) / 2;
                    GuiParser.SlotGridNode.SlotGridDimension dim = node.getDimension(size);
                    int rows = dim.rows;
                    int cols = dim.cols;
                    int idx = node.offset;
                    if (!node.vertical) {
                        int y = y0;
                        for (int row = 0; row < rows && idx < size; ++row) {
                            int x = x0;
                            for (int col = 0; col < cols && idx < size; ++col) {
                                this.addSlotToContainer((Slot)new SlotInvSlot(slot, idx, x, y));
                                ++idx;
                                x += node.style.width;
                            }
                            y += node.style.height;
                        }
                        break;
                    }
                    int x = x0;
                    for (int col = 0; col < cols && idx < size; ++col) {
                        int y = y0;
                        for (int row = 0; row < rows && idx < size; ++row) {
                            this.addSlotToContainer((Slot)new SlotInvSlot(slot, idx, x, y));
                            ++idx;
                            y += node.style.height;
                        }
                        x += node.style.width;
                    }
                }
            }
            if (!(rawNode instanceof GuiParser.ParentNode)) continue;
            this.initialize(player, guiNode, (GuiParser.ParentNode)rawNode);
        }
    }

    @Override
    public List<String> getNetworkedFields() {
        List ret = networkedFieldCache.get(this.base.getClass());
        if (ret != null) {
            return ret;
        }
        ret = new ArrayList<String>();
        Class cls = this.base.getClass();
        do {
            for (Field field : cls.getDeclaredFields()) {
                if (field.getAnnotation(GuiSynced.class) == null) continue;
                ret.add(field.getName());
            }
        } while ((cls = cls.getSuperclass()) != TileEntity.class && cls != Object.class);
        ret = ret.isEmpty() ? Collections.emptyList() : new ArrayList<String>(ret);
        networkedFieldCache.put(this.base.getClass(), ret);
        return ret;
    }

}

