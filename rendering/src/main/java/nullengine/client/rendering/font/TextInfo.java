package nullengine.client.rendering.font;

import nullengine.util.Color;

import java.util.Objects;

public class TextInfo {
    private CharSequence text;
    private Font font;
    private Color color;

    public TextInfo(CharSequence text, Font font, Color color){
        this.color = color;
        this.font = font;
        this.text = text;
    }

    public CharSequence getText() {
        return text;
    }

    public Font getFont() {
        return font;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextInfo textInfo = (TextInfo) o;
        return text.equals(textInfo.text) &&
                font.equals(textInfo.font) &&
                color.equals(textInfo.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, font, color);
    }
}
