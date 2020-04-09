package engine.graphics.gl.shader;

import com.github.mouse0w0.observable.value.MutableObjectValue;
import com.github.mouse0w0.observable.value.ObservableObjectValue;
import com.github.mouse0w0.observable.value.SimpleMutableObjectValue;
import com.google.gson.JsonParser;
import engine.graphics.shader.ShaderType;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShaderManager {

    private static final Map<String, MutableObjectValue<ShaderProgram>> registeredShader = new HashMap<>();
    private static final JsonParser JSON_PARSER = new JsonParser();

    private static final ShaderResourceLoader loader = ClassPathShaderResourceLoader.DEFAULT;

    public static ObservableObjectValue<ShaderProgram> register(String name) {
        return registeredShader.computeIfAbsent(name, key -> new SimpleMutableObjectValue<>(load(name))).toUnmodifiable();
    }

    public static ShaderProgram load(String name) {
        return load(name, loader);
    }

    public static ShaderProgram load(String name, ShaderResourceLoader loader) {
        var input = loader.openStream(name + ".json");
        if (input == null) {
            throw new RuntimeException("Cannot load shader " + name);
        }

        List<CompiledShader> shaders = new ArrayList<>();
        try (var reader = new InputStreamReader(input)) {
            var jsonShader = JSON_PARSER.parse(reader).getAsJsonObject();
            if (jsonShader.has("Vertex")) {
                shaders.add(loadShader(ShaderType.VERTEX_SHADER, jsonShader.get("Vertex").getAsString(), loader));
            }
            if (jsonShader.has("Fragment")) {
                shaders.add(loadShader(ShaderType.FRAGMENT_SHADER, jsonShader.get("Fragment").getAsString(), loader));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return new ShaderProgram(shaders.toArray(CompiledShader[]::new));
    }

    private static CompiledShader loadShader(ShaderType type, String name, ShaderResourceLoader loader) {
        var input = loader.openStream(name);
        if (input == null) {
            throw new RuntimeException();
        }
        try (input) {
            return CompiledShader.compile(GLShaderType.valueOf(type), IOUtils.toString(input, StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
