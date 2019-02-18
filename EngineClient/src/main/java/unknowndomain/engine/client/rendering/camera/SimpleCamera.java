package unknowndomain.engine.client.rendering.camera;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class SimpleCamera implements Camera {

    private final Vector3fc position;
    private final Vector3fc lookAt;
    private final Vector3fc frontVector;
    private final Matrix4fc viewMatrix;

    public SimpleCamera(Vector3fc position, Vector3fc frontVector) {
        this.position = position;
        this.frontVector = frontVector;
        this.lookAt = position.add(frontVector, new Vector3f());
        this.viewMatrix = new Matrix4f().lookAt(this.position, this.lookAt, UP_VECTOR);
    }

    @Override
    public void update(float partial) {

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
    public Vector3fc getFrontVector() {
        return frontVector;
    }

    @Override
    public Matrix4fc getViewMatrix() {
        return viewMatrix;
    }
}
