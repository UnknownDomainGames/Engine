package unknowndomain.engine.client.rendering.shader;

import org.lwjgl.opengl.*;

public enum ShaderType {
    VERTEX_SHADER(GL20.GL_VERTEX_SHADER),
    TESSELLATION_CONTROL_SHADER(GL40.GL_TESS_CONTROL_SHADER),
    TESSELLATION_EVALUATION_SHADER(GL40.GL_TESS_EVALUATION_SHADER),
    TESSELLATION_CONTROL_SHADER_ARB(ARBTessellationShader.GL_TESS_CONTROL_SHADER),
    TESSELLATION_EVALUATION_SHADER_ARB(ARBTessellationShader.GL_TESS_EVALUATION_SHADER),
    GEOMATRY_SHADER(GL32.GL_GEOMETRY_SHADER),
    FRAGMENT_SHADER(GL20.GL_FRAGMENT_SHADER),
    COMPUTE_SHADER(GL43.GL_COMPUTE_SHADER),
    COMPUTE_SHADER_ARB(ARBComputeShader.GL_COMPUTE_SHADER),;


    private int glEnum;

    ShaderType(int gl){
        glEnum = gl;
    }

    public int getGlEnum() {
        return glEnum;
    }
}
