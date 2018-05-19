package com.github.unknownstudio.unknowndomain.engine.client.util;

import javax.vecmath.Matrix4f;
import javax.vecmath.Vector3f;
import java.io.*;
import java.nio.FloatBuffer;

public class GLHelper {
    public static Matrix4f ortho(float left, float right,
                                 float bottom, float top,
                                 float zNear, float zFar) {

        Matrix4f m = new Matrix4f();

        m.m00 = 2 / (right - left);
        m.m11 = 2 / (top - bottom);
        m.m22 = - 2 / (zFar - zNear);
        m.m30 = - (right + left) / (right - left);
        m.m31 = - (top + bottom) / (top - bottom);
        m.m32 = - (zFar + zNear) / (zFar - zNear);

        return m;
    }

    public static Matrix4f frustum(float left, float right,
                                   float bottom, float top,
                                   float zNear, float zFar) {

        Matrix4f m = new Matrix4f();

        m.m00 = (2 * zNear) / (right - left);
        m.m11 = (2 * zNear) / (top - bottom);
        m.m20 = (right + left) / (right - left);
        m.m21 = (top + bottom) / (top - bottom);
        m.m22 = -(zFar + zNear) / (zFar - zNear);
        m.m23 = -1;
        m.m32 = -(2 * zFar * zNear) / (zFar - zNear);

        return m;
    }

    public static Matrix4f perspective(float fovy, float aspect, float zNear, float zFar) {

        float range = (float) Math.tan(Math.toRadians(fovy / 2)) * zNear;
        float left = -range * aspect;
        float right = range * aspect;
        float bottom = -range;
        float top = range;

        Matrix4f m = new Matrix4f();

        m.m00 = (2 * zNear) / (right - left);
        m.m11 = (2 * zNear) / (top - bottom);
        m.m22 = - (zFar + zNear) / (zFar - zNear);
        m.m23 = - 1;
        m.m32 = - (2 * zFar * zNear) / (zFar - zNear);

        return m;
    }

    public static Matrix4f lookAt(Vector3f eye, Vector3f center, Vector3f up) {

        Vector3f forward = new Vector3f(center);
        forward.sub(eye);
        forward.normalize(forward);

        up.normalize();

        Vector3f side = new Vector3f();
        side.cross(forward, up);
        side.normalize(side);

        up.cross(side, forward);

        Matrix4f m = new Matrix4f();

        m.m00 = side.x;
        m.m10 = side.y;
        m.m20 = side.z;

        m.m01 = up.x;
        m.m11 = up.y;
        m.m21 = up.z;

        m.m02 = -forward.x;
        m.m12 = -forward.y;
        m.m22 = -forward.z;

        m = translate(new Vector3f(-eye.x, -eye.y, -eye.z), m,m);

        return m;
    }

    public static Matrix4f translate(Vector3f vec, Matrix4f src, Matrix4f dest){
        if (dest == null)
            dest = new Matrix4f();

        dest.m30 += src.m00 * vec.x + src.m10 * vec.y + src.m20 * vec.z;
        dest.m31 += src.m01 * vec.x + src.m11 * vec.y + src.m21 * vec.z;
        dest.m32 += src.m02 * vec.x + src.m12 * vec.y + src.m22 * vec.z;
        dest.m33 += src.m03 * vec.x + src.m13 * vec.y + src.m23 * vec.z;

        return dest;
    }

    public static FloatBuffer toBuffer(Matrix4f m, FloatBuffer buf){
        buf.put(m.m00).put(m.m10).put(m.m20).put(m.m30);
        buf.put(m.m01).put(m.m11).put(m.m21).put(m.m31);
        buf.put(m.m02).put(m.m12).put(m.m22).put(m.m32);
        buf.put(m.m03).put(m.m13).put(m.m23).put(m.m33);
        return buf;
    }

    public static String readText(String path){
        StringBuilder sb = new StringBuilder();
        try(InputStream a = GLHelper.class.getResourceAsStream(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(a))){
            String str;
            while ((str = reader.readLine()) != null){
                sb.append(str).append('\n');
            }
        }
        catch (IOException e){

        }
        return sb.toString();
    }
}
