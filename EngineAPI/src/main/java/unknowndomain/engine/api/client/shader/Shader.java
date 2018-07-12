package unknowndomain.engine.api.client.shader;

import org.apache.commons.io.IOUtils;
import org.lwjgl.opengl.GL20;
import unknowndomain.engine.api.Platform;

import java.io.IOException;

import static org.lwjgl.opengl.GL20.*;

public class Shader {

    private int shaderId = -1;

    private ShaderType type;

    private String location;

    public Shader(String loc, ShaderType type){
        location = loc;
        this.type = type;
    }

    public void loadShader(){
        shaderId = glCreateShader(type.getGlEnum());

        try {
            glShaderSource(shaderId, IOUtils.toString(IOUtils.resourceToURL(location, Shader.class.getClassLoader()), "utf-8"));
        } catch (IOException e) {
            Platform.getClientLogger().warn(String.format("Error reading shader code for %s", location), e);
        }
        glCompileShader(shaderId);
        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            Platform.getClientLogger().warn(String.format("Error compiling shader code for %s, log: %s", location, glGetShaderInfoLog(shaderId, 2048)));
        }
    }

    public void deleteShader(){
        if(shaderId != -1){
            glDeleteShader(shaderId);
            shaderId = -1;
        }
    }

    public int getShaderId() {
        return shaderId;
    }
}
