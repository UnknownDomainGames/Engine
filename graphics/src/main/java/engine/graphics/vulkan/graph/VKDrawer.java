package engine.graphics.vulkan.graph;

import engine.graphics.gl.graph.GLRenderPass;
import engine.graphics.gl.graph.GLShaderResource;
import engine.graphics.gl.shader.ShaderManager;
import engine.graphics.gl.shader.ShaderProgram;
import engine.graphics.graph.DrawDispatcher;
import engine.graphics.graph.DrawerInfo;

public class VKDrawer {
    private final DrawerInfo info;
    private final VKRenderGraphPass renderPass;

//    private final VKShaderResource shaderResource;
//    private final DrawDispatcher drawDispatcher;

    public VKDrawer(DrawerInfo info, VKRenderGraphPass renderPass) {
        this.info = info;
        this.renderPass = renderPass;
//        this.shader = ShaderManager.load(info.getShader());
//        this.shaderResource = new GLShaderResource(shader);
//        this.drawDispatcher = info.getDrawDispatcher();
//        this.drawDispatcher.init(shaderResource);
    }
}
