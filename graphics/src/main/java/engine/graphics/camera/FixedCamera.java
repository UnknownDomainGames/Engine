package engine.graphics.camera;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class FixedCamera implements OldCamera {

    private Vector3fc position;
    private Vector3fc lookAt;
    private Vector3fc frontVector;
    private final Matrix4f viewMatrix = new Matrix4f();

    private ChangeListener changeListener;

    public FixedCamera() {
        this(new Vector3f(0, 0, 0), new Vector3f(0, 0, -1));
    }

    public FixedCamera(Vector3fc position, Vector3fc frontVector) {
        look(position, frontVector);
    }

    public void look(Vector3fc position, Vector3fc frontVector) {
        this.position = position;
        this.frontVector = frontVector;
        this.lookAt = position.add(frontVector, new Vector3f());
        this.viewMatrix.setLookAt(this.position, this.lookAt, UP_VECTOR);
        if (changeListener != null) changeListener.onChanged(this);
    }

    public void lookAt(Vector3fc position, Vector3fc lookAt) {
        this.position = position;
        this.frontVector = lookAt.sub(position, new Vector3f());
        this.lookAt = lookAt;
        this.viewMatrix.setLookAt(this.position, this.lookAt, UP_VECTOR);
        if (changeListener != null) changeListener.onChanged(this);
    }

    @Override
    public Vector3fc getPosition() {
        return position;
    }

    @Override
    public Vector3fc getLookAt() {
        return lookAt;
    }

    @Override
    public Vector3fc getFront() {
        return frontVector;
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
}
