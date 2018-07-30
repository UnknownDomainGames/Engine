package unknowndomain.engine.util;

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
}