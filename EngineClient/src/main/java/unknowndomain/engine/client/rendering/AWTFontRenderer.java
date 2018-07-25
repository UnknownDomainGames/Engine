package unknowndomain.engine.client.rendering;

import org.lwjgl.opengl.GL11;

import unknowndomain.engine.client.resource.Texture2D;
import unknowndomain.engine.client.util.BufferBuilder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class AWTFontRenderer {

    private float SIDE_RATIO = 10.0f / 256.0f;

    private final Font font;
    private final String charset;
    private final HashMap<Character, CharInfo> charMap;
    private int height;
    private int width;


    private List<Texture2D> textures;
    private Texture2D texture;

    public AWTFontRenderer(Font font, String charset) {
        this.font = font;
        this.charset = charset;

        textures = new ArrayList<>();
        charMap = new HashMap<>();
        initTexture();
    }

    private void initTexture() {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2d = img.createGraphics();
        g2d.setFont(font);
        FontMetrics metrics = g2d.getFontMetrics();
        String allChars = getAvailableChars(charset);
        this.width = (int) Math.ceil(font.getSize() / SIDE_RATIO);
        this.height = (int) Math.ceil(font.getSize() / SIDE_RATIO);
        int maxheight = Math.max(metrics.getAscent(), metrics.getHeight());
        int i = 0;
        int j = 0;
        int k = 0;
        for (char c : allChars.toCharArray()) {
            // Get the size for each character and update global image size
            CharInfo charInfo = new CharInfo(i * font.getSize(), j * metrics.getHeight(), metrics.charWidth(c), metrics.getHeight(), k);
            charMap.put(c, charInfo);
            i++;
            if (i > 15) {
                j++;
                i = 0;
                if (j > 15) {
                    k++;
                    j = 0;
                }
            }
        }
        g2d.dispose();

        for (int k1 = 0; k1 <= k; k1++) {
            img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
            g2d = img.createGraphics();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setFont(font);
            metrics = g2d.getFontMetrics();
            g2d.setColor(Color.WHITE);
            outer:
            for (int j1 = 0; j1 < 16; j1++)
                for (int i1 = 0; i1 < 16; i1++){
                    if(k1 * 256 + j1 * 16 + i1 >= allChars.length())break outer;
                    g2d.drawString(String.valueOf(allChars.charAt(k1 * 256 + j1 * 16 + i1)), i1 * font.getSize(), metrics.getAscent() + j1 * metrics.getHeight());
                }
            g2d.dispose();
            textures.add(new Texture2D(img));
        }
    }

    private String getAvailableChars(String charset) {
        CharsetEncoder encoder = Charset.forName(charset).newEncoder();
        StringBuilder sb = new StringBuilder();
        for (char c = 0; c < Character.MAX_VALUE; c++) {
            if (encoder.canEncode(c)) {
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public void drawText(String text, float x, float y, int color) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        int lastpage = -1;
        float startX = 0;
        for (int i = 0; i < text.length(); i++) {
            CharInfo info = charMap.get(text.charAt(i));
            if (lastpage != info.getPage()) {
                textures.get(info.getPage()).useTexture();
                lastpage = info.getPage();
            }
            buffer.begin(GL11.GL_QUADS, true, true, true, false);
            buffer.pos(x + startX, y, 0)
                    .color(((color >> 16) & 255) / 255f, ((color >> 8) & 255) / 255f, (color & 255) / 255f, ((color >> 24) & 255) / 255f)
                    .tex(info.getStartX() / (float) width, info.getStartY() / (float)height).endVertex();
            buffer.pos(x + startX, y + info.getHeight(), 0)
                    .color(((color >> 16) & 255) / 255f, ((color >> 8) & 255) / 255f, (color & 255) / 255f, ((color >> 24) & 255) / 255f)
                    .tex(info.getStartX() / (float) width, (info.getStartY() + info.getHeight()) / (float)height).endVertex();
            buffer.pos(x + startX + info.getWidth(), y + info.getHeight(), 0)
                    .color(((color >> 16) & 255) / 255f, ((color >> 8) & 255) / 255f, (color & 255) / 255f, ((color >> 24) & 255) / 255f)
                    .tex((info.getStartX() + info.getWidth()) / (float) width, (info.getStartY() + info.getHeight()) / (float)height).endVertex();
            buffer.pos(x + startX + info.getWidth(), y, 0)
                    .color(((color >> 16) & 255) / 255f, ((color >> 8) & 255) / 255f, (color & 255) / 255f, ((color >> 24) & 255) / 255f)
                    .tex((info.getStartX() + info.getWidth()) / (float) width, info.getStartY() / (float)height).endVertex();

            tessellator.draw();
            startX += info.getWidth();
        }
    }


    public static class CharInfo {

        private final int startX;
        private final int startY;

        private final int width;
        private final int height;
        private final int page;

        public CharInfo(int startX, int startY, int width, int height, int page) {
            this.startX = startX;
            this.startY = startY;
            this.width = width;
            this.height = height;
            this.page = page;
        }

        public int getStartX() {
            return startX;
        }

        public int getStartY() {
            return startY;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }

        public int getPage() {
            return page;
        }
    }
}
