package unknowndomain.engine.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MathsTest {

    @Test
    void sign() {
        Assertions.assertEquals(1, Maths.sign(1));
        Assertions.assertEquals(1, Maths.sign(100));

        Assertions.assertEquals(-1, Maths.sign(-1));
        Assertions.assertEquals(-1, Maths.sign(-100));

        Assertions.assertEquals(1, Maths.sign((short) 100));
        Assertions.assertEquals(-1, Maths.sign((short) -100));
    }

    @Test
    void doz() {
        Assertions.assertEquals(1, Maths.doz(1, 0));
        Assertions.assertEquals(1, Maths.doz(2, 1));
        Assertions.assertEquals(0, Maths.doz(1, 3));
    }

    @Test
    void max() {
        Assertions.assertEquals(13, Maths.max(13, 10));
        Assertions.assertEquals(100, Maths.max(100, -17));
    }
}