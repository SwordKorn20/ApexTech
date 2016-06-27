/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 */
package ic2.api.reactor;

import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;

public interface IReactor {
    public TileEntity getCoreTe();

    public int getHeat();

    public void setHeat(int var1);

    public int addHeat(int var1);

    public int getMaxHeat();

    public void setMaxHeat(int var1);

    public void addEmitHeat(int var1);

    public float getHeatEffectModifier();

    public void setHeatEffectModifier(float var1);

    public float getReactorEnergyOutput();

    public double getReactorEUEnergyOutput();

    public float addOutput(float var1);

    public ItemStack getItemAt(int var1, int var2);

    public void setItemAt(int var1, int var2, ItemStack var3);

    public void explode();

    public int getTickRate();

    public boolean produceEnergy();

    public boolean isFluidCooled();
}

