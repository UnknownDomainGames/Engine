package unknowndomain.engine.client.rendering.block.model;

import unknowndomain.engine.util.Facing;

import javax.annotation.Nullable;

// TODO: Implement it.
public interface BakedBlockModel {

    float[] getFaceData(@Nullable Facing facing);
}
