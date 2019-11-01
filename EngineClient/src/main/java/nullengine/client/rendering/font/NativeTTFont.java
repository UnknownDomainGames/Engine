package nullengine.client.rendering.font;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NativeTTFont {

    private final NativeTTFontInfo parent;
    private final Font font;

    private final float scaleForPixelHeight;

    private final List<FontPlaneTexture> planeTextures;

    public NativeTTFont(NativeTTFontInfo parent, Font font, float scaleForPixelHeight, FontPlaneTexture... planes) {
        this.parent = parent;
        this.font = font;
        this.scaleForPixelHeight = scaleForPixelHeight;
        planeTextures = new ArrayList<>();
        planeTextures.addAll(Arrays.asList(planes));
    }

    public boolean isBlockLoaded(char c){
        return isBlockLoaded(Character.UnicodeBlock.of(c));
    }

    public boolean isBlockLoaded(Character.UnicodeBlock block){
        return planeTextures.stream().anyMatch(fontPlaneTexture -> fontPlaneTexture.isBlockInList(block));
    }

    public List<FontPlaneTexture> getPlaneTextures() {
        return planeTextures;
    }

    public NativeTTFontInfo getInfo() {
        return parent;
    }

    public Font getFont() {
        return font;
    }

    public float getScaleForPixelHeight() {
        return scaleForPixelHeight;
    }
}
