package engine.gui.control;

import com.github.mouse0w0.observable.value.*;
import engine.gui.shape.Rect;
import engine.util.Color;
import org.joml.Vector2f;

public class ProgressBar extends Control {
    private Rect progressRect = new Rect();

    private Rect back = new Rect();

    private MutableDoubleValue max = new SimpleMutableDoubleValue(1);
    private MutableDoubleValue progress = new SimpleMutableDoubleValue(0);

    private final MutableFloatValue barLength = new SimpleMutableFloatValue(150);
    private final MutableFloatValue barThickness = new SimpleMutableFloatValue(15);

    public ProgressBar() {
        progress.addChangeListener((ob, o, n) -> rebuild());
        max.addChangeListener((observable, oldValue, newValue) -> {
            if (newValue < 0) {
                max.set(oldValue);
            } else {
                rebuild();
            }
        });
        barLength.addChangeListener((observable, oldValue, newValue) -> rebuild());
        barThickness.addChangeListener((observable, oldValue, newValue) -> rebuild());
        this.getChildren().addAll(back, progressRect);
        backgroundColor().setValue(Color.WHITE);
        progressColor().setValue(Color.GREEN);
    }

    public MutableDoubleValue value() {
        return progress;
    }

    public MutableDoubleValue max() {
        return max;
    }

    public MutableFloatValue barThickness() {
        return barThickness;
    }

    public MutableFloatValue barLength() {
        return barLength;
    }

    public void rebuild() {
        if (progress.get() > max.get()) {
            progress.set(max.get());
        } else if (progress.get() < 0) {
            progress.set(0);
        }
        resizeBack(barLength.get(), barThickness.get());
        resizeProgressRect((float) (barLength.get() * progress.get() / max.get()), barThickness.get());
        progressRect.relocate(0,0);
    }

    private void resizeBack(float width, float height) {
        back.rectSize().setValue(new Vector2f(width, height));
    }

    private void resizeProgressRect(float width, float height) {
        progressRect.rectSize().setValue(new Vector2f(width, height));
    }

    public MutableValue<Color> backgroundColor() {
        return back.fillColor();
    }

    public MutableValue<Color> progressColor() {
        return progressRect.fillColor();
    }
}
