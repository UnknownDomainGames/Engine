package engine.graphics.vulkan.graph;

import engine.graphics.graph.*;
import engine.graphics.texture.FrameBuffer;
import engine.graphics.vulkan.CommandBuffer;

import java.util.List;
import java.util.stream.Collectors;

public class VKRenderGraphPass implements RenderPass {
    private final RenderPassInfo info;
    private final VKRenderTask task;

    //    private final VKFrameBuffer frameBuffer;
    private final List<VKDrawer> drawers;

    public VKRenderGraphPass(RenderPassInfo renderPassInfo, VKRenderTask vkRenderTask) {
        this.info = renderPassInfo;
        this.task = vkRenderTask;
//        this.frameBuffer = isBackBuffer(info) ? GLFrameBuffer.getBackBuffer() : createFrameBuffer(info, task);
        this.drawers = info.getDrawers().stream()
                .map(drawerInfo -> new VKDrawer(drawerInfo, this))
                .collect(Collectors.toUnmodifiableList());
    }

    private boolean isBackBuffer(RenderPassInfo info) {
        List<ColorOutputInfo> colorOutputs = info.getColorOutputs();
        if (colorOutputs.size() != 1) return false;
        if (colorOutputs.get(0).getColorBuffer() != null) return false;

        DepthOutputInfo depthOutput = info.getDepthOutput();
        if (depthOutput != null && depthOutput.getDepthBuffer() != null) return false;
        return true;
    }

//    private GLFrameBuffer createFrameBuffer(RenderPassInfo info, GLRenderTask task) {
//        var colorOutputs = info.getColorOutputs();
//        this.colorOutputs = new GLRenderPass.ColorOutput[colorOutputs.size()];
//        for (int i = 0; i < colorOutputs.size(); i++) {
//            var colorOutputInfo = colorOutputs.get(i);
//            var renderBuffer = task.getRenderBufferProxy(colorOutputInfo.getColorBuffer());
//            this.colorOutputs[i] = new GLRenderPass.ColorOutput(colorOutputInfo, renderBuffer);
//            enableBlend |= colorOutputInfo.getBlendMode() != BlendMode.DISABLED;
//        }
//
//        var depthOutputInfo = info.getDepthOutput();
//        if (depthOutputInfo != null) {
//            var renderBuffer = task.getRenderBufferProxy(depthOutputInfo.getDepthBuffer());
//            this.depthOutput = new GLRenderPass.DepthOutput(depthOutputInfo, renderBuffer);
//        }
//
//        return new GLFrameBuffer();
//    }

    @Override
    public RenderPassInfo getInfo() {
        return info;
    }

    public String getName() {
        return info.getName();
    }

    public List<String> getDependencies() {
        return info.getDependencies();
    }

    @Override
    public RenderTask getRenderTask() {
        return task;
    }

    @Override
    public FrameBuffer getFrameBuffer() {
//        return frameBuffer;
        return null;
    }


    public void draw(Frame frame, CommandBuffer cmdBuffer) {
//        setupFrameBuffer(frameContext.getFrame().isResized());
//        setupViewport();
//        setupCullMode(info.getCullMode());
//        setupDepthTest(info.getDepthOutput());
//        setupBlend();
        drawers.forEach(drawer -> drawer.draw(frame, cmdBuffer));
    }

    public void dispose() {
//        frameBuffer.dispose();
        drawers.forEach(VKDrawer::dispose);
    }
}
