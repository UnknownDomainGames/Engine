package engine.math;

import org.joml.Vector3i;
import org.joml.Vector3ic;

import java.util.Iterator;

public class SphereIterator implements Iterator<Vector3i> {

    private SphereIterator(int radius, Vector3ic center) {
        this.radius = radius;
        this.center = center;

        i = -radius;
        yBoundSq = radius * radius - i * i;
        yBound = (int) Math.sqrt(yBoundSq);
        j = -yBound;
        zBoundSq = yBoundSq - j * j;
        zBound = (int) Math.sqrt(zBoundSq);
        k = -zBound;
    }

    public static SphereIterator getCoordinatesWithinSphere(int radius) {
        return getCoordinatesWithinSphere(radius, new Vector3i());
    }

    public static SphereIterator getCoordinatesWithinSphere(int radius, Vector3ic center) {
        if (radius < 0) {
            throw new IllegalArgumentException("radius cannot be a negative number");
        }
        return new SphereIterator(radius, center);
    }

    private final int radius;
    private final Vector3ic center;

    private int i, j, k;
    private int zBound, yBound;
    private int zBoundSq, yBoundSq;

    @Override
    public boolean hasNext() {
        return i <= radius && j <= yBound && k <= zBound;
    }

    @Override
    public Vector3i next() {
        var coord = new Vector3i(i, j, k);
        if (k >= zBound) {
            if (j >= yBound) {
                i++;
                yBoundSq = radius * radius - i * i;
                yBound = (int) Math.sqrt(yBoundSq);
                j = -yBound;
            } else {
                j++;
            }
            zBoundSq = yBoundSq - j * j;
            zBound = (int) Math.sqrt(zBoundSq);
            k = -zBound;
        } else {
            k++;
        }
        return coord.add(center);
    }
}
