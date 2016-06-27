/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.entity.player.PlayerCapabilities
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.BlockRenderLayer
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumFacing$Axis
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block;

import ic2.api.recipe.IRecipeInput;
import ic2.api.recipe.RecipeInputOreDict;
import ic2.core.IC2;
import ic2.core.block.BlockIC2Fence;
import ic2.core.block.BlockMultiID;
import ic2.core.block.state.EnumProperty;
import ic2.core.block.state.IIdProvider;
import ic2.core.block.type.IExtBlockType;
import ic2.core.ref.BlockName;
import ic2.core.util.StackUtil;
import ic2.core.util.Util;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockScaffold
extends BlockMultiID<ScaffoldType> {
    private static final IRecipeInput stickInput = new RecipeInputOreDict("stickWood");
    private static final EnumFacing[] supportedFacings = new EnumFacing[]{EnumFacing.UP, EnumFacing.NORTH, EnumFacing.SOUTH, EnumFacing.WEST, EnumFacing.EAST};
    private static final double border = 0.03125;
    private static final AxisAlignedBB aabb = new AxisAlignedBB(0.03125, 0.0, 0.03125, 0.96875, 1.0, 0.96875);

    public static BlockScaffold create() {
        return (BlockScaffold)BlockMultiID.create(BlockScaffold.class, ScaffoldType.class, new Object[0]);
    }

    private BlockScaffold() {
        super(BlockName.scaffold, Material.WOOD);
        this.setTickRandomly(true);
    }

    @SideOnly(value=Side.CLIENT)
    public BlockRenderLayer getBlockLayer() {
        return BlockRenderLayer.CUTOUT;
    }

    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    public boolean isNormalCube(IBlockState state) {
        return false;
    }

    public boolean isLadder(IBlockState state, IBlockAccess world, BlockPos pos, EntityLivingBase entity) {
        return true;
    }

    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity rawEntity) {
        if (rawEntity instanceof EntityLivingBase) {
            EntityLivingBase entity = (EntityLivingBase)rawEntity;
            entity.fallDistance = 0.0f;
            double limit = 0.15;
            entity.motionX = Util.limit(entity.motionX, - limit, limit);
            entity.motionZ = Util.limit(entity.motionZ, - limit, limit);
            entity.motionY = entity.isSneaking() && entity instanceof EntityPlayer ? (entity.isInWater() ? 0.02 : 0.08) : (entity.isCollidedHorizontally ? 0.2 : Math.max(entity.motionY, -0.07));
        }
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        return aabb;
    }

    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos) {
        return FULL_BLOCK_AABB.offset(pos);
    }

    public boolean isBlockSolid(IBlockAccess world, BlockPos pos, EnumFacing side) {
        return side.getAxis() == EnumFacing.Axis.Y;
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        if (state.getBlock() != this) {
            return Collections.emptyList();
        }
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        ScaffoldType type = (ScaffoldType)((Object)state.getValue((IProperty)this.typeProperty));
        switch (type) {
            case wood: 
            case iron: {
                ret.add(this.getItemStack(type));
                break;
            }
            case reinforced_wood: {
                ret.add(this.getItemStack(ScaffoldType.wood));
                ret.add(new ItemStack(Items.STICK, 2));
                break;
            }
            case reinforced_iron: {
                ret.add(this.getItemStack(ScaffoldType.iron));
                ret.add(BlockName.fence.getItemStack(BlockIC2Fence.IC2FenceType.iron));
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
        return ret;
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (player.isSneaking()) {
            return false;
        }
        if (heldItem == null || !StackUtil.check(heldItem) || heldItem.stackSize < 1) {
            return false;
        }
        ScaffoldType type = (ScaffoldType)this.getType(state);
        if (type == null) {
            return false;
        }
        switch (type) {
            case wood: {
                if (stickInput.matches(heldItem) && heldItem.stackSize >= 2) break;
                return false;
            }
            case iron: {
                if (StackUtil.checkItemEquality(heldItem, BlockName.fence.getItemStack(BlockIC2Fence.IC2FenceType.iron))) break;
                return false;
            }
            case reinforced_wood: 
            case reinforced_iron: {
                return false;
            }
            default: {
                throw new IllegalStateException();
            }
        }
        if (!this.isPillar(world, pos)) {
            return false;
        }
        switch (type) {
            case wood: {
                heldItem.stackSize -= 2;
                type = ScaffoldType.reinforced_wood;
                break;
            }
            case iron: {
                --heldItem.stackSize;
                type = ScaffoldType.reinforced_iron;
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
        world.setBlockState(pos, state.withProperty((IProperty)this.typeProperty, (Comparable)((Object)type)));
        return true;
    }

    public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
        ItemStack stack = player.getHeldItemMainhand();
        if (stack == null || !StackUtil.check(stack) || stack.stackSize < 1) {
            return;
        }
        if (StackUtil.checkItemEquality(stack, Item.getItemFromBlock((Block)this))) {
            while (world.getBlockState(pos).getBlock() == this) {
                pos = pos.up();
            }
            if (this.canPlaceBlockAt(world, pos) && pos.getY() < IC2.getWorldHeight(world)) {
                int prevSize = stack.stackSize;
                stack.onItemUse(player, world, pos.down(), EnumHand.MAIN_HAND, EnumFacing.UP, 0.5f, 1.0f, 0.5f);
                if (!player.capabilities.isCreativeMode) {
                    if (stack.stackSize <= 0) {
                        player.inventory.mainInventory[player.inventory.currentItem] = null;
                    }
                } else {
                    stack.stackSize = prevSize;
                }
            }
        }
    }

    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return super.canPlaceBlockAt(world, pos) && this.hasSupport((IBlockAccess)world, pos, ScaffoldType.wood);
    }

    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighborBlock) {
        this.checkSupport(world, pos);
    }

    public void randomTick(World world, BlockPos pos, IBlockState state, Random random) {
        if (random.nextInt(8) == 0) {
            this.checkSupport(world, pos);
        }
    }

    private boolean isPillar(World world, BlockPos pos) {
        while (world.getBlockState(pos).getBlock() == this) {
            pos = pos.down();
        }
        return world.isBlockNormalCube(pos, false);
    }

    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face) {
        ScaffoldType type = (ScaffoldType)this.getType(world, pos);
        if (type == null) {
            return 0;
        }
        switch (type) {
            case wood: 
            case reinforced_wood: {
                return 8;
            }
            case iron: 
            case reinforced_iron: {
                return 0;
            }
        }
        throw new IllegalStateException();
    }

    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face) {
        ScaffoldType type = (ScaffoldType)this.getType(world, pos);
        if (type == null) {
            return 0;
        }
        switch (type) {
            case wood: 
            case reinforced_wood: {
                return 20;
            }
            case iron: 
            case reinforced_iron: {
                return 0;
            }
        }
        throw new IllegalStateException();
    }

    private boolean hasSupport(IBlockAccess world, BlockPos start, ScaffoldType type) {
        return this.calculateSupport((IBlockAccess)world, (BlockPos)start, (ScaffoldType)type).get((Object)start).strength >= 0;
    }

    private void checkSupport(World world, BlockPos start) {
        IBlockState state = world.getBlockState(start);
        if (state.getBlock() != this) {
            return;
        }
        Map<BlockPos, Support> results = this.calculateSupport((IBlockAccess)world, start, (ScaffoldType)((Object)state.getValue((IProperty)this.typeProperty)));
        boolean droppedAny = false;
        for (Support support2 : results.values()) {
            if (support2.strength >= 0) continue;
            world.setBlockState(support2.pos, Blocks.AIR.getDefaultState(), 2);
            this.dropBlockAsItem(world, support2.pos, this.getDefaultState().withProperty((IProperty)this.typeProperty, (Comparable)((Object)support2.type)), 0);
            droppedAny = true;
        }
        if (droppedAny) {
            for (Support support2 : results.values()) {
                if (support2.strength >= 0) continue;
                world.notifyNeighborsRespectDebug(support2.pos, (Block)this);
            }
        }
    }

    private Map<BlockPos, Support> calculateSupport(IBlockAccess world, BlockPos start, ScaffoldType type) {
        BlockPos pos;
        HashMap<BlockPos, Support> results = new HashMap<BlockPos, Support>();
        ArrayDeque<Object> queue = new ArrayDeque<Object>();
        HashSet<BlockPos> groundSupports = new HashSet<BlockPos>();
        Support support = new Support(start, type, -1);
        results.put(start, support);
        queue.add(support);
        while ((support = (Support)queue.poll()) != null) {
            for (EnumFacing dir : EnumFacing.VALUES) {
                pos = support.pos.offset(dir);
                if (results.containsKey((Object)pos)) continue;
                EnumFacing[] state = world.getBlockState(pos);
                Block block = state.getBlock();
                if (block == this) {
                    type = (ScaffoldType)((Object)state.getValue((IProperty)this.typeProperty));
                    reference cSupport = (reference)new Support(pos, type, -1);
                    results.put(pos, cSupport);
                    queue.add(cSupport);
                    continue;
                }
                if (!block.isNormalCube((IBlockState)state, world, pos)) continue;
                groundSupports.add(pos);
            }
        }
        for (BlockPos groundPos : groundSupports) {
            BlockPos pos2 = groundPos.up();
            int propagatedStrength = 0;
            while ((support = results.get((Object)pos2)) != null) {
                int strength;
                if (support.type.strength >= propagatedStrength) {
                    strength = support.type.strength;
                    propagatedStrength = strength - 1;
                } else {
                    strength = propagatedStrength--;
                }
                if (support.strength < strength) {
                    support.strength = strength;
                    for (EnumFacing dir : EnumFacing.HORIZONTALS) {
                        BlockPos nPos = pos2.offset(dir);
                        Support nSupport = results.get((Object)nPos);
                        if (nSupport == null || nSupport.strength >= strength) continue;
                        nSupport.strength = strength - 1;
                        queue.add(nSupport);
                    }
                }
                pos2 = pos2.up();
            }
        }
        while ((support = (Support)queue.poll()) != null) {
            for (EnumFacing dir : supportedFacings) {
                pos = support.pos.offset(dir);
                Support nSupport = results.get((Object)pos);
                if (nSupport == null || nSupport.strength >= support.strength) continue;
                nSupport.strength = support.strength - 1;
                if (nSupport.strength <= 0) continue;
                queue.add(nSupport);
            }
        }
        return results;
    }

    public static enum ScaffoldType implements IIdProvider,
    IExtBlockType
    {
        wood(2, 0.5f, 0.12f),
        reinforced_wood(5, 0.6f, 0.24f),
        iron(5, 0.8f, 6.0f),
        reinforced_iron(12, 1.0f, 8.0f);
        
        public final int strength;
        private final float hardness;
        private final float explosionResistance;

        private ScaffoldType(int strength, float hardness, float explosionResistance) {
            if (strength < 1) {
                throw new IllegalArgumentException();
            }
            this.strength = strength;
            this.hardness = hardness;
            this.explosionResistance = explosionResistance;
        }

        @Override
        public String getName() {
            return this.name();
        }

        @Override
        public int getId() {
            return this.ordinal();
        }

        @Override
        public float getHardness() {
            return this.hardness;
        }

        @Override
        public float getExplosionResistance() {
            return this.explosionResistance;
        }
    }

    private static class Support {
        final BlockPos pos;
        final ScaffoldType type;
        int strength;

        Support(BlockPos pos, ScaffoldType type, int strength) {
            this.pos = pos;
            this.type = type;
            this.strength = strength;
        }
    }

}

