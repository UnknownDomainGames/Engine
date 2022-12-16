package engine.gui.layout;

import com.github.mouse0w0.observable.value.MutableDoubleValue;
import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.SimpleMutableDoubleValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import engine.gui.Node;
import engine.gui.Parent;
import engine.gui.misc.*;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class FlowPane extends Pane {

    private final MutableObjectValue<Pos> alignment = new SimpleMutableObjectValue<>(Pos.TOP_LEFT);
    private final MutableObjectValue<Orientation> orientation = new SimpleMutableObjectValue<>(Orientation.HORIZONTAL);
    private final MutableDoubleValue spacing = new SimpleMutableDoubleValue();

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

    public MutableDoubleValue spacing() {
        return spacing;
    }

    @Override
    protected void layoutChildren() {
        Insets padding = getPadding();
        double spacing = spacing().get();
        List<Pair<List<Node>, Double>> groups = new ArrayList<>();
        var max = 0d;
        var tmpsize = 0d;
        var tmpgroup = new MutablePair<List<Node>, Double>(new ArrayList<>(), 0d);
        for (var child : getChildren()) {
            double pw = Parent.prefWidth(child);
            double ph = Parent.prefHeight(child);
            if (orientation.get() == Orientation.VERTICAL) {
                if (tmpsize + ph > getHeight()) {
                    tmpsize = ph + spacing;
                    tmpgroup.setRight(max);
                    groups.add(tmpgroup);
                    max = pw;
                    tmpgroup = new MutablePair<>(new ArrayList<>(), 0d);
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
                    tmpgroup = new MutablePair<>(new ArrayList<>(), 0d);
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
        double x;
        double y;
        switch (alignment.get().getHPos()) {
            case RIGHT:
                x = getWidth() - padding.getRight();
                break;
            case CENTER:
                if (orientation.get() == Orientation.VERTICAL) {
                    x = Math.max((getWidth() - padding.getLeft() - padding.getRight()) - size, 0) / 2 + padding.getLeft();
                } else {
                    x = 0; // Handle x in groups
                }
                break;
            default:
                x = padding.getLeft();
                break;
        }
        switch (alignment.get().getVPos()) {
            case BOTTOM:
                y = getHeight() - padding.getBottom();
                break;
            case CENTER:
                if (orientation.get() == Orientation.HORIZONTAL) {
                    y = Math.max(getHeight() - padding.getTop() - padding.getBottom() - size, 0) / 2 + padding.getTop();
                } else {
                    y = 0; // Handle y in groups
                }
                break;
            default:
                y = padding.getTop();
                break;
        }
        double lineW = 0;
        double lineH = 0;
        for (Pair<List<Node>, Double> group : groups) {
            if (orientation.get() == Orientation.VERTICAL) {
                y = Math.max(getHeight() - padding.getTop() - padding.getBottom() - group.getLeft().stream().mapToDouble(Node::prefHeight).reduce(0, Double::sum) - spacing * (group.getLeft().size() - 1), 0) / 2 + padding.getTop();
            } else {
                x = Math.max(getWidth() - padding.getLeft() - padding.getRight() - group.getLeft().stream().mapToDouble(Node::prefWidth).reduce(0, Double::sum) - spacing * (group.getLeft().size() - 1), 0) / 2 + padding.getLeft();
            }
            for (var child : group.getLeft()) {
                double pw = Parent.prefWidth(child);
                double ph = Parent.prefHeight(child);
                double x1;
                if (alignment.get().getHPos() == HPos.RIGHT) {
                    x1 = x - pw;
                } else if (alignment.get().getHPos() == HPos.CENTER) {
                    if (orientation.get() == Orientation.HORIZONTAL) {
                        x1 = x;
                    } else {
                        x1 = x + (group.getRight() - pw) / 2;
                    }
                } else {
                    x1 = x;
                }
                double y1;
                if (alignment.get().getVPos() == VPos.BOTTOM) {
                    y1 = y - ph;
                } else if (alignment.get().getVPos() == VPos.CENTER) {
                    if (orientation.get() == Orientation.VERTICAL) {
                        y1 = y;
                    } else {
                        y1 = y + (group.getRight() - ph) / 2;
                    }
                } else {
                    y1 = y;
                }
                layoutInArea(child, snap(x1, true), snap(y1, true), snap(pw, true), snap(ph, true));
                if (orientation.get() == Orientation.HORIZONTAL) {
                    x = x1 + (alignment.get().getHPos() == HPos.RIGHT ? -spacing : spacing + pw);
                } else {
                    y = y1 + (alignment.get().getVPos() == VPos.BOTTOM ? -spacing : spacing + ph);
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
