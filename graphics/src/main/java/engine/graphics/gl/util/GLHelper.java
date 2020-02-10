package engine.graphics.gl.util;

import engine.graphics.util.DataType;
import engine.graphics.vertex.VertexElement;
import engine.graphics.vertex.VertexFormat;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.GL_HALF_FLOAT;
import static org.lwjgl.opengl.GL30.GL_INVALID_FRAMEBUFFER_OPERATION;

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
        glVertexAttribPointer(index, element.getComponentCount(), toGLDataType(element.getType()), element.isNormalized(), stride, offset);
    }

    public static void enableVertexFormat(VertexFormat format) {
        enableVertexFormat(format, 0);
    }

    public static void enableVertexFormat(VertexFormat format, int firstIndex) {
        VertexElement[] elements = format.getElements();
        int stride = format.getBytes();
        int offset = 0;
        for (int index = 0; index < elements.length; index++) {
            VertexElement element = elements[index];
            enableVertexElement(element, firstIndex + index, stride, offset);
            offset += element.getBytes();
        }
    }

    public static void disableVertexFormat(VertexFormat format) {
        disableVertexFormat(format, 0);
    }

    public static void disableVertexFormat(VertexFormat format, int firstIndex) {
        for (int i = firstIndex, size = firstIndex + format.getElementCount(); i < size; i++) {
            glDisableVertexAttribArray(i);
        }
    }

    public static String getFriendlyErrorCode(int errorCode) {
        switch (errorCode) {
            case GL_NO_ERROR:
                return "NO_ERROR";
            case GL_INVALID_ENUM:
                return "INVALID_ENUM";
            case GL_INVALID_VALUE:
                return "INVALID_VALUE";
            case GL_INVALID_OPERATION:
                return "INVALID_OPERATION";
            case GL_STACK_OVERFLOW:
                return "STACK_OVERFLOW";
            case GL_STACK_UNDERFLOW:
                return "STACK_UNDERFLOW";
            case GL_OUT_OF_MEMORY:
                return "OUT_OF_MEMORY";
            case GL_INVALID_FRAMEBUFFER_OPERATION:
                return "INVALID_FRAMEBUFFER_OPERATION";
            default:
                throw new IllegalArgumentException("Unknown error code");
        }
    }


    private GLHelper() {
    }
}
