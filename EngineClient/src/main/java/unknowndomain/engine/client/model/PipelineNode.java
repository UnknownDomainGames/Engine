package unknowndomain.engine.client.model;

import java.util.function.BiConsumer;

public interface PipelineNode<I> {
    void process(I in, BiConsumer<String, Object> emittor) throws Exception;
}