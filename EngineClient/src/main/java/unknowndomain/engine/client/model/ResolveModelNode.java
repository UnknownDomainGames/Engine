package unknowndomain.engine.client.model;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.function.Function;

import com.google.gson.Gson;

import unknowndomain.engine.api.resource.Resource;
import unknowndomain.engine.api.resource.ResourceManager;
import unknowndomain.engine.api.util.DomainedPath;
import unknowndomain.engine.client.model.MinecraftModelLoader.Model;

class ResolveModelNode implements PipelineNode<DomainedPath> {
    private ResourceManager manager;

    ResolveModelNode(ResourceManager manager) {
        this.manager = manager;
    }

    @Override
    public Model process(DomainedPath path) throws IOException {
        Resource load = manager.load(path);
        if (load == null) return null;

        Model model = new Gson().fromJson(new InputStreamReader(load.open()), Model.class);
        if (model.parent != null) {
            Model parent = process(new DomainedPath("", "minecraft/models/" + model.parent + ".json"));
            if (parent == null) throw new IllegalArgumentException("Missing parent");
            if (model.elements == null) model.elements = parent.elements;
            if (model.ambientocclusion == null) model.ambientocclusion = parent.ambientocclusion;
            if (model.display == null) model.display = parent.display;

            if (parent.textures != null) model.textures.putAll(parent.textures);
        }
        model.ambientocclusion = model.ambientocclusion == null ? false : model.ambientocclusion;
        
        return model;
    }

}