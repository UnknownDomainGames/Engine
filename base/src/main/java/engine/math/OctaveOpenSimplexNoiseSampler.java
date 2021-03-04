package engine.math;

import it.unimi.dsi.fastutil.ints.IntRBTreeSet;
import it.unimi.dsi.fastutil.ints.IntSortedSet;

import java.util.List;

public class OctaveOpenSimplexNoiseSampler extends OpenSimplexNoiseSampler {

    private final long seed;
    private IntSortedSet octaves;

    public OctaveOpenSimplexNoiseSampler(long seed, List<Integer> octaves) {
        this(seed, new IntRBTreeSet(octaves));
    }

    private OctaveOpenSimplexNoiseSampler(long seed, IntSortedSet octaves) {
        super(seed);
        this.octaves = octaves;
        if (octaves == null || octaves.isEmpty())
            throw new IllegalArgumentException();
        this.seed = seed;
    }

    @Override
    public double noise2(double x, double y) {
        return octaves.stream().mapToDouble(octave -> {
            var pow = Math.pow(2, octave);
            return 1.0 / pow * super.noise2(pow * x, pow * y);
        }).sum();
    }

    @Override
    public double noise2_XBeforeY(double x, double y) {
        return octaves.stream().mapToDouble(octave -> {
            var pow = Math.pow(2, octave);
            return 1.0 / pow * super.noise2_XBeforeY(pow * x, pow * y);
        }).sum();
    }

    @Override
    public double noise3_Classic(double x, double y, double z) {
        return octaves.stream().mapToDouble(octave -> {
            var pow = Math.pow(2, octave);
            return 1.0 / pow * super.noise3_Classic(pow * x, pow * y, pow * z);
        }).sum();
    }

    @Override
    public double noise3_XYBeforeZ(double x, double y, double z) {
        return octaves.stream().mapToDouble(octave -> {
            var pow = Math.pow(2, octave);
            return 1.0 / pow * super.noise3_XYBeforeZ(pow * x, pow * y, pow * z);
        }).sum();
    }

    @Override
    public double noise3_XZBeforeY(double x, double y, double z) {
        return octaves.stream().mapToDouble(octave -> {
            var pow = Math.pow(2, octave);
            return 1.0 / pow * super.noise3_XZBeforeY(pow * x, pow * y, pow * z);
        }).sum();
    }
}
