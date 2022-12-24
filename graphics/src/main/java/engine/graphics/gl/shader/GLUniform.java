package engine.graphics.gl.shader;

import engine.graphics.shader.Uniform;

public class GLUniform implements Uniform {
    private final String name;

    public GLUniform(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
