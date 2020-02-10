package engine.graphics.vulkan.render;

import java.util.List;

public class Subpass {
    private List<Attachment.AttachmentReference> inputAttachments;
    private List<Attachment.AttachmentReference> colorAttachments;
    private List<Attachment.AttachmentReference> resolvedAttachments;
    private Attachment.AttachmentReference depthStencilAttachment;
    private List<Attachment.AttachmentReference> preservedAttachments;

    public Subpass(List<Attachment.AttachmentReference> colorAttachments, Attachment.AttachmentReference depthStencilAttachment, List<Attachment.AttachmentReference> inputAttachments){
        this.colorAttachments = colorAttachments;
        this.depthStencilAttachment = depthStencilAttachment;
        this.inputAttachments = inputAttachments;
    }

    public List<Attachment.AttachmentReference> getColorAttachments() {
        return colorAttachments;
    }

    public Attachment.AttachmentReference getDepthStencilAttachment() {
        return depthStencilAttachment;
    }

    public List<Attachment.AttachmentReference> getInputAttachments() {
        return inputAttachments;
    }

    public List<Attachment.AttachmentReference> getPreservedAttachments() {
        return preservedAttachments;
    }

    public List<Attachment.AttachmentReference> getResolvedAttachments() {
        return resolvedAttachments;
    }
}
