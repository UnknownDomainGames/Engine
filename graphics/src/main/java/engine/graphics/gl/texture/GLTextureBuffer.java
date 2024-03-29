package engine.graphics.gl.texture;

import engine.graphics.gl.buffer.GLBufferType;
import engine.graphics.gl.buffer.GLVertexBuffer;
import engine.graphics.gl.util.GLHelper;
import org.lwjgl.opengl.GL31C;
import org.lwjgl.opengl.GL45C;

public class GLTextureBuffer extends GLTexture {
    public GLTextureBuffer(GLColorFormat format, GLVertexBuffer texBuf) {
        super(GL31C.GL_TEXTURE_BUFFER, format);
        if (texBuf.getType() != GLBufferType.TEXTURE_BUFFER) {
            throw new IllegalArgumentException("incoming buffer is not texture buffer");
        }
        if (GLHelper.isSupportARBDirectStateAccess()) {
            GL45C.glTextureBuffer(id, format.internalFormat, texBuf.getId());
        } else {
            bind();
            GL31C.glTexBuffer(GL31C.GL_TEXTURE_BUFFER, format.internalFormat, texBuf.getId());
        }
    }
}
