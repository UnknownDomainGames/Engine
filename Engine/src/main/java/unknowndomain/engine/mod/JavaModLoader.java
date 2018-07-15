package unknowndomain.engine.mod;

import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.lang3.Validate;

import unknowndomain.engine.api.mod.ModContainer;
import unknowndomain.engine.api.mod.ModLoader;

public class JavaModLoader implements ModLoader {
	
	private final Path path;
	
	public JavaModLoader(Path path) {
		Validate.notNull(path);
		if(!Files.exists(path))
			throw new IllegalArgumentException(path.toAbsolutePath() + " don't exist.");
		if(!Files.isDirectory(path))
			throw new IllegalArgumentException(path.toAbsolutePath() + " isn't a directory.");
		
		this.path = path;
	}

	@Override
	public ModContainer loadMod(String modId) {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	public ModContainer getModContainer(String modId) {
		// TODO 自动生成的方法存根
		return null;
	}

	@Override
	public boolean hasMod(String modId) {
		// TODO 自动生成的方法存根
		return false;
	}
	
}
