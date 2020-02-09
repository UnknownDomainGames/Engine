package engine.gui;

import com.github.mouse0w0.observable.collection.ObservableCollections;
import com.github.mouse0w0.observable.collection.ObservableList;
import com.github.mouse0w0.observable.value.ObservableValue;
import com.github.mouse0w0.observable.value.ValueChangeListener;
import engine.gui.util.Utils;

import java.util.LinkedList;

public abstract class Parent extends Node {

    private final ObservableList<Node> children = ObservableCollections.observableList(new LinkedList<>());
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

    @Override
    public float prefWidth() {
        float minX = 0, maxX = 0;
        for (Node child : getChildren()) {
            float childMinX = child.x().get();
            float childMaxX = childMinX + Math.max(Utils.prefWidth(child), child.width().get());
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
    public float prefHeight() {
        float minY = 0, maxY = 0;
        for (Node child : getChildren()) {
            float childMinY = child.y().get();
            float childMaxY = childMinY + Math.max(Utils.prefHeight(child), child.height().get());
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
        Parent parent = parent().get();
        while (parent != null && parent.layoutState == LayoutState.CLEAN) {
            parent.layoutState = LayoutState.DIRTY_BRANCH;
            parent = parent.parent().get();
        }
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
            layoutInArea(node, node.x().get(), node.y().get(), Utils.prefWidth(node), Utils.prefHeight(node));
        }
    }

    protected final void layoutInArea(Node node, float x, float y, float width, float height) {
        node.relocate(x, y);
        node.resize(width, height);
    }
}
