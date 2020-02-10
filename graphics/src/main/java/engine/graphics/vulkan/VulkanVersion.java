package engine.graphics.vulkan;

import org.lwjgl.vulkan.VK10;
import org.lwjgl.vulkan.VK11;

public enum VulkanVersion {
    VER_1_0(VK10.VK_API_VERSION_1_0),
    VER_1_1(VK11.VK_API_VERSION_1_1);

    int vk;

    VulkanVersion(int vk){
        this.vk = vk;
    }
}
