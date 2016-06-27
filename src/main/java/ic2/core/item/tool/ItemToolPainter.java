/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockColored
 *  net.minecraft.block.BlockStainedGlass
 *  net.minecraft.block.BlockStainedGlassPane
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyEnum
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.ItemMeshDefinition
 *  net.minecraft.client.renderer.block.model.ModelBakery
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.passive.EntitySheep
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.EnumDyeColor
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$EntityInteract
 *  net.minecraftforge.fml.common.eventhandler.EventBus
 *  net.minecraftforge.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 *  net.minecraftforge.oredict.OreDictionary
 */
package ic2.core.item.tool;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import ic2.api.item.IBoxable;
import ic2.core.IC2;
import ic2.core.Platform;
import ic2.core.audio.AudioManager;
import ic2.core.audio.PositionSpec;
import ic2.core.block.state.EnumProperty;
import ic2.core.init.Localization;
import ic2.core.item.ItemIC2;
import ic2.core.ref.IMultiItem;
import ic2.core.ref.ItemName;
import ic2.core.util.Ic2Color;
import ic2.core.util.Keyboard;
import ic2.core.util.StackUtil;
import ic2.core.util.Util;
import java.util.Collection;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockColored;
import net.minecraft.block.BlockStainedGlass;
import net.minecraft.block.BlockStainedGlassPane;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;

public class ItemToolPainter
extends ItemIC2
implements IMultiItem<Ic2Color>,
IBoxable {
    private static final EnumProperty<Ic2Color> typeProperty = new EnumProperty("type", Ic2Color.class);
    private static final int maxDamage = 32;

    public ItemToolPainter() {
        super(ItemName.painter);
        this.setMaxDamage(31);
        this.setMaxStackSize(1);
        this.setHasSubtypes(true);
        MinecraftForge.EVENT_BUS.register((Object)this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public void registerModels(final ItemName name) {
        ModelLoader.setCustomMeshDefinition((Item)this, (ItemMeshDefinition)new ItemMeshDefinition(){

            public ModelResourceLocation getModelLocation(ItemStack stack) {
                Ic2Color color = ItemToolPainter.this.getColor(stack);
                return ItemIC2.getModelLocation(name, color != null ? color.getName() : null);
            }
        });
        ModelBakery.registerItemVariants((Item)this, (ResourceLocation[])new ResourceLocation[]{ItemToolPainter.getModelLocation(name, null)});
        for (Ic2Color type : typeProperty.getAllowedValues()) {
            ModelBakery.registerItemVariants((Item)this, (ResourceLocation[])new ResourceLocation[]{ItemToolPainter.getModelLocation(name, type.getName())});
        }
    }

    public int getDamage(ItemStack stack) {
        int rawDamage = super.getDamage(stack);
        if (rawDamage == 0) {
            return 0;
        }
        return (rawDamage - 1) / Ic2Color.values.length;
    }

    public boolean isDamaged(ItemStack stack) {
        return this.getDamage(stack) > 0;
    }

    public void setDamage(ItemStack stack, int damage) {
        int oldRawDamage = super.getDamage(stack);
        if (oldRawDamage == 0) {
            return;
        }
        int oldDamage = this.getDamage(stack);
        int newDamage = Util.limit(damage, 0, 32);
        super.setDamage(stack, oldRawDamage + (newDamage - oldDamage) * Ic2Color.values.length);
    }

    public int getMetadata(ItemStack stack) {
        int rawDamage = super.getDamage(stack);
        if (rawDamage == 0) {
            return 0;
        }
        return (rawDamage - 1) % Ic2Color.values.length + 1;
    }

    private Ic2Color getColor(ItemStack stack) {
        int meta = this.getMetadata(stack);
        if (meta == 0) {
            return null;
        }
        return Ic2Color.values[meta - 1];
    }

    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        Ic2Color color = this.getColor(stack);
        if (color == null) {
            return EnumActionResult.PASS;
        }
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();
        if (block.recolorBlock(world, pos, side, color.mcColor) || this.colorBlock(world, pos, block, state, color.mcColor)) {
            this.damagePainter(stack, color, player);
            if (world.isRemote) {
                IC2.audioManager.playOnce((Object)player, PositionSpec.Hand, "Tools/Painter.ogg", true, IC2.audioManager.getDefaultVolume());
            }
            return world.isRemote ? EnumActionResult.PASS : EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

    private boolean colorBlock(World world, BlockPos pos, Block block, IBlockState state, EnumDyeColor newColor) {
        for (IProperty property : state.getProperties().keySet()) {
            if (property.getValueClass() != EnumDyeColor.class) continue;
            IProperty typedProperty = property;
            EnumDyeColor oldColor = (EnumDyeColor)state.getValue(typedProperty);
            if (oldColor == newColor || !typedProperty.getAllowedValues().contains((Object)newColor)) {
                return false;
            }
            world.setBlockState(pos, state.withProperty(typedProperty, (Comparable)newColor));
            return true;
        }
        if (block == Blocks.HARDENED_CLAY) {
            world.setBlockState(pos, Blocks.STAINED_HARDENED_CLAY.getDefaultState().withProperty((IProperty)BlockColored.COLOR, (Comparable)newColor));
            return true;
        }
        if (block == Blocks.GLASS) {
            world.setBlockState(pos, Blocks.STAINED_GLASS.getDefaultState().withProperty((IProperty)BlockStainedGlass.COLOR, (Comparable)newColor));
            return true;
        }
        if (block == Blocks.GLASS_PANE) {
            world.setBlockState(pos, Blocks.STAINED_GLASS_PANE.getDefaultState().withProperty((IProperty)BlockStainedGlassPane.COLOR, (Comparable)newColor));
            return true;
        }
        return false;
    }

    @SubscribeEvent
    public void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        EntitySheep sheep;
        EntityPlayer player = event.getEntityPlayer();
        if (player.worldObj.isRemote) {
            return;
        }
        Entity entity = event.getEntity();
        ItemStack stack = player.getActiveItemStack();
        if (stack == null || stack.getItem() != this) {
            return;
        }
        Ic2Color color = this.getColor(stack);
        if (color == null) {
            return;
        }
        if (entity instanceof EntitySheep && (sheep = (EntitySheep)entity).getFleeceColor() != color.mcColor) {
            ((EntitySheep)entity).setFleeceColor(color.mcColor);
            this.damagePainter(stack, color, player);
            event.setCanceled(true);
        }
    }

    public ActionResult<ItemStack> onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer, EnumHand hand) {
        if (!world.isRemote && IC2.keyboard.isModeSwitchKeyDown(entityplayer)) {
            NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(itemstack);
            boolean newValue = !nbtData.getBoolean("autoRefill");
            nbtData.setBoolean("autoRefill", newValue);
            if (newValue) {
                IC2.platform.messagePlayer(entityplayer, "Painter automatic refill mode enabled", new Object[0]);
            } else {
                IC2.platform.messagePlayer(entityplayer, "Painter automatic refill mode disabled", new Object[0]);
            }
            return new ActionResult(EnumActionResult.SUCCESS, (Object)itemstack);
        }
        return new ActionResult(EnumActionResult.PASS, (Object)itemstack);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        Ic2Color color = this.getColor(stack);
        if (color == null) {
            return this.getUnlocalizedName();
        }
        return this.getUnlocalizedName() + "." + color.getName();
    }

    @SideOnly(value=Side.CLIENT)
    public final void getSubItems(Item item, CreativeTabs tab, List<ItemStack> subItems) {
        subItems.add(this.getItemStackUnchecked(null));
        for (Ic2Color type : typeProperty.getAllowedValues()) {
            subItems.add(this.getItemStackUnchecked(type));
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        Ic2Color color = this.getColor(stack);
        if (color == null) {
            return;
        }
        ItemStack dyeStack = new ItemStack(Items.DYE, 1, color.mcColor.getDyeDamage());
        tooltip.add(Localization.translate(Items.DYE.getUnlocalizedName(dyeStack) + ".name"));
    }

    private void damagePainter(ItemStack stack, Ic2Color color, EntityPlayer player) {
        assert (color != null);
        if (stack.getItemDamage() >= stack.getMaxDamage()) {
            ItemStack dyeStack = null;
            int dyeSlot = -1;
            NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(stack);
            if (nbtData.getBoolean("autoRefill")) {
                block0 : for (int i = 0; i < player.inventory.mainInventory.length; ++i) {
                    if (player.inventory.mainInventory[i] == null) continue;
                    for (ItemStack ore : OreDictionary.getOres((String)color.oreDictDyeName)) {
                        if (!ore.isItemEqual(player.inventory.mainInventory[i])) continue;
                        dyeStack = player.inventory.mainInventory[i];
                        dyeSlot = i;
                        continue block0;
                    }
                }
            }
            if (dyeStack == null) {
                super.setDamage(stack, 0);
            } else {
                --dyeStack.stackSize;
                if (dyeStack.stackSize == 0) {
                    player.inventory.mainInventory[dyeSlot] = null;
                }
                this.setDamage(stack, 0);
            }
        } else {
            stack.damageItem(1, (EntityLivingBase)player);
        }
    }

    @Override
    public ItemStack getItemStack(Ic2Color type) {
        if (type != null && !typeProperty.getAllowedValues().contains(type)) {
            throw new IllegalArgumentException("invalid property value " + type + " for property " + typeProperty);
        }
        return this.getItemStackUnchecked(type);
    }

    private ItemStack getItemStackUnchecked(Ic2Color type) {
        if (type == null) {
            return new ItemStack((Item)this);
        }
        return new ItemStack((Item)this, 1, 1 + type.getId());
    }

    @Override
    public ItemStack getItemStack(String variant) {
        Ic2Color type;
        if (variant != null && !variant.isEmpty()) {
            type = typeProperty.getValue(variant);
            if (type == null) {
                throw new IllegalArgumentException("invalid variant " + variant + " for " + this);
            }
        } else {
            type = null;
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
        Ic2Color color = this.getColor(stack);
        if (color == null) {
            return null;
        }
        return color.getName();
    }

    @Override
    public boolean canBeStoredInToolbox(ItemStack itemstack) {
        return true;
    }

}

