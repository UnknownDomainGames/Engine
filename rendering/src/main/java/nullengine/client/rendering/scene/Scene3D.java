package nullengine.client.rendering.scene;

import com.github.mouse0w0.observable.collection.ObservableCollections;
import com.github.mouse0w0.observable.collection.ObservableList;
import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import nullengine.client.rendering.scene.light.LightManager;
import nullengine.client.rendering.scene.queue.RenderQueue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Scene3D {

    private final ObservableList<Node3D> children = ObservableCollections.observableList(new ArrayList<>());

    private final RenderQueue renderQueue = new RenderQueue();
    private final LightManager lightManager = new LightManager();

    private final MutableObjectValue<CameraNode> primaryCamera = new SimpleMutableObjectValue<>();

    final Map<String, Node3D> idToNode = new HashMap<>();

    public Scene3D() {
        children.addChangeListener(change -> {
            for (Node3D node : change.getAdded()) node.scene.set(Scene3D.this);
            for (Node3D node : change.getRemoved()) node.scene.set(null);
        });
    }

    public List<Node3D> getChildren() {
        return children;
    }

    public void addNode(Node3D node) {
        children.add(node);
    }

    public void addNode(Node3D... nodes) {
        children.addAll(nodes);
    }

    public Node3D getNodeById(String id) {
        return idToNode.get(id);
    }

    public RenderQueue getRenderQueue() {
        return renderQueue;
    }

    public LightManager getLightManager() {
        return lightManager;
    }

    public MutableObjectValue<CameraNode> primaryCamera() {
        return primaryCamera;
    }

    public CameraNode getPrimaryCamera() {
        return primaryCamera.get();
    }

    public void setPrimaryCamera(CameraNode camera) {
        this.primaryCamera.set(camera);
    }

    public void doUpdate(float partial) {
        children.forEach(child -> child.doUpdate(partial));
    }
}
