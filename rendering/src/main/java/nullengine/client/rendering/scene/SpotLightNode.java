package nullengine.client.rendering.scene;

import nullengine.client.rendering.light.SpotLight;
import org.joml.Vector3f;

public class SpotLightNode extends Node3D {
    private final SpotLight light;

    public SpotLightNode(SpotLight light) {
        this.light = light;
        this.light.setPosition(getWorldTransform().getTranslation());
        this.light.setDirection(new Vector3f());
        scene().addChangeListener((observable, oldValue, newValue) -> {
            if (oldValue != null) oldValue.getLightManager().remove(light);
            if (newValue != null) newValue.getLightManager().add(light);
        });
    }

    public SpotLight getLight() {
        return light;
    }

    @Override
    protected void refreshTransform() {
        super.refreshTransform();
        this.light.getDirection().set(0, 0, -1).rotate(getWorldRotation());
    }
}
