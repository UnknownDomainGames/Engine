package engine.graphics.graph;

import engine.graphics.shader.ShaderResource;

public interface DrawDispatcher {

    void draw(Frame frame, ShaderResource shader);
}
