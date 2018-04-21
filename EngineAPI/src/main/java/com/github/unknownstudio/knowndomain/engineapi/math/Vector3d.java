package com.github.unknownstudio.knowndomain.engineapi.math;

public class Vector3d {
    private double x,y,z;

    public Vector3d(double x, double y, double z){
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public Vector3d add(Vector3d others){
        return this; //TODO: addition of vector
    }

    public Vector3d minus(Vector3d others){
        return this; //TODO: subtraction of vector
    }

    public Vector3d dotProduct(Vector3d others){
        return this; //TODO: dot product of vector
    }

    public Vector3d crossProduct(Vector3d others){
        return this; //TODO: cross product of vector
    }
}
