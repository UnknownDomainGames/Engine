package unknowndomain.engine.client.rendering.util.buffer;

import org.lwjgl.opengl.GL20;

import java.util.Arrays;

public class GLBufferFormat {

    public static GLBufferFormat of(GLBufferElement... elements) {
        return new GLBufferFormat(elements);
    }

    private final GLBufferElement[] elements;
    private final int stride;

    public GLBufferFormat(GLBufferElement[] elements) {
        this.elements = elements;
        int stride = 0;
        for (GLBufferElement element : elements) {
            stride += element.getBytes();
        }
        this.stride = stride;
    }

    public GLBufferElement[] getElements() {
        return elements;
    }

    public int getStride() {
        return stride;
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
