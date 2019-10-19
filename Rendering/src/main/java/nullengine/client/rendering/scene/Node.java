package nullengine.client.rendering.scene;

import nullengine.client.rendering.math.Transform;
import org.apache.commons.lang3.Validate;
import org.joml.Quaternionfc;
import org.joml.Vector3fc;

import java.util.*;

public class Node {

    private final List<Node> children = new ArrayList<>();
    private final List<Node> unmodifiableChildren = Collections.unmodifiableList(children);

    private Node parent;

    private Transform transform = new Transform();
    private Transform worldTransform = new Transform();

    private Map<Object, Object> properties;

    public List<Node> getChildren() {
        return unmodifiableChildren;
    }

    public void addChild(Node node) {
        Validate.notNull(node);
        children.add(node);
        node.setParent(this);
    }

    public void removeChild(Node node) {
        if (children.remove(node)) {
            node.setParent(null);
        }
    }

    public Node getParent() {
        return parent;
    }

    private void setParent(Node parent) {
        this.parent = parent;
        refreshTransform();
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

    public void refreshTransform() {
        worldTransform.set(transform);
        if (parent != null) {
            worldTransform.applyParent(parent.worldTransform);
        }
        getChildren().forEach(node -> refreshTransform());
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
