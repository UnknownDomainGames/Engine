package engine.graphics.vulkan.shader;

import engine.graphics.vulkan.device.LogicalDevice;
import engine.graphics.vulkan.util.VulkanUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.VkPipelineShaderStageCreateInfo;
import org.lwjgl.vulkan.VkShaderModuleCreateInfo;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

import static engine.graphics.vulkan.util.VulkanUtils.translateVulkanResult;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.vulkan.VK10.*;

public class VulkanShader {

    private long moduleHandle;

    private ShaderType stage;

    private String entryPoint;

    private VulkanShader(String entryPoint, long module, ShaderType stage){
        this.entryPoint = entryPoint;
        this.moduleHandle = module;
        this.stage = stage;
    }

    public ShaderType getStage() {
        return stage;
    }

    public String getEntryPoint() {
        return entryPoint;
    }

    public static VulkanShader createShader(Path sourcePath, LogicalDevice device, ShaderType stage){
        byte[] src;
        try {
            src = Files.readAllBytes(sourcePath);
        } catch (IOException e) {
            return null;
        }
        var compiledCode = VulkanUtils.compileShaderCode(src, stage);
        return createShader(compiledCode, device, stage);
    }

    public static VulkanShader createShader(ByteBuffer compiledCode, LogicalDevice device, ShaderType stage){
        try(var stack = MemoryStack.stackPush()) {
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
            return new VulkanShader("main", shaderModule, stage);
        }
    }

    public void free(){
//        vkDestroyShaderModule();
    }
}
