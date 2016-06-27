/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockGrass
 *  net.minecraft.block.BlockMycelium
 *  net.minecraft.block.BlockRedstoneWire
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.Item
 *  net.minecraft.item.Item$ToolMaterial
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemTool
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.registry.GameRegistry
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.item.tool;

import ic2.api.item.ElectricItem;
import ic2.api.item.IBoxable;
import ic2.api.item.IElectricItem;
import ic2.api.item.IElectricItemManager;
import ic2.api.item.IItemHudInfo;
import ic2.core.CreativeTabIC2;
import ic2.core.IC2;
import ic2.core.Platform;
import ic2.core.audio.AudioManager;
import ic2.core.audio.AudioSource;
import ic2.core.audio.PositionSpec;
import ic2.core.init.Localization;
import ic2.core.item.ElectricItemManager;
import ic2.core.item.ItemIC2;
import ic2.core.ref.IItemModelProvider;
import ic2.core.ref.ItemName;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.BlockMycelium;
import net.minecraft.block.BlockRedstoneWire;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ItemElectricTool
extends ItemTool
implements IItemModelProvider,
IElectricItem,
IBoxable,
IItemHudInfo {
    public double operationEnergyCost;
    public int maxCharge;
    public int transferLimit;
    public int tier;
    protected AudioSource audioSource;
    protected boolean wasEquipped;
    private final Set<ToolClass> toolClasses;

    protected ItemElectricTool(ItemName name, int operationEnergyCost) {
        this(name, operationEnergyCost, HarvestLevel.Iron, Collections.emptySet());
    }

    protected ItemElectricTool(ItemName name, int operationEnergyCost, HarvestLevel harvestLevel, Set<ToolClass> toolClasses) {
        this(name, 2.0f, -3.0f, operationEnergyCost, harvestLevel, toolClasses, new HashSet<Block>());
    }

    private ItemElectricTool(ItemName name, float damage, float speed, int operationEnergyCost, HarvestLevel harvestLevel, Set<ToolClass> toolClasses, Set<Block> mineableBlocks) {
        super(damage, speed, harvestLevel.toolMaterial, mineableBlocks);
        this.operationEnergyCost = operationEnergyCost;
        this.toolClasses = toolClasses;
        this.setMaxDamage(27);
        this.setMaxStackSize(1);
        this.setNoRepair();
        this.setUnlocalizedName(name.name());
        this.setCreativeTab((CreativeTabs)IC2.tabIC2);
        for (ToolClass toolClass : toolClasses) {
            if (toolClass.name == null) continue;
            this.setHarvestLevel(toolClass.name, harvestLevel.level);
        }
        if (toolClasses.contains((Object)ToolClass.Pickaxe) && harvestLevel.toolMaterial == Item.ToolMaterial.DIAMOND) {
            mineableBlocks.add(Blocks.OBSIDIAN);
            mineableBlocks.add(Blocks.REDSTONE_ORE);
            mineableBlocks.add(Blocks.LIT_REDSTONE_ORE);
        }
        GameRegistry.registerItem((Item)this, (String)name.name());
        name.setInstance(this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public void registerModels(ItemName name) {
        ItemIC2.registerModel((Item)this, 0, name, null);
    }

    public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float xOffset, float yOffset, float zOffset) {
        ElectricItem.manager.use(stack, 0.0, (EntityLivingBase)player);
        return super.onItemUse(stack, player, world, pos, hand, side, xOffset, yOffset, zOffset);
    }

    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        ElectricItem.manager.use(stack, 0.0, (EntityLivingBase)player);
        return super.onItemRightClick(stack, world, player, hand);
    }

    public String getUnlocalizedName() {
        return "ic2." + super.getUnlocalizedName().substring(5);
    }

    public String getUnlocalizedName(ItemStack stack) {
        return this.getUnlocalizedName();
    }

    public String getItemStackDisplayName(ItemStack stack) {
        return Localization.translate(this.getUnlocalizedName(stack));
    }

    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return ItemIC2.shouldReequip(oldStack, newStack, slotChanged);
    }

    public boolean canHarvestBlock(IBlockState state, ItemStack stack) {
        Material material = state.getMaterial();
        for (ToolClass toolClass : this.toolClasses) {
            if (!toolClass.whitelist.contains((Object)state.getBlock()) && !toolClass.whitelist.contains((Object)material)) continue;
            return true;
        }
        return super.canHarvestBlock(state, stack);
    }

    public float getStrVsBlock(ItemStack stack, IBlockState state) {
        if (!ElectricItem.manager.canUse(stack, this.operationEnergyCost)) {
            return 1.0f;
        }
        if (this.canHarvestBlock(state, stack)) {
            return this.efficiencyOnProperMaterial;
        }
        return super.getStrVsBlock(stack, state);
    }

    public boolean hitEntity(ItemStack itemstack, EntityLivingBase entityliving, EntityLivingBase entityliving1) {
        return true;
    }

    public int getItemEnchantability() {
        return 0;
    }

    public boolean isRepairable() {
        return false;
    }

    @Override
    public boolean canProvideEnergy(ItemStack stack) {
        return false;
    }

    @Override
    public double getMaxCharge(ItemStack stack) {
        return this.maxCharge;
    }

    @Override
    public int getTier(ItemStack stack) {
        return this.tier;
    }

    @Override
    public double getTransferLimit(ItemStack stack) {
        return this.transferLimit;
    }

    public boolean onBlockDestroyed(ItemStack stack, World world, IBlockState state, BlockPos pos, EntityLivingBase user) {
        if (state.getBlockHardness(world, pos) != 0.0f) {
            if (user != null) {
                ElectricItem.manager.use(stack, this.operationEnergyCost, user);
            } else {
                ElectricItem.manager.discharge(stack, this.operationEnergyCost, this.tier, true, false, false);
            }
        }
        return true;
    }

    public boolean getIsRepairable(ItemStack par1ItemStack, ItemStack par2ItemStack) {
        return false;
    }

    @Override
    public boolean canBeStoredInToolbox(ItemStack itemstack) {
        return true;
    }

    public boolean isBookEnchantable(ItemStack itemstack1, ItemStack itemstack2) {
        return false;
    }

    @SideOnly(value=Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> subItems) {
        ElectricItemManager.addChargeVariants(item, subItems);
    }

    @Override
    public List<String> getHudInfo(ItemStack stack) {
        LinkedList<String> info = new LinkedList<String>();
        info.add(ElectricItem.manager.getToolTip(stack));
        info.add(Localization.translate("ic2.item.tooltip.PowerTier", this.tier));
        return info;
    }

    protected ItemStack getItemStack(double charge) {
        ItemStack ret = new ItemStack((Item)this);
        ElectricItem.manager.charge(ret, charge, Integer.MAX_VALUE, true, false);
        return ret;
    }

    public void onUpdate(ItemStack itemstack, World world, Entity entity, int i, boolean flag) {
        boolean isEquipped;
        boolean bl = isEquipped = flag && entity instanceof EntityLivingBase;
        if (IC2.platform.isRendering()) {
            if (isEquipped && !this.wasEquipped) {
                String initSound;
                String sound;
                if (this.audioSource == null && (sound = this.getIdleSound((EntityLivingBase)entity, itemstack)) != null) {
                    this.audioSource = IC2.audioManager.createSource((Object)entity, PositionSpec.Hand, sound, true, false, IC2.audioManager.getDefaultVolume());
                }
                if (this.audioSource != null) {
                    this.audioSource.play();
                }
                if ((initSound = this.getStartSound((EntityLivingBase)entity, itemstack)) != null) {
                    IC2.audioManager.playOnce((Object)entity, PositionSpec.Hand, initSound, true, IC2.audioManager.getDefaultVolume());
                }
            } else if (!isEquipped && this.audioSource != null) {
                String sound;
                this.removeAudioSource();
                if (entity instanceof EntityLivingBase && (sound = this.getStopSound((EntityLivingBase)entity, itemstack)) != null) {
                    IC2.audioManager.playOnce((Object)entity, PositionSpec.Hand, sound, true, IC2.audioManager.getDefaultVolume());
                }
            } else if (this.audioSource != null) {
                this.audioSource.updatePosition();
            }
            this.wasEquipped = isEquipped;
        }
    }

    protected void removeAudioSource() {
        if (this.audioSource != null) {
            this.audioSource.stop();
            this.audioSource.remove();
            this.audioSource = null;
        }
    }

    public boolean onDroppedByPlayer(ItemStack item, EntityPlayer player) {
        this.removeAudioSource();
        return true;
    }

    protected String getIdleSound(EntityLivingBase player, ItemStack stack) {
        return null;
    }

    protected String getStopSound(EntityLivingBase player, ItemStack stack) {
        return null;
    }

    protected String getStartSound(EntityLivingBase player, ItemStack stack) {
        return null;
    }

    protected static enum ToolClass {
        Axe("axe", new Object[]{Material.WOOD, Material.PLANTS, Material.VINE}),
        Pickaxe("pickaxe", new Object[]{Material.IRON, Material.ANVIL, Material.ROCK}),
        Shears("shears", new Object[]{Blocks.WEB, Blocks.WOOL, Blocks.REDSTONE_WIRE, Blocks.TRIPWIRE, Material.LEAVES}),
        Shovel("shovel", new Object[]{Blocks.SNOW_LAYER, Blocks.SNOW}),
        Sword("sword", new Object[]{Blocks.WEB, Material.PLANTS, Material.VINE, Material.CORAL, Material.LEAVES, Material.GOURD}),
        Hoe(null, new Object[]{Blocks.DIRT, Blocks.GRASS, Blocks.MYCELIUM});
        
        public final String name;
        public final Set<Object> whitelist;

        private /* varargs */ ToolClass(String name, Object ... whitelist) {
            this.name = name;
            this.whitelist = new HashSet<Object>(Arrays.asList(whitelist));
        }
    }

    protected static enum HarvestLevel {
        Wood(0, Item.ToolMaterial.WOOD),
        Stone(1, Item.ToolMaterial.STONE),
        Iron(2, Item.ToolMaterial.IRON),
        Diamond(3, Item.ToolMaterial.DIAMOND),
        Iridium(100, Item.ToolMaterial.DIAMOND);
        
        public final int level;
        public final Item.ToolMaterial toolMaterial;

        private HarvestLevel(int level, Item.ToolMaterial toolMaterial) {
            this.level = level;
            this.toolMaterial = toolMaterial;
        }
    }

}

