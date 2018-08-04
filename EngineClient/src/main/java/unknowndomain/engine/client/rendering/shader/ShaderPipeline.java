package unknowndomain.engine.client.rendering.shader;

import unknowndomain.engine.api.client.shader.Shader;
import unknowndomain.engine.api.client.shader.ShaderType;
import unknowndomain.engine.api.resource.Pipeline;
import unknowndomain.engine.client.rendering.RendererShaderProgramCommon;

import java.util.Map;

public class ShaderPipeline {
    public static void create(Pipeline pipeline) {
        pipeline.add("Renderer", new CreateShaderNode())
                .add("Renderer", new ShaderToCommonRendererNode());
    }

    private static class ShaderToCommonRendererNode implements Pipeline.Node {
        @Override
        public Object process(Pipeline.Context context, Object in) throws Exception {
            Map<ShaderType, Shader> map = (Map<ShaderType, Shader>) in;
            return new RendererShaderProgramCommon(map.get(ShaderType.VERTEX_SHADER), map.get(ShaderType.FRAGMENT_SHADER));
        }
    }
}
