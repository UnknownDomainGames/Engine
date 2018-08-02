package unknowndomain.engine.client.model;

import unknowndomain.engine.api.resource.ResourceManager;
import unknowndomain.engine.api.util.DomainedPath;

import java.util.HashMap;
import java.util.Map;

public class ModelManager {
    private ResourceManager resourceManager;
    private Map<DomainedPath, GLMesh> managedMesh = new HashMap<>();
    private Map<DomainedPath, Mesh> meshMap = new HashMap<>();
    // private Map<DomainedPath, Model> managedModels = new HashMap<>();

    public ModelManager(ResourceManager resourceManager) {
        this.resourceManager = resourceManager;
    }

    // public Model getModel(DomainedPath path) {
        // Model model = managedModels.get(path);
        // if (model != null) return model;
//        resourceManager.load(path);
        // return null;
    // }
}
