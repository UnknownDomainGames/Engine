package engine.graphics.shader;

import engine.graphics.util.Struct;

public interface UniformBlock {

    String getName();

    Struct get();

    void set(Struct value);
}
