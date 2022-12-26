package engine.graphics.gl.graph;

import engine.graphics.gl.shader.ShaderManager;
import engine.graphics.gl.shader.ShaderProgram;
import engine.graphics.graph.DrawDispatcher;
import engine.graphics.graph.Drawer;
import engine.graphics.graph.DrawerInfo;
import engine.graphics.graph.Frame;
import engine.graphics.shader.ShaderResource;
import engine.graphics.texture.FrameBuffer;

public final class GLDrawer implements Drawer {
    private final DrawerInfo info;
    private final GLRenderPass renderPass;

    private final ShaderProgram shader;
    private final GLShaderResource shaderResource;
    private final DrawDispatcher drawDispatcher;

    public GLDrawer(DrawerInfo info, GLRenderPass renderPass) {
        this.info = info;
        this.renderPass = renderPass;
        this.shader = ShaderManager.load(info.getShader());
        this.shaderResource = new GLShaderResource(shader);
        this.drawDispatcher = info.getDrawDispatcher();
        this.drawDispatcher.init(this);
    }

    @Override
    public DrawerInfo getInfo() {
        return info;
    }

    @Override
    public GLRenderPass getRenderPass() {
        return renderPass;
    }

    @Override
    public ShaderResource getShaderResource() {
        return shaderResource;
    }

    @Override
    public FrameBuffer getFrameBuffer() {
        return renderPass.getFrameBuffer();
    }

    public void draw(Frame frame) {
        shader.use();
        drawDispatcher.draw(frame, this, GLRenderer.getInstance());
    }

    public void dispose() {
        shader.dispose();
    }
}
