package nullengine.client.rendering.gl.vertex;

import static nullengine.client.rendering.gl.GLDataType.FLOAT;

public final class GLVertexElements {

    public static final GLVertexElement POSITION = new GLVertexElement(FLOAT, GLVertexElement.Usage.POSITION, 3);
    public static final GLVertexElement COLOR_RGB = new GLVertexElement(FLOAT, GLVertexElement.Usage.COLOR, 3);
    public static final GLVertexElement COLOR_RGBA = new GLVertexElement(FLOAT, GLVertexElement.Usage.COLOR, 4);
    public static final GLVertexElement TEXTURE_UV = new GLVertexElement(FLOAT, GLVertexElement.Usage.TEXTURE_UV, 2);
    public static final GLVertexElement NORMAL = new GLVertexElement(FLOAT, GLVertexElement.Usage.NORMAL, 3);

    private GLVertexElements() {
    }
}
