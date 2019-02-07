package unknowndomain.engine.client.rendering.shader;

import com.github.mouse0w0.lib4j.observable.value.MutableValue;
import com.github.mouse0w0.lib4j.observable.value.ObservableValue;
import com.github.mouse0w0.lib4j.observable.value.SimpleMutableObjectValue;
import org.joml.*;
import unknowndomain.engine.Platform;

import java.util.HashMap;
import java.util.Map;

public class ShaderManager {

    public static final ShaderManager INSTANCE = new ShaderManager();

    private final Map<String, MutableValue<ShaderProgram>> loadedShaders;
    private final Map<String, ShaderProgramBuilder> registeredShaders;

    private ShaderProgram lastShader;

    private ShaderProgram usingShader;

    private boolean overriding;

    private ShaderManager() {
        loadedShaders = new HashMap<>();
        registeredShaders = new HashMap<>();
        overriding = false;
    }

    public ObservableValue<ShaderProgram> registerShader(String name, ShaderProgramBuilder builder) {
        if (registeredShaders.containsKey(name)) {
            throw new IllegalStateException();
        }
        registeredShaders.put(name, builder);
        MutableValue<ShaderProgram> value = new SimpleMutableObjectValue<>();
        loadedShaders.put(name, value);
        return value.toImmutable();
    }

    public void reload() {
        for (MutableValue<ShaderProgram> value : loadedShaders.values()) {
            ShaderProgram shaderProgram = value.getValue();
            if (shaderProgram != null) {
                shaderProgram.dispose();
            }
        }

        for (Map.Entry<String, ShaderProgramBuilder> entry : registeredShaders.entrySet()) {
            loadedShaders.get(entry.getKey()).setValue(entry.getValue().build());
        }
    }

    public void bindShader(ShaderProgram sp) {
        if (!overriding) {
            bindShaderInternal(sp);
        }
    }

    public void bindShader(String name) {
        if (loadedShaders.containsKey(name)) {
            bindShader(loadedShaders.get(name).getValue());
        } else {
            Platform.getLogger().warn("Shader Program %s cannot be found at Shader Manager!", name);
        }
    }

    public ObservableValue<ShaderProgram> getShader(String name) {
        return loadedShaders.get(name).toImmutable();
    }

    private void bindShaderInternal(ShaderProgram sp) {
        if (usingShader != null) {
            lastShader = usingShader;
        }
        usingShader = sp;
        usingShader.use();
    }

    public void restoreShader() {
        if (!overriding && lastShader != null) {
            var tmp = lastShader;
            bindShaderInternal(tmp);
        }
    }

    public void bindShaderOverriding(ShaderProgram sp) {
        overriding = true;
        bindShaderInternal(sp);
    }

    public void unbindOverriding() {
        overriding = false;
        restoreShader();
    }

    public void setUniform(String location, int value) {
        usingShader.setUniform(location, value);
    }

    public void setUniform(String location, float value) {
        usingShader.setUniform(location, value);
    }

    public void setUniform(String location, boolean value) {
        usingShader.setUniform(location, value);
    }

    public void setUniform(String location, Vector2fc value) {
        usingShader.setUniform(location, value);
    }

    public void setUniform(String location, Vector3fc value) {
        usingShader.setUniform(location, value);
    }

    public void setUniform(String location, Vector4fc value) {
        usingShader.setUniform(location, value);
    }

    public void setUniform(String location, Matrix3fc value) {
        usingShader.setUniform(location, value);
    }

    public void setUniform(String location, Matrix4fc value) {
        usingShader.setUniform(location, value);
    }

    public void setUniform(String location, Matrix4fc[] value) {
        usingShader.setUniform(location, value);
    }

    public ShaderProgram getUsingShader() {
        return usingShader;
    }
}
