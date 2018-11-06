package unknowndomain.engine.client.rendering.gui.font;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBTTBakedChar;
import org.lwjgl.stb.STBTTFontinfo;
import org.lwjgl.system.MemoryStack;
import unknowndomain.engine.client.gui.text.Font;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL11.glBindTexture;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.memUTF16;

public final class FontHelper {

    private final BiMap<Font, InternalFont> internalFontMap = HashBiMap.create();

    public static final int SUPPORTING_CHARACTER_COUNT = 256;

    public Font loadFont(InputStream inputStream, float size) throws IOException {
        byte[] bytes = IOUtils.toByteArray(inputStream);
        return loadFont((ByteBuffer) ByteBuffer.allocateDirect(bytes.length).put(bytes).flip(), size);
    }

    public Font loadFont(ByteBuffer buffer, float size) {
        STBTTFontinfo fontInfo = STBTTFontinfo.create();
        if (!stbtt_InitFont(fontInfo, buffer)) {
            throw new IllegalStateException("Failed in initializing ttf font info");
        }

        String family = memUTF16(stbtt_GetFontNameString(fontInfo, STBTT_PLATFORM_ID_MICROSOFT, STBTT_MS_EID_UNICODE_BMP, STBTT_MS_LANG_ENGLISH, 1)
                .order(ByteOrder.BIG_ENDIAN));
        String style = memUTF16(stbtt_GetFontNameString(fontInfo, STBTT_PLATFORM_ID_MICROSOFT, STBTT_MS_EID_UNICODE_BMP, STBTT_MS_LANG_ENGLISH, 2)
                .order(ByteOrder.BIG_ENDIAN));

        Font font = new Font(family, style, size);

        try (MemoryStack stack = stackPush()) {
            IntBuffer pAscent = stack.mallocInt(1);
            IntBuffer pDescent = stack.mallocInt(1);
            IntBuffer pLineGap = stack.mallocInt(1);

            stbtt_GetFontVMetrics(fontInfo, pAscent, pDescent, pLineGap);

            FloatBuffer p1 = stack.mallocFloat(1);
            FloatBuffer p2 = stack.mallocFloat(1);

            GLFW.glfwGetMonitorContentScale(GLFW.glfwGetPrimaryMonitor(), p1, p2);

            int textureId = GL11.glGenTextures();
            STBTTBakedChar.Buffer charBuffer = STBTTBakedChar.malloc(SUPPORTING_CHARACTER_COUNT);
            int side = getBitmapSide(size, SUPPORTING_CHARACTER_COUNT);
            ByteBuffer bitmap = BufferUtils.createByteBuffer(side * side);
            stbtt_BakeFontBitmap(buffer, size, bitmap, side, side, 0, charBuffer);

            glBindTexture(GL_TEXTURE_2D, textureId);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RED, side, side, 0, GL_RED, GL_UNSIGNED_BYTE, bitmap);
            glBindTexture(GL_TEXTURE_2D, 0);

            InternalFont internalFont = new InternalFont(fontInfo, pAscent.get(0), pDescent.get(0), pLineGap.get(0), p1.get(0), p2.get(0), textureId, charBuffer);
            internalFontMap.forcePut(font, internalFont);
        }
        return font;
    }

    private int getBitmapSide(float size, int countOfChar) {
        return (int) Math.ceil((size + 2 * size / 16.0f) * Math.sqrt(countOfChar));
    }

    public InternalFont getInternalFont(Font font) {
        return internalFontMap.get(font);
    }
}
