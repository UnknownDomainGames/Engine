package unknowndomain.engine.client.rendering;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import unknowndomain.engine.api.client.display.Camera;
import unknowndomain.engine.api.client.rendering.Renderer;
import unknowndomain.engine.api.client.shader.ShaderProgram;
import unknowndomain.engine.client.rendering.shader.ShaderProgramCommon;
import unknowndomain.engine.client.block.model.BasicData;
import unknowndomain.engine.client.block.model.Mesh;
import unknowndomain.engine.client.block.model.Texture;

public class RenderCommon implements Renderer {
    private ShaderProgram shader;
    private Camera camera;
    protected int u_Projection, u_View, u_Model;

    private Debug debug = new Debug();

    public RenderCommon(Camera camera) {
        this.camera = camera;
        this.shader = new ShaderProgramCommon();

        shader.createShader();

        shader.useShader();
        u_Projection = shader.getUniformLocation("u_ProjMatrix");
        u_View = shader.getUniformLocation("u_ViewMatrix");
        u_Model = shader.getUniformLocation("u_ModelMatrix");

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glCullFace(GL11.GL_BACK);
    }

    public void render() {
        shader.useShader();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        ShaderProgram.setUniform(u_Projection, camera.projection());
        ShaderProgram.setUniform(u_View, camera.view());

        debug.render();
    }

    class Debug {
        private Mesh mesh;

        public Debug() {
            Texture texture;
            try {
                texture = new Texture("/textures/grassblock.png");
                this.mesh = new Mesh(BasicData.INSTANCE.getPositions(), BasicData.INSTANCE.getTextCoords(),
                        BasicData.INSTANCE.getIndices(), texture);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        public void render() {
            for (int i = 0; i < 100; i++) {
                for (int j = 0; j < 100; j++) {
                    ShaderProgram.setUniform(u_Model, new Matrix4f().setTranslation(i, 0, j));
                    mesh.render();
                }
            }
        }
    }
}
