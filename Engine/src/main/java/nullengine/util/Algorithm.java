package nullengine.util;

import static java.lang.Math.PI;

public class Algorithm {
    public static void calculateVelocityVerlet(float[] args, float time_step, float force, float mass) {
        float acceleration = args[0];
        float velocity = args[1];
        float position = args[2];

        float last_acceleration = acceleration;
        position += velocity * time_step + (0.5 * last_acceleration * time_step * time_step);
        float new_acceleration = force / mass;
        float avg_acceleration = (last_acceleration + new_acceleration) / 2;
        velocity += avg_acceleration * time_step;

        args[0] = new_acceleration;
        args[1] = velocity;
        args[2] = position;
    }
    public static double sin(float value) {
        if (value >= 0 && value <= PI / 2)
            return (((((-0.000960664 * value + 0.0102697866) * value - 0.00198601997) * value - 0.1656067221)
                * value - 0.0002715666) * value + 1.000026227) * value;
        if (value < 0)
            return -sin(-value);
        else
            return -sin((float) (value % PI));
    }
    public static double cos(float value) {
        return sin((float)PI/2-value);
    }
}