package unknowndomain.engine.client.rendering.light;

import org.joml.Vector3f;
import unknowndomain.engine.client.rendering.shader.ShaderProgram;

public class SpotLight extends PointLight {
    Vector3f direction;
    float cutoffAngle; // in Radian
    float outerCutoffAngle; // in Radian

    @Override
    public void bind(ShaderProgram program, String fieldName) {
        super.bind(program, fieldName);
        program.setUniform(fieldName + ".direction", direction);
        program.setUniform(fieldName + ".cutoffCosine", (float)Math.cos(cutoffAngle));
        program.setUniform(fieldName + ".direction", (float)Math.cos(outerCutoffAngle));
    }
}
