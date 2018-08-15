package unknowndomain.engine.client.rendering;

import java.io.IOException;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import unknowndomain.engine.client.resource.ResourceManager;
import unknowndomain.engine.client.resource.ResourcePath;
import unknowndomain.engine.client.shader.RendererShaderProgram;
import unknowndomain.engine.client.shader.Shader;
import unknowndomain.engine.client.shader.ShaderType;

public abstract class RendererShaderProgramCommon extends RendererShaderProgram {
    protected final int A_POSITION = 0, A_TEXTCOORD = 1, A_NORMAL = 2, A_COLOR = 3;

    protected int u_Projection, u_View, u_Model;

    protected abstract ResourcePath vertexShader();
    protected abstract ResourcePath fragmentShader();

    public void init(ResourceManager resourceManager) throws IOException {
        createShader(Shader.create(resourceManager.load(vertexShader()).cache(), ShaderType.VERTEX_SHADER),
                Shader.create(resourceManager.load(fragmentShader()).cache(), ShaderType.FRAGMENT_SHADER));
        useShader();
        u_Projection = getUniformLocation("u_ProjMatrix");
        u_View = getUniformLocation("u_ViewMatrix");
        u_Model = getUniformLocation("u_ModelMatrix");
    }

    private void createShader(Shader... shaders) {
        shaderId = GL20.glCreateProgram();

        for (Shader s : shaders)
            attachShader(s);

        linkShader();
        useShader();

        GL20.glValidateProgram(shaderId);

        for (Shader s : shaders)
            s.deleteShader();

        GL20.glUseProgram(0);
    }

    @Override
    protected void useShader() {
        super.useShader();

        // GL11.glEnable(GL11.GL_CULL_FACE);
        // GL11.glFrontFace(GL11.GL_CW);
        // GL11.glCullFace(GL11.GL_BACK);
    }

    public void render(Context context) {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
        useShader();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        Shader.setUniform(u_Projection, context.getCamera().projection());
        Shader.setUniform(u_View, context.getCamera().view());
    }

    @Override
    public void dispose() {
        GL20.glUseProgram(0);
        GL20.glDeleteProgram(shaderId);
        shaderId = -1;
    }

}
