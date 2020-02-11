package engine.graphics.gl.texture;

import engine.graphics.gl.util.GLCleaner;
import engine.graphics.texture.FilterMode;
import engine.graphics.texture.Sampler;
import engine.graphics.texture.WrapMode;
import engine.graphics.util.Cleaner;

import static engine.graphics.gl.texture.GLTexture.toGLFilterMode;
import static engine.graphics.gl.texture.GLTexture.toGLWrapMode;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_TEXTURE_WRAP_R;
import static org.lwjgl.opengl.GL33.glGenSamplers;
import static org.lwjgl.opengl.GL33.glSamplerParameteri;

public class GLSampler implements Sampler {

    public static final Sampler DEFAULT = new GLSampler(null);

    private int id;
    private Cleaner.Disposable disposable;

    public GLSampler() {
        id = glGenSamplers();
        disposable = GLCleaner.registerSampler(this, id);
    }

    private GLSampler(Void arg0) {
        this.id = 0;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setMagFilter(FilterMode filterMode) {
        glSamplerParameteri(id, GL_TEXTURE_MAG_FILTER, toGLFilterMode(filterMode));
    }

    @Override
    public void setMinFilter(FilterMode filterMode) {
        glSamplerParameteri(id, GL_TEXTURE_MIN_FILTER, toGLFilterMode(filterMode));
    }

    @Override
    public void setWrapS(WrapMode wrapMode) {
        glSamplerParameteri(id, GL_TEXTURE_WRAP_S, toGLWrapMode(wrapMode));
    }

    @Override
    public void setWrapT(WrapMode wrapMode) {
        glSamplerParameteri(id, GL_TEXTURE_WRAP_T, toGLWrapMode(wrapMode));
    }

    @Override
    public void setWrapR(WrapMode wrapMode) {
        glSamplerParameteri(id, GL_TEXTURE_WRAP_R, toGLWrapMode(wrapMode));
    }

    @Override
    public void dispose() {
        if (id == 0) return;

        disposable.dispose();
        id = 0;
    }

    @Override
    public boolean isDisposed() {
        return id == 0;
    }
}
