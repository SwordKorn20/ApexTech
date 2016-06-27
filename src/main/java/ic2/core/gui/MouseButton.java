/*
 * Decompiled with CFR 0_114.
 */
package ic2.core.gui;

public enum MouseButton {
    left(0),
    right(1);
    
    public final int id;
    private static final MouseButton[] map;

    private MouseButton(int id) {
        this.id = id;
    }

    public static MouseButton get(int id) {
        if (id < 0 || id >= map.length) {
            return null;
        }
        return map[id];
    }

    private static MouseButton[] createMap() {
        MouseButton[] values = MouseButton.values();
        int max = -1;
        for (MouseButton button2 : values) {
            if (button2.id <= max) continue;
            max = button2.id;
        }
        if (max < 0) {
            return new MouseButton[0];
        }
        MouseButton[] ret = new MouseButton[max + 1];
        MouseButton[] arrmouseButton = values;
        int n = arrmouseButton.length;
        for (int button2 = 0; button2 < n; ++button2) {
            MouseButton button3;
            ret[button3.id] = button3 = arrmouseButton[button2];
        }
        return ret;
    }

    static {
        map = MouseButton.createMap();
    }
}

