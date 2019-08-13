package nullengine.client.rendering.model;

import nullengine.client.rendering.util.buffer.GLBuffer;
import nullengine.util.Direction;

public interface BakedModel {

    /**
     * @param coveredFace {@link ModelUtils#toDirectionInt(Direction...)}
     * @return
     */
    void putVertexes(GLBuffer buffer, int coveredFace);

    boolean isFullFace(Direction direction);
}
