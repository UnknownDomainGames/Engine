package engine.graphics.vulkan.pipeline;

import java.util.ArrayList;
import java.util.List;

public class PipelineLayout {
    private long pointer;
    private List<DescriptorSet> sets = new ArrayList<>();

    public PipelineLayout(long layoutPtr, List<DescriptorSet> descriptorSets) {
        pointer = layoutPtr;
        this.sets = descriptorSets;
    }

    public List<DescriptorSet> getDescriptorSets() {
        return sets;
    }
}
