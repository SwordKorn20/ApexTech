/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.properties.PropertyDirection
 *  net.minecraft.block.state.BlockStateContainer
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.particle.ParticleManager
 *  net.minecraft.client.renderer.ItemMeshDefinition
 *  net.minecraft.client.renderer.block.model.ModelBakery
 *  net.minecraft.client.renderer.block.model.ModelResourceLocation
 *  net.minecraft.client.renderer.block.statemap.IStateMapper
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.EnumDyeColor
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumFacing$Axis
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.Explosion
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 *  net.minecraftforge.client.model.ModelLoader
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block;

import com.google.common.collect.ImmutableList;
import ic2.api.tile.IWrenchable;
import ic2.core.IC2;
import ic2.core.Platform;
import ic2.core.block.BlockBase;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.state.Ic2BlockState;
import ic2.core.init.MainConfig;
import ic2.core.item.block.ItemBlockTileEntity;
import ic2.core.model.ModelUtil;
import ic2.core.network.NetworkManager;
import ic2.core.ref.BlockName;
import ic2.core.ref.IMultiBlock;
import ic2.core.ref.MetaTeBlock;
import ic2.core.ref.MetaTeBlockProperty;
import ic2.core.ref.TeBlock;
import ic2.core.util.ConfigUtil;
import ic2.core.util.ParticleUtil;
import ic2.core.util.SideGateway;
import ic2.core.util.Util;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.IStateMapper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public final class BlockTileEntity
extends BlockBase
implements IMultiBlock<TeBlock>,
IWrenchable {
    public static final IProperty<MetaTeBlock> typeProperty = new MetaTeBlockProperty();
    public static final IProperty<EnumFacing> facingProperty = PropertyDirection.create((String)"facing");
    private static final int removedTesToKeep = 4;
    private static final WeakReference<TileEntityBlock>[] removedTes = new WeakReference[4];
    private static int nextRemovedTeIndex;
    private final Item item;

    public BlockTileEntity() {
        super(BlockName.te, Material.IRON, ItemBlockTileEntity.class);
        this.setDefaultState(this.blockState.getBaseState().withProperty(typeProperty, (Comparable)TeBlock.invalid.getMeta(false)).withProperty(facingProperty, (Comparable)EnumFacing.DOWN));
        this.item = Item.getItemFromBlock((Block)this);
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public void registerModels(BlockName name) {
        final ResourceLocation loc = Util.getName(this);
        final ModelResourceLocation invalidLocation = ModelUtil.getModelLocation(loc, this.blockState.getBaseState().withProperty(typeProperty, (Comparable)TeBlock.invalid.getMeta(false)).withProperty(facingProperty, (Comparable)EnumFacing.NORTH));
        ModelLoader.setCustomStateMapper((Block)this, (IStateMapper)new IStateMapper(){

            public Map<IBlockState, ModelResourceLocation> putStateModelLocations(Block block) {
                IdentityHashMap<IBlockState, ModelResourceLocation> ret = new IdentityHashMap<IBlockState, ModelResourceLocation>();
                for (IBlockState state : block.getBlockState().getValidStates()) {
                    MetaTeBlock metaTeBlock = (MetaTeBlock)state.getValue(BlockTileEntity.typeProperty);
                    EnumFacing facing = (EnumFacing)state.getValue(BlockTileEntity.facingProperty);
                    if (metaTeBlock.teBlock.supportedFacings.contains((Object)facing) || facing == EnumFacing.DOWN && metaTeBlock.teBlock.supportedFacings.isEmpty()) {
                        ret.put(state, ModelUtil.getModelLocation(loc, state));
                        continue;
                    }
                    ret.put(state, invalidLocation);
                }
                return ret;
            }
        });
        ModelLoader.setCustomMeshDefinition((Item)this.item, (ItemMeshDefinition)new ItemMeshDefinition(){

            public ModelResourceLocation getModelLocation(ItemStack stack) {
                IBlockState state = BlockTileEntity.this.getDefaultState().withProperty(BlockTileEntity.typeProperty, (Comparable)TeBlock.get(stack.getItemDamage()).getMeta(false)).withProperty(BlockTileEntity.facingProperty, (Comparable)EnumFacing.NORTH);
                return ModelUtil.getModelLocation(loc, state);
            }
        });
        for (TeBlock teBlock : TeBlock.values) {
            if (!teBlock.hasItem()) continue;
            IBlockState state = this.blockState.getBaseState().withProperty(typeProperty, (Comparable)teBlock.getMeta(false)).withProperty(facingProperty, (Comparable)EnumFacing.NORTH);
            ModelBakery.registerItemVariants((Item)this.item, (ResourceLocation[])new ResourceLocation[]{ModelUtil.getModelLocation(loc, state)});
        }
    }

    public boolean hasTileEntity() {
        return true;
    }

    public boolean hasTileEntity(IBlockState state) {
        return true;
    }

    protected BlockStateContainer createBlockState() {
        return new Ic2BlockState(this, typeProperty, facingProperty);
    }

    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntityBlock te = BlockTileEntity.getTe(world, pos);
        if (te == null) {
            return state;
        }
        return state.withProperty(typeProperty, (Comparable)TeBlock.get(te.getClass()).getMeta(te.getActive())).withProperty(facingProperty, (Comparable)te.getFacing());
    }

    @SideOnly(value=Side.CLIENT)
    public void getSubBlocks(Item item, CreativeTabs tabs, List<ItemStack> list) {
        for (TeBlock type : TeBlock.values) {
            if (!type.hasItem()) continue;
            list.add(this.getItemStack(type));
        }
    }

    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player) {
        TileEntityBlock te = BlockTileEntity.getTe((IBlockAccess)world, pos);
        if (te == null) {
            return null;
        }
        return te.getPickBlock(player, target);
    }

    @Override
    public IBlockState getState(TeBlock variant) {
        if (variant == null) {
            throw new IllegalArgumentException("invalid type: " + variant);
        }
        EnumFacing facing = variant.supportedFacings.isEmpty() ? EnumFacing.DOWN : (variant.supportedFacings.contains((Object)EnumFacing.NORTH) ? EnumFacing.NORTH : variant.supportedFacings.iterator().next());
        return this.getDefaultState().withProperty(typeProperty, (Comparable)variant.getMeta(false)).withProperty(facingProperty, (Comparable)facing);
    }

    @Override
    public ItemStack getItemStack(TeBlock type) {
        if (type == null) {
            throw new IllegalArgumentException("invalid type: " + type);
        }
        int id = type.getId();
        if (id != -1) {
            return new ItemStack(this.item, 1, type.getId());
        }
        return null;
    }

    @Override
    public ItemStack getItemStack(String variant) {
        if (variant == null) {
            throw new IllegalArgumentException("invalid type: " + variant);
        }
        TeBlock type = TeBlock.get(variant);
        if (type == null) {
            throw new IllegalArgumentException("invalid type: " + variant);
        }
        return this.getItemStack(type);
    }

    @Override
    public String getVariant(ItemStack stack) {
        if (stack == null) {
            throw new NullPointerException("null stack");
        }
        if (stack.getItem() != this.item) {
            throw new IllegalArgumentException("The stack " + (Object)stack + " doesn't match " + (Object)this.item + " (" + this + ")");
        }
        TeBlock type = TeBlock.get(stack.getMetadata());
        if (type == null) {
            throw new IllegalArgumentException("The stack " + (Object)stack + " doesn't reference any valid subtype");
        }
        return type.getName();
    }

    public boolean isFullCube(IBlockState state) {
        return false;
    }

    public boolean canReplace(World world, BlockPos pos, EnumFacing side, ItemStack stack) {
        if (!world.getBlockState(pos).getBlock().isReplaceable((IBlockAccess)world, pos)) {
            return false;
        }
        if (stack == null) {
            return true;
        }
        if (stack.getItem() != this.item) {
            return false;
        }
        TeBlock type = TeBlock.get(stack.getMetadata());
        if (type == null) {
            return false;
        }
        TeBlock.ITePlaceHandler handler = type.getPlaceHandler();
        return handler == null || handler.canReplace(world, pos, side, stack);
    }

    public boolean canPlaceBlockOnSide(World world, BlockPos pos, EnumFacing side) {
        return this.canReplace(world, pos, side, null);
    }

    public boolean canPlaceBlockAt(World world, BlockPos pos) {
        return this.canReplace(world, pos, null, null);
    }

    public boolean addLandingEffects(IBlockState state, WorldServer world, BlockPos pos, IBlockState state2, EntityLivingBase entity, int numberOfParticles) {
        TileEntityBlock te = BlockTileEntity.getTe((IBlockAccess)world, pos);
        if (te == null || !(world instanceof WorldServer)) {
            return super.addLandingEffects(state, world, pos, state2, entity, numberOfParticles);
        }
        IC2.network.get(true).initiateTeblockLandEffect((World)world, entity.posX, entity.posY, entity.posZ, numberOfParticles, te.teBlock);
        return true;
    }

    @SideOnly(value=Side.CLIENT)
    public boolean addHitEffects(IBlockState state, World world, RayTraceResult target, ParticleManager manager) {
        BlockPos pos = target.getBlockPos();
        TileEntityBlock te = BlockTileEntity.getTe((IBlockAccess)world, pos);
        if (te == null) {
            return super.addHitEffects(state, world, target, manager);
        }
        ParticleUtil.spawnBlockHitParticles(te, target.sideHit);
        return true;
    }

    @SideOnly(value=Side.CLIENT)
    public boolean addDestroyEffects(World world, BlockPos pos, ParticleManager manager) {
        return super.addDestroyEffects(world, pos, manager);
    }

    public IBlockState getExtendedState(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntityBlock te = BlockTileEntity.getTe(world, pos);
        if (te == null) {
            return state;
        }
        return te.getExtendedState((Ic2BlockState.Ic2BlockStateInstance)state);
    }

    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        TileEntityBlock te = BlockTileEntity.getTe((IBlockAccess)world, pos);
        if (te == null) {
            return;
        }
        te.onPlaced(stack, placer, EnumFacing.UP);
    }

    public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end) {
        TileEntityBlock te = BlockTileEntity.getTe((IBlockAccess)world, pos);
        if (te == null) {
            return super.collisionRayTrace(state, world, pos, start, end);
        }
        return te.collisionRayTrace(start, end);
    }

    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntityBlock te = BlockTileEntity.getTe(world, pos);
        if (te == null) {
            return super.getBoundingBox(state, world, pos);
        }
        return te.getVisualBoundingBox();
    }

    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos) {
        TileEntityBlock te = BlockTileEntity.getTe((IBlockAccess)world, pos);
        if (te == null) {
            return super.getSelectedBoundingBox(state, world, pos);
        }
        return te.getOutlineBoundingBox().offset(pos);
    }

    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos) {
        TileEntityBlock te = BlockTileEntity.getTe((IBlockAccess)world, pos);
        if (te == null) {
            return super.getCollisionBoundingBox(state, world, pos);
        }
        return te.getPhysicsBoundingBox();
    }

    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
        TileEntityBlock te = BlockTileEntity.getTe((IBlockAccess)world, pos);
        if (te == null) {
            super.addCollisionBoxToList(state, world, pos, mask, list, collidingEntity);
        } else {
            te.addCollisionBoxesToList(mask, list, collidingEntity);
        }
    }

    public void onEntityCollidedWithBlock(World world, BlockPos pos, IBlockState state, Entity entity) {
        TileEntityBlock te = BlockTileEntity.getTe((IBlockAccess)world, pos);
        if (te == null) {
            return;
        }
        te.onEntityCollision(entity);
    }

    public boolean doesSideBlockRendering(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing face) {
        TileEntityBlock te = BlockTileEntity.getTe(world, pos);
        if (te == null) {
            return false;
        }
        return te.doesSideBlockRendering(face);
    }

    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntityBlock te = BlockTileEntity.getTe(world, pos);
        if (te == null) {
            return false;
        }
        return te.isNormalCube();
    }

    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        TileEntityBlock te = BlockTileEntity.getTe(world, pos);
        if (te == null) {
            return false;
        }
        return te.isSideSolid(side);
    }

    public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntityBlock te = BlockTileEntity.getTe(world, pos);
        if (te == null) {
            return this.getLightOpacity(state);
        }
        return te.getLightOpacity();
    }

    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntityBlock te = BlockTileEntity.getTe(world, pos);
        if (te == null) {
            return 0;
        }
        return te.getLightValue();
    }

    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (player.isSneaking()) {
            return false;
        }
        TileEntityBlock te = BlockTileEntity.getTe((IBlockAccess)world, pos);
        if (te == null) {
            return false;
        }
        return te.onActivated(player, hand, heldItem, side, hitX, hitY, hitZ);
    }

    public void onBlockClicked(World world, BlockPos pos, EntityPlayer player) {
        TileEntityBlock te = BlockTileEntity.getTe((IBlockAccess)world, pos);
        if (te == null) {
            return;
        }
        te.onClicked(player);
    }

    public void neighborChanged(IBlockState state, World world, BlockPos pos, Block neighborBlock) {
        TileEntityBlock te = BlockTileEntity.getTe((IBlockAccess)world, pos);
        if (te == null) {
            return;
        }
        te.onNeighborChange(neighborBlock);
    }

    public int getStrongPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        TileEntityBlock te = BlockTileEntity.getTe(world, pos);
        if (te == null) {
            return 0;
        }
        return te.getStrongPower(side);
    }

    public int getWeakPower(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        TileEntityBlock te = BlockTileEntity.getTe(world, pos);
        if (te == null) {
            return 0;
        }
        return te.getWeakPower(side);
    }

    public boolean canConnectRedstone(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side) {
        TileEntityBlock te = BlockTileEntity.getTe(world, pos);
        if (te == null) {
            return false;
        }
        return te.connectRedstone(side);
    }

    public boolean hasComparatorInputOverride(IBlockState state) {
        return true;
    }

    public int getComparatorInputOverride(IBlockState state, World world, BlockPos pos) {
        TileEntityBlock te = BlockTileEntity.getTe((IBlockAccess)world, pos);
        if (te == null) {
            return 0;
        }
        return te.getComparatorInputOverride();
    }

    public boolean recolorBlock(World world, BlockPos pos, EnumFacing side, EnumDyeColor color) {
        TileEntityBlock te = BlockTileEntity.getTe((IBlockAccess)world, pos);
        if (te == null) {
            return false;
        }
        return te.recolor(side, color);
    }

    public void onBlockExploded(World world, BlockPos pos, Explosion explosion) {
        TileEntityBlock te = BlockTileEntity.getTe((IBlockAccess)world, pos);
        if (te != null) {
            te.onExploded(explosion);
        }
        super.onBlockExploded(world, pos, explosion);
    }

    public void breakBlock(World world, BlockPos pos, IBlockState state) {
        TileEntityBlock te = BlockTileEntity.getTe((IBlockAccess)world, pos);
        if (te != null) {
            te.onBlockBreak();
        }
        super.breakBlock(world, pos, state);
    }

    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        TileEntityBlock te = BlockTileEntity.getTe((IBlockAccess)world, pos);
        if (te != null) {
            if (!te.onRemovedByPlayer(player, willHarvest)) {
                return false;
            }
            if (willHarvest && !world.isRemote) {
                BlockTileEntity.removedTes[BlockTileEntity.nextRemovedTeIndex] = new WeakReference<TileEntityBlock>(te);
                nextRemovedTeIndex = (nextRemovedTeIndex + 1) % 4;
            }
        }
        return super.removedByPlayer(state, world, pos, player, willHarvest);
    }

    public float getPlayerRelativeBlockHardness(IBlockState state, EntityPlayer player, World world, BlockPos pos) {
        TileEntityBlock te;
        float ret = super.getPlayerRelativeBlockHardness(state, player, world, pos);
        if (!player.canHarvestBlock(state) && (te = BlockTileEntity.getTe((IBlockAccess)world, pos)) != null && te.teBlock.harvestTool == TeBlock.HarvestTool.None) {
            ret *= 3.3333333f;
        }
        return ret;
    }

    public boolean canHarvestBlock(IBlockAccess world, BlockPos pos, EntityPlayer player) {
        boolean ret = super.canHarvestBlock(world, pos, player);
        if (ret) {
            return ret;
        }
        TileEntityBlock te = BlockTileEntity.getTe(world, pos);
        if (te != null && te.teBlock.harvestTool == TeBlock.HarvestTool.None) {
            return true;
        }
        return false;
    }

    public String getHarvestTool(IBlockState state) {
        if (state.getBlock() != this) {
            return null;
        }
        return ((MetaTeBlock)state.getValue(BlockTileEntity.typeProperty)).teBlock.harvestTool.toolClass;
    }

    public int getHarvestLevel(IBlockState state) {
        if (state.getBlock() != this) {
            return 0;
        }
        return ((MetaTeBlock)state.getValue(BlockTileEntity.typeProperty)).teBlock.harvestTool.level;
    }

    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        TileEntityBlock te = BlockTileEntity.getTe(world, pos);
        if (te == null) {
            int checkIdx;
            World realWorld = Util.getWorld(world);
            if (realWorld != null && realWorld.isRemote || realWorld == null && !IC2.platform.isSimulating()) {
                return new ArrayList<ItemStack>();
            }
            int idx = nextRemovedTeIndex;
            do {
                TileEntityBlock cTe;
                WeakReference<TileEntityBlock> ref;
                if ((ref = removedTes[checkIdx = (idx + 4 - 1) % 4]) == null || (cTe = ref.get()) == null || realWorld != null && cTe.getWorld() != realWorld || !cTe.getPos().equals((Object)pos)) continue;
                te = cTe;
                BlockTileEntity.removedTes[checkIdx] = null;
                break;
            } while ((idx = checkIdx) != nextRemovedTeIndex);
            if (te == null) {
                return new ArrayList<ItemStack>();
            }
        }
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();
        ret.addAll(te.getSelfDrops(fortune, ConfigUtil.getBool(MainConfig.get(), "balance/ignoreWrenchRequirement")));
        ret.addAll(te.getAuxDrops(fortune));
        return ret;
    }

    public float getBlockHardness(IBlockState state, World world, BlockPos pos) {
        TileEntityBlock te = BlockTileEntity.getTe((IBlockAccess)world, pos);
        if (te == null) {
            return 5.0f;
        }
        return te.getHardness();
    }

    public float getExplosionResistance(World world, BlockPos pos, Entity exploder, Explosion explosion) {
        TileEntityBlock te = BlockTileEntity.getTe((IBlockAccess)world, pos);
        if (te == null) {
            return 10.0f;
        }
        return te.getExplosionResistance(exploder, explosion);
    }

    public boolean canEntityDestroy(IBlockState state, IBlockAccess world, BlockPos pos, Entity entity) {
        TileEntityBlock te = BlockTileEntity.getTe(world, pos);
        if (te == null) {
            return true;
        }
        return te.canEntityDestroy(entity);
    }

    @Override
    public EnumFacing getFacing(World world, BlockPos pos) {
        TileEntityBlock te = BlockTileEntity.getTe((IBlockAccess)world, pos);
        if (te == null) {
            return EnumFacing.DOWN;
        }
        return te.getFacing();
    }

    @Override
    public boolean setFacing(World world, BlockPos pos, EnumFacing newDirection, EntityPlayer player) {
        TileEntityBlock te = BlockTileEntity.getTe((IBlockAccess)world, pos);
        if (te == null) {
            return false;
        }
        return te.setFacingWrench(newDirection, player);
    }

    @Override
    public boolean wrenchCanRemove(World world, BlockPos pos, EntityPlayer player) {
        TileEntityBlock te = BlockTileEntity.getTe((IBlockAccess)world, pos);
        if (te == null) {
            return false;
        }
        return te.wrenchCanRemove(player);
    }

    @Override
    public List<ItemStack> getWrenchDrops(World world, BlockPos pos, IBlockState state, TileEntity te, EntityPlayer player, int fortune) {
        if (!(te instanceof TileEntityBlock)) {
            return Collections.emptyList();
        }
        return ((TileEntityBlock)te).getWrenchDrops(player, fortune);
    }

    private static TileEntityBlock getTe(IBlockAccess world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityBlock) {
            return (TileEntityBlock)te;
        }
        return null;
    }

    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        TileEntityBlock te = BlockTileEntity.getTe((IBlockAccess)world, pos);
        if (te != null) {
            EnumFacing target = te.getFacing().rotateAround(axis.getAxis());
            if (te.getSupportedFacings().contains((Object)target) && te.getFacing() != target) {
                te.setFacing(target);
                return true;
            }
        }
        return false;
    }

}

