package unknowndomain.engine.client.resource;

import org.apache.commons.lang3.tuple.Pair;
import unknowndomain.engine.client.model.GLMesh;
import unknowndomain.engine.client.texture.GLTextureMap;

import java.io.IOException;
import java.util.*;

/**
 * The one manager for the game. Not really considering concurrency now.
 * <p>
 * Not considering the data dependency here (like reload resource toggling re-bake model and others...)
 * <p>
 * I suggest that we should handle data dependency in other places (not here)
 */
public class ResourceManagerImpl implements Pipeline.Endpoint, ResourceManager, Pipeline {
    private Map<String, Resource> cache = new HashMap<>();
    private List<ResourceSource> sources = new ArrayList<>();

    private Map<String, Pair<List<Node>, List<Endpoint>>> pipes = new HashMap<>();

    private GLTextureMap textureMap;
    private List<GLMesh> blockModels;

    private Resource putCache(Resource res) {
        this.cache.put(res.location().toString(), res);
        return res;
    }

    @Override
    public void clearCache() {
        this.cache.clear();
    }

    @Override
    public void clearAll() {
        this.cache.clear();
        this.sources.clear();
    }

    @Override
    public void invalidate(ResourcePath path) {
        cache.remove(path.toString());
    }

    /**
     * Add resource source to the manager
     *
     * @param source
     */
    @Override
    public void addResourceSource(ResourceSource source) {
        this.sources.add(source);
    }

    /**
     * Load the resource by iterating the sources
     */
    @Override
    public Resource load(ResourcePath location) throws IOException {
        Resource cached = this.cache.get(location.toString());
        if (cached != null) return cached;

        for (ResourceSource src : sources) {
            Resource loaded = src.load(location);
            if (loaded == null) continue;
            return this.putCache(loaded);
        }

        return null;
    }

    @Override
    public void accept(String source, Object content) {
        switch (source) {
            case "BlockModels":
                this.blockModels = (List<GLMesh>) content;
                break;
            case "TextureMap":
                this.textureMap = (GLTextureMap) content;
                break;
            case "Textures":
                break;
            case "EntityModels":
                break;
        }
    }

    @Override
    public Pipeline add(String line, Node... node) {
        if (node == null || node.length == 0) return this;
        List<Node> nodes = pipes.computeIfAbsent(line, (k) -> Pair.of(new ArrayList<>(), new ArrayList<>())).getLeft();
        nodes.addAll(Arrays.asList(node));
        return this;
    }

    @Override
    public Pipeline subscribe(String line, Endpoint... node) {
        if (node == null || node.length == 0) return this;
        List<Endpoint> nodes = pipes.computeIfAbsent(line, (k) -> Pair.of(new ArrayList<>(), new ArrayList<>())).getRight();
        nodes.addAll(Arrays.asList(node));
        return this;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T push(String name, Object o) throws Exception {
        List<Exception> exceptions = new ArrayList<>();
        Map<String, Object> cached = new HashMap<>();
        Context context = new Context() {
            @Override
            public ResourceManager manager() {
                return ResourceManagerImpl.this;
            }

            @Override
            public <T> T get(String name) {
                return (T) cached.get(name);
            }

            @Override
            public void emit(String name, Object o) {
                Pair<List<Node>, List<Endpoint>> pair = pipes.get(name);
                if (pair != null) {
                    if (pair.getLeft().size() != 0) {
                        try {
                            Object pack = o;
                            for (Node node : pair.getLeft()) pack = node.process(this, pack);
                            cached.put(name, pack);
                            final Object result = pack;
                            List<Endpoint> right = pair.getRight();
                            if (right != null && pack != null) right.forEach(e -> e.accept(name, result));
                        } catch (Exception e) {
                            exceptions.add(e);
                        }
                    } else if (pair.getRight().size() != 0) {
                        pair.getRight().forEach(e -> e.accept(name, o));
                    }
                }
            }
        };
        context.emit(name, o);

        if (exceptions.size() != 0) {
            Exception ex = new Exception("Some errors occurred when we process the " + name);
            for (Exception e : exceptions) {
                ex.addSuppressed(e);
            }
            throw ex;
        }
        return (T) cached.get(name);
    }
}
