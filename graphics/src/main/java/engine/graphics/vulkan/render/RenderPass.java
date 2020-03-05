package engine.graphics.vulkan.render;

import engine.graphics.vulkan.device.LogicalDevice;
import engine.graphics.vulkan.texture.VKTexture;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.vulkan.*;

import java.nio.LongBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.vulkan.VK10.*;

public class RenderPass {
    private final LogicalDevice device;

    private long handle;
    private boolean released = false;
    private List<Attachment> attachments;
    private Map<Attachment, List<VKTexture.Usage>> attachmentUsages;
    private List<Subpass> subpasses;

    private RenderPass(LogicalDevice device, long handle){
        this.device = device;
        this.handle = handle;
    }

    public void dispose(){
        if(released) throw new IllegalStateException("RenderPass already released!");
        vkDestroyRenderPass(device.getNativeDevice(), handle, null);
        handle = 0;
    }

    public long getHandle() {
        return handle;
    }

    public List<Subpass> getSubpasses() {
        return subpasses;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public Map<Attachment, List<VKTexture.Usage>> getAttachmentUsages() {
        return attachmentUsages;
    }

    public static RenderPass createRenderPass(LogicalDevice device, List<Subpass> subpasses){
        try(var stack = MemoryStack.stackPush()){
            VkRenderPassCreateInfo info = VkRenderPassCreateInfo.callocStack(stack).sType(VK10.VK_STRUCTURE_TYPE_RENDER_PASS_CREATE_INFO);
            var attachments = new ArrayList<Attachment>();
            var attachmentUsages = new HashMap<Attachment, List<VKTexture.Usage>>();
            var nativeSubpasses = VkSubpassDescription.callocStack(subpasses.size(), stack);
            for (Subpass subpass : subpasses) {
                VkSubpassDescription description = nativeSubpasses.get();
                description.pipelineBindPoint(VK_PIPELINE_BIND_POINT_GRAPHICS);
                if(subpass.getColorAttachments() != null && !subpass.getColorAttachments().isEmpty()){
                    var references = VkAttachmentReference.callocStack(subpass.getColorAttachments().size(), stack);
                    for (Attachment.AttachmentReference reference : subpass.getColorAttachments()) {
                        attachmentUsages.computeIfAbsent(reference.getAttachment(), k -> new ArrayList<>()).add(VKTexture.Usage.COLOR_ATTACHMENT);
                        buildNativeAttachmentReference(attachments, references.get(), reference);
                    }
                    references.flip();
                    description.pColorAttachments(references).colorAttachmentCount(references.remaining());
                }
                if(subpass.getDepthStencilAttachment() != null){
                    var reference = VkAttachmentReference.callocStack(stack);
                    attachmentUsages.computeIfAbsent(subpass.getDepthStencilAttachment().getAttachment(), k -> new ArrayList<>()).add(VKTexture.Usage.DEPTH_STENCIL_ATTACHMENT);
                    buildNativeAttachmentReference(attachments, reference, subpass.getDepthStencilAttachment());
                    description.pDepthStencilAttachment(reference);
                }
                if(subpass.getInputAttachments() != null && !subpass.getInputAttachments().isEmpty()){
                    var references = VkAttachmentReference.callocStack(subpass.getInputAttachments().size(), stack);
                    for (Attachment.AttachmentReference reference : subpass.getInputAttachments()) {
                        attachmentUsages.computeIfAbsent(reference.getAttachment(), k -> new ArrayList<>()).add(VKTexture.Usage.INPUT_ATTACHMENT);
                        buildNativeAttachmentReference(attachments, references.get(), reference);
                    }
                    references.flip();
                    description.pInputAttachments(references);
                }
                if(subpass.getResolvedAttachments() != null && !subpass.getResolvedAttachments().isEmpty()){
                    var references = VkAttachmentReference.callocStack(subpass.getResolvedAttachments().size(), stack);
                    for (Attachment.AttachmentReference reference : subpass.getResolvedAttachments()) {
                        buildNativeAttachmentReference(attachments, references.get(), reference);
                    }
                    references.flip();
                    description.pResolveAttachments(references);
                }
                if(subpass.getPreservedAttachments() != null && !subpass.getPreservedAttachments().isEmpty()){
                    var attachmentIndices = new ArrayList<Integer>();
                    for (Attachment.AttachmentReference reference : subpass.getPreservedAttachments()) {
                        var i = attachments.indexOf(reference.getAttachment());
                        if(i != -1){
                           attachmentIndices.add(i);
                        }
                    }
                    var buf = stack.mallocInt(attachmentIndices.size());
                    buf.put(attachmentIndices.stream().mapToInt(i->i).toArray()).flip();
                    description.pPreserveAttachments(buf);
                }
            }
            nativeSubpasses.flip();
            info.pSubpasses(nativeSubpasses);
            var nativeAttachments = VkAttachmentDescription.callocStack(attachments.size(), stack);
            for (Attachment attachment : attachments) {
                var nativeAttahcment = nativeAttachments.get();
                nativeAttahcment.format(attachment.getFormat().getVk())
                        .samples(attachment.getSamples())
                        .initialLayout(attachment.getInitialLayout().getVk())
                        .finalLayout(attachment.getFinalLayout().getVk())
                        .loadOp(attachment.getLoadOp().getVk())
                        .storeOp(attachment.getStoreOp().getVk())
                        .stencilLoadOp(attachment.getStencilLoadOp().getVk())
                        .stencilStoreOp(attachment.getStencilStoreOp().getVk());
            }
            nativeAttachments.flip();
            info.pAttachments(nativeAttachments);
            var ptrs = stack.mallocLong(1);
            var err = VK10.vkCreateRenderPass(device.getNativeDevice(), info, null, ptrs);
            if(err != VK_SUCCESS){
                return null;
            }
            var renderPass = new RenderPass(device, ptrs.get(0));
            renderPass.subpasses = subpasses;
            renderPass.attachments = attachments;
            renderPass.attachmentUsages = attachmentUsages;
            return renderPass;
        }
    }

    private static void buildNativeAttachmentReference(ArrayList<Attachment> attachments, VkAttachmentReference reference, Attachment.AttachmentReference warpedReference) {
        var attachment = warpedReference.getAttachment();
        if (!attachments.contains(attachment)) {
            attachments.add(attachment);
        }
        reference.attachment(attachments.indexOf(attachment)).layout(warpedReference.getExpectedLayout().getVk());
    }
}
