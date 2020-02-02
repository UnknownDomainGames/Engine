package nullengine.client.rendering.model;

import nullengine.client.rendering.math.Transform;
import nullengine.client.rendering.vertex.VertexDataBuf;
import nullengine.util.Direction;

public interface BakedModel {

    /**
     * @param coveredFace {@link ModelUtils#toDirectionInt(Direction...)}
     * @return
     */
    void putVertexes(VertexDataBuf buffer, int coveredFace);

    boolean isFullFace(Direction direction);

    Transform getTransformation(DisplayType type);
}
