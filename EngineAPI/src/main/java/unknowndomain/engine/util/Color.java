package unknowndomain.engine.util;

import java.util.Objects;

public class Color {

    public static final Color WHITE = Color.fromRGB(0xffffff);
    public static final Color BLACK = Color.fromRGB(0x000000);

    public static final Color TRANSPARENT = Color.fromRGBA(0x00000000);

    public static Color fromRGB(String rgb) {
        return fromRGB(Integer.parseInt(rgb, 16));
    }

    public static Color fromRGB(int rgb) {
        return new Color((rgb >> 16) & 255, (rgb >> 8) & 255, rgb & 255);
    }

    public static Color fromRGBA(String rgba) {
        return fromRGBA(Integer.parseInt(rgba, 16));
    }

    public static Color fromRGBA(int rgba) {
        return new Color((rgba >> 16) & 255, (rgba >> 8) & 255, rgba & 255, (rgba >> 24) & 255);
    }

    private final float red, green, blue, alpha;

    public Color(int red, int green, int blue) {
        this(red / 255f, green / 255f, blue / 255f, 1f);
    }

    public Color(float red, float green, float blue) {
        this(red, green, blue, 1f);
    }

    public Color(int red, int green, int blue, int alpha) {
        this(red / 255f, green / 255f, blue / 255f, alpha / 255f);
    }

    public Color(float red, float green, float blue, float alpha) {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }

    public float getRed() {
        return red;
    }

    public float getGreen() {
        return green;
    }

    public float getBlue() {
        return blue;
    }

    public float getAlpha() {
        return alpha;
    }

    public int getRedAsInt() {
        return (int) (red * 255);
    }

    public int getGreenAsInt() {
        return (int) (green * 255);
    }

    public int getBlueAsInt() {
        return (int) (blue * 255);
    }

    public int getAlphaAsInt() {
        return (int) (alpha * 255);
    }

    public int toRGB() {
        return (getRedAsInt() << 16) | (getGreenAsInt() << 8) | getBlueAsInt();
    }

    public int toRGBA() {
        return (getAlphaAsInt() << 24) | toRGB();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Color color = (Color) o;
        return Float.compare(color.red, red) == 0 &&
                Float.compare(color.green, green) == 0 &&
                Float.compare(color.blue, blue) == 0 &&
                Float.compare(color.alpha, alpha) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(red, green, blue, alpha);
    }

    @Override
    public String toString() {
        return "Color{" +
                "red=" + red +
                ", green=" + green +
                ", blue=" + blue +
                ", alpha=" + alpha +
                '}';
    }
}
