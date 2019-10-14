package nullengine.client.gui.component;

import com.github.mouse0w0.observable.value.MutableBooleanValue;
import com.github.mouse0w0.observable.value.SimpleMutableBooleanValue;
import nullengine.client.gui.event.MouseEvent;
import nullengine.client.gui.misc.Border;
import nullengine.util.Color;

public class ToggleButton extends Button {
    private MutableBooleanValue selected = new SimpleMutableBooleanValue(false);

    private Border onColor = new Border(Color.GREEN, 1f);
    private Border offColor = new Border(Color.RED, 1f);

    public ToggleButton() {
        this("", false);
    }

    public ToggleButton(boolean selected) {
        this("", selected);
    }

    public ToggleButton(String text) {
        this(text, false);
    }

    public ToggleButton(String text, boolean selected) {
        super();
        border().setValue(selected ? onColor : offColor);
        this.selected.addChangeListener((ob, o, n) -> border().setValue(n ? onColor : offColor));
        this.selected.set(selected);
        this.text().setValue(text);
    }

    public Border getOnColor() {
        return onColor;
    }

    public void setOnColor(Border onColor) {
        this.onColor = onColor;
    }

    public Border getOffColor() {
        return offColor;
    }

    public void setOffColor(Border offColor) {
        this.offColor = offColor;
    }

    @Override
    public void onClick(MouseEvent.MouseClickEvent event) {
        super.onClick(event);
        selected.set(!selected.getValue());
    }

    public MutableBooleanValue selected() {
        return selected;
    }

}
