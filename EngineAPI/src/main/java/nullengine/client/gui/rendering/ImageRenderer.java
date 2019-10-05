package nullengine.client.gui.rendering;

import nullengine.client.gui.Component;
import nullengine.client.gui.component.ImageView;
import nullengine.client.rendering.RenderManager;

public class ImageRenderer implements ComponentRenderer {

    public static final ImageRenderer INSTANCE = new ImageRenderer();

    @Override
    public void render(Component component, Graphics graphics, RenderManager context) {
        ImageView img = (ImageView) component;
        if (img.getCachedTexture() != null)
            graphics.drawTexture(img.getCachedTexture(), 0, 0, img.prefWidth(), img.prefHeight());
    }
}
