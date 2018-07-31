package unknowndomain.engine.client.util;


import org.lwjgl.opengl.GLCapabilities;
import unknowndomain.engine.api.client.shader.ShaderProgram;

public class OpenGLHelper {

    private static GLCapabilities context;



    public static void initHelper(GLCapabilities context){
        OpenGLHelper.context = context;
    }

    public static boolean isHelperAvailable(){
        return context != null;
    }

    private static ShaderProgram currentShaderProgram;

    public static void useShaderProgram(ShaderProgram sp){
        currentShaderProgram = sp;
        currentShaderProgram.useShader();
    }

    public static int getCurrentShaderId(){
        return currentShaderProgram.getShaderProgramId();
    }

}
