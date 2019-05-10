package unknowndomain.engine.client.gui.component;

import com.github.mouse0w0.lib4j.observable.value.MutableFloatValue;
import com.github.mouse0w0.lib4j.observable.value.SimpleMutableFloatValue;
import org.checkerframework.checker.nullness.qual.NonNull;
import unknowndomain.engine.client.gui.event.MouseEvent;

public class Slider extends Control {

    protected Slider(){
        super();
    }

    private MutableFloatValue toX = new SimpleMutableFloatValue();
    private MutableFloatValue toY = new SimpleMutableFloatValue();

    @Override
    public void onClick(MouseEvent.MouseClickEvent event) {
        this.addEventHandler(MouseEvent.MouseMoveEvent.class,this::onMove);
    }

    @Override
    public void onRelease(MouseEvent.MouseReleasedEvent event) {
        this.removeEventHandler(MouseEvent.MouseMoveEvent.class,this::onMove);
    }
    private void onMove(MouseEvent.MouseMoveEvent event){
        toX().set((float) event.getNewPosX());
        toY().set((float) event.getNewPosY());
    }

    public void bindSlider(@NonNull MutableFloatValue x,@NonNull  MutableFloatValue y){
        x.addChangeListener((ob,o,n)->{
            this.x().set(n);
        });
        y.addChangeListener((ob,o,n)->{
            this.y().set(n);
        });
    }
    public MutableFloatValue toX() {
        return toX;
    }

    public MutableFloatValue toY() {
        return toY;
    }
}
