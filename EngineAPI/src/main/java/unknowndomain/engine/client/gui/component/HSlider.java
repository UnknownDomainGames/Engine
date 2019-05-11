package unknowndomain.engine.client.gui.component;

import com.github.mouse0w0.lib4j.observable.value.MutableFloatValue;
import com.github.mouse0w0.lib4j.observable.value.MutableValue;
import com.github.mouse0w0.lib4j.observable.value.SimpleMutableFloatValue;
import com.github.mouse0w0.lib4j.observable.value.SimpleMutableObjectValue;
import unknowndomain.engine.client.asset.AssetPath;
import unknowndomain.engine.client.gui.misc.Background;
import unknowndomain.engine.util.Color;


public class HSlider extends Control {
    private Range range = new Range();

    private ImageButton leftBtn = new ImageButton();
    private ImageButton rightBtn = new ImageButton();

    private ImageButton back = new ImageButton();

    private MutableFloatValue value = new SimpleMutableFloatValue();

    private float max = 0;
    private float min = 0;

    private float preMove = 0;

    public HSlider() {
        back.setOnClick(e -> {
            if (e.getPosX() > range.x().get()) {
                value.set(value.getValue() + preMove);
            } else if (e.getPosX() < range.x().get()) {
                value.set(value.getValue() - preMove);
            }
        });
        value.addChangeListener((ob, o, n) -> rebuild());
        this.getChildren().addAll(back, leftBtn, rightBtn, range);
    }


    public MutableFloatValue value() {
        return value;
    }

    public float getMax() {
        return max;
    }

    public float getMin() {
        return min;
    }

    public float getPreMove() {
        return preMove;
    }

    public void setMax(float max) {
        this.max = max;
    }

    public void setMin(float min) {
        this.min = min;
    }

    public void setPreMove(float preMove) {
        this.preMove = preMove;
    }

    public void rebuild() {
        if (value.get() > max) {
            value.set(max);
        } else if (value.get() < min) {
            value.set(min);
        }

        leftBtn.x().set(back.x().getValue() - leftBtn.prefWidth() / 2);
        leftBtn.y().set(back.y().getValue() + back.prefHeight() / 2 - leftBtn.prefHeight() / 2);

        rightBtn.x().set(back.x().get() + back.prefWidth() - rightBtn.prefWidth() / 2);
        rightBtn.y().set(back.y().get() + back.prefHeight() / 2 - rightBtn.prefHeight() / 2);

        range.x().set(back.x().get() + back.prefWidth()/2 - range.prefWidth() / 2);
        range.y().set(back.y().get() + back.prefHeight() / 2 - range.prefHeight() / 2);
        System.out.println(back.x().get());
    }

    public MutableValue<AssetPath> leftBtnBg() {
        return leftBtn.buttonBackground();
    }

    public MutableValue<AssetPath> leftBtnBgDisable() {
        return leftBtn.disabledBackground();
    }

    public MutableValue<AssetPath> rightBtnBg() {
        return rightBtn.buttonBackground();
    }

    public MutableValue<AssetPath> rightBtnBgDisable() {
        return rightBtn.disabledBackground();
    }

    public MutableValue<AssetPath> backBg() {
        return back.buttonBackground();
    }
}
