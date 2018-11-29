package unknowndomain.engine.client.rendering.gui.font;

import org.joml.Vector2f;
import org.lwjgl.opengl.GL11;
import unknowndomain.engine.client.rendering.gui.Tessellator;
import unknowndomain.engine.client.rendering.util.BufferBuilder;
import unknowndomain.engine.client.util.UDBitmapFont;

import java.io.File;

public class UDBitmapFontRenderer implements FontRenderer {
    private final UDBitmapFont font;

    public UDBitmapFontRenderer(File file) {
        font = UDBitmapFont.readFont(file);
    }

    public UDBitmapFont getFont() {
        return font;
    }

    /**
     * @param text
     * @param x
     * @param y     y-coordinate of text, where baseline is located
     * @param color
     */
    public void drawText(CharSequence text, float x, float y, int color) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        float startX = 0;
        for (int i = 0, size = text.length(); i < size ; i++) {
            char c = text.charAt(i);
            UDBitmapFont.CharInfo info = font.getFontData().get(c);
            if (info == null) {
                info = font.getFontData().get(' ');
                if (info == null)
                    continue;
            }
            buffer.begin(GL11.GL_QUADS, true, true, true, false);
            float r = ((color >> 16) & 255) / 255f;
            float g = ((color >> 8) & 255) / 255f;
            float b = (color & 255) / 255f;
            float a = ((color >> 24) & 255) / 255f;
            buffer.pos(x + startX, y - info.getBaseline(), 0).color(r, g, b, a)
                    .tex(info.getPosX() / (float) font.getWidth(), info.getPosX() / (float) font.getWidth())
                    .endVertex();
            buffer.pos(x + startX, y - info.getBaseline() + info.getHeight(), 0).color(r, g, b, a)
                    .tex(info.getPosX() / (float) font.getWidth(),
                            (info.getPosY() + info.getHeight()) / (float) font.getHeight())
                    .endVertex();
            buffer.pos(x + startX + info.getWidth(), y - info.getBaseline() + info.getHeight(), 0).color(r, g, b, a)
                    .tex((info.getPosX() + info.getWidth()) / (float) font.getWidth(),
                            (info.getPosY() + info.getHeight()) / (float) font.getHeight())
                    .endVertex();
            buffer.pos(x + startX + info.getWidth(), y - info.getBaseline(), 0).color(r, g, b, a)
                    .tex((info.getPosX() + info.getWidth()) / (float) font.getWidth(),
                            info.getPosY() / (float) font.getHeight())
                    .endVertex();

            tessellator.draw();
            startX += info.getWidth();
        }
    }

    @Override
    public Vector2f sizeText(CharSequence text) {
        return null;
    }
}
