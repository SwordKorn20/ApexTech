/*
 * Decompiled with CFR 0_114.
 */
package ic2.core.gui;

import ic2.core.GuiIC2;
import ic2.core.gui.Gauge;
import ic2.core.gui.dynamic.IGuiValueProvider;

public class LinkedGauge
extends Gauge<LinkedGauge> {
    private final IGuiValueProvider provider;
    private final String name;

    public LinkedGauge(GuiIC2<?> gui, int x, int y, IGuiValueProvider provider, String name, Gauge.GaugeStyle style) {
        super(gui, x, y, style.properties);
        this.provider = provider;
        this.name = name;
    }

    @Override
    protected double getRatio() {
        return this.provider.getGuiValue(this.name);
    }
}

