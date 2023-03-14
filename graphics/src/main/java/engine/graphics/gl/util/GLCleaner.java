package engine.graphics.gl.util;

import engine.graphics.util.Cleaner;
import org.lwjgl.opengl.*;

public class GLCleaner {
    public static Cleaner.Cleanable registerTexture(Object obj, int textureId) {
        return Cleaner.register(obj, () -> GL11C.glDeleteTextures(textureId));
    }

    public static Cleaner.Cleanable registerBuffer(Object obj, int bufferId) {
        return Cleaner.register(obj, () -> GL15C.glDeleteBuffers(bufferId));
    }

    public static Cleaner.Cleanable registerFrameBuffer(Object obj, int frameBufferId) {
        return Cleaner.register(obj, () -> GL30C.glDeleteFramebuffers(frameBufferId));
    }

    public static Cleaner.Cleanable registerRenderBuffer(Object obj, int renderBufferId) {
        return Cleaner.register(obj, () -> GL30C.glDeleteRenderbuffers(renderBufferId));
    }

    public static Cleaner.Cleanable registerVertexArray(Object obj, int vertexArrayId) {
        return Cleaner.register(obj, () -> GL30C.glDeleteVertexArrays(vertexArrayId));
    }

    public static Cleaner.Cleanable registerProgram(Object obj, int programId) {
        return Cleaner.register(obj, () -> GL20C.glDeleteProgram(programId));
    }

    public static Cleaner.Cleanable registerSampler(Object obj, int samplerId) {
        return Cleaner.register(obj, () -> GL33C.glDeleteSamplers(samplerId));
    }
}
