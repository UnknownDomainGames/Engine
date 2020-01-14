package nullengine.client.rendering3d;

import nullengine.client.rendering.light.PointLight;

public class PointLightNode extends Node3D {
    private final PointLight light;

    public PointLightNode(PointLight light) {
        this.light = light;
        this.light.setPosition(getWorldTransform().getTranslation());
        scene().addChangeListener((observable, oldValue, newValue) -> {
            if (oldValue != null) oldValue.getLightManager().remove(light);
            if (newValue != null) newValue.getLightManager().add(light);
        });
    }

    public PointLight getLight() {
        return light;
    }
}
