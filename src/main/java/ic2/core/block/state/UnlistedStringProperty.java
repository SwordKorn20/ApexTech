/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.common.property.IUnlistedProperty
 */
package ic2.core.block.state;

import net.minecraftforge.common.property.IUnlistedProperty;

public class UnlistedStringProperty
implements IUnlistedProperty<String> {
    private final String name;

    public UnlistedStringProperty(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public boolean isValid(String value) {
        return true;
    }

    public Class<String> getType() {
        return String.class;
    }

    public String valueToString(String value) {
        return value;
    }

    public String toString() {
        return this.getClass().getSimpleName() + "{name=" + this.name + "}";
    }
}

