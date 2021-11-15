package engine.graphics.model;

import engine.graphics.math.Transform;
import engine.graphics.vertex.VertexDataBuffer;
import engine.util.Direction;

public interface BakedModel {

    /**
     * @param coveredFace {@link ModelUtils#toDirectionInt(Direction...)}
     * @return
     */
    void putVertexes(VertexDataBuffer buffer, int coveredFace);

    boolean isFullFace(Direction direction);

    Transform getTransformation(DisplayType type);
}
