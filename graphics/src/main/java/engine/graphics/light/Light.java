package engine.graphics.light;

import engine.graphics.util.Struct;
import engine.util.Color;
import org.joml.Matrix4fc;

public abstract class Light implements Struct {
    protected Color color;
    protected float intensity;

    public Light() {
    }

    public abstract void update(Matrix4fc viewMatrix);

    public Color getColor() {
        return color;
    }

    public Light setColor(Color color) {
        this.color = color;
        return this;
    }

    public float getIntensity() {
        return intensity;
    }

    public Light setIntensity(float intensity) {
        this.intensity = intensity;
        return this;
    }
}
