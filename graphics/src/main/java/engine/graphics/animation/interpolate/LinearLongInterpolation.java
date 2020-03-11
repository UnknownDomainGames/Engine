package engine.graphics.animation.interpolate;

public class LinearLongInterpolation implements Interpolation<Long> {
    @Override
    public Long interpolate(Long start, Long end, double t) {
        return start + Math.round((end - start) * t);
    }
}
