package unknowndomain.engine.api.world;

import unknowndomain.engine.api.registry.RegistryEntry;
import unknowndomain.engine.api.resource.ResourcePath;

public abstract class WorldProvider implements RegistryEntry<WorldProvider> {
	
	public abstract String getName();
	
	public abstract World createWorld(String worldName);

	@Override
	public Class<WorldProvider> getRegistryType(){
		return WorldProvider.class;
	}

    private ResourcePath registryName;

    @Override
    public ResourcePath getRegistryName() {
        return registryName;
    }

    @Override
    public WorldProvider setRegistryName(ResourcePath location) {
        registryName = location;
        return this;
    }
}
