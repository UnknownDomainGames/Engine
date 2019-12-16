package nullengine.client.rendering.scene.light;

import nullengine.client.rendering.camera.Camera;
import nullengine.client.rendering.management.BindingProxy;

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

    public void add(Light light) {
        if (light instanceof DirectionalLight)
            directionalLights.add((DirectionalLight) light);
        else if (light instanceof PointLight)
            pointLights.add((PointLight) light);
        else if (light instanceof SpotLight)
            spotLights.add((SpotLight) light);
    }

    public void remove(Light light) {
        if (light instanceof DirectionalLight)
            directionalLights.remove(light);
        else if (light instanceof PointLight)
            pointLights.remove(light);
        else if (light instanceof SpotLight)
            spotLights.remove(light);
    }

    public void bind(Camera camera, BindingProxy proxy) {
        for (int i = 0; i < directionalLights.size() && i < MAX_DIRECTIONAL_LIGHT_COUNT; i++) {
            directionalLights.get(i).bind(proxy, "dirLights[" + i + "]");
        }

        var position = camera.getPosition();
        pointLights.sort(Comparator.comparingInt(light -> (int) light.getPosition().distanceSquared(position)));
        for (int i = 0; i < pointLights.size() && i < MAX_POINT_LIGHT_COUNT; i++) {
            pointLights.get(i).bind(proxy, "pointLights[" + i + "]");
        }

        spotLights.sort(Comparator.comparingInt(light -> (int) light.getDirection().distanceSquared(position)));
        for (int i = 0; i < spotLights.size() && i < MAX_SPOT_LIGHT_COUNT; i++) {
            spotLights.get(i).bind(proxy, "spotLights[" + i + "]");
        }
    }

    public void clear() {
        directionalLights.clear();
        pointLights.clear();
        spotLights.clear();
    }
}
