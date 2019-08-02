package nullengine.client.rendering.light;

import nullengine.client.rendering.shader.ShaderManager;
import org.joml.Vector3f;

public class SpotLight extends PointLight {
    Vector3f direction;
    float cutoffAngle; // in Radian
    float outerCutoffAngle; // in Radian

    @Override
    public void bind(String fieldName) {
        super.bind(fieldName);
        ShaderManager.instance().setUniform(fieldName + ".direction", direction);
        ShaderManager.instance().setUniform(fieldName + ".cutoffCosine", (float) Math.cos(cutoffAngle));
        ShaderManager.instance().setUniform(fieldName + ".direction", (float) Math.cos(outerCutoffAngle));
    }
}
