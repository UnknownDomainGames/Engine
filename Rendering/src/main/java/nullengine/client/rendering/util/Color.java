package nullengine.client.rendering.util;

import java.util.Objects;

public class Color {

    public static final Color WHITE = Color.fromRGB(0xffffff);
    public static final Color RED = Color.fromRGB(0xff0000);
    public static final Color GREEN = Color.fromRGB(0x00ff00);
    public static final Color BLUE = Color.fromRGB(0x0000ff);
    public static final Color BLACK = Color.fromRGB(0x000000);

    public static final Color TRANSPARENT = new Color(0f, 0f, 0f, 0f);

    public static Color fromRGB(String rgb) {
        return fromRGB(Integer.parseInt(rgb, 16));
    }

    public static Color fromRGB(int rgb) {
        return new Color((rgb >> 16) & 255, (rgb >> 8) & 255, rgb & 255);
    }

    public static Color fromARGB(String argb) {
        return fromARGB(Integer.parseInt(argb, 16));
    }

    public static Color fromARGB(int argb) {
        return new Color((argb >> 16) & 255, (argb >> 8) & 255, argb & 255, (argb >> 24) & 255);
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

    public Color invert() {
        return difference(Color.WHITE);
    }

    public Color difference(Color color) {
        return new Color(Math.abs(color.red - this.red) * color.alpha + red * (1 - color.alpha), Math.abs(color.green - this.green) * color.alpha + color.green * (1 - color.alpha), Math.abs(color.blue - this.blue) * color.alpha + blue * (1 - color.alpha), alpha);
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
