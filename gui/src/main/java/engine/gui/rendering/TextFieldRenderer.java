package engine.gui.rendering;

import com.google.common.base.Strings;
import engine.graphics.font.Font;
import engine.graphics.font.FontManager;
import engine.graphics.font.TextMesh;
import engine.gui.control.TextField;
import engine.util.Color;

public final class TextFieldRenderer extends RegionRenderer<TextField> {

    private TextField textField;

    private boolean textMeshDirty = true;
    private TextMesh textMesh;

    public TextFieldRenderer(TextField textField) {
        this.textField = textField;
        textField.text().addChangeListener((observable, oldValue, newValue) -> textMeshDirty = true);
        textField.font().addChangeListener((observable, oldValue, newValue) -> textMeshDirty = true);
    }

    private void bakeTextMesh() {
        textMeshDirty = false;
        String text = textField.text().get();
        if (Strings.isNullOrEmpty(text)) {
            textMesh = null;
            return;
        }

        Font font = textField.font().get();
        FontManager fontManager = FontManager.instance();
        textMesh = fontManager.bakeTextMesh(text, font);
    }

    @Override
    public void render(TextField textField, Graphics graphics) {
        super.render(textField, graphics);
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
        Color frontColor = textField.fontcolor().getValue();
        graphics.setColor(frontColor);
        Font font = textField.font().getValue();
        FontManager helper = FontManager.instance();
        float caretWidth = helper.computeTextWidth(textField.getTextInRange(0, textField.caret().get()), font);
        float offset = textField.getLineScrollOffset();
        Color selectionColor = Color.BLUE;
        if (textMeshDirty) bakeTextMesh();
        if (textMesh != null) {
            if (textField.selectedText().isEmpty()) {
                graphics.setColor(frontColor);
                graphics.drawText(textMesh, offset, 0);
            } else {
                int selectionStart = textField.selection().get().getStart();
                int selectionEnd = textField.selection().get().getEnd();
                graphics.setColor(selectionColor);
                graphics.fillRect(textMesh.getWidth(0, selectionStart) + offset, 0, textMesh.getWidth(selectionStart, selectionEnd), ph - py);
                graphics.setColor(frontColor);
                graphics.drawText(textMesh, 0, selectionStart, offset, 0);
                graphics.drawText(textMesh, selectionEnd, textMesh.length(), offset, 0);
                graphics.setColor(frontColor.difference(selectionColor));
                graphics.drawText(textMesh, selectionStart, selectionEnd, offset, 0);
            }
        }
        if (textField.focused().get() && System.currentTimeMillis() % 1000 < 500) {
            if (textField.selection().isPresent() && textField.selection().getValue().isInRange(textField.caret().get())) {
                graphics.setColor(frontColor.difference(selectionColor));
            }
            graphics.fillRect(caretWidth + offset, 0, 1, ph - py);
        }
        graphics.popClipRect();
    }
}
