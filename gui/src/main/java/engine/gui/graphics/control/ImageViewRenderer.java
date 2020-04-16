package engine.gui.graphics.control;

import engine.gui.graphics.Graphics;
import engine.gui.graphics.NodeRenderer;
import engine.gui.image.Image;
import engine.gui.image.ImageView;

public class ImageViewRenderer implements NodeRenderer<ImageView> {

    public static final ImageViewRenderer INSTANCE = new ImageViewRenderer();

    @Override
    public void render(ImageView node, Graphics graphics) {
        Image image = node.getImage();
        if (image == null) {
            return;
        }
        graphics.drawImage(image, 0, 0, image.getWidth(), image.getHeight());
    }
}
