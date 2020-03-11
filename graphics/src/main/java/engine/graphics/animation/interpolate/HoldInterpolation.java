package engine.graphics.animation.interpolate;

public class HoldInterpolation<T> implements Interpolation<T> {

    /**
     * Ceiling flag:
     *
     * When this is true, the interpolation will act like ceil(), which means the value of this keyframe will be used for t in (0,1)
     *
     * Otherwise, the interpolation will act like floor(), which means the value of the previous keyframe will be used for t in (0,1)
     */
    private final boolean ceiling;

    public HoldInterpolation() {
        this(false);
    }

    public HoldInterpolation(boolean ceiling) {
        this.ceiling = ceiling;
    }

    @Override
    public T interpolate(T start, T end, double t) {
        if (ceiling) {
            if (t > 0) {
                return end;
            }
            return start;
        }
        else {
            if (t < 1) {
                return start;
            }
            return end;
        }
    }
}
