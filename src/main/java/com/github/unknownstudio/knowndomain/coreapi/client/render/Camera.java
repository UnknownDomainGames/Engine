package com.github.unknownstudio.knowndomain.coreapi.client.render;

public interface Camera {

    /**
     * Move the camera
     * @param x x-translate
     * @param y y-translate
     * @param z z-translate
     */
    void move(double x,double y, double z);

    /**
     * Rotate the camera
     * @param angle angle
     * @param fx rotate factor by x-axis
     * @param fy rotate factor by y-axis
     * @param fz rotate factor by z-axis
     */
    void rotate(double angle, double fx, double fy, double fz);

    /**
     * zoom in or out.
     * ratio should be positive number. 1.0 is defined as unchanged. smaller than 1.0 is zoom out, larger than 1.0 is zoom in.
     *
     * @param ratio zoom ratio
     */
    void zoom(double ratio);
}
