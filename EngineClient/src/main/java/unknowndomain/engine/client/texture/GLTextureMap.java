package unknowndomain.engine.client.texture;

public class GLTextureMap extends GLTexture {
    private int dimension;

    public GLTextureMap(int id, int dimension) {
        super(id);
        this.dimension = dimension;
    }

    /**
     * @return the dimension
     */
    public int getDimension() {
        return dimension;
    }

    @Override
    public String toString() {
        return "GLTexture { id: " + id + ", dimension: " + dimension + " }";
    }
}