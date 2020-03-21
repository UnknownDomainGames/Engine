package engine.gui.control;

import com.github.mouse0w0.observable.value.*;
import engine.gui.Region;
import engine.gui.input.MouseActionEvent;
import engine.gui.input.MouseEvent;
import engine.gui.shape.Rect;
import engine.util.Color;
import org.joml.Vector2f;


public class HSlider extends Region {
    private Rect slider = new Rect();

    private Rect back = new Rect();

    private MutableDoubleValue min = new SimpleMutableDoubleValue(0);
    private MutableDoubleValue max = new SimpleMutableDoubleValue(1);
    private MutableDoubleValue value = new SimpleMutableDoubleValue(0);

    private final MutableFloatValue sliderLength = new SimpleMutableFloatValue(150);
    private final MutableFloatValue sliderThickness = new SimpleMutableFloatValue(15);
    private final MutableFloatValue step = new SimpleMutableFloatValue(0.01f);

    private boolean select = false;

    public HSlider() {
        value.addChangeListener((ob, o, n) -> rebuild());
        min.addChangeListener((observable, oldValue, newValue) -> {
            if (newValue > max.get()) {
                min.set(oldValue);
            } else {
                rebuild();
            }
        });
        max.addChangeListener((observable, oldValue, newValue) -> {
            if (newValue < min.get()) {
                max.set(oldValue);
            } else {
                rebuild();
            }
        });
        step.addChangeListener((observable, oldValue, newValue) -> {
            if (newValue == 0) {
                step.set(oldValue);
            } else {
                resizeSlider(sliderLength.get() * (float) (step.get() / (max.get() - min.get())), sliderThickness.get());
            }
        });
        sliderLength.addChangeListener((observable, oldValue, newValue) -> rebuild());
        sliderThickness.addChangeListener((observable, oldValue, newValue) -> rebuild());
        this.getChildren().addAll(back, slider);
        backBg().set(Color.BLUE);
        sliderBg().set(Color.WHITE);

        addEventHandler(MouseActionEvent.MOUSE_PRESSED, this::onMousePressed);
        addEventHandler(MouseActionEvent.MOUSE_RELEASED, this::onMouseReleased);
        addEventHandler(MouseEvent.MOUSE_MOVED, this::onMouseMove);
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
        resizeBack(sliderLength.get(), sliderThickness.get());
        resizeSlider(sliderLength.get() * (float) (step.get() / (max.get() - min.get())), sliderThickness.get());
        slider.setLayoutX((float) ((back.rectSize().get().x() - slider.rectSize().get().x()) * ((value.get() - min.get()) / (max.get() - min.get()))));
        slider.setLayoutY(back.getLayoutY());
    }

    private void onMousePressed(MouseActionEvent e) {
        if (e.getX() > slider.getLayoutX() + slider.getWidth()) {
            value.set(value.get() + step.get());
        } else if (e.getX() < slider.getLayoutX()) {
            value.set(value.get() - step.get());
        }
        if (slider.contains(e.getX(), e.getY()))
            select = true;
    }

    private void onMouseMove(MouseEvent event) {
        if (!select) return;
        if ((event.getX() - slider.getLayoutX() - slider.rectSize().get().x()) / getWidth() > step.get() / (max.get() - min.get()) * 0.9) {
            value.set(value.get() + step.get());
        } else if ((slider.getLayoutX() - event.getX()) / getWidth() > step.get() / (max.get() - min.get()) * 0.9) {
            value.set(value.get() - step.get());
        }
    }

    private void onMouseReleased(MouseActionEvent event) {
        select = false;
    }

    private void resizeBack(float width, float height) {
        back.rectSize().set(new Vector2f(width, height));
    }

    private void resizeSlider(float width, float height) {
        slider.rectSize().set(new Vector2f(width, height));
    }

    public MutableObjectValue<Color> backBg() {
        return back.fillColor();
    }

    public MutableObjectValue<Color> sliderBg() {
        return slider.fillColor();
    }

}
