package engine.graphics.animation.interpolate;

public class LinearFloatInterpolation implements Interpolation<Float> {
    @Override
    public Float interpolate(Float start, Float end, double t) {
        return start + (float)((end - start) * t);
    }
}
