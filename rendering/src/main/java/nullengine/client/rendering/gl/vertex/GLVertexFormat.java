package nullengine.client.rendering.gl.vertex;

import org.lwjgl.opengl.GL20;

import java.util.Arrays;

public class GLVertexFormat {

    public static GLVertexFormat of(GLVertexElement... elements) {
        return new GLVertexFormat(elements);
    }

    private final GLVertexElement[] elements;
    private final int stride;

    private final int positionElement;
    private final int colorElement;
    private final int uvElement;
    private final int normalElement;

    public GLVertexFormat(GLVertexElement... elements) {
        this.elements = elements;
        int stride = 0;
        int positionElement = -1;
        int colorElement = -1;
        int uvElement = -1;
        int normalElement = -1;
        for (int i = 0; i < elements.length; i++) {
            GLVertexElement element = elements[i];
            stride += element.getBytes();
            switch (element.getUsage()) {
                case POSITION:
                    positionElement = i;
                    break;
                case COLOR:
                    colorElement = i;
                    break;
                case TEXTURE_UV:
                    uvElement = i;
                    break;
                case NORMAL:
                    normalElement = i;
                    break;
            }
        }
        this.stride = stride;
        this.positionElement = positionElement;
        this.colorElement = colorElement;
        this.uvElement = uvElement;
        this.normalElement = normalElement;
    }

    public GLVertexElement[] getElements() {
        return elements;
    }

    public int getStride() {
        return stride;
    }

    public boolean isUsingPosition() {
        return positionElement != -1;
    }

    public boolean isUsingColor() {
        return colorElement != -1;
    }

    public boolean isUsingTextureUV() {
        return uvElement != -1;
    }

    public boolean isUsingNormal() {
        return normalElement != -1;
    }

    public GLVertexElement getColorElement() {
        return elements[colorElement];
    }

    public void apply() {
        int offset = 0;
        for (int i = 0; i < elements.length; i++) {
            GLVertexElement element = elements[i];
            element.apply(i, stride, offset);
            offset += element.getBytes();
        }
    }

    public void enable() {
        for (int i = 0; i < elements.length; i++) {
            GL20.glEnableVertexAttribArray(i);
        }
    }

    public void enableAndApply() {
        int offset = 0;
        for (int i = 0; i < elements.length; i++) {
            GLVertexElement element = elements[i];
            GL20.glEnableVertexAttribArray(i);
            element.apply(i, stride, offset);
            offset += element.getBytes();
        }
    }

    public void disable() {
        for (int i = 0; i < elements.length; i++) {
            GL20.glDisableVertexAttribArray(i);
        }
    }

    @Override
    public String toString() {
        return "GLBufferFormat{" +
                "elements=" + Arrays.toString(elements) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GLVertexFormat that = (GLVertexFormat) o;
        return Arrays.equals(elements, that.elements);
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(elements);
    }
}
