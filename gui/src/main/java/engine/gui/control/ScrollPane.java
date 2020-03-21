package engine.gui.control;

import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.ObservableDoubleValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import engine.gui.Node;
import engine.gui.layout.BorderPane;
import engine.gui.misc.HPos;
import engine.gui.misc.Pos;
import engine.gui.misc.VPos;
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
        bottom().set(hScroll);
        right().set(vScroll);
        setAlignment(hScroll, Pos.BOTTOM_LEFT);
        setAlignment(vScroll, Pos.TOP_RIGHT);
        content.addChangeListener((observable, oldValue, newValue) -> {
            if (oldValue != null) getChildren().remove(oldValue);
            if (newValue != null) getChildren().add(newValue);
            update();
            if (newValue != null) {
                newValue.width().addChangeListener((observable1, o, n) -> update());
                newValue.height().addChangeListener((observable1, o, n) -> update());
            }
        });
        width().addChangeListener((observable, oldValue, newValue) -> {
            hScroll.sliderLength().set(getWidth());
            update();
        });
        height().addChangeListener((observable, oldValue, newValue) -> {
            vScroll.sliderLength().set(getHeight());
            update();
        });
        hScroll.value().addChangeListener((observable, oldValue, newValue) -> needsLayout());
        vScroll.value().addChangeListener((observable, oldValue, newValue) -> needsLayout());
    }

    private void update() {
        Node content = getContent();
        if (content != null) {
            if (content.getWidth() > getWidth()) {
                hScroll.setDisabled(false);
                vScroll.setVisible(true);
                var delta = content.getWidth() - getWidth();
                hScroll.max().set(delta);
                hScroll.step().set(getWidth() / content.getWidth() * delta);
            } else {
                hScroll.setDisabled(true);
                hScroll.setVisible(false);
                hScroll.max().set(1);
            }
            if (content.getHeight() > getHeight()) {
                vScroll.setDisabled(false);
                vScroll.setVisible(true);
                var delta = content.getHeight() - getHeight();
                vScroll.max().set(delta);
                vScroll.step().set(getHeight() / content.getHeight() * delta);
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

    public MutableObjectValue<Node> content() {
        return content;
    }

    public Node getContent() {
        return content.get();
    }

    public void setContent(Node content) {
        this.content.set(content);
    }

    public ObservableDoubleValue xOffset() {
        return hScroll.value().toUnmodifiable();
    }

    public ObservableDoubleValue yOffset() {
        return vScroll.value().toUnmodifiable();
    }

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        var content = getContent();
        if (content != null) {
            layoutInArea(content, hScroll.disabled().get() ? 0 : hScroll.value().getFloat(), vScroll.disabled().get() ? 0 : -vScroll.value().getFloat(),
                    Utils.prefWidth(content),
                    Utils.prefHeight(content), 0/*ignore baseline*/,
                    getNodeMargin(content),
                    HPos.CENTER,
                    VPos.CENTER);
//            update();
        }
    }
}
