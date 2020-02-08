package engine.graphics.gl.shader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.lwjgl.opengl.GL20.*;

public class CompiledShader {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompiledShader.class);

    private int id;
    private final ShaderType type;

    public static CompiledShader compile(ShaderType type, String source) {
        int id = glCreateShader(type.gl);
        glShaderSource(id, source);
        glCompileShader(id);
        if (glGetShaderi(id, GL_COMPILE_STATUS) == 0) {
            LOGGER.warn("Error compiling shader code for {}, log: {}", source, glGetShaderInfoLog(id));
        }
        return new CompiledShader(id, type);
    }

    private CompiledShader(int id, ShaderType type) {
        this.id = id;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public ShaderType getType() {
        return type;
    }

    public void dispose() {
        if (id == 0) return;

        glDeleteShader(id);
        this.id = 0;
    }

    @Override
    public String toString() {
        return "Shader{" +
                "id=" + id +
                ", type=" + type +
                '}';
    }
}
