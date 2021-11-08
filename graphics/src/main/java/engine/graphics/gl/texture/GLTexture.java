package engine.graphics.gl.texture;

import engine.graphics.gl.util.GLCleaner;
import engine.graphics.gl.util.GLHelper;
import engine.graphics.texture.ColorFormat;
import engine.graphics.texture.FilterMode;
import engine.graphics.texture.Texture;
import engine.graphics.texture.WrapMode;
import engine.graphics.util.Cleaner;
import org.apache.commons.lang3.Validate;
import org.lwjgl.opengl.*;

import java.nio.FloatBuffer;

public abstract class GLTexture implements Texture {

    protected final int target;

    protected int id;
    protected Cleaner.Disposable disposable;

    protected final GLColorFormat format;

    public static int toGLFilterMode(FilterMode filterMode) {
        switch (filterMode) {
            case LINEAR:
                return GL11C.GL_LINEAR;
            case NEAREST:
                return GL11C.GL_NEAREST;
            case LINEAR_MIPMAP_LINEAR:
                return GL11C.GL_LINEAR_MIPMAP_LINEAR;
            case LINEAR_MIPMAP_NEAREST:
                return GL11C.GL_LINEAR_MIPMAP_NEAREST;
            case NEAREST_MIPMAP_LINEAR:
                return GL11C.GL_NEAREST_MIPMAP_LINEAR;
            case NEAREST_MIPMAP_NEAREST:
                return GL11C.GL_NEAREST_MIPMAP_NEAREST;
            default:
                throw new IllegalArgumentException();
        }
    }

    public static int toGLWrapMode(WrapMode wrapMode) {
        switch (wrapMode) {
            case REPEAT:
                return GL11C.GL_REPEAT;
            case CLAMP_TO_EDGE:
                return GL12C.GL_CLAMP_TO_EDGE;
            case CLAMP_TO_BORDER:
                return GL13C.GL_CLAMP_TO_BORDER;
            case MIRRORED_REPEAT:
                return GL14C.GL_MIRRORED_REPEAT;
            default:
                throw new IllegalArgumentException();
        }
    }

    public GLTexture(int target, GLColorFormat format) {
        this.target = target;
        this.id = GLHelper.isSupportARBDirectStateAccess() ? GL45C.glCreateTextures(target) : GL11C.glGenTextures();
        this.format = Validate.notNull(format);
        this.disposable = GLCleaner.registerTexture(this, id);
    }

    public GLTexture(int target) {
        this.target = target;
        this.id = 0;
        this.format = GLColorFormat.RGB8;
    }

    public int getTarget() {
        return target;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public ColorFormat getFormat() {
        return format.peer;
    }

    public boolean isMultiSample() {
        return false;
    }

    @Override
    public int getSamples() {
        return 0;
    }

    public void bind() {
        GL11C.glBindTexture(target, id);
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

    public void setTextureParameteri(int pname, int param) {
        if (GLHelper.isSupportARBDirectStateAccess()) {
            GL45C.glTextureParameteri(id, pname, param);
        } else {
            GL11C.glBindTexture(target, id);
            GL11C.glTexParameteri(target, pname, param);
        }
    }

    public void setTextureParameterfv(int pname, FloatBuffer params) {
        if (GLHelper.isSupportARBDirectStateAccess()) {
            GL45C.glTextureParameterfv(id, pname, params);
        } else {
            GL11C.glBindTexture(target, id);
            GL11C.glTexParameterfv(target, pname, params);
        }
    }
}
