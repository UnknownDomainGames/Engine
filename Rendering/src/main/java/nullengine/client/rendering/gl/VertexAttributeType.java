package nullengine.client.rendering.gl;

import nullengine.client.rendering.gl.vertex.GLVertexElement;
import org.joml.Vector2fc;
import org.joml.Vector3fc;
import org.joml.Vector4fc;

import static org.lwjgl.opengl.GL20.*;

enum VertexAttributeType {
    BUFFER(VertexBufferObject.class) {
        @Override
        public void apply(int index, GLVertexElement element, Object value) {
            ((VertexBufferObject) value).bind();
            glEnableVertexAttribArray(index);
            element.apply(index);
        }

        @Override
        public void applyDefault(int index, GLVertexElement element) {
            glDisableVertexAttribArray(index);
        }
    },
    VEC2F(Vector2fc.class) {
        @Override
        public void apply(int index, GLVertexElement element, Object value) {
            Vector2fc vector2fc = (Vector2fc) value;
            glVertexAttrib2f(index, vector2fc.x(), vector2fc.y());
        }

        @Override
        public void applyDefault(int index, GLVertexElement element) {
            glVertexAttrib2f(index, 0, 0);
        }
    },
    VEC3F(Vector3fc.class) {
        @Override
        public void apply(int index, GLVertexElement element, Object value) {
            Vector3fc vector3fc = (Vector3fc) value;
            glVertexAttrib3f(index, vector3fc.x(), vector3fc.y(), vector3fc.z());
        }

        @Override
        public void applyDefault(int index, GLVertexElement element) {
            glVertexAttrib3f(index, 0, 0, 0);
        }
    },
    VEC4F(Vector4fc.class) {
        @Override
        public void apply(int index, GLVertexElement element, Object value) {
            Vector4fc vector4fc = (Vector4fc) value;
            glVertexAttrib4f(index, vector4fc.x(), vector4fc.y(), vector4fc.z(), vector4fc.w());
        }

        @Override
        public void applyDefault(int index, GLVertexElement element) {
            glVertexAttrib4f(index, 0, 0, 0, 1);
        }
    };

    private final Class<?> superType;

    VertexAttributeType(Class<?> superType) {
        this.superType = superType;
    }

    public abstract void apply(int index, GLVertexElement element, Object value);

    public abstract void applyDefault(int index, GLVertexElement element);

    public boolean is(Class<?> clazz) {
        return superType.isAssignableFrom(clazz);
    }
}
