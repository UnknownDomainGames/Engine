package nullengine.client.gui.component;

import com.github.mouse0w0.observable.value.*;
import nullengine.client.gui.event.CharEvent;
import nullengine.client.gui.event.KeyEvent;
import nullengine.client.gui.event.MouseEvent;
import nullengine.client.gui.internal.Internal;
import nullengine.client.gui.misc.Background;
import nullengine.client.gui.misc.Border;
import nullengine.client.gui.misc.IndexRange;
import nullengine.client.gui.misc.Insets;
import nullengine.client.gui.rendering.ComponentRenderer;
import nullengine.client.gui.rendering.TextFieldRenderer;
import nullengine.client.gui.text.Font;
import nullengine.client.input.Clipboard;
import nullengine.client.input.keybinding.KeyModifier;
import nullengine.event.Event;
import nullengine.math.Math2;
import nullengine.util.Color;
import org.apache.commons.lang3.ArrayUtils;

import java.text.BreakIterator;

public class TextField extends Control {
    private MutableValue<Color> fontcolor = new SimpleMutableObjectValue<>(Color.WHITE);
    private MutableValue<Font> font = new SimpleMutableObjectValue<>(Font.getDefaultFont());

    private MutableValue<String> text = new SimpleMutableObjectValue<>("");
    private MutableValue<String> promptText = new SimpleMutableObjectValue<>("");

    private MutableValue<IndexRange> selection = new SimpleMutableObjectValue<>();

    private MutableBooleanValue editable = new SimpleMutableBooleanValue(true);
    private MutableBooleanValue undoable = new SimpleMutableBooleanValue(false);
    private MutableBooleanValue redoable = new SimpleMutableBooleanValue(false);

    //Selection
    private MutableIntValue anchor = new SimpleMutableIntValue(0);
    private MutableIntValue caret = new SimpleMutableIntValue(0);

    private float lineScrollOffset = 0;

    public float getLineScrollOffset() {
        return lineScrollOffset;
    }

    private BreakIterator charIterator;
    private BreakIterator wordIterator;

    public TextField() {
        background().setValue(Background.fromColor(Color.fromARGB(0x000000c8)));
        border().setValue(new Border(Color.WHITE, 2));
        padding().setValue(new Insets(3f));
        caret().addChangeListener((observable, oldValue, newValue) -> updatePointer());
    }

    public MutableValue<Font> font() {
        return font;
    }

    public MutableValue<Color> fontcolor() {
        return fontcolor;
    }

    public MutableValue<String> text() {
        return text;
    }

    public MutableValue<String> promptText() {
        return promptText;
    }

    public MutableBooleanValue editable() {
        return editable;
    }

    @Override
    public void handleEvent(Event event) {
        super.handleEvent(event);
        if (event instanceof KeyEvent) {
            if (event instanceof KeyEvent.KeyDownEvent) {
                onKeyDown((KeyEvent.KeyDownEvent) event);
            } else if (event instanceof KeyEvent.KeyHoldEvent) {
                onKeyHold((KeyEvent.KeyHoldEvent) event);
            } else if (event instanceof KeyEvent.KeyUpEvent) {
                onKeyUp(((KeyEvent.KeyUpEvent) event));
            }
        }
        if (event instanceof CharEvent) {
            char c = ((CharEvent) event).getCharacter();
            if (caret.get() != anchor.get()) {
                replaceSelection("");
                deselect();
            }
            insertText(Math.max(caret.get(), 0), String.valueOf(c));
            //positionCaret(caret.get() + 1);
        }
        if (event instanceof MouseEvent.MouseMoveEvent) {
            onMouseMove((MouseEvent.MouseMoveEvent) event);
        }
    }

    public void onKeyDown(KeyEvent.KeyDownEvent event) {
        switch (event.getKey()) {
            case KEY_LEFT:
                if (ArrayUtils.contains(event.getModifiers(), KeyModifier.SHIFT)) {
                    selectBackward();
                } else {
                    backward();
                }
                break;
            case KEY_RIGHT:
                if (ArrayUtils.contains(event.getModifiers(), KeyModifier.SHIFT)) {
                    selectForward();
                } else {
                    forward();
                }
                break;
            case KEY_BACKSPACE:
                if (caret.get() != anchor.get()) {
                    replaceSelection("");
                    deselect();
                } else {
                    backspace();
                }
                break;
            case KEY_DELETE:
                if (caret.get() != anchor.get()) {
                    replaceSelection("");
                    deselect();
                } else {
                    delete();
                }
                break;
            case KEY_X:
                if (event.getModifiers().length == 1 && event.getModifiers()[0] == KeyModifier.CONTROL) {
                    cut();
                }
                break;
            case KEY_C:
                if (event.getModifiers().length == 1 && event.getModifiers()[0] == KeyModifier.CONTROL) {
                    copy();
                }
                break;
            case KEY_V:
                if (event.getModifiers().length == 1 && event.getModifiers()[0] == KeyModifier.CONTROL) {
                    paste();
                }
                break;
            case KEY_A:
                if (event.getModifiers().length == 1 && event.getModifiers()[0] == KeyModifier.CONTROL) {
                    selectAll();
                }
                break;
        }
    }

    public void onKeyUp(KeyEvent.KeyUpEvent event) {
    }

    public void onKeyHold(KeyEvent.KeyHoldEvent event) {
        switch (event.getKey()) {
            case KEY_X:
            case KEY_C:
            case KEY_A:
                //No need to repeat
                break;
            default:
                onKeyDown(new KeyEvent.KeyDownEvent(event.getComponent(), event.getKey(), event.getMode(), event.getModifiers()));
        }
    }

    @Override
    public void onClick(MouseEvent.MouseClickEvent event) {
        super.onClick(event);
        positionCaret(getNearestMousePos(event.getPosX()));
    }

    private int getNearestMousePos(float posX) {
        float adjustedX = posX - lineScrollOffset;
        int posExclusive = 1;
        float x = 0;
        while (posExclusive <= length()) {
            x += Internal.getContext().getFontHelper().computeTextWidth(getTextInRange(posExclusive - 1, posExclusive), font().getValue());
            if (x > adjustedX)
                break;
            posExclusive++;
        }
        return posExclusive - 1;
    }

    public void onMouseMove(MouseEvent.MouseMoveEvent event) {
        if (pressed.get()) {
            selectPositionCaret(getNearestMousePos(relativePos(((float) event.getNewPosX()), ((float) event.getNewPosY())).getLeft()));
        }
    }

    @Override
    protected ComponentRenderer createDefaultRenderer() {
        return TextFieldRenderer.INSTANCE;
    }

    public String selectedText() {
        if (!text.isPresent() || !selection.isPresent()) {
            return "";
        } else {
            int s = selection.getValue().getStart();
            int e = selection.getValue().getEnd();
            int l = length();
            if (e > s + l) e = l;
            if (s > l - 1) s = e = 0;
            return text.getValue().substring(s, e);
        }
    }

    public MutableValue<IndexRange> selection() {
        return selection;
    }

    public int length() {
        return text.isPresent() ? text.getValue().length() : 0;
    }

    public String getTextInRange(int start, int end) {
        rangeCheck(start, end);
        return text.getValue().substring(start, end);
    }

    public void appendText(String text) {
        insertText(length(), text);
    }

    public void insertText(int index, String text) {
        replaceText(index, index, text);
    }

    public void deleteText(IndexRange range) {
        replaceText(range, "");
    }

    public void deleteText(int start, int end) {
        replaceText(start, end, "");
    }

    public void replaceText(IndexRange range, String text) {
        final int start = range.getStart();
        final int end = start + range.length();
        replaceText(start, end, text);
    }

    public void replaceText(final int start, final int end, final String text) {
        rangeCheck(start, end);
        if (text == null) {
            throw new NullPointerException();
        }
        replaceText(start, end, text, anchor.get(), caret.get());
    }

//    private void updateContent()

    private int replaceText(int start, int end, String text, int anchor, int caret) {
        int len = length();
        int adjusted = 0;
        String content = text().getValue();
        if (end != start) {
            content = content.substring(0, start) + content.substring(end);
            len -= (end - start);
        }
        if (text != null && !text.isEmpty()) {
            String s = (start <= content.length() ? content.substring(0, start) : "") + text;
            if (start < content.length())
                s = s + content.substring(start);
            content = s;
            adjusted = text.length() - (length() - len);
            anchor += adjusted;
            caret += adjusted;
        }
        text().setValue(content);
        selectRange(anchor, caret);
        return adjusted;
    }

    public void replaceSelection(String replacement) {
        replaceText(selection.getValue(), replacement);
    }

    private void selectRange(int anchor, int caret) {
        this.anchor.set(Math2.clamp(anchor, 0, length()));
        this.caret.set(Math2.clamp(caret, 0, length()));
        this.selection.setValue(IndexRange.ofOrderless(this.anchor.get(), this.caret.get()));
    }

    private void rangeCheck(int start, int end) {
        if (end < start) {
            throw new IllegalArgumentException(String.format("start(%d) > end(%d)", start, end));
        }
        if (start < 0 || end > length()) {
            throw new IndexOutOfBoundsException(String.format("Range %s out of bound %s", new IndexRange(start, end), new IndexRange(0, length())));
        }
    }

    public void cut() {
        copy();
        IndexRange selection = this.selection.getValue();
        deleteText(selection.getStart(), selection.getEnd());
    }

    /**
     * Transfers the currently selected range in the text to the clipboard,
     * leaving the current selection.
     */
    public void copy() {
        final String selectedText = selectedText();
        if (selectedText.length() > 0) {
            Clipboard.setString(selectedText);
        }
    }

    /**
     * Transfers the contents in the clipboard into this text,
     * replacing the current selection.  If there is no selection, the contents
     * in the clipboard is inserted at the current caret position.
     */
    public void paste() {
        final String text = Clipboard.getString();
        if (text != null) {
            //createNewUndoRecord = true;
            try {
                replaceSelection(text);
                deselect();
            } finally {
                //createNewUndoRecord = false;
            }
        }

    }

    public void selectBackward() {
        if (caret.get() > 0 && length() > 0) {
            // because the anchor stays put, by moving the caret to the left
            // we ensure that a selection is registered and that it is correct
            if (charIterator == null) {
                charIterator = BreakIterator.getCharacterInstance();
            }
            charIterator.setText(text.getValue());
            selectRange(anchor.get(), charIterator.preceding(caret.get()));
        }
    }

    /**
     * Moves the selection forward one char in the text. This may have the
     * effect of deselecting, depending on the location of the anchor relative
     * to the caretPosition. This function effectively just moves the caret forward.
     */
    public void selectForward() {
        final int textLength = length();
        if (textLength > 0 && caret.get() < textLength) {
            if (charIterator == null) {
                charIterator = BreakIterator.getCharacterInstance();
            }
            charIterator.setText(text.getValue());
            selectRange(anchor.get(), charIterator.following(caret.get()));
        }
    }

    public void selectAll() {
        selectRange(0, length());
    }

    public void deselect() {
        // set the anchor equal to the caret position, which clears the selection
        // while also preserving the caret position
        selectRange(caret.get(), caret.get());
    }

    public void home() {
        // user wants to go to start
        selectRange(0, 0);
    }

    public void end() {
        // user wants to go to end
        final int textLength = length();
        if (textLength > 0) {
            selectRange(textLength, textLength);
        }
    }

    public void selectToHome() {
        selectRange(anchor.get(), 0);
    }

    public void selectToEnd() {
        final int textLength = length();
        if (textLength > 0) selectRange(anchor.get(), textLength);
    }

    public boolean backspace() {
        boolean failed = true;
        if (editable.get() && !disabled.get()) {
            final String original = text.getValue();
            final int caret = this.caret.get();
            final int anchor = this.anchor.get();
            if (caret != anchor) {
                replaceSelection("");
                failed = false;
            } else if (caret > 0) {
                int p = Character.offsetByCodePoints(original, caret, -1);
                deleteText(p, caret);
                failed = false;
            }
        }
        return !failed;

    }

    public boolean delete() {
        boolean failed = true;
        if (editable.get() && !disabled.get()) {
            final int textLength = length();
            final String original = text.getValue();
            final int caret = this.caret.get();
            final int anchor = this.anchor.get();
            if (caret != anchor) {
                replaceSelection("");
                failed = false;
            } else if (textLength > 0 && caret < textLength) {
                if (charIterator == null) {
                    charIterator = BreakIterator.getCharacterInstance();
                }
                charIterator.setText(original);
                int p = charIterator.following(caret);
                deleteText(caret, p);
                failed = false;
            }
        }
        return !failed;
    }

    public void forward() {
        // user has moved caret to the right
        final int textLength = length();
        final int dot = caret.get();
        final int mark = anchor.get();
        if (dot != mark) {
            int pos = Math.max(dot, mark);
            selectRange(pos, pos);
        } else if (dot < textLength && textLength > 0) {
            if (charIterator == null) {
                charIterator = BreakIterator.getCharacterInstance();
            }
            charIterator.setText(text.getValue());
            int pos = charIterator.following(dot);
            selectRange(pos, pos);
        }
        deselect();
    }

    public void backward() {
        // user has moved caret to the left
        final int textLength = length();
        final int dot = caret.get();
        final int mark = anchor.get();
        if (dot != mark) {
            int pos = Math.min(dot, mark);
            selectRange(pos, pos);
        } else if (dot > 0 && textLength > 0) {
            if (charIterator == null) {
                charIterator = BreakIterator.getCharacterInstance();
            }
            charIterator.setText(text.getValue());
            int pos = charIterator.preceding(dot);
            selectRange(pos, pos);
        }
        deselect();
    }

    public void positionCaret(int pos) {
        final int p = Math2.clamp(0, pos, length());
        selectRange(p, p);
    }

    private void updatePointer() {
        var viewableWidth = width().get() - padding().getValue().getLeft() - padding().getValue().getRight();
        var caretOffset = Internal.getContext().getFontHelper().computeTextWidth(getTextInRange(0, caret().get()), font().getValue());
        var adjusted = caretOffset + lineScrollOffset;
        if (viewableWidth < adjusted) {
            lineScrollOffset = viewableWidth - caretOffset;
        } else if (adjusted < 0) {
            lineScrollOffset = lineScrollOffset - adjusted;
        }
        lineScrollOffset = Math.min(lineScrollOffset, 0);
    }

    /**
     * Positions the caret to the position indicated by {@code pos} and extends
     * the selection, if there is one. If there is no selection, then a
     * selection is formed where the anchor is at the current caret position
     * and the caretPosition is moved to pos.
     */
    public void selectPositionCaret(int pos) {
        selectRange(anchor.get(), Math2.clamp(0, pos, length()));
    }

    public MutableIntValue caret() {
        return caret;
    }

    public MutableIntValue anchor() {
        return anchor;
    }
}
