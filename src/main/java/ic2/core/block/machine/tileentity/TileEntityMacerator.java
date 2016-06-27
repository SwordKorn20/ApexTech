/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumParticleTypes
 *  net.minecraft.util.math.BlockPos
 *  net.minecraft.world.World
 *  net.minecraftforge.fml.relauncher.Side
 *  net.minecraftforge.fml.relauncher.SideOnly
 */
package ic2.core.block.machine.tileentity;

import ic2.api.recipe.IMachineRecipeManager;
import ic2.api.recipe.Recipes;
import ic2.core.block.TileEntityInventory;
import ic2.core.block.invslot.InvSlotProcessable;
import ic2.core.block.invslot.InvSlotProcessableGeneric;
import ic2.core.block.machine.tileentity.TileEntityStandardMachine;
import ic2.core.recipe.BasicMachineRecipeManager;
import ic2.core.upgrade.UpgradableProperty;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Vector;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TileEntityMacerator
extends TileEntityStandardMachine {
    public static List<Map.Entry<ItemStack, ItemStack>> recipes = new Vector<Map.Entry<ItemStack, ItemStack>>();

    public TileEntityMacerator() {
        super(2, 300, 1);
        this.inputSlot = new InvSlotProcessableGeneric(this, "input", 1, Recipes.macerator);
    }

    public static void init() {
        Recipes.macerator = new BasicMachineRecipeManager();
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    protected void updateEntityClient() {
        super.updateEntityClient();
        if (this.getActive() && this.worldObj.rand.nextInt(8) == 0) {
            for (int i = 0; i < 4; ++i) {
                double x = (double)this.pos.getX() + 0.5 + (double)this.worldObj.rand.nextFloat() * 0.6 - 0.3;
                double y = (double)(this.pos.getY() + 1) + (double)this.worldObj.rand.nextFloat() * 0.2 - 0.1;
                double z = (double)this.pos.getZ() + 0.5 + (double)this.worldObj.rand.nextFloat() * 0.6 - 0.3;
                this.worldObj.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, x, y, z, 0.0, 0.0, 0.0, new int[0]);
            }
        }
    }

    @Override
    public String getStartSoundFile() {
        return "Machines/MaceratorOp.ogg";
    }

    @Override
    public String getInterruptSoundFile() {
        return "Machines/InterruptOne.ogg";
    }

    @Override
    public Set<UpgradableProperty> getUpgradableProperties() {
        return EnumSet.of(UpgradableProperty.Processing, UpgradableProperty.Transformer, UpgradableProperty.EnergyStorage, UpgradableProperty.ItemConsuming, UpgradableProperty.ItemProducing);
    }
}

