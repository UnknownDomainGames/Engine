package engine.graphics.gl.shader;

import engine.graphics.shader.ShaderType;
import org.lwjgl.opengl.GL20C;
import org.lwjgl.opengl.GL32C;
import org.lwjgl.opengl.GL40C;
import org.lwjgl.opengl.GL43C;

public enum GLShaderType {

    VERTEX_SHADER(ShaderType.VERTEX_SHADER, GL20C.GL_VERTEX_SHADER),
    TESSELLATION_CONTROL_SHADER(ShaderType.TESSELLATION_CONTROL_SHADER, GL40C.GL_TESS_CONTROL_SHADER),
    TESSELLATION_EVALUATION_SHADER(ShaderType.TESSELLATION_EVALUATION_SHADER, GL40C.GL_TESS_EVALUATION_SHADER),
    GEOMETRY_SHADER(ShaderType.GEOMETRY_SHADER, GL32C.GL_GEOMETRY_SHADER),
    FRAGMENT_SHADER(ShaderType.FRAGMENT_SHADER, GL20C.GL_FRAGMENT_SHADER),
    COMPUTE_SHADER(ShaderType.COMPUTE_SHADER, GL43C.GL_COMPUTE_SHADER);

    private static final GLShaderType[] VALUES = values();

    public final ShaderType peer;
    public final int gl;

    public static GLShaderType valueOf(ShaderType type) {
        return VALUES[type.ordinal()];
    }

    GLShaderType(ShaderType peer, int gl) {
        this.peer = peer;
        this.gl = gl;
    }
}
