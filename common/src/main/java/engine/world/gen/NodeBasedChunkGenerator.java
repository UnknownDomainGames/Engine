package engine.world.gen;

import engine.world.WorldCreationSetting;
import engine.world.chunk.Chunk;
import engine.world.chunk.ChunkStatus;
import engine.world.chunk.ChunkStatusListener;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class NodeBasedChunkGenerator implements ChunkGenerator {

    private final WorldCreationSetting setting;
    private final Map<ChunkStatus, ChunkGeneratorNode> nodeMap;
    private final List<ChunkGeneratorNode> sortedNodes;
    private ChunkStatusListener chunkStatusListener;

    public NodeBasedChunkGenerator(NodeBasedChunkGeneratorInfo info, WorldCreationSetting setting) {
        nodeMap = info.getNodeInfos().stream().collect(Collectors.toMap(ChunkGeneratorNodeInfo::getStatus, ChunkGeneratorNode::new));
        sortedNodes = getOrderedNodes(nodeMap.values());

        this.setting = setting;
    }

    public void setChunkStatusListener(ChunkStatusListener chunkStatusListener) {
        this.chunkStatusListener = chunkStatusListener;
    }

    private static List<ChunkGeneratorNode> getOrderedNodes(Collection<ChunkGeneratorNode> nodes) {
        var closedList = new ArrayList<ChunkGeneratorNode>(nodes.size());
        var openList = new LinkedList<>(nodes);
        while (!openList.isEmpty()) {
            var iterator = openList.listIterator();
            while (iterator.hasNext()) {
                var node = iterator.next();
                if (openList.stream().noneMatch(o1 -> node.getInfo().getDependencies().contains(o1.getInfo().getStatus()))) {
                    closedList.add(node);
                    iterator.remove();
                }
            }
        }
        return List.copyOf(closedList);
    }

    @Override
    public void generate(Chunk chunk) {
        GeneratorContext context = new GeneratorContext(chunk.getWorld(), setting);
        context.setTargetChunk(chunk);
        context.setStatusOrder(sortedNodes.stream().map(node -> node.getInfo().getStatus()).collect(Collectors.toList()));
        final CompletableFuture<Chunk>[] cf = new CompletableFuture[1];
        sortedNodes.forEach(node -> {
            var future = node.processAsync(chunk, context);
            if (cf[0] == null) {
                cf[0] = future;
            } else {
                cf[0] = cf[0].thenCombine(future, (chunk1, chunk2) -> chunk2);
            }
        });
    }

    @Override
    public CompletableFuture<Chunk> generateAsync(Chunk chunk) {
        GeneratorContext context = new GeneratorContext(chunk.getWorld(), setting);
        context.setTargetChunk(chunk);
        context.setStatusOrder(sortedNodes.stream().map(node -> node.getInfo().getStatus()).collect(Collectors.toList()));
        final CompletableFuture<Chunk>[] cf = new CompletableFuture[1];
        sortedNodes.forEach(node -> {
            var future = node.processAsync(chunk, context).thenApply(chunk1 -> {
                if (chunkStatusListener != null) {
                    chunkStatusListener.onChunkStatusUpdate(chunk1.getPos(), node.getInfo().getStatus());
                }
                return chunk1;
            });
            if (cf[0] == null) {
                cf[0] = future;
            } else {
                cf[0] = cf[0].thenCombine(future, (chunk1, chunk2) -> chunk2);
            }
        });
        return cf[0];
    }
}
