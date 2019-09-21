package nullengine.client.rendering.model.voxel;

import nullengine.math.Transformation;

public class ModelLoadUtils {

    public static void fillTransformationArray(Transformation[] transformations) {
        for (int i = 0; i < transformations.length; i++) {
            if (transformations[i] == null) {
                transformations[i] = Transformation.DEFAULT;
            }
        }
    }
}
