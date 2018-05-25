package com.github.unknownstudio.unknowndomain.engineapi.client.display;

import org.joml.Matrix4f;

public interface Camera {

    /**
     * Move the camera
     * @param x x-translate
     * @param y y-translate
     * @param z z-translate
     */
    void move(double x,double y, double z);
    /**
     * Move the camera to the given position
     * @param x x-translate
     * @param y y-translate
     * @param z z-translate
     */
    void moveTo(double x,double y, double z);

    void rotate(double yaw, double pitch);

    void rotateTo(double yaw, double pitch);

    /**
     * Rotate the camera
     * @param angle angle
     * @param fx rotate factor by x-axis
     * @param fy rotate factor by y-axis
     * @param fz rotate factor by z-axis
     */
    void rotate(double angle, double fx, double fy, double fz);

    /**
     * Rotate the camera to the given position
     * @param angle angle
     * @param fx rotate factor by x-axis
     * @param fy rotate factor by y-axis
     * @param fz rotate factor by z-axis
     */
    void rotateTo(double angle, double fx, double fy, double fz);

    /**
     * zoom in or out.
     * ratio should be positive number. 1.0 is defined as unchanged. smaller than 1.0 is zoom out, larger than 1.0 is zoom in.
     *
     * @param ratio zoom factor
     */
    void zoom(double ratio);

    /**
     * zoom in or out to the given ratio.
     * ratio should be positive number. 1.0 is defined as unchanged. smaller than 1.0 is zoom out, larger than 1.0 is zoom in.
     *
     * @param ratio zoom ratio
     */
    void zoomTo(double ratio);

    /**
     * create view matrix for shader to use
     * @return
     */
    Matrix4f makeViewMatrix();
    /**
     * create projection matrix for shader to use
     * @return
     * @param width
     * @param height
     */
    Matrix4f makeProjectionMatrix(float width, float height);
}
