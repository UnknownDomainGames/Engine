package unknowndomain.engine._mod.impl;

import unknowndomain.engine._mod.ModContainer;
import unknowndomain.engine._mod.ModLoader;

import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;

public class CompositeModLoader implements ModLoader {

    private final List<ModLoader> modLoaders = new LinkedList<>();

    public List<ModLoader> getModLoaders() {
        return modLoaders;
    }

    @Override
    public ModContainer load(Path path) {
        for (ModLoader modLoader : modLoaders) {
            ModContainer modContainer = modLoader.load(path);
            if (modContainer != null) {
                return modContainer;
            }
        }
        return null;
    }
}
