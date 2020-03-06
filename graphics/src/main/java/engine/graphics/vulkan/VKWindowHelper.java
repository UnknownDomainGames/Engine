package engine.graphics.vulkan;

import engine.graphics.display.BaseWindowHelper;
import engine.graphics.display.Window;
import engine.graphics.glfw.GLFWVulkanWindow;

public class VKWindowHelper extends BaseWindowHelper {
    @Override
    public Window createWindow() {
        return new GLFWVulkanWindow();
    }

    @Override
    public Window createWindow(Window parent) {
        return new GLFWVulkanWindow(parent);
    }
}
