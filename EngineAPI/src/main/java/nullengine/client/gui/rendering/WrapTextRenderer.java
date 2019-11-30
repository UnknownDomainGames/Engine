package nullengine.client.gui.rendering;

import com.google.common.base.Strings;
import nullengine.client.gui.misc.Pos;
import nullengine.client.gui.text.WrapText;
import nullengine.client.rendering.font.FontHelper;

import java.util.stream.Collectors;

public class WrapTextRenderer implements ComponentRenderer<WrapText> {

    public static final WrapTextRenderer INSTANCE = new WrapTextRenderer();

    @Override
    public void render(WrapText text, Graphics graphics) {
        if(Strings.isNullOrEmpty(text.text().getValue()))
            return;
        graphics.setColor(text.color().getValue());
        graphics.setFont(text.font().getValue());

        Pos alignment = text.textAlignment().getValue();

        float x = 0, y = 0;
        var lines = text.text().getValue().lines().flatMap(str->FontHelper.instance().wrapText(str, text.textWidth().get(), text.font().getValue()).stream()).collect(Collectors.toList());
        var lineHeight = FontHelper.instance().computeTextHeight(text.text().getValue(), text.font().getValue(), text.textWidth().get()) / lines.size();
        for (String line : lines) {
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
    }
}
