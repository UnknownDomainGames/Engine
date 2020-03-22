package engine.math;

import org.junit.jupiter.api.Test;

import static engine.math.Math2.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Math2Test {

    @Test
    public void testCeilPowerOfTwo() {
        assertThrows(IllegalArgumentException.class, () -> ceilPowerOfTwo(0));
        assertEquals(1, ceilPowerOfTwo(1));
        assertEquals(2, ceilPowerOfTwo(2));
        assertEquals(4, ceilPowerOfTwo(3));
    }

    @Test
    public void testGetUsingBits() {
        assertEquals(0, getUsingBits(0));
        assertEquals(2, getUsingBits(1), 1);
        assertEquals(2, getUsingBits(2), 2);
        assertEquals(3, getUsingBits(4));
        assertEquals(32, getUsingBits(-1));
    }

    @Test
    public void testCeil() {
        assertEquals(0, ceil(0, 16));
        assertEquals(16, ceil(1, 16));
        assertEquals(16, ceil(15, 16));
        assertEquals(16, ceil(16, 16));
        assertEquals(32, ceil(17, 16));
        assertEquals(-16, ceil(-1, 16));
        assertEquals(-16, ceil(-15, 16));
        assertEquals(-16, ceil(-16, 16));
        assertEquals(-32, ceil(-17, 16));
    }

    @Test
    public void testLoop() {
        assertEquals(0, loop(0, 360));
        assertEquals(90, loop(90, 360));
        assertEquals(0, loop(360, 360));
        assertEquals(90, loop(360 + 90, 360));
        assertEquals(270, loop(-90, 360));
        assertEquals(90, loop(-270, 360));
        assertEquals(0, loop(-360, 360));
        assertEquals(270, loop(-360 - 90, 360));
    }

    @Test
    void testCeilDiv() {
        assertEquals(1, ceilDiv(3, 3));
        assertEquals(1, ceilDiv(2, 3));
        assertEquals(2, ceilDiv(4, 3));
        assertEquals(-1, ceilDiv(-3, 3));
        assertEquals(-1, ceilDiv(-4, 3));
        assertEquals(0, ceilDiv(-2, 3));
    }
}
