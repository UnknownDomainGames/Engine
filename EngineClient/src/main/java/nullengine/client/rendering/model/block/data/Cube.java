package nullengine.client.rendering.model.block.data;

import com.google.gson.JsonElement;
import nullengine.util.Direction;
import org.joml.Vector3f;
import org.joml.Vector3fc;

public final class Cube {
    public Vector3f from;
    public Vector3f to;
    public Face[] faces;

    public Vector3fc[] getFacePositions(Direction direction) {
        Vector3fc[] positions = new Vector3fc[4];
        switch (direction) {
            case NORTH:
                positions[0] = new Vector3f(to.x(), from.y(), from.z());
                positions[1] = new Vector3f(from.x(), from.y(), from.z());
                positions[2] = new Vector3f(from.x(), to.y(), from.z());
                positions[3] = new Vector3f(to.x(), to.y(), from.z());
                break;
            case SOUTH:
                positions[0] = new Vector3f(from.x(), from.y(), to.z());
                positions[1] = new Vector3f(to.x(), from.y(), to.z());
                positions[2] = new Vector3f(to.x(), to.y(), to.z());
                positions[3] = new Vector3f(from.x(), to.y(), to.z());
                break;
            case EAST:
                positions[0] = new Vector3f(to.x(), from.y(), to.z());
                positions[1] = new Vector3f(to.x(), from.y(), from.z());
                positions[2] = new Vector3f(to.x(), to.y(), from.z());
                positions[3] = new Vector3f(to.x(), to.y(), to.z());
                break;
            case WEST:
                positions[0] = new Vector3f(from.x(), from.y(), from.z());
                positions[1] = new Vector3f(from.x(), from.y(), to.z());
                positions[2] = new Vector3f(from.x(), to.y(), to.z());
                positions[3] = new Vector3f(from.x(), to.y(), from.z());
                break;
            case UP:
                positions[0] = new Vector3f(from.x(), to.y(), to.z());
                positions[1] = new Vector3f(to.x(), to.y(), to.z());
                positions[2] = new Vector3f(to.x(), to.y(), from.z());
                positions[3] = new Vector3f(from.x(), to.y(), from.z());
                break;
            case DOWN:
                positions[0] = new Vector3f(to.x(), from.y(), to.z());
                positions[1] = new Vector3f(from.x(), from.y(), to.z());
                positions[2] = new Vector3f(from.x(), from.y(), from.z());
                positions[3] = new Vector3f(to.x(), from.y(), from.z());
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + direction);
        }
        return positions;
    }

    public static Cube deserialize(BlockModel blockModel, JsonElement json) {
        var object = json.getAsJsonObject();
        var cube = new Cube();
        cube.from = ModelJsonUtils.vector3f(object.get("from"));
        cube.to = ModelJsonUtils.vector3f(object.get("to"));
        checkMinAndMax(cube.from, cube.to);

        cube.faces = new Face[6];
        var faces = object.getAsJsonObject("faces");
        for (var face : faces.entrySet()) {
            var direction = Direction.valueOf(face.getKey().toUpperCase());
            cube.faces[direction.index] = Face.deserialize(blockModel, face.getValue());
        }
        return cube;
    }

    private static void checkMinAndMax(Vector3f min, Vector3f max) {
        float t;
        if (min.x > max.x) {
            t = min.x;
            min.x = max.x;
            max.x = t;
        }
        if (min.y > max.y) {
            t = min.y;
            min.y = max.y;
            max.y = t;
        }
        if (min.z > max.z) {
            t = min.z;
            min.z = max.z;
            max.z = t;
        }
    }
}
