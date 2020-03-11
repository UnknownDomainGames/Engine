package engine.graphics.animation;

import engine.graphics.animation.interpolate.Interpolation;

public class AnimationKeyframe<T> {
    private double timestamp;
    private T value;
    private Interpolation<T> interpolation;

    public T getValue() {
        return value;
    }

    public double getTimestamp() {
        return timestamp;
    }

    void setTimestamp(double timestamp) {
        this.timestamp = timestamp;
    }

    public Interpolation<T> getInterpolation() {
        return interpolation;
    }
}
