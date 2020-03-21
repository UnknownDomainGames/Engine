package engine.gui.control;

import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import engine.gui.Node;
import engine.gui.Parent;
import engine.gui.event.EventHandler;
import engine.gui.input.ActionEvent;
import engine.gui.input.MouseActionEvent;
import engine.gui.misc.Background;
import engine.gui.misc.Insets;
import engine.gui.text.Text;
import engine.util.Color;

public class Button extends Labeled {

    private final MutableObjectValue<Background> background = new SimpleMutableObjectValue<>(Background.fromColor(Color.BLACK));
    private final MutableObjectValue<Background> hoveredBg = new SimpleMutableObjectValue<>(Background.fromColor(Color.BLUE));
    private final MutableObjectValue<Background> pressedBg = new SimpleMutableObjectValue<>(Background.fromColor(Color.fromRGB(0x507fff)));
    private final MutableObjectValue<Background> disableBg = new SimpleMutableObjectValue<>(Background.fromColor(Color.fromRGB(0x7f7f7f)));

    public Button() {
        super();
        pressed().addChangeListener((observable, oldValue, newValue) -> handleBackground());
        disabled().addChangeListener((observable, oldValue, newValue) -> handleBackground());
        hover().addChangeListener((observable, oldValue, newValue) -> handleBackground());
        background.addChangeListener((observable, oldValue, newValue) -> handleBackground());
        pressedBg.addChangeListener((observable, oldValue, newValue) -> handleBackground());
        hoveredBg.addChangeListener((observable, oldValue, newValue) -> handleBackground());
        disableBg.addChangeListener((observable, oldValue, newValue) -> handleBackground());
        text().addChangeListener((observable, oldValue, newValue) -> requestParentLayout());
        handleBackground();
        setPadding(new Insets(0, 5, 5, 5));
        addEventHandler(MouseActionEvent.MOUSE_PRESSED, event -> new ActionEvent(ActionEvent.ACTION, Button.this).fireEvent());
    }

    public Button(String text) {
        this();
        this.text().set(text);
    }

    public MutableObjectValue<Background> hoverBackground() {
        return hoveredBg;
    }

    public MutableObjectValue<Background> pressBackground() {
        return pressedBg;
    }

    public MutableObjectValue<Background> disabledBackground() {
        return disableBg;
    }

    public MutableObjectValue<Background> buttonBackground() {
        return background;
    }

    protected void handleBackground() {
        if (isDisabled()) {
            super.setBackground(disabledBackground().get());
        } else if (isPressed()) {
            super.setBackground(pressBackground().get());
        } else if (isHover()) {
            super.setBackground(hoverBackground().get());
        } else {
            super.setBackground(buttonBackground().get());
        }
    }

    @Override
    protected void layoutChildren() {
        for (Node child : getChildren()) {
            if (child instanceof Text) {
                var align = ((Text) child).textAlignment().get();
                var aw = this.prefWidth() - getPadding().getLeft() - getPadding().getRight();
                var ah = this.prefHeight() - getPadding().getTop() - getPadding().getBottom();
                float x = 0, y = 0;
                switch (align.getHPos()) {
                    case LEFT:
                        x = 0;
                        break;
                    case CENTER:
                        x = (aw - child.prefWidth()) / 2;
                        break;
                    case RIGHT:
                        x = aw - child.prefWidth();
                        break;
                }
                switch (align.getVPos()) {
                    case TOP:
                        y = 0;
                        break;
                    case CENTER:
                        y = (ah - child.prefHeight()) / 2;
                        break;
                    case BOTTOM:
                        y = ah - child.prefHeight();
                        break;
                    case BASELINE:
                        y = (ah - ((Text) child).getFont().getSize()) / 2;
                        break;
                }
                x = (float) Math.floor(x + 0.5f);
                y = (float) Math.floor(y + 0.5f);
                layoutInArea(child, snap(getPadding().getLeft() + x, true), snap(getPadding().getTop() + y, true), Parent.prefWidth(child), Parent.prefHeight(child));
            } else { //Although we only have Text inside, we still layout others in case acting as a child
                layoutInArea(child, child.getLayoutX(), child.getLayoutY(), Parent.prefWidth(child), Parent.prefHeight(child));
            }
        }
    }

    private MutableObjectValue<EventHandler<ActionEvent>> onAction;

    public final MutableObjectValue<EventHandler<ActionEvent>> onAction() {
        if (onAction == null) {
            onAction = new SimpleMutableObjectValue<>();
            onAction.addChangeListener((observable, oldValue, newValue) -> {
                if (oldValue != null) removeEventHandler(ActionEvent.ACTION, oldValue);
                if (newValue != null) addEventHandler(ActionEvent.ACTION, newValue);
            });
        }
        return onAction;
    }

    public final EventHandler<ActionEvent> getOnAction() {
        return onAction == null ? null : onAction.get();
    }

    public final void setOnAction(EventHandler<ActionEvent> onAction) {
        onAction().set(onAction);
    }
}
