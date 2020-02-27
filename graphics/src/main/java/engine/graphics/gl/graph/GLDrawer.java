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
    private final DrawDispatcher drawDispatcher;

    public GLDrawer(DrawerInfo info, GLRenderPass renderPass) {
        this.info = info;
        this.renderPass = renderPass;
        this.shader = ShaderManager.load(info.getShader());
        this.drawDispatcher = info.getDrawDispatcher();
    }

    public DrawerInfo getInfo() {
        return info;
    }

    public GLRenderPass getRenderPass() {
        return renderPass;
    }

    public void draw(Frame frame) {
        shader.use();
        drawDispatcher.draw(frame, shader);
    }

    public void dispose() {
        shader.dispose();
    }
}
