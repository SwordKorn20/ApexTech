/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.ResourceLocation
 */
package ic2.core.gui;

import ic2.core.GuiIC2;
import ic2.core.gui.GuiElement;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import net.minecraft.util.ResourceLocation;

public abstract class Gauge<T extends Gauge<T>>
extends GuiElement<T> {
    protected final GaugeProperties properties;

    protected Gauge(GuiIC2<?> gui, int x, int y, GaugeProperties properties) {
        super(gui, x + properties.hoverXOffset, y + properties.hoverYOffset, properties.hoverWidth, properties.hoverHeight);
        this.properties = properties;
    }

    protected abstract double getRatio();

    protected boolean isActive(double ratio) {
        return ratio > 0.0;
    }

    @Override
    public void drawBackground(int mouseX, int mouseY) {
        double ratio = this.getRatio();
        if (ratio <= 0.0 && this.properties.bgWidth <= 0) {
            return;
        }
        Gauge.bindTexture(this.properties.texture);
        double x = this.x - this.properties.hoverXOffset;
        double y = this.y - this.properties.hoverYOffset;
        if (this.properties.bgWidth >= 0) {
            boolean active = this.isActive(ratio);
            this.gui.drawTexturedRect(x + (double)this.properties.bgXOffset, y + (double)this.properties.bgYOffset, this.properties.bgWidth, this.properties.bgHeight, active ? (double)this.properties.uBgActive : (double)this.properties.uBgInactive, active ? (double)this.properties.vBgActive : (double)this.properties.vBgInactive);
            if (ratio <= 0.0) {
                return;
            }
        }
        ratio = Math.min(ratio, 1.0);
        double u = this.properties.uInner;
        double v = this.properties.vInner;
        double width = this.properties.innerWidth;
        double height = this.properties.innerHeight;
        double size = this.properties.vertical ? height : width;
        double renderSize = ratio * size;
        if (!this.properties.smooth) {
            renderSize = Math.round(renderSize);
        }
        if (renderSize <= 0.0) {
            return;
        }
        if (this.properties.vertical) {
            if (this.properties.reverse) {
                v += height - renderSize;
                y += height - renderSize;
            }
            height = renderSize;
        } else {
            if (this.properties.reverse) {
                u += width - renderSize;
                x += width - renderSize;
            }
            width = renderSize;
        }
        this.gui.drawTexturedRect(x, y, width, height, u, v);
    }

    public static enum GaugeStyle {
        Fuel(new GaugePropertyBuilder(112, 80, 13, 13, GaugePropertyBuilder.GaugeOrientation.Up).withHover(0, 0, 14, 14).withBackground(0, 0, 16, 16, 96, 80).build()),
        ProgressArrow(new GaugePropertyBuilder(165, 16, 22, 15, GaugePropertyBuilder.GaugeOrientation.Right).withBackground(-5, 0, 32, 16, 160, 0).build()),
        ProgressCrush(new GaugePropertyBuilder(165, 52, 21, 11, GaugePropertyBuilder.GaugeOrientation.Right).withBackground(-5, -3, 32, 16, 160, 32).build()),
        ProgressTriangle(new GaugePropertyBuilder(165, 80, 22, 15, GaugePropertyBuilder.GaugeOrientation.Right).withBackground(-5, 0, 32, 16, 160, 64).build()),
        ProgressDrop(new GaugePropertyBuilder(165, 112, 22, 15, GaugePropertyBuilder.GaugeOrientation.Right).withBackground(-5, 0, 32, 16, 160, 96).build()),
        ProgressRecycler(new GaugePropertyBuilder(133, 80, 18, 15, GaugePropertyBuilder.GaugeOrientation.Right).withBackground(-5, 0, 32, 16, 128, 64).build()),
        ProgressMetalFormer(new GaugePropertyBuilder(200, 19, 46, 9, GaugePropertyBuilder.GaugeOrientation.Right).withBackground(-8, -3, 64, 16, 192, 0).build()),
        ProgressCentrifuge(new GaugePropertyBuilder(252, 33, 3, 28, GaugePropertyBuilder.GaugeOrientation.Up).withBackground(-1, -1, 5, 30, 246, 32).build()),
        HeatCentrifuge(new GaugePropertyBuilder(225, 54, 20, 4, GaugePropertyBuilder.GaugeOrientation.Right).withBackground(-1, -1, 22, 6, 224, 47).build()),
        HeatNuclearReactor(new GaugePropertyBuilder(0, 243, 100, 13, GaugePropertyBuilder.GaugeOrientation.Right).withHoverBorder(1).withTexture(new ResourceLocation("ic2", "textures/gui/GUINuclearReactor.png")).build()),
        HeatSteamGenerator(new GaugePropertyBuilder(177, 1, 7, 76, GaugePropertyBuilder.GaugeOrientation.Up).withHoverBorder(1).withTexture(new ResourceLocation("ic2", "textures/gui/GUISteamGenerator.png")).build()),
        CalcificationSteamGenerator(new GaugePropertyBuilder(187, 1, 7, 58, GaugePropertyBuilder.GaugeOrientation.Up).withHoverBorder(1).withTexture(new ResourceLocation("ic2", "textures/gui/GUISteamGenerator.png")).build()),
        ProgressCondenser(new GaugePropertyBuilder(1, 185, 82, 7, GaugePropertyBuilder.GaugeOrientation.Right).withHoverBorder(1).withTexture(new ResourceLocation("ic2", "textures/gui/GUICondenser.png")).build()),
        HeatFermenter(new GaugePropertyBuilder(177, 10, 40, 3, GaugePropertyBuilder.GaugeOrientation.Right).withHoverBorder(1).withTexture(new ResourceLocation("ic2", "textures/gui/GUIFermenter.png")).build()),
        ProgressFermenter(new GaugePropertyBuilder(177, 1, 40, 7, GaugePropertyBuilder.GaugeOrientation.Right).withHoverBorder(1).withTexture(new ResourceLocation("ic2", "textures/gui/GUIFermenter.png")).build()),
        ProgressOreWasher(new GaugePropertyBuilder(177, 118, 18, 18, GaugePropertyBuilder.GaugeOrientation.Right).withTexture(new ResourceLocation("ic2", "textures/gui/GUIOreWashingPlant.png")).withBackground(-1, -1, 20, 19, 102, 38).build());
        
        private static final Map<String, GaugeStyle> map;
        public final String name;
        public final GaugeProperties properties;

        private GaugeStyle(GaugeProperties properties) {
            this.name = this.name().toLowerCase(Locale.ENGLISH);
            this.properties = properties;
        }

        public static GaugeStyle get(String name) {
            return map.get(name);
        }

        private static Map<String, GaugeStyle> getMap() {
            GaugeStyle[] values = GaugeStyle.values();
            HashMap<String, GaugeStyle> ret = new HashMap<String, GaugeStyle>(values.length);
            for (GaugeStyle style : values) {
                ret.put(style.name, style);
            }
            return ret;
        }

        static {
            map = GaugeStyle.getMap();
        }
    }

    public static class GaugeProperties {
        public final short uInner;
        public final short vInner;
        public final short innerWidth;
        public final short innerHeight;
        public final short hoverXOffset;
        public final short hoverYOffset;
        public final short hoverWidth;
        public final short hoverHeight;
        public final short bgXOffset;
        public final short bgYOffset;
        public final short bgWidth;
        public final short bgHeight;
        public final short uBgInactive;
        public final short vBgInactive;
        public final short uBgActive;
        public final short vBgActive;
        public final boolean vertical;
        public final boolean reverse;
        public final boolean smooth;
        public final ResourceLocation texture;

        public GaugeProperties(int uInner, int vInner, int innerWidth, int innerHeight, int hoverXOffset, int hoverYOffset, int hoverWidth, int hoverHeight, int bgXOffset, int bgYOffset, int bgWidth, int bgHeight, int uBgInactive, int vBgInactive, int uBgActive, int vBgActive, boolean vertical, boolean reverse, boolean smooth, ResourceLocation texture) {
            this.uInner = (short)uInner;
            this.vInner = (short)vInner;
            this.innerWidth = (short)innerWidth;
            this.innerHeight = (short)innerHeight;
            this.hoverXOffset = (short)hoverXOffset;
            this.hoverYOffset = (short)hoverYOffset;
            this.hoverWidth = (short)hoverWidth;
            this.hoverHeight = (short)hoverHeight;
            this.bgXOffset = (short)bgXOffset;
            this.bgYOffset = (short)bgYOffset;
            this.bgWidth = (short)bgWidth;
            this.bgHeight = (short)bgHeight;
            this.uBgInactive = (short)uBgInactive;
            this.vBgInactive = (short)vBgInactive;
            this.uBgActive = (short)uBgActive;
            this.vBgActive = (short)vBgActive;
            this.vertical = vertical;
            this.reverse = reverse;
            this.smooth = smooth;
            this.texture = texture;
        }
    }

    public static class GaugePropertyBuilder {
        private final short uInner;
        private final short vInner;
        private final short innerWidth;
        private final short innerHeight;
        private short hoverXOffset;
        private short hoverYOffset;
        private short hoverWidth;
        private short hoverHeight;
        private short bgXOffset;
        private short bgYOffset;
        private short bgWidth;
        private short bgHeight;
        private short uBgInactive;
        private short vBgInactive;
        private short uBgActive;
        private short vBgActive;
        private final boolean vertical;
        private final boolean reverse;
        private boolean smooth = true;
        private ResourceLocation texture = GuiElement.commonTexture;

        public GaugePropertyBuilder(int uInner, int vInner, int innerWidth, int innerHeight, GaugeOrientation dir) {
            this.uInner = GaugePropertyBuilder.toShort(uInner);
            this.vInner = GaugePropertyBuilder.toShort(vInner);
            this.innerWidth = this.hoverWidth = GaugePropertyBuilder.toShort(innerWidth);
            this.innerHeight = this.hoverHeight = GaugePropertyBuilder.toShort(innerHeight);
            this.vertical = dir.vertical;
            this.reverse = dir.reverse;
        }

        public GaugePropertyBuilder withHoverBorder(int border) {
            this.hoverXOffset = GaugePropertyBuilder.toShort(- border);
            this.hoverYOffset = GaugePropertyBuilder.toShort(- border);
            this.hoverWidth = GaugePropertyBuilder.toShort(this.innerWidth + 2 * border);
            this.hoverHeight = GaugePropertyBuilder.toShort(this.innerHeight + 2 * border);
            return this;
        }

        public GaugePropertyBuilder withHover(int hoverXOffset, int hoverYOffset, int hoverWidth, int hoverHeight) {
            this.hoverXOffset = GaugePropertyBuilder.toShort(hoverXOffset);
            this.hoverYOffset = GaugePropertyBuilder.toShort(hoverYOffset);
            this.hoverWidth = GaugePropertyBuilder.toShort(hoverWidth);
            this.hoverHeight = GaugePropertyBuilder.toShort(hoverHeight);
            return this;
        }

        public GaugePropertyBuilder withBackground(int uBg, int vBg) {
            return this.withBackground(0, 0, this.innerWidth, this.innerHeight, uBg, vBg);
        }

        public GaugePropertyBuilder withBackground(int bgXOffset, int bgYOffset, int bgWidth, int bgHeight, int uBg, int vBg) {
            return this.withBackground(bgXOffset, bgYOffset, bgWidth, bgHeight, uBg, vBg, uBg, vBg);
        }

        public GaugePropertyBuilder withBackground(int uBgInactive, int vBgInactive, int uBgActive, int vBgActive) {
            return this.withBackground(0, 0, this.innerWidth, this.innerHeight, uBgInactive, vBgInactive, uBgActive, vBgActive);
        }

        public GaugePropertyBuilder withBackground(int bgXOffset, int bgYOffset, int bgWidth, int bgHeight, int uBgInactive, int vBgInactive, int uBgActive, int vBgActive) {
            this.bgXOffset = GaugePropertyBuilder.toShort(bgXOffset);
            this.bgYOffset = GaugePropertyBuilder.toShort(bgYOffset);
            this.bgWidth = GaugePropertyBuilder.toShort(bgWidth);
            this.bgHeight = GaugePropertyBuilder.toShort(bgHeight);
            this.uBgInactive = GaugePropertyBuilder.toShort(uBgInactive);
            this.vBgInactive = GaugePropertyBuilder.toShort(vBgInactive);
            this.uBgActive = GaugePropertyBuilder.toShort(uBgActive);
            this.vBgActive = GaugePropertyBuilder.toShort(vBgActive);
            return this;
        }

        public GaugePropertyBuilder withSmooth(boolean smooth) {
            this.smooth = smooth;
            return this;
        }

        public GaugePropertyBuilder withTexture(ResourceLocation texture) {
            this.texture = texture;
            return this;
        }

        public GaugeProperties build() {
            return new GaugeProperties(this.uInner, this.vInner, this.innerWidth, this.innerHeight, this.hoverXOffset, this.hoverYOffset, this.hoverWidth, this.hoverHeight, this.bgXOffset, this.bgYOffset, this.bgWidth, this.bgHeight, this.uBgInactive, this.vBgInactive, this.uBgActive, this.vBgActive, this.vertical, this.reverse, this.smooth, this.texture);
        }

        private static short toShort(int value) {
            return (short)value;
        }

        public static enum GaugeOrientation {
            Up(true, true),
            Down(true, false),
            Left(false, true),
            Right(false, false);
            
            final boolean vertical;
            final boolean reverse;

            private GaugeOrientation(boolean vertical, boolean reverse) {
                this.vertical = vertical;
                this.reverse = reverse;
            }
        }

    }

}

