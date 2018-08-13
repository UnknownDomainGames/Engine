package unknowndomain.engine.client.rendering.gui;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import unknowndomain.engine.client.resource.Resource;
import unknowndomain.engine.client.resource.ResourceManager;
import unknowndomain.engine.client.resource.ResourcePath;
import unknowndomain.engine.client.shader.RendererShaderProgram;
import unknowndomain.engine.client.shader.Shader;

import java.io.IOException;
import java.nio.ByteBuffer;

/**
 * render for any gui
 */
public class RendererGui extends RendererShaderProgram {
    private TTFFontRenderer fontRenderer;
    private Shader guiShader;

    private void createShader() {

        shaderId = GL20.glCreateProgram();

        attachShader(guiShader);

        linkShader();
        useShader();

        GL20.glValidateProgram(shaderId);

        GL20.glUseProgram(0);
    }

    @Override
    public void init(ResourceManager manager) throws IOException {

//        manager.load(new ResourcePath("", "unknowndomain/"))

        createShader();


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

    }

    @Override
    public void dispose() {

    }
}
