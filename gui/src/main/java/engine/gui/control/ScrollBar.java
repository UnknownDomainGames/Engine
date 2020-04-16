package engine.gui.control;

import com.github.mouse0w0.observable.value.*;
import engine.gui.Region;
import engine.gui.input.MouseActionEvent;
import engine.gui.input.MouseEvent;
import engine.gui.misc.Orientation;
import engine.gui.shape.Rect;
import engine.util.Color;
import org.joml.Vector2f;

public class ScrollBar extends Region {
    private Rect slider = new Rect();

    private Rect back = new Rect();

    private MutableDoubleValue min = new SimpleMutableDoubleValue(0);
    private MutableDoubleValue max = new SimpleMutableDoubleValue(1);
    private MutableDoubleValue value = new SimpleMutableDoubleValue(0);

    private final MutableFloatValue sliderLength = new SimpleMutableFloatValue(150);
    private final MutableFloatValue sliderThickness = new SimpleMutableFloatValue(15);
    private final MutableFloatValue step = new SimpleMutableFloatValue(0.01f);

    private final MutableObjectValue<Orientation> orientation = new SimpleMutableObjectValue<>(Orientation.HORIZONTAL);

    private boolean select = false;

    public ScrollBar(Orientation orientation) {
        value.addChangeListener((ob, o, n) -> rebuild());
        this.orientation.addChangeListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                rebuild();
            }
        });
        step.addChangeListener((observable, oldValue, newValue) -> {
            if (newValue <= 0) {
                step.set(oldValue);
            } else {
                rebuild();
            }
        });
        sliderLength.addChangeListener((observable, oldValue, newValue) -> rebuild());
        this.getChildren().addAll(back, slider);
        this.orientation.set(orientation);
        backBg().set(Color.BLUE);
        sliderBg().set(Color.WHITE);

        addEventHandler(MouseActionEvent.MOUSE_PRESSED, this::onMousePressed);
        addEventHandler(MouseActionEvent.MOUSE_RELEASED, this::onMouseReleased);
        addEventHandler(MouseEvent.MOUSE_MOVED, this::onMouseMove);
    }

    public MutableDoubleValue value() {
        return value;
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

        var knobLength = sliderLength.get() * (float) (step.get() / (max.get() - min.get()));
        float knobOffset;
        if (orientation.get() == Orientation.HORIZONTAL) {
            knobOffset = (float) ((back.rectSize().get().x() - slider.rectSize().get().x()) * ((value.get() - min.get()) / (max.get() - min.get())));
            resizeBack(sliderLength.get(), sliderThickness.get());
            resizeSlider(knobLength, sliderThickness.get());
            slider.setLayoutX(knobOffset);
            slider.setLayoutY(back.getLayoutY());
        } else {
            knobOffset = (float) ((back.rectSize().get().y() - slider.rectSize().get().y()) * ((value.get() - min.get()) / (max.get() - min.get())));
            resizeBack(sliderThickness.get(), sliderLength.get());
            resizeSlider(sliderThickness.get(), knobLength);
            slider.setLayoutX(back.getLayoutX());
            slider.setLayoutY(knobOffset);
        }
    }

    private float anchorX = Float.NaN;
    private float anchorY = Float.NaN;

    private void onMousePressed(MouseActionEvent e) {
        if (e.getTarget().equals(back)) {
            if (orientation.get() == Orientation.HORIZONTAL) {
                if (e.getX() > slider.getLayoutX() + slider.getWidth()) {
                    value.set(value.get() + step.get());
                } else if (e.getX() < slider.getLayoutX()) {
                    value.set(value.get() - step.get());
                }
            } else {
                if (e.getY() > slider.getLayoutY() + slider.getHeight()) {
                    value.set(value.get() + step.get());
                } else if (e.getY() < slider.getLayoutY()) {
                    value.set(value.get() - step.get());
                }
            }
            if (slider.contains(e.getX(), e.getY())) {
                anchorX = e.getX();
                anchorY = e.getY();
                select = true;
            }
        }
    }

    private void onMouseMove(MouseEvent event) {
        if (!select) return;
        if (orientation.get() == Orientation.HORIZONTAL) {
            var deltaPos = event.getX() - anchorX;
            var dx = deltaPos / (back.rectSize().get().x() - slider.rectSize().get().x());
            value.set(value.get() + dx);
        } else {
            var deltaPos = event.getY() - anchorY;
            var dy = deltaPos / (back.rectSize().get().y() - slider.rectSize().get().y());
            value.set(value.get() + dy);
        }
        anchorX = event.getX();
        anchorY = event.getY();
    }

    private void onMouseReleased(MouseActionEvent event) {
        anchorX = Float.NaN;
        anchorY = Float.NaN;
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
