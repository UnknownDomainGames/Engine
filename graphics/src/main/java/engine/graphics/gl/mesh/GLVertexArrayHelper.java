package engine.graphics.gl.mesh;

import engine.graphics.gl.buffer.GLVertexBuffer;
import engine.graphics.gl.util.GLHelper;
import engine.graphics.vertex.VertexElement;
import engine.graphics.vertex.VertexFormat;
import org.lwjgl.opengl.GL20C;
import org.lwjgl.opengl.GL30C;
import org.lwjgl.opengl.GL45C;

import static engine.graphics.gl.util.GLHelper.toGLDataType;

public final class GLVertexArrayHelper {

    public static void enableVertexFormat(int vertexArray, GLVertexBuffer buffer, VertexFormat format) {
        enableVertexFormat(vertexArray, buffer, format, 0);
    }

    public static void enableVertexFormat(int vertexArray, GLVertexBuffer buffer, VertexFormat format, int firstIndex) {
//        Fix failed to draw multi window.
//        if (GLHelper.isSupportARBDirectStateAccess()) {
//            VertexElement[] elements = format.getElements();
//            int stride = format.getBytes(), offset = 0;
//            for (int i = 0, index = firstIndex, size = elements.length; i < size; i++, index++) {
//                VertexElement element = elements[i];
//                GL45C.glEnableVertexArrayAttrib(vertexArray, index);
//                GL45C.glVertexArrayVertexBuffer(vertexArray, index, buffer.getId(), offset, stride);
//                GL45C.glVertexArrayAttribFormat(vertexArray, index, element.getComponentCount(),
//                        toGLDataType(element.getType()), element.isNormalized(), 0);
//                GL45C.glVertexArrayAttribBinding(vertexArray, index, index);
//                offset += element.getBytes();
//            }
//        } else {
        GL30C.glBindVertexArray(vertexArray);
        buffer.bind();
        VertexElement[] elements = format.getElements();
        int stride = format.getBytes(), offset = 0;
        for (int i = 0, index = firstIndex, size = elements.length; i < size; i++, index++) {
            VertexElement element = elements[i];
            GL20C.glEnableVertexAttribArray(index);
            GL20C.glVertexAttribPointer(index, element.getComponentCount(),
                    toGLDataType(element.getType()), element.isNormalized(), stride, offset);
            offset += element.getBytes();
            }
//        }
    }

    public static void disableVertexFormat(int vertexArray, VertexFormat format) {
        disableVertexFormat(vertexArray, 0, format);
    }

    public static void disableVertexFormat(int vertexArray, int first, VertexFormat format) {
        disableVertexFormat(vertexArray, first, format.getElementCount());
    }

    public static void disableVertexFormat(int vertexArray, int first, int count) {
        if (GLHelper.isSupportARBDirectStateAccess()) {
            for (int i = first, size = first + count; i < size; i++)
                GL45C.glDisableVertexArrayAttrib(vertexArray, i);
        } else {
            GL30C.glBindVertexArray(vertexArray);
            for (int i = first, size = first + count; i < size; i++)
                GL20C.glDisableVertexAttribArray(i);
        }
    }

    public static void bindElementBuffer(int vertexArray, GLVertexBuffer buffer) {
        if (GLHelper.isSupportARBDirectStateAccess()) {
            GL45C.glVertexArrayElementBuffer(vertexArray, buffer.getId());
        } else {
            GL30C.glBindVertexArray(vertexArray);
            buffer.bind();
        }
    }

    private GLVertexArrayHelper() {
    }
}
