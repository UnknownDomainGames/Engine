package engine.gui.control;

import engine.graphics.font.FontManager;
import engine.gui.graphics.ComponentRenderer;
import engine.gui.graphics.TextFieldRenderer;

public class TextField extends TextInput {

    private float lineScrollOffset = 0;

    public float getLineScrollOffset() {
        return lineScrollOffset;
    }

    public TextField() {
        super();
    }

    //    public void onKeyHold(KeyEvent event) {
//        switch (event.getKey()) {
//            case KEY_X:
//            case KEY_C:
//            case KEY_A:
//                //No need to repeat
//                break;
//            default:
//                onKeyDown(new KeyEvent_.KeyDownEvent(event.getNode(), event.getKey(), event.getMode(), event.getModifier()));
//        }
//    }

    @Override
    protected int getNearestMousePos(float posX, float posY) {
        float adjustedX = posX - lineScrollOffset;
        int posExclusive = 1;
        float x = 0;
        while (posExclusive <= length()) {
            x += FontManager.instance().computeTextWidth(getTextInRange(posExclusive - 1, posExclusive), font().get());
            if (x > adjustedX)
                break;
            posExclusive++;
        }
        return posExclusive - 1;
    }

    @Override
    protected ComponentRenderer createDefaultRenderer() {
        return new TextFieldRenderer(this);
    }

    @Override
    protected void updatePointer() {
        var viewableWidth = getWidth() - getPadding().getLeft() - getPadding().getRight();
        var caretOffset = FontManager.instance().computeTextWidth(getTextInRange(0, caret().get()), font().get());
        var adjusted = caretOffset + lineScrollOffset;
        if (viewableWidth < adjusted) {
            lineScrollOffset = viewableWidth - caretOffset;
        } else if (adjusted < 0) {
            lineScrollOffset = lineScrollOffset - adjusted;
        }
        lineScrollOffset = Math.min(lineScrollOffset, 0);
    }

    //    private void updateContent()

}
