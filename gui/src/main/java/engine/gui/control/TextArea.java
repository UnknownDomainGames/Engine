package engine.gui.control;

import engine.graphics.font.FontManager;
import engine.gui.input.KeyEvent;
import engine.input.KeyCode;

public class TextArea extends TextInput {

    private ScrollPane scrollPane;
    private Text text;

    public TextArea() {
        super();
//        removeEventHandler(KeyEvent.KEY_PRESSED, super::onKeyPressed);
//        addEventHandler(KeyEvent.KEY_PRESSED, this::onKeyPressed);
        scrollPane = new ScrollPane();
        text = new Text();
        text.text().bindBidirectional(this.text());
        scrollPane.setContent(text);
        width().addChangeListener((observable, o, n) -> scrollPane.getSize().prefWidth().set(n));
        height().addChangeListener((observable, o, n) -> scrollPane.getSize().prefHeight().set(n));
        this.getChildren().add(scrollPane);
    }

    public void onKeyPressed(KeyEvent event) {
        super.onKeyPressed(event);
        if (event.getKey() == KeyCode.ENTER || event.getKey() == KeyCode.NUMPAD_ENTER) {
            if (caret.get() != anchor.get()) {
                replaceSelection("");
                deselect();
            }
            insertText(Math.max(caret.get(), 0), "\n");
        }
    }

    @Override
    protected int getNearestMousePos(double posX, double posY) {
        //TODO
        double adjustedX = posX - scrollPane.xOffset().getFloat();
        int posExclusive = 1;
        double x = 0;
        while (posExclusive <= length()) {
            x += FontManager.instance().computeTextWidth(getTextInRange(posExclusive - 1, posExclusive), font().get());
            if (x > adjustedX)
                break;
            posExclusive++;
        }
        return posExclusive - 1;
    }

    @Override
    protected void updatePointer() {

    }
}
