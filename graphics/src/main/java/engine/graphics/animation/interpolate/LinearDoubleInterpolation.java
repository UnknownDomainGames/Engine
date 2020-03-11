package engine.graphics.animation.interpolate;

public class LinearDoubleInterpolation implements Interpolation<Double> {
    @Override
    public Double interpolate(Double start, Double end, double t) {
        return start + (end - start) * t;
    }
}
