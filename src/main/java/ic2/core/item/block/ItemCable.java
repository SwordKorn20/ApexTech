/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.SoundType
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.ItemMeshDefinition
 *  net.minecraft.client.renderer.block.model.ModelBakery
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.SoundCategory
 *  net.minecraft.util.SoundEvent
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.item.block;

import ic2.api.item.IBoxable;
import ic2.core.IC2;
import ic2.core.block.state.IIdProvider;
import ic2.core.block.wiring.CableType;
import ic2.core.block.wiring.TileEntityCable;
import ic2.core.block.wiring.TileEntityCableDetector;
import ic2.core.block.wiring.TileEntityCableSplitter;
import ic2.core.init.Localization;
import ic2.core.item.ItemIC2;
import ic2.core.item.block.ItemBlockTileEntity;
import ic2.core.ref.BlockName;
import ic2.core.ref.IBlockModelProvider;
import ic2.core.ref.IItemModelProvider;
import ic2.core.ref.IMultiItem;
import ic2.core.ref.ItemName;
import ic2.core.ref.TeBlock;
import ic2.core.util.Ic2Color;
import ic2.core.util.Log;
import ic2.core.util.LogCategory;
import ic2.core.util.StackUtil;
import ic2.core.util.Util;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCable
extends ItemIC2
implements IMultiItem<CableType>,
IBoxable {
    private final List<ItemStack> variants = new ArrayList<ItemStack>();
    private static final NumberFormat lossFormat = new DecimalFormat("0.00#");

    public ItemCable() {
        super(ItemName.cable);
        this.setHasSubtypes(true);
        for (CableType type : CableType.values) {
            for (int insulation = 0; insulation <= type.maxInsulation; ++insulation) {
                this.variants.add(ItemCable.getCable(type, insulation));
            }
        }
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public void registerModels(ItemName name) {
        final ResourceLocation loc = Util.getName(this);
        ModelLoader.setCustomMeshDefinition((Item)this, (ItemMeshDefinition)new ItemMeshDefinition(){

            public ModelResourceLocation getModelLocation(ItemStack stack) {
                return ItemCable.getModelLocation(loc, stack);
            }
        });
        for (ItemStack stack : this.variants) {
            ModelBakery.registerItemVariants((Item)this, (ResourceLocation[])new ResourceLocation[]{ItemCable.getModelLocation(loc, stack)});
        }
    }

    private static ModelResourceLocation getModelLocation(ResourceLocation loc, ItemStack stack) {
        return new ModelResourceLocation(new ResourceLocation(loc.getResourceDomain(), loc.getResourcePath() + "/" + ItemCable.getName(stack)), null);
    }

    @Override
    public ItemStack getItemStack(CableType type) {
        return ItemCable.getCable(type, 0);
    }

    @Override
    public ItemStack getItemStack(String variant) {
        int pos = 0;
        CableType type = null;
        int insulation = 0;
        while (pos < variant.length()) {
            int sepPos;
            int nextPos = variant.indexOf(44, pos);
            if (nextPos == -1) {
                nextPos = variant.length();
            }
            if ((sepPos = variant.indexOf(58, pos)) == -1 || sepPos >= nextPos) {
                return null;
            }
            String key = variant.substring(pos, sepPos);
            String value = variant.substring(sepPos + 1, nextPos);
            if (key.equals("type")) {
                type = CableType.get(value);
                if (type == null) {
                    IC2.log.warn(LogCategory.Item, "Invalid cable type: %s", value);
                }
            } else if (key.equals("insulation")) {
                try {
                    insulation = Integer.valueOf(value);
                }
                catch (NumberFormatException e) {
                    IC2.log.warn(LogCategory.Item, "Invalid cable insulation: %s", value);
                }
            }
            pos = nextPos + 1;
        }
        if (type == null) {
            return null;
        }
        if (insulation < 0 || insulation > type.maxInsulation) {
            IC2.log.warn(LogCategory.Item, "Invalid cable insulation: %d", insulation);
            return null;
        }
        return ItemCable.getCable(type, insulation);
    }

    @Override
    public String getVariant(ItemStack stack) {
        if (stack == null) {
            throw new NullPointerException("null stack");
        }
        if (stack.getItem() != this) {
            throw new IllegalArgumentException("The stack " + (Object)stack + " doesn't match " + this);
        }
        CableType type = ItemCable.getCableType(stack);
        int insulation = ItemCable.getInsulation(stack);
        return "type:" + type.getName() + ",insulation:" + insulation;
    }

    public static ItemStack getCable(CableType type, int insulation) {
        ItemStack ret = new ItemStack(ItemName.cable.getInstance(), 1, type.getId());
        NBTTagCompound nbt = StackUtil.getOrCreateNbtData(ret);
        nbt.setByte("type", (byte)type.ordinal());
        nbt.setByte("insulation", (byte)insulation);
        return ret;
    }

    private static CableType getCableType(ItemStack stack) {
        NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
        int type = nbt.getByte("type") & 255;
        if (type < CableType.values.length) {
            return CableType.values[type];
        }
        return CableType.copper;
    }

    private static int getInsulation(ItemStack stack) {
        CableType type = ItemCable.getCableType(stack);
        NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
        int insulation = nbt.getByte("insulation") & 255;
        return Math.min(insulation, type.maxInsulation);
    }

    private static String getName(ItemStack stack) {
        CableType type = ItemCable.getCableType(stack);
        int insulation = ItemCable.getInsulation(stack);
        return type.getName(insulation, null);
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName(stack) + "." + ItemCable.getName(stack);
    }

    public void addInformation(ItemStack stack, EntityPlayer player, List<String> info, boolean b) {
        CableType type = ItemCable.getCableType(stack);
        info.add("" + type.capacity + " " + Localization.translate("ic2.generic.text.EUt"));
        info.add(Localization.translate("ic2.cable.tooltip.loss", lossFormat.format(type.loss)));
    }

    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        IBlockState oldState = world.getBlockState(pos);
        Block oldBlock = oldState.getBlock();
        if (!oldBlock.isReplaceable((IBlockAccess)world, pos)) {
            pos = pos.offset(side);
        }
        Object newBlock = BlockName.te.getInstance();
        if (stack.stackSize <= 0 || !player.canPlayerEdit(pos, side, stack) || !world.canBlockBePlaced(newBlock, pos, false, side, (Entity)player, BlockName.te.getItemStack(TeBlock.cable))) {
            return EnumActionResult.PASS;
        }
        newBlock.onBlockPlaced(world, pos, side, hitX, hitY, hitZ, 0, (EntityLivingBase)player);
        CableType type = ItemCable.getCableType(stack);
        int insulation = ItemCable.getInsulation(stack);
        TileEntityCable te = this.getTileEntityForCable(type, insulation);
        if (ItemBlockTileEntity.placeTeBlock(stack, (EntityLivingBase)player, world, pos, side, te)) {
            SoundType soundtype = newBlock.getSoundType();
            world.playSound(player, pos, soundtype.getPlaceSound(), SoundCategory.BLOCKS, (soundtype.getVolume() + 1.0f) / 2.0f, soundtype.getPitch() * 0.8f);
            --stack.stackSize;
        }
        return EnumActionResult.SUCCESS;
    }

    private TileEntityCable getTileEntityForCable(CableType type, int insulation) {
        switch (type) {
            case detector: {
                return new TileEntityCableDetector();
            }
            case splitter: {
                return new TileEntityCableSplitter();
            }
        }
        return new TileEntityCable(type, insulation);
    }

    public void getSubItems(Item item, CreativeTabs tabs, List<ItemStack> itemList) {
        itemList.addAll(this.variants);
    }

    @Override
    public boolean canBeStoredInToolbox(ItemStack itemstack) {
        return true;
    }

}

