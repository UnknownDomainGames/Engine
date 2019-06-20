package unknowndomain.engine.client.gui.rendering;

import unknowndomain.engine.client.gui.Component;
import unknowndomain.engine.client.gui.misc.Pos;
import unknowndomain.engine.client.gui.text.Text;
import unknowndomain.engine.client.rendering.RenderContext;

public class TextRenderer implements ComponentRenderer {

    public static final TextRenderer INSTANCE = new TextRenderer();

    @Override
    public void render(Component component, Graphics graphics, RenderContext context) {
        Text text = (Text) component;
        graphics.setColor(text.color().getValue());
        graphics.setFont(text.font().getValue());

        Pos alignment = text.textAlignment().getValue();

        float x = 0, y = 0;

        switch (alignment.getHpos()) {
            case RIGHT:
                x = text.width().get() - text.prefWidth();
                break;
            case CENTER:
                x = (text.width().get() - text.prefWidth()) / 2;
                break;
            case LEFT:
                break;
        }

        switch (alignment.getVpos()) {
            case BOTTOM:
                y = text.height().get() - text.prefHeight();
                break;
            case CENTER:
                y = (text.height().get() - text.prefHeight()) / 2;
                break;
            case TOP:
                break;
        }

        graphics.drawText(text.text().getValue(), x, y);
    }
}
