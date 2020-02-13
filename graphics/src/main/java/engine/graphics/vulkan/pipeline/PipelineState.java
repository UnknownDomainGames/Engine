package engine.graphics.vulkan.pipeline;

import engine.graphics.math.ViewSpace;
import engine.graphics.util.DrawMode;
import engine.graphics.vertex.VertexFormat;
import engine.graphics.vulkan.shader.ShaderStage;
import org.joml.Vector4i;
import org.lwjgl.vulkan.VK10;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PipelineState {
    //Parent of this states
    Pipeline parent;

    //Flags of the pipeline when created
    private final boolean disableOptimization;
    private final boolean allowDerivatives;
    private final boolean isDerivative;

    //Shader stages
    private ShaderStage[] shaders;

    //Vertex input attributes
    private VertexFormat format;

    //Input assembly state
    private DrawMode drawMode;
    private boolean allowStripsFansRestart;

    //Viewport state
    private ViewSpace[] viewSpaces;
    private Vector4i[] scissors;

    //Rasterization state
    private boolean depthClampEnable;
    private boolean rasterizerDiscardEnable;
    private PolygonMode polygonMode;
    private CullMode cullMode;
    private boolean cwAsFrontFace; //set false to use ccw as front face
    private boolean depthBiasEnable;
    private float depthBiasConstFactor;
    private float depthBiasClamp;
    private float depthBiasSlopeFactor;

    //Multisample state
    //TODO

    //Depth Stencil state
    private boolean depthTestEnable;
    private boolean depthWriteEnable;
    private CompareOp depthCompareOp;
    private boolean depthBoundTestEnable;
    private boolean stencilTestEnable;
    private StencilOpState stencilFront;
    private StencilOpState stencilBack;
    private float minDepthBounds;
    private float maxDepthBounds;

    //Dynamic state
    private List<DynamicState> dynamicStates;


    private PipelineState(boolean disableOptimization, boolean allowDerivatives, boolean isDerivative) {
        this.disableOptimization = disableOptimization;
        this.allowDerivatives = allowDerivatives;
        this.isDerivative = isDerivative;
    }

    public boolean isOptimizationDisabled() {
        return disableOptimization;
    }

    public boolean isDerivativesAllowed() {
        return allowDerivatives;
    }

    public boolean isDerivative() {
        return isDerivative;
    }

    public void setViewSpace(ViewSpace viewSpace, int index) {
        if(dynamicStates.contains(DynamicState.VIEWSPACE)) {
            this.viewSpaces[index] = viewSpace;
        }
    }

    public void setScissor(Vector4i scissor, int index) {
        if(dynamicStates.contains(DynamicState.SCISSOR)) {
            this.scissors[index] = scissor;
        }
    }

    public void setDepthBiasClamp(float depthBiasClamp) {
        if(dynamicStates.contains(DynamicState.DEPTH_BIAS)) {
            this.depthBiasClamp = depthBiasClamp;
        }
    }

    public void setDepthBiasConstFactor(float depthBiasConstFactor) {
        if(dynamicStates.contains(DynamicState.DEPTH_BIAS)) {
            this.depthBiasConstFactor = depthBiasConstFactor;
        }
    }

    public void setDepthBiasSlopeFactor(float depthBiasSlopeFactor) {
        if(dynamicStates.contains(DynamicState.DEPTH_BIAS)) {
            this.depthBiasSlopeFactor = depthBiasSlopeFactor;
        }
    }

    public void setMinDepthBounds(float minDepthBounds) {
        if(dynamicStates.contains(DynamicState.DEPTH_BOUNDS)) {
            this.minDepthBounds = minDepthBounds;
        }
    }

    public void setMaxDepthBounds(float maxDepthBounds) {
        if(dynamicStates.contains(DynamicState.DEPTH_BOUNDS)) {
            this.maxDepthBounds = maxDepthBounds;
        }
    }

    public ShaderStage[] getShaders() {
        return Arrays.copyOf(shaders, shaders.length);
    }

    public VertexFormat getFormat() {
        return format;
    }

    public DrawMode getDrawMode() {
        return drawMode;
    }

    public boolean isAllowStripsFansRestart() {
        return allowStripsFansRestart;
    }

    public ViewSpace[] getViewSpaces() {
        return Arrays.copyOf(viewSpaces, viewSpaces.length);
    }

    public Vector4i[] getScissors() {
        return Arrays.copyOf(scissors, scissors.length);
    }

    public boolean isDepthClampEnable() {
        return depthClampEnable;
    }

    public boolean isRasterizerDiscardEnable() {
        return rasterizerDiscardEnable;
    }

    public PolygonMode getPolygonMode() {
        return polygonMode;
    }

    public CullMode getCullMode() {
        return cullMode;
    }

    public boolean isCwAsFrontFace() {
        return cwAsFrontFace;
    }

    public boolean isDepthBiasEnable() {
        return depthBiasEnable;
    }

    public float getDepthBiasConstFactor() {
        return depthBiasConstFactor;
    }

    public float getDepthBiasClamp() {
        return depthBiasClamp;
    }

    public float getDepthBiasSlopeFactor() {
        return depthBiasSlopeFactor;
    }

    public boolean isDepthTestEnable() {
        return depthTestEnable;
    }

    public boolean isDepthWriteEnable() {
        return depthWriteEnable;
    }

    public boolean isDepthBoundTestEnable() {
        return depthBoundTestEnable;
    }

    public boolean isStencilTestEnable() {
        return stencilTestEnable;
    }

    public float getMinDepthBounds() {
        return minDepthBounds;
    }

    public float getMaxDepthBounds() {
        return maxDepthBounds;
    }

    public List<DynamicState> getDynamicStates() {
        return Collections.unmodifiableList(dynamicStates);
    }

    public static class Builder {
        //Flags of the pipeline when created
        private boolean disableOptimization;
        private boolean allowDerivatives;
        private boolean isDerivative;

        //Shader stages
        private List<ShaderStage> shaders = new ArrayList<>();

        //Vertex input attributes
        private VertexFormat format;

        //Input assembly state
        private DrawMode drawMode;
        private boolean allowStripsFansRestart;

        //Viewport state
        private List<ViewSpace> viewSpaces;
        private List<Vector4i> scissors;

        //Rasterization state
        private boolean depthClampEnable;
        private boolean rasterizerDiscardEnable;
        private PolygonMode polygonMode;
        private CullMode cullMode;
        private boolean cwAsFrontFace; //set false to use ccw as front face
        private boolean depthBiasEnable;
        private float depthBiasConstFactor;
        private float depthBiasClamp;
        private float depthBiasSlopeFactor;

        //Multisample state
        //TODO

        //Depth Stencil state
        private boolean depthTestEnable;
        private boolean depthWriteEnable;
        private CompareOp depthCompareOp;
        private boolean depthBoundTestEnable;
        private boolean stencilTestEnable;
        private StencilOpState stencilFront;
        private StencilOpState stencilBack;
        private float minDepthBounds;
        private float maxDepthBounds;

        //Dynamic state
        private List<DynamicState> dynamicStates;

        public Builder(){

        }

        public boolean isDisableOptimization() {
            return disableOptimization;
        }

        public Builder setDisableOptimization(boolean disableOptimization) {
            this.disableOptimization = disableOptimization;
            return this;
        }

        public boolean isAllowDerivatives() {
            return allowDerivatives;
        }

        public Builder setAllowDerivatives(boolean allowDerivatives) {
            this.allowDerivatives = allowDerivatives;
            return this;
        }

        public boolean isDerivative() {
            return isDerivative;
        }

        public Builder setDerivative(boolean derivative) {
            isDerivative = derivative;
            return this;
        }

        public List<ShaderStage> getShaders() {
            return shaders;
        }

        public Builder setShaders(ShaderStage... shaders) {
            this.shaders = List.of(shaders);
            return this;
        }

        public VertexFormat getFormat() {
            return format;
        }

        public Builder setFormat(VertexFormat format) {
            this.format = format;
            return this;
        }

        public DrawMode getDrawMode() {
            return drawMode;
        }

        public Builder setDrawMode(DrawMode drawMode) {
            this.drawMode = drawMode;
            return this;
        }

        public boolean isAllowStripsFansRestart() {
            return allowStripsFansRestart;
        }

        public Builder setAllowStripsFansRestart(boolean allowStripsFansRestart) {
            this.allowStripsFansRestart = allowStripsFansRestart;
            return this;
        }

        public List<ViewSpace> getViewSpaces() {
            return viewSpaces;
        }

        public Builder setViewSpaces(ViewSpace... viewSpaces) {
            this.viewSpaces = List.of(viewSpaces);
            return this;
        }

        public List<Vector4i> getScissors() {
            return scissors;
        }

        public Builder setScissors(Vector4i... scissors) {
            this.scissors = List.of(scissors);
            return this;
        }

        public boolean isDepthClampEnable() {
            return depthClampEnable;
        }

        public Builder setDepthClampEnable(boolean depthClampEnable) {
            this.depthClampEnable = depthClampEnable;
            return this;
        }

        public boolean isRasterizerDiscardEnable() {
            return rasterizerDiscardEnable;
        }

        public Builder setRasterizerDiscardEnable(boolean rasterizerDiscardEnable) {
            this.rasterizerDiscardEnable = rasterizerDiscardEnable;
            return this;
        }

        public PolygonMode getPolygonMode() {
            return polygonMode;
        }

        public Builder setPolygonMode(PolygonMode polygonMode) {
            this.polygonMode = polygonMode;
            return this;
        }

        public CullMode getCullMode() {
            return cullMode;
        }

        public Builder setCullMode(CullMode cullMode) {
            this.cullMode = cullMode;
            return this;
        }

        public boolean isCwAsFrontFace() {
            return cwAsFrontFace;
        }

        public Builder setCwAsFrontFace(boolean cwAsFrontFace) {
            this.cwAsFrontFace = cwAsFrontFace;
            return this;
        }

        public boolean isDepthBiasEnable() {
            return depthBiasEnable;
        }

        public Builder setDepthBiasEnable(boolean depthBiasEnable) {
            this.depthBiasEnable = depthBiasEnable;
            return this;
        }

        public float getDepthBiasConstFactor() {
            return depthBiasConstFactor;
        }

        public Builder setDepthBiasConstFactor(float depthBiasConstFactor) {
            this.depthBiasConstFactor = depthBiasConstFactor;
            return this;
        }

        public float getDepthBiasClamp() {
            return depthBiasClamp;
        }

        public Builder setDepthBiasClamp(float depthBiasClamp) {
            this.depthBiasClamp = depthBiasClamp;
            return this;
        }

        public float getDepthBiasSlopeFactor() {
            return depthBiasSlopeFactor;
        }

        public Builder setDepthBiasSlopeFactor(float depthBiasSlopeFactor) {
            this.depthBiasSlopeFactor = depthBiasSlopeFactor;
            return this;
        }

        public boolean isDepthTestEnable() {
            return depthTestEnable;
        }

        public Builder setDepthTestEnable(boolean depthTestEnable) {
            this.depthTestEnable = depthTestEnable;
            return this;
        }

        public boolean isDepthWriteEnable() {
            return depthWriteEnable;
        }

        public Builder setDepthWriteEnable(boolean depthWriteEnable) {
            this.depthWriteEnable = depthWriteEnable;
            return this;
        }

        public boolean isDepthBoundTestEnable() {
            return depthBoundTestEnable;
        }

        public Builder setDepthBoundTestEnable(boolean depthBoundTestEnable) {
            this.depthBoundTestEnable = depthBoundTestEnable;
            return this;
        }

        public boolean isStencilTestEnable() {
            return stencilTestEnable;
        }

        public Builder setStencilTestEnable(boolean stencilTestEnable) {
            this.stencilTestEnable = stencilTestEnable;
            return this;
        }

        public CompareOp getDepthCompareOp() {
            return depthCompareOp;
        }

        public Builder setDepthCompareOp(CompareOp depthCompareOp) {
            this.depthCompareOp = depthCompareOp;
            return this;
        }

        public StencilOpState getStencilFront() {
            return stencilFront;
        }

        public Builder setStencilFront(StencilOpState stencilFront) {
            this.stencilFront = stencilFront;
            return this;
        }

        public StencilOpState getStencilBack() {
            return stencilBack;
        }

        public Builder setStencilBack(StencilOpState stencilBack) {
            this.stencilBack = stencilBack;
            return this;
        }

        public float getMinDepthBounds() {
            return minDepthBounds;
        }

        public Builder setMinDepthBounds(float minDepthBounds) {
            this.minDepthBounds = minDepthBounds;
            return this;
        }

        public float getMaxDepthBounds() {
            return maxDepthBounds;
        }

        public Builder setMaxDepthBounds(float maxDepthBounds) {
            this.maxDepthBounds = maxDepthBounds;
            return this;
        }

        public List<DynamicState> getDynamicStates() {
            return dynamicStates;
        }

        public Builder setDynamicStates(DynamicState... dynamicStates) {
            this.dynamicStates = List.of(dynamicStates);
            return this;
        }

        public PipelineState build(){
            var state = new PipelineState(disableOptimization, allowDerivatives, isDerivative);
            state.allowStripsFansRestart = allowStripsFansRestart;
            state.cullMode = cullMode;
            state.cwAsFrontFace = cwAsFrontFace;
            state.depthBiasClamp = depthBiasClamp;
            state.depthBiasConstFactor = depthBiasConstFactor;
            state.depthBiasEnable = depthBiasEnable;
            state.depthBiasSlopeFactor = depthBiasSlopeFactor;
            state.depthBoundTestEnable = depthBoundTestEnable;
            state.depthClampEnable = depthClampEnable;
            state.depthTestEnable = depthTestEnable;
            state.depthWriteEnable = depthWriteEnable;
            state.drawMode = drawMode;
            state.format = format;
            state.maxDepthBounds = maxDepthBounds;
            state.minDepthBounds = minDepthBounds;
            state.polygonMode = polygonMode;
            state.rasterizerDiscardEnable = rasterizerDiscardEnable;
            state.scissors = scissors.toArray(Vector4i[]::new);
            state.shaders = shaders.toArray(ShaderStage[]::new);
            state.stencilTestEnable = stencilTestEnable;
            state.viewSpaces = viewSpaces.toArray(ViewSpace[]::new);
            state.dynamicStates = dynamicStates;
            return state;
        }
    }

    public enum PolygonMode {
        SOLID(VK10.VK_POLYGON_MODE_FILL),
        LINE(VK10.VK_POLYGON_MODE_LINE),
        POINT(VK10.VK_POLYGON_MODE_POINT);

        private final int vk;

        PolygonMode(int vk){
            this.vk = vk;
        }

        public int getVk() {
            return vk;
        }
    }

    public enum CullMode {
        NONE(VK10.VK_CULL_MODE_NONE),
        FRONT(VK10.VK_CULL_MODE_FRONT_BIT),
        BACK(VK10.VK_CULL_MODE_BACK_BIT),
        ALL(VK10.VK_CULL_MODE_FRONT_AND_BACK);

        private final int vk;

        CullMode(int vk){
            this.vk = vk;
        }

        public int getVk() {
            return vk;
        }
    }

    public enum CompareOp {
        ALWAYS(VK10.VK_COMPARE_OP_ALWAYS),
        NEVER(VK10.VK_COMPARE_OP_NEVER),
        EQUAL(VK10.VK_COMPARE_OP_EQUAL),
        NOT_EQUAL(VK10.VK_COMPARE_OP_NOT_EQUAL),
        LESS_THAN(VK10.VK_COMPARE_OP_LESS),
        GREATER_OR_EQUAL(VK10.VK_COMPARE_OP_GREATER_OR_EQUAL),
        GREATER(VK10.VK_COMPARE_OP_GREATER),
        LESS_OR_EQUAL(VK10.VK_COMPARE_OP_LESS_OR_EQUAL);

        private final int vk;

        CompareOp(int vk){
            this.vk = vk;
        }

        public int getVk() {
            return vk;
        }
    }

    public enum StencilOp {
        KEEP(VK10.VK_STENCIL_OP_KEEP),
        ZERO_OUT(VK10.VK_STENCIL_OP_ZERO),
        REPLACE(VK10.VK_STENCIL_OP_REPLACE),
        INCREASE_AND_CLAMP(VK10.VK_STENCIL_OP_INCREMENT_AND_CLAMP),
        INCREASE_AND_WRAP(VK10.VK_STENCIL_OP_INCREMENT_AND_WRAP),
        DECREASE_AND_CLAMP(VK10.VK_STENCIL_OP_INCREMENT_AND_CLAMP),
        DECREASE_AND_WRAP(VK10.VK_STENCIL_OP_INCREMENT_AND_WRAP),
        INVERT(VK10.VK_STENCIL_OP_INVERT);

        private final int vk;

        StencilOp(int vk){
            this.vk = vk;
        }

        public int getVk() {
            return vk;
        }
    }

    public static class StencilOpState {
        StencilOp failOp;
        StencilOp passOp;
        StencilOp depthFailOp;
        CompareOp compareOp;
        int compareMask;
        int writeMask;
        int reference;

        public CompareOp getCompareOp() {
            return compareOp;
        }

        public int getCompareMask() {
            return compareMask;
        }

        public int getReference() {
            return reference;
        }

        public int getWriteMask() {
            return writeMask;
        }

        public StencilOp getDepthFailOp() {
            return depthFailOp;
        }

        public StencilOp getFailOp() {
            return failOp;
        }

        public StencilOp getPassOp() {
            return passOp;
        }
    }

    public enum DynamicState {
        VIEWSPACE(VK10.VK_DYNAMIC_STATE_VIEWPORT),
        SCISSOR(VK10.VK_DYNAMIC_STATE_SCISSOR),
        DEPTH_BIAS(VK10.VK_DYNAMIC_STATE_DEPTH_BIAS),
        BLEND_CONSTANTS(VK10.VK_DYNAMIC_STATE_BLEND_CONSTANTS),
        DEPTH_BOUNDS(VK10.VK_DYNAMIC_STATE_DEPTH_BOUNDS),
        STENCIL_COMPARE_MASK(VK10.VK_DYNAMIC_STATE_STENCIL_COMPARE_MASK),
        STENCIL_WRITE_MASK(VK10.VK_DYNAMIC_STATE_STENCIL_WRITE_MASK),
        STENCIL_REFERENCE(VK10.VK_DYNAMIC_STATE_STENCIL_REFERENCE);

        private final int vk;

        DynamicState(int vk){
            this.vk = vk;
        }

        public int getVk() {
            return vk;
        }
    }
}
