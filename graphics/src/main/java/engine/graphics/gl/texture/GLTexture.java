package engine.graphics.gl.texture;

import engine.graphics.gl.util.GLCleaner;
import engine.graphics.gl.util.GLHelper;
import engine.graphics.texture.FilterMode;
import engine.graphics.texture.Texture;
import engine.graphics.texture.TextureFormat;
import engine.graphics.texture.WrapMode;
import engine.graphics.util.Cleaner;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL45;

import java.nio.FloatBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL14.GL_MIRRORED_REPEAT;

public abstract class GLTexture implements Texture {

    protected final int target;

    protected int id;
    protected Cleaner.Disposable disposable;

    protected final GLTextureFormat format;

    public static int toGLFilterMode(FilterMode filterMode) {
        switch (filterMode) {
            case LINEAR:
                return GL_LINEAR;
            case NEAREST:
                return GL_NEAREST;
            case LINEAR_MIPMAP_LINEAR:
                return GL_LINEAR_MIPMAP_LINEAR;
            case LINEAR_MIPMAP_NEAREST:
                return GL_LINEAR_MIPMAP_NEAREST;
            case NEAREST_MIPMAP_LINEAR:
                return GL_NEAREST_MIPMAP_LINEAR;
            case NEAREST_MIPMAP_NEAREST:
                return GL_NEAREST_MIPMAP_NEAREST;
            default:
                throw new IllegalArgumentException();
        }
    }

    public static int toGLWrapMode(WrapMode wrapMode) {
        switch (wrapMode) {
            case CLAMP:
                return GL_CLAMP;
            case REPEAT:
                return GL_REPEAT;
            case CLAMP_TO_EDGE:
                return GL_CLAMP_TO_EDGE;
            case CLAMP_TO_BORDER:
                return GL_CLAMP_TO_BORDER;
            case MIRRORED_REPEAT:
                return GL_MIRRORED_REPEAT;
            default:
                throw new IllegalArgumentException();
        }
    }

    public GLTexture(int target, GLTextureFormat format) {
        this.target = target;
        this.id = GLHelper.isSupportARBDirectStateAccess() ? GL45.glCreateTextures(target) : GL11.glGenTextures();
        this.format = format;
        this.disposable = GLCleaner.registerTexture(this, id);
    }

    public GLTexture(int target) {
        this.target = target;
        this.id = 0;
        this.format = GLTextureFormat.RGB8;
    }

    public int getTarget() {
        return target;
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public TextureFormat getFormat() {
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
        glBindTexture(target, id);
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
            GL45.glTextureParameteri(id, pname, param);
        } else {
            GL11.glBindTexture(target, id);
            GL11.glTexParameteri(target, pname, param);
        }
    }

    public void setTextureParameterfv(int pname, FloatBuffer params) {
        if (GLHelper.isSupportARBDirectStateAccess()) {
            GL45.glTextureParameterfv(id, pname, params);
        } else {
            GL11.glBindTexture(target, id);
            GL11.glTexParameterfv(target, pname, params);
        }
    }
}
