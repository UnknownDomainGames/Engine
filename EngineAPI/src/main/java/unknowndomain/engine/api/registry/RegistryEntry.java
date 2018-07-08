package unknowndomain.engine.api.registry;

import com.google.common.reflect.TypeToken;

import unknowndomain.engine.api.resource.ResourceLocation;

public interface RegistryEntry<T> {

	ResourceLocation getRegistryName();

	T setRegistryName(ResourceLocation location);
	
	Class<T> getRegistryType();

	default T setRegistryName(String name){
		return setRegistryName(new ResourceLocation(name));
	}
	
	public static abstract class Impl<T extends RegistryEntry<T>> implements RegistryEntry<T> {
		
		private final TypeToken<T> token = new TypeToken<T>(getClass()){};
		private ResourceLocation location;
		
		@Override
		public final ResourceLocation getRegistryName() {
			return location;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public final T setRegistryName(ResourceLocation location) {
			this.location = location;
			return (T) this;
		}

		@SuppressWarnings("unchecked")
		@Override
		public final Class<T> getRegistryType() {
			return (Class<T>) token.getRawType();
		};
	}
}
