package unknowndomain.engine.client.rendering.gui;

import org.joml.Matrix4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import unknowndomain.engine.client.resource.Resource;
import unknowndomain.engine.client.resource.ResourceManager;
import unknowndomain.engine.client.resource.ResourcePath;
import unknowndomain.engine.client.shader.RendererShaderProgram;
import unknowndomain.engine.client.shader.Shader;
import unknowndomain.engine.client.shader.ShaderType;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * render for any gui
 */
public class RendererGui extends RendererShaderProgram {
    private TTFFontRenderer fontRenderer;

    private void createShader(Shader vertexShader, Shader fragShader) {
        shaderId = GL20.glCreateProgram();

        attachShader(vertexShader);
        attachShader(fragShader);

        linkShader();
        useShader();

        vertexShader.deleteShader();
        fragShader.deleteShader();

        GL20.glValidateProgram(shaderId);

        GL20.glUseProgram(0);
    }

    @Override
    protected void useShader() {
        super.useShader();

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    }

    @Override
    public void init(ResourceManager manager) throws IOException {
        createShader(
                Shader.create(manager.load(new ResourcePath("", "unknowndomain/shader/gui.vert")).cache(),
                        ShaderType.VERTEX_SHADER),
                Shader.create(manager.load(new ResourcePath("", "unknowndomain/shader/gui.frag")).cache(),
                        ShaderType.FRAGMENT_SHADER));

        Tessellator.getInstance().setShaderId(shaderId);
        Resource resource = manager.load(new ResourcePath("", "unknowndomain/fonts/arial.ttf"));
        byte[] cache = resource.cache();
        ByteBuffer direct = BufferUtils.createByteBuffer(cache.length);
        direct.put(cache);
        direct.flip();
        this.fontRenderer = new TTFFontRenderer(direct);
    }

    @Override
    public void render(Context context) {
        useShader();
        // this.setUniform("projection", context.getCamera().projection());
        // this.setUniform("view", context.getCamera().view());
        // setUniform("projection", new Matrix4f().identity().ortho(0, 854f, 480f, 0, -1000f, 2000f));
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        setUniform("usingAlpha", true);

        setUniform("model", new Matrix4f().scale(0.01F));
        fontRenderer.drawText("abcd", 0, 0, 0xffffffff, 16);
    }

    @Override
    public void dispose() {

    }
}
