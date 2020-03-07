package engine.gui.stage;

import com.github.mouse0w0.observable.value.*;
import engine.gui.Node;

import java.util.ArrayList;
import java.util.List;

public class Popup extends Stage {

    private final MutableObjectValue<Node> ownerNode = new SimpleMutableObjectValue<>();

    private final MutableFloatValue anchorX = new SimpleMutableFloatValue();
    private final MutableFloatValue anchorY = new SimpleMutableFloatValue();

    private final MutableObjectValue<AnchorLocation> anchorLocation = new SimpleMutableObjectValue<>();

    private MutableBooleanValue autoHide;

    {
        focused().addChangeListener((observable, oldValue, newValue) -> {
            if (isAutoHide() && !newValue) hide();
        });
    }

    private final List<Popup> children = new ArrayList<>();

    public enum AnchorLocation {
        SCREEN_LEFT_TOP,
        SCREEN_RIGHT_TOP,
        SCREEN_LEFT_BOTTOM,
        SCREEN_RIGHT_BOTTOM,

        CONTENT_LEFT_TOP,
        CONTENT_RIGHT_TOP,
        CONTENT_LEFT_BOTTOM,
        CONTENT_RIGHT_BOTTOM,

        NODE_LEFT_TOP,
        NODE_RIGHT_TOP,
        NODE_LEFT_BOTTOM,
        NODE_RIGHT_BOTTOM,
    }

    public final MutableBooleanValue autoHide() {
        if (autoHide == null) {
            autoHide = new SimpleMutableBooleanValue();
        }
        return autoHide;
    }

    public final boolean isAutoHide() {
        return autoHide != null && autoHide.get();
    }

    public final void setAutoHide(boolean autoHide) {
        autoHide().set(autoHide);
    }

    public final ObservableObjectValue<Node> ownerNode() {
        return ownerNode.toUnmodifiable();
    }

    public final Node getOwnerNode() {
        return ownerNode.get();
    }

    public final ObservableFloatValue anchorX() {
        return anchorX.toUnmodifiable();
    }

    public final float getAnchorX() {
        return anchorX.get();
    }

    public final ObservableFloatValue anchorY() {
        return anchorY.toUnmodifiable();
    }

    public final float getAnchorY() {
        return anchorY.get();
    }

    public final MutableObjectValue<AnchorLocation> anchorLocation() {
        return anchorLocation;
    }

    public final AnchorLocation getAnchorLocation() {
        return anchorLocation.get();
    }

    public final void setAnchorLocation(AnchorLocation anchorLocation) {
        anchorLocation().set(anchorLocation);
    }

    public void show(Stage ownerStage, float anchorX, float anchorY) {
        setOwner(ownerStage);
        updateWindowPos(anchorX, anchorY);
        show(ownerStage);
    }

    public void show(Node ownerNode, float anchorX, float anchorY) {
        this.ownerNode.set(ownerNode);
        Stage ownerStage = ownerNode.getScene().getStage();
        setOwner(ownerStage);
        updateWindowPos(anchorX, anchorY);
        show(ownerStage);
    }

    private void updateWindowPos(float anchorX, float anchorY) {

    }

    private void show(Stage ownerStage) {
        if (ownerStage instanceof Popup) {
            ((Popup) ownerStage).children.add(this);
        }
        super.show();
        window.setDecorated(false);
        window.setFloating(true);
    }

    @Override
    public void hide() {
        children.forEach(Popup::hide);
        children.clear();
        super.hide();
        Stage ownerStage = getOwner();
        if (ownerStage instanceof Popup) {
            ((Popup) ownerStage).children.remove(this);
        }
    }
}
