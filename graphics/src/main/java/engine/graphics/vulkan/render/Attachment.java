package engine.graphics.vulkan.render;

import engine.graphics.vulkan.texture.VKColorFormat;
import engine.graphics.vulkan.texture.VKTexture;
import org.lwjgl.vulkan.VK10;

public class Attachment {

    private VKColorFormat format;
    private int samples;
    private VKTexture.Layout initialLayout;
    private VKTexture.Layout finalLayout;

    public Attachment(VKColorFormat format, int samples, VKTexture.Layout initialLayout, VKTexture.Layout finalLayout) {
        this.format = format;
        this.samples = samples;
        this.initialLayout = initialLayout;
        this.finalLayout = finalLayout;
    }

    private Operation loadOp = Operation.IGNORE, storeOp = Operation.DISCARD, stencilLoadOp = Operation.IGNORE, stencilStoreOp = Operation.DISCARD;

    public VKColorFormat getFormat() {
        return format;
    }

    public int getSamples() {
        return samples;
    }

    public VKTexture.Layout getInitialLayout() {
        return initialLayout;
    }

    public VKTexture.Layout getFinalLayout() {
        return finalLayout;
    }

    public Attachment setOp(Operation loadOp, Operation storeOp){
        this.loadOp = loadOp;
        this.storeOp = storeOp;
        return this;
    }

    public Attachment setStencilOp(Operation loadOp, Operation storeOp){
        this.stencilLoadOp = loadOp;
        this.stencilStoreOp = storeOp;
        return this;
    }

    public Operation getLoadOp() {
        return loadOp;
    }

    public Operation getStoreOp() {
        return storeOp;
    }

    public Operation getStencilLoadOp() {
        return stencilLoadOp;
    }

    public Operation getStencilStoreOp() {
        return stencilStoreOp;
    }

    public enum Operation{
        LOAD(VK10.VK_ATTACHMENT_LOAD_OP_LOAD, false),
        CLEAR(VK10.VK_ATTACHMENT_LOAD_OP_CLEAR, false),
        STORE(VK10.VK_ATTACHMENT_STORE_OP_STORE, true),
        IGNORE(VK10.VK_ATTACHMENT_LOAD_OP_DONT_CARE, false),
        DISCARD(VK10.VK_ATTACHMENT_STORE_OP_DONT_CARE, true);

        private final int vk;
        private final boolean isStoreOp;

        Operation(int vk, boolean isStoreOp){
            this.vk = vk;
            this.isStoreOp = isStoreOp;
        }

        public int getVk() {
            return vk;
        }

        public boolean isStoreOp() {
            return isStoreOp;
        }
    }

    public static class AttachmentReference {
        private Attachment attachment;
        private VKTexture.Layout expectedLayout;

        public AttachmentReference(Attachment attachment, VKTexture.Layout expectedLayout) {
            this.attachment = attachment;
            this.expectedLayout = expectedLayout;
        }

        public Attachment getAttachment() {
            return attachment;
        }

        public VKTexture.Layout getExpectedLayout() {
            return expectedLayout;
        }
    }
}
