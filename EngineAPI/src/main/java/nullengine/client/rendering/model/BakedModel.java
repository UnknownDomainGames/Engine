package nullengine.client.rendering.model;

import nullengine.client.rendering.gl.GLBuffer;
import nullengine.client.rendering.math.Transform;
import nullengine.util.Direction;

public interface BakedModel {

    /**
     * @param coveredFace {@link ModelUtils#toDirectionInt(Direction...)}
     * @return
     */
    void putVertexes(GLBuffer buffer, int coveredFace);

    boolean isFullFace(Direction direction);

    Transform getTransformation(DisplayType type);
}
