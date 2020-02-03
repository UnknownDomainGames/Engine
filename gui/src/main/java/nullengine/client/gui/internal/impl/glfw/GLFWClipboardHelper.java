package nullengine.client.gui.internal.impl.glfw;

import nullengine.client.gui.internal.ClipboardHelper;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;

public class GLFWClipboardHelper implements ClipboardHelper {
    @Override
    public String getString() {
        return GLFW.glfwGetClipboardString(MemoryUtil.NULL);
    }

    @Override
    public void setString(String value) {
        GLFW.glfwSetClipboardString(MemoryUtil.NULL, value);
    }
}
