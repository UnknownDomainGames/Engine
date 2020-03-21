package engine.graphics.lwjgl.font;

import engine.graphics.font.Font;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class TTFont {

    private final TTFontInfo fontInfo;
    private final Font font;

    private final float scaleForPixelHeight;

    private final List<FontPlaneTexture> planeTextures;

    public TTFont(TTFontInfo fontInfo, Font font, float scaleForPixelHeight, FontPlaneTexture... planes) {
        this.fontInfo = fontInfo;
        this.font = font;
        this.scaleForPixelHeight = scaleForPixelHeight;
        planeTextures = new ArrayList<>();
        planeTextures.addAll(Arrays.asList(planes));
    }

    public boolean isBlockLoaded(char c) {
        return isBlockLoaded(Character.UnicodeBlock.of(c));
    }

    public boolean isBlockLoaded(Character.UnicodeBlock block) {
        return planeTextures.stream().anyMatch(fontPlaneTexture -> fontPlaneTexture.isBlockInList(block));
    }

    public List<FontPlaneTexture> getPlaneTextures() {
        return planeTextures;
    }

    public TTFontInfo getFontInfo() {
        return fontInfo;
    }

    public Font getFont() {
        return font;
    }

    public float getScaleForPixelHeight() {
        return scaleForPixelHeight;
    }
}
