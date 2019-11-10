package nullengine.client.rendering.scene;

import com.github.mouse0w0.observable.value.*;
import nullengine.client.rendering.math.Transform;
import org.apache.commons.lang3.Validate;
import org.joml.Quaternionfc;
import org.joml.Vector3fc;

import java.util.*;

public class Node {

    private final MutableObjectValue<Node> parent = new SimpleMutableObjectValue<>();

    final MutableObjectValue<Scene> scene = new SimpleMutableObjectValue<>();

    private MutableStringValue id;

    private List<Node> children;
    private List<Node> unmodifiableChildren;

    private Transform transform = new Transform();
    private Transform worldTransform = new Transform();

    private Map<Object, Object> properties;

    public Node() {
        parent.addChangeListener((observable, oldValue, newValue) -> refreshTransform());
    }

    public final ObservableObjectValue<Scene> scene() {
        return scene.toImmutable();
    }

    public final ObservableObjectValue<Node> parent() {
        return parent.toImmutable();
    }

    public final Scene getScene() {
        return scene.getValue();
    }

    public final Node getParent() {
        return parent.getValue();
    }

    private void setParent(Node parent) {
        Node oldParent = getParent();
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

    public final List<Node> getUnmodifiableChildren() {
        return unmodifiableChildren == null ? List.of() : unmodifiableChildren;
    }

    private List<Node> getChildren() {
        if (children == null) {
            children = new ArrayList<>();
            unmodifiableChildren = Collections.unmodifiableList(children);
        }
        return children;
    }

    public void addChild(Node node) {
        Validate.notNull(node);
        getChildren().add(node);
        node.setParent(this);
    }

    public void removeChild(Node node) {
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
        Node parent = getParent();
        if (parent != null) {
            worldTransform.applyParent(parent.worldTransform);
        }
        getUnmodifiableChildren().forEach(node -> refreshTransform());
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
