package engine.graphics.gl.texture;

import engine.graphics.gl.util.GLCleaner;
import engine.graphics.gl.util.GLHelper;
import engine.graphics.texture.ColorFormat;
import engine.graphics.texture.Texture;
import engine.graphics.util.Cleaner;
import org.apache.commons.lang3.Validate;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL45C;

public class GLTexture implements Texture {

    public static final GLTexture NONE = new GLTexture();

    protected final int target;
    protected final GLColorFormat format;

    protected int id;
    protected Cleaner.Disposable disposable;

    public GLTexture(int target, GLColorFormat format) {
        this.target = target;
        this.id = GLHelper.isSupportARBDirectStateAccess() ? GL45C.glCreateTextures(target) : GL11C.glGenTextures();
        this.format = Validate.notNull(format);
        this.disposable = GLCleaner.registerTexture(this, id);
    }

    private GLTexture() {
        this.target = 0;
        this.id = 0;
        this.format = GLColorFormat.RGBA8;
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

    public GLColorFormat getGLFormat() {
        return format;
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
}
