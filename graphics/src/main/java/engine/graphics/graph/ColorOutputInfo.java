package engine.graphics.graph;

import engine.util.Color;

public class ColorOutputInfo {
    private String name;
    private boolean clear;
    private Color clearColor = Color.BLACK;

    public static ColorOutputInfo colorOutput() {
        return new ColorOutputInfo();
    }

    public ColorOutputInfo name(String name) {
        this.name = name;
        return this;
    }

    public ColorOutputInfo clear() {
        this.clear = true;
        return this;
    }

    public ColorOutputInfo clearColor(Color color) {
        this.clearColor = color;
        return this;
    }

    public String getName() {
        return name;
    }

    public boolean isClear() {
        return clear;
    }

    public Color getClearColor() {
        return clearColor;
    }
}
