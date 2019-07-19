package nullengine.client.rendering.font;

import static org.apache.commons.lang3.Validate.notEmpty;

public class Font {

    private final String family;
    private final String style;
    private final String fullName;
    private final float size;

    private int hash = 0;

    public static Font getDefaultFont() {
        return FontHelper.instance().getDefaultFont();
    }

    public Font(String family, String style, float size) {
        this.family = notEmpty(family);
        this.style = notEmpty(style);
        this.fullName = family + " " + style;
        this.size = size;
    }

    public Font(Font font, float size) {
        this.family = font.family;
        this.style = font.style;
        this.fullName = font.fullName;
        this.size = size;
    }

    public String getFamily() {
        return family;
    }

    public String getStyle() {
        return style;
    }

    public String getFullName() {
        return fullName;
    }

    public float getSize() {
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Font font = (Font) o;

        if (Float.compare(font.size, size) != 0) return false;
        return fullName.equals(font.fullName);
    }

    @Override
    public int hashCode() {
        int h = hash;
        if (h == 0) {
            hash = h = 31 * fullName.hashCode() + (size != +0.0f ? Float.floatToIntBits(size) : 0);
        }
        return h;
    }

    @Override
    public String toString() {
        return "Font{" +
                "family='" + family + '\'' +
                ", style='" + style + '\'' +
                ", getSize=" + size +
                '}';
    }
}
