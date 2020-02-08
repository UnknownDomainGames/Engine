package engine.graphics.gl.util;

import engine.graphics.util.Cleaner;

import static org.lwjgl.opengl.GL11.glDeleteTextures;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL30.glDeleteFramebuffers;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;

public class GLCleaner {
    public static Cleaner.Disposable registerTexture(Object obj, int textureId) {
        return Cleaner.register(obj, () -> glDeleteTextures(textureId));
    }

    public static Cleaner.Disposable registerBuffer(Object obj, int bufferId) {
        return Cleaner.register(obj, () -> glDeleteBuffers(bufferId));
    }

    public static Cleaner.Disposable registerFrameBuffer(Object obj, int frameBufferId) {
        return Cleaner.register(obj, () -> glDeleteFramebuffers(frameBufferId));
    }

    public static Cleaner.Disposable registerVertexArray(Object obj, int vertexArrayId) {
        return Cleaner.register(obj, () -> glDeleteVertexArrays(vertexArrayId));
    }

    public static Cleaner.Disposable registerProgram(Object obj, int programId) {
        return Cleaner.register(obj, () -> glDeleteProgram(programId));
    }
}
