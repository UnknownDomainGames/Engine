package unknowndomain.engine.mod;

import java.util.LinkedList;

public class ModLoaderWrapper implements ModLoader {
    private LinkedList<ModLoader> loaders = new LinkedList<>();

    public ModLoaderWrapper add(ModLoader l) {
        loaders.addFirst(l);
        return this;
    }

    @Override
    public ModContainer load(ModIdentifier identifier) {
        for (ModLoader l : loaders) {
            ModContainer c = l.load(identifier);
            if (c != null)
                return c;
        }
        return null;
    }
}