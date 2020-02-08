package engine.graphics.model.voxel;

import engine.graphics.math.Transform;

public class ModelLoadUtils {

    public static void fillTransformationArray(Transform[] transforms) {
        for (int i = 0; i < transforms.length; i++) {
            if (transforms[i] == null) {
                transforms[i] = Transform.IDENTITY;
            }
        }
    }
}
