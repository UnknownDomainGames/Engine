package unknowndomain.engine.client.rendering.shader;

import unknowndomain.engine.Platform;
import unknowndomain.engine.client.asset.AssetPath;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class ShaderProgramBuilder {

    private final Map<ShaderType, AssetPath> shaders = new HashMap<>();

    public ShaderProgramBuilder addShader(@Nonnull ShaderType type, @Nonnull AssetPath path) {
        Objects.requireNonNull(type);
        Objects.requireNonNull(path);
        shaders.put(type, path);
        return this;
    }

    public ShaderProgram build() {
        Shader[] loadedShaders = new Shader[shaders.size()];
        int i = 0;
        for (Map.Entry<ShaderType, AssetPath> entry : shaders.entrySet()) {
            Optional<Path> shaderPath = Platform.getEngineClient().getAssetManager().getSourceManager().getPath(entry.getValue());
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
