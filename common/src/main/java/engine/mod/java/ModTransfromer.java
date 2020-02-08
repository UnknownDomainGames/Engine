package engine.mod.java;

import engine.mod.ModContainer;

public interface ModTransfromer {

    byte[] transform(ModContainer mod, String className, byte[] bytecode);
}
