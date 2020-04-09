package engine.graphics.gl.shader;

import engine.graphics.shader.ShaderType;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL43;

public enum GLShaderType {

    VERTEX_SHADER(ShaderType.VERTEX_SHADER, GL20.GL_VERTEX_SHADER),
    TESSELLATION_CONTROL_SHADER(ShaderType.TESSELLATION_CONTROL_SHADER, GL40.GL_TESS_CONTROL_SHADER),
    TESSELLATION_EVALUATION_SHADER(ShaderType.TESSELLATION_EVALUATION_SHADER, GL40.GL_TESS_EVALUATION_SHADER),
    GEOMETRY_SHADER(ShaderType.GEOMETRY_SHADER, GL32.GL_GEOMETRY_SHADER),
    FRAGMENT_SHADER(ShaderType.FRAGMENT_SHADER, GL20.GL_FRAGMENT_SHADER),
    COMPUTE_SHADER(ShaderType.COMPUTE_SHADER, GL43.GL_COMPUTE_SHADER);

    private ShaderType peer;
    public final int gl;

    public static GLShaderType valueOf(ShaderType type) {
        return values()[type.ordinal()];
    }

    GLShaderType(ShaderType peer, int gl) {
        this.peer = peer;
        this.gl = gl;
    }
}
