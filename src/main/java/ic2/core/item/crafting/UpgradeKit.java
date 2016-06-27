/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumFacing
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 */
package ic2.core.item.crafting;

import ic2.core.IC2;
import ic2.core.Platform;
import ic2.core.block.TileEntityBlock;
import ic2.core.block.wiring.TileEntityChargepadMFE;
import ic2.core.block.wiring.TileEntityChargepadMFSU;
import ic2.core.block.wiring.TileEntityElectricMFE;
import ic2.core.init.Localization;
import ic2.core.item.ItemMulti;
import ic2.core.item.type.UpdateKitType;
import ic2.core.ref.ItemName;
import java.util.List;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class UpgradeKit
extends ItemMulti<UpdateKitType> {
    public UpgradeKit() {
        super(ItemName.upgrade_kit, UpdateKitType.class);
    }

    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (!IC2.platform.isSimulating()) {
            return EnumActionResult.PASS;
        }
        UpdateKitType type = (UpdateKitType)this.getType(stack);
        if (type == null) {
            return EnumActionResult.PASS;
        }
        boolean ret = false;
        switch (type) {
            case mfsu: {
                ret = UpgradeKit.upgradeToMfsu(world, pos);
            }
        }
        if (!ret) {
            return EnumActionResult.PASS;
        }
        --stack.stackSize;
        return EnumActionResult.SUCCESS;
    }

    private static boolean upgradeToMfsu(World world, BlockPos pos) {
        TileEntity te = world.getTileEntity(pos);
        if (!(te instanceof TileEntityBlock)) {
            return false;
        }
        TileEntityChargepadMFSU replacement = null;
        if (te instanceof TileEntityElectricMFE) {
            replacement = new TileEntityChargepadMFSU();
        } else if (te instanceof TileEntityChargepadMFE) {
            replacement = new TileEntityChargepadMFSU();
        }
        if (replacement != null) {
            NBTTagCompound nbt = new NBTTagCompound();
            te.writeToNBT(nbt);
            replacement.readFromNBT(nbt);
            world.setTileEntity(pos, (TileEntity)replacement);
            replacement.markDirty();
            return true;
        }
        return false;
    }

    public void addInformation(ItemStack stack, EntityPlayer playerIn, List<String> tooltip, boolean advanced) {
        UpdateKitType type = (UpdateKitType)this.getType(stack);
        if (type == null) {
            return;
        }
        switch (type) {
            case mfsu: {
                tooltip.add(Localization.translate("ic2.upgrade_kit.mfsu.info"));
            }
        }
    }

}

