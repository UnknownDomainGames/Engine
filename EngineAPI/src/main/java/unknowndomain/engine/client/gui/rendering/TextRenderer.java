package unknowndomain.engine.client.gui.rendering;

import unknowndomain.engine.client.gui.Component;
import unknowndomain.engine.client.gui.text.Text;
import unknowndomain.engine.client.gui.text.TextAlignment;

public class TextRenderer implements ComponentRenderer {

    public static final TextRenderer INSTANCE = new TextRenderer();

    @Override
    public void render(Component component, Graphics graphics) {
        Text text = (Text) component;
        graphics.setColor(text.color().getValue());
        graphics.setFont(text.font().getValue());
        if(text.textAlignment().getValue() == TextAlignment.RIGHT){
            graphics.drawText(text.text().getValue(), text.width().get() - text.prefWidth(), 0);
        }
        else if(text.textAlignment().getValue() == TextAlignment.CENTER){
            graphics.drawText(text.text().getValue(), (text.width().get() - text.prefWidth()) / 2, 0);
        }else {
            graphics.drawText(text.text().getValue(), 0, 0);
        }
    }
}
