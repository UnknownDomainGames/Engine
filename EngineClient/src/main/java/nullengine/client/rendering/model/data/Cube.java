package nullengine.client.rendering.model.data;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import nullengine.util.Direction;
import org.joml.Vector3f;

import java.lang.reflect.Type;

public final class Cube {
    public Vector3f from;
    public Vector3f to;
    public Face[] faces;

    public enum Deserializer implements JsonDeserializer<Cube> {
        INSTANCE;

        @Override
        public Cube deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            var object = json.getAsJsonObject();
            var cube = new Cube();
            cube.from = context.deserialize(object.get("from"), Vector3f.class);
            cube.to = context.deserialize(object.get("to"), Vector3f.class);
            checkMinAndMax(cube.from, cube.to);

            cube.faces = new Face[6];
            var faces = object.getAsJsonObject("faces");
            for (var face : faces.entrySet()) {
                var direction = Direction.valueOf(face.getKey().toUpperCase());
                cube.faces[direction.index] = context.deserialize(face.getValue(), Face.class);
            }
            return cube;
        }

        private void checkMinAndMax(Vector3f min, Vector3f max) {
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
}
