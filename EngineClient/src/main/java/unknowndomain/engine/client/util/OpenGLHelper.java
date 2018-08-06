package unknowndomain.engine.client.util;


import org.lwjgl.opengl.GLCapabilities;

public class OpenGLHelper {

    private static GLCapabilities context;


    public static void initHelper(GLCapabilities context) {
        OpenGLHelper.context = context;
    }

    public static boolean isHelperAvailable() {
        return context != null;
    }

}
