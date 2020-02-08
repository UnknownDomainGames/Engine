package engine.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ReflectionUtilsTest {

    @Test
    public void testGetCallerClassName() {
        assertEquals(ReflectionUtilsTest.class.getName(), dummyMethod().getClassName());
    }

    private StackTraceElement dummyMethod() {
        return ReflectionUtils.getCallerElement();
    }
}
