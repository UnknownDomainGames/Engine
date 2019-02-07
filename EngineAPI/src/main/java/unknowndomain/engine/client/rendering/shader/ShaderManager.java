package unknowndomain.engine.client.rendering.shader;

import org.joml.*;
import unknowndomain.engine.Platform;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ShaderManager {

    public static final ShaderManager INSTANCE = new ShaderManager();

    private final Map<String, ShaderProgram> loadedShaders;
    private final Map<String, ShaderProgramBuilder> registeredShaders;

    private ShaderProgram lastShader;

    private ShaderProgram usingShader;

    private boolean overriding;

    private ShaderManager() {
        loadedShaders = new HashMap<>();
        registeredShaders = new HashMap<>();
        overriding = false;
    }

    @Deprecated
    public ShaderProgram createShader(String name, Shader... shaders) {
        if (loadedShaders.containsKey(name)) {
            Platform.getLogger().warn(String.format("repeating creating shader program with the same name! name: %s", name));
            return loadedShaders.get(name);
        }
        ShaderProgram sp = new ShaderProgram();
        sp.init(shaders);
        loadedShaders.put(name, sp);
        return sp;
    }

    public void registerShader(String name, ShaderProgramBuilder builder) {
        if (registeredShaders.containsKey(name)) {
            throw new IllegalStateException();
        }
        registeredShaders.put(name, builder);
    }

    public void reload() {
        for (ShaderProgram shaderProgram : loadedShaders.values()) {
            shaderProgram.dispose();
        }
        loadedShaders.clear();

        for (Map.Entry<String, ShaderProgramBuilder> entry : registeredShaders.entrySet()) {
            loadedShaders.put(entry.getKey(), entry.getValue().build());
        }
    }

    public void bindShader(ShaderProgram sp) {
        if (!overriding) {
            bindShaderInternal(sp);
        }
    }

    public void bindShader(String name) {
        if (loadedShaders.containsKey(name)) {
            bindShader(loadedShaders.get(name));
        } else {
            Platform.getLogger().warn("Shader Program %s cannot be found at Shader Manager!", name);
        }
    }

    public Optional<ShaderProgram> getShader(String name) {
        return Optional.ofNullable(loadedShaders.get(name));
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
