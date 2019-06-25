package nullengine.client.gui.rendering;

import nullengine.client.gui.Component;
import nullengine.client.gui.component.TextureImg;
import nullengine.client.rendering.RenderContext;

public class ImageRenderer implements ComponentRenderer {

    public static final ImageRenderer INSTANCE = new ImageRenderer();

    @Override
    public void render(Component component, Graphics graphics, RenderContext context) {
        TextureImg img = (TextureImg) component;
        if (img.getCachedTexture() != null)
            graphics.drawTexture(img.getCachedTexture(), 0, 0, img.prefWidth(), img.prefHeight());
    }
}
