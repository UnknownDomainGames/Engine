package engine.graphics.model.voxel.block;

import com.google.gson.JsonElement;
import engine.client.asset.AssetURL;
import engine.graphics.model.voxel.ModelJsonUtils;
import engine.util.Direction;
import org.joml.Vector3f;
import org.joml.Vector3fc;

import java.util.Set;

final class Cube {
    Vector3f from;
    Vector3f to;
    Face[] faces;

    static Cube deserialize(BlockModel blockModel, JsonElement json, Set<AssetURL> requestTextures) {
        var object = json.getAsJsonObject();
        var cube = new Cube();
        cube.from = ModelJsonUtils.vector3f(object.get("From"));
        cube.to = ModelJsonUtils.vector3f(object.get("To"));
        checkMinAndMax(cube.from, cube.to);

        cube.faces = new Face[6];
        var faces = object.getAsJsonObject("Faces");
        for (var face : faces.entrySet()) {
            var direction = Direction.valueOf(face.getKey().toUpperCase());
            cube.faces[direction.index] = Face.deserialize(blockModel, face.getValue(), requestTextures);
        }
        return cube;
    }

    Vector3fc[] getFacePositions(Direction direction) {
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
