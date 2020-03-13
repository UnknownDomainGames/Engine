package engine.graphics.graph;

import engine.graphics.shader.ShaderResource;
import engine.graphics.texture.FrameBuffer;

import java.util.Map;
import java.util.function.Consumer;

public interface Drawer {
    DrawerInfo getInfo();

    RenderPass getRenderPass();

    ShaderResource getShaderResource();

    FrameBuffer getFrameBuffer();

    void dispatchTask(String name, Frame frame, Map<String, Object> args, Consumer<RenderTask> callback);
}
