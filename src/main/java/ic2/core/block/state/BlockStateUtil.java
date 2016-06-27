/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableSet
 *  net.minecraft.block.Block
 *  net.minecraft.block.properties.IProperty
 *  net.minecraft.block.state.IBlockState
 */
package ic2.core.block.state;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import java.util.Collection;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;

public class BlockStateUtil {
    public static String getVariantString(IBlockState state) {
        ImmutableMap properties = state.getProperties();
        if (properties.isEmpty()) {
            return "normal";
        }
        StringBuilder ret = new StringBuilder();
        for (Map.Entry entry : properties.entrySet()) {
            IProperty property = (IProperty)entry.getKey();
            if (ret.length() > 0) {
                ret.append(',');
            }
            ret.append(property.getName());
            ret.append('=');
            ret.append(property.getName((Comparable)entry.getValue()));
        }
        return ret.toString();
    }

    public static IBlockState getState(Block block, String variant) {
        IBlockState ret = block.getDefaultState();
        if (variant.isEmpty() || variant.equals("normal")) {
            return ret;
        }
        int pos = 0;
        while (pos < variant.length()) {
            int sepPos;
            int nextPos = variant.indexOf(44, pos);
            if (nextPos == -1) {
                nextPos = variant.length();
            }
            if ((sepPos = variant.indexOf(61, pos)) == -1 || sepPos >= nextPos) {
                return null;
            }
            String name = variant.substring(pos, sepPos);
            String value = variant.substring(sepPos + 1, nextPos);
            ret = BlockStateUtil.applyProperty(ret, name, value);
            pos = nextPos + 1;
        }
        return ret;
    }

    private static <T extends Comparable<T>> IBlockState applyProperty(IBlockState state, String name, String value) {
        IProperty property = null;
        for (IProperty cProperty : state.getPropertyNames()) {
            if (!cProperty.getName().equals(name)) continue;
            property = cProperty;
            break;
        }
        if (property == null) {
            return state;
        }
        for (Comparable cValue : property.getAllowedValues()) {
            if (!value.equals(property.getName(cValue))) continue;
            return state.withProperty(property, cValue);
        }
        return state;
    }
}

