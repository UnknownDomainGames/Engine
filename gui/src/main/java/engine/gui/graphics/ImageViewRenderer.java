package engine.gui.graphics;

import engine.gui.image.Image;
import engine.gui.image.ImageView;

public class ImageViewRenderer implements ComponentRenderer<ImageView> {

    public static final ImageViewRenderer INSTANCE = new ImageViewRenderer();

    @Override
    public void render(ImageView component, Graphics graphics) {
        Image image = component.getImage();
        if (image == null) {
            return;
        }
        graphics.drawImage(image, 0, 0, image.getWidth(), image.getHeight());
    }
}
