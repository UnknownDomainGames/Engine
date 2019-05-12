package unknowndomain.engine.client.gui.component;

import com.github.mouse0w0.lib4j.observable.value.MutableFloatValue;
import com.github.mouse0w0.lib4j.observable.value.MutableValue;
import com.github.mouse0w0.lib4j.observable.value.SimpleMutableObjectValue;
import unknowndomain.engine.client.asset.AssetPath;
import unknowndomain.engine.client.gui.event.MouseEvent;

import java.util.function.Consumer;

public class ImageButton extends Control {
    private final MutableValue<AssetPath> background = new SimpleMutableObjectValue<>();
    private final MutableValue<AssetPath> hoveredBg = new SimpleMutableObjectValue<>();
    private final MutableValue<AssetPath> pressedBg = new SimpleMutableObjectValue<>();
    private final MutableValue<AssetPath> disableBg = new SimpleMutableObjectValue<>();
    private Image image = new Image();
    public ImageButton(AssetPath path) {
        this();
        background.setValue(path);
    }

    public ImageButton() {
        pressed.addChangeListener((observable, oldValue, newValue) -> handleBackground());
        disabled.addChangeListener((observable, oldValue, newValue) -> handleBackground());
        hover.addChangeListener((observable, oldValue, newValue) -> handleBackground());
        background.addChangeListener((observable, oldValue, newValue) -> handleBackground());
        pressedBg.addChangeListener((observable, oldValue, newValue) -> handleBackground());
        hoveredBg.addChangeListener((observable, oldValue, newValue) -> handleBackground());
        disableBg.addChangeListener((observable, oldValue, newValue) -> handleBackground());
        handleBackground();
        this.getChildren().addAll(image);
    }

    public MutableValue<AssetPath> hoverBackground() {
        return hoveredBg;
    }

    public MutableValue<AssetPath> pressBackground() {
        return pressedBg;
    }

    public MutableValue<AssetPath> disabledBackground() {
        return disableBg;
    }

    public MutableValue<AssetPath> buttonBackground() {
        return background;
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

    public MutableFloatValue buttonWidth() {
        return image.imageHeight();
    }

    public MutableFloatValue buttonHeight() {
        return image.imageHeight();
    }


    protected void handleBackground() {
        if (disabled().get()) {
            image.path().setValue(disabledBackground().getValue());
        } else if (pressed().get()) {
            image.path().setValue(pressBackground().getValue());
        } else if (hover().get()) {
            image.path().setValue(hoverBackground().getValue());
        } else {
            image.path().setValue(buttonBackground().getValue());
        }
    }

}
