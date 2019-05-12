package unknowndomain.engine.client.gui.component;

import com.github.mouse0w0.lib4j.observable.value.MutableFloatValue;
import com.github.mouse0w0.lib4j.observable.value.SimpleMutableFloatValue;
import unknowndomain.engine.client.asset.AssetPath;
import unknowndomain.engine.client.gui.event.MouseEvent;

/*
 * Range:a component which is limited in a range;
 */
public class Range extends Shadow {

    private MutableFloatValue minX = new SimpleMutableFloatValue();
    private MutableFloatValue maxX = new SimpleMutableFloatValue();
    private MutableFloatValue minY = new SimpleMutableFloatValue();
    private MutableFloatValue maxY = new SimpleMutableFloatValue();
    private Image image = new Image();
    protected Range() {
        super();
//        minX().addChangeListener((ob, o, n) -> reBuild());
//        maxX().addChangeListener((ob, o, n) -> reBuild());
//        minY().addChangeListener((ob, o, n) -> reBuild());
//        maxY().addChangeListener((ob, o, n) -> reBuild());
        x().addChangeListener((ob,o,n)->reBuild());
        y().addChangeListener((ob,o,n)->reBuild());
        this.getChildren().addAll(image);
        image.path().setValue(AssetPath.of("engine","texture","gui","slider.png"));
    }

    public Range(float minX, float maxX, float minY, float maxY) {
        this();
        this.minX.set(minX);
        this.maxX.set(maxX);
        this.minY.set(minY);
        this.maxY.set(maxY);
        x().set((minX + maxX) / 2);
        y().set((minY + maxY) / 2);

    }

    public void reBuild() {
        System.out.println(x().get()+"x");
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
        image.x().set(x().get());
        image.y().set(y().get());
        System.out.println(image.x().get()+"img");
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

}
