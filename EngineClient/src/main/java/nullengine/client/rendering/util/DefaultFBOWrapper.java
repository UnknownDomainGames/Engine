package nullengine.client.rendering.util;

import nullengine.client.rendering.Tessellator;
import nullengine.client.rendering.gl.buffer.GLBuffer;
import nullengine.client.rendering.gl.buffer.GLBufferFormats;
import nullengine.client.rendering.gl.buffer.GLBufferMode;
import org.joml.Vector4ic;

import static org.lwjgl.opengl.GL11.*;

/**
 * A FrameBuffer Wrapper for default frame buffer. i.e. main display
 */
public final class DefaultFBOWrapper extends FrameBuffer {

    @Override
    public int getFboPointer() {
        return 0;
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void createFrameBuffer() {
    }

    @Override
    public void check() {

    }

    public void drawFrameBuffer(FrameBuffer frameBuffer) {
        bind();
        bindDrawOnly();
        bindReadOnly();
        glDisable(GL_DEPTH_TEST);
        glBindTexture(GL_TEXTURE_2D, frameBuffer.getTexPointer());
        Tessellator t = Tessellator.getInstance();
        GLBuffer bb = t.getBuffer();
        bb.begin(GLBufferMode.CONTINUOUS_TRIANGLES, GLBufferFormats.POSITION_TEXTURE);
        bb.pos(-1.0f, 1.0f, 0).uv(0, 1.0f).endVertex();
        bb.pos(-1.0f, -1.0f, 0).uv(0, 0).endVertex();
        bb.pos(1.0f, 1.0f, 0).uv(1.0f, 1.0f).endVertex();
        bb.pos(1.0f, -1.0f, 0).uv(1.0f, 0).endVertex();
        t.draw();
    }

    @Override
    public void blitFrom(FrameBuffer source, Vector4ic sourceRect, Vector4ic destRect) {
        super.blitFrom(source, sourceRect, sourceRect);
    }
}
