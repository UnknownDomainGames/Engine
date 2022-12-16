package engine.gui;

import com.github.mouse0w0.observable.collection.ObservableCollections;
import com.github.mouse0w0.observable.collection.ObservableList;
import com.github.mouse0w0.observable.value.ObservableValue;
import com.github.mouse0w0.observable.value.ValueChangeListener;
import engine.gui.graphics.NodeRenderer;
import engine.gui.graphics.ParentRenderer;
import engine.math.Math2;

import java.util.ArrayList;
import java.util.List;

public abstract class Parent extends Node {

    private final List<Node> internalChildren = new ArrayList<>();
    private final ObservableList<Node> children = ObservableCollections.observableList(internalChildren);
    private final ObservableList<Node> unmodifiableChildren = ObservableCollections.unmodifiableObservableList(children);

    public Parent() {
        children.addChangeListener(change -> {
            for (Node node : change.getAdded()) {
                Parent oldParent = node.parent.get();
                if (oldParent != null) {
                    node.scene.unbindBidirectional(oldParent.scene);
                }
                node.parent.set(this);
                node.scene.bindBidirectional(Parent.this.scene);
                node.parent.addChangeListener(new ValueChangeListener<>() {
                    @Override
                    public void onChanged(ObservableValue<? extends Parent> observable, Parent oldValue, Parent newValue) {
                        children.remove(node);
                        observable.removeChangeListener(this);
                    }
                });
            }
            for (Node node : change.getRemoved()) {
                if (node.parent.get() == this) {
                    node.scene.unbindBidirectional(Parent.this.scene);
                    node.parent.set(null);
                }
            }
            needsLayout();
        });
    }

    protected ObservableList<Node> getChildren() {
        return children;
    }

    public final ObservableList<Node> getUnmodifiableChildren() {
        return unmodifiableChildren;
    }

    public final void toFront(Node node) {
        int lastChild = internalChildren.size() - 1;
        if (internalChildren.get(lastChild) != node) {
            internalChildren.remove(node);
            internalChildren.add(node);
        }
    }

    public final void toBack(Node node) {
        if (internalChildren.get(0) != node) {
            internalChildren.remove(node);
            internalChildren.add(0, node);
        }
    }

    @Override
    public double prefWidth() {
        double minX = 0, maxX = 0;
        for (Node child : getChildren()) {
            double childMinX = child.getLayoutX();
            double childMaxX = childMinX + Math.max(prefWidth(child), child.getWidth());
            if (minX > childMinX) {
                minX = childMinX;
            }
            if (maxX < childMaxX) {
                maxX = childMaxX;
            }
        }
        return maxX - minX;
    }

    @Override
    public double prefHeight() {
        double minY = 0, maxY = 0;
        for (Node child : getChildren()) {
            double childMinY = child.getLayoutY();
            double childMaxY = childMinY + Math.max(prefHeight(child), child.getHeight());
            if (minY > childMinY) {
                minY = childMinY;
            }
            if (maxY < childMaxY) {
                maxY = childMaxY;
            }
        }
        return maxY - minY;
    }

    private LayoutState layoutState = LayoutState.NEED_LAYOUT;
    private boolean performingLayout = false;

    public void needsLayout() {
        layoutState = LayoutState.NEED_LAYOUT;
        for (Node child : children) {
            if (child instanceof Parent) {
                ((Parent) child).needsLayout();
            }
        }
        Parent parent = getParent();
        while (parent != null && parent.layoutState == LayoutState.CLEAN) {
            parent.layoutState = LayoutState.NEED_LAYOUT;
            parent = parent.getParent();
        }
    }

    // Tell that this Parent does not need layouting anymore
    public void revokeNeedsLayout() {
        var flag = LayoutState.CLEAN;
        for (Node child : children) {
            if (child instanceof Parent && ((Parent) child).isNeedsLayout()) {
                flag = LayoutState.DIRTY_BRANCH;
                break;
            }
        }
        layoutState = flag;
    }

    public boolean isNeedsLayout() {
        return layoutState == LayoutState.NEED_LAYOUT;
    }

    public boolean isShouldUpdate() {
        return layoutState != LayoutState.CLEAN;
    }

    public final void layout() {
        switch (layoutState) {
            case CLEAN:
                break;
            case NEED_LAYOUT:
                if (performingLayout) {
                    break;
                }
                performingLayout = true;
                layoutChildren();
                // Intended fall-through
            case DIRTY_BRANCH:
                for (Node node : getChildren()) {
                    if (node instanceof Parent) {
                        ((Parent) node).layout();
                    }
                }
                layoutState = LayoutState.CLEAN;
                performingLayout = false;
                break;
        }
    }

    protected void layoutChildren() {
        for (Node node : getChildren()) {
            layoutInArea(node, node.getLayoutX(), node.getLayoutY(), prefWidth(node), prefHeight(node));
        }
    }

    protected final void layoutInArea(Node node, double x, double y, double width, double height) {
        node.resize(width, height);
        node.relocate(x, y);
    }

    protected static double prefWidth(Node node) {
        return Math2.second(node.prefWidth(), node.minWidth(), node.maxWidth());
    }

    protected static double prefHeight(Node node) {
        return Math2.second(node.prefHeight(), node.minHeight(), node.maxHeight());
    }

    @Override
    protected NodeRenderer createDefaultRenderer() {
        return ParentRenderer.INSTANCE;
    }
}
