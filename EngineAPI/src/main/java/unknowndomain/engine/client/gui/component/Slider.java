package unknowndomain.engine.client.gui.component;

import com.github.mouse0w0.lib4j.observable.value.MutableFloatValue;
import com.github.mouse0w0.lib4j.observable.value.SimpleMutableFloatValue;
import com.github.mouse0w0.lib4j.observable.value.SimpleMutableObjectValue;
import org.checkerframework.checker.nullness.qual.NonNull;
import unknowndomain.engine.client.gui.event.MouseEvent;
import unknowndomain.engine.client.gui.text.Text;
import unknowndomain.engine.event.Event;

public class Slider extends Control {

    private MutableFloatValue minX = new SimpleMutableFloatValue();
    private MutableFloatValue maxX = new SimpleMutableFloatValue();
    private MutableFloatValue minY = new SimpleMutableFloatValue();
    private MutableFloatValue maxY = new SimpleMutableFloatValue();

    private ReflectMoveToObject reflect;

    protected Slider() {
        super();

//        minX().addChangeListener((ob, o, n) -> reBuild());
//        maxX().addChangeListener((ob, o, n) -> reBuild());
//        minY().addChangeListener((ob, o, n) -> reBuild());
//        maxY().addChangeListener((ob, o, n) -> reBuild());
        x().addChangeListener((ob,o,n)->reBuild());
        y().addChangeListener((ob,o,n)->reBuild());
        this.getChildren().add(new Text("121"));
    }

    public Slider(float minX, float maxX, float minY, float maxY) {
        this();
        this.minX.set(minX);
        this.maxX.set(maxX);
        this.minY.set(minY);
        this.maxY.set(maxY);
        x().set((minX + maxX) / 2);
        y().set((minY + maxY) / 2);

    }

    @Override
    public void onClick(MouseEvent.MouseClickEvent event) {
        System.out.println(111);
        this.addEventHandler(MouseEvent.MouseMoveEvent.class, this::onMove);
    }

    @Override
    public void onRelease(MouseEvent.MouseReleasedEvent event) {
        System.out.println(112);
        this.removeEventHandler(MouseEvent.MouseMoveEvent.class, this::onMove);
    }

    private void onMove(MouseEvent.MouseMoveEvent event) {
        if (reflect != null) {
            reflect.accept(event);
        }
    }
    @Override
    public void handleEvent(Event event) {
        super.handleEvent(event);
        if(event instanceof MouseEvent.MouseReleasedEvent){
            System.out.println(112);
            this.removeEventHandler(MouseEvent.MouseMoveEvent.class, this::onMove);
        }
    }

    public <T> void setReflectMoveToObject(@NonNull ReflectMoveToObject reflect) {
        this.reflect = reflect;
    }

    public <T> void setReflectObjectToPos(@NonNull SimpleMutableObjectValue<T> t, @NonNull ReflectObjectToPos<T> reflect) {
        System.out.println(1);
        t.addChangeListener((ob, o, n) -> reflect.accept(n, x(), y()));
    }

    public void reBuild() {
        if (x().get() > maxX().get()) {
            x().set(maxX().get());
        }
        if (x().get() < minX().get()) {
            x().set(minX().get());
        }
        if (y().get() > maxY().get()) {
            y().set(maxY().get());
        }
        if (y().get() < minY().get()) {
            y().set(minY().get());
        }
    }

    public MutableFloatValue minX() {
        return minX;
    }

    public MutableFloatValue maxX() {
        return maxX;
    }

    public MutableFloatValue minY() {
        return minY;
    }

    public MutableFloatValue maxY() {
        return maxY;
    }


    public interface ReflectObjectToPos<T> {
        void accept(T t, MutableFloatValue x, MutableFloatValue y);
    }

    public interface ReflectMoveToObject {
        void accept(MouseEvent.MouseMoveEvent event);
    }

}
