package unknowndomain.engine.client.rendering.util;

import org.joml.Vector4ic;
import unknowndomain.engine.client.rendering.Tessellator;
import unknowndomain.engine.client.rendering.util.buffer.GLBuffer;
import unknowndomain.engine.client.rendering.util.buffer.GLBufferFormats;
import unknowndomain.engine.client.rendering.util.buffer.GLBufferMode;

import static org.lwjgl.opengl.GL11.*;

/**
 * A FrameBuffer Wrapper for default frame buffer. i.e. main display
 */
public final class DefaultFBOWrapper extends FrameBuffer {

    @Override
    public int getFboId() {
        return 0;
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void createFrameBuffer() {
    }

    @Override
    public void deleteFrameBuffer() {

    }

    @Override
    public void check() {

    }

    public void drawFrameBuffer(FrameBuffer frameBuffer){
        bind();
        bindDrawOnly();
        bindReadOnly();
        glDisable(GL_DEPTH_TEST);
        glBindTexture(GL_TEXTURE_2D, frameBuffer.getTexId());
        Tessellator t = Tessellator.getInstance();
        GLBuffer bb = t.getBuffer();
        bb.begin(GLBufferMode.CONTINUOUS_TRIANGLES, GLBufferFormats.POSITION_TEXTURE);
        bb.pos(-1.0f,1.0f,0).uv(0,1.0f).endVertex();
        bb.pos(-1.0f,-1.0f,0).uv(0,0).endVertex();
        bb.pos(1.0f,1.0f,0).uv(1.0f,1.0f).endVertex();
        bb.pos(1.0f,-1.0f,0).uv(1.0f,0).endVertex();
        t.draw();
    }

    @Override
    public void blitFrom(FrameBuffer source, Vector4ic sourceRect, Vector4ic destRect) {
        super.blitFrom(source, sourceRect, sourceRect);
    }
}
