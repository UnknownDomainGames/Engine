package com.github.unknownstudio.unknowndomain.engine.client.rendering;

import com.github.unknownstudio.unknowndomain.engine.client.util.BufferBuilder;
import com.github.unknownstudio.unknowndomain.engine.client.util.UDBitmapFont;
import org.lwjgl.opengl.GL11;

import java.io.File;

public class UDBitmapFontRenderer {
    private final UDBitmapFont font;

    public UDBitmapFontRenderer(File file){
        font = UDBitmapFont.readFont(file);
    }

    public UDBitmapFont getFont() {
        return font;
    }

    /**
     *
     * @param text
     * @param x
     * @param y y-coordinate of text, where baseline is located
     * @param color
     */
    public void drawText(String text, float x, float y, int color) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        float startX = 0;
        for (char c:text.toCharArray()) {
            UDBitmapFont.CharInfo info = font.getFontData().get(c);
            if (info == null) {
                info = font.getFontData().get(' ');
                if(info == null)continue;
            }
            buffer.begin(GL11.GL_QUADS, true, true, true);
            float r = ((color >> 16) & 255) / 255f;
            float g = ((color >> 8) & 255) / 255f;
            float b = (color & 255) / 255f;
            float a = ((color >> 24) & 255) / 255f;
            buffer.pos(x + startX, y - info.getBaseline(), 0)
                    .color(r, g, b, a)
                    .tex(info.getPosX() / (float) font.getWidth(), info.getPosX() / (float)font.getWidth()).endVertex();
            buffer.pos(x + startX, y - info.getBaseline() + info.getHeight(), 0)
                    .color(r, g, b, a)
                    .tex(info.getPosX() / (float) font.getWidth(), (info.getPosY() + info.getHeight()) / (float)font.getHeight()).endVertex();
            buffer.pos(x + startX + info.getWidth(), y - info.getBaseline() + info.getHeight(), 0)
                    .color(r, g, b, a)
                    .tex((info.getPosX() + info.getWidth()) / (float) font.getWidth(), (info.getPosY() + info.getHeight()) / (float)font.getHeight()).endVertex();
            buffer.pos(x + startX + info.getWidth(), y - info.getBaseline(), 0)
                    .color(r, g, b, a)
                    .tex((info.getPosX() + info.getWidth()) / (float) font.getWidth(), info.getPosY() / (float)font.getHeight()).endVertex();

            tessellator.draw();
            startX += info.getWidth();
        }
    }
}
