/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.DamageSource
 *  net.minecraftforge.fml.common.registry.IForgeRegistryEntry
 */
package ic2.core;

import ic2.core.IC2DamageSource;
import java.util.Arrays;
import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.fml.common.registry.IForgeRegistryEntry;

public class IC2Potion
extends Potion {
    public static IC2Potion radiation;
    private final List<ItemStack> curativeItems;

    public static void init() {
        radiation.setPotionName("ic2.potion.radiation");
        radiation.setIconIndex(6, 0);
        radiation.setEffectiveness(0.25);
    }

    public /* varargs */ IC2Potion(String name, boolean badEffect, int liquidColor, ItemStack ... curativeItems) {
        super(badEffect, liquidColor);
        this.curativeItems = Arrays.asList(curativeItems);
        this.setRegistryName(name);
    }

    public void performEffect(EntityLivingBase entity, int amplifier) {
        if (this == radiation) {
            entity.attackEntityFrom((DamageSource)IC2DamageSource.radiation, (float)(amplifier / 100) + 0.5f);
        }
    }

    public boolean isReady(int duration, int amplifier) {
        if (this == radiation) {
            int rate = 25 >> amplifier;
            return rate > 0 ? duration % rate == 0 : true;
        }
        return false;
    }

    public void applyTo(EntityLivingBase entity, int duration, int amplifier) {
        PotionEffect effect = new PotionEffect((Potion)radiation, duration, amplifier);
        effect.setCurativeItems(this.curativeItems);
        entity.addPotionEffect(effect);
    }
}

