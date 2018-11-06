package unknowndomain.engine.client.gui.text;

public class Font {

    private final String family;
    private final String style;
    private final String name;
    private final float size;

    public Font(String family, String style, float size) {
        this.family = family;
        this.style = style;
        this.name = family + " " + style;
        this.size = size;
    }

    public String getFamily() {
        return family;
    }

    public String getStyle() {
        return style;
    }

    public String getFullName() {
        return name;
    }

    public float getSize() {
        return size;
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
