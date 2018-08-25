package unknowndomain.engine.client.rendering;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import unknowndomain.engine.client.model.GLMesh;
import unknowndomain.engine.client.model.Mesh;
import unknowndomain.engine.client.resource.ResourceManager;
import unknowndomain.engine.client.resource.ResourcePath;
import unknowndomain.engine.client.shader.RendererShaderProgram;
import unknowndomain.engine.client.shader.Shader;
import unknowndomain.engine.client.shader.ShaderType;
import unknowndomain.engine.client.texture.GLTexture;

import java.io.IOException;

public class RendererSkybox extends RendererShaderProgram {
    protected int u_Projection;
    protected int u_View;
    protected int u_Model;
    private GLTexture texsky;

    private GLMesh glMesh;

    //    @Override
    public void init(ResourceManager resourceManager) throws IOException {
        createShader(Shader.create(resourceManager.load(vertexShader()).cache(), ShaderType.VERTEX_SHADER),
                Shader.create(resourceManager.load(fragmentShader()).cache(), ShaderType.FRAGMENT_SHADER));

        texsky = GLTexture.ofPNG(resourceManager.load(new ResourcePath("", "unknowndomain/textures/misc/skybox_steel.png")).open());
        useProgram();
        u_Projection = getUniformLocation("u_ProjMatrix");
        u_View = getUniformLocation("u_ViewMatrix");
        u_Model = getUniformLocation("u_ModelMatrix");

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

    @Override
    public void render(Context context) {

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        useProgram();

        Matrix4f pro = context.getProjection().projection();
        Matrix4f view = new Matrix4f(context.getCamera().view());
        view.m30(0).m31(0).m32(0);
        Shader.setUniform(u_Projection, pro);
        Shader.setUniform(u_View, view);
        Shader.setUniform(u_Model, new Matrix4f().identity());

        texsky.bind();
        glMesh.render();
    }

    @Override
    public void dispose() {
        GL20.glUseProgram(0);
        GL20.glDeleteProgram(programId);
        programId = -1;
    }


    protected ResourcePath vertexShader() {
        return new ResourcePath("", "unknowndomain/shader/common.vert");
    }

    protected ResourcePath fragmentShader() {
        return new ResourcePath("", "unknowndomain/shader/common.frag");
    }
}
