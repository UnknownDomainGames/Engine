package engine.graphics.graph;

import engine.graphics.shader.ShaderResource;
import engine.graphics.texture.FrameBuffer;

public interface Drawer {
    DrawerInfo getInfo();

    RenderPass getRenderPass();

    ShaderResource getShaderResource();

    FrameBuffer getFrameBuffer();
}
