package engine.graphics.model;

import engine.util.Direction;

public final class ModelUtils {

    public static int toDirectionInt(Direction... directions) {
        int result = 0;
        for (var direction : directions) {
            result |= direction.mask;
        }
        return result;
    }

    public static boolean checkCullFace(int coveredFace, int cullFace) {
        return (coveredFace & cullFace) == cullFace;
    }

}
