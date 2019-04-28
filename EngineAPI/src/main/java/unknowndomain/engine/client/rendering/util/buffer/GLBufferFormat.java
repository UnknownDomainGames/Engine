package unknowndomain.engine.client.rendering.util.buffer;

import org.lwjgl.opengl.GL20;

import java.util.Arrays;
import java.util.stream.Stream;

public class GLBufferFormat {

    public static GLBufferFormat of(GLBufferElement... elements) {
        return new GLBufferFormat(elements);
    }

    private final GLBufferElement[] elements;
    private final int stride;

    private final int positionElement;
    private final int colorElement;
    private final int uvElement;
    private final int normalElement;

    public GLBufferFormat(GLBufferElement[] elements) {
        this.elements = elements;
        int stride = 0;
        int positionElement = -1;
        int colorElement = -1;
        int uvElement = -1;
        int normalElement = -1;
        for (int i = 0; i < elements.length; i++) {
            GLBufferElement element = elements[i];
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

    public GLBufferElement[] getElements() {
        return elements;
    }

    public Stream<GLBufferElement> getElementsQueriable(){
        return Stream.of(elements);
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

    public void bind() {
        int offset = 0;
        for (int i = 0; i < elements.length; i++) {
            GLBufferElement element = elements[i];
            GL20.glVertexAttribPointer(i, element.getSize(), element.getType().glId, false, stride, offset);
            GL20.glEnableVertexAttribArray(i);
            offset += element.getBytes();
        }
    }

    public void unbind() {
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
}
