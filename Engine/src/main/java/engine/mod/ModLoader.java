package engine.mod;

import engine.mod.impl.ModCandidate;

import java.util.List;

public interface ModLoader {

    ModContainer load(ModCandidate modCandidate, List<ModContainer> dependencies);
}
