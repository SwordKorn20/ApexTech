/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Optional
 *  net.minecraft.block.properties.IProperty
 */
package ic2.core.ref;

import com.google.common.base.Optional;
import ic2.core.ref.MetaTeBlock;
import ic2.core.ref.TeBlock;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import net.minecraft.block.properties.IProperty;

public class MetaTeBlockProperty
implements IProperty<MetaTeBlock> {
    private static Collection<MetaTeBlock> allowedValues = new AbstractCollection<MetaTeBlock>(){

        @Override
        public Iterator<MetaTeBlock> iterator() {
            return new Iterator<MetaTeBlock>(){
                private int teBlockIdx;
                private boolean active;

                @Override
                public boolean hasNext() {
                    return this.teBlockIdx < TeBlock.values.length;
                }

                @Override
                public MetaTeBlock next() {
                    if (!this.hasNext()) {
                        throw new NoSuchElementException();
                    }
                    TeBlock teBlock = TeBlock.values[this.teBlockIdx];
                    MetaTeBlock ret = teBlock.getMeta(this.active);
                    if (!this.active && teBlock.hasActive()) {
                        this.active = true;
                    } else {
                        this.active = false;
                        ++this.teBlockIdx;
                    }
                    return ret;
                }

                @Override
                public void remove() {
                    throw new UnsupportedOperationException();
                }
            };
        }

        @Override
        public int size() {
            return TeBlock.getMetaCount();
        }

    };

    public String getName() {
        return "type";
    }

    public Collection<MetaTeBlock> getAllowedValues() {
        return allowedValues;
    }

    public Class<MetaTeBlock> getValueClass() {
        return MetaTeBlock.class;
    }

    public Optional<MetaTeBlock> parseValue(String value) {
        return null;
    }

    public String getName(MetaTeBlock value) {
        if (value.active) {
            return value.teBlock.getName() + "_active";
        }
        return value.teBlock.getName();
    }

}

