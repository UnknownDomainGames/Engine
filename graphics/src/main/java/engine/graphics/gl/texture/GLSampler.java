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

public final class GLSampler implements Sampler {

    public static final Sampler NONE = new GLSampler();

    private int id;
    private Cleaner.Disposable disposable;

    public static Builder builder() {
        return new Builder();
    }

    private GLSampler(Builder builder) {
        if (GLHelper.isSupportARBDirectStateAccess()) {
            id = GL45C.glCreateSamplers();
        } else {
            id = GL33C.glGenSamplers();
        }
        disposable = GLCleaner.registerSampler(this, id);
        GL33C.glSamplerParameteri(id, GL11C.GL_TEXTURE_MAG_FILTER, builder.magFilter);
        GL33C.glSamplerParameteri(id, GL11C.GL_TEXTURE_MIN_FILTER, builder.minFilter);
        GL33C.glSamplerParameteri(id, GL11C.GL_TEXTURE_WRAP_S, builder.wrapS);
        GL33C.glSamplerParameteri(id, GL11C.GL_TEXTURE_WRAP_T, builder.wrapT);
        GL33C.glSamplerParameteri(id, GL12C.GL_TEXTURE_WRAP_R, builder.wrapR);
        if (builder.minLod != -1000f) {
            GL33C.glSamplerParameterf(id, GL12C.GL_TEXTURE_MIN_LOD, builder.minLod);
        }
        if (builder.maxLod != 1000f) {
            GL33C.glSamplerParameterf(id, GL12C.GL_TEXTURE_MAX_LOD, builder.maxLod);
        }
        if (builder.borderColor != Color.TRANSPARENT) {
            GL33C.glSamplerParameterfv(id, GL12C.GL_TEXTURE_BORDER_COLOR, builder.borderColor.toRGBAFloatArray());
        }
        if (builder.compareMode != GL11C.GL_NONE) {
            GL33C.glSamplerParameteri(id, GL14C.GL_TEXTURE_COMPARE_MODE, builder.compareMode);
            GL33C.glSamplerParameteri(id, GL14C.GL_TEXTURE_COMPARE_FUNC, builder.compareFunc);
        }
    }

    private GLSampler() {
        this.id = 0;
    }

    @Override
    public int getId() {
        return id;
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

    public static final class Builder implements Sampler.Builder {
        private int magFilter = GL11C.GL_LINEAR;
        private int minFilter = GL11C.GL_NEAREST_MIPMAP_LINEAR;
        private int wrapS = GL11C.GL_REPEAT;
        private int wrapT = GL11C.GL_REPEAT;
        private int wrapR = GL11C.GL_REPEAT;
        private float minLod = -1000f;
        private float maxLod = 1000f;
        private Color borderColor = Color.TRANSPARENT;
        private int compareMode = GL11C.GL_NONE;
        private int compareFunc = GL11C.GL_ALWAYS;

        @Override
        public Builder magFilter(FilterMode filterMode) {
            this.magFilter = GLHelper.toGLFilterMode(filterMode);
            return this;
        }

        @Override
        public Builder minFilter(FilterMode filterMode) {
            this.minFilter = GLHelper.toGLFilterMode(filterMode);
            return this;
        }

        @Override
        public Builder wrapS(WrapMode wrapMode) {
            this.wrapS = GLHelper.toGLWrapMode(wrapMode);
            return this;
        }

        @Override
        public Builder wrapT(WrapMode wrapMode) {
            this.wrapT = GLHelper.toGLWrapMode(wrapMode);
            return this;
        }

        @Override
        public Builder wrapR(WrapMode wrapMode) {
            this.wrapR = GLHelper.toGLWrapMode(wrapMode);
            return this;
        }

        @Override
        public Builder minLod(float min) {
            this.minLod = min;
            return this;
        }

        @Override
        public Builder maxLod(float max) {
            this.maxLod = max;
            return this;
        }

        @Override
        public Builder borderColor(Color color) {
            this.borderColor = color;
            return this;
        }

        @Override
        public Builder depthCompareMode(DepthCompareMode depthCompareMode) {
            this.compareMode = GLHelper.toGLCompareMode(depthCompareMode);
            this.compareFunc = GLHelper.toGLCompareFunc(depthCompareMode);
            return this;
        }

        @Override
        public GLSampler build() {
            return new GLSampler(this);
        }
    }
}
