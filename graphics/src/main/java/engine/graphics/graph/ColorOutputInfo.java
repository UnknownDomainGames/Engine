package engine.graphics.graph;

import engine.graphics.util.BlendMode;
import engine.util.Color;

public class ColorOutputInfo {
    private String colorBuffer;
    private boolean clear = false;
    private Color clearColor = Color.BLACK;
    private float[] clearColorArray = {0f, 0f, 0f, 1f};
    private BlendMode blendMode = BlendMode.DISABLED;

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

    public float[] getClearColorArray() {
        return clearColorArray;
    }

    public ColorOutputInfo setClearColor(Color clearColor) {
        this.clearColor = clearColor;
        this.clearColor.get(0, clearColorArray);
        return this;
    }

    public BlendMode getBlendMode() {
        return blendMode;
    }

    public ColorOutputInfo setBlendMode(BlendMode blendMode) {
        this.blendMode = blendMode;
        return this;
    }
}
