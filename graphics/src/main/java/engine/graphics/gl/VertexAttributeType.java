package engine.graphics.gl;

import engine.graphics.gl.buffer.GLVertexBuffer;
import engine.graphics.gl.util.GLHelper;
import engine.graphics.vertex.VertexFormat;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.joml.Vector4fc;

import static org.lwjgl.opengl.GL20.*;

enum VertexAttributeType {
    BUFFER(GLVertexBuffer.class, false) {
        @Override
        public void apply(int index, VertexFormat format, Object value) {
            ((GLVertexBuffer) value).bind();
            GLHelper.enableVertexFormat(format, index);
        }

        @Override
        public void applyDefault(int index, VertexFormat format) {
            GLHelper.disableVertexFormat(format, index);
        }
    },
    FLOAT(Float.class, true) {
        @Override
        public void apply(int index, VertexFormat format, Object value) {
            glVertexAttrib1f(index, (Float) value);
        }

        @Override
        public void applyDefault(int index, VertexFormat format) {
            glVertexAttrib1f(index, 0);
        }
    },
    VEC2F(Vector2fc.class, true) {
        @Override
        public void apply(int index, VertexFormat format, Object value) {
            Vector2fc vector2fc = (Vector2fc) value;
            glVertexAttrib2f(index, vector2fc.x(), vector2fc.y());
        }

        @Override
        public void applyDefault(int index, VertexFormat format) {
            glVertexAttrib2f(index, 0, 0);
        }
    },
    VEC3F(Vector3fc.class, true) {
        @Override
        public void apply(int index, VertexFormat format, Object value) {
            Vector3fc vector3fc = (Vector3fc) value;
            glVertexAttrib3f(index, vector3fc.x(), vector3fc.y(), vector3fc.z());
        }

        @Override
        public void applyDefault(int index, VertexFormat format) {
            glVertexAttrib3f(index, 0, 0, 0);
        }
    },
    VEC4F(Vector4fc.class, true) {
        @Override
        public void apply(int index, VertexFormat format, Object value) {
            Vector4fc vector4fc = (Vector4fc) value;
            glVertexAttrib4f(index, vector4fc.x(), vector4fc.y(), vector4fc.z(), vector4fc.w());
        }

        @Override
        public void applyDefault(int index, VertexFormat format) {
            glVertexAttrib4f(index, 0, 0, 0, 1);
        }
    };

    private final Class<?> superType;
    private final boolean applyBeforeRendering;

    VertexAttributeType(Class<?> superType, boolean applyBeforeRendering) {
        this.superType = superType;
        this.applyBeforeRendering = applyBeforeRendering;
    }

    public abstract void apply(int index, VertexFormat format, Object value);

    public abstract void applyDefault(int index, VertexFormat format);

    public boolean is(Class<?> clazz) {
        return superType.isAssignableFrom(clazz);
    }

    public boolean isApplyBeforeRendering() {
        return applyBeforeRendering;
    }
}
