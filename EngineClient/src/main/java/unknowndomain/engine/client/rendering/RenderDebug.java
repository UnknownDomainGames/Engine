package unknowndomain.engine.client.rendering;

import org.joml.Matrix4f;
import org.joml.Rayd;
import org.joml.Rayf;
import org.lwjgl.opengl.GL11;
import unknowndomain.engine.client.shader.Shader;
import unknowndomain.engine.client.resource.Pipeline;
import unknowndomain.engine.client.model.GLMesh;
import unknowndomain.engine.client.model.Mesh;
import unknowndomain.engine.client.model.MeshToGLNode;
import unknowndomain.engine.client.texture.GLTexture;
import unknowndomain.engine.client.world.EasyWorld;
import unknowndomain.engine.math.BlockPos;
import unknowndomain.engine.block.BlockObject;

import java.util.Map;

public class RenderDebug extends RendererShaderProgramCommon implements Pipeline.Endpoint {
    private GLTexture texture;
    private EasyWorld world;
    private Map<BlockObject, GLMesh> meshMap;

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

    public RenderDebug(Shader vertexShader, Shader fragmentShader, EasyWorld world, Map<BlockObject, GLMesh> meshMap) {
        super(vertexShader, fragmentShader);
        this.world = world;
        this.meshMap = meshMap;
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

        BlockPos pick = world.pick(context.getCamera().getPosition(), context.getCamera().getFrontVector(), 10);

        for (Map.Entry<BlockPos, BlockObject> entry : world.getAllBlock()) {
            BlockPos pos = entry.getKey();
            BlockObject value = entry.getValue();
            GLMesh mesh = meshMap.get(value);

            boolean match = pick != null && pick.equals(pos);
            if (match) {
                this.setUniform("u_Picked", 1);
            }

            Shader.setUniform(u_Model, new Matrix4f().setTranslation(pos.getX(), pos.getY(), pos.getZ()));
            mesh.render();
            if (match) {
                this.setUniform("u_Picked", 0);
            }
        }
    }

    @Override
    public void accept(String source, Object content) {
        switch (source) {
            case "TextureMap":
                this.texture = (GLTexture) content;
                break;
        }
    }
}