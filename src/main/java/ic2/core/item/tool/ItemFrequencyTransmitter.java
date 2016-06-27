/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.item.tool;

import ic2.core.IC2;
import ic2.core.Platform;
import ic2.core.block.machine.tileentity.TileEntityTeleporter;
import ic2.core.init.Localization;
import ic2.core.item.ItemIC2;
import ic2.core.ref.ItemName;
import ic2.core.util.StackUtil;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemFrequencyTransmitter
extends ItemIC2 {
    private static final String targetSetNbt = "targetSet";
    private static final String targetJustSetNbt = "targetJustSet";
    private static final String targetXNbt = "targetX";
    private static final String targetYNbt = "targetY";
    private static final String targetZNbt = "targetZ";

    public ItemFrequencyTransmitter() {
        super(ItemName.frequency_transmitter);
        this.maxStackSize = 1;
    }

    public ActionResult<ItemStack> onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer, EnumHand hand) {
        if (IC2.platform.isSimulating()) {
            NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(itemstack);
            boolean hadJustSet = nbtData.getBoolean("targetJustSet");
            if (nbtData.getBoolean("targetSet") && !hadJustSet) {
                nbtData.setBoolean("targetSet", false);
                IC2.platform.messagePlayer(entityplayer, "Frequency Transmitter unlinked", new Object[0]);
            }
            if (hadJustSet) {
                nbtData.setBoolean("targetJustSet", false);
            }
        }
        return new ActionResult(EnumActionResult.SUCCESS, (Object)itemstack);
    }

    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (world.isRemote) {
            return EnumActionResult.PASS;
        }
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof TileEntityTeleporter)) {
            return EnumActionResult.PASS;
        }
        TileEntityTeleporter tp = (TileEntityTeleporter)te;
        NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(stack);
        boolean targetSet = nbtData.getBoolean("targetSet");
        boolean justSetTarget = true;
        BlockPos target = new BlockPos(nbtData.getInteger("targetX"), nbtData.getInteger("targetY"), nbtData.getInteger("targetZ"));
        if (!targetSet) {
            targetSet = true;
            target = tp.getPos();
            IC2.platform.messagePlayer(player, "Frequency Transmitter linked to Teleporter.", new Object[0]);
        } else if (tp.getPos().equals((Object)target)) {
            IC2.platform.messagePlayer(player, "Can't link Teleporter to itself.", new Object[0]);
        } else if (tp.hasTarget() && tp.getTarget().equals((Object)target)) {
            IC2.platform.messagePlayer(player, "Teleportation link unchanged.", new Object[0]);
        } else {
            TileEntity targetTe = world.getTileEntity(target);
            if (targetTe instanceof TileEntityTeleporter) {
                tp.setTarget(target);
                ((TileEntityTeleporter)targetTe).setTarget(pos);
                IC2.platform.messagePlayer(player, "Teleportation link established.", new Object[0]);
            } else {
                justSetTarget = false;
                targetSet = false;
            }
        }
        nbtData.setBoolean("targetSet", targetSet);
        nbtData.setBoolean("targetJustSet", justSetTarget);
        nbtData.setInteger("targetX", target.getX());
        nbtData.setInteger("targetY", target.getY());
        nbtData.setInteger("targetZ", target.getZ());
        return EnumActionResult.SUCCESS;
    }

    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> tooltip, boolean advanced) {
        NBTTagCompound nbtData = StackUtil.getOrCreateNbtData(stack);
        if (nbtData.getBoolean("targetSet")) {
            tooltip.add(Localization.translate("ic2.frequency_transmitter.tooltip.target", nbtData.getInteger("targetX"), nbtData.getInteger("targetY"), nbtData.getInteger("targetZ")));
        } else {
            tooltip.add(Localization.translate("ic2.frequency_transmitter.tooltip.blank"));
        }
    }
}

