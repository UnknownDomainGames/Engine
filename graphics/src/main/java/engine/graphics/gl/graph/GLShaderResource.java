package engine.graphics.gl.graph;

import engine.graphics.gl.shader.GLUniformBlock;
import engine.graphics.gl.shader.GLUniformTexture;
import engine.graphics.gl.shader.ShaderProgram;
import engine.graphics.shader.ShaderResource;
import engine.graphics.shader.UniformBlock;
import engine.graphics.shader.UniformTexture;
import org.joml.*;
import org.lwjgl.opengl.GL31;

import java.util.ArrayList;
import java.util.List;

public class GLShaderResource implements ShaderResource {

    private final ShaderProgram shader;

    private final List<GLUniformBlock> blocks = new ArrayList<>();
    private final List<GLUniformTexture> textures = new ArrayList<>();

    public GLShaderResource(ShaderProgram shader) {
        this.shader = shader;
    }

    @Override
    public UniformBlock getUniformBlock(String name) {
        int programId = shader.getId();
        int blockIndex = GL31.glGetUniformBlockIndex(programId, name);
        int blockBinding = blocks.size();
        GL31.glUniformBlockBinding(programId, blockIndex, blockBinding);
        GLUniformBlock uniformBlock = new GLUniformBlock(name, blockBinding);
        blocks.add(uniformBlock);
        return uniformBlock;
    }

    @Override
    public UniformTexture getUniformTexture(String name) {
        var texture = new GLUniformTexture(name, shader.getUniformLocation(name), textures.size());
        textures.add(texture);
        return texture;
    }

    @Override
    public void setUniform(String name, int value) {
        shader.setUniform(name, value);
    }

    @Override
    public void setUniform(String name, float value) {
        shader.setUniform(name, value);
    }

    @Override
    public void setUniform(String name, boolean value) {
        shader.setUniform(name, value);
    }

    @Override
    public void setUniform(String name, Vector2fc value) {
        shader.setUniform(name, value);
    }

    @Override
    public void setUniform(String name, Vector3fc value) {
        shader.setUniform(name, value);
    }

    @Override
    public void setUniform(String name, Vector4fc value) {
        shader.setUniform(name, value);
    }

    @Override
    public void setUniform(String name, Matrix3fc value) {
        shader.setUniform(name, value);
    }

    @Override
    public void setUniform(String name, Matrix3x2fc value) {
        shader.setUniform(name, value);
    }

    @Override
    public void setUniform(String name, Matrix4fc value) {
        shader.setUniform(name, value);
    }

    @Override
    public void setUniform(String name, Matrix4x3fc value) {
        shader.setUniform(name, value);
    }

    @Override
    public void setUniform(String name, Matrix4fc[] values) {
        shader.setUniform(name, values);
    }

    @Override
    public void refresh() {

    }
}
