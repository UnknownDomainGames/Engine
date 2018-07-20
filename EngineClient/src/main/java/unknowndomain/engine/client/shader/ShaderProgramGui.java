package unknowndomain.engine.client.shader;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import unknowndomain.engine.api.client.shader.Shader;
import unknowndomain.engine.api.client.shader.ShaderType;
import unknowndomain.engine.client.util.GLHelper;

public class ShaderProgramGui extends ShaderProgramDefault {

    private boolean useAlphaChannel;

    private boolean useTexture;

    public ShaderProgramGui(){
        vertexShader = new Shader("assets/unknowndomain/shader/gui.vert", ShaderType.VERTEX_SHADER);
        fragmentShader = new Shader("assets/unknowndomain/shader/gui.frag", ShaderType.FRAGMENT_SHADER);
    }

    @Override
    public void createShader() {

        super.createShader();

        setUniform("texImage", 0);

        Matrix4f model = new Matrix4f();
        model.identity();
        setUniform("model", model);

        Matrix4f view = new Matrix4f();
        view.identity();
        setUniform("view", view);


        Matrix4f projection = new Matrix4f().identity();
        projection = projection.ortho(0,854,480,0,-1000f,2000f);

        setUniform("projection", projection);

        setUniform("color", new Vector4f(1f,1f,1f,1f));


        GL20.glUseProgram(0);
    }

    public boolean isUsingAlphaChannel() {
        return useAlphaChannel;
    }

    public void setUseAlphaChannel(boolean useAlphaChannel) {
        this.useAlphaChannel = useAlphaChannel;
        setUniform("usingAlpha", useAlphaChannel);
    }

    public boolean isUsingTexture() {
        return useTexture;
    }

    public void setUseTexture(boolean useTexture) {
        this.useTexture = useTexture;
        setUniform("usingTex", useAlphaChannel);
    }
}
