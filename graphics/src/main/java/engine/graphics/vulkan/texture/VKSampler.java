package engine.graphics.vulkan.texture;

import engine.graphics.texture.DepthCompareMode;
import engine.graphics.texture.FilterMode;
import engine.graphics.texture.Sampler;
import engine.graphics.texture.WrapMode;
import engine.graphics.vulkan.device.LogicalDevice;
import engine.graphics.vulkan.pipeline.PipelineState;
import engine.util.Color;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VkSamplerCreateInfo;

import static org.lwjgl.vulkan.VK10.VK_SUCCESS;

public class VKSampler implements Sampler {

    private final LogicalDevice device;
    private long handle;
    private boolean isReleased = false;

    private VKSampler(LogicalDevice device, long handle) {
        this.device = device;
        this.handle = handle;
    }

    public long getHandle() {
        return handle;
    }

    private static int toVkFilter(FilterMode mode) {
        switch (mode) {
            case LINEAR:
            case LINEAR_MIPMAP_NEAREST:
            case LINEAR_MIPMAP_LINEAR:
                return VK10.VK_FILTER_LINEAR;
            case NEAREST:
            case NEAREST_MIPMAP_NEAREST:
            case NEAREST_MIPMAP_LINEAR:
            default:
                return VK10.VK_FILTER_NEAREST;
        }
    }

    private static int toVkMipmapMode(FilterMode mode) {
        switch (mode) {
            case LINEAR_MIPMAP_LINEAR:
            case NEAREST_MIPMAP_LINEAR:
                return VK10.VK_SAMPLER_MIPMAP_MODE_LINEAR;
            case LINEAR:
            case NEAREST:
            default:
            case LINEAR_MIPMAP_NEAREST:
            case NEAREST_MIPMAP_NEAREST:
                return VK10.VK_SAMPLER_MIPMAP_MODE_NEAREST;
        }
    }

    private static int toVkAddressMode(WrapMode mode) {
        switch (mode){
            case CLAMP:
            case CLAMP_TO_EDGE:
                return (VK10.VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_EDGE);
            case CLAMP_TO_BORDER:
                return (VK10.VK_SAMPLER_ADDRESS_MODE_CLAMP_TO_BORDER);
            case MIRRORED_REPEAT:
                return (VK10.VK_SAMPLER_ADDRESS_MODE_MIRRORED_REPEAT);
            case REPEAT:
            default:
                return (VK10.VK_SAMPLER_ADDRESS_MODE_REPEAT);
        }
    }

    private static PipelineState.CompareOp fromDepthCompareMode(DepthCompareMode mode) {
        switch (mode) {
            case LESS_OR_EQUAL:
                return PipelineState.CompareOp.LESS_OR_EQUAL;
            case GREATER_OR_EQUAL:
                return PipelineState.CompareOp.GREATER_OR_EQUAL;
            case LESS:
                return PipelineState.CompareOp.LESS;
            case GREATER:
                return PipelineState.CompareOp.GREATER;
            case EQUAL:
                return PipelineState.CompareOp.EQUAL;
            case NOT_EQUAL:
                return PipelineState.CompareOp.NOT_EQUAL;
            case NEVER:
                return PipelineState.CompareOp.NEVER;
            case NONE:
            case ALWAYS:
            default:
                return PipelineState.CompareOp.ALWAYS;
        }
    }

    public static VKSampler createSampler(LogicalDevice device,
                                          FilterMode mag, FilterMode min,
                                          WrapMode wrapModeX, WrapMode wrapModeY, WrapMode wrapModeZ,
                                          Color borderColor,
                                          DepthCompareMode compareMode,
                                          float mipLodBias,
                                          float minLod, float maxLod) {
        try(var stack = MemoryStack.stackPush()){
            var info = VkSamplerCreateInfo.callocStack(stack).sType(VK10.VK_STRUCTURE_TYPE_SAMPLER_CREATE_INFO);
            info.magFilter(toVkFilter(mag))
                    .minFilter(toVkFilter(min))
                    .addressModeU(toVkAddressMode(wrapModeX))
                    .addressModeV(toVkAddressMode(wrapModeY))
                    .addressModeW(toVkAddressMode(wrapModeZ))
                    .borderColor(borderColor.toRGBA())
                    .anisotropyEnable(true).maxAnisotropy(16.0f)
                    .unnormalizedCoordinates(false)
                    .mipmapMode(toVkMipmapMode(min))
                    .mipLodBias(mipLodBias)
                    .minLod(minLod).maxLod(maxLod).compareEnable(compareMode != DepthCompareMode.NONE)
                    .compareOp(fromDepthCompareMode(compareMode).getVk());
            var ptr = stack.mallocLong(1);
            var err = VK10.vkCreateSampler(device.getNativeDevice(), info, null, ptr);
            if(err != VK_SUCCESS){
                return null;
            }
            return new VKSampler(device, ptr.get(0));
        }
    }

    @Override
    public int getId() {
        return Math.toIntExact(handle);
    }

    @Override
    public Sampler setMagFilter(FilterMode filterMode) {
        return this;
    }

    @Override
    public Sampler setMinFilter(FilterMode filterMode) {
        return this;
    }

    @Override
    public Sampler setWrapMode(WrapMode wrapMode) {
        return this;
    }

    @Override
    public Sampler setMinLod(float min) {
        return this;
    }

    @Override
    public Sampler setMaxLod(float max) {
        return this;
    }

    @Override
    public Sampler setBorderColor(Color color) {
        return this;
    }

    @Override
    public Sampler setDepthCompareMode(DepthCompareMode depthCompareMode) {
        return this;
    }

    @Override
    public void dispose() {
        if(isReleased) return;
        VK10.vkDestroySampler(device.getNativeDevice(), handle, null);
        handle = 0;
        isReleased = true;
    }

    @Override
    public boolean isDisposed() {
        return isReleased;
    }
}
