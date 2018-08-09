package unknowndomain.engine.client.resource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The one manager for the game. Not really considering concurrency now.
 * <p>
 * Not considering the data dependency here (like reload resource toggling re-bake model and others...)
 * <p>
 * I suggest that we should handle data dependency in other places (not here)
 */
public class ResourceManagerImpl implements ResourceManager {
    private Map<String, Resource> cache = new HashMap<>();
    private List<ResourceSource> sources = new ArrayList<>();


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

}
