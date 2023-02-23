package engine.graphics.gl.texture;

import engine.graphics.gl.util.GLCleaner;
import engine.graphics.gl.util.GLHelper;
import engine.graphics.texture.ColorFormat;
import engine.graphics.texture.FilterMode;
import engine.graphics.texture.FrameBuffer;
import engine.graphics.util.Cleaner;
import org.joml.Vector4i;
import org.joml.Vector4ic;
import org.lwjgl.opengl.GL11C;
import org.lwjgl.opengl.GL30C;
import org.lwjgl.opengl.GL32C;
import org.lwjgl.opengl.GL45C;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import static engine.graphics.gl.util.GLHelper.getMask;
import static engine.graphics.gl.util.GLHelper.glFilterMode;

public class GLFrameBuffer implements FrameBuffer {

    private static final GLFrameBuffer BACK_BUFFER = new BackBuffer();

    private final Map<Integer, Attachable> attachments;

    private int id;
    private Cleaner.Disposable disposable;

    private int width = Integer.MAX_VALUE;
    private int height = Integer.MAX_VALUE;

    public static GLFrameBuffer getBackBuffer() {
        return BACK_BUFFER;
    }

    public GLFrameBuffer() {
        if (GLHelper.isSupportARBDirectStateAccess()) {
            this.id = GL45C.glCreateFramebuffers();
        } else {
            this.id = GL30C.glGenFramebuffers();
        }
        this.disposable = GLCleaner.registerFrameBuffer(this, id);
        this.attachments = new HashMap<>();
    }

    private GLFrameBuffer(Void unused) {
        this.id = 0;
        this.attachments = Map.of();
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    public Attachable getAttachable(int attachment) {
        return attachments.get(attachment);
    }

    public void attach(int attachment, Attachable attachable) {
        if (GLHelper.isSupportARBDirectStateAccess()) {
            GL45C.glNamedFramebufferTexture(id, attachment, attachable.getId(), 0);
        } else {
            bind();
            GL32C.glFramebufferTexture(GL30C.GL_FRAMEBUFFER, attachment, attachable.getId(), 0);
        }
        attachments.put(attachment, attachable);
        if (attachable.getWidth() < width) width = attachable.getWidth();
        if (attachable.getHeight() < height) height = attachable.getHeight();
    }

    public void reset() {
        attachments.clear();
        width = Integer.MAX_VALUE;
        height = Integer.MAX_VALUE;
    }

    @Override
    public void bind() {
        GL30C.glBindFramebuffer(GL30C.GL_FRAMEBUFFER, id);
    }

    @Override
    public void bindReadOnly() {
        GL30C.glBindFramebuffer(GL30C.GL_READ_FRAMEBUFFER, id);
    }

    @Override
    public void bindDrawOnly() {
        GL30C.glBindFramebuffer(GL30C.GL_DRAW_FRAMEBUFFER, id);
    }

    @Override
    public void copyFrom(FrameBuffer src, boolean copyColor, boolean copyDepth, boolean copyStencil,
                         FilterMode filterMode) {
        copyFrom(src,
                new Vector4i(0, 0, src.getWidth(), src.getHeight()),
                new Vector4i(0, 0, getWidth(), getHeight()),
                copyColor, copyDepth, copyStencil, filterMode);
    }

    @Override
    public void copyFrom(FrameBuffer src, Vector4ic srcRect, Vector4ic destRect,
                         boolean copyColor, boolean copyDepth, boolean copyStencil, FilterMode filterMode) {
        copy(src, srcRect, this, destRect, copyColor, copyDepth, copyStencil, filterMode);
    }

    @Override
    public void readPixels(ColorFormat format, ByteBuffer pixels) {
        readPixels(0, 0, width, height, format, pixels);
    }

    @Override
    public void readPixels(int x, int y, int width, int height, ColorFormat format, ByteBuffer pixels) {
        GLColorFormat glFormat = GLColorFormat.valueOf(format);
        bindReadOnly();
        GL11C.glReadPixels(x, y, width, height, glFormat.format, glFormat.type, pixels);
    }

    public static void copy(FrameBuffer src, Vector4ic srcRect, FrameBuffer dest, Vector4ic destRect,
                            boolean copyColor, boolean copyDepth, boolean copyStencil, FilterMode filterMode) {
        copy(src, srcRect, dest, destRect, getMask(copyColor, copyDepth, copyStencil), glFilterMode(filterMode));
    }

    public static void copy(FrameBuffer src, Vector4ic srcRect, FrameBuffer dest, Vector4ic destRect, int mask, int filter) {
        if (GLHelper.isSupportARBDirectStateAccess()) {
            GL45C.glBlitNamedFramebuffer((int) src.getId(), (int) dest.getId(), srcRect.x(), srcRect.y(), srcRect.z(), srcRect.w(),
                    destRect.x(), destRect.y(), destRect.z(), destRect.w(), mask, filter);
        } else {
            src.bindReadOnly();
            dest.bindDrawOnly();
            GL30C.glBlitFramebuffer(srcRect.x(), srcRect.y(), srcRect.z(), srcRect.w(),
                    destRect.x(), destRect.y(), destRect.z(), destRect.w(), mask, filter);
        }
    }

    @Override
    public void dispose() {
        if (id == 0) return;
        disposable.dispose();
        id = 0;

        attachments.values().forEach(Attachable::dispose);
        attachments.clear();
    }

    @Override
    public boolean isDisposed() {
        return id == 0;
    }

    public interface Attachable {
        ColorFormat getFormat();

        int getId();

        int getTarget();

        boolean isMultiSample();

        int getWidth();

        int getHeight();

        void dispose();

        boolean isDisposed();
    }

    private static final class BackBuffer extends GLFrameBuffer {

        public BackBuffer() {
            super(null);
        }

        @Override
        public void attach(int attachment, Attachable attachable) {
            throw new UnsupportedOperationException("Cannot attach attachment to back buffer");
        }

        @Override
        public void reset() {
            // DO NOTHING
        }

        @Override
        public void copyFrom(FrameBuffer src, boolean copyColor, boolean copyDepth, boolean copyStencil, FilterMode filterMode) {
            Vector4i rect = new Vector4i(0, 0, src.getWidth(), src.getHeight());
            copyFrom(src, rect, rect, copyColor, copyDepth, copyStencil, filterMode);
        }
    }
}
