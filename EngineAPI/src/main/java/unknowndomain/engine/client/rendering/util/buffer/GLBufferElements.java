package unknowndomain.engine.client.rendering.util.buffer;

import static unknowndomain.engine.client.rendering.util.GLDataType.FLOAT;

public final class GLBufferElements {

    public static final GLBufferElement POSITION = new GLBufferElement(FLOAT, 3);
    public static final GLBufferElement COLOR_RGB = new GLBufferElement(FLOAT, 3);
    public static final GLBufferElement COLOR_RGBA = new GLBufferElement(FLOAT, 4);
    public static final GLBufferElement TEXTURE_UV = new GLBufferElement(FLOAT, 2);
    public static final GLBufferElement NORMAL = new GLBufferElement(FLOAT, 3);

    private GLBufferElements() {
    }
}
