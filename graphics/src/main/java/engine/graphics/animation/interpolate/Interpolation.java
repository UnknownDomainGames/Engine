package engine.graphics.animation.interpolate;

/**
 * Interpolation of a keyframe
 *
 * this will interpolate a value between the <b>previous</b> keyframe and this keyframe
 * @param <T>
 */
public interface Interpolation<T> {
    /**
     * Interpolate a value.
     *
     * @param start the value when t<=0
     * @param end the value when t>=1
     * @param t time in [0,1]
     * @return interpolated value with respect to t
     */
    T interpolate(T start, T end, double t);
}
