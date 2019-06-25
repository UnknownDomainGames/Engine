package nullengine.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReflectionUtilsTest {

    @Test
    public void testGetCallerClassName() {
        dummyMethod();
    }

    public void dummyMethod() {
        assertEquals(ReflectionUtils.getCallerClassName(), ReflectionUtilsTest.class.getName());
    }
}
