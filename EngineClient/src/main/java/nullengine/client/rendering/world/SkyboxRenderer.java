package nullengine.client.rendering.world;

import com.github.mouse0w0.observable.value.ObservableValue;
import nullengine.client.asset.Asset;
import nullengine.client.asset.AssetURL;
import nullengine.client.rendering.RenderManager;
import nullengine.client.rendering.model.GLMesh;
import nullengine.client.rendering.model.Mesh;
import nullengine.client.rendering.shader.ShaderManager;
import nullengine.client.rendering.shader.ShaderProgram;
import nullengine.client.rendering.texture.GLTexture;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

import static nullengine.client.asset.AssetTypes.TEXTURE;

public class SkyboxRenderer {

    private RenderManager context;
    private ObservableValue<ShaderProgram> worldShader;
    private Asset<GLTexture> skybox;
    private GLMesh skyboxMesh;

    public void init(RenderManager context) {
        this.context = context;
        worldShader = ShaderManager.instance().getShader("world_shader");
        skybox = context.getEngine().getAssetManager().create(TEXTURE, AssetURL.of("engine", "texture/misc/skybox.png"));
        skyboxMesh = GLMesh.of(new Mesh(
                new float[]{
                        256, 256, -256,
                        256, 256, 256,
                        256, -256, -256,
                        256, -256, 256, //East
                        -256, 256, -256,
                        -256, 256, 256,
                        -256, -256, -256,
                        -256, -256, 256, //West
                        -256, 256, -256,
                        256, 256, -256,
                        -256, -256, -256,
                        256, -256, -256, //North
                        256, 256, 256,
                        -256, 256, 256,
                        256, -256, 256,
                        -256, -256, 256, //South
                        256, 256, -256,
                        256, 256, 256,
                        -256, 256, -256,
                        -256, 256, 256, //Up
                        256, -256, -256,
                        256, -256, 256,
                        -256, -256, -256,
                        -256, -256, 256, //Down
                },
                new float[]{
                        0.6666667f, 0.0f,
                        1.0f, 0.0f,
                        0.6666667f, 0.5f,
                        1.0f, 0.5f, //East
                        0.33333334f, 0.0f,
                        0.0f, 0.0f,
                        0.33333334f, 0.5f,
                        0.0f, 0.5f, //West
                        0.33333334f, 0.0f,
                        0.6666667f, 0.0f,
                        0.33333334f, 0.5f,
                        0.6666667f, 0.5f, //North
                        0.0f, 0.5f,
                        0.33333334f, 0.5f,
                        0.0f, 1.0f,
                        0.33333334f, 1.0f, //South
                        0.33333334f, 1.0f,
                        0.6666667f, 1.0f,
                        0.33333334f, 0.5f,
                        0.6666667f, 0.5f, //Up
                        0.6666667f, 0.5f,
                        1.0f, 0.5f,
                        0.6666667f, 1.0f,
                        1.0f, 1.0f, //Down
                },
                new float[0],
                new int[]{
                        0, 2, 1,
                        2, 3, 1, //East
                        4, 5, 6,
                        6, 5, 7, //West
                        8, 10, 9,
                        10, 11, 9, //North
                        12, 14, 13,
                        13, 14, 15, //South
                        16, 17, 18,
                        18, 17, 19, //Up
                        20, 22, 21,
                        21, 22, 23 //Down

                },
                GL11.GL_TRIANGLES
        ));
    }

    public void render(float partial){
        ShaderProgram program = worldShader.getValue();
        ShaderManager.instance().bindShader(program);
        skybox.get().bind();
        program.setUniform("u_ViewMatrix", context.getCamera().getViewMatrix());
        program.setUniform("u_ProjMatrix", context.getWindow().projection());
        program.setUniform("u_ModelMatrix", new Matrix4f());
        skyboxMesh.render();
    }
}
