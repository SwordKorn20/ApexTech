/*
 * Decompiled with CFR 0_114.
 */
package ic2.core.gui;

import ic2.core.GuiIC2;
import ic2.core.gui.Gauge;

public class CustomGauge
extends Gauge<CustomGauge> {
    private final IGaugeRatioProvider provider;

    public static CustomGauge asFuel(GuiIC2<?> gui, int x, int y, IGaugeRatioProvider provider) {
        return new CustomGauge(gui, x, y, provider, Gauge.GaugeStyle.Fuel.properties);
    }

    public static CustomGauge create(GuiIC2<?> gui, int x, int y, IGaugeRatioProvider provider, Gauge.GaugeStyle style) {
        return new CustomGauge(gui, x, y, provider, style.properties);
    }

    public CustomGauge(GuiIC2<?> gui, int x, int y, IGaugeRatioProvider provider, Gauge.GaugeProperties properties) {
        super(gui, x, y, properties);
        this.provider = provider;
    }

    @Override
    protected double getRatio() {
        return this.provider.getRatio();
    }

    public static interface IGaugeRatioProvider {
        public double getRatio();
    }

}

