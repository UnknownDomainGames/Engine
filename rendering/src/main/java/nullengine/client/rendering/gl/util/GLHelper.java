package nullengine.client.rendering.gl.util;

import nullengine.client.rendering.util.DataType;
import nullengine.client.rendering.vertex.VertexElement;
import nullengine.client.rendering.vertex.VertexFormat;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.GL_HALF_FLOAT;

public final class GLHelper {

    public static int toGLDataType(DataType type) {
        switch (type) {
            case BYTE:
                return GL_BYTE;
            case UNSIGNED_BYTE:
                return GL_UNSIGNED_BYTE;
            case SHORT:
                return GL_SHORT;
            case UNSIGNED_SHORT:
                return GL_UNSIGNED_SHORT;
            case INT:
                return GL_INT;
            case UNSIGNED_INT:
                return GL_UNSIGNED_INT;
            case FLOAT:
                return GL_FLOAT;
            case DOUBLE:
                return GL_DOUBLE;
            case HALF_FLOAT:
                return GL_HALF_FLOAT;
            default:
                throw new IllegalArgumentException();
        }
    }

    public static void enableVertexElement(VertexElement element, int index) {
        enableVertexElement(element, index, 0, 0);
    }

    public static void enableVertexElement(VertexElement element, int index, int stride, int offset) {
        glEnableVertexAttribArray(index);
        glVertexAttribPointer(index, element.getSize(), toGLDataType(element.getType()), element.isNormalized(), stride, offset);
    }

    public static void enableVertexFormat(VertexFormat format) {
        VertexElement[] elements = format.getElements();
        int stride = format.getBytes();
        int offset = 0;
        for (int index = 0; index < elements.length; index++) {
            VertexElement element = elements[index];
            enableVertexElement(element, index, stride, offset);
            offset += element.getBytes();
        }
    }

    public static void disableVertexFormat(VertexFormat format) {
        for (int i = 0, size = format.getElements().length; i < size; i++) {
            glDisableVertexAttribArray(i);
        }
    }

    private GLHelper() {
    }
}
