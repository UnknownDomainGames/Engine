package nullengine.client.gui.misc;

import nullengine.client.gui.Component;
import nullengine.client.gui.rendering.Graphics;
import nullengine.client.rendering.gl.texture.GLTexture2D;
import nullengine.client.rendering.texture.Texture2D;
import nullengine.util.Color;

import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;

public class Background {

    public static final Background NOTHING = Background.fromColor(Color.TRANSPARENT);

    private final Color color;

    private final Texture2D image;

    private boolean repeat;

    public Background(Color color) {
        this.color = color;
        this.image = null;
    }

    public Background(Texture2D image) {
        this.image = image;
        this.color = Color.TRANSPARENT;
    }

    public Color getColor() {
        return color;
    }

    public Texture2D getImage() {
        return image;
    }

    public boolean isRepeat() {
        return repeat;
    }

    public void setRepeat(boolean repeat) {
        this.repeat = repeat;
    }

    public void render(Component component, Graphics graphics) {
        if (image != null) {
            image.bind();
            if (repeat) {
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
            } else {
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
                glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
            }
            graphics.drawTexture(image, 0, 0, component.width().get(), component.height().get());
        } else {
            graphics.setColor(color);
            graphics.fillRect(0, 0, component.width().get(), component.height().get());
        }
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

    public static Background fromImage(GLTexture2D image) {
        return new Background(image);
    }
}
