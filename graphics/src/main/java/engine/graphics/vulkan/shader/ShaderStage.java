package engine.graphics.vulkan.shader;

import com.google.common.collect.ImmutableMap;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class ShaderStage {
    private VulkanShader shader;
    private String entryPoint;
    private ShaderType stage;
    private Map<Integer, Object> constantSpecification = new HashMap<>();
    private Map<Integer, Class> constantSpecClass = new HashMap<>();
    private Map<Integer, Object> constantSpecUnmodifiable = Collections.unmodifiableMap(constantSpecification);
    private Map<Integer, Class> constantSpecClassUnmodifiable = Collections.unmodifiableMap(constantSpecClass);

    public ShaderStage(VulkanShader shader, ShaderType stage) {
        this(shader, stage, "main");
    }

    public ShaderStage(VulkanShader shader, ShaderType stage, String entryPoint){
        this.shader = shader;
        this.stage = stage;
        this.entryPoint = entryPoint;
    }

    public VulkanShader getShader() {
        return shader;
    }

    public ShaderType getStage() {
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
