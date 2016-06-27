/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.PlayerCapabilities
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ActionResult
 *  net.minecraft.util.EnumActionResult
 *  net.minecraft.util.EnumHand
 *  net.minecraft.util.math.AxisAlignedBB
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.util.math.MathHelper
 *  net.minecraft.util.math.RayTraceResult
 *  net.minecraft.util.math.RayTraceResult$Type
 *  net.minecraft.util.math.Vec3d
 *  net.minecraft.world.World
 */
package ic2.core.item;

import ic2.core.block.state.IIdProvider;
import ic2.core.item.EntityBoatCarbon;
import ic2.core.item.EntityBoatElectric;
import ic2.core.item.EntityBoatRubber;
import ic2.core.item.EntityIC2Boat;
import ic2.core.item.ItemMulti;
import ic2.core.ref.ItemName;
import ic2.core.util.Util;
import ic2.core.util.Vector3;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class ItemIC2Boat
extends ItemMulti<BoatType> {
    public ItemIC2Boat() {
        super(ItemName.boat, BoatType.class);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
        EntityIC2Boat boat = this.makeBoat(stack, world, player);
        if (boat == null) {
            return new ActionResult(EnumActionResult.PASS, (Object)stack);
        }
        Vector3 lookVec = Util.getLookScaled((Entity)player);
        Vector3 start = Util.getEyePosition((Entity)player);
        Vec3d startMc = start.toVec3();
        RayTraceResult hitPos = world.rayTraceBlocks(startMc, start.add(lookVec).toVec3(), true);
        if (hitPos == null) {
            return new ActionResult(EnumActionResult.PASS, (Object)stack);
        }
        boolean inEntity = false;
        float border = 1.0f;
        List list = world.getEntitiesWithinAABBExcludingEntity((Entity)player, player.getEntityBoundingBox().addCoord(lookVec.x, lookVec.y, lookVec.z).expand((double)border, (double)border, (double)border));
        for (Entity entity : list) {
            if (!entity.canBeCollidedWith()) continue;
            border = entity.getCollisionBorderSize();
            AxisAlignedBB aabb = entity.getEntityBoundingBox().expand((double)border, (double)border, (double)border);
            if (!aabb.isVecInside(startMc)) continue;
            inEntity = true;
            break;
        }
        if (inEntity) {
            return new ActionResult(EnumActionResult.PASS, (Object)stack);
        }
        if (hitPos.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos pos = hitPos.getBlockPos();
            if (world.getBlockState(pos).getBlock() == Blocks.SNOW_LAYER) {
                pos = pos.down();
            }
            boat.setPosition((double)pos.getX() + 0.5, (double)(pos.getY() + 1), (double)pos.getZ() + 0.5);
            boat.rotationYaw = ((MathHelper.floor_double((double)((double)(player.rotationYaw * 4.0f / 360.0f) + 0.5)) & 3) - 1) * 90;
            if (!world.getCollisionBoxes(boat.getCollisionBoundingBox().expand(-0.1, -0.1, -0.1)).isEmpty()) {
                return new ActionResult(EnumActionResult.PASS, (Object)stack);
            }
            if (!world.isRemote) {
                world.spawnEntityInWorld((Entity)boat);
            }
            if (!player.capabilities.isCreativeMode) {
                --stack.stackSize;
            }
        }
        return new ActionResult(EnumActionResult.SUCCESS, (Object)stack);
    }

    protected EntityIC2Boat makeBoat(ItemStack stack, World world, EntityPlayer player) {
        BoatType type = (BoatType)this.getType(stack);
        if (type == null) {
            return null;
        }
        switch (type) {
            case carbon: {
                return new EntityBoatCarbon(world);
            }
            case rubber: {
                return new EntityBoatRubber(world);
            }
            case electric: {
                return new EntityBoatElectric(world);
            }
        }
        return null;
    }

    public static enum BoatType implements IIdProvider
    {
        broken_rubber,
        rubber,
        carbon,
        electric;
        

        private BoatType() {
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

