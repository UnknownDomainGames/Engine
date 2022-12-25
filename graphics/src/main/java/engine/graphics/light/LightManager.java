package engine.graphics.light;

import engine.graphics.camera.Camera;
import engine.graphics.util.Struct;
import org.joml.Matrix4fc;
import org.joml.Vector3fc;
import org.joml.Vector4f;
import org.joml.Vector4fc;
import org.lwjgl.system.MemoryUtil;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LightManager implements Struct {

    public static final int MAX_DIRECTIONAL_LIGHTS = 1;
    public static final int MAX_POINT_LIGHTS = 16;
    public static final int MAX_SPOT_LIGHTS = 16;

    private static final int POINT_LIGHT_OFFSET = MAX_DIRECTIONAL_LIGHTS * 32;
    private static final int SPOT_LIGHT_OFFSET = POINT_LIGHT_OFFSET + MAX_POINT_LIGHTS * 48;
    private static final int AMBIENT_LIGHT_OFFSET = SPOT_LIGHT_OFFSET + MAX_SPOT_LIGHTS * 80;
    private static final int BUFFER_CAPACITY = AMBIENT_LIGHT_OFFSET + 16;

    private final List<DirectionalLight> directionalLights = new ArrayList<>();
    private final List<PointLight> pointLights = new ArrayList<>();
    private final List<SpotLight> spotLights = new ArrayList<>();
    private final Vector4f ambientLight = new Vector4f();

    public List<DirectionalLight> getDirectionalLights() {
        return directionalLights;
    }

    public void add(DirectionalLight light) {
        directionalLights.add(light);
    }

    public void remove(DirectionalLight light) {
        directionalLights.remove(light);
    }

    public List<PointLight> getPointLights() {
        return pointLights;
    }

    public void add(PointLight light) {
        pointLights.add(light);
    }

    public void remove(PointLight light) {
        pointLights.remove(light);
    }

    public List<SpotLight> getSpotLights() {
        return spotLights;
    }

    public void add(SpotLight light) {
        spotLights.add(light);
    }

    public void remove(SpotLight light) {
        spotLights.remove(light);
    }

    public Vector4fc getAmbientLight() {
        return ambientLight;
    }

    public void setAmbientLight(float r, float g, float b) {
        this.ambientLight.set(r, g, b, 1f);
    }

    public void setAmbientLight(float value) {
        this.ambientLight.set(value, value, value, 1f);
    }

    public void setup(Camera camera) {
        Vector3fc position = camera.getPosition();
        pointLights.sort(Comparator.comparingDouble(light -> light.getPosition().distanceSquared(position)));
        spotLights.sort(Comparator.comparingDouble(light -> light.getPosition().distanceSquared(position)));

        Matrix4fc viewMatrix = camera.getViewMatrix();
        for (DirectionalLight directionalLight : directionalLights) {
            directionalLight.setup(viewMatrix);
        }
        for (PointLight pointLight : pointLights) {
            pointLight.setup(viewMatrix);
        }
        for (SpotLight spotLight : spotLights) {
            spotLight.setup(viewMatrix);
        }
    }

    @Override
    public int sizeof() {
        return BUFFER_CAPACITY;
    }

    @Override
    public ByteBuffer get(int index, ByteBuffer buffer) {
        MemoryUtil.memSet(buffer, 0);
        for (int i = 0, size = Math.min(directionalLights.size(), MAX_DIRECTIONAL_LIGHTS); i < size; i++) {
            directionalLights.get(i).get(i * 32, buffer);
        }

        for (int i = 0, size = Math.min(pointLights.size(), MAX_POINT_LIGHTS); i < size; i++) {
            pointLights.get(i).get(POINT_LIGHT_OFFSET + i * 48, buffer);
        }

        for (int i = 0, size = Math.min(spotLights.size(), MAX_SPOT_LIGHTS); i < size; i++) {
            spotLights.get(i).get(SPOT_LIGHT_OFFSET + i * 80, buffer);
        }

        ambientLight.get(AMBIENT_LIGHT_OFFSET, buffer);
        return buffer;
    }

    public void clear() {
        directionalLights.clear();
        pointLights.clear();
        spotLights.clear();
    }
}
