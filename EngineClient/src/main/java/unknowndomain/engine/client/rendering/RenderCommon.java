package unknowndomain.engine.client.rendering;

import org.lwjgl.opengl.GL11;
import unknowndomain.engine.api.client.display.Camera;
import unknowndomain.engine.api.client.rendering.Renderer;
import unknowndomain.engine.api.client.shader.ShaderProgram;
//import unknowndomain.engine.client.model.Model;
import unknowndomain.engine.client.rendering.shader.ShaderProgramCommon;

import java.util.Collections;
import java.util.List;

public class RenderCommon implements Renderer {
    private ShaderProgram shader;
    private Camera camera;
    private int u_Projection, u_View, u_Model;
//    private List<Model> modelList = Collections.emptyList();

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
//
//        for (Model model : modelList) {
//            model.render();
//        }
//        super.render();
    }
}
