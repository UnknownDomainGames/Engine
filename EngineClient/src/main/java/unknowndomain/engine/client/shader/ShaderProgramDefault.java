package unknowndomain.engine.client.shader;

import unknowndomain.engine.api.client.shader.ShaderProgram;
import unknowndomain.engine.client.util.GLHelper;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

public class ShaderProgramDefault extends ShaderProgram {

    protected int shaderId = -1;
    protected int vertexShaderId = -1;
    protected int fragmentShaderId = -1;

    @Override
    public void createShader() {

        vertexShaderId = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);

        //TODO: maybe we should extract shader into a separated class?
        GL20.glShaderSource(vertexShaderId, GLHelper.readText("/assets/unknowndomain/shader/default.vert"));
        GL20.glCompileShader(vertexShaderId);
        if (GL20.glGetShaderi(vertexShaderId, GL20.GL_COMPILE_STATUS) == 0) {
            System.out.println("Error compiling Shader code: " + GL20.glGetShaderInfoLog(vertexShaderId, 1024));
        }

        fragmentShaderId = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);

        GL20.glShaderSource(fragmentShaderId, GLHelper.readText("/assets/unknowndomain/shader/default.frag"));
        GL20.glCompileShader(fragmentShaderId);
        if (GL20.glGetShaderi(fragmentShaderId, GL20.GL_COMPILE_STATUS) == 0) {
            System.out.println("Error compiling Shader code: " + GL20.glGetShaderInfoLog(fragmentShaderId, 1024));
        }

        shaderId = GL20.glCreateProgram();
        GL20.glAttachShader(shaderId, vertexShaderId);
        GL20.glAttachShader(shaderId, fragmentShaderId);
        GL30.glBindFragDataLocation(shaderId, 0, "fragColor");
        GL20.glLinkProgram(shaderId);
        useShader();

        GL20.glValidateProgram(shaderId);

        //We can deleter the shader after linking to the shader program
        GL20.glDeleteShader(vertexShaderId);
        GL20.glDeleteShader(fragmentShaderId);

        int uniTex = getUniformLocation("texImage");
        GL20.glUniform1i(uniTex, 0);

        Matrix4f model = new Matrix4f();
        model.identity();
        int uniModel = getUniformLocation("model");
        setUniform(uniModel, model);

        Matrix4f view = new Matrix4f();
        view.identity();
        view = view.lookAt(new Vector3f(0,0,-5f),new Vector3f(0,0,0f),new Vector3f(0,1,0)); //TODO: Controlled by Camera
        int uniView = getUniformLocation("view");
        setUniform(uniView, view);


        Matrix4f projection = new Matrix4f();
        projection = projection.perspective((float)Math.toRadians(60), 16.0f/9.0f, 0.01f,1000f); //TODO: Controlled by Camera
        int uniProjection = getUniformLocation("projection");
        setUniform(uniProjection, projection);


        GL20.glUseProgram(0);
    }

    @Override
    public void deleteShader() {

        GL20.glUseProgram(0);
        GL20.glDeleteShader(vertexShaderId);
        vertexShaderId = -1;
        GL20.glDeleteShader(fragmentShaderId);
        fragmentShaderId = -1;
        GL20.glDeleteProgram(shaderId);
        shaderId = -1;
    }

    @Override
    public void linkShader() {
        GL20.glLinkProgram(shaderId);
    }

    @Override
    public void useShader() {
        GL20.glUseProgram(shaderId);
    }

    @Override
    public int getUniformLocation(String name) {
        return GL20.glGetUniformLocation(shaderId, name);
    }

    @Override
    public int getAttributeLocation(String name){
        return GL20.glGetAttribLocation(shaderId, name);
    }

    @Override
    public void setUniform(String location, Object value) {
        setUniform(getUniformLocation(location), value);
    }
}
