/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.item;

import ic2.core.block.state.EnumProperty;
import ic2.core.block.state.IIdProvider;
import ic2.core.item.ItemIC2;
import ic2.core.ref.IMultiItem;
import ic2.core.ref.ItemName;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemMulti<T extends Enum<T>>
extends ItemIC2
implements IMultiItem<T> {
    protected final EnumProperty<T> typeProperty;
    private final Map<T, IItemRightClickHandler> rightClickHandlers = new IdentityHashMap<T, IItemRightClickHandler>();
    private final Map<T, IItemUseHandler> useHandlers = new IdentityHashMap<T, IItemUseHandler>();
    private final Map<T, IItemUpdateHandler> updateHandlers = new IdentityHashMap<T, IItemUpdateHandler>();

    public static <T extends Enum<T>> ItemMulti<T> create(ItemName name, Class<T> typeClass) {
        EnumProperty<T> typeProperty = new EnumProperty<T>("type", typeClass);
        if (typeProperty.getAllowedValues().size() > 32767) {
            throw new IllegalArgumentException("Too many values to fit in a short for " + typeClass);
        }
        return new ItemMulti<T>(name, typeProperty);
    }

    private ItemMulti(ItemName name, EnumProperty<T> typeProperty) {
        super(name);
        this.typeProperty = typeProperty;
        this.setHasSubtypes(true);
    }

    protected ItemMulti(ItemName name, Class<T> typeClass) {
        this(name, new EnumProperty<T>("type", typeClass));
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public void registerModels(ItemName name) {
        for (Enum type : this.typeProperty.getAllowedValues()) {
            this.registerModel(((IIdProvider)((Object)type)).getId(), name, ((IIdProvider)((Object)type)).getName());
        }
    }

    @Override
    public final String getUnlocalizedName(ItemStack stack) {
        T type = this.getType(stack);
        if (type == null) {
            return super.getUnlocalizedName(stack);
        }
        return super.getUnlocalizedName(stack) + "." + ((IIdProvider)type).getName();
    }

    @Override
    public ItemStack getItemStack(T type) {
        if (!this.typeProperty.getAllowedValues().contains(type)) {
            throw new IllegalArgumentException("invalid property value " + type + " for property " + this.typeProperty);
        }
        return this.getItemStackUnchecked(type);
    }

    private ItemStack getItemStackUnchecked(T type) {
        return new ItemStack((Item)this, 1, ((IIdProvider)type).getId());
    }

    @Override
    public ItemStack getItemStack(String variant) {
        T type = this.typeProperty.getValue(variant);
        if (type == null) {
            throw new IllegalArgumentException("invalid variant " + variant + " for " + this);
        }
        return this.getItemStackUnchecked(type);
    }

    @Override
    public String getVariant(ItemStack stack) {
        if (stack == null) {
            throw new NullPointerException("null stack");
        }
        if (stack.getItem() != this) {
            throw new IllegalArgumentException("The stack " + (Object)stack + " doesn't match " + this);
        }
        T type = this.getType(stack);
        if (type == null) {
            throw new IllegalArgumentException("The stack " + (Object)stack + " doesn't reference any valid subtype");
        }
        return ((IIdProvider)type).getName();
    }

    @SideOnly(value=Side.CLIENT)
    public final void getSubItems(Item item, CreativeTabs tab, List<ItemStack> subItems) {
        for (Enum type : this.typeProperty.getAllowedValues()) {
            subItems.add(this.getItemStackUnchecked(type));
        }
    }

    public final T getType(ItemStack stack) {
        return this.typeProperty.getValue(stack.getMetadata());
    }

    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        T type = this.getType(stack);
        if (type == null) {
            return new ActionResult(EnumActionResult.PASS, (Object)stack);
        }
        IItemRightClickHandler handler = this.rightClickHandlers.get(type);
        if (handler == null) {
            return new ActionResult(EnumActionResult.PASS, (Object)stack);
        }
        return handler.onRightClick(stack, player, hand);
    }

    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        T type = this.getType(stack);
        if (type == null) {
            return EnumActionResult.PASS;
        }
        IItemUseHandler handler = this.useHandlers.get(type);
        if (handler == null) {
            return EnumActionResult.PASS;
        }
        return handler.onUse(stack, player, pos, hand, side);
    }

    public void onUpdate(ItemStack stack, World world, Entity entity, int slotIndex, boolean isCurrentItem) {
        T type = this.getType(stack);
        if (type == null) {
            return;
        }
        IItemUpdateHandler handler = this.updateHandlers.get(type);
        if (handler == null) {
            return;
        }
        handler.onUpdate(stack, world, entity, slotIndex, isCurrentItem);
    }

    public void setRightClickHandler(T type, IItemRightClickHandler handler) {
        if (type == null) {
            for (Enum cType : this.typeProperty.getAllowedValues()) {
                this.setRightClickHandler(cType, handler);
            }
        } else {
            this.rightClickHandlers.put(type, handler);
        }
    }

    public void setUseHandler(T type, IItemUseHandler handler) {
        if (type == null) {
            for (Enum cType : this.typeProperty.getAllowedValues()) {
                this.setUseHandler(cType, handler);
            }
        } else {
            this.useHandlers.put(type, handler);
        }
    }

    public void setUpdateHandler(T type, IItemUpdateHandler handler) {
        if (type == null) {
            for (Enum cType : this.typeProperty.getAllowedValues()) {
                this.setUpdateHandler(cType, handler);
            }
        } else {
            this.updateHandlers.put(type, handler);
        }
    }

    public static interface IItemUpdateHandler {
        public void onUpdate(ItemStack var1, World var2, Entity var3, int var4, boolean var5);
    }

    public static interface IItemUseHandler {
        public EnumActionResult onUse(ItemStack var1, EntityPlayer var2, BlockPos var3, EnumHand var4, EnumFacing var5);
    }

    public static interface IItemRightClickHandler {
        public ActionResult<ItemStack> onRightClick(ItemStack var1, EntityPlayer var2, EnumHand var3);
    }

}

