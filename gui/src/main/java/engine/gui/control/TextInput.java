package engine.gui.control;

import com.github.mouse0w0.observable.value.*;
import engine.graphics.font.Font;
import engine.gui.Parent;
import engine.gui.input.Clipboard;
import engine.gui.input.KeyEvent;
import engine.gui.input.MouseActionEvent;
import engine.gui.input.MouseEvent;
import engine.gui.misc.Background;
import engine.gui.misc.Border;
import engine.gui.misc.IndexRange;
import engine.gui.misc.Insets;
import engine.input.Modifiers;
import engine.math.Math2;
import engine.util.Color;

import java.text.BreakIterator;

public abstract class TextInput extends Control {
    //Selection
    protected MutableIntValue anchor = new SimpleMutableIntValue(0);
    protected MutableIntValue caret = new SimpleMutableIntValue(0);
    private MutableObjectValue<Color> fontcolor = new SimpleMutableObjectValue<>(Color.WHITE);
    private MutableObjectValue<Font> font = new SimpleMutableObjectValue<>(Font.getDefaultFont());
    private MutableStringValue text = new SimpleMutableStringValue("");
    private MutableStringValue promptText = new SimpleMutableStringValue("");
    private MutableObjectValue<IndexRange> selection = new SimpleMutableObjectValue<>();
    private MutableBooleanValue editable = new SimpleMutableBooleanValue(true);
    private MutableBooleanValue undoable = new SimpleMutableBooleanValue(false);
    private MutableBooleanValue redoable = new SimpleMutableBooleanValue(false);
    private BreakIterator charIterator;
    private BreakIterator wordIterator;

    private Text prompt;

    public TextInput() {
        setBackground(Background.fromColor(Color.fromARGB(0x000000c8)));
        setBorder(new Border(Color.WHITE, 2));
        setPadding(new Insets(3f));
        caret().addChangeListener((observable, oldValue, newValue) -> updatePointer());

        addEventHandler(KeyEvent.KEY_PRESSED, this::onKeyPressed);
        addEventHandler(KeyEvent.KEY_TYPED, this::onKeyTyped);
        addEventHandler(MouseActionEvent.MOUSE_PRESSED, this::onPressed);
        addEventHandler(MouseEvent.MOUSE_MOVED, this::onMouseMove);

        prompt = new Text();
        promptText().bindBidirectional(prompt.text());
        getChildren().add(prompt);
        font().bind(prompt.font());
        text().addChangeListener((observable, oldValue, newValue) -> prompt.setVisible(length() == 0));
    }

    public MutableObjectValue<Font> font() {
        return font;
    }

    public MutableObjectValue<Color> fontcolor() {
        return fontcolor;
    }

    public MutableStringValue text() {
        return text;
    }

    public MutableStringValue promptText() {
        return promptText;
    }

    public MutableBooleanValue editable() {
        return editable;
    }

    protected void onKeyTyped(KeyEvent event) {
        String character = event.getCharacter();
        if (caret.get() != anchor.get()) {
            replaceSelection("");
            deselect();
        }
        insertText(Math.max(caret.get(), 0), character);
    }

    protected void onKeyPressed(KeyEvent event) {
        switch (event.getKey()) {
            case LEFT:
                if (event.getModifier().hasShift()) {
                    selectBackward();
                } else {
                    backward();
                }
                break;
            case RIGHT:
                if (event.getModifier().hasShift()) {
                    selectForward();
                } else {
                    forward();
                }
                break;
            case BACKSPACE:
                if (caret.get() != anchor.get()) {
                    replaceSelection("");
                    deselect();
                } else {
                    backspace();
                }
                break;
            case DELETE:
                if (caret.get() != anchor.get()) {
                    replaceSelection("");
                    deselect();
                } else {
                    delete();
                }
                break;
            case X:
                if (event.getModifier().has(Modifiers.Modifier.CONTROL)) {
                    cut();
                }
                break;
            case C:
                if (event.getModifier().has(Modifiers.Modifier.CONTROL)) {
                    copy();
                }
                break;
            case V:
                if (event.getModifier().has(Modifiers.Modifier.CONTROL)) {
                    paste();
                }
                break;
            case A:
                if (event.getModifier().has(Modifiers.Modifier.CONTROL)) {
                    selectAll();
                }
                break;
        }
    }

    protected void onPressed(MouseActionEvent event) {
        positionCaret(getNearestMousePos(event.getX(), event.getY()));
    }

    protected abstract int getNearestMousePos(float posX, float posY);

    protected void onMouseMove(MouseEvent event) {
        if (isPressed()) {
            selectPositionCaret(getNearestMousePos(event.getX(), event.getY()));
        }
    }

    public String selectedText() {
        if (!text.isPresent() || !selection.isPresent()) {
            return "";
        } else {
            int s = selection.get().getStart();
            int e = selection.get().getEnd();
            int l = length();
            if (e > s + l) e = l;
            if (s > l - 1) s = e = 0;
            return text.get().substring(s, e);
        }
    }

    public MutableObjectValue<IndexRange> selection() {
        return selection;
    }

    public int length() {
        return text.isPresent() ? text.get().length() : 0;
    }

    public String getTextInRange(int start, int end) {
        rangeCheck(start, end);
        return text.get().substring(start, end);
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

    private int replaceText(int start, int end, String text, int anchor, int caret) {
        int len = length();
        int adjusted = 0;
        String content = text().get();
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
        text().set(content);
        selectRange(anchor, caret);
        return adjusted;
    }

    public void replaceSelection(String replacement) {
        replaceText(selection.get(), replacement);
    }

    private void selectRange(int anchor, int caret) {
        this.anchor.set(Math2.clamp(anchor, 0, length()));
        this.caret.set(Math2.clamp(caret, 0, length()));
        this.selection.set(IndexRange.ofOrderless(this.anchor.get(), this.caret.get()));
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
        IndexRange selection = this.selection.get();
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
            charIterator.setText(text.get());
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
            charIterator.setText(text.get());
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
        if (editable.get()) {
            final String original = text.get();
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
        if (editable.get()) {
            final int textLength = length();
            final String original = text.get();
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
            charIterator.setText(text.get());
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
            charIterator.setText(text.get());
            int pos = charIterator.preceding(dot);
            selectRange(pos, pos);
        }
        deselect();
    }

    public void positionCaret(int pos) {
        final int p = Math2.clamp(0, pos, length());
        selectRange(p, p);
    }

    protected abstract void updatePointer();

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

    @Override
    protected void layoutChildren() {
        Insets padding = getPadding();
        layoutInArea(prompt, padding.getLeft(), padding.getTop(), Parent.prefWidth(prompt), Parent.prefHeight(prompt));
    }
}
