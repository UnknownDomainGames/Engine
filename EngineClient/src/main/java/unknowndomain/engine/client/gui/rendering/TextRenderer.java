package unknowndomain.engine.client.gui.rendering;

import unknowndomain.engine.client.gui.Component;
import unknowndomain.engine.client.gui.text.Text;

public class TextRenderer implements ComponentRenderer {

    public static final TextRenderer INSTANCE = new TextRenderer();

    @Override
    public void render(Component component, Graphics graphics) {
        Text text = (Text) component;
        graphics.setColor(text.color().getValue());
        graphics.setFont(text.font().getValue());
        graphics.drawText(text.text().getValue(), 0, 0);
    }
}
