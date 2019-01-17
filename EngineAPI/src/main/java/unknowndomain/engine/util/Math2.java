package unknowndomain.engine.util;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class Math2 {
    private Math2() {
    }

    public static int roundUp(int value, int interval) {
        if (value == 0)
            return 0;
        if (interval == 0)
            return 0;
        if (value < 0)
            interval *= -1;
        int mod = value % interval;
        return mod == 0 ? value : value + interval - mod;
    }

    public static float roundUp(float value, float interval) {
        if (value == 0)
            return 0;
        if (interval == 0)
            return 0;
        if (value < 0)
            interval *= -1;
        int mod = (int) (value / interval);
        return mod == 0 ? value : value - mod * interval;
    }

    public static float clamp(float val, float min, float max){
        return Math.min(max,Math.max(val,min));
    }
    public static int clamp(int val, int min, int max){
        return Math.min(max,Math.max(val,min));
    }
    public static long clamp(long val, long min, long max){
        return Math.min(max,Math.max(val,min));
    }
    public static double clamp(double val, double min, double max){
        return Math.min(max,Math.max(val,min));
    }

    /**
     * Calculate normal of a triangle by vertices.
     * @param vertices
     * @return
     */
    public static Vector3fc calcNormalByVertices(float[] vertices){
        assert vertices.length == 9;
        float nx,ny,nz = 0;
        var u = new Vector3f(vertices[3],vertices[4],vertices[5]).sub(vertices[0],vertices[1],vertices[2]);
        var v = new Vector3f(vertices[6],vertices[7],vertices[8]).sub(vertices[0],vertices[1],vertices[2]);
        nx = u.y * v.z - u.z * v.y;
        ny = u.z * v.x - u.x * v.z;
        nz = u.x * v.y - u.y * v.x;
        return new Vector3f(nx,ny,nz).normalize();
    }
}
