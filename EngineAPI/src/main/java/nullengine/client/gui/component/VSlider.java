package nullengine.client.gui.component;

import com.github.mouse0w0.observable.value.*;
import nullengine.client.gui.Region;
import nullengine.client.gui.event.old.MouseEvent_;
import nullengine.client.gui.shape.Rect;
import nullengine.event.Event;
import nullengine.util.Color;
import org.joml.Vector2f;

public class VSlider extends Region {

    private Rect slider = new Rect();

    private Rect back = new Rect();

    private MutableDoubleValue min = new SimpleMutableDoubleValue(0);
    private MutableDoubleValue max = new SimpleMutableDoubleValue(1);
    private MutableDoubleValue value = new SimpleMutableDoubleValue(0);

    private final MutableFloatValue sliderLength = new SimpleMutableFloatValue();
    private final MutableFloatValue sliderThickness = new SimpleMutableFloatValue();
    private final MutableFloatValue step = new SimpleMutableFloatValue(0.01f);
    /**
     * VSlider is designed to be up-to-down as default. this variable defines as if this slider uses down-to-up as min to max or not
     */
    private final MutableBooleanValue flip = new SimpleMutableBooleanValue();

    private boolean select = false;

    public VSlider() {
        value.addChangeListener((ob, o, n) -> rebuild());
        min.addChangeListener((observable, oldValue, newValue) -> {
            if (newValue > max.get()) {
                min.set(oldValue);
            } else {
                resizeSlider(sliderThickness.get(), sliderLength.get() * (float) (step.get() / (max.get() - min.get())));
                rebuild();
            }
        });
        max.addChangeListener((observable, oldValue, newValue) -> {
            if (newValue < min.get()) {
                max.set(oldValue);
            } else {
                resizeSlider(sliderThickness.get(), sliderLength.get() * (float) (step.get() / (max.get() - min.get())));
                rebuild();
            }
        });
        step.addChangeListener((observable, oldValue, newValue) -> {
            if (newValue == 0) {
                step.set(oldValue);
            } else {
                resizeSlider(sliderThickness.get(), sliderLength.get() * (float) (step.get() / (max.get() - min.get())));
            }
        });
        sliderLength.addChangeListener((observable, oldValue, newValue) -> {
            resizeBack(sliderThickness.get(), newValue);
            resizeSlider(sliderThickness.get(), newValue * (float) (step.get() / (max.get() - min.get())));
        });
        sliderThickness.addChangeListener((observable, oldValue, newValue) -> {
            resizeBack(newValue, sliderLength.get());
            resizeSlider(newValue, sliderLength.get() * (float) (step.get() / (max.get() - min.get())));
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

    public MutableBooleanValue flip() {
        return flip;
    }

    public void rebuild() {
        if (value.get() > max.get()) {
            value.set(max.get());
        } else if (value.get() < min.get()) {
            value.set(min.get());
        }
        slider.x().set(back.x().get());
        slider.y().set((float) (((back.height().get() - slider.height().get()) * (flip.get() ? 1 - (value.get() / (max.get() - min.get())) : (value.get() / (max.get() - min.get()))))));
    }

    @Override
    public void onClick_(MouseEvent_.MouseClickEvent e) {
        super.onClick_(e);
        if (e.getPosY() > slider.y().get() + slider.width().get()) {
            value.set(value.getValue() + step.get() * (flip.get() ? -1 : 1));
        } else if (e.getPosY() < slider.y().get()) {
            value.set(value.getValue() - step.get() * (flip.get() ? -1 : 1));
        }
        if (slider.contains(e.getPosX(), e.getPosY()))
            select = true;
    }

    @Override
    public void handleEvent(Event event) {
        super.handleEvent(event);
        if (event instanceof MouseEvent_.MouseMoveEvent && select) {
            var event1 = (MouseEvent_.MouseMoveEvent) event;
            var ry = relativePos((float) event1.getNewPosX(), (float) event1.getNewPosY()).getRight();
            if ((ry - slider.y().get()) / height().get() > step.get() / (max.get() - min.get()) * 0.9) {
                value.set(value.getValue() + step.get() * (flip.get() ? -1 : 1));
            } else if ((slider.y().get() - ry) / height().get() > step.get() / (max.get() - min.get()) * 0.9) {
                value.set(value.getValue() - step.get() * (flip.get() ? -1 : 1));
            }
        } else if (event instanceof MouseEvent_.MouseReleasedEvent) {
            select = false;
        } else if (event instanceof MouseEvent_.MouseLeaveEvent) {
            //select = false;
        } else if (event instanceof MouseEvent_.MouseHoldEvent) {
            var event1 = (MouseEvent_.MouseHoldEvent) event;
            if ((event1.getPosY() - y().get() - slider.y().get()) / height().get() > step.get() / (max.get() - min.get()) * 0.9) {
                value.set(value.getValue() + step.get() * (flip.get() ? -1 : 1));
            } else if ((slider.y().get() - event1.getPosY() + y().get()) / height().get() > step.get() / (max.get() - min.get()) * 0.9) {
                value.set(value.getValue() - step.get() * (flip.get() ? -1 : 1));
            }
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
