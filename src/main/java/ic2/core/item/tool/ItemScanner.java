/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.inventory.Container
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.BlockPos$MutableBlockPos
 *  net.minecraft.world.ChunkCache
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.item.tool;

import ic2.api.item.ElectricItem;
import ic2.api.item.IBoxable;
import ic2.api.item.IElectricItemManager;
import ic2.core.IC2;
import ic2.core.IHasGui;
import ic2.core.Ic2Player;
import ic2.core.Platform;
import ic2.core.audio.AudioManager;
import ic2.core.audio.PositionSpec;
import ic2.core.init.Localization;
import ic2.core.init.OreValues;
import ic2.core.item.BaseElectricItem;
import ic2.core.item.IHandHeldInventory;
import ic2.core.item.tool.ContainerToolScanner;
import ic2.core.item.tool.HandHeldScanner;
import ic2.core.ref.ItemName;
import ic2.core.util.ItemComparableItemStack;
import ic2.core.util.StackUtil;
import ic2.core.util.Tuple;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ChunkCache;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemScanner
extends BaseElectricItem
implements IBoxable,
IHandHeldInventory {
    public ItemScanner() {
        this(ItemName.scanner, 100000.0, 128.0, 1);
    }

    public ItemScanner(ItemName name, double maxCharge, double transferLimit, int tier) {
        super(name, maxCharge, transferLimit, tier);
    }

    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        super.addInformation(stack, player, tooltip, advanced);
        tooltip.add(Localization.translate("ic2.scanner.range", "" + this.getScanRange()));
    }

    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        if (this.tier == 1 && !ElectricItem.manager.use(stack, 50.0, (EntityLivingBase)player) || this.tier == 2 && !ElectricItem.manager.use(stack, 250.0, (EntityLivingBase)player)) {
            return new ActionResult(EnumActionResult.FAIL, (Object)stack);
        }
        if (!world.isRemote) {
            if (IC2.platform.launchGui(player, this.getInventory(player, stack)) && player.openContainer instanceof ContainerToolScanner) {
                ContainerToolScanner container = (ContainerToolScanner)player.openContainer;
                Map<ItemComparableItemStack, Integer> scanResult = this.scan(player.worldObj, player.getPosition(), this.getScanRange());
                container.setResults(this.scanMapToSortedList(scanResult));
            }
        } else {
            IC2.audioManager.playOnce((Object)player, PositionSpec.Hand, "Tools/ODScanner.ogg", true, IC2.audioManager.getDefaultVolume());
        }
        return new ActionResult(EnumActionResult.SUCCESS, (Object)stack);
    }

    public int startLayerScan(ItemStack stack) {
        return ElectricItem.manager.use(stack, 50.0, null) ? this.getScanRange() / 2 : 0;
    }

    public int getScanRange() {
        return 6;
    }

    public boolean haveChargeforScan(ItemStack stack) {
        return ElectricItem.manager.canUse(stack, 128.0);
    }

    @Override
    public boolean canBeStoredInToolbox(ItemStack itemstack) {
        return true;
    }

    @Override
    public IHasGui getInventory(EntityPlayer player, ItemStack stack) {
        return new HandHeldScanner(player, stack);
    }

    private Map<ItemComparableItemStack, Integer> scan(World world, BlockPos center, int range) {
        HashMap<ItemComparableItemStack, Integer> ret = new HashMap<ItemComparableItemStack, Integer>();
        ChunkCache cache = new ChunkCache(world, center.add(- range, - range, - range), center.add(range, range, range), 0);
        Ic2Player player = Ic2Player.get(world);
        BlockPos.MutableBlockPos tmpPos = new BlockPos.MutableBlockPos();
        for (int y = center.getY() - range; y <= center.getY() + range; ++y) {
            for (int z = center.getZ() - range; z <= center.getZ() + range; ++z) {
                for (int x = center.getX() - range; x <= center.getX() + range; ++x) {
                    List<ItemStack> drops;
                    tmpPos.setPos(x, y, z);
                    IBlockState state = cache.getBlockState((BlockPos)tmpPos);
                    if (state.getBlock().isAir(state, (IBlockAccess)cache, (BlockPos)tmpPos)) continue;
                    ItemStack pickStack = StackUtil.getPickStack(world, (BlockPos)tmpPos, state, player);
                    if (pickStack != null && OreValues.get(pickStack) > 0) {
                        drops = Arrays.asList(new ItemStack[]{pickStack});
                    } else {
                        drops = StackUtil.getDrops((IBlockAccess)cache, (BlockPos)tmpPos, state, 0);
                        if (drops.isEmpty() || OreValues.get(drops) <= 0) continue;
                    }
                    for (ItemStack drop : drops) {
                        ItemComparableItemStack key = new ItemComparableItemStack(drop, true);
                        Integer count = ret.get(key);
                        if (count == null) {
                            count = 0;
                        }
                        count = count + drop.stackSize;
                        ret.put(key, count);
                    }
                }
            }
        }
        return ret;
    }

    private List<Tuple.T2<ItemStack, Integer>> scanMapToSortedList(Map<ItemComparableItemStack, Integer> map) {
        ArrayList<Tuple.T2<ItemStack, Integer>> ret = new ArrayList<Tuple.T2<ItemStack, Integer>>(map.size());
        for (Map.Entry<ItemComparableItemStack, Integer> entry : map.entrySet()) {
            ret.add(new Tuple.T2<ItemStack, Integer>(entry.getKey().toStack(), entry.getValue()));
        }
        Collections.sort(ret, new Comparator<Tuple.T2<ItemStack, Integer>>(){

            @Override
            public int compare(Tuple.T2<ItemStack, Integer> a, Tuple.T2<ItemStack, Integer> b) {
                return (Integer)b.b - (Integer)a.b;
            }
        });
        return ret;
    }

}

