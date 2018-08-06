package unknowndomain.engine.client.block.model;

import org.joml.Matrix4d;

/**
 * @author byxiaobai
 * 来自 教程
 */


public class Transformation {
    private final Matrix4d projectionMatrix;
    private final Matrix4d modelViewMatrix;
    private final Matrix4d viewMatrix;

    public Transformation() {
        projectionMatrix = new Matrix4d();
        modelViewMatrix = new Matrix4d();
        viewMatrix = new Matrix4d();
    }

    public final Matrix4d getProjectionMatrix(float fov, float width, float height, float zNear, float zFar) {
        float aspectRatio = width / height;
        projectionMatrix.identity();
        projectionMatrix.perspective(fov, aspectRatio, zNear, zFar);
        return projectionMatrix;
    }

//    public Matrix4d getModelMatrix(GameItem gameItem) {
//        Vector3d rotation = gameItem.getRotation();
//        Matrix4d model = new Matrix4d();
//        model.identity().translate(gameItem.getPosition()).
//                rotateX((float) Math.toRadians(-rotation.x)).
//                rotateY((float) Math.toRadians(-rotation.y)).
//                rotateZ((float) Math.toRadians(-rotation.z)).
//                scale(gameItem.getScale());
//        return model;
//    }

//    public Matrix4d getModelViewMatrix(GameItem gameItem, Matrix4d viewMatrix) {
//        getModelMatrix(gameItem).get(modelViewMatrix);
//        Matrix4d viewCurr = new Matrix4d(viewMatrix);
//        return viewCurr.mul(modelViewMatrix);
//    }
}
