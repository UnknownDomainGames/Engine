package nullengine.client.gui.input;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryUtil;

public class Clipboard {
    public static String getString() {
        return GLFW.glfwGetClipboardString(MemoryUtil.NULL);
    }

    public static void setString(String s) {
        GLFW.glfwSetClipboardString(MemoryUtil.NULL, s);
    }
}
