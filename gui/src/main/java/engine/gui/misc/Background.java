package engine.gui.misc;

import engine.gui.image.Image;
import engine.util.Color;

import java.util.Objects;

public class Background {
    public static final Background NOTHING = Background.fromColor(Color.TRANSPARENT);

    private final Color color;
    private final Image image;

    private boolean repeat;

    public Background(Color color) {
        this.color = color;
        this.image = null;
    }

    public Background(Image image) {
        this.image = image;
        this.color = Color.TRANSPARENT;
    }

    public Color getColor() {
        return color;
    }

    public Image getImage() {
        return image;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    @Override
    public int hashCode() {
        return Objects.hash(color, image);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Background) {
            if (this == obj) {
                return true;
            }
            var other = (Background) obj;
            return other.image == image && other.color == color && other.repeat == repeat;
        }
        return false;
    }

    public static Background fromColor(Color color) {
        return new Background(color);
    }

    public static Background fromImage(Image image) {
        return new Background(image);
    }
}
