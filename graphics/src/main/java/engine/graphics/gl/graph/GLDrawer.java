package engine.graphics.gl.graph;

import engine.graphics.gl.shader.ShaderManager;
import engine.graphics.gl.shader.ShaderProgram;
import engine.graphics.graph.DrawDispatcher;
import engine.graphics.graph.DrawerInfo;
import engine.graphics.graph.Frame;

public final class GLDrawer {
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
        this.drawDispatcher.init(shaderResource);
    }

    public DrawerInfo getInfo() {
        return info;
    }

    public GLRenderPass getRenderPass() {
        return renderPass;
    }

    public void draw(Frame frame) {
        shader.use();
        drawDispatcher.draw(frame, shaderResource, GLRenderer.getInstance());
    }

    public void dispose() {
        shader.dispose();
    }
}
