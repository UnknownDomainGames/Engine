package nullengine.client.gui.component;

import com.github.mouse0w0.observable.value.MutableDoubleValue;
import com.github.mouse0w0.observable.value.MutableValue;
import com.github.mouse0w0.observable.value.SimpleMutableDoubleValue;
import nullengine.client.gui.Region;
import nullengine.client.gui.event.MouseEvent;
import nullengine.client.gui.misc.Background;
import nullengine.client.gui.shape.Rect;
import nullengine.event.Event;
import nullengine.util.Color;
import org.joml.Vector2f;

public class VSlider extends Region {

    private Rect slider = new Rect();

    private Rect back = new Rect();

    private MutableDoubleValue value = new SimpleMutableDoubleValue(0);

    private double preMove = 0;

    private boolean select = false;

    public VSlider() {
        value.addChangeListener((ob, o, n) -> rebuild());
        this.getChildren().addAll(back, slider);
        backBg().setValue(Color.BLUE);
        sliderBg().setValue(Color.WHITE);
    }

    public MutableDoubleValue value() {
        return value;
    }

    public void setPreMove(double preMove) {
        this.preMove = preMove;
    }

    public void rebuild() {
        if (value.get() > 1) {
            value.set(1);
        } else if (value.get() < 0) {
            value.set(0);
        }
        slider.x().set(back.x().get());
        slider.y().set((float) (((back.height().get() - slider.height().get()) * value.get())));
    }

    @Override
    public void onClick(MouseEvent.MouseClickEvent e) {
        super.onClick(e);
        if (e.getPosY() > slider.y().get() + slider.prefWidth()) {
            value.set(value.getValue() + preMove);
        } else if (e.getPosY() < slider.y().get()) {
            value.set(value.getValue() - preMove);
        }
        if (slider.contains(e.getPosX(), e.getPosY()))
            select = true;
    }

    @Override
    public void handleEvent(Event event) {
        super.handleEvent(event);
        if (event instanceof MouseEvent.MouseMoveEvent && select) {
            if ((((MouseEvent.MouseMoveEvent) event).getNewPosY() - y().get() - slider.y().get()) / prefWidth() > preMove * 0.9) {
                value.set(value.getValue() + preMove);
            } else if ((slider.y().get() - ((MouseEvent.MouseMoveEvent) event).getNewPosY() + y().get()) / prefWidth() > preMove * 0.9) {
                value.set(value.getValue() - preMove);
            }
        } else if (event instanceof MouseEvent.MouseReleasedEvent) {
            select = false;
        } else if (event instanceof MouseEvent.MouseLeaveEvent) {
            //select = false;
        } else if (event instanceof MouseEvent.MouseHoldEvent) {

        }
    }


    public void resizeBack(float width, float height) {
        back.rectSize().setValue(new Vector2f(width, height));
        rebuild();
    }

    public void resizeSlider(float width, float height) {
        slider.rectSize().setValue(new Vector2f(width, height));
        rebuild();
    }

    public MutableValue<Color> backBg() {
        return back.fillColor();
    }

    public MutableValue<Color> sliderBg() {
        return slider.fillColor();
    }

}
