package unknowndomain.engine.math;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Math2Test {

    @Test
    public void testCeilPowerOfTwo() {
        assertEquals(Math2.ceilPowerOfTwo(1), 1);
        assertEquals(Math2.ceilPowerOfTwo(2), 2);
        assertEquals(Math2.ceilPowerOfTwo(3), 4);
        assertEquals(Math2.ceilPowerOfTwo(4), 4);
        assertEquals(Math2.ceilPowerOfTwo(5), 8);
    }

    @Test
    public void testGetUsingBits() {
        assertEquals(Math2.getUsingBits(0), 0);
        assertEquals(Math2.getUsingBits(1), 1);
        assertEquals(Math2.getUsingBits(2), 2);
        assertEquals(Math2.getUsingBits(3), 2);
        assertEquals(Math2.getUsingBits(4), 3);
        assertEquals(Math2.getUsingBits(-1), 32);
    }

    @Test
    public void testCeil() {
        assertEquals(Math2.ceil(0, 16), 0);
        assertEquals(Math2.ceil(1, 16), 16);
        assertEquals(Math2.ceil(15, 16), 16);
        assertEquals(Math2.ceil(16, 16), 16);
        assertEquals(Math2.ceil(17, 16), 32);
        assertEquals(Math2.ceil(-1, 16), -16);
    }

    @Test
    public void testLoop() {
        assertEquals(Math2.loop(0, 360), 0);
        assertEquals(Math2.loop(90, 360), 90);
        assertEquals(Math2.loop(360, 360), 0);
        assertEquals(Math2.loop(180 + 360, 360), 180);
        assertEquals(Math2.loop(-90, 360), 270);
        assertEquals(Math2.loop(-180, 360), 180);
        assertEquals(Math2.loop(-360, 360), 0);
        assertEquals(Math2.loop(-360 - 90, 360), 270);
    }
}
