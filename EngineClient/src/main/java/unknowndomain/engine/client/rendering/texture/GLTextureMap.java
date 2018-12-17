package unknowndomain.engine.client.rendering.texture;

public class GLTextureMap extends GLTexture {
    private int dimension;

    public GLTextureMap(int id, int dimension) {
        super(id, 16, 16);
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
        return "GLTexture { id: " + getId() + ", dimension: " + dimension + " }";
    }
}