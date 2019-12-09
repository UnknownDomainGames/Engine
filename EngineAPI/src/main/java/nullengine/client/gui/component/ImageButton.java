package nullengine.client.gui.component;

import com.github.mouse0w0.observable.value.MutableFloatValue;
import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import nullengine.client.asset.AssetURL;
import nullengine.client.gui.event.old.MouseEvent_;

import java.util.function.Consumer;

public class ImageButton extends Image {
    private final MutableObjectValue<AssetURL> background = new SimpleMutableObjectValue<>();
    private final MutableObjectValue<AssetURL> hoveredBg = new SimpleMutableObjectValue<>();
    private final MutableObjectValue<AssetURL> pressedBg = new SimpleMutableObjectValue<>();
    private final MutableObjectValue<AssetURL> disableBg = new SimpleMutableObjectValue<>();

    public ImageButton(AssetURL path) {
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
    }

    public MutableObjectValue<AssetURL> hoverBackground() {
        return hoveredBg;
    }

    public MutableObjectValue<AssetURL> pressBackground() {
        return pressedBg;
    }

    public MutableObjectValue<AssetURL> disabledBackground() {
        return disableBg;
    }

    public MutableObjectValue<AssetURL> buttonBackground() {
        return background;
    }

    private Consumer<MouseEvent_.MouseClickEvent> onClick;

    @Override
    public void onClick(MouseEvent_.MouseClickEvent event) {
        if (onClick != null)
            onClick.accept(event);
    }

    public void setOnClick(Consumer<MouseEvent_.MouseClickEvent> onClick) {
        this.onClick = onClick;
    }

    public MutableFloatValue buttonWidth() {
        return super.imageHeight();
    }

    public MutableFloatValue buttonHeight() {
        return super.imageHeight();
    }


    protected void handleBackground() {
        if (disabled().get() && disabledBackground().getValue() != null) {
            super.path().setValue(disabledBackground().getValue());
        } else if (pressed().get() && pressBackground().getValue() != null) {
            super.path().setValue(pressBackground().getValue());
        } else if (hover().get() && hoverBackground().getValue() != null) {
            super.path().setValue(hoverBackground().getValue());
        } else {
            super.path().setValue(buttonBackground().getValue());
        }
    }

}
