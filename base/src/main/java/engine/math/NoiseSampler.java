package engine.math;

public interface NoiseSampler {

    double sample(double x, double y);

    double sample(double x, double y, double z);

}
