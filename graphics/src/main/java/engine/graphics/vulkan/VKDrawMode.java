package engine.graphics.vulkan;

import engine.graphics.util.DrawMode;
import org.lwjgl.vulkan.VK10;

public enum VKDrawMode {
    POINTS(DrawMode.POINTS, VK10.VK_PRIMITIVE_TOPOLOGY_POINT_LIST),
    LINES(DrawMode.LINES, VK10.VK_PRIMITIVE_TOPOLOGY_LINE_LIST),
    LINE_STRIP(DrawMode.LINE_STRIP, VK10.VK_PRIMITIVE_TOPOLOGY_LINE_STRIP),
    TRIANGLES(DrawMode.TRIANGLES, VK10.VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST),
    TRIANGLE_STRIP(DrawMode.TRIANGLE_STRIP, VK10.VK_PRIMITIVE_TOPOLOGY_TRIANGLE_STRIP),
    TRIANGLE_FAN(DrawMode.TRIANGLE_FAN, VK10.VK_PRIMITIVE_TOPOLOGY_TRIANGLE_FAN),
    LINES_ADJACENCY(DrawMode.LINES_ADJACENCY, VK10.VK_PRIMITIVE_TOPOLOGY_LINE_LIST_WITH_ADJACENCY),
    LINE_STRIP_ADJACENCY(DrawMode.LINE_STRIP_ADJACENCY, VK10.VK_PRIMITIVE_TOPOLOGY_LINE_STRIP_WITH_ADJACENCY),
    TRIANGLES_ADJACENCY(DrawMode.TRIANGLES_ADJACENCY, VK10.VK_PRIMITIVE_TOPOLOGY_TRIANGLE_LIST_WITH_ADJACENCY),
    TRIANGLE_STRIP_ADJACENCY(DrawMode.TRIANGLE_STRIP_ADJACENCY, VK10.VK_PRIMITIVE_TOPOLOGY_TRIANGLE_STRIP_WITH_ADJACENCY),
    PATCHES(DrawMode.PATCHES, VK10.VK_PRIMITIVE_TOPOLOGY_PATCH_LIST);

    public final DrawMode peer;
    public final int vk;

    public static VKDrawMode valueOf(DrawMode drawMode) {
        for (VKDrawMode value : values()) {
            if(value.peer == drawMode) return value;
        }
        return null;
    }

    VKDrawMode(DrawMode peer, int vk) {
        this.peer = peer;
        this.vk = vk;
    }

    public int getVk() {
        return vk;
    }
}
