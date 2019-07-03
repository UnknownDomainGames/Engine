package nullengine.mod;

import nullengine.mod.impl.ModCandidate;

import java.util.List;

public interface ModLoader {

    ModContainer load(ModCandidate modCandidate, List<ModContainer> dependencies);
}
