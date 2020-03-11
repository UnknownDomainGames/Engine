package engine.graphics.animation.interpolate;

public class LinearIntInterpolation implements Interpolation<Integer> {
    @Override
    public Integer interpolate(Integer start, Integer end, double t) {
        return start + (int)Math.round((end - start) * t);
    }
}
