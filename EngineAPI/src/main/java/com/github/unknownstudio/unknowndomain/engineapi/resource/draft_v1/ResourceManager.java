package com.github.unknownstudio.unknowndomain.engineapi.resource.draft_v1;

import com.github.unknownstudio.unknowndomain.engineapi.resource.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ResourceManager {
    private Map<String, Resource> cache;
    private Map<String, List<ResourceSource>> sourceMap;

    // private list: ResourceSourceWrapper[] = []

    private Resource putCache(Resource res) {
        this.cache.put(res.location().toString(), res);
        return res;
    }

    public void clearCache() {
        this.cache.clear();
    }

    public void clearAll() {
        this.cache.clear();
        this.sourceMap.clear();
        // this.list = []
    }

    public void addResourceSource(ResourceSource source) {
        PackInfo info = source.info();
        String[] domains = source.domains();
        // const wrapper = { info, source, domains }

        for (String domain : domains) {
            List<ResourceSource> sources = this.sourceMap.computeIfAbsent(domain, k -> new ArrayList<>());
            sources.add(source);
            // sources.push(this.list.length)
        }
        // this.list.push(wrapper)
    }

    public Resource load(ResourceLocation location) {
        Resource cached = this.cache.get(location.toString());
        if (cached != null) return cached;

        List<ResourceSource> sources = this.sourceMap.get(location.getDomain());
        if (sources == null) return null;

        for (ResourceSource src : sources) {
            Resource loaded = src.load(location);
            if (loaded == null) continue;
            return this.putCache(loaded);
        }

        return null;
    }
}
