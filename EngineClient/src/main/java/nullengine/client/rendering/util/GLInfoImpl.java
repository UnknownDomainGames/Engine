package nullengine.client.rendering.util;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

public class GLInfoImpl implements GLInfo {

    private final String vendor;
    private final String renderer;
    private final String version;
    private final String extensions;
    private final String shadingLanguageVersion;

    public GLInfoImpl() {
        this.vendor = GL11.glGetString(GL11.GL_VENDOR);
        this.renderer = GL11.glGetString(GL11.GL_RENDERER);
        this.version = GL11.glGetString(GL11.GL_VERSION);
        this.extensions = GL11.glGetString(GL11.GL_EXTENSIONS);
        this.shadingLanguageVersion = GL11.glGetString(GL20.GL_SHADING_LANGUAGE_VERSION);
    }

    @Override
    public String getVendor() {
        return vendor;
    }

    @Override
    public String getRenderer() {
        return renderer;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getExtensions() {
        return extensions;
    }

    @Override
    public String getShadingLanguageVersion() {
        return shadingLanguageVersion;
    }
}
