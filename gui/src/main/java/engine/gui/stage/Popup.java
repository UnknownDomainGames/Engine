package engine.gui.stage;

import com.github.mouse0w0.observable.value.*;
import engine.graphics.display.Screen;
import engine.graphics.display.VideoMode;
import engine.gui.Node;
import engine.gui.Scene;

import java.util.ArrayList;
import java.util.List;

public class Popup extends Stage {

    private final MutableObjectValue<Node> ownerNode = new SimpleMutableObjectValue<>();

    private final MutableDoubleValue anchorX = new SimpleMutableDoubleValue();
    private final MutableDoubleValue anchorY = new SimpleMutableDoubleValue();

    private MutableObjectValue<AnchorLocation> anchorLocation;

    private MutableBooleanValue autoHide;
    private MutableBooleanValue autoFix;

    private final List<Popup> children = new ArrayList<>();

    public enum AnchorLocation {
        LEFT_TOP(0, 0),
        RIGHT_TOP(1, 0),
        LEFT_BOTTOM(0, 1),
        RIGHT_BOTTOM(1, 1);

        private final double xFactor;
        private final double yFactor;

        AnchorLocation(double xFactor, double yFactor) {
            this.xFactor = xFactor;
            this.yFactor = yFactor;
        }

        public double getXFactor() {
            return xFactor;
        }

        public double getYFactor() {
            return yFactor;
        }
    }

    public Popup() {
        focused().addChangeListener((observable, oldValue, newValue) -> {
            if (isAutoHide() && !newValue) hide();
        });
    }

    public final ObservableObjectValue<Node> ownerNode() {
        return ownerNode.toUnmodifiable();
    }

    public final Node getOwnerNode() {
        return ownerNode.get();
    }

    public final ObservableDoubleValue anchorX() {
        return anchorX.toUnmodifiable();
    }

    public final double getAnchorX() {
        return anchorX.get();
    }

    public final ObservableDoubleValue anchorY() {
        return anchorY.toUnmodifiable();
    }

    public final double getAnchorY() {
        return anchorY.get();
    }

    public final MutableObjectValue<AnchorLocation> anchorLocation() {
        if (anchorLocation == null) {
            anchorLocation = new SimpleMutableObjectValue<>(AnchorLocation.LEFT_TOP);
        }
        return anchorLocation;
    }

    public final AnchorLocation getAnchorLocation() {
        return anchorLocation == null ? AnchorLocation.LEFT_TOP : anchorLocation.get();
    }

    public final void setAnchorLocation(AnchorLocation anchorLocation) {
        anchorLocation().set(anchorLocation);
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

    public final MutableBooleanValue autoFix() {
        if (autoFix == null) {
            autoFix = new SimpleMutableBooleanValue(true);
        }
        return autoFix;
    }

    public final boolean isAutoFix() {
        return autoFix == null || autoFix.get();
    }

    public final void setAutoFix(boolean autoFix) {
        autoFix().set(autoFix);
    }

    public void show(Stage ownerStage, double anchorX, double anchorY) {
        setOwner(ownerStage);
        updateWindowPos(anchorX, anchorY);
        show(ownerStage);
    }

    public void show(Node ownerNode, double anchorX, double anchorY) {
        this.ownerNode.set(ownerNode);
        Stage ownerStage = ownerNode.getScene().getStage();
        setOwner(ownerStage);
        updateWindowPos(anchorX, anchorY);
        show(ownerStage);
    }

    private void updateWindowPos(double anchorX, double anchorY) {
        sizeToScene();

        Scene scene = getScene();
        AnchorLocation anchorLocation = getAnchorLocation();
        double anchorXFactor = anchorLocation.getXFactor();
        double anchorYFactor = anchorLocation.getYFactor();
        double anchorDeltaX = anchorXFactor * scene.getWidth();
        double anchorDeltaY = anchorYFactor * scene.getHeight();
        double windowX = anchorX - anchorDeltaX;
        double windowY = anchorY - anchorDeltaY;

        if (isAutoFix()) {
            Screen screen = getScreen();
            VideoMode videoMode = screen.getVideoMode();
            double screenMinX = screen.getWorkareaX();
            double screenMinY = screen.getWorkareaY();
            double screenMaxX = screenMinX + videoMode.getWidth();
            double screenMaxY = screenMinY + videoMode.getHeight();
            if (anchorXFactor <= 0.5) {
                // Left
                windowX = Math.max(Math.min(windowX, screenMaxX - scene.getWidth()), screenMinX);
            } else {
                // Right
                windowX = Math.min(Math.max(windowX, screenMinX), screenMaxX - scene.getWidth());
            }

            if (anchorYFactor <= 0.5) {
                // Top
                windowY = Math.max(Math.min(windowY, screenMaxY - scene.getHeight()), screenMinY);
            } else {
                // Bottom
                windowY = Math.min(Math.max(windowY, screenMinY), screenMaxY - scene.getHeight());
            }
        }

        setPos((int) windowX, (int) windowY);

        this.anchorX.set(windowX + anchorDeltaX);
        this.anchorY.set(windowY + anchorDeltaY);
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
