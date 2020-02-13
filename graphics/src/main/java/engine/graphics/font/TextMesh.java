package engine.graphics.font;

import engine.graphics.texture.Texture2D;
import engine.graphics.vertex.VertexDataBuf;
import engine.util.Color;
import org.joml.Vector4fc;

public class TextMesh {

    private final CharSequence text;
    private final Font font;
    private final Texture2D texture;
    private final CharQuad[] quads;

    public TextMesh(CharSequence text, Font font, Texture2D texture, CharQuad[] quads) {
        this.text = text;
        this.font = font;
        this.texture = texture;
        this.quads = quads;
    }

    public CharSequence getText() {
        return text;
    }

    public Font getFont() {
        return font;
    }

    public Texture2D getTexture() {
        return texture;
    }

    public int length() {
        return quads.length;
    }

    public float getWidth(int beginIndex, int endIndex) {
        if (beginIndex >= endIndex) return 0;
        if (endIndex == quads.length) return quads[endIndex - 1].x1 - quads[beginIndex].x0;
        return quads[endIndex].x0 - quads[beginIndex].x0;
    }

    public float getHeight(int beginIndex, int endIndex) {
        float height = 0;
        for (int i = beginIndex; i < endIndex; i++) {
            float quadHeight = quads[i].getHeight();
            if (quadHeight > height) height = quadHeight;
        }
        return height;
    }

    public void put(VertexDataBuf buf, Color color) {
        put(buf, color, 0, quads.length);
    }

    public void put(VertexDataBuf buf, Color color, int beginIndex, int endIndex) {
        for (int i = beginIndex; i < endIndex; i++) {
            quads[i].put(buf, color);
        }
    }

    public static final class CharQuad {
        private final char character;
        private final float x0, y0, x1, y1;
        private final Vector4fc texCoord;

        public CharQuad(char character, float x0, float y0, float x1, float y1, Vector4fc texCoord) {
            this.character = character;
            this.x0 = x0;
            this.y0 = y0;
            this.x1 = x1;
            this.y1 = y1;
            this.texCoord = texCoord;
        }

        public char getCharacter() {
            return character;
        }

        public float getWidth() {
            return x1 - x0;
        }

        public float getHeight() {
            return y1 - y0;
        }

        public void put(VertexDataBuf buf, Color color) {
            buf.pos(x0, y0, 0).color(color).tex(texCoord.x(), texCoord.y()).endVertex();
            buf.pos(x0, y1, 0).color(color).tex(texCoord.x(), texCoord.w()).endVertex();
            buf.pos(x1, y0, 0).color(color).tex(texCoord.z(), texCoord.y()).endVertex();

            buf.pos(x1, y0, 0).color(color).tex(texCoord.z(), texCoord.y()).endVertex();
            buf.pos(x0, y1, 0).color(color).tex(texCoord.x(), texCoord.w()).endVertex();
            buf.pos(x1, y1, 0).color(color).tex(texCoord.z(), texCoord.w()).endVertex();
        }
    }
}
