package nullengine.client.rendering.scene;

import com.github.mouse0w0.observable.collection.ObservableCollections;
import com.github.mouse0w0.observable.collection.ObservableList;
import nullengine.client.rendering.queue.RenderQueue;
import nullengine.client.rendering.scene.light.LightManager;

import java.util.ArrayList;
import java.util.List;

public class Scene {

    private final ObservableList<Node> children = ObservableCollections.observableList(new ArrayList<>());

    private final RenderQueue renderQueue = new RenderQueue();
    private final LightManager lightManager = new LightManager();

    public Scene() {
        children.addChangeListener(change -> {
            for (Node node : change.getAdded()) node.scene.set(Scene.this);
            for (Node node : change.getRemoved()) node.scene.set(null);
        });
    }

    public List<Node> getChildren() {
        return children;
    }

    public Node getNode(String id) {
        return null;
    }

    public RenderQueue getRenderQueue() {
        return renderQueue;
    }

    public LightManager getLightManager() {
        return lightManager;
    }


}
