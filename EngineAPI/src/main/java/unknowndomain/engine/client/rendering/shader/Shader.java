package unknowndomain.engine.client.rendering.shader;

import org.apache.commons.io.IOUtils;
import org.joml.*;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;
import org.lwjgl.system.MemoryStack;
import unknowndomain.engine.Engine;

import java.io.IOException;
import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL20.*;

public class Shader {
    private int shaderId;
    private ShaderType type;

    public Shader(int shaderId, ShaderType type) {
        this.shaderId = shaderId;
        this.type = type;
    }

    public ShaderType getType() {
        return type;
    }

    @Override
    public String toString() {
        return "Shader{" + "programId=" + shaderId + ", type=" + type + '}';
    }

    public void deleteShader() {
        if (shaderId != -1) {
            glDeleteShader(shaderId);
            shaderId = -1;
        }
    }

    public int getShaderId() {
        return shaderId;
    }

    public static Shader create(byte[] content, ShaderType type) throws IOException {
        return create(IOUtils.toString(content, "utf-8"), type);
    }

    public static Shader create(String content, ShaderType type) {
        int shaderId = glCreateShader(type.getGlEnum());
        glShaderSource(shaderId, content);

        glCompileShader(shaderId);
        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            Engine.getLogger().warn(String.format("Error compiling shader code for %s, log: %s", content,
                    glGetShaderInfoLog(shaderId, 2048)));
        }
        return new Shader(shaderId, type);
    }
}
