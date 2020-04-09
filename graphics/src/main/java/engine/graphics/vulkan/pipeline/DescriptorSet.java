package engine.graphics.vulkan.pipeline;

import java.util.List;

public class DescriptorSet {
    private List<Descriptor> descriptorList;
    private long layoutPtr;

    public DescriptorSet(long layoutPtr, List<Descriptor> descriptors) {
        this.layoutPtr = layoutPtr;
        this.descriptorList = descriptors;
    }
}
