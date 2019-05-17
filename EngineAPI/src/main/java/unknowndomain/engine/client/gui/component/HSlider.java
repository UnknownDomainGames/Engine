package unknowndomain.engine.client.gui.component;

import com.github.mouse0w0.lib4j.observable.value.*;
import unknowndomain.engine.client.asset.AssetPath;
import unknowndomain.engine.client.gui.Region;
import unknowndomain.engine.client.gui.event.MouseEvent;
import unknowndomain.engine.client.gui.misc.Background;
import unknowndomain.engine.event.Event;
import unknowndomain.engine.util.Color;


public class HSlider extends Region {
    private Label slider = new Label();

    private Label back = new Label();

    private MutableDoubleValue value = new SimpleMutableDoubleValue(0);

    private double preMove = 0;

    private boolean select = false;

    public HSlider() {
        value.addChangeListener((ob, o, n) -> rebuild());
        backHeight().addChangeListener((ob, o, n) -> rebuild());
        sliderHeight().addChangeListener((ob, o, n) -> rebuild());
        sliderWidth().addChangeListener((ob, o, n) -> rebuild());
        backWidth().addChangeListener((ob, o, n) -> rebuild());
        this.getChildren().addAll(back, slider);
        backBg().setValue(new Background(Color.BLUE));
        sliderBg().setValue(new Background(Color.WHITE));
    }

    public MutableDoubleValue value() {
        return value;
    }

    public void setPreMove(double preMove) {
        this.preMove = preMove;
    }

    public void rebuild() {
        if (value.get() > 1) {
            value.set(1);
        } else if (value.get() < 0) {
            value.set(0);
        }
        slider.x().set((float) ((back.prefWidth()) * value.get() - slider.prefWidth() / 2));
        slider.y().set(back.y().get() + back.prefHeight() / 2 - slider.prefHeight() / 2);
    }

    @Override
    public void onClick(MouseEvent.MouseClickEvent e) {
        super.onClick(e);
        if (e.getPosX() > slider.x().get() + slider.prefWidth()) {
            value.set(value.getValue() + preMove);
        } else if (e.getPosX() < slider.x().get()) {
            value.set(value.getValue() - preMove);
        }
        if (slider.contains(e.getPosX(), e.getPosY()))
            select = true;
    }

    @Override
    public void handleEvent(Event event) {
        super.handleEvent(event);
        if (event instanceof MouseEvent.MouseMoveEvent && select) {
            if ((((MouseEvent.MouseMoveEvent) event).getNewPosX() - x().get() - slider.x().get()) / prefWidth() > preMove * 0.8) {
                value.set(value.getValue() + preMove);
            } else if ((slider.x().get() - ((MouseEvent.MouseMoveEvent) event).getNewPosX() + x().get()) / prefWidth() > preMove * 0.8) {
                value.set(value.getValue() - preMove);
            }
        } else if (event instanceof MouseEvent.MouseReleasedEvent) {
            select = false;
        } else if (event instanceof MouseEvent.MouseLeaveEvent) {
            //select = false;
        }
    }

    public MutableFloatValue backWidth() {
        return back.labelWidth();
    }

    public MutableFloatValue backHeight() {
        return back.labelHeight();
    }

    public MutableValue<Background> backBg() {
        return back.background();
    }

    public MutableValue<Background> sliderBg() {
        return slider.background();
    }

    public MutableFloatValue sliderWidth() {
        return slider.labelWidth();
    }

    public MutableFloatValue sliderHeight() {
        return slider.labelHeight();
    }
}
