package unknowndomain.engine.client.gui.rendering;

import unknowndomain.engine.client.gui.component.TextField;
import unknowndomain.engine.client.gui.internal.FontHelper;
import unknowndomain.engine.client.gui.internal.Internal;
import unknowndomain.engine.client.gui.text.Font;
import unknowndomain.engine.client.rendering.RenderContext;
import unknowndomain.engine.util.Color;

public class TextFieldRenderer extends RegionRenderer<TextField> {

    public static final TextFieldRenderer INSTANCE = new TextFieldRenderer();

    @Override
    public void render(TextField textField, Graphics graphics, RenderContext context) {
        super.render(textField, graphics, context);
        float px, py, pw, ph;
        if (textField.padding().isPresent()) {
            px = textField.padding().getValue().getLeft();
            py = textField.padding().getValue().getTop();
            pw = textField.width().get() - textField.padding().getValue().getRight();
            ph = textField.height().get() - textField.padding().getValue().getBottom();
        } else {
            px = 0;
            py = 0;
            pw = textField.width().get();
            ph = textField.height().get();
        }
        graphics.pushClipRect(px, py, pw, ph);
        graphics.setColor(textField.fontcolor().getValue());
        Font font = textField.font().getValue();
        graphics.setFont(font);
        FontHelper helper = Internal.getContext().getFontHelper();
        float caretWidth = helper.computeTextWidth(textField.getTextInRange(0, textField.caret().get()), font);
        float offset = textField.getLineScrollOffset();
        Color selectionColor = Color.BLUE;
        if (textField.length() == 0) {
            graphics.drawText(textField.promptText().getValue(), 0, 0);
        } else {
            if(textField.selectedText().isEmpty()) {
                graphics.drawText(textField.text().getValue(), offset, 0);
            }else{
                String b = textField.text().getValue().substring(0, textField.selection().getValue().getStart());
                String s = textField.selectedText();
                String a = textField.text().getValue().substring(textField.selection().getValue().getEnd());

                graphics.setColor(selectionColor);
                graphics.fillRect(helper.computeTextWidth(b, font) + offset, 0, helper.computeTextWidth(s, font), ph-py);
                graphics.setColor(textField.fontcolor().getValue());
                graphics.drawText(b, offset, 0);
                graphics.setColor(textField.fontcolor().getValue().difference(selectionColor));
                graphics.drawText(s, helper.computeTextWidth(b, font) + offset, 0);
                graphics.setColor(textField.fontcolor().getValue());
                graphics.drawText(a, helper.computeTextWidth(b+s, font) + offset, 0);
            }
        }
        if (textField.focused().get() && System.currentTimeMillis() % 1000 < 500) {
            if(textField.selection().isPresent() && textField.selection().getValue().isInRange(textField.caret().get())){
                graphics.setColor(textField.fontcolor().getValue().difference(selectionColor));
            }
            graphics.fillRect(caretWidth + offset, 0, 1, ph - py);
        }
        graphics.popClipRect();
    }
}
