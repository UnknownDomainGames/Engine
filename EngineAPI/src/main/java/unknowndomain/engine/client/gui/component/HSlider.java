package unknowndomain.engine.client.gui.component;

import com.github.mouse0w0.lib4j.observable.value.*;
import unknowndomain.engine.client.asset.AssetPath;
import unknowndomain.engine.client.gui.Region;
import unknowndomain.engine.client.gui.event.MouseEvent;
import unknowndomain.engine.client.gui.text.Text;


public class HSlider extends Region {
    private Image range = new Image(AssetPath.of("engine", "texture", "gui", "slider.png"));

    private ImageButton back = new ImageButton();

    private MutableDoubleValue value = new SimpleMutableDoubleValue(0.5);

    private double preMove = 0;

    public HSlider() {
        value.addChangeListener((ob, o, n) -> rebuild());
        backBg().addChangeListener((ob, o, n) -> reSize());

        this.getChildren().addAll(back, range);
    }

    public MutableDoubleValue value() {
        return value;
    }

    public double getPreMove() {
        return preMove;
    }

    public void setPreMove(float preMove) {
        this.preMove = preMove;
    }

    public void rebuild() {
        if (value.get() > 1) {
            value.set(1);
        } else if (value.get() < 0) {
            value.set(0);
        }
        range.x().set((float) ((back.prefWidth()) * value.get() - range.prefWidth() / 2));
        range.y().set(back.y().get() + back.prefHeight() / 2 - range.prefHeight() / 2);
    }

    public void reSize() {
        range.x().set(back.x().get() + back.prefWidth() / 2 - range.prefWidth() / 2);
        range.y().set(back.y().get() + back.prefHeight() / 2 - range.prefHeight() / 2);
    }

    @Override
    public void onClick(MouseEvent.MouseClickEvent e) {
        super.onClick(e);
        if (e.getPosX() > range.x().get()) {
            value.set(value.getValue() + preMove);
        } else if (e.getPosX() < range.x().get()) {
            value.set(value.getValue() - preMove);
        }
    }

    public MutableValue<AssetPath> backBg() {
        return back.buttonBackground();
    }

}
