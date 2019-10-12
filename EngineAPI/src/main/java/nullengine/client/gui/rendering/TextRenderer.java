package nullengine.client.gui.rendering;

import nullengine.client.gui.misc.Pos;
import nullengine.client.gui.text.Text;
import nullengine.client.rendering.RenderManager;
import nullengine.client.rendering.font.FontHelper;

import java.util.stream.Collectors;

public class TextRenderer implements ComponentRenderer<Text> {

    public static final TextRenderer INSTANCE = new TextRenderer();

    @Override
    public void render(Text text, Graphics graphics, RenderManager context) {
        graphics.setColor(text.color().getValue());
        graphics.setFont(text.font().getValue());

        Pos alignment = text.textAlignment().getValue();

        float x = 0, y = 0;
        var lines = text.text().getValue().lines();
        var lineHeight = FontHelper.instance().computeTextHeight(text.text().getValue(), text.font().getValue());
        for (String line : lines.collect(Collectors.toList())) {
            var lineWidth = FontHelper.instance().computeTextWidth(line, text.font().getValue());
            switch (alignment.getHpos()) {
                case RIGHT:
                    x = text.width().get() - lineWidth;
                    break;
                case CENTER:
                    x = (text.width().get() - lineWidth) / 2;
                    break;
                case LEFT:
                    break;
            }
            var y1 = y + (lineHeight * text.leading().getFloat() - lineHeight) / 2;

            graphics.pushClipRect(x, y1, text.width().get(), text.height().get());
            graphics.drawText(line, x, y1);
            graphics.popClipRect();
            y += lineHeight * text.leading().getFloat();
        }
/*        switch (alignment.getHpos()) {
            case RIGHT:
                x = text.width().get() - text.prefWidth();
                break;
            case CENTER:
                x = (text.width().get() - text.prefWidth()) / 2;
                break;
            case LEFT:
                break;
        }*/

/*        switch (alignment.getVpos()) {
            case BOTTOM:
                y = text.height().get() - text.prefHeight();
                break;
            case CENTER:
                y = (text.height().get() - text.prefHeight()) / 2;
                break;
            case TOP:
                break;
        }*/

    }
}
