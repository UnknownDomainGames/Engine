package unknowndomain.engine.client.rendering;

import unknowndomain.engine.api.client.display.Camera;
import unknowndomain.engine.client.model.GLMesh;
import unknowndomain.engine.client.texture.GLTexture;

public class RenderBlock extends RenderCommon {
    private GLMesh mesh;
    private GLTexture texture;

    public RenderBlock(Camera camera, GLMesh mesh, GLTexture texture) {
        super(camera);
        this.mesh = mesh;
        this.texture = texture;
    }

    /**
     * @param mesh the mesh to set
     */
    public void setMesh(GLMesh mesh) {
        this.mesh = mesh;
    }

    /**
     * @param texture the texture to set
     */
    public void setTexture(GLTexture texture) {
        this.texture = texture;
    }

    /**
     * @return the mesh
     */
    public GLMesh getMesh() {
        return mesh;
    }

    /**
     * @return the texture
     */
    public GLTexture getTexture() {
        return texture;
    }

    @Override
    public void render() {
        super.render();
        if (mesh != null && texture != null) {
            texture.bind();
            mesh.render();
        }
    }
}