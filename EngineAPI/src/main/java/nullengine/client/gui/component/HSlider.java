package nullengine.client.gui.component;

import com.github.mouse0w0.observable.value.*;
import nullengine.client.gui.Region;
import nullengine.client.gui.event.MouseEvent;
import nullengine.client.gui.shape.Rect;
import nullengine.event.Event;
import nullengine.util.Color;
import org.joml.Vector2f;


public class HSlider extends Region {
    private Rect slider = new Rect();

    private Rect back = new Rect();

    private MutableDoubleValue min = new SimpleMutableDoubleValue(0);
    private MutableDoubleValue max = new SimpleMutableDoubleValue(1);
    private MutableDoubleValue value = new SimpleMutableDoubleValue(0);

    private final MutableFloatValue sliderLength = new SimpleMutableFloatValue();
    private final MutableFloatValue sliderThickness = new SimpleMutableFloatValue();
    private final MutableFloatValue step = new SimpleMutableFloatValue(0.01f);

    private boolean select = false;

    public HSlider() {
        value.addChangeListener((ob, o, n) -> rebuild());
        min.addChangeListener((observable, oldValue, newValue) -> {
            if(newValue > max.get()){
                min.set(oldValue);
            }
            else{
                resizeSlider(sliderLength.get() * (float)(step.get() / (max.get() - min.get())), sliderThickness.get());
                rebuild();
            }
        });
        max.addChangeListener((observable, oldValue, newValue) -> {
            if(newValue < min.get()){
                max.set(oldValue);
            }
            else{
                resizeSlider(sliderLength.get() * (float)(step.get() / (max.get() - min.get())), sliderThickness.get());
                rebuild();
            }
        });
        step.addChangeListener((observable, oldValue, newValue) -> {
            if(newValue == 0){
                step.set(oldValue);
            }
        });
        sliderLength.addChangeListener((observable, oldValue, newValue) -> {
            resizeBack(newValue, sliderThickness.get());
            resizeSlider(newValue * (float)(step.get() / (max.get() - min.get())), sliderThickness.get());
        });
        sliderThickness.addChangeListener((observable, oldValue, newValue) -> {
            resizeBack(sliderLength.get(), newValue);
            resizeSlider(sliderLength.get() * (float)(step.get() / (max.get() - min.get())), newValue);
        });
        this.getChildren().addAll(back, slider);
        backBg().setValue(Color.BLUE);
        sliderBg().setValue(Color.WHITE);
    }

    public MutableDoubleValue value() {
        return value;
    }

    public MutableDoubleValue max() {
        return max;
    }

    public MutableDoubleValue min() {
        return min;
    }

    public MutableFloatValue sliderThickness() {
        return sliderThickness;
    }

    public MutableFloatValue sliderLength() {
        return sliderLength;
    }

    public MutableFloatValue step() {
        return step;
    }

    public void rebuild() {
        if (value.get() > max.get()) {
            value.set(max.get());
        } else if (value.get() < min.get()) {
            value.set(min.get());
        }
        slider.x().set((float) ((back.width().get() - slider.width().get()) * (value.get() / (max.get() - min.get()))));
        slider.y().set(back.y().get());
    }

    @Override
    public void onClick(MouseEvent.MouseClickEvent e) {
        super.onClick(e);
        if (e.getPosX() > slider.x().get() + slider.width().get()) {
            value.set(value.getValue() + step.get());
        } else if (e.getPosX() < slider.x().get()) {
            value.set(value.getValue() - step.get());
        }
        if (slider.contains(e.getPosX(), e.getPosY()))
            select = true;
    }

    @Override
    public void handleEvent(Event event) {
        super.handleEvent(event);
        if (event instanceof MouseEvent.MouseMoveEvent && select) {
            var event1 = (MouseEvent.MouseMoveEvent) event;
            if ((event1.getNewPosX() - x().get() - slider.x().get()) / width().get() > step.get() * 0.9) {
                value.set(value.getValue() + step.get());
            } else if ((slider.x().get() - event1.getNewPosX() + x().get()) / width().get() > step.get() * 0.9) {
                value.set(value.getValue() - step.get());
            }
        } else if (event instanceof MouseEvent.MouseReleasedEvent) {
            select = false;
        } else if (event instanceof MouseEvent.MouseLeaveEvent) {
            //select = false;
        } else if (event instanceof MouseEvent.MouseHoldEvent) {
            var event1 = (MouseEvent.MouseHoldEvent) event;
            if ((event1.getPosX() - x().get() - slider.x().get()) / width().get() > step.get() * 0.9) {
                value.set(value.getValue() + step.get());
            } else if ((slider.x().get() - event1.getPosX() + x().get()) / width().get() > step.get() * 0.9) {
                value.set(value.getValue() - step.get());
            }
        }
    }

    public void resizeBack(float width, float height) {
        back.rectSize().setValue(new Vector2f(width,height));
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
