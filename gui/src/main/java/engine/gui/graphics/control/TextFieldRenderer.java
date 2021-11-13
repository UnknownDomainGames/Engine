package engine.gui.graphics.control;

import com.google.common.base.Strings;
import engine.graphics.font.Font;
import engine.graphics.font.FontManager;
import engine.graphics.font.TextMesh;
import engine.gui.control.TextField;
import engine.gui.graphics.Graphics;
import engine.gui.graphics.RegionRenderer;
import engine.gui.graphics.util.ClipRectHelper;
import engine.gui.misc.Insets;
import engine.util.Color;
import org.joml.primitives.Rectanglei;

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
        Insets padding = textField.getPadding();
        float px = padding != null ? padding.getLeft() : 0;
        float py = padding != null ? padding.getTop() : 0;
        float pw = padding != null ? textField.getWidth() - padding.getLeft() - padding.getRight() : textField.getWidth();
        float ph = padding != null ? textField.getHeight() - padding.getTop() - padding.getBottom() : textField.getHeight();
        Rectanglei clipRect = ClipRectHelper.setAndTransform(px, py, pw, ph, graphics.getTransformNoClone(), new Rectanglei());
        graphics.setClipRect(clipRect);
        Color frontColor = textField.fontcolor().get();
        graphics.setColor(frontColor);
        Font font = textField.font().get();
        FontManager helper = FontManager.instance();
        float caretWidth = helper.computeTextWidth(textField.getTextInRange(0, textField.caret().get()), font);
        float offset = px + textField.getLineScrollOffset();
        Color selectionColor = Color.BLUE;
        if (textMeshDirty) bakeTextMesh();
        if (textMesh != null) {
            if (textField.selectedText().isEmpty()) {
                graphics.setColor(frontColor);
                graphics.drawText(textMesh, offset, py);
            } else {
                int selectionStart = textField.selection().get().getStart();
                int selectionEnd = textField.selection().get().getEnd();
                graphics.setColor(selectionColor);
                graphics.fillRect(textMesh.getWidth(0, selectionStart) + offset, py, textMesh.getWidth(selectionStart, selectionEnd), ph - py);
                graphics.setColor(frontColor);
                graphics.drawText(textMesh, 0, selectionStart, offset, py);
                graphics.drawText(textMesh, selectionEnd, textMesh.length(), offset, py);
                graphics.setColor(frontColor.difference(selectionColor));
                graphics.drawText(textMesh, selectionStart, selectionEnd, offset, py);
            }
        }
        if (textField.isFocused() && System.currentTimeMillis() % 1000 < 500) {
            if (textField.selection().isPresent() && textField.selection().get().isInRange(textField.caret().get())) {
                graphics.setColor(frontColor.difference(selectionColor));
            }
            graphics.fillRect(caretWidth + offset, py, 1, ph);
        }
        graphics.resetClipRect();
    }
}
