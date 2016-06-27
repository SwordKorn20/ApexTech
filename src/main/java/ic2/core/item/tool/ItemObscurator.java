/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.client.renderer.block.model.IBakedModel
 *  net.minecraft.client.renderer.vertex.DefaultVertexFormats
 *  net.minecraft.client.renderer.vertex.VertexFormat
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.BlockRenderLayer
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.client.model.pipeline.IVertexConsumer
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.fml.common.eventhandler.Event
 *  net.minecraftforge.fml.common.eventhandler.EventBus
 */
package ic2.core.item.tool;

import ic2.api.event.RetextureEvent;
import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItemManager;
import ic2.api.item.IItemHudInfo;
import ic2.core.IC2;
import ic2.core.Platform;
import ic2.core.block.state.BlockStateUtil;
import ic2.core.item.BaseElectricItem;
import ic2.core.model.ModelUtil;
import ic2.core.network.IPlayerItemDataListener;
import ic2.core.network.NetworkManager;
import ic2.core.ref.ItemName;
import ic2.core.util.Log;
import ic2.core.util.LogCategory;
import ic2.core.util.SideGateway;
import ic2.core.util.StackUtil;
import ic2.core.util.Util;
import ic2.core.util.Vector3;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.pipeline.IVertexConsumer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventBus;

public class ItemObscurator
extends BaseElectricItem
implements IPlayerItemDataListener,
IItemHudInfo {
    private final int scanOperationCost = 20000;
    private final int printOperationCost = 5000;
    private static ThreadLocal<ExtractingVertexConsumer> testConsumers = new ThreadLocal<ExtractingVertexConsumer>(){

        @Override
        protected ExtractingVertexConsumer initialValue() {
            return new ExtractingVertexConsumer();
        }
    };
    private static final int[] noTint = new int[]{-1};
    private static final int[] zeroTint = new int[]{0};
    private static final int[] defaultColorMultiplier = new int[]{16777215};
    private static final int[] colorMultiplierOpaqueWhite = new int[]{-1};

    public ItemObscurator() {
        super(ItemName.obscurator, 100000.0, 250.0, 2);
        this.setMaxDamage(27);
        this.setMaxStackSize(1);
        this.setNoRepair();
    }

    @Override
    public List<String> getHudInfo(ItemStack stack) {
        LinkedList<String> info = new LinkedList<String>();
        info.add(ElectricItem.manager.getToolTip(stack));
        return info;
    }

    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (!player.isSneaking() && !world.isRemote && ElectricItem.manager.canUse(stack, 5000.0)) {
            int[] colorMultipliers;
            EnumFacing refSide;
            NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
            IBlockState refState = ItemObscurator.getState(nbt);
            if (refState == null || (refSide = ItemObscurator.getSide(nbt)) == null || (colorMultipliers = ItemObscurator.getColorMultipliers(nbt)) == null) {
                ItemObscurator.clear(nbt);
                return EnumActionResult.PASS;
            }
            IBlockState state = world.getBlockState(pos);
            RetextureEvent event = new RetextureEvent(world, pos, state, side, player, refState, ItemObscurator.getVariant(nbt), refSide, colorMultipliers);
            MinecraftForge.EVENT_BUS.post((Event)event);
            if (event.applied) {
                ElectricItem.manager.use(stack, 5000.0, (EntityLivingBase)player);
                return EnumActionResult.SUCCESS;
            }
            return EnumActionResult.PASS;
        }
        if (player.isSneaking() && world.isRemote && ElectricItem.manager.canUse(stack, 20000.0)) {
            return this.scanBlock(stack, player, world, pos, side) ? EnumActionResult.SUCCESS : EnumActionResult.PASS;
        }
        return EnumActionResult.PASS;
    }

    private boolean scanBlock(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side) {
        assert (world.isRemote);
        IBlockState state = ItemObscurator.getBlockState((IBlockAccess)world, pos);
        if (state.getBlock().isAir(state, (IBlockAccess)world, pos)) {
            return false;
        }
        ObscuredRenderInfo renderInfo = ItemObscurator.getRenderInfo(state, side);
        if (renderInfo == null) {
            return false;
        }
        String variant = ModelUtil.getVariant(state);
        int[] colorMultipliers = new int[renderInfo.tints.length];
        for (int i = 0; i < renderInfo.tints.length; ++i) {
            colorMultipliers[i] = IC2.platform.getColorMultiplier(state, (IBlockAccess)world, pos, renderInfo.tints[i]);
        }
        NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
        if (ItemObscurator.getState(nbt) != state || !variant.equals(ItemObscurator.getVariant(nbt)) || ItemObscurator.getSide(nbt) != side || !Arrays.equals(ItemObscurator.getColorMultipliers(nbt), colorMultipliers)) {
            IC2.network.get(false).sendPlayerItemData(player, player.inventory.currentItem, new Object[]{state.getBlock(), variant, side, colorMultipliers});
            return true;
        }
        return false;
    }

    @Override
    public /* varargs */ void onPlayerItemNetworkData(EntityPlayer player, int slot, Object ... data) {
        if (!(data[0] instanceof Block)) {
            return;
        }
        if (!(data[1] instanceof String)) {
            return;
        }
        if (!(data[2] instanceof Integer)) {
            return;
        }
        if (!(data[3] instanceof int[])) {
            return;
        }
        ItemStack stack = player.inventory.mainInventory[slot];
        if (!ElectricItem.manager.use(stack, 20000.0, (EntityLivingBase)player)) {
            return;
        }
        NBTTagCompound nbt = StackUtil.getOrCreateNbtData(stack);
        ItemObscurator.setState(nbt, (Block)data[0], (String)data[1]);
        ItemObscurator.setSide(nbt, (Integer)data[2]);
        ItemObscurator.setColorMultipliers(nbt, (int[])data[3]);
    }

    public static IBlockState getState(NBTTagCompound nbt) {
        String blockName = nbt.getString("refBlock");
        if (blockName.isEmpty()) {
            return null;
        }
        Block block = Util.getBlock(blockName);
        if (block == null) {
            return null;
        }
        String variant = ItemObscurator.getVariant(nbt);
        return BlockStateUtil.getState(block, variant);
    }

    public static String getVariant(NBTTagCompound nbt) {
        return nbt.getString("refVariant");
    }

    private static void setState(NBTTagCompound nbt, Block block, String variant) {
        nbt.setString("refBlock", Util.getName(block).toString());
        nbt.setString("refVariant", variant);
    }

    public static EnumFacing getSide(NBTTagCompound nbt) {
        byte ordinal = nbt.getByte("refSide");
        if (ordinal < 0 || ordinal >= EnumFacing.VALUES.length) {
            return null;
        }
        return EnumFacing.VALUES[ordinal];
    }

    private static void setSide(NBTTagCompound nbt, int side) {
        nbt.setByte("refSide", (byte)side);
    }

    public static int[] getColorMultipliers(NBTTagCompound nbt) {
        int[] ret = nbt.getIntArray("refColorMuls");
        return ret.length == 0 ? null : ItemObscurator.internColorMultipliers(ret);
    }

    public static void setColorMultipliers(NBTTagCompound nbt, int[] colorMultipliers) {
        if (colorMultipliers.length == 0) {
            throw new IllegalArgumentException();
        }
        nbt.setIntArray("refColorMuls", colorMultipliers);
    }

    private static void clear(NBTTagCompound nbt) {
        nbt.removeTag("refBlock");
        nbt.removeTag("refVariant");
        nbt.removeTag("refSide");
        nbt.removeTag("refColorMul");
    }

    public static IBlockState getBlockState(IBlockAccess world, BlockPos pos) {
        IBlockState state = world.getBlockState(pos);
        return state.getActualState(world, pos);
    }

    public static ObscuredRenderInfo getRenderInfo(IBlockState state, EnumFacing side) {
        Block block = state.getBlock();
        if (block.getBlockLayer() == BlockRenderLayer.TRANSLUCENT) {
            return null;
        }
        IBakedModel model = ModelUtil.getBlockModel(state);
        if (model == null) {
            return null;
        }
        List faceQuads = model.getQuads(state, side, 0);
        if (faceQuads.isEmpty()) {
            return null;
        }
        float[] uvs = new float[faceQuads.size() * 4];
        int uvsOffset = 0;
        int[] tints = new int[faceQuads.size()];
        ExtractingVertexConsumer testConsumer = testConsumers.get();
        for (BakedQuad faceQuad : faceQuads) {
            Vector3 normal;
            Vector3 v4;
            Vector3 v1;
            Vector3 v3;
            try {
                faceQuad.pipe((IVertexConsumer)testConsumer);
            }
            catch (Throwable t) {
                IC2.log.warn(LogCategory.General, t, "Can't retrieve face data");
                ObscuredRenderInfo obscuredRenderInfo = null;
                return obscuredRenderInfo;
            }
            finally {
                testConsumer.reset();
            }
            float[] positions = testConsumer.positions;
            int dx = side.getFrontOffsetX();
            int dy = side.getFrontOffsetY();
            int dz = side.getFrontOffsetZ();
            int xS = (dx + 1) / 2;
            int yS = (dy + 1) / 2;
            int zS = (dz + 1) / 2;
            int vertices = 4;
            int positionElements = 3;
            int firstVertex = -1;
            for (int v = 0; v < 4; ++v) {
                int vo = v * 3;
                if (!Util.isSimilar(positions[vo + 0], xS) || !Util.isSimilar(positions[vo + 1], yS) || !Util.isSimilar(positions[vo + 2], zS)) continue;
                firstVertex = v;
                break;
            }
            if (firstVertex == -1 || !Util.isSimilar((v1 = new Vector3(positions[3] - positions[0], positions[4] - positions[1], positions[5] - positions[2])).lengthSquared(), 1.0) || !Util.isSimilar((v4 = new Vector3(positions[9] - positions[0], positions[10] - positions[1], positions[11] - positions[2])).lengthSquared(), 1.0) || !Util.isSimilar((v3 = new Vector3(positions[9] - positions[6], positions[10] - positions[7], positions[11] - positions[8])).copy().add(v1).lengthSquared(), 0.0) || !Util.isSimilar((normal = v1.copy().cross(v4)).copy().sub(dx, dy, dz).lengthSquared(), 0.0)) continue;
            tints[uvsOffset / 4] = testConsumer.tint;
            uvs[uvsOffset++] = testConsumer.uvs[firstVertex * 2];
            uvs[uvsOffset++] = testConsumer.uvs[firstVertex * 2 + 1];
            uvs[uvsOffset++] = testConsumer.uvs[(firstVertex + 2) % 4 * 2];
            uvs[uvsOffset++] = testConsumer.uvs[(firstVertex + 2) % 4 * 2 + 1];
        }
        if (uvsOffset == 0) {
            return null;
        }
        if (uvsOffset < uvs.length) {
            uvs = Arrays.copyOf(uvs, uvsOffset);
            tints = Arrays.copyOf(tints, uvsOffset / 4);
        }
        tints = ItemObscurator.internTints(tints);
        return new ObscuredRenderInfo(uvs, tints);
    }

    public static int[] internTints(int[] tints) {
        if (tints.length == 1) {
            if (tints[0] == noTint[0]) {
                return noTint;
            }
            if (tints[0] == zeroTint[0]) {
                return zeroTint;
            }
        }
        return tints;
    }

    public static int[] internColorMultipliers(int[] colorMultipliers) {
        if (colorMultipliers.length == 1) {
            if (colorMultipliers[0] == defaultColorMultiplier[0]) {
                return defaultColorMultiplier;
            }
            if (colorMultipliers[0] == colorMultiplierOpaqueWhite[0]) {
                return colorMultiplierOpaqueWhite;
            }
        }
        return colorMultipliers;
    }

    private static class ExtractingVertexConsumer
    implements IVertexConsumer {
        private final float[] positions = new float[12];
        private int posIdx;
        private final float[] uvs = new float[8];
        private int uvIdx;
        private int tint = -1;

        private ExtractingVertexConsumer() {
        }

        public VertexFormat getVertexFormat() {
            return DefaultVertexFormats.POSITION_TEX;
        }

        public void setQuadTint(int tint) {
            this.tint = tint;
        }

        public void setQuadOrientation(EnumFacing orientation) {
        }

        public void setApplyDiffuseLighting(boolean diffuse) {
        }

        public /* varargs */ void put(int element, float ... data) {
            if (element == 0) {
                this.positions[this.posIdx++] = data[0];
                this.positions[this.posIdx++] = data[1];
                this.positions[this.posIdx++] = data[2];
            } else if (element == 1) {
                this.uvs[this.uvIdx++] = data[0];
                this.uvs[this.uvIdx++] = data[1];
            } else {
                throw new IllegalStateException("invalid element: " + element);
            }
        }

        public void reset() {
            this.posIdx = 0;
            this.uvIdx = 0;
            this.tint = -1;
        }
    }

    public static class ObscuredRenderInfo {
        public final float[] uvs;
        public final int[] tints;

        private ObscuredRenderInfo(float[] uvs, int[] tints) {
            this.uvs = uvs;
            this.tints = tints;
        }
    }

}

