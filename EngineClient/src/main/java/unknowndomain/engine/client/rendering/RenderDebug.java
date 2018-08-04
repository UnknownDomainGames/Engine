package unknowndomain.engine.client.rendering;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import unknowndomain.engine.api.client.shader.Shader;
import unknowndomain.engine.api.resource.Pipeline;
import unknowndomain.engine.client.model.GLMesh;
import unknowndomain.engine.client.model.Mesh;
import unknowndomain.engine.client.model.MeshToGLNode;
import unknowndomain.engine.client.texture.GLTexture;

import java.util.List;

public class RenderDebug extends RendererShaderProgramCommon implements Pipeline.Endpoint {
    private GLTexture texture;
    private List<GLMesh> meshList;

    private GLMesh textureMap;

    {
        textureMap = new MeshToGLNode().convert(new Mesh(new float[]{
                0, 0, 0,
                2, 0, 0,
                2, 2, 0,
                0, 2, 0,
        }, new float[]{
                0, 1,
                1, 1,
                1, 0,
                0, 0,
        }, new float[]{

        }, new int[]{
                0, 2, 1, 0, 3, 2
        }, GL11.GL_TRIANGLES));
    }

    public RenderDebug(Shader vertexShader, Shader fragmentShader) {
        super(vertexShader, fragmentShader);
    }

    /**
     * @return the texture
     */
    public GLTexture getTexture() {
        return texture;
    }

    /**
     * @param texture the texture to set
     */
    public void setTexture(GLTexture texture) {
        this.texture = texture;
    }

    @Override
    public void render(Context context) {
        super.render(context);

        if (texture != null)
            texture.bind();
        Shader.setUniform(u_Model, new Matrix4f().setTranslation(2, 2, 2));
        textureMap.render();
        if (meshList != null && texture != null) {
            texture.bind();
            for (int i = 0; i < meshList.size(); i++) {
                Shader.setUniform(u_Model, new Matrix4f().setTranslation(2, 0, i - 5));
                GLMesh glMesh = meshList.get(i);
                if (glMesh != null) glMesh.render();
            }
        }
    }

    @Override
    public void accept(String source, Object content) {
        switch (source) {
            case "TextureMap":
                this.texture = (GLTexture) content;
                break;
            case "BlockModels":
                this.meshList = (List<GLMesh>) content;
                break;
        }
    }
}