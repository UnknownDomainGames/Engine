package unknowndomain.engine.api.world;

import unknowndomain.engine.api.registry.RegistryEntry;
import unknowndomain.engine.api.util.DomainedPath;

public abstract class WorldProvider implements RegistryEntry<WorldProvider> {
	
	public abstract String getName();
	
	public abstract World createWorld(String worldName);

	@Override
	public Class<WorldProvider> getRegistryType(){
		return WorldProvider.class;
	}

	private DomainedPath registryName;

    @Override
    public DomainedPath getRegistryName() {
        return registryName;
    }

    @Override
    public WorldProvider setRegistryName(DomainedPath location) {
        registryName = location;
        return this;
    }
}
