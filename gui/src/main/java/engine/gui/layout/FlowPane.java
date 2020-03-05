package engine.gui.layout;

import com.github.mouse0w0.observable.value.MutableFloatValue;
import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.SimpleMutableFloatValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import engine.gui.Node;
import engine.gui.misc.Insets;
import engine.gui.misc.Orientation;
import engine.gui.misc.Pos;
import engine.gui.util.Utils;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class FlowPane extends Pane {

    private final MutableObjectValue<Pos> alignment = new SimpleMutableObjectValue<>(Pos.TOP_LEFT);
    private final MutableObjectValue<Orientation> orientation = new SimpleMutableObjectValue<>(Orientation.HORIZONTAL);
    private final MutableFloatValue spacing = new SimpleMutableFloatValue();

    public FlowPane() {
        alignment.addChangeListener((observable, oldValue, newValue) -> this.needsLayout());
        orientation.addChangeListener((observable, oldValue, newValue) -> this.needsLayout());
    }

    public MutableObjectValue<Pos> alignment() {
        return alignment;
    }

    public MutableObjectValue<Orientation> orientation() {
        return orientation;
    }

    public MutableFloatValue spacing() {
        return spacing;
    }

    @Override
    protected void layoutChildren() {
        Insets padding = padding().getValue();
        float spacing = spacing().get();
        List<Pair<List<Node>, Float>> groups = new ArrayList<>();
        var max = 0f;
        var tmpsize = 0f;
        var tmpgroup = new MutablePair<List<Node>, Float>(new ArrayList<>(), 0f);
        for (var child : getChildren()) {
            float pw = Utils.prefWidth(child);
            float ph = Utils.prefHeight(child);
            if (orientation.get() == Orientation.VERTICAL) {
                if (tmpsize + ph > getHeight()) {
                    tmpsize = ph + spacing;
                    tmpgroup.setRight(max);
                    groups.add(tmpgroup);
                    max = pw;
                    tmpgroup = new MutablePair<>(new ArrayList<>(), 0f);
                } else {
                    max = Math.max(max, pw);
                    tmpsize += ph + spacing;
                }
            } else {
                if (tmpsize + pw > getWidth()) {
                    tmpsize = pw + spacing;
                    tmpgroup.setRight(max);
                    groups.add(tmpgroup);
                    max = ph;
                    tmpgroup = new MutablePair<>(new ArrayList<>(), 0f);
                } else {
                    max = Math.max(max, ph);
                    tmpsize += pw + spacing;
                }
            }
            tmpgroup.left.add(child);
        }
        tmpgroup.setRight(max);
        groups.add(tmpgroup);
        var size = Math.min(orientation.get() == Orientation.VERTICAL ? getWidth() : getHeight(), groups.stream().mapToDouble(Pair::getRight).reduce(0, Double::sum));
        float x;
        float y;
        switch (alignment.getValue().getHpos()) {
            case RIGHT:
                x = getWidth() - padding.getRight();
                break;
            case CENTER:
                if (orientation.get() == Orientation.VERTICAL) {
                    x = (float) (Math.max((getWidth() - padding.getLeft() - padding.getRight()) - size, 0) / 2 + padding.getLeft());
                } else {
                    x = 0; // Handle x in groups
                }
                break;
            default:
                x = padding.getLeft();
                break;
        }
        switch (alignment.getValue().getVpos()) {
            case BOTTOM:
                y = getHeight() - padding.getBottom();
                break;
            case CENTER:
                if (orientation.get() == Orientation.HORIZONTAL) {
                    y = (float) (Math.max(getHeight() - padding.getTop() - padding.getBottom() - size, 0) / 2 + padding.getTop());
                } else {
                    y = 0; // Handle y in groups
                }
                break;
            default:
                y = padding.getTop();
                break;
        }
        float lineW = 0;
        float lineH = 0;
        for (Pair<List<Node>, Float> group : groups) {
            if (orientation.get() == Orientation.VERTICAL) {
                y = (float) (Math.max(getHeight() - padding.getTop() - padding.getBottom() - group.getLeft().stream().mapToDouble(Node::prefHeight).reduce(0, Double::sum) - spacing * (group.getLeft().size() - 1), 0) / 2 + padding.getTop());
            } else {
                x = (float) (Math.max(getWidth() - padding.getLeft() - padding.getRight() - group.getLeft().stream().mapToDouble(Node::prefWidth).reduce(0, Double::sum) - spacing * (group.getLeft().size() - 1), 0) / 2 + padding.getLeft());
            }
            for (var child : group.getLeft()) {
                float pw = Utils.prefWidth(child);
                float ph = Utils.prefHeight(child);
                float x1;
                if (alignment.getValue().getHpos() == Pos.HPos.RIGHT) {
                    x1 = x - pw;
                } else if (alignment.get().getHpos() == Pos.HPos.CENTER) {
                    if (orientation.get() == Orientation.HORIZONTAL) {
                        x1 = x;
                    } else {
                        x1 = x + (group.getRight() - pw) / 2;
                    }
                } else {
                    x1 = x;
                }
                float y1;
                if (alignment.getValue().getVpos() == Pos.VPos.BOTTOM) {
                    y1 = y - ph;
                } else if (alignment.get().getVpos() == Pos.VPos.CENTER) {
                    if (orientation.get() == Orientation.VERTICAL) {
                        y1 = y;
                    } else {
                        y1 = y + (group.getRight() - ph) / 2;
                    }
                } else {
                    y1 = y;
                }
                layoutInArea(child, snap(x1,true), snap(y1, true), snap(pw, true), snap(ph, true));
                if (orientation.getValue() == Orientation.HORIZONTAL) {
                    x = x1 + (alignment.getValue().getHpos() == Pos.HPos.RIGHT ? -spacing : spacing + pw);
                } else {
                    y = y1 + (alignment.getValue().getVpos() == Pos.VPos.BOTTOM ? -spacing : spacing + ph);
                }
            }
            if (orientation.get() == Orientation.VERTICAL) {
                x += group.getRight();
            } else {
                y += group.getRight();
            }
        }
    }
}
