package engine.gui.control;

import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import engine.gui.Node;
import engine.gui.event.EventHandler;
import engine.gui.input.ActionEvent;
import engine.gui.input.MouseActionEvent;
import engine.gui.misc.Background;
import engine.gui.misc.Insets;
import engine.gui.text.Text;
import engine.gui.util.Utils;
import engine.util.Color;

public class Button extends Label {

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
        padding().setValue(new Insets(0, 5, 5, 5));
        addEventHandler(MouseActionEvent.MOUSE_PRESSED, event -> new ActionEvent(ActionEvent.ACTION, Button.this).fireEvent());
    }

    public Button(String text) {
        this();
        this.text().setValue(text);
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
        if (disabled().get()) {
            super.background().setValue(disabledBackground().get());
        } else if (pressed().get()) {
            super.background().setValue(pressBackground().get());
        } else if (hover().get()) {
            super.background().setValue(hoverBackground().get());
        } else {
            super.background().setValue(buttonBackground().get());
        }
    }

    @Override
    protected void layoutChildren() {
        for (Node child : getChildren()) {
            if (child instanceof Text) {
                var align = ((Text) child).textAlignment().get();
                var aw = this.prefWidth() - padding().get().getLeft() - padding().get().getRight();
                var ah = this.prefHeight() - padding().get().getTop() - padding().get().getBottom();
                float x = 0, y = 0;
                switch (align.getHpos()) {
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
                switch (align.getVpos()) {
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
                        y = (ah - ((Text) child).font().get().getSize()) / 2;
                        break;
                }
                x = (float) Math.floor(x + 0.5f);
                y = (float) Math.floor(y + 0.5f);
                layoutInArea(child, snap(padding().get().getLeft() + x, true), snap(padding().get().getTop() + y, true), Utils.prefWidth(child), Utils.prefHeight(child));
            } else { //Although we only have Text inside, we still layout others in case acting as a child
                layoutInArea(child, child.getLayoutX(), child.getLayoutY(), Utils.prefWidth(child), Utils.prefHeight(child));
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
