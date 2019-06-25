package nullengine.client.rendering.util.buffer;

public class GLBufferFormats {
    public static final GLBufferFormat POSITION = GLBufferFormat.of(GLBufferElements.POSITION);
    public static final GLBufferFormat POSITION_COLOR = GLBufferFormat.of(GLBufferElements.POSITION, GLBufferElements.COLOR_RGB);
    public static final GLBufferFormat POSITION_COLOR_ALPHA = GLBufferFormat.of(GLBufferElements.POSITION, GLBufferElements.COLOR_RGBA);
    public static final GLBufferFormat POSITION_TEXTURE = GLBufferFormat.of(GLBufferElements.POSITION, GLBufferElements.TEXTURE_UV);
    public static final GLBufferFormat POSITION_TEXTURE_NORMAL = GLBufferFormat.of(GLBufferElements.POSITION, GLBufferElements.TEXTURE_UV, GLBufferElements.NORMAL);
    public static final GLBufferFormat POSITION_COLOR_TEXTURE = GLBufferFormat.of(GLBufferElements.POSITION, GLBufferElements.COLOR_RGB, GLBufferElements.TEXTURE_UV);
    public static final GLBufferFormat POSITION_COLOR_ALPHA_TEXTURE = GLBufferFormat.of(GLBufferElements.POSITION, GLBufferElements.COLOR_RGBA, GLBufferElements.TEXTURE_UV);
    public static final GLBufferFormat POSITION_COLOR_TEXTURE_NORMAL = GLBufferFormat.of(GLBufferElements.POSITION, GLBufferElements.COLOR_RGB, GLBufferElements.TEXTURE_UV, GLBufferElements.NORMAL);
    public static final GLBufferFormat POSITION_COLOR_ALPHA_TEXTURE_NORMAL = GLBufferFormat.of(GLBufferElements.POSITION, GLBufferElements.COLOR_RGBA, GLBufferElements.TEXTURE_UV, GLBufferElements.NORMAL);

}
