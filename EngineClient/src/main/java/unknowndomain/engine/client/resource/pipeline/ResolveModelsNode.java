package unknowndomain.engine.client.resource.pipeline;

import com.google.gson.Gson;
import unknowndomain.engine.api.resource.Pipeline;
import unknowndomain.engine.api.resource.Resource;
import unknowndomain.engine.api.resource.ResourceManager;
import unknowndomain.engine.api.resource.ResourcePath;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ResolveModelsNode implements Pipeline.Node {
    @Override
    public Object process(Pipeline.Context context, Object in) throws IOException {
        ResourceManager manager = context.manager();
        List<ResourcePath> paths = (List<ResourcePath>) in;
        List<Model> models = new ArrayList<>();
        for (ResourcePath path : paths) {
            Model loaded = load(manager, path);
            models.add(loaded);
        }
        return models;
    }

    private Model load(ResourceManager manager, ResourcePath path) throws IOException {
        Resource load = manager.load(path);
        if (load == null) {
            return null;
        }

        Model model = new Gson().fromJson(new InputStreamReader(load.open()), Model.class);
        if (model.parent != null) {
            Model parent = load(manager, new ResourcePath("", "minecraft/models/" + model.parent + ".json"));
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