package nullengine.client.rendering.light;

import nullengine.client.rendering.camera.Camera;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LightManager {

    public static final int MAX_DIRECTIONAL_LIGHT_COUNT = 4;
    public static final int MAX_POINT_LIGHT_COUNT = 16;
    public static final int MAX_SPOT_LIGHT_COUNT = 10;

    private final List<DirectionalLight> directionalLights = new ArrayList<>();
    private final List<PointLight> pointLights = new ArrayList<>();
    private final List<SpotLight> spotLights = new ArrayList<>();

    public List<DirectionalLight> getDirectionalLights() {
        return directionalLights;
    }

    public List<PointLight> getPointLights() {
        return pointLights;
    }

    public List<SpotLight> getSpotLights() {
        return spotLights;
    }

    public void bind(Camera camera) {
        for (int i = 0; i < directionalLights.size() && i < MAX_DIRECTIONAL_LIGHT_COUNT; i++) {
            directionalLights.get(i).bind("dirLights[" + i + "]");
        }

        var position = camera.getPosition();
        pointLights.sort(Comparator.comparingInt(light -> (int) light.position.distanceSquared(position)));
        for (int i = 0; i < pointLights.size() && i < MAX_POINT_LIGHT_COUNT; i++) {
            pointLights.get(i).bind("pointLights[" + i + "]");
        }

        spotLights.sort(Comparator.comparingInt(light -> (int) light.position.distanceSquared(position)));
        for (int i = 0; i < spotLights.size(); i++) {
            spotLights.get(i).bind("spotLights[" + i + "]");
        }
    }

    public void clear() {
        directionalLights.clear();
        pointLights.clear();
        spotLights.clear();
    }
}
