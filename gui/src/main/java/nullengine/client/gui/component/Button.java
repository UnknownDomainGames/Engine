package nullengine.client.gui.component;

import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import nullengine.client.gui.Node;
import nullengine.client.gui.misc.Background;
import nullengine.client.gui.misc.Insets;
import nullengine.client.gui.text.Text;
import nullengine.client.gui.util.Utils;
import nullengine.util.Color;

public class Button extends Label {

    private final MutableObjectValue<Background> background = new SimpleMutableObjectValue<>(Background.fromColor(Color.BLACK));
    private final MutableObjectValue<Background> hoveredBg = new SimpleMutableObjectValue<>(Background.fromColor(Color.BLUE));
    private final MutableObjectValue<Background> pressedBg = new SimpleMutableObjectValue<>(Background.fromColor(Color.fromRGB(0x507fff)));
    private final MutableObjectValue<Background> disableBg = new SimpleMutableObjectValue<>(Background.fromColor(Color.fromRGB(0x7f7f7f)));

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
            super.background().setValue(disabledBackground().getValue());
        } else if (pressed().get()) {
            super.background().setValue(pressBackground().getValue());
        } else if (hover().get()) {
            super.background().setValue(hoverBackground().getValue());
        } else {
            super.background().setValue(buttonBackground().getValue());
        }
    }

    @Override
    protected void layoutChildren() {
        for (Node child : getChildren()) {
            if (child instanceof Text) {
                var align = ((Text) child).textAlignment().getValue();
                var aw = this.prefWidth() - padding().getValue().getLeft() - padding().getValue().getRight();
                var ah = this.prefHeight() - padding().getValue().getTop() - padding().getValue().getBottom();
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
                        y = (ah - ((Text) child).font().getValue().getSize()) / 2;
                        break;
                }
                x = (float) Math.floor(x + 0.5f);
                y = (float) Math.floor(y + 0.5f);
                layoutInArea(child, padding().getValue().getLeft() + x, padding().getValue().getTop() + y, Utils.prefWidth(child), Utils.prefHeight(child));
            } else { //Although we only have Text inside, we still layout others in case acting as a child
                layoutInArea(child, child.x().get(), child.y().get(), Utils.prefWidth(child), Utils.prefHeight(child));
            }
        }
    }
}
