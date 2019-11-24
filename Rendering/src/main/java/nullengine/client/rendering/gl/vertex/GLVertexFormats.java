package nullengine.client.rendering.gl.vertex;

public class GLVertexFormats {
    public static final GLVertexFormat POSITION = GLVertexFormat.of(GLVertexElements.POSITION);
    public static final GLVertexFormat POSITION_COLOR = GLVertexFormat.of(GLVertexElements.POSITION, GLVertexElements.COLOR_RGB);
    public static final GLVertexFormat POSITION_COLOR_ALPHA = GLVertexFormat.of(GLVertexElements.POSITION, GLVertexElements.COLOR_RGBA);
    public static final GLVertexFormat POSITION_TEXTURE = GLVertexFormat.of(GLVertexElements.POSITION, GLVertexElements.TEXTURE_UV);
    public static final GLVertexFormat POSITION_TEXTURE_NORMAL = GLVertexFormat.of(GLVertexElements.POSITION, GLVertexElements.TEXTURE_UV, GLVertexElements.NORMAL);
    public static final GLVertexFormat POSITION_COLOR_TEXTURE = GLVertexFormat.of(GLVertexElements.POSITION, GLVertexElements.COLOR_RGB, GLVertexElements.TEXTURE_UV);
    public static final GLVertexFormat POSITION_COLOR_ALPHA_TEXTURE = GLVertexFormat.of(GLVertexElements.POSITION, GLVertexElements.COLOR_RGBA, GLVertexElements.TEXTURE_UV);
    public static final GLVertexFormat POSITION_COLOR_TEXTURE_NORMAL = GLVertexFormat.of(GLVertexElements.POSITION, GLVertexElements.COLOR_RGB, GLVertexElements.TEXTURE_UV, GLVertexElements.NORMAL);
    public static final GLVertexFormat POSITION_COLOR_ALPHA_TEXTURE_NORMAL = GLVertexFormat.of(GLVertexElements.POSITION, GLVertexElements.COLOR_RGBA, GLVertexElements.TEXTURE_UV, GLVertexElements.NORMAL);

}
