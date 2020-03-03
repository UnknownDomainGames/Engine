package engine.graphics.graph;

import engine.graphics.shader.ShaderResource;

public interface DrawDispatcher {

    void init(ShaderResource resource);

    void draw(Frame frame, ShaderResource resource, Renderer renderer);
}
