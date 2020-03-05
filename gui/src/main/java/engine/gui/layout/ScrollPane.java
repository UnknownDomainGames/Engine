package engine.gui.layout;

import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.ObservableDoubleValue;
import com.github.mouse0w0.observable.value.ObservableValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import engine.gui.Node;
import engine.gui.control.HSlider;
import engine.gui.control.VSlider;
import engine.gui.misc.Pos;
import engine.gui.util.Utils;

public class ScrollPane extends BorderPane {
    private final VSlider vScroll;
    private final HSlider hScroll;

    private final MutableObjectValue<Node> content = new SimpleMutableObjectValue<>();

    public ScrollPane() {
        super();
        vScroll = new VSlider();
        hScroll = new HSlider();
        vScroll.sliderThickness().set(20f);
        hScroll.sliderThickness().set(20f);
        vScroll.step().set(1);
        hScroll.step().set(1);
        bottom().setValue(hScroll);
        right().setValue(vScroll);
        setAlignment(hScroll, Pos.BOTTOM_LEFT);
        setAlignment(vScroll, Pos.TOP_RIGHT);
        content.addChangeListener((observable, oldValue, newValue) -> {
            update();
            if (newValue != null) {
                newValue.layoutBounds().addChangeListener((observable1, o, n) -> update());
            }
        });
        layoutBounds().addChangeListener((observable, oldValue, newValue) -> {
            hScroll.sliderLength().set(getWidth());
            vScroll.sliderLength().set(getHeight());
            update();
        });
    }

    private void update() {
        if (content.isPresent()) {
            if (content.get().getWidth() > getWidth()) {
                hScroll.setDisabled(false);
                vScroll.setVisible(true);
                var delta = content.get().getWidth() - getWidth();
                hScroll.max().set(delta);
                hScroll.step().set(delta / 10.0f);
                hScroll.value().addChangeListener((observable, oldValue, newValue) -> content.get().relocate(-hScroll.value().getFloat(), content.get().getLayoutY()));
            } else {
                hScroll.setDisabled(true);
                hScroll.setVisible(false);
                hScroll.max().set(1);
            }
            if (content.get().getHeight() > getHeight()) {
                vScroll.setDisabled(false);
                vScroll.setVisible(true);
                var delta = content.get().getHeight() - getHeight();
                vScroll.max().set(delta);
                vScroll.step().set(delta / 10.0f);
                vScroll.value().addChangeListener((observable, oldValue, newValue) -> content.get().relocate(content.get().getLayoutX(), -vScroll.value().getFloat()));
            } else {
                vScroll.setDisabled(true);
                vScroll.setVisible(false);
                vScroll.max().set(1);
            }
        } else {
            hScroll.setDisabled(true);
            hScroll.max().set(1);
            vScroll.setDisabled(true);
            vScroll.max().set(1);
        }
    }

    public void setContent(Node content) {
        this.content.setValue(content);
        center().setValue(content);
    }

    public ObservableValue<Node> content() {
        return content.toUnmodifiable();
    }

    public ObservableDoubleValue xOffset(){
        return hScroll.value().toUnmodifiable();
    }

    public ObservableDoubleValue yOffset(){
        return vScroll.value().toUnmodifiable();
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        if (content.isPresent()) {
            var c = content.get();
            layoutInArea(c, hScroll.disabled().get() ? 0 : hScroll.value().getFloat(), vScroll.disabled().get() ? 0 : vScroll.value().getFloat(),
                    Utils.prefWidth(c),
                    Utils.prefHeight(c), 0/*ignore baseline*/,
                    getNodeMargin(c),
                    Pos.HPos.CENTER,
                    Pos.VPos.CENTER);
        }
    }
}
