/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.material.Material
 *  net.minecraft.block.state.IBlockState
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityPlayerSP
 *  net.minecraft.enchantment.EnchantmentHelper
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.entity.player.PlayerCapabilities
 *  net.minecraft.item.ItemStack
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.management.PlayerList
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.common.FMLCommonHandler
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.item.tool;

import ic2.api.item.ElectricItem;
import ic2.api.item.IElectricItemManager;
import ic2.core.IC2;
import ic2.core.IHitSoundOverride;
import ic2.core.Platform;
import ic2.core.item.tool.ItemElectricTool;
import ic2.core.ref.ItemName;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class ItemDrill
extends ItemElectricTool
implements IHitSoundOverride {
    protected ItemDrill(ItemName name, int operationEnergyCost, ItemElectricTool.HarvestLevel harvestLevel) {
        super(name, operationEnergyCost, harvestLevel, EnumSet.of(ItemElectricTool.ToolClass.Pickaxe, ItemElectricTool.ToolClass.Shovel));
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public String getHitSoundForBlock(EntityPlayerSP player, World world, BlockPos pos, ItemStack stack) {
        IBlockState state = world.getBlockState(pos);
        float hardness = state.getBlockHardness(world, pos);
        if (hardness > 1.0f || hardness < 0.0f) {
            return "Tools/Drill/DrillHard.ogg";
        }
        return "Tools/Drill/DrillSoft.ogg";
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public String getBreakSoundForBlock(EntityPlayerSP player, World world, BlockPos pos, ItemStack stack) {
        if (player.capabilities.isCreativeMode) {
            return null;
        }
        IBlockState state = world.getBlockState(pos);
        float hardness = state.getBlockHardness(world, pos);
        if ((double)hardness > 0.5 || !ElectricItem.manager.canUse(stack, 80.0)) {
            return null;
        }
        return "Tools/Drill/DrillSoft.ogg";
    }

    @Override
    public float getStrVsBlock(ItemStack stack, IBlockState state) {
        float speed = super.getStrVsBlock(stack, state);
        EntityPlayer player = ItemDrill.getPlayerHoldingItem(stack);
        if (player != null) {
            if (player.isInsideOfMaterial(Material.WATER) && !EnchantmentHelper.getAquaAffinityModifier((EntityLivingBase)player)) {
                speed *= 5.0f;
            }
            if (!player.onGround) {
                speed *= 5.0f;
            }
        }
        return speed;
    }

    private static EntityPlayer getPlayerHoldingItem(ItemStack stack) {
        if (IC2.platform.isRendering()) {
            return ItemDrill.getClientPlayerHoldingItem(stack);
        }
        for (EntityPlayer player : FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList().getPlayerList()) {
            if (player.inventory.getCurrentItem() != stack) continue;
            return player;
        }
        return null;
    }

    @SideOnly(value=Side.CLIENT)
    private static EntityPlayer getClientPlayerHoldingItem(ItemStack stack) {
        EntityPlayerSP player = Minecraft.getMinecraft().thePlayer;
        if (player.inventory.getCurrentItem() == stack) {
            return player;
        }
        return null;
    }
}

