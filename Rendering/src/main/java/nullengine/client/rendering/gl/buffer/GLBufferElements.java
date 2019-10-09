package nullengine.client.rendering.gl.buffer;

import static nullengine.client.rendering.gl.GLDataType.FLOAT;

public final class GLBufferElements {

    public static final GLBufferElement POSITION = new GLBufferElement(FLOAT, GLBufferElement.Usage.POSITION, 3);
    public static final GLBufferElement COLOR_RGB = new GLBufferElement(FLOAT, GLBufferElement.Usage.COLOR, 3);
    public static final GLBufferElement COLOR_RGBA = new GLBufferElement(FLOAT, GLBufferElement.Usage.COLOR, 4);
    public static final GLBufferElement TEXTURE_UV = new GLBufferElement(FLOAT, GLBufferElement.Usage.TEXTURE_UV, 2);
    public static final GLBufferElement NORMAL = new GLBufferElement(FLOAT, GLBufferElement.Usage.NORMAL, 3);

    private GLBufferElements() {
    }
}
