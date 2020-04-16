package engine.gui.control;

import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.ObservableDoubleValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import engine.gui.Node;
import engine.gui.Parent;
import engine.gui.layout.BorderPane;
import engine.gui.misc.HPos;
import engine.gui.misc.Orientation;
import engine.gui.misc.Pos;
import engine.gui.misc.VPos;

public class ScrollPane extends BorderPane {
    private final ScrollBar vScroll;
    private final ScrollBar hScroll;

    private final MutableObjectValue<Node> content = new SimpleMutableObjectValue<>();

    public ScrollPane() {
        super();
        vScroll = new ScrollBar(Orientation.VERTICAL);
        hScroll = new ScrollBar(Orientation.HORIZONTAL);
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
                hScroll.step().set(getWidth() / content.getWidth());
            } else {
                hScroll.setDisabled(true);
                hScroll.setVisible(false);
            }
            if (content.getHeight() > getHeight()) {
                vScroll.setDisabled(false);
                vScroll.setVisible(true);
                vScroll.step().set(getHeight() / content.getHeight());
            } else {
                vScroll.setDisabled(true);
                vScroll.setVisible(false);
            }
        } else {
            hScroll.setDisabled(true);
            vScroll.setDisabled(true);
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
            var x = hScroll.disabled().get() ? 0 : hScroll.value().getFloat() * (content.getWidth() - getWidth());
            var y = vScroll.disabled().get() ? 0 : -vScroll.value().getFloat() * (content.getHeight() - getHeight());
            layoutInArea(content, x, y,
                    Parent.prefWidth(content),
                    Parent.prefHeight(content), 0/*ignore baseline*/,
                    getNodeMargin(content),
                    HPos.CENTER,
                    VPos.CENTER);
        }
    }
}
