package nullengine.client.rendering.world;

import com.github.mouse0w0.lib4j.observable.value.ObservableValue;
import nullengine.client.asset.AssetPath;
import nullengine.client.rendering.RenderContext;
import nullengine.client.rendering.model.GLMesh;
import nullengine.client.rendering.model.Mesh;
import nullengine.client.rendering.shader.ShaderManager;
import nullengine.client.rendering.shader.ShaderProgram;
import nullengine.client.rendering.texture.GLTexture;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;

public class SkyboxRenderer {

    private RenderContext context;
    private ObservableValue<ShaderProgram> worldShader;
    private GLTexture texsky;
    private GLMesh glMesh;

    public void init(RenderContext context){
        this.context = context;
        worldShader = ShaderManager.INSTANCE.getShader("world_shader");
        texsky = context.getTextureManager().getTextureDirect(AssetPath.of("engine","texture", "misc","skybox.png"));
        glMesh = GLMesh.of(new Mesh(
                new float[]{
                        128, 128, -128,
                        128, 128, 128,
                        128, -128, -128,
                        128, -128, 128, //East
                        -128, 128, -128,
                        -128, 128, 128,
                        -128, -128, -128,
                        -128, -128, 128, //West
                        -128, 128, -128,
                        128, 128, -128,
                        -128, -128, -128,
                        128, -128, -128, //North
                        128, 128, 128,
                        -128, 128, 128,
                        128, -128, 128,
                        -128, -128, 128, //South
                        128, 128, -128,
                        128, 128, 128,
                        -128, 128, -128,
                        -128, 128, 128, //Up
                        128, -128, -128,
                        128, -128, 128,
                        -128, -128, -128,
                        -128, -128, 128, //Down
                },
                new float[]{
                        2 * (1 / 3f), 0 * (1 / 2f),
                        (1 + 2) * (1 / 3f), 0 * (1 / 2f),
                        2 * (1 / 3f), 1 * (1 / 2f),
                        (1 + 2) * (1 / 3f), 1 * (1 / 2f), //East
                        1 * (1 / 3f), 0 * (1 / 2f),
                        0 * (1 / 3f), 0 * (1 / 2f),
                        1 * (1 / 3f), 1 * (1 / 2f),
                        0 * (1 / 3f), 1 * (1 / 2f), //West
                        1 * (1 / 3f), 0 * (1 / 2f),
                        (1 + 1) * (1 / 3f), 0 * (1 / 2f),
                        1 * (1 / 3f), 1 * (1 / 2f),
                        (1 + 1) * (1 / 3f), 1 * (1 / 2f), //North
                        0 * (1 / 3f), 1 * (1 / 2f),
                        1 * (1 / 3f), 1 * (1 / 2f),
                        0 * (1 / 3f), (1 + 1) * (1 / 2f),
                        1 * (1 / 3f), (1 + 1) * (1 / 2f), //South
                        1 * (1 / 3f), (1 + 1) * (1 / 2f),
                        (1 + 1) * (1 / 3f), (1 + 1) * (1 / 2f),
                        1 * (1 / 3f), 1 * (1 / 2f),
                        (1 + 1) * (1 / 3f), 1 * (1 / 2f), //Up
                        2 * (1 / 3f), 1 * (1 / 2f),
                        (1 + 2) * (1 / 3f), 1 * (1 / 2f),
                        2 * (1 / 3f), (1 + 1) * (1 / 2f),
                        (1 + 2) * (1 / 3f), (1 + 1) * (1 / 2f), //Down
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
        ShaderManager.INSTANCE.bindShader(program);
        texsky.bind();
        program.setUniform("u_ViewMatrix", context.getCamera().getViewMatrix());
        program.setUniform("u_ProjMatrix", context.getWindow().projection());
        program.setUniform("u_ModelMatrix", new Matrix4f());
        glMesh.render();
    }
}
