package nullengine.client.gui.component;

import com.github.mouse0w0.observable.value.MutableValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import nullengine.client.gui.event.MouseEvent;
import nullengine.client.gui.misc.Background;
import nullengine.client.gui.misc.Insets;
import nullengine.client.rendering.util.Color;

import java.util.function.Consumer;

public class Button extends Label {

    private final MutableValue<Background> background = new SimpleMutableObjectValue<>(Background.fromColor(Color.BLACK));
    private final MutableValue<Background> hoveredBg = new SimpleMutableObjectValue<>(Background.fromColor(Color.BLUE));
    private final MutableValue<Background> pressedBg = new SimpleMutableObjectValue<>(Background.fromColor(Color.fromRGB(0x507fff)));
    private final MutableValue<Background> disableBg = new SimpleMutableObjectValue<>(Background.fromColor(Color.fromRGB(0x7f7f7f)));

    public Button() {
        super();
        pressed.addChangeListener((observable, oldValue, newValue) -> handleBackground());
        disabled.addChangeListener((observable, oldValue, newValue) -> handleBackground());
        hover.addChangeListener((observable, oldValue, newValue) -> handleBackground());
        background.addChangeListener((observable, oldValue, newValue) -> handleBackground());
        pressedBg.addChangeListener((observable, oldValue, newValue) -> handleBackground());
        hoveredBg.addChangeListener((observable, oldValue, newValue) -> handleBackground());
        disableBg.addChangeListener((observable, oldValue, newValue) -> handleBackground());
        text().addChangeListener((observable, oldValue, newValue) -> requestParentLayout());
        handleBackground();
        padding().setValue(new Insets(0, 5, 5, 5));
    }

    public Button(String text) {
        this();
        this.text().setValue(text);
    }


    public MutableValue<Background> hoverBackground() {
        return hoveredBg;
    }

    public MutableValue<Background> pressBackground() {
        return pressedBg;
    }

    public MutableValue<Background> disabledBackground() {
        return disableBg;
    }

    private Consumer<MouseEvent.MouseClickEvent> onClick;

    @Override
    public void onClick(MouseEvent.MouseClickEvent event) {
        if (onClick != null)
            onClick.accept(event);
    }

    public void setOnClick(Consumer<MouseEvent.MouseClickEvent> onClick) {
        this.onClick = onClick;
    }

    public MutableValue<Background> buttonBackground() {
        return background;
    }

    protected void handleBackground() {
        if (disabled().get()) {
            super.background().setValue(disabledBackground().getValue());
        } else if (pressed().get()) {
            super.background().setValue(pressBackground().getValue());
        } else if (hover().get()) {
            super.background().setValue(hoverBackground().getValue());
        } else {
            super.background().setValue(buttonBackground().getValue());
        }
    }
}
