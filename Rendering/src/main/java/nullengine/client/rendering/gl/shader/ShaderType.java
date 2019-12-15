package nullengine.client.rendering.gl.shader;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL32;
import org.lwjgl.opengl.GL40;
import org.lwjgl.opengl.GL43;

public enum ShaderType {

    VERTEX_SHADER(GL20.GL_VERTEX_SHADER),
    TESSELLATION_CONTROL_SHADER(GL40.GL_TESS_CONTROL_SHADER),
    TESSELLATION_EVALUATION_SHADER(GL40.GL_TESS_EVALUATION_SHADER),
    GEOMETRY_SHADER(GL32.GL_GEOMETRY_SHADER),
    FRAGMENT_SHADER(GL20.GL_FRAGMENT_SHADER),
    COMPUTE_SHADER(GL43.GL_COMPUTE_SHADER);

    public final int gl;

    ShaderType(int gl) {
        this.gl = gl;
    }
}
