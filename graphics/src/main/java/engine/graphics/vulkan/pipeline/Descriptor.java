package engine.graphics.vulkan.pipeline;

import engine.graphics.shader.ShaderModuleInfo;
import engine.graphics.vulkan.shader.ShaderType;

import java.util.List;

public class Descriptor {
    private String name;
    private ShaderModuleInfo.VariableType type;
    private ShaderModuleInfo.LayoutVariableQualifier layout;
    private List<ShaderType> stageReferred;

    public Descriptor(String name, ShaderModuleInfo.VariableType type, ShaderModuleInfo.LayoutVariableQualifier layout, List<ShaderType> stageReferred) {
        this.name = name;
        this.type = type;
        this.layout = layout;
        this.stageReferred = stageReferred;
    }

    public String getName() {
        return name;
    }

    public ShaderModuleInfo.VariableType getType() {
        return type;
    }

    public ShaderModuleInfo.LayoutVariableQualifier getLayout() {
        return layout;
    }

    public List<ShaderType> getStageReferred() {
        return stageReferred;
    }

    public static class Builder {
        private String name;
        private ShaderModuleInfo.VariableType type;
        private ShaderModuleInfo.LayoutVariableQualifier layout;
        private List<ShaderType> stageReferred;

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setType(ShaderModuleInfo.VariableType type) {
            this.type = type;
            return this;
        }

        public Builder setLayout(ShaderModuleInfo.LayoutVariableQualifier layout) {
            this.layout = layout;
            return this;
        }

        public Builder setStageReferred(List<ShaderType> stageReferred) {
            this.stageReferred = stageReferred;
            return this;
        }

        public String getName() {
            return name;
        }

        public ShaderModuleInfo.VariableType getType() {
            return type;
        }

        public ShaderModuleInfo.LayoutVariableQualifier getLayout() {
            return layout;
        }

        public List<ShaderType> getStageReferred() {
            return stageReferred;
        }

        public Descriptor build() {
            return new Descriptor(name, type, layout, stageReferred);
        }
    }
}
