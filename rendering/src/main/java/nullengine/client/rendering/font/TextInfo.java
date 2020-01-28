package nullengine.client.rendering.font;

import java.util.Objects;

public class TextInfo {
    private CharSequence text;
    private Font font;

    public TextInfo(CharSequence text, Font font){
        this.font = font;
        this.text = text;
    }

    public CharSequence getText() {
        return text;
    }

    public Font getFont() {
        return font;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TextInfo textInfo = (TextInfo) o;
        return text.equals(textInfo.text) &&
                font.equals(textInfo.font);
    }

    @Override
    public int hashCode() {
        return Objects.hash(text, font);
    }
}
