package engine.graphics.vulkan.graph;

import engine.graphics.graph.*;
import engine.graphics.shader.ShaderResource;
import engine.graphics.texture.FrameBuffer;
import engine.graphics.vulkan.CommandBuffer;

import java.util.Map;
import java.util.function.Consumer;

public class VKDrawer implements Drawer {
    private final DrawerInfo info;
    private final VKRenderGraphPass renderPass;

    //    private final VKShaderResource shaderResource;
    private final DrawDispatcher drawDispatcher;

    public VKDrawer(DrawerInfo info, VKRenderGraphPass renderPass) {
        this.info = info;
        this.renderPass = renderPass;
//        this.shader = ShaderManager.load(info.getShader());
//        this.shaderResource = new GLShaderResource(shader);
        this.drawDispatcher = info.getDrawDispatcher();
        this.drawDispatcher.init(this);
    }

    @Override
    public DrawerInfo getInfo() {
        return info;
    }

    @Override
    public RenderPass getRenderPass() {
        return renderPass;
    }

    @Override
    public ShaderResource getShaderResource() {
//        return shaderResource;
        return null;
    }

    @Override
    public FrameBuffer getFrameBuffer() {
        return renderPass.getFrameBuffer();
    }

    @Override
    public void dispatchTask(String name, Frame frame, Map<String, Object> args, Consumer<RenderTask> callback) {

    }

    public void draw(Frame frame, CommandBuffer cmdBuffer) {
        drawDispatcher.draw(frame, this, new VKRenderer(cmdBuffer));
    }

    public void dispose() {
    }
}
