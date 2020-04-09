package engine.graphics.vulkan.shader;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ShaderStage {
    private VulkanShader shader;
    private String entryPoint;
    private VKShaderType stage;
    private Map<Integer, Object> constantSpecification = new HashMap<>();
    private Map<Integer, Class> constantSpecClass = new HashMap<>();
    private Map<Integer, Object> constantSpecUnmodifiable = Collections.unmodifiableMap(constantSpecification);
    private Map<Integer, Class> constantSpecClassUnmodifiable = Collections.unmodifiableMap(constantSpecClass);

    public ShaderStage(VulkanShader shader, VKShaderType stage) {
        this(shader, stage, "main");
    }

    public ShaderStage(VulkanShader shader, VKShaderType stage, String entryPoint) {
        this.shader = shader;
        this.stage = stage;
        this.entryPoint = entryPoint;
    }

    public VulkanShader getShader() {
        return shader;
    }

    public VKShaderType getStage() {
        return stage;
    }

    public String getEntryPoint() {
        return entryPoint;
    }

    public Map<Integer, Object> getConstantSpecification() {
        return constantSpecUnmodifiable;
    }

    public Map<Integer, Class> getConstantSpecClass() {
        return constantSpecClassUnmodifiable;
    }

    public ShaderStage specifyConstant(int id, Object data){
        constantSpecification.put(id, data);
        constantSpecClass.put(id, data.getClass());
        return this;
    }

}
