package nullengine.client.gui.layout;

import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.ObservableValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import nullengine.client.gui.Node;
import nullengine.client.gui.component.HSlider;
import nullengine.client.gui.component.VSlider;
import nullengine.client.gui.misc.Pos;
import nullengine.client.gui.util.Utils;

public class ScrollPane extends BorderPane {
    private final VSlider vScroll;
    private final HSlider hScroll;

    private final MutableObjectValue<Node> content = new SimpleMutableObjectValue<>();

    public ScrollPane(){
        super();
        vScroll = new VSlider();
        hScroll = new HSlider();
        vScroll.sliderThickness().set(20f);
        hScroll.sliderThickness().set(20f);
        vScroll.step().set(1);
        hScroll.step().set(1);
        bottom().setValue(hScroll);
        right().setValue(vScroll);
        content.addChangeListener((observable, oldValue, newValue) -> {
            update();
            if(newValue != null){
                newValue.width().addChangeListener((observable1, oldValue1, newValue1) -> update());
                newValue.height().addChangeListener((observable1, oldValue1, newValue1) -> update());
            }
        });
        width().addChangeListener((observable, oldValue, newValue) -> {
            hScroll.sliderLength().set(width().get());
            update();
        });
        height().addChangeListener((observable, oldValue, newValue) -> {
            vScroll.sliderLength().set(height().get());
            update();
        });
    }

    private void update(){
        if(content.isPresent()){
            if(content.getValue().width().get() > width().get()){
                hScroll.disabled().set(false);
                vScroll.visible().set(true);
                var delta = content.getValue().width().get() - width().get();
                hScroll.max().set(delta);
                hScroll.step().set(delta / 10.0f);
                hScroll.value().addChangeListener((observable, oldValue, newValue) -> content.getValue().relocate(-hScroll.value().getFloat(), content.getValue().y().get()));
            }
            else{
                hScroll.disabled().set(true);
                hScroll.visible().set(false);
                hScroll.max().set(1);
            }
            if(content.getValue().height().get() > height().get()){
                vScroll.disabled().set(false);
                vScroll.visible().set(true);
                var delta = content.getValue().height().get() - height().get();
                vScroll.max().set(delta);
                vScroll.step().set(delta / 10.0f);
                vScroll.value().addChangeListener((observable, oldValue, newValue) -> content.getValue().relocate(content.getValue().x().get(), -vScroll.value().getFloat()));
            }
            else{
                vScroll.disabled().set(true);
                vScroll.visible().set(false);
                vScroll.max().set(1);
            }
        }
        else{
            hScroll.disabled().set(true);
            hScroll.max().set(1);
            vScroll.disabled().set(true);
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

    @Override
    protected void layoutChildren() {
        super.layoutChildren();
        if(content.isPresent()){
            var c = content.getValue();
            layoutInArea(c, hScroll.disabled().get() ? 0 : hScroll.value().getFloat(), vScroll.disabled().get() ? 0 : vScroll.value().getFloat(),
                    Utils.prefWidth(c),
                    Utils.prefHeight(c), 0/*ignore baseline*/,
                    getNodeMargin(c),
                    Pos.HPos.CENTER,
                    Pos.VPos.CENTER);
        }
    }
}
