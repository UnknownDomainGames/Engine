package com.github.unknownstudio.unknowndomain.engine.client.display;

import com.github.unknownstudio.unknowndomain.engineapi.client.display.Camera;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class CameraDefault implements Camera {

    private double x,y,z;
    private double yaw, pitch;
    private double aX,aY,aZ;
    private double zoomRate;

    public CameraDefault(){
        zoomRate = 1;
    }

    @Override
    public void move(double x, double y, double z) {
        this.x += x;
        this.y += y;
        this.z += z;
    }

    @Override
    public void moveTo(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    private double lastX, lastY;

    private static final float SENSIBILITY = 0.5f;

    @Override
    public void rotate(double x, double y) {
        double yaw = (x - lastX) * SENSIBILITY;
        double pitch = (lastY - y) * SENSIBILITY;
        lastX = x;
        lastY = y;
        this.pitch += pitch;
        this.pitch = Math.min(89.0, Math.max(-89.0, this.pitch));
        this.yaw += yaw;
    }

    @Override
    public void rotateTo(double yaw, double pitch) {
        this.pitch = pitch;
        this.pitch = Math.min(89.0, Math.max(-89.0, this.pitch));
        this.yaw = yaw;
    }

    @Override
    public void rotate(double angle, double fx, double fy, double fz) {
        aX += angle * fx;
        aY += angle * fy;
        aZ += angle * fz;
        aX %= 360.0;
        aY %= 360.0;
        aZ %= 360.0;
    }

    @Override
    public void rotateTo(double angle, double fx, double fy, double fz) {
        aX = angle * fx;
        aY = angle * fy;
        aZ = angle * fz;
        aX %= 360.0;
        aY %= 360.0;
        aZ %= 360.0;
    }

    @Override
    public void zoom(double ratio) {
        zoomRate *= ratio;
    }

    @Override
    public void zoomTo(double ratio) {
        zoomRate = ratio;
    }

    @Override
    public Matrix4f makeViewMatrix() {
        Vector3f pos = new Vector3f((float)x,(float)y,(float)z);
        Vector3f front = new Vector3f((float)(Math.cos(Math.toRadians(pitch)) * Math.cos(Math.toRadians(yaw))), (float)Math.sin(Math.toRadians(pitch)), (float)(Math.cos(Math.toRadians(pitch)) * Math.sin(Math.toRadians(yaw)))).normalize();
        Vector3f up = new Vector3f(0,1,0);
        return new Matrix4f().lookAt(pos,pos.add(front), up);
    }

    @Override
    public Matrix4f makeProjectionMatrix(float width, float height) {
        return new Matrix4f().perspective((float)(Math.toRadians(Math.max(1.0, Math.min(90.0, 60.0f * zoomRate)))), width / height, 0.01f, 1000f);
    }
}
