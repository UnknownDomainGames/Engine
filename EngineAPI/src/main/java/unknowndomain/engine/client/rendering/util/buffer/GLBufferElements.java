package unknowndomain.engine.client.rendering.util.buffer;

import unknowndomain.engine.client.rendering.util.buffer.GLBufferElement.Usage;

import static unknowndomain.engine.client.rendering.util.GLDataType.FLOAT;

public final class GLBufferElements {

    public static final GLBufferElement POSITION = new GLBufferElement(FLOAT, Usage.POSITION, 3);
    public static final GLBufferElement COLOR_RGB = new GLBufferElement(FLOAT, Usage.COLOR, 3);
    public static final GLBufferElement COLOR_RGBA = new GLBufferElement(FLOAT, Usage.COLOR, 4);
    public static final GLBufferElement TEXTURE_UV = new GLBufferElement(FLOAT, Usage.TEXTURE_UV, 2);
    public static final GLBufferElement NORMAL = new GLBufferElement(FLOAT, Usage.NORMAL, 3);

    private GLBufferElements() {
    }
}
