package engine.graphics.font;

import java.util.Locale;

import static org.apache.commons.lang3.Validate.notEmpty;

public class Font {

    public static final String REGULAR = "Regular";
    public static final String BOLD = "Bold";
    public static final String ITALIC = "Italic";
    public static final String BOLD_ITALIC = "Bold Italic";
    public static final String LIGHT = "Light";

    private final String family;
    private final String style;
    private final String fullName;
    private final float size;

    private int hash = 0;

    public static Font getDefaultFont() {
        return FontManager.instance().getDefaultFont();
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

    public String getFamily(Locale locale) {
        return FontManager.instance().getFontFamily(this, locale);
    }

    public String getStyle() {
        return style;
    }

    public String getName() {
        return fullName;
    }

    public String getFullName() {
        return getFullName(Locale.getDefault());
    }

    public String getFullName(Locale locale) {
        return FontManager.instance().getFontName(this, locale);
    }

    public float getSize() {
        return size;
    }

    public Font withSize(float size) {
        return new Font(family, style, size);
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
                ", size=" + size +
                '}';
    }
}
