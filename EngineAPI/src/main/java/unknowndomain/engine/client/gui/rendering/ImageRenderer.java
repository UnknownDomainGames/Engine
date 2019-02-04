package unknowndomain.engine.client.gui.rendering;

import unknowndomain.engine.client.gui.Component;
import unknowndomain.engine.client.gui.component.Image;

public class ImageRenderer implements ComponentRenderer {

    public static final ImageRenderer INSTANCE = new ImageRenderer();

    @Override
    public void render(Component component, Graphics graphics) {
        Image img = (Image) component;
        if(img.getCachedTexture() != null)
            graphics.drawTexture(img.getCachedTexture(), 0,0,img.prefWidth(),img.prefHeight());
    }
}
