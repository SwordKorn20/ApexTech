/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.inventory.EntityEquipmentSlot
 *  net.minecraft.inventory.EntityEquipmentSlot$Type
 */
package ic2.core.slot;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.minecraft.inventory.EntityEquipmentSlot;

public class ArmorSlot {
    private static final EntityEquipmentSlot[] armorSlots = ArmorSlot.getArmorSlots();
    private static final List<EntityEquipmentSlot> armorSlotList = Collections.unmodifiableList(Arrays.asList(armorSlots));

    public static EntityEquipmentSlot get(int index) {
        return armorSlots[index];
    }

    public static int getCount() {
        return armorSlots.length;
    }

    public static Iterable<EntityEquipmentSlot> getAll() {
        return armorSlotList;
    }

    private static EntityEquipmentSlot[] getArmorSlots() {
        int i;
        EntityEquipmentSlot[] values = EntityEquipmentSlot.values();
        int count = 0;
        for (EntityEquipmentSlot slot : values) {
            if (slot.getSlotType() != EntityEquipmentSlot.Type.ARMOR) continue;
            ++count;
        }
        EntityEquipmentSlot[] ret = new EntityEquipmentSlot[count];
        block1 : for (i = 0; i < ret.length; ++i) {
            for (EntityEquipmentSlot slot2 : values) {
                if (slot2.getSlotType() != EntityEquipmentSlot.Type.ARMOR || slot2.getIndex() != i) continue;
                ret[i] = slot2;
                continue block1;
            }
        }
        for (i = 0; i < ret.length; ++i) {
            if (ret[i] != null) continue;
            throw new RuntimeException("Can't find an armor mapping for idx " + i);
        }
        return ret;
    }
}

