package nullengine.mod.java;

import nullengine.mod.ModContainer;

public interface ModTransfromer {

    byte[] transform(ModContainer mod, String className, byte[] bytecode);
}
