package engine.graphics.gl.texture;

import engine.graphics.gl.util.GLCleaner;
import engine.graphics.gl.util.GLHelper;
import engine.graphics.texture.FilterMode;
import engine.graphics.texture.Sampler;
import engine.graphics.texture.WrapMode;
import engine.graphics.util.Cleaner;
import engine.graphics.util.DepthCompareMode;
import engine.util.Color;
import org.lwjgl.opengl.GL33;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_COMPARE_FUNC;
import static org.lwjgl.opengl.GL14.GL_TEXTURE_COMPARE_MODE;
import static org.lwjgl.opengl.GL33.glBindSampler;
import static org.lwjgl.opengl.GL33.glGenSamplers;

public final class GLSampler implements Sampler {

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
    public void bind(int unit) {
        glBindSampler(unit, id);
    }

    @Override
    public Sampler setMagFilter(FilterMode filterMode) {
        GL33.glSamplerParameteri(id, GL_TEXTURE_MAG_FILTER, GLTexture.toGLFilterMode(filterMode));
        return this;
    }

    @Override
    public Sampler setMinFilter(FilterMode filterMode) {
        GL33.glSamplerParameteri(id, GL_TEXTURE_MIN_FILTER, GLTexture.toGLFilterMode(filterMode));
        return this;
    }

    @Override
    public Sampler setWrapMode(WrapMode wrapMode) {
        int glWrapMode = GLTexture.toGLWrapMode(wrapMode);
        GL33.glSamplerParameteri(id, GL_TEXTURE_WRAP_S, glWrapMode);
        GL33.glSamplerParameteri(id, GL_TEXTURE_WRAP_T, glWrapMode);
        GL33.glSamplerParameteri(id, GL_TEXTURE_WRAP_R, glWrapMode);
        return this;
    }

    @Override
    public Sampler setMinLod(float min) {
        GL33.glSamplerParameterf(id, GL_TEXTURE_MIN_LOD, min);
        return this;
    }

    @Override
    public Sampler setMaxLod(float max) {
        GL33.glSamplerParameterf(id, GL_TEXTURE_MAX_LOD, max);
        return this;
    }

    @Override
    public Sampler setBorderColor(Color color) {
        GL33.glSamplerParameterfv(id, GL_TEXTURE_BORDER_COLOR,
                new float[]{color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()});
        return this;
    }

    @Override
    public Sampler setDepthCompareMode(DepthCompareMode depthCompareMode) {
        GL33.glSamplerParameteri(id, GL_TEXTURE_COMPARE_MODE, GLHelper.toGLCompareMode(depthCompareMode));
        GL33.glSamplerParameteri(id, GL_TEXTURE_COMPARE_FUNC, GLHelper.toGLCompareFunc(depthCompareMode));
        return this;
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
