package nullengine.client.rendering.math;

import org.joml.Vector3f;
import org.joml.Vector3fc;

public class RenderingMath {
    /**
     * Calculate normal of a triangle by vertices.
     *
     * @param vertices
     * @return
     */
    public static Vector3fc calcNormalByVertices(float[] vertices) {
        assert vertices.length == 9;
        float nx, ny, nz = 0;
        var u = new Vector3f(vertices[3], vertices[4], vertices[5]).sub(vertices[0], vertices[1], vertices[2]);
        var v = new Vector3f(vertices[6], vertices[7], vertices[8]).sub(vertices[0], vertices[1], vertices[2]);
        nx = u.y * v.z - u.z * v.y;
        ny = u.z * v.x - u.x * v.z;
        nz = u.x * v.y - u.y * v.x;
        return new Vector3f(nx, ny, nz).normalize();
    }

    /**
     * Calculate normal of a triangle by vertices.
     *
     * @return
     */
    public static Vector3fc calcNormalByVertices(Vector3fc v1, Vector3fc v2, Vector3fc v3) {
        var u = new Vector3f();
        var v = new Vector3f();
        v2.sub(v1, u);
        v3.sub(v1, v);
        float nx = u.y * v.z - u.z * v.y;
        float ny = u.z * v.x - u.x * v.z;
        float nz = u.x * v.y - u.y * v.x;
        return new Vector3f(nx, ny, nz).normalize();
    }
}
