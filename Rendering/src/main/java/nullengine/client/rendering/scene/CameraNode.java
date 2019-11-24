package nullengine.client.rendering.scene;

import nullengine.client.rendering.camera.Camera;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class CameraNode extends Node implements Camera {

    private static final Vector3fc UP_VECTOR = new Vector3f(0, 1, 0);
    private static final Vector3fc FRONT_VECTOR = new Vector3f(0, 0, -1);

    private final Vector3f lookAt = new Vector3f();
    private final Vector3f front = new Vector3f();
    private final Matrix4f viewMatrix = new Matrix4f();

    private ChangeListener changeListener;

    @Override
    public Vector3fc getPosition() {
        return getWorldTranslation();
    }

    @Override
    public Vector3fc getLookAt() {
        return lookAt;
    }

    @Override
    public Vector3fc getFront() {
        return front;
    }

    @Override
    public Matrix4fc getViewMatrix() {
        return viewMatrix;
    }

    @Override
    public ChangeListener getChangeListener() {
        return changeListener;
    }

    @Override
    public void setChangeListener(ChangeListener listener) {
        this.changeListener = listener;
    }

    @Override
    protected void refreshTransform() {
        super.refreshTransform();
        Vector3fc translation = getWorldTranslation();
        getWorldTransform().transform(lookAt.set(FRONT_VECTOR), lookAt);
        front.set(lookAt).mul(translation);
        viewMatrix.setLookAt(translation, lookAt, UP_VECTOR);
        if (changeListener != null) changeListener.onChanged(this);
    }
}
