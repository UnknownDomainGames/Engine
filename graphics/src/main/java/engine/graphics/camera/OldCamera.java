package engine.graphics.camera;

import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

@Deprecated
public interface OldCamera {

    Vector3fc UP_VECTOR = new Vector3f(0, 1, 0);

    @Deprecated
    default void update(float partial) {
    }

    Vector3fc getPosition();

    Vector3fc getLookAt();

    Vector3fc getFront();

    Matrix4fc getViewMatrix();

    ChangeListener getChangeListener();

    void setChangeListener(ChangeListener listener);

    @FunctionalInterface
    interface ChangeListener {
        void onChanged(OldCamera camera);
    }
}
