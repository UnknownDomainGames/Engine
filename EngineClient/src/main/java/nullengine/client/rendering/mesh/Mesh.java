package nullengine.client.rendering.mesh;

public class Mesh {
    private final float[] vertices;
    private final float[] uv;
    private final float[] normals;
    private final int[] indices;
    private final int mode;

    public Mesh(float[] vertices, float[] uv, float[] normals, int[] indices, int mode) {
        this.vertices = vertices;
        this.uv = uv;
        this.normals = normals;
        this.indices = indices;
        this.mode = mode;
    }

    /**
     * @return the mode
     */
    public int getMode() {
        return mode;
    }

    public int[] getIndices() {
        return indices;
    }

    public float[] getVertices() {
        return vertices;
    }

    public float[] getUv() {
        return uv;
    }

    public float[] getNormals() {
        return normals;
    }

//    public static class Builder {
//        public static Builder parse() {
//            return new Builder();
//        }
//
//        public Builder vertex(float x, float y, float z) {
//
//            return this;
//        }
//    }
}
