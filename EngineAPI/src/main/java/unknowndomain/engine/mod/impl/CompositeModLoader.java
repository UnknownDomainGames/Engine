package unknowndomain.engine.mod.impl;

import unknowndomain.engine.mod.ModContainer;
import unknowndomain.engine.mod.ModDescriptor;
import unknowndomain.engine.mod.ModLoader;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class CompositeModLoader implements ModLoader {

    private final List<ModLoader> modLoaders = new LinkedList<>();

    public CompositeModLoader() {
    }

    public CompositeModLoader(ModLoader... modLoaders) {
        Collections.addAll(this.modLoaders, modLoaders);
    }

    public List<ModLoader> getModLoaders() {
        return modLoaders;
    }

    @Override
    public ModContainer load(ModDescriptor descriptor) {
        for (ModLoader modLoader : modLoaders) {
            ModContainer modContainer = modLoader.load(descriptor);
            if (modContainer != null) {
                return modContainer;
            }
        }
        return null;
    }
}
