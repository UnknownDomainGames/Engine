package engine.gui.control;

import com.github.mouse0w0.observable.value.MutableDoubleValue;
import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.SimpleMutableDoubleValue;
import engine.gui.shape.Rect;
import engine.util.Color;
import org.joml.Vector2f;

public class ProgressBar extends Control {
    private Rect progressRect = new Rect();

    private Rect back = new Rect();

    private MutableDoubleValue max = new SimpleMutableDoubleValue(1);
    private MutableDoubleValue progress = new SimpleMutableDoubleValue(0);

    private final MutableDoubleValue barLength = new SimpleMutableDoubleValue(150);
    private final MutableDoubleValue barThickness = new SimpleMutableDoubleValue(15);

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
        backgroundColor().set(Color.WHITE);
        progressColor().set(Color.GREEN);
    }

    public MutableDoubleValue value() {
        return progress;
    }

    public MutableDoubleValue max() {
        return max;
    }

    public MutableDoubleValue barThickness() {
        return barThickness;
    }

    public MutableDoubleValue barLength() {
        return barLength;
    }

    public void rebuild() {
        if (progress.get() > max.get()) {
            progress.set(max.get());
        } else if (progress.get() < 0) {
            progress.set(0);
        }
        resizeBack(barLength.get(), barThickness.get());
        resizeProgressRect(barLength.get() * progress.get() / max.get(), barThickness.get());
        progressRect.relocate(0, 0);
    }

    private void resizeBack(double width, double height) {
        back.rectSize().set(new Vector2f((float) width, (float) height));
    }

    private void resizeProgressRect(double width, double height) {
        progressRect.rectSize().set(new Vector2f((float) width, (float) height));
    }

    public MutableObjectValue<Color> backgroundColor() {
        return back.fillColor();
    }

    public MutableObjectValue<Color> progressColor() {
        return progressRect.fillColor();
    }
}
