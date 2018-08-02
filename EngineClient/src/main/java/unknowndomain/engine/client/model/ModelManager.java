package unknowndomain.engine.client.model;

import org.lwjgl.system.MemoryUtil;
import unknowndomain.engine.api.resource.ResourceManager;
import unknowndomain.engine.api.util.DomainedPath;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

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
