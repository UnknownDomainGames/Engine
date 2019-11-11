package nullengine.client.rendering.gl.util;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public final class GLContextUtils {

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
