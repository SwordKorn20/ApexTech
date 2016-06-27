/*
 * Decompiled with CFR 0_114.
 */
package ic2.core.item.type;

import ic2.core.block.state.IIdProvider;

public final class CellType
extends Enum<CellType>
implements IIdProvider {
    private final int id;
    private static final /* synthetic */ CellType[] $VALUES;

    public static CellType[] values() {
        return (CellType[])$VALUES.clone();
    }

    public static CellType valueOf(String name) {
        return (CellType)Enum.valueOf(CellType.class, name);
    }

    private CellType(int id) {
        super(string, n);
        this.id = id;
    }

    @Override
    public String getName() {
        return this.name();
    }

    @Override
    public int getId() {
        return this.id;
    }

    static {
        $VALUES = new CellType[0];
    }
}

