package unknowndomain.engine.client.rendering.util.buffer;

import org.lwjgl.opengl.GL20;

import java.util.Arrays;

public class GLBufferFormat {

    public static GLBufferFormat of(GLBufferElement... elements) {
        return new GLBufferFormat(elements);
    }

    private final GLBufferElement[] elements;
    private final int stride;

    private final boolean usingPosition;
    private final boolean usingColor;
    private final boolean usingTextureUV;
    private final boolean usingNormal;

    public GLBufferFormat(GLBufferElement[] elements) {
        this.elements = elements;
        int stride = 0;
        boolean usingPosition = false;
        boolean usingColor = false;
        boolean usingTextureUV = false;
        boolean usingNormal = false;
        for (GLBufferElement element : elements) {
            stride += element.getBytes();
            switch (element.getUsage()) {
                case POSITION:
                    usingPosition = true;
                    break;
                case COLOR:
                    usingColor = true;
                    break;
                case TEXTURE_UV:
                    usingTextureUV = true;
                    break;
                case NORMAL:
                    usingNormal = true;
                    break;
            }
        }
        this.stride = stride;
        this.usingPosition = usingPosition;
        this.usingColor = usingColor;
        this.usingTextureUV = usingTextureUV;
        this.usingNormal = usingNormal;
    }

    public GLBufferElement[] getElements() {
        return elements;
    }

    public int getStride() {
        return stride;
    }

    public boolean isUsingPosition() {
        return usingPosition;
    }

    public boolean isUsingColor() {
        return usingColor;
    }

    public boolean isUsingTextureUV() {
        return usingTextureUV;
    }

    public boolean isUsingNormal() {
        return usingNormal;
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
