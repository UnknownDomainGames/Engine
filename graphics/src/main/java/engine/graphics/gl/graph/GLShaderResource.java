package engine.graphics.gl.graph;

import engine.graphics.gl.shader.*;
import engine.graphics.shader.*;
import org.lwjgl.opengl.GL31C;

import java.util.ArrayList;
import java.util.List;

public class GLShaderResource implements ShaderResource {

    private final ShaderProgram shader;

    private final List<GLTextureBinding> textureBindings = new ArrayList<>();
    private final List<GLImageBinding> imageBindings = new ArrayList<>();
    private final List<GLUniformBlock> blocks = new ArrayList<>();
    private final List<GLUniformTexture> textures = new ArrayList<>();
    private final List<GLUniformImage> images = new ArrayList<>();

    public GLShaderResource(ShaderProgram shader) {
        this.shader = shader;
    }

    @Override
    public TextureBinding createTextureBinding() {
        GLTextureBinding binding = new GLTextureBinding(textureBindings.size());
        textureBindings.add(binding);
        return binding;
    }

    @Override
    public TextureBinding getTextureBinding(int unit) {
        return textureBindings.get(unit);
    }

    @Override
    public ImageBinding createImageBinding() {
        var binding = new GLImageBinding(imageBindings.size());
        imageBindings.add(binding);
        return binding;
    }

    @Override
    public ImageBinding getImageBinding(int unit) {
        return imageBindings.get(unit);
    }

    @Override
    public UniformTexture getUniformTexture(String name) {
        var texture = new GLUniformTexture(name, shader.getUniformLocation(name), createTextureBinding());
        textures.add(texture);
        return texture;
    }

    @Override
    public UniformImage getUniformImage(String name) {
        var texture = new GLUniformImage(name, shader.getUniformLocation(name), createImageBinding());
        images.add(texture);
        return texture;
    }

    @Override
    public UniformBlock getUniformBlock(String name) {
        int programId = shader.getId();
        int blockIndex = GL31C.glGetUniformBlockIndex(programId, name);
        if (blockIndex == GL31C.GL_INVALID_INDEX)
            throw new IllegalArgumentException("Failed to get uniform block: " + name);
        int blockBinding = blocks.size();
        GL31C.glUniformBlockBinding(programId, blockIndex, blockBinding);
        GLUniformBlock uniformBlock = new GLUniformBlock(name, blockBinding);
        blocks.add(uniformBlock);
        return uniformBlock;
    }

    @Override
    public void refresh() {
        textureBindings.forEach(GLTextureBinding::bind);
        imageBindings.forEach(GLImageBinding::bind);
        textures.forEach(GLUniformTexture::bind);
        blocks.forEach(GLUniformBlock::bind);
        images.forEach(GLUniformImage::bind);
    }
}
