package engine.graphics.graph;

import engine.util.Color;

public class ColorOutputInfo {
    private String colorBuffer;
    private boolean clear;
    private Color clearColor = Color.BLACK;

    public static ColorOutputInfo colorOutput() {
        return new ColorOutputInfo();
    }

    public String getColorBuffer() {
        return colorBuffer;
    }

    public ColorOutputInfo setColorBuffer(String colorBuffer) {
        this.colorBuffer = colorBuffer;
        return this;
    }

    public boolean isClear() {
        return clear;
    }

    public ColorOutputInfo setClear(boolean clear) {
        this.clear = clear;
        return this;
    }

    public Color getClearColor() {
        return clearColor;
    }

    public ColorOutputInfo setClearColor(Color clearColor) {
        this.clearColor = clearColor;
        return this;
    }
}
