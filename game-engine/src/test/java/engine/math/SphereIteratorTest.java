package engine.math;

import org.junit.jupiter.api.Test;

public class SphereIteratorTest {
    @Test
    public void testInRange() {
        var sphere = SphereIterator.getCoordinatesWithinSphere(12);
        while (sphere.hasNext()) {
            var next = sphere.next();
            if (next.lengthSquared() <= 144) {

            } else {
                System.out.println(String.format("outside bound! %s", next));
            }
        }
    }
}
