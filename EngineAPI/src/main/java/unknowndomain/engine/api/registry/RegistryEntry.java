package unknowndomain.engine.api.registry;

import com.google.common.reflect.TypeToken;

public interface RegistryEntry<T> {

	RegistryName getRegistryName();

	T setRegistryName(RegistryName location);
	
	Class<T> getRegistryType();

	default T setRegistryName(String name){
		return setRegistryName(new RegistryName(name));
	}
	
	public static abstract class Impl<T extends RegistryEntry<T>> implements RegistryEntry<T> {
		
		private final TypeToken<T> token = new TypeToken<T>(getClass()){};
		private RegistryName location;
		
		@Override
		public final RegistryName getRegistryName() {
			return location;
		}
		
		@SuppressWarnings("unchecked")
		@Override
		public final T setRegistryName(RegistryName location) {
			this.location = location;
			return (T) this;
		}
		
		public final T setRegistryName(String domain, String path) {
			return setRegistryName(new RegistryName(domain, path));
		}
		
		public final T setRegistryName(String path) {
			throw new UnsupportedOperationException();
			//TODO: 
			//return setRegistryName(new ResourceLocation(domain, path));
		}

		@SuppressWarnings("unchecked")
		@Override
		public final Class<T> getRegistryType() {
			return (Class<T>) token.getRawType();
		};
	}
}
