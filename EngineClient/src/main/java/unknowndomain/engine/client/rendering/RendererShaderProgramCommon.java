package unknowndomain.engine.client.rendering;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import unknowndomain.engine.client.resource.ResourceManager;
import unknowndomain.engine.client.shader.RendererShaderProgram;
import unknowndomain.engine.client.shader.Shader;

public abstract class RendererShaderProgramCommon extends RendererShaderProgram {
    protected final int A_POSITION = 0, A_TEXTCOORD = 1, A_NORMAL = 2, A_COLOR = 3;
    private Shader vertexShader;
    private Shader fragmentShader;

    protected int u_Projection, u_View, u_Model;

    public RendererShaderProgramCommon(Shader vertexShader, Shader fragmentShader) {
        this.vertexShader = vertexShader;
        this.fragmentShader = fragmentShader;
    }

    public void init(ResourceManager resourceManager) {
        createShader();
        useShader();
        u_Projection = getUniformLocation("u_ProjMatrix");
        u_View = getUniformLocation("u_ViewMatrix");
        u_Model = getUniformLocation("u_ModelMatrix");
    }

    protected void createShader() {
        shaderId = GL20.glCreateProgram();

        attachShader(vertexShader);
        attachShader(fragmentShader);

        linkShader();
        useShader();

        GL20.glValidateProgram(shaderId);

        GL20.glUseProgram(0);
    }

    @Override
    protected void useShader() {
        super.useShader();

        GL11.glEnable(GL11.GL_CULL_FACE);
        GL11.glFrontFace(GL11.GL_CW);
        GL11.glCullFace(GL11.GL_BACK);
    }

    public void render(Context context) {
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
        vertexShader.deleteShader();
        fragmentShader.deleteShader();
        GL20.glDeleteProgram(shaderId);
        shaderId = -1;
    }

}
