package engine.graphics.gl.shader;

import org.lwjgl.opengl.GL20C;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CompiledShader {

    private static final Logger LOGGER = LoggerFactory.getLogger(CompiledShader.class);

    private int id;
    private final GLShaderType type;

    public static CompiledShader compile(GLShaderType type, String source) {
        int id = GL20C.glCreateShader(type.gl);
        GL20C.glShaderSource(id, source);
        GL20C.glCompileShader(id);
        if (GL20C.glGetShaderi(id, GL20C.GL_COMPILE_STATUS) == 0) {
            LOGGER.warn("Error compiling shader code for {}, log: {}", source, GL20C.glGetShaderInfoLog(id));
        }
        return new CompiledShader(id, type);
    }

    private CompiledShader(int id, GLShaderType type) {
        this.id = id;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public GLShaderType getType() {
        return type;
    }

    public void dispose() {
        if (id == 0) return;

        GL20C.glDeleteShader(id);
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
