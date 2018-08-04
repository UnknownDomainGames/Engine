package unknowndomain.engine.client.rendering.shader;

import org.apache.commons.io.IOUtils;
import unknowndomain.engine.Platform;
import unknowndomain.engine.client.shader.Shader;
import unknowndomain.engine.client.shader.ShaderType;
import unknowndomain.engine.client.resource.Pipeline;
import unknowndomain.engine.client.resource.ResourceManager;
import unknowndomain.engine.client.resource.ResourcePath;

import java.util.EnumMap;

import static org.lwjgl.opengl.GL20.*;

public class CreateShaderNode implements Pipeline.Node {
    private Shader loadShader(String content, ShaderType type) {
        int shaderId = glCreateShader(type.getGlEnum());
        glShaderSource(shaderId, content, "utf-8");

        glCompileShader(shaderId);
        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0) {
            Platform.getLogger().warn(String.format("Error compiling shader code for %s, log: %s", content, glGetShaderInfoLog(shaderId, 2048)));
        }
        return new Shader(shaderId, type);
    }

    @Override
    public Object process(Pipeline.Context context, Object in) throws Exception {
        String name = (String) in;

        ResourceManager manager = context.manager();
        byte[] vert = manager.load(new ResourcePath("", "/unknowndomain/shader/" + name + ".vert")).cache();
        if (vert == null)
            throw new Exception();
        Shader vertShader = loadShader(IOUtils.toString(vert, "utf-8"), ShaderType.VERTEX_SHADER);

        byte[] frag = manager.load(new ResourcePath("", "/unknowndomain/shader/" + name + ".frag")).cache();
        if (frag == null)
            throw new Exception();

        Shader fragShader = loadShader(IOUtils.toString(frag, "utf-8"), ShaderType.FRAGMENT_SHADER);

        EnumMap<ShaderType, Shader> map = new EnumMap<>(ShaderType.class);
        map.put(ShaderType.VERTEX_SHADER, vertShader);
        map.put(ShaderType.FRAGMENT_SHADER, fragShader);
        System.out.println(map);
        return map;
    }
}
