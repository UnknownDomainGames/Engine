package unknowndomain.engine.mod;

public interface ModLoader {
	ModContainer loadMod(ModIdentity modId);

	boolean hasMod(ModIdentity modId);
}
