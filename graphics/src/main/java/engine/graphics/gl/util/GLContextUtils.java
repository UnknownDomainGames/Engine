package engine.graphics.gl.util;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GLCapabilities;

public final class GLContextUtils {

    private static GLCapabilities capabilities;

    public static GLCapabilities getCapabilities() {
        return capabilities;
    }

    public static void setCapabilities(GLCapabilities capabilities) {
        GLContextUtils.capabilities = capabilities;
    }

    public static String getVendor() {
        return GL11.glGetString(GL11.GL_VENDOR);
    }

    public static String getRenderer() {
        return GL11.glGetString(GL11.GL_RENDERER);
    }

    public static String getVersion() {
        return GL11.glGetString(GL11.GL_VERSION);
    }

    public static String getExtensions() {
        return GL11.glGetString(GL11.GL_EXTENSIONS);
    }

    public static String getShadingLanguageVersion() {
        return GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION);
    }

    private GLContextUtils() {
    }
}
