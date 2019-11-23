package nullengine.client.rendering.camera;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public class FixedCamera implements Camera {

    private Vector3fc position;
    private Vector3fc lookAt;
    private Vector3fc frontVector;
    private Matrix4fc viewMatrix;

    public FixedCamera() {
    }

    public FixedCamera(Vector3fc position, Vector3fc frontVector) {
        look(position, frontVector);
    }

    public void look(Vector3fc position, Vector3fc frontVector) {
        this.position = position;
        this.frontVector = frontVector;
        this.lookAt = position.add(frontVector, new Vector3f());
        this.viewMatrix = new Matrix4f().lookAt(this.position, this.lookAt, UP_VECTOR);
    }

    public void lookAt(Vector3fc position, Vector3fc lookAt) {
        this.position = position;
        this.frontVector = lookAt.sub(position, new Vector3f());
        this.lookAt = lookAt;
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
