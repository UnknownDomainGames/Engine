package engine.world.gen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NodeBasedChunkGeneratorInfo {
    private List<ChunkGeneratorNodeInfo> nodeInfos = new ArrayList<>();

    public List<ChunkGeneratorNodeInfo> getNodeInfos() {
        return nodeInfos;
    }

    public NodeBasedChunkGeneratorInfo addNodes(ChunkGeneratorNodeInfo... infos) {
        this.nodeInfos.addAll(Arrays.asList(infos));
        return this;
    }
}
