package unknowndomain.engine.client.resource.pipeline;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import unknowndomain.engine.api.resource.Resource;
import unknowndomain.engine.api.resource.ResourceManager;
import unknowndomain.engine.api.util.DomainedPath;

class ResolveModelsNode implements ResourcePipeline.Node {

    private Model load(ResourceManager manager, DomainedPath path) throws IOException {
        Resource load = manager.load(path);
        if (load == null) {
            return null;
        }

        Model model = new Gson().fromJson(new InputStreamReader(load.open()), Model.class);
        if (model.parent != null) {
            Model parent = load(manager, new DomainedPath("", "minecraft/models/" + model.parent + ".json"));
            if (parent == null) throw new IllegalArgumentException("Missing parent");
            if (model.elements == null) model.elements = parent.elements;
            if (model.ambientocclusion == null) model.ambientocclusion = parent.ambientocclusion;
            if (model.display == null) model.display = parent.display;

            if (parent.textures != null) model.textures.putAll(parent.textures);
        }
        model.ambientocclusion = model.ambientocclusion == null ? false : model.ambientocclusion;
        return model;
    }

    @Override
    public void process(ResourcePipeline.Context context) throws IOException {
        ResourceManager manager = context.manager();
        List<DomainedPath> paths = context.in("ModelPaths");
        List<Model> models = new ArrayList<>();
        for (DomainedPath path : paths) {
            Model loaded = load(manager, path);
            if (loaded != null) {
                models.add(loaded);
            }
        }
        context.out("ResolvedModels", models);
    }

}