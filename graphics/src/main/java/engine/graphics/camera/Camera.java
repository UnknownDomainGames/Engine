package engine.graphics.camera;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class Camera {

    public static final Vector3fc UP_VECTOR = new Vector3f(0, 1, 0);

    private final Vector3f position = new Vector3f();
    private final Vector3f lookAt = new Vector3f();
    private final Vector3f front = new Vector3f();
    private final Matrix4f viewMatrix = new Matrix4f();

    private ChangeListener changeListener;

    public Camera() {
        this(new Vector3f(0, 0, 0), new Vector3f(0, 0, -1));
    }

    public Camera(Vector3fc position, Vector3fc front) {
        look(position, front);
    }

    public void look(Vector3fc position, Vector3fc front) {
        this.position.set(position);
        this.front.set(front);
        this.lookAt.set(position).add(front);
        this.viewMatrix.setLookAt(this.position, this.lookAt, UP_VECTOR);
        if (changeListener != null) changeListener.onChanged(this);
    }

    public void lookAt(Vector3fc position, Vector3fc lookAt) {
        this.position.set(position);
        this.front.set(lookAt).sub(position);
        this.lookAt.set(lookAt);
        this.viewMatrix.setLookAt(this.position, this.lookAt, UP_VECTOR);
        if (changeListener != null) changeListener.onChanged(this);
    }

    public void move(Vector3fc offset) {
        look(this.position.add(offset), this.front);
    }

    public void moveTo(Vector3fc position) {
        look(position, this.front);
    }

    public Vector3fc getPosition() {
        return position;
    }

    public Vector3fc getLookAt() {
        return lookAt;
    }

    public Vector3fc getFront() {
        return front;
    }

    public Matrix4fc getViewMatrix() {
        return viewMatrix;
    }

    public ChangeListener getChangeListener() {
        return changeListener;
    }

    public void setChangeListener(ChangeListener listener) {
        this.changeListener = listener;
    }

    @FunctionalInterface
    public interface ChangeListener {
        void onChanged(Camera camera);
    }
}
