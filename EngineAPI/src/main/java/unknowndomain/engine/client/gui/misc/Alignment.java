package unknowndomain.engine.client.gui.misc;

import com.github.mouse0w0.lib4j.observable.value.MutableValue;
import com.github.mouse0w0.lib4j.observable.value.SimpleMutableObjectValue;

public class Alignment {
    private MutableValue<Alignments> horizontal = new SimpleMutableObjectValue<>(Alignments.CENTER);
    private MutableValue<Alignments> vertical = new SimpleMutableObjectValue<>(Alignments.CENTER);

    public Alignment() {}

    public Alignment(Alignments horizontal, Alignments vertical) {
        this.horizontal.setValue(horizontal);
        this.vertical.setValue(vertical);
    }

    public MutableValue<Alignments> horizontal() {
        return horizontal;
    }

    public MutableValue<Alignments> vertical() {
        return vertical;
    }

    public enum Alignments {
        LEFT,CENTER,RIGHT,TOP,BOTTOM
    }
}
