package unknowndomain.engine.client.gui.component;

import com.github.mouse0w0.lib4j.observable.value.*;

public class Range extends Control {
    private MutableFloatValue max = new SimpleMutableFloatValue();
    private MutableFloatValue min = new SimpleMutableFloatValue();
    private float value = 0;
    private final MutableFloatValue sliderX = new SimpleMutableFloatValue();
    private final MutableFloatValue sliderY = new SimpleMutableFloatValue();
    private final MutableValue<Slider> slider = new SimpleMutableObjectValue<>();

    private final float perMove = 0;

    public Range() {
        super();
    }

    public Range(float min, float max) {
        this();
        this.setRange(min, max);
    }

    public void setRange(float min, float max) {
        max().set(min < max ? max : min);
        min().set(min < max ? min : max);
    }

    public void setValue(float value) {
        this.value = value;
    }

    public MutableFloatValue max() {
        return max;
    }

    public MutableFloatValue min() {
        return min;
    }

    public MutableValue<Slider> slider() {
        return slider;
    }

    public MutableFloatValue getSliderX() {
        return sliderX;
    }

    public MutableFloatValue getSliderY() {
        return sliderY;
    }

    public MutableValue<Slider> getSlider() {
        return slider;
    }

    private void rebuildSlider() {
        if (value > max.get()) {
            value = max.get();
        } else if (value < min.get()) {
            value = min.get();
        }
    }


}
