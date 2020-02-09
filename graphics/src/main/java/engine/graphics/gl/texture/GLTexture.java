package engine.graphics.gl.texture;

import engine.graphics.gl.util.GLCleaner;
import engine.graphics.texture.FilterMode;
import engine.graphics.texture.Texture;
import engine.graphics.texture.TextureFormat;
import engine.graphics.texture.WrapMode;
import engine.graphics.util.Cleaner;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_CLAMP_TO_BORDER;
import static org.lwjgl.opengl.GL14.GL_MIRRORED_REPEAT;

public abstract class GLTexture implements Texture {

    protected int id;
    protected Cleaner.Disposable disposable;

    protected GLTextureFormat format;

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

    protected GLTexture(int id) {
        this.id = id;
        disposable = GLCleaner.registerTexture(this, id);
    }

    public abstract int getTarget();

    @Override
    public int getId() {
        return id;
    }

    @Override
    public TextureFormat getFormat() {
        return format.peer;
    }

    public void bind() {
        glBindTexture(getTarget(), id);
    }

    @Override
    public void dispose() {
        if (id == -1) return;

        disposable.dispose();
        id = -1;
    }
}
