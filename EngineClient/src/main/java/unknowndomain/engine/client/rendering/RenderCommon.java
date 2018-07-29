package unknowndomain.engine.client.rendering;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import unknowndomain.engine.api.client.display.Camera;
import unknowndomain.engine.api.client.rendering.Renderer;
import unknowndomain.engine.api.client.rendering.RenderingLayer;
import unknowndomain.engine.api.client.shader.ShaderProgram;
import unknowndomain.engine.client.block.model.BasicData;
import unknowndomain.engine.client.block.model.Mesh;
import unknowndomain.engine.client.block.model.Texture;
import unknowndomain.engine.client.shader.ShaderProgramCommon;


public class RenderCommon extends RenderingLayer {
    private ShaderProgram shader;
    private Camera camera;
    private int u_Projection, u_View, u_Model;

    public RenderCommon(Camera camera) {
        this.camera = camera;
        this.shader = new ShaderProgramCommon();
        this.putRenderer(new GrassBlockRenderer());

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

        super.render();
    }

    class GrassBlockRenderer implements Renderer {
        Mesh mesh;

        private GrassBlockRenderer() {
            try {
                this.mesh = new Mesh(BasicData.INSTANCE.getPositions(), BasicData.INSTANCE.getTextCoords()
                        , BasicData.INSTANCE.getIndices(), new Texture("/assets/unknowndomain/textures/block/grassblock.png"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void render() {
            ShaderProgram.setUniform(u_Model, new Matrix4f()
                    .setTranslation(2f, 0, 0)
                    .rotateY((float) Math.toRadians(90)));
            mesh.render();
//            ShaderProgram.setUniform(u_Model, new Matrix4f().setTranslation(0, 0, -2));
//            mesh.render();
        }
    }
}
