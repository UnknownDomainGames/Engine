package unknowndomain.engine.client.gui.component;

import com.github.mouse0w0.lib4j.observable.value.MutableBooleanValue;
import com.github.mouse0w0.lib4j.observable.value.SimpleMutableBooleanValue;
import unknowndomain.engine.client.gui.event.MouseEvent;
import unknowndomain.engine.client.gui.misc.Background;
import unknowndomain.engine.client.gui.misc.Border;
import unknowndomain.engine.util.Color;

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
        this.selected.addChangeListener((ob,o,n)-> border().setValue(n ? onColor : offColor));
        this.selected.set(selected);
        this.text().setValue(text);
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
