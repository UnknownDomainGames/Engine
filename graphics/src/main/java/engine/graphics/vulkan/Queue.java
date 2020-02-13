package engine.graphics.vulkan;

import engine.graphics.vulkan.pipeline.PipelineStage;
import engine.graphics.vulkan.synchronize.Semaphore;
import engine.graphics.vulkan.synchronize.VulkanFence;
import engine.graphics.vulkan.util.VulkanUtils;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.KHRSwapchain;
import org.lwjgl.vulkan.VkPresentInfoKHR;
import org.lwjgl.vulkan.VkQueue;
import org.lwjgl.vulkan.VkSubmitInfo;

import javax.annotation.Nullable;
import java.util.List;

import static org.lwjgl.vulkan.KHRSwapchain.VK_STRUCTURE_TYPE_PRESENT_INFO_KHR;
import static org.lwjgl.vulkan.VK10.*;
import static org.lwjgl.vulkan.VK10.VK_SUCCESS;

public class Queue {
    private VkQueue vk;

    public Queue(VkQueue vkQueue){
        this.vk = vkQueue;
    }

    public VkQueue getNativeQueue() {
        return vk;
    }

    public void submit(CommandBuffer buffer, @Nullable List<Semaphore> waitingSignal, @Nullable List<PipelineStage> waitingStage, @Nullable List<Semaphore> signal, @Nullable VulkanFence signalingFence) {
        submit(List.of(buffer), waitingSignal, waitingStage, signal, signalingFence);
    }

    public void submit(List<CommandBuffer> buffers, @Nullable List<Semaphore> waitingSignal, @Nullable List<PipelineStage> waitingStage, @Nullable List<Semaphore> signal, @Nullable VulkanFence signalingFence){
        buffers.forEach(CommandBuffer::checkReleased);
        try(var stack = MemoryStack.stackPush()) {
            VkSubmitInfo submitInfo = VkSubmitInfo.callocStack(stack)
                    .sType(VK_STRUCTURE_TYPE_SUBMIT_INFO);
            var pCommandBuffers = stack.mallocPointer(buffers.size());
            for (CommandBuffer buffer : buffers) {
                pCommandBuffers.put(buffer.getNativeCommandBuffer());
            }
            pCommandBuffers.flip();
            if(waitingSignal != null && waitingStage != null && waitingSignal.size() == waitingStage.size()) {
                var waiting = stack.mallocLong(waitingSignal.size());
                for (Semaphore semaphore : waitingSignal) {
                    waiting.put(semaphore.getHandle());
                }
                waiting.flip();
                submitInfo.pSignalSemaphores(waiting);
                var stage = stack.ints(waitingStage.stream().mapToInt(PipelineStage::getVk).toArray());
                submitInfo.pWaitDstStageMask(stage);
            }
            if(signal != null) {
                var establish = stack.mallocLong(signal.size());
                for (Semaphore semaphore : signal) {
                    establish.put(semaphore.getHandle());
                }
                establish.flip();
                submitInfo.pWaitSemaphores(establish);
            }
            submitInfo.pCommandBuffers(pCommandBuffers);
            int err = vkQueueSubmit(vk, submitInfo, signalingFence != null ? signalingFence.getHandle() : VK_NULL_HANDLE);
            if (err != VK_SUCCESS) {
                throw new AssertionError("Failed to submit command buffer: " + VulkanUtils.translateVulkanResult(err));
            }
        }
    }

    public void waitIdle(){
        vkQueueWaitIdle(vk);
    }

    public void present(Swapchain swapchain, int imageIndex, @Nullable List<Semaphore> waitSignal){
        present(List.of(swapchain), List.of(imageIndex), waitSignal);
    }

    public void present(List<Swapchain> swapchains, List<Integer> imageIndex, @Nullable List<Semaphore> waitSignal){
        assert swapchains.size() == imageIndex.size();
        try(var stack = MemoryStack.stackPush()){
            var info = VkPresentInfoKHR.callocStack(stack).sType(VK_STRUCTURE_TYPE_PRESENT_INFO_KHR);
            var handles = stack.mallocLong(swapchains.size());
            var indicesbuf = stack.mallocInt(imageIndex.size());
            for (int i = 0; i < swapchains.size(); i++) {
                handles.put(swapchains.get(i).getHandle());
                indicesbuf.put(imageIndex.get(i));
            }
            info.pSwapchains(handles);
            info.pImageIndices(indicesbuf);
            if(waitSignal != null){
                var sema = stack.mallocLong(waitSignal.size());
                sema.put(waitSignal.stream().mapToLong(Semaphore::getHandle).toArray()).flip();
                info.pWaitSemaphores(sema);
            }
            KHRSwapchain.vkQueuePresentKHR(getNativeQueue(), info);
        }
    }
}
