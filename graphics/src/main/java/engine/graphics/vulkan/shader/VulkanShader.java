package engine.graphics.vulkan.shader;

import engine.graphics.shader.ShaderModuleInfo;
import engine.graphics.vulkan.device.LogicalDevice;
import engine.graphics.vulkan.util.VulkanUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkShaderModuleCreateInfo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

import static engine.graphics.vulkan.util.VulkanUtils.translateVulkanResult;
import static org.lwjgl.system.MemoryUtil.NULL;
import static org.lwjgl.vulkan.VK10.*;

public class VulkanShader {

    private long moduleHandle;

    private final LogicalDevice device;

    private VKShaderType stage;

    private ShaderModuleInfo descriptor;

    private VulkanShader(LogicalDevice device, long module, VKShaderType stage, ShaderModuleInfo descriptor) {
        this.device = device;
        this.moduleHandle = module;
        this.stage = stage;
        this.descriptor = descriptor;
    }

    public long getModuleHandle() {
        return moduleHandle;
    }

    public VKShaderType getStage() {
        return stage;
    }

    public ShaderModuleInfo getDescriptor() {
        return descriptor;
    }

    public static VulkanShader createShader(Path sourcePath, LogicalDevice device, VKShaderType stage) {
        byte[] src;
        try {
            src = Files.readAllBytes(sourcePath);
        } catch (IOException e) {
            return null;
        }
        var compiledCode = VulkanUtils.compileShaderCode(src, stage);
        return createShader(compiledCode, device, stage);
    }

    public static VulkanShader createShader(ByteBuffer compiledCode, LogicalDevice device, VKShaderType stage) {
        try (var stack = MemoryStack.stackPush()) {
            int err;
            var moduleCreateInfo = VkShaderModuleCreateInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_SHADER_MODULE_CREATE_INFO)
                    .pNext(NULL)
                    .pCode(compiledCode)
                    .flags(0);
            var pShaderModule = stack.mallocLong(1);
            err = vkCreateShaderModule(device.getNativeDevice(), moduleCreateInfo, null, pShaderModule);
            if (err != VK_SUCCESS) {
                throw new AssertionError("Failed to create shader module: " + translateVulkanResult(err));
            }
            long shaderModule = pShaderModule.get(0);
            return new VulkanShader(device, shaderModule, stage, new ShaderModuleInfo());
        }
    }

    public void free(){
        vkDestroyShaderModule(device.getNativeDevice(), moduleHandle, null);
    }
}
