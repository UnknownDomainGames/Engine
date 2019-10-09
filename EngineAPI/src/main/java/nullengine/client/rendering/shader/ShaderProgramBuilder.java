package nullengine.client.rendering.shader;

import nullengine.Platform;
import nullengine.client.asset.AssetURL;
import nullengine.client.rendering.gl.shader.ShaderType;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ShaderProgramBuilder {

    private final Map<ShaderType, AssetURL> shaders = new HashMap<>();

    public ShaderProgramBuilder addShader(@Nonnull ShaderType type, @Nonnull AssetURL path) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(path);
        shaders.put(type, path);
        return this;
    }

    public ShaderProgram build() {
        Shader[] loadedShaders = new Shader[shaders.size()];
        int i = 0;
        for (Map.Entry<ShaderType, AssetURL> entry : shaders.entrySet()) {
            var shaderPath = Platform.getEngineClient().getAssetManager().getSourceManager().getPath(entry.getValue().toFileLocation());
            if (shaderPath.isPresent()) {
                try {
                    loadedShaders[i] = Shader.create(Files.readAllBytes(shaderPath.get()), entry.getKey());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            i++;
        }
        ShaderProgram shaderProgram = new ShaderProgram();
        shaderProgram.init(loadedShaders);
        return shaderProgram;
    }
}
