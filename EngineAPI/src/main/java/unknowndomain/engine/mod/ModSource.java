package unknowndomain.engine.mod;

import java.util.Collection;

public interface ModSource {

    Collection<LoadableMod> getLoadableMods();

    LoadableMod getLoadableMod(String modId);

    boolean hasLoadableMod(String modId);

    void refresh();
}
