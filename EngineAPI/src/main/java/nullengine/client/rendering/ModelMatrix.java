package nullengine.client.rendering;

import org.joml.*;

import javax.annotation.concurrent.NotThreadSafe;
import java.util.Stack;

@NotThreadSafe
public class ModelMatrix {

    private static final Stack<Matrix4f> matrixStack = new Stack<>();

    private static Matrix4f currentMatrix;

    public static void pop() {
        currentMatrix = matrixStack.pop();
    }

    public static void push() {
        currentMatrix = new Matrix4f(matrixStack.push(currentMatrix));
    }

    public static Matrix4fc currentMatrix() {
        return currentMatrix;
    }

    public static void zero() {
        currentMatrix.zero();
    }

    public static void identity() {
        currentMatrix.identity();
    }

    public static void normal() {
        currentMatrix.normal();
    }

    public static void rotateX(float ang) {
        currentMatrix.rotateX(ang);
    }

    public static void rotateY(float ang) {
        currentMatrix.rotateY(ang);
    }

    public static void rotateZ(float ang) {
        currentMatrix.rotateZ(ang);
    }

    public static void rotateTowardsXY(float dirX, float dirY) {
        currentMatrix.rotateTowardsXY(dirX, dirY);
    }

    public static void rotateXYZ(Vector3f angles) {
        currentMatrix.rotateXYZ(angles);
    }

    public static void rotateXYZ(float angleX, float angleY, float angleZ) {
        currentMatrix.rotateXYZ(angleX, angleY, angleZ);
    }

    public static void rotateAffineXYZ(float angleX, float angleY, float angleZ) {
        currentMatrix.rotateAffineXYZ(angleX, angleY, angleZ);
    }

    public static void rotateZYX(Vector3f angles) {
        currentMatrix.rotateZYX(angles);
    }

    public static void rotateZYX(float angleZ, float angleY, float angleX) {
        currentMatrix.rotateZYX(angleZ, angleY, angleX);
    }

    public static void rotateAffineZYX(float angleZ, float angleY, float angleX) {
        currentMatrix.rotateAffineZYX(angleZ, angleY, angleX);
    }

    public static void rotateYXZ(Vector3f angles) {
        currentMatrix.rotateYXZ(angles);
    }

    public static void rotateYXZ(float angleY, float angleX, float angleZ) {
        currentMatrix.rotateYXZ(angleY, angleX, angleZ);
    }

    public static void rotateAffineYXZ(float angleY, float angleX, float angleZ) {
        currentMatrix.rotateAffineYXZ(angleY, angleX, angleZ);
    }

    public static void rotate(float ang, float x, float y, float z) {
        currentMatrix.rotate(ang, x, y, z);
    }

    public static void rotateAffine(float ang, float x, float y, float z) {
        currentMatrix.rotateAffine(ang, x, y, z);
    }

    public static void rotateLocal(float ang, float x, float y, float z) {
        currentMatrix.rotateLocal(ang, x, y, z);
    }

    public static void rotateLocalX(float ang) {
        currentMatrix.rotateLocalX(ang);
    }

    public static void rotateLocalY(float ang) {
        currentMatrix.rotateLocalY(ang);
    }

    public static void rotateLocalZ(float ang) {
        currentMatrix.rotateLocalZ(ang);
    }

    public static void translate(Vector3fc offset) {
        currentMatrix.translate(offset);
    }

    public static void translate(float x, float y, float z) {
        currentMatrix.translate(x, y, z);
    }

    public static void translateLocal(Vector3fc offset) {
        currentMatrix.translateLocal(offset);
    }

    public static void translateLocal(float x, float y, float z) {
        currentMatrix.translateLocal(x, y, z);
    }

    public static void rotate(Quaternionfc quat) {
        currentMatrix.rotate(quat);
    }

    public static void rotateAffine(Quaternionfc quat) {
        currentMatrix.rotateAffine(quat);
    }

    public static void rotateAround(Quaternionfc quat, float ox, float oy, float oz) {
        currentMatrix.rotateAround(quat, ox, oy, oz);
    }

    public static void rotationAround(Quaternionfc quat, float ox, float oy, float oz) {
        currentMatrix.rotationAround(quat, ox, oy, oz);
    }

    public static void rotateLocal(Quaternionfc quat) {
        currentMatrix.rotateLocal(quat);
    }

    public static void rotateAroundLocal(Quaternionfc quat, float ox, float oy, float oz) {
        currentMatrix.rotateAroundLocal(quat, ox, oy, oz);
    }

    public static void rotate(AxisAngle4f axisAngle) {
        currentMatrix.rotate(axisAngle);
    }

    public static void rotate(float angle, Vector3fc axis) {
        currentMatrix.rotate(angle, axis);
    }

    public static void scale(Vector3fc xyz) {
        currentMatrix.scale(xyz);
    }

    public static void scale(float xyz) {
        currentMatrix.scale(xyz);
    }

    public static void scale(float x, float y, float z) {
        currentMatrix.scale(x, y, z);
    }

    public static void scaleAround(float sx, float sy, float sz, float ox, float oy, float oz) {
        currentMatrix.scaleAround(sx, sy, sz, ox, oy, oz);
    }

    public static void scaleAround(float factor, float ox, float oy, float oz) {
        currentMatrix.scaleAround(factor, ox, oy, oz);
    }

    public static void scaleLocal(float xyz) {
        currentMatrix.scaleLocal(xyz);
    }

    public static void scaleLocal(float x, float y, float z) {
        currentMatrix.scaleLocal(x, y, z);
    }

    public static void scaleAroundLocal(float sx, float sy, float sz, float ox, float oy, float oz) {
        currentMatrix.scaleAroundLocal(sx, sy, sz, ox, oy, oz);
    }

    public static void scaleAroundLocal(float factor, float ox, float oy, float oz) {
        currentMatrix.scaleAroundLocal(factor, ox, oy, oz);
    }
}
