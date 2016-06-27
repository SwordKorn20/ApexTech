/*
 * Decompiled with CFR 0_114.
 */
package ic2.core.ref;

import ic2.core.ref.TeBlock;

public class MetaTeBlock
implements Comparable<MetaTeBlock> {
    public final TeBlock teBlock;
    public final boolean active;

    MetaTeBlock(TeBlock teBlock, boolean active) {
        this.teBlock = teBlock;
        this.active = active;
    }

    @Override
    public int compareTo(MetaTeBlock o) {
        int ret = this.teBlock.compareTo(o.teBlock);
        if (ret != 0) {
            return ret;
        }
        return Boolean.compare(this.active, o.active);
    }
}

