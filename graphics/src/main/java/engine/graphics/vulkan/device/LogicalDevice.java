package engine.graphics.vulkan.device;

import engine.graphics.math.ViewSpace;
import engine.graphics.shader.ShaderModuleInfo;
import engine.graphics.vulkan.CommandPool;
import engine.graphics.vulkan.Queue;
import engine.graphics.vulkan.VKDrawMode;
import engine.graphics.vulkan.buffer.VulkanBuffer;
import engine.graphics.vulkan.pipeline.Descriptor;
import engine.graphics.vulkan.pipeline.Pipeline;
import engine.graphics.vulkan.pipeline.PipelineState;
import engine.graphics.vulkan.render.RenderPass;
import engine.graphics.vulkan.synchronize.VulkanFence;
import engine.graphics.vulkan.texture.VKColorFormat;
import engine.graphics.vulkan.texture.VKTexture;
import engine.graphics.vulkan.util.VulkanUtils;
import org.joml.Vector4i;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.lwjgl.vulkan.VK10.*;

public class LogicalDevice {
    private VkDevice vk;
    private final PhysicalDevice physicalDevice;
    private boolean released = false;

    public LogicalDevice(PhysicalDevice physicalDevice, VkDevice device){
        this.vk = device;
        this.physicalDevice = physicalDevice;
    }

    public VulkanBuffer createBuffer(long size, VulkanBuffer.Usage[] usage){
        return createBufferInternal(size,usage,false, new int[0]);
    }

    public VulkanBuffer createSharedBuffer(long size, VulkanBuffer.Usage[] usage, int[] index){
        return createBufferInternal(size, usage, true, index);
    }

    private VulkanBuffer createBufferInternal(long size, VulkanBuffer.Usage[] usage, boolean shared, int[] queueFamilyIndices){
        try(var stack = MemoryStack.stackPush()){
            var createInfo = VkBufferCreateInfo.callocStack(stack);
            createInfo.sType(VK10.VK_STRUCTURE_TYPE_BUFFER_CREATE_INFO)
            .size(size).usage(Stream.of(usage).mapToInt(VulkanBuffer.Usage::getVk).reduce(0,(flag1,flag2)->flag1|flag2)).sharingMode(VK10.VK_SHARING_MODE_EXCLUSIVE);
            if(shared){
                var queues = stack.mallocInt(queueFamilyIndices.length).put(queueFamilyIndices).flip();
                createInfo.sharingMode(VK10.VK_SHARING_MODE_CONCURRENT).pQueueFamilyIndices(queues);
            }
            var ptr = stack.mallocLong(1);
            var err = VK10.vkCreateBuffer(vk,createInfo,null, ptr);
            if(err != VK_SUCCESS){
                return null;
            }
            return new VulkanBuffer(this, ptr.get(0), Stream.of(usage).collect(Collectors.toList()));
        }
    }

    public VKTexture createTexture(int width, int height, VKColorFormat format, List<VKTexture.Usage> usage, VKTexture.Layout layout) {
        return createTextureInternal(width, height, 0, format, usage, layout, false, new int[0]);
    }

    public VKTexture createSharedTexture(int width, int height, VKColorFormat format, List<VKTexture.Usage> usage, VKTexture.Layout layout, int[] queueFamilyIndex) {
        return createTextureInternal(width, height, 0, format, usage, layout, true, queueFamilyIndex);
    }

    private VKTexture createTextureInternal(int width, int height, int flag, VKColorFormat format, List<VKTexture.Usage> usage, VKTexture.Layout layout, boolean shared, int[] queueFamilyIndices) {
        try (var stack = MemoryStack.stackPush()) {
            var createInfo = VkImageCreateInfo.callocStack(stack).sType(VK_STRUCTURE_TYPE_IMAGE_CREATE_INFO);
            createInfo.imageType(VK_IMAGE_TYPE_2D).samples(VK_SAMPLE_COUNT_1_BIT).mipLevels(0)
                    .extent(vkExtent3D -> vkExtent3D.set(width, height, 1))
                    .format(format.getVk()).usage(usage.stream().mapToInt(VKTexture.Usage::getVk).reduce(0, (i1, i2) -> i1 | i2)).initialLayout(layout.getVk()).arrayLayers(1).flags(flag).sharingMode(VK_SHARING_MODE_EXCLUSIVE);
            if (shared) {
                var queues = stack.mallocInt(queueFamilyIndices.length).put(queueFamilyIndices).flip();
                createInfo.sharingMode(VK10.VK_SHARING_MODE_CONCURRENT).pQueueFamilyIndices(queues);
            }
            var ptr = stack.mallocLong(1);
            var err = vkCreateImage(vk, createInfo, null, ptr);
            if(err != VK_SUCCESS){
                return null;
            }
            return new VKTexture(this, ptr.get(0), format, usage);
        }
    }

    public Queue getQueue(int queueFamilyIndex, int queueIndex){
        if (queueFamilyIndex >= physicalDevice.getQueueFamilyCount()) throw new IllegalArgumentException(String.format("QueueFamily out of bound. size: %d index: %d", physicalDevice.getQueueFamilyCount(), queueFamilyIndex));
        if (queueIndex >= physicalDevice.getQueueFamilyPropertiesList().get(queueFamilyIndex).queueCount())
            throw new IllegalArgumentException(String.format("Queue from QueueFamily#%d out of bound. size: %d index: %d", queueFamilyIndex, physicalDevice.getQueueFamilyPropertiesList().get(queueFamilyIndex).queueCount(), queueIndex));
        try(var stack = MemoryStack.stackPush()) {
            var ptr = stack.mallocPointer(1);
            VK10.vkGetDeviceQueue(vk, queueFamilyIndex, queueIndex, ptr);
            return new Queue(new VkQueue(ptr.get(0), vk));
        }
    }


    public CommandPool createCommandPool(int queueFamilyIndex) {
        if (queueFamilyIndex >= physicalDevice.getQueueFamilyCount()) throw new IllegalArgumentException(String.format("QueueFamily out of bound. size: %d index: %d", physicalDevice.getQueueFamilyCount(), queueFamilyIndex));
        try(var stack = MemoryStack.stackPush()) {
            VkCommandPoolCreateInfo cmdPoolInfo = VkCommandPoolCreateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_COMMAND_POOL_CREATE_INFO)
                    .queueFamilyIndex(queueFamilyIndex)
                    .flags(VK_COMMAND_POOL_CREATE_RESET_COMMAND_BUFFER_BIT | VK_COMMAND_POOL_CREATE_TRANSIENT_BIT);
            var pCmdPool = stack.mallocLong(1);
            int err = vkCreateCommandPool(vk, cmdPoolInfo, null, pCmdPool);
            if (err != VK_SUCCESS) {
//                throw new AssertionError("Failed to create command pool: " + translateVulkanResult(err));
                return null;
            }
            return new CommandPool(pCmdPool.get(0), this);
        }
    }

    public Pipeline createPipeline(PipelineState.Builder state, RenderPass pass, int subpassIndex){
        try(var stack = MemoryStack.stackPush()){
            VkGraphicsPipelineCreateInfo.Buffer infos = VkGraphicsPipelineCreateInfo.callocStack(1, stack);
            var info = infos.get(0);
            info.flags((state.isDisableOptimization() ? VK_PIPELINE_CREATE_DISABLE_OPTIMIZATION_BIT : 0)
            | (state.isAllowDerivatives() ? VK_PIPELINE_CREATE_ALLOW_DERIVATIVES_BIT : 0)
            | (state.isDerivative() ? VK_PIPELINE_CREATE_DERIVATIVE_BIT : 0));

            var vertexInputState = VkPipelineVertexInputStateCreateInfo.callocStack(stack).sType(VK_STRUCTURE_TYPE_PIPELINE_VERTEX_INPUT_STATE_CREATE_INFO);
            var vertexBinding = VkVertexInputBindingDescription.callocStack(1, stack);
            vertexInputState.pVertexBindingDescriptions(vertexBinding);
            vertexBinding.get(0)
                    .binding(0)
                    .stride(state.getFormat().getBytes())
                    .inputRate(VK_VERTEX_INPUT_RATE_VERTEX);
            var vertexAttr = VkVertexInputAttributeDescription.callocStack(state.getFormat().getElements().length, stack);
            vertexInputState.pVertexAttributeDescriptions(vertexAttr);
            int offset = 0;
            for (int i = 0; i < state.getFormat().getIndexCount(); i++) {
                var attr = vertexAttr.get();
                attr.binding(0).location(i).offset(offset).format();
                offset += state.getFormat().getElements()[i].getBytes();
            }
            info.pVertexInputState(vertexInputState);
            var inputAssembly = VkPipelineInputAssemblyStateCreateInfo.callocStack(stack).sType(VK_STRUCTURE_TYPE_PIPELINE_INPUT_ASSEMBLY_STATE_CREATE_INFO);
            var vkDrawMode = VKDrawMode.valueOf(state.getDrawMode());
            inputAssembly.topology(vkDrawMode != null ? vkDrawMode.vk : 0).primitiveRestartEnable(state.isAllowStripsFansRestart());
            info.pInputAssemblyState(inputAssembly);

            var viewportState = VkPipelineViewportStateCreateInfo.callocStack(stack).sType(VK_STRUCTURE_TYPE_PIPELINE_VIEWPORT_STATE_CREATE_INFO);
            var viewports = VkViewport.callocStack(state.getViewSpaces().size(), stack);
            for (ViewSpace viewSpace : state.getViewSpaces()) {
                viewports.get().set(viewSpace.getOrigin().x, viewSpace.getOrigin().y, viewSpace.getSize().x, viewSpace.getSize().y, viewSpace.getDepthRange().x, viewSpace.getDepthRange().y);
            }
            viewports.flip();
            viewportState.pViewports(viewports).viewportCount(viewports.remaining());
            var scissors = VkRect2D.callocStack(state.getScissors().size(), stack);
            for (Vector4i stateScissor : state.getScissors()) {
                var rect2D = scissors.get();
                rect2D.offset().x(stateScissor.x).y(stateScissor.y);
                rect2D.extent().width(stateScissor.z).height(stateScissor.w);
            }
            scissors.flip();
            viewportState.pScissors(scissors).scissorCount(scissors.remaining());
            info.pViewportState(viewportState);

            var rasterizationState = VkPipelineRasterizationStateCreateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_PIPELINE_RASTERIZATION_STATE_CREATE_INFO)
                    .polygonMode(state.getPolygonMode().getVk())
                    .cullMode(state.getCullMode().getVk())
                    .frontFace(state.isCwAsFrontFace() ? VK_FRONT_FACE_CLOCKWISE : VK_FRONT_FACE_COUNTER_CLOCKWISE)
                    .depthClampEnable(state.isDepthClampEnable())
                    .rasterizerDiscardEnable(state.isRasterizerDiscardEnable())
                    .depthBiasEnable(state.isDepthBiasEnable())
                    .lineWidth(1.0f);
            info.pRasterizationState(rasterizationState);

            //TODO configurable state
            var multisampleState = VkPipelineMultisampleStateCreateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_PIPELINE_MULTISAMPLE_STATE_CREATE_INFO)
                    .pSampleMask(null)
                    .rasterizationSamples(VK_SAMPLE_COUNT_1_BIT);
            info.pMultisampleState(multisampleState);

            //TODO configurable state
            var depthStencilState = VkPipelineDepthStencilStateCreateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_PIPELINE_DEPTH_STENCIL_STATE_CREATE_INFO)
                    .depthTestEnable(state.isDepthTestEnable())
                    .depthWriteEnable(state.isDepthWriteEnable())
                    .depthCompareOp(VK_COMPARE_OP_ALWAYS)
                    .depthBoundsTestEnable(state.isDepthBoundTestEnable())
                    .stencilTestEnable(state.isStencilTestEnable());
            depthStencilState.back()
                    .failOp(VK_STENCIL_OP_KEEP)
                    .passOp(VK_STENCIL_OP_KEEP)
                    .compareOp(VK_COMPARE_OP_ALWAYS);
            depthStencilState.front(depthStencilState.back());
            info.pDepthStencilState(depthStencilState);

            //TODO configurable color blend
            var colorWriteMask = VkPipelineColorBlendAttachmentState.callocStack(1, stack)
                    .blendEnable(false)
                    .colorWriteMask(0xF); // <- RGBA
            var colorBlendState = VkPipelineColorBlendStateCreateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_PIPELINE_COLOR_BLEND_STATE_CREATE_INFO)
                    .pAttachments(colorWriteMask);
            info.pColorBlendState(colorBlendState);

            var dynamicState = VkPipelineDynamicStateCreateInfo.callocStack(stack).sType(VK_STRUCTURE_TYPE_PIPELINE_DYNAMIC_STATE_CREATE_INFO);
            var stateflag = stack.mallocInt(state.getDynamicStates().size());
            stateflag.put(state.getDynamicStates().stream().mapToInt(PipelineState.DynamicState::getVk).toArray()).flip();
            dynamicState.pDynamicStates(stateflag);

            var descriptors = new ArrayList<Descriptor.Builder>();
            state.getShaders().stream().flatMap(stage ->
                stage.getShader().getDescriptor().getVariables().stream().map(variable -> variable.getQualifiers().stream()
                        .filter(variableQualifier -> variableQualifier instanceof ShaderModuleInfo.LayoutVariableQualifier)
                        .findFirst().map(variableQualifier -> new Descriptor.Builder().setName(variable.getName()).setLayout((ShaderModuleInfo.LayoutVariableQualifier) variableQualifier).setType(variable.getType()).setStageReferred(List.of(stage.getStage())))
            )).filter(Optional::isPresent).forEach(optional -> {
                var builder = optional.get();
                var potentialMatch = descriptors.stream().filter(builder1 -> builder1.getLayout().getBinding() == builder.getLayout().getBinding()
                        && builder1.getLayout().getDescriptorSet() == builder.getLayout().getDescriptorSet()
                        && builder1.getType() == builder.getType()).findFirst();
                if(potentialMatch.isPresent()){
                   potentialMatch.get().getStageReferred().addAll(builder.getStageReferred());
                }
                else {
                    descriptors.add(builder);
                }
            });
            var descriptorSets = descriptors.stream().map(Descriptor.Builder::build).collect(Collectors.groupingBy(descriptor -> descriptor.getLayout().getDescriptorSet()));
            state.setDescriptorSets(descriptorSets);
            var setLayouts = stack.mallocLong(descriptorSets.size());

            var pPipelineLayoutCreateInfo = VkPipelineLayoutCreateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_PIPELINE_LAYOUT_CREATE_INFO)
                    .pSetLayouts(setLayouts);

            for (var entry : descriptorSets.entrySet()) {
                var set = entry.getKey();
                var variables = entry.getValue();
                var setLayoutInfo = VkDescriptorSetLayoutCreateInfo.callocStack(stack).sType(VK_STRUCTURE_TYPE_DESCRIPTOR_SET_LAYOUT_CREATE_INFO);
                var bindings = VkDescriptorSetLayoutBinding.callocStack(variables.size(), stack);
                for (var variable : variables) {
                    var binding = bindings.get();
                    var layoutQualifier = variable.getLayout();
                    binding.binding(layoutQualifier.getBinding());
                    if(variable.getType() instanceof ShaderModuleInfo.StructVariableType) {
                        binding.descriptorType(VK_DESCRIPTOR_TYPE_UNIFORM_BUFFER);
                    }
                    else if (variable.getType() instanceof ShaderModuleInfo.OpaqueVariableType){
                        if("sampler".equals(variable.getType().getType())) {
                            binding.descriptorType(VK_DESCRIPTOR_TYPE_SAMPLER);
                        }
                    }
                }

                var layoutptr = stack.mallocLong(1);
                VK10.vkCreateDescriptorSetLayout(getNativeDevice(), setLayoutInfo, null, layoutptr);
                setLayouts.put(layoutptr.get(0));
            }

            var pPipelineLayout = stack.mallocLong(1);
            vkCreatePipelineLayout(getNativeDevice(), pPipelineLayoutCreateInfo, null, pPipelineLayout);
            long layout = pPipelineLayout.get(0);
            info.layout(layout);

            info.renderPass(pass.getHandle()).subpass(subpassIndex);

            LongBuffer ptrs = stack.mallocLong(1);
            var err = vkCreateGraphicsPipelines(vk, 0, infos, null, ptrs);
            if(err != VK_SUCCESS){
                return null;
            }
            return new Pipeline(this, ptrs.get(0), state.build());
        }
    }

    public boolean waitForFences(VulkanFence... fences) {
        return waitForFences(-1L, true, fences);
    }

    public boolean waitForFences(long timeout, boolean waitForAll, VulkanFence... fences){
        try(var stack = MemoryStack.stackPush()){
            var ptrs = stack.mallocLong(fences.length);
            ptrs.put(Arrays.stream(fences).mapToLong(VulkanFence::getHandle).toArray()).flip();
            var code = vkWaitForFences(getNativeDevice(), ptrs, waitForAll, timeout);
            if(code == VK_SUCCESS) return true;
            else if(code == VK_TIMEOUT) return false;
            else {
                throw new IllegalStateException("Error occurred when waiting for fences to signal: " + VulkanUtils.translateVulkanResult(code));
            }
        }
    }

    public DeviceMemory allocateMemory(long size, int typeBits){
        try(var stack = MemoryStack.stackPush()){
            var allocateInfo = VkMemoryAllocateInfo.callocStack(stack);
            var memoryType = getMemoryType(typeBits);
            if(memoryType == -1) return null;
            allocateInfo.sType(VK_STRUCTURE_TYPE_MEMORY_ALLOCATE_INFO).allocationSize(size).memoryTypeIndex(memoryType);
            var ptr = stack.mallocLong(1);
            var err = vkAllocateMemory(vk, allocateInfo, null, ptr);
            if(err != VK_SUCCESS){
                return null;
            }
            return new DeviceMemory(ptr.get(0), this);
        }
    }

    private int getMemoryType(int typeBits) {
        int bits = typeBits;
        for (int i = 0; i < 32; i++) {
            if ((bits & 1) == 1) {
                if ((physicalDevice.getMemoryProperties().getMemoryTypes().get(i).getProperty() & VK_MEMORY_PROPERTY_HOST_VISIBLE_BIT) != 0) {
                    return i;
                }
            }
            bits >>= 1;
        }
        return -1;
    }

    public void waitIdle(){
        vkDeviceWaitIdle(vk);
    }

    public void free(){
        VK10.vkDestroyDevice(vk, null);
        vk = null;
        released = true;
    }

    public VkDevice getNativeDevice() {
        return vk;
    }

    public PhysicalDevice getPhysicalDevice() {
        return physicalDevice;
    }
}
