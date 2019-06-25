package nullengine.client.rendering.util;

import org.lwjgl.opengl.GL11;

public enum GLDataType {
    BYTE(GL11.GL_BYTE, Byte.BYTES),
    UNSIGNED_BYTE(GL11.GL_UNSIGNED_BYTE, Byte.BYTES),
    SHORT(GL11.GL_SHORT, Short.BYTES),
    UNSIGNED_SHORT(GL11.GL_UNSIGNED_SHORT, Short.BYTES),
    INT(GL11.GL_INT, Integer.BYTES),
    UNSIGNED_INT(GL11.GL_UNSIGNED_INT, Integer.BYTES),
    FLOAT(GL11.GL_FLOAT, Float.BYTES),
    GL_2_BYTES(GL11.GL_2_BYTES, 2),
    GL_3_BYTES(GL11.GL_3_BYTES, 3),
    GL_4_BYTES(GL11.GL_4_BYTES, 4),
    DOUBLE(GL11.GL_DOUBLE, Double.BYTES);

    public final int glId;
    public final int bytes;

    GLDataType(int glId, int bytes) {
        this.glId = glId;
        this.bytes = bytes;
    }
}
