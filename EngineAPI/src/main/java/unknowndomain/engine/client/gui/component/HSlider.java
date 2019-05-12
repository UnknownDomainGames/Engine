package unknowndomain.engine.client.gui.component;

import com.github.mouse0w0.lib4j.observable.value.MutableFloatValue;
import com.github.mouse0w0.lib4j.observable.value.MutableValue;
import com.github.mouse0w0.lib4j.observable.value.SimpleMutableFloatValue;
import com.github.mouse0w0.lib4j.observable.value.SimpleMutableObjectValue;
import unknowndomain.engine.client.asset.AssetPath;
import unknowndomain.engine.client.gui.event.MouseEvent;
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
        value.addChangeListener((ob, o, n) -> rebuild());
        backBg().addChangeListener((ob, o, n) -> reSize());
        this.getChildren().addAll(back, range, leftBtn, rightBtn);
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
        System.out.println(value.get() + "v");
        System.out.println(preMove);
        range.x().set((back.prefWidth()) / (range.maxX().get() - range.minX().get()) * value.get());
        range.y().set(back.y().get() + back.prefHeight() / 2 - range.prefHeight() / 2);
    }

    public void reSize() {
        leftBtn.x().set(back.x().getValue() - leftBtn.prefWidth() / 2);
        leftBtn.y().set(back.y().getValue() + back.prefHeight() / 2 - leftBtn.prefHeight() / 2);

        rightBtn.x().set(back.x().get() + back.prefWidth() - rightBtn.prefWidth() / 2);
        rightBtn.y().set(back.y().get() + back.prefHeight() / 2 - rightBtn.prefHeight() / 2);
        range.minX().set(back.x().getValue());
        range.maxX().set(back.x().getValue() + back.prefWidth() - range.prefWidth() / 2);
        range.minY().set(back.y().get() + back.prefHeight() / 2 - range.prefHeight() / 2);
        range.maxY().set(back.y().get() + back.prefHeight() / 2 - range.prefHeight() / 2);

        range.x().set(back.x().get() + back.prefWidth() / 2 - range.prefWidth() / 2);
        range.y().set(back.y().get() + back.prefHeight() / 2 - range.prefHeight() / 2);

    }

    @Override
    public void onClick(MouseEvent.MouseClickEvent e) {
        super.onClick(e);
        System.out.println(range.x().get());
        System.out.println(e.getPosX());
        if (e.getPosX() > range.x().get()) {
            System.out.println("r");
            value.set(value.getValue() + preMove);
        } else if (e.getPosX() < range.x().get()) {
            System.out.println("l");
            value.set(value.getValue() - preMove);
        }
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

    public void setRange(float minX, float maxX, float minY, float maxY) {
        this.range.maxX().set(maxX);
        this.range.minX().set(minX);
        this.range.minY().set(minY);
        this.range.maxY().set(maxY);
    }
}
