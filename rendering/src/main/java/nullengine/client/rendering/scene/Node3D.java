package nullengine.client.rendering.scene;

import com.github.mouse0w0.observable.value.*;
import nullengine.client.rendering.math.Transform;
import org.apache.commons.lang3.Validate;
import org.joml.Quaternionfc;
import org.joml.Vector3fc;

import java.util.*;

public class Node3D {

    private final MutableObjectValue<Node3D> parent = new SimpleMutableObjectValue<>();

    final MutableObjectValue<Scene3D> scene = new SimpleMutableObjectValue<>();

    private MutableStringValue id;

    private List<Node3D> children;
    private List<Node3D> unmodifiableChildren;

    private Transform transform = new Transform();
    private Transform worldTransform = new Transform();

    private Controller controller;

    private Map<Object, Object> properties;

    public Node3D() {
        parent.addChangeListener((observable, oldValue, newValue) -> refreshTransform());
    }

    public final ObservableObjectValue<Scene3D> scene() {
        return scene.toUnmodifiable();
    }

    public final ObservableObjectValue<Node3D> parent() {
        return parent.toUnmodifiable();
    }

    public final Scene3D getScene() {
        return scene.getValue();
    }

    public final Node3D getParent() {
        return parent.getValue();
    }

    private void setParent(Node3D parent) {
        Node3D oldParent = getParent();
        if (oldParent == parent) {
            return;
        }
        if (oldParent != null) {
            scene.unbindBidirectional(oldParent.scene);
            oldParent.children.remove(this);
        }
        this.parent.set(parent);
        if (parent != null) {
            scene.bindBidirectional(parent.scene);
        } else {
            scene.set(null);
        }
    }

    public final MutableStringValue id() {
        if (id == null) {
            id = new SimpleMutableStringValue();
            id.addChangeListener((observable, oldValue, newValue) -> {
                if (oldValue != null) {
                    scene.ifPresent($ -> $.idToNode.remove(oldValue, this));
                }
                if (newValue != null) {
                    scene.ifPresent($ -> {
                        if ($.idToNode.containsKey(newValue)) {
                            throw new IllegalArgumentException("Id \"" + newValue + "has been exists");
                        }
                        $.idToNode.put(newValue, this);
                    });
                }
            });
            scene.addChangeListener((observable, oldValue, newValue) -> {
                if (oldValue != null && id != null) {
                    id.ifPresent($ -> oldValue.idToNode.remove($, this));
                }

                if (newValue != null && id != null) {
                    id.ifPresent($ -> {
                        if (newValue.idToNode.containsKey($)) {
                            throw new IllegalArgumentException("Id \"" + $ + "has been exists");
                        }
                        newValue.idToNode.put($, this);
                    });
                }
            });
        }
        return id;
    }

    public final String getId() {
        return id == null ? null : id.get();
    }

    public final void setId(String id) {
        id().set(id);
    }

    public final List<Node3D> getUnmodifiableChildren() {
        return unmodifiableChildren == null ? List.of() : unmodifiableChildren;
    }

    private List<Node3D> getChildren() {
        if (children == null) {
            children = new ArrayList<>();
            unmodifiableChildren = Collections.unmodifiableList(children);
        }
        return children;
    }

    public void addChild(Node3D node) {
        Validate.notNull(node);
        getChildren().add(node);
        node.setParent(this);
    }

    public void removeChild(Node3D node) {
        getChildren().remove(node);
        node.setParent(null);
    }

    public Transform getTransform() {
        return transform;
    }

    public void setTransform(Transform transform) {
        this.transform.set(transform);
        refreshTransform();
    }

    public void setTranslation(Vector3fc translation) {
        transform.setTranslation(translation);
        refreshTransform();
    }

    public void setTranslation(float x, float y, float z) {
        transform.setTranslation(x, y, z);
        refreshTransform();
    }

    public void setRotation(Vector3fc rotation) {
        transform.setRotation(rotation);
        refreshTransform();
    }

    public void setRotation(float angleX, float angleY, float angleZ) {
        transform.setRotation(angleX, angleY, angleZ);
        refreshTransform();
    }

    public void setRotation(Quaternionfc rotation) {
        transform.setRotation(rotation);
        refreshTransform();
    }

    public void setRotation(float x, float y, float z, float w) {
        transform.setRotation(x, y, z, w);
        refreshTransform();
    }

    public void setScale(Vector3fc scale) {
        transform.setScale(scale);
        refreshTransform();
    }

    public void setScale(float x, float y, float z) {
        transform.setScale(x, y, z);
        refreshTransform();
    }

    public Transform getWorldTransform() {
        return worldTransform;
    }

    public Vector3fc getWorldTranslation() {
        return worldTransform.getTranslation();
    }

    public Quaternionfc getWorldRotation() {
        return worldTransform.getRotation();
    }

    public Vector3fc getWorldScale() {
        return worldTransform.getScale();
    }

    protected void refreshTransform() {
        worldTransform.set(transform);
        Node3D parent = getParent();
        if (parent != null) {
            worldTransform.applyParent(parent.worldTransform);
        }
        getUnmodifiableChildren().forEach(node -> refreshTransform());
    }

    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    void doUpdate(float partial) {
        if (controller != null) {
            controller.update(this, partial);
        }

        if (children != null && !children.isEmpty()) {
            children.forEach(child -> child.doUpdate(partial));
        }
    }

    public Map<Object, Object> getProperties() {
        if (properties == null) {
            properties = new HashMap<>();
        }
        return properties;
    }

    public boolean hasProperties() {
        return properties != null && !properties.isEmpty();
    }
}
