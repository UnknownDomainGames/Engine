package engine.graphics.gl.texture;

import engine.graphics.gl.util.GLCleaner;
import engine.graphics.gl.util.GLHelper;
import engine.graphics.texture.FilterMode;
import engine.graphics.texture.Sampler;
import engine.graphics.texture.WrapMode;
import engine.graphics.util.Cleaner;
import engine.graphics.util.DepthCompareMode;
import engine.util.Color;
import org.lwjgl.opengl.*;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;

public final class GLSampler implements Sampler {

    public static final Sampler DEFAULT = new GLSampler(null);

    private int id;
    private Cleaner.Disposable disposable;

    public GLSampler() {
        if (GLHelper.isOpenGL45()) {
            id = GL45.glCreateSamplers();
        } else {
            id = GL33.glGenSamplers();
        }
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
    public Sampler setMagFilter(FilterMode filterMode) {
        setSamplerParameteri(GL11.GL_TEXTURE_MAG_FILTER, GLTexture.toGLFilterMode(filterMode));
        return this;
    }

    @Override
    public Sampler setMinFilter(FilterMode filterMode) {
        setSamplerParameteri(GL11.GL_TEXTURE_MIN_FILTER, GLTexture.toGLFilterMode(filterMode));
        return this;
    }

    @Override
    public Sampler setWrapMode(WrapMode wrapMode) {
        int glWrapMode = GLTexture.toGLWrapMode(wrapMode);
        setSamplerParameteri(GL11.GL_TEXTURE_WRAP_S, glWrapMode);
        setSamplerParameteri(GL11.GL_TEXTURE_WRAP_T, glWrapMode);
        setSamplerParameteri(GL12.GL_TEXTURE_WRAP_R, glWrapMode);
        return this;
    }

    @Override
    public Sampler setMinLod(float min) {
        GL33.glSamplerParameterf(id, GL12.GL_TEXTURE_MIN_LOD, min);
        return this;
    }

    @Override
    public Sampler setMaxLod(float max) {
        GL33.glSamplerParameterf(id, GL12.GL_TEXTURE_MAX_LOD, max);
        return this;
    }

    @Override
    public Sampler setBorderColor(Color color) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            setSamplerParameterfv(GL11.GL_TEXTURE_BORDER_COLOR, color.get(stack.mallocFloat(4)));
        }
        return this;
    }

    @Override
    public Sampler setDepthCompareMode(DepthCompareMode depthCompareMode) {
        setSamplerParameteri(GL14.GL_TEXTURE_COMPARE_MODE, GLHelper.toGLCompareMode(depthCompareMode));
        setSamplerParameteri(GL14.GL_TEXTURE_COMPARE_FUNC, GLHelper.toGLCompareFunc(depthCompareMode));
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

    private void setSamplerParameteri(int pname, int param) {
        GL33.glSamplerParameteri(id, pname, param);
    }

    private void setSamplerParameterfv(int pname, FloatBuffer params) {
        GL33.glSamplerParameterfv(id, pname, params);
    }
}
