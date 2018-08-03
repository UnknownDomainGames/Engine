package unknowndomain.engine.client.rendering;

import unknowndomain.engine.api.client.display.Camera;
import unknowndomain.engine.api.client.shader.ShaderProgram;

public interface RenderContext {
    Camera getCamera();

    ShaderProgram getShader();
}