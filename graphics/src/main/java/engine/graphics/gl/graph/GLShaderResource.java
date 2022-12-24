package engine.graphics.gl.graph;

import engine.graphics.gl.shader.GLUniformBlock;
import engine.graphics.gl.shader.GLUniformImage;
import engine.graphics.gl.shader.GLUniformTexture;
import engine.graphics.gl.shader.ShaderProgram;
import engine.graphics.shader.ShaderResource;
import engine.graphics.shader.UniformBlock;
import engine.graphics.shader.UniformImage;
import engine.graphics.shader.UniformTexture;
import org.lwjgl.opengl.GL15C;
import org.lwjgl.opengl.GL31C;

import java.util.ArrayList;
import java.util.List;

public class GLShaderResource implements ShaderResource {
    private final ShaderProgram shader;

    private final List<GLUniformBlock> blocks = new ArrayList<>();
    private final List<GLUniformTexture> textures = new ArrayList<>();
    private final List<GLUniformImage> images = new ArrayList<>();

    private int nextTextureUnit = 0;
    private int nextImageUnit = 0;

    public GLShaderResource(ShaderProgram shader) {
        this.shader = shader;
    }

    @Override
    public UniformTexture getUniformTexture(String name) {
        GLUniformTexture texture = new GLUniformTexture(name, shader.getUniformLocation(name), nextTextureUnit++);
        textures.add(texture);
        return texture;
    }

    @Override
    public UniformImage getUniformImage(String name, boolean canRead, boolean canWrite) {
        int access;
        if (canRead) {
            if (canWrite) access = GL15C.GL_READ_WRITE;
            else access = GL15C.GL_READ_ONLY;
        } else {
            if (canWrite) access = GL15C.GL_WRITE_ONLY;
            else throw new IllegalArgumentException("canRead and canWrite cannot both be false.");
        }
        GLUniformImage texture = new GLUniformImage(name, shader.getUniformLocation(name), nextImageUnit++, access);
        images.add(texture);
        return texture;
    }

    @Override
    public UniformBlock getUniformBlock(String name, long size) {
        int program = shader.getId();
        int index = GL31C.glGetUniformBlockIndex(program, name);
        if (index == GL31C.GL_INVALID_INDEX)
            throw new IllegalArgumentException("Failed to get uniform block: " + name);
        int binding = blocks.size();
        GL31C.glUniformBlockBinding(program, index, binding);
        GLUniformBlock block = new GLUniformBlock(name, size, binding);
        blocks.add(block);
        return block;
    }

    @Override
    public void setup() {
        textures.forEach(GLUniformTexture::bind);
        blocks.forEach(GLUniformBlock::bind);
        images.forEach(GLUniformImage::bind);
    }
}
