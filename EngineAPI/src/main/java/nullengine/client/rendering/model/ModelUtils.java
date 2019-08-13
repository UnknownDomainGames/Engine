package nullengine.client.rendering.model;

import nullengine.util.Direction;

import java.util.ArrayList;

public final class ModelUtils {

    public static int toDirectionInt(Direction... directions) {
        int result = 0;
        for (var direction : directions) {
            result |= 1 << direction.index;
        }
        return result;
    }

    public static int toDirectionInt(boolean[] directions) {
        if (directions.length != 6) {
            throw new IllegalArgumentException();
        }

        int result = 0;
        for (int i = 0; i < directions.length; i++) {
            if (directions[i]) {
                result |= 1 << i;
            }
        }
        return result;
    }

    public static Direction[] toDirections(int directionInt) {
        var directions = new ArrayList<Direction>(6);
        for (int i = 1; i < 1 << 6; i <<= 1) {
            if ((directionInt & i) > 0) {
                directions.add(Direction.valueOf(i));
            }
        }
        return directions.toArray(new Direction[0]);
    }

    public static boolean chechCullFace(int coveredFace, int cullFace) {
        return (coveredFace & cullFace) == cullFace;
    }

}
