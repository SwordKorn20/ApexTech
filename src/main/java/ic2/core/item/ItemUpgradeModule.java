/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.ItemMeshDefinition
 *  net.minecraft.client.renderer.block.model.ModelBakery
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.item;

import ic2.api.item.IItemHudInfo;
import ic2.core.IC2;
import ic2.core.Platform;
import ic2.core.block.state.EnumProperty;
import ic2.core.block.state.IIdProvider;
import ic2.core.init.Localization;
import ic2.core.item.ItemIC2;
import ic2.core.item.ItemMulti;
import ic2.core.ref.ItemName;
import ic2.core.upgrade.IUpgradableBlock;
import ic2.core.upgrade.IUpgradeItem;
import ic2.core.upgrade.UpgradableProperty;
import ic2.core.upgrade.UpgradeRegistry;
import ic2.core.util.LiquidUtil;
import ic2.core.util.StackUtil;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemUpgradeModule
extends ItemMulti<UpgradeType>
implements IUpgradeItem,
IItemHudInfo {
    private static final DecimalFormat decimalformat = new DecimalFormat("0.##");
    private static final List<StackUtil.AdjacentInv> emptyInvList = Collections.emptyList();
    private static final List<LiquidUtil.AdjacentFluidHandler> emptyFhList = Collections.emptyList();

    public ItemUpgradeModule() {
        super(ItemName.upgrade, UpgradeType.class);
        this.setHasSubtypes(true);
        for (UpgradeType type : UpgradeType.values()) {
            UpgradeRegistry.register(new ItemStack((Item)this, 1, type.getId()));
        }
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public void registerModels(final ItemName name) {
        ModelLoader.setCustomMeshDefinition((Item)this, (ItemMeshDefinition)new ItemMeshDefinition(){

            public ModelResourceLocation getModelLocation(ItemStack stack) {
                EnumFacing dir;
                UpgradeType type = (UpgradeType)ItemUpgradeModule.this.getType(stack);
                if (type == null) {
                    new ModelResourceLocation("builtin/missing", "missing");
                }
                if (type.directional && (dir = ItemUpgradeModule.getDirection(stack)) != null) {
                    return ItemIC2.getModelLocation(name, type.getName() + "_" + dir.getName());
                }
                return ItemIC2.getModelLocation(name, type.getName());
            }
        });
        for (UpgradeType type : this.typeProperty.getAllowedValues()) {
            ModelBakery.registerItemVariants((Item)this, (ResourceLocation[])new ResourceLocation[]{ItemUpgradeModule.getModelLocation(name, type.getName())});
            if (!type.directional) continue;
            for (EnumFacing dir : EnumFacing.VALUES) {
                ModelBakery.registerItemVariants((Item)this, (ResourceLocation[])new ResourceLocation[]{ItemUpgradeModule.getModelLocation(name, type.getName() + "_" + dir.getName())});
            }
        }
    }

    @Override
    public List<String> getHudInfo(ItemStack stack) {
        LinkedList<String> info = new LinkedList<String>();
        info.add("Machine Upgrade");
        return info;
    }

    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        UpgradeType type = (UpgradeType)this.getType(stack);
        if (type == null) {
            return;
        }
        switch (type) {
            case overclocker: {
                tooltip.add(Localization.translate("ic2.tooltip.upgrade.overclocker.time", decimalformat.format(100.0 * Math.pow(this.getProcessTimeMultiplier(stack, null), stack.stackSize))));
                tooltip.add(Localization.translate("ic2.tooltip.upgrade.overclocker.power", decimalformat.format(100.0 * Math.pow(this.getEnergyDemandMultiplier(stack, null), stack.stackSize))));
                break;
            }
            case transformer: {
                tooltip.add(Localization.translate("ic2.tooltip.upgrade.transformer", this.getExtraTier(stack, null) * stack.stackSize));
                break;
            }
            case energy_storage: {
                tooltip.add(Localization.translate("ic2.tooltip.upgrade.storage", this.getExtraEnergyStorage(stack, null) * stack.stackSize));
                break;
            }
            case ejector: {
                String side = ItemUpgradeModule.getSideName(stack);
                tooltip.add(Localization.translate("ic2.tooltip.upgrade.ejector", Localization.translate(side)));
                break;
            }
            case pulling: {
                String side = ItemUpgradeModule.getSideName(stack);
                tooltip.add(Localization.translate("ic2.tooltip.upgrade.pulling", Localization.translate(side)));
                break;
            }
            case fluid_ejector: {
                String side = ItemUpgradeModule.getSideName(stack);
                tooltip.add(Localization.translate("ic2.tooltip.upgrade.ejector", Localization.translate(side)));
                break;
            }
            case fluid_pulling: {
                String side = ItemUpgradeModule.getSideName(stack);
                tooltip.add(Localization.translate("ic2.tooltip.upgrade.pulling", Localization.translate(side)));
                break;
            }
            case redstone_inverter: {
                tooltip.add(Localization.translate("ic2.tooltip.upgrade.redstone"));
            }
        }
    }

    private static String getSideName(ItemStack stack) {
        EnumFacing dir = ItemUpgradeModule.getDirection(stack);
        if (dir == null) {
            return "ic2.tooltip.upgrade.ejector.anyside";
        }
        switch (dir) {
            case WEST: {
                return "ic2.dir.west";
            }
            case EAST: {
                return "ic2.dir.east";
            }
            case DOWN: {
                return "ic2.dir.bottom";
            }
            case UP: {
                return "ic2.dir.top";
            }
            case NORTH: {
                return "ic2.dir.north";
            }
            case SOUTH: {
                return "ic2.dir.south";
            }
        }
        throw new RuntimeException("invalid dir: " + (Object)dir);
    }

    @Override
    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer entityplayer, World world, BlockPos pos, EnumHand hand, EnumFacing side, float xOffset, float yOffset, float zOffset) {
        UpgradeType type = (UpgradeType)this.getType(stack);
        if (type == null) {
            return EnumActionResult.PASS;
        }
        if (type.directional) {
            int dir = 1 + side.ordinal();
            NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(stack);
            if (nbtData.getByte("dir") == dir) {
                nbtData.setByte("dir", 0);
            } else {
                nbtData.setByte("dir", (byte)dir);
            }
            if (IC2.platform.isRendering()) {
                switch (type) {
                    case ejector: {
                        IC2.platform.messagePlayer(entityplayer, Localization.translate("ic2.tooltip.upgrade.ejector", Localization.translate(ItemUpgradeModule.getSideName(stack))), new Object[0]);
                        break;
                    }
                    case pulling: {
                        IC2.platform.messagePlayer(entityplayer, Localization.translate("ic2.tooltip.upgrade.pulling", Localization.translate(ItemUpgradeModule.getSideName(stack))), new Object[0]);
                        break;
                    }
                    case fluid_ejector: {
                        IC2.platform.messagePlayer(entityplayer, Localization.translate("ic2.tooltip.upgrade.ejector", Localization.translate(ItemUpgradeModule.getSideName(stack))), new Object[0]);
                        break;
                    }
                    case fluid_pulling: {
                        IC2.platform.messagePlayer(entityplayer, Localization.translate("ic2.tooltip.upgrade.pulling", Localization.translate(ItemUpgradeModule.getSideName(stack))), new Object[0]);
                        break;
                    }
                }
            }
            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

    @Override
    public boolean isSuitableFor(ItemStack stack, Set<UpgradableProperty> types) {
        UpgradeType type = (UpgradeType)this.getType(stack);
        if (type == null) {
            return false;
        }
        switch (type) {
            case ejector: {
                return types.contains((Object)UpgradableProperty.ItemProducing);
            }
            case pulling: {
                return types.contains((Object)UpgradableProperty.ItemConsuming);
            }
            case fluid_ejector: {
                return types.contains((Object)UpgradableProperty.FluidProducing);
            }
            case fluid_pulling: {
                return types.contains((Object)UpgradableProperty.FluidConsuming);
            }
            case energy_storage: {
                return types.contains((Object)UpgradableProperty.EnergyStorage);
            }
            case overclocker: {
                return types.contains((Object)UpgradableProperty.Processing) || types.contains((Object)UpgradableProperty.Augmentable);
            }
            case redstone_inverter: {
                return types.contains((Object)UpgradableProperty.RedstoneSensitive);
            }
            case transformer: {
                return types.contains((Object)UpgradableProperty.Transformer);
            }
        }
        return false;
    }

    @Override
    public int getAugmentation(ItemStack stack, IUpgradableBlock parent) {
        UpgradeType type = (UpgradeType)this.getType(stack);
        if (type == null) {
            return 0;
        }
        switch (type) {
            case overclocker: {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public int getExtraProcessTime(ItemStack stack, IUpgradableBlock parent) {
        return 0;
    }

    @Override
    public double getProcessTimeMultiplier(ItemStack stack, IUpgradableBlock parent) {
        UpgradeType type = (UpgradeType)this.getType(stack);
        if (type == null) {
            return 1.0;
        }
        switch (type) {
            case overclocker: {
                return 0.7;
            }
        }
        return 1.0;
    }

    @Override
    public int getExtraEnergyDemand(ItemStack stack, IUpgradableBlock parent) {
        return 0;
    }

    @Override
    public double getEnergyDemandMultiplier(ItemStack stack, IUpgradableBlock parent) {
        UpgradeType type = (UpgradeType)this.getType(stack);
        if (type == null) {
            return 1.0;
        }
        switch (type) {
            case overclocker: {
                return 1.6;
            }
        }
        return 1.0;
    }

    @Override
    public int getExtraEnergyStorage(ItemStack stack, IUpgradableBlock parent) {
        UpgradeType type = (UpgradeType)this.getType(stack);
        if (type == null) {
            return 0;
        }
        switch (type) {
            case energy_storage: {
                return 10000;
            }
        }
        return 0;
    }

    @Override
    public double getEnergyStorageMultiplier(ItemStack stack, IUpgradableBlock parent) {
        return 1.0;
    }

    @Override
    public int getExtraTier(ItemStack stack, IUpgradableBlock parent) {
        UpgradeType type = (UpgradeType)this.getType(stack);
        if (type == null) {
            return 0;
        }
        switch (type) {
            case transformer: {
                return 1;
            }
        }
        return 0;
    }

    @Override
    public boolean modifiesRedstoneInput(ItemStack stack, IUpgradableBlock parent) {
        UpgradeType type = (UpgradeType)this.getType(stack);
        if (type == null) {
            return false;
        }
        switch (type) {
            case redstone_inverter: {
                return true;
            }
        }
        return false;
    }

    @Override
    public int getRedstoneInput(ItemStack stack, IUpgradableBlock parent, int externalInput) {
        UpgradeType type = (UpgradeType)this.getType(stack);
        if (type == null) {
            return externalInput;
        }
        switch (type) {
            case redstone_inverter: {
                return 15 - externalInput;
            }
        }
        return externalInput;
    }

    @Override
    public boolean onTick(ItemStack stack, IUpgradableBlock parent) {
        UpgradeType type = (UpgradeType)this.getType(stack);
        if (type == null) {
            return false;
        }
        TileEntity te = (TileEntity)parent;
        boolean ret = false;
        switch (type) {
            case ejector: {
                int amount = (int)Math.pow(4.0, Math.min(4, stack.stackSize - 1));
                for (StackUtil.AdjacentInv inv : ItemUpgradeModule.getTargetInventories(stack, te)) {
                    StackUtil.transfer(te, inv.te, inv.dir, amount);
                }
                break;
            }
            case pulling: {
                int amount = (int)Math.pow(4.0, Math.min(4, stack.stackSize - 1));
                for (StackUtil.AdjacentInv inv : ItemUpgradeModule.getTargetInventories(stack, te)) {
                    StackUtil.transfer(inv.te, te, inv.dir.getOpposite(), amount);
                }
                break;
            }
            case fluid_ejector: {
                if (!LiquidUtil.isFluidTile(te, null)) {
                    return false;
                }
                int amount = (int)(50.0 * Math.pow(4.0, Math.min(4, stack.stackSize - 1)));
                for (LiquidUtil.AdjacentFluidHandler fh : ItemUpgradeModule.getTargetFluidHandlers(stack, te)) {
                    LiquidUtil.transfer(te, fh.dir, fh.handler, amount);
                }
                break;
            }
            case fluid_pulling: {
                if (!LiquidUtil.isFluidTile(te, null)) {
                    return false;
                }
                int amount = (int)(50.0 * Math.pow(4.0, Math.min(4, stack.stackSize - 1)));
                for (LiquidUtil.AdjacentFluidHandler fh : ItemUpgradeModule.getTargetFluidHandlers(stack, te)) {
                    LiquidUtil.transfer(fh.handler, fh.dir.getOpposite(), te, amount);
                }
                break;
            }
            default: {
                return false;
            }
        }
        return ret;
    }

    private static List<StackUtil.AdjacentInv> getTargetInventories(ItemStack stack, TileEntity parent) {
        EnumFacing dir = ItemUpgradeModule.getDirection(stack);
        if (dir == null) {
            return StackUtil.getAdjacentInventories(parent);
        }
        StackUtil.AdjacentInv inv = StackUtil.getAdjacentInventory(parent, dir);
        if (inv == null) {
            return emptyInvList;
        }
        return Arrays.asList(inv);
    }

    private static List<LiquidUtil.AdjacentFluidHandler> getTargetFluidHandlers(ItemStack stack, TileEntity parent) {
        EnumFacing dir = ItemUpgradeModule.getDirection(stack);
        if (dir == null) {
            return LiquidUtil.getAdjacentHandlers(parent);
        }
        LiquidUtil.AdjacentFluidHandler fh = LiquidUtil.getAdjacentHandler(parent, dir);
        if (fh == null) {
            return emptyFhList;
        }
        return Arrays.asList(fh);
    }

    @Override
    public void onProcessEnd(ItemStack stack, IUpgradableBlock parent, List<ItemStack> output) {
    }

    private static EnumFacing getDirection(ItemStack stack) {
        byte rawDir = StackUtil.getOrCreateNbtData(stack).getByte("dir");
        if (rawDir < 1 || rawDir > 6) {
            return null;
        }
        return EnumFacing.VALUES[rawDir - 1];
    }

    public static enum UpgradeType implements IIdProvider
    {
        overclocker(false),
        transformer(false),
        energy_storage(false),
        redstone_inverter(false),
        ejector(true),
        pulling(true),
        fluid_ejector(true),
        fluid_pulling(true);
        
        public final boolean directional;

        private UpgradeType(boolean directional) {
            this.directional = directional;
        }

        @Override
        public String getName() {
            return this.name();
        }

        @Override
        public int getId() {
            return this.ordinal();
        }
    }

}

