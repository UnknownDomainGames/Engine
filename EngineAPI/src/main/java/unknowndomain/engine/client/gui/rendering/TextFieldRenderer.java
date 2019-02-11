package unknowndomain.engine.client.gui.rendering;

import unknowndomain.engine.client.gui.Component;
import unknowndomain.engine.client.gui.component.TextField;
import unknowndomain.engine.client.gui.internal.Internal;

public class TextFieldRenderer extends RegionRenderer<TextField> {

    public static final TextFieldRenderer INSTANCE = new TextFieldRenderer();

    @Override
    public void render(TextField textField, Graphics graphics) {
        super.render(textField,graphics);
        float px,py,pw,ph;
        if(textField.padding().isPresent()){
            px = textField.padding().getValue().getLeft();
            py = textField.padding().getValue().getTop();
            pw = textField.width().get()-textField.padding().getValue().getRight();
            ph = textField.height().get()-textField.padding().getValue().getBottom();
        }else{
            px = 0;
            py = 0;
            pw = textField.width().get();
            ph = textField.height().get();
        }
        graphics.pushClipRect(px,py,pw,ph);
        graphics.setColor(textField.fontcolor().getValue());
        graphics.setFont(textField.font().getValue());
        if(textField.length() == 0){
            graphics.drawText(textField.promptText().getValue(), 0,0);
        }else{
            graphics.drawText(textField.text().getValue(),0,0);
        }
        if(textField.focused().get()) {
            graphics.fillRect(Internal.getContext().getFontHelper().computeTextWidth(textField.getTextInRange(0, textField.caret().get()), textField.font().getValue()), 0, 1, ph-py);
        }
        graphics.popClipRect();
    }
}
