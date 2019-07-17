package nullengine.client.gui;

import com.github.mouse0w0.observable.collection.ObservableCollections;
import com.github.mouse0w0.observable.collection.ObservableList;
import com.github.mouse0w0.observable.value.ObservableValue;
import com.github.mouse0w0.observable.value.ValueChangeListener;
import nullengine.client.gui.component.Control;
import nullengine.client.gui.util.Utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class Container extends Component {

    private final List<Component> childrenBackingList = new LinkedList<>();
    private final ObservableList<Component> children = ObservableCollections.observableList(childrenBackingList);
    private final ObservableList<Component> unmodifiableChildren = ObservableCollections.unmodifiableObservableList(children);

    public Container() {
        children.addChangeListener(change -> {
            for (Component component : change.getAdded()) {
                component.parent.setValue(this);
                component.parent.addChangeListener(new ValueChangeListener<>() {
                    @Override
                    public void onChanged(ObservableValue<? extends Container> observable, Container oldValue, Container newValue) {
                        childrenBackingList.remove(component);
                        observable.removeChangeListener(this);
                    }
                });
            }
            for (Component component : change.getRemoved()) {
                component.parent.setValue(null);
            }
            needsLayout();
        });
    }

    protected ObservableList<Component> getChildren() {
        return children;
    }

    public final ObservableList<Component> getUnmodifiableChildren() {
        return unmodifiableChildren;
    }

    public final List<Component> getChildrenRecursive() {
        var list = new ArrayList<Component>();
        for (Component child : children) {
            if (child instanceof Container) {
                list.addAll(((Container) child).getChildrenRecursive());
            }
            list.add(child);
        }
        return list;
    }

    @Override
    public float prefWidth() {
        float minX = 0, maxX = 0;
        for (Component child : getChildren()) {
            float childMinX = child.x().get();
            float childMaxX = childMinX + Utils.prefWidth(child);
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
        for (Component child : getChildren()) {
            float childMinY = child.y().get();
            float childMaxY = childMinY + Utils.prefHeight(child);
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
        for (Component child : children) {
            if(child instanceof Container) {
                ((Container) child).needsLayout();
            }
        }
        Container parent = parent().getValue();
        while (parent != null && parent.layoutState == LayoutState.CLEAN) {
            parent.layoutState = LayoutState.DIRTY_BRANCH;
            parent = parent.parent().getValue();
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
                for (Component component : getChildren()) {
                    if (component instanceof Container) {
                        ((Container) component).layout();
                    }
                }
                layoutState = LayoutState.CLEAN;
                performingLayout = false;
                break;
        }
    }

    protected void layoutChildren() {
        for (Component component : getChildren()) {
            layoutInArea(component, component.x().get(), component.y().get(), component.prefWidth(), component.prefHeight());
        }
    }

    protected final void layoutInArea(Component component, float x, float y, float width, float height) {
        component.x().set(x);
        component.y().set(y);
        component.width.set(width);
        component.height.set(height);
    }

    public List<Component> getPointingComponents(float posX, float posY) {
        var list = new ArrayList<Component>();
        for (Component component : getChildren()) {
            if (component.contains(posX, posY)) {
                if (component instanceof Container) {
                    var container = (Container) component;
                    if (!(component instanceof Control)) {
                        list.add(container);
                    }
                    list.addAll(container.getPointingComponents(posX - container.x().get(), posY - container.y().get()));

                } else {
                    list.add(component);
                }
            }
        }
        return list;
    }

    private boolean closeRequired = false;

    public boolean closeRequired() {
        return closeRequired;
    }

    public void requireClose() {
        closeRequired = true;
    }
}
